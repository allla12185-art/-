package com.example.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AiMentorRepository
import com.example.data.AudioPlaybackState
import com.example.data.AudioPlayerManager
import com.example.data.CurriculumRepository
import com.example.data.DailyWordRecord
import com.example.data.DailyWordsCatalog
import com.example.data.FirestoreDailyWordRepository
import com.example.data.FirestoreProgressRepository
import com.example.data.FirestoreQuizRepository
import com.example.data.LearnerProgressSnapshot
import com.example.data.LessonProgressRecord
import com.example.data.PersonalizedAdvice
import com.example.data.ProgressDataStore
import com.example.data.QuizQuestionRecord
import com.example.data.QuizResultRecord
import com.example.data.UserProgress
import com.example.model.AudioLessonTrack
import com.example.model.CefrLevel
import com.example.model.Lesson
import com.example.model.MentorMessage
import com.example.model.MindMapNode
import com.example.model.MinimalPair
import com.example.model.PhoneticCategory
import com.example.model.PhoneticSound
import com.example.model.QuizQuestion
import com.example.model.StudyMode
import com.example.model.StudyRule
import com.example.notifications.DailyReminderScheduler
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

private const val TAG = "MainViewModel"

sealed interface FirestoreProgressState {
    data object Loading : FirestoreProgressState
    data class Synced(
        val records: List<LessonProgressRecord>,
        val completedLessonIds: Set<String>
    ) : FirestoreProgressState
    data class Error(val message: String) : FirestoreProgressState
}

sealed interface DailyWordUiState {
    data object Loading : DailyWordUiState
    data class Ready(
        val word: DailyWordRecord,
        val masteredCount: Int
    ) : DailyWordUiState
    data class Error(val message: String) : DailyWordUiState
}

sealed interface AiUiState {
    data object Idle : AiUiState
    data object Loading : AiUiState
    data class Streaming(val partialReply: String) : AiUiState
    data object Success : AiUiState
    data class Error(val message: String) : AiUiState
}

sealed interface AdviceUiState {
    data object Idle : AdviceUiState
    data object Loading : AdviceUiState
    data class Success(val advice: PersonalizedAdvice) : AdviceUiState
    data class Error(val message: String) : AdviceUiState
}

sealed interface LessonQuizQuestionsState {
    data object Loading : LessonQuizQuestionsState
    data class Ready(
        val lessonId: String,
        val questions: List<QuizQuestionRecord>
    ) : LessonQuizQuestionsState
    data class Error(val message: String) : LessonQuizQuestionsState
}

sealed interface QuizSubmissionState {
    data object Idle : QuizSubmissionState
    data object Submitting : QuizSubmissionState
    data class Evaluated(val result: QuizResultRecord) : QuizSubmissionState
    data class Error(val message: String) : QuizSubmissionState
}

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModel(
    private val currentUserId: String,
    private val firestoreProgressRepo: FirestoreProgressRepository,
    private val dailyWordRepo: FirestoreDailyWordRepository,
    private val quizRepo: FirestoreQuizRepository,
    private val progressStore: ProgressDataStore,
    private val audioManager: AudioPlayerManager,
    private val reminderScheduler: DailyReminderScheduler,
    private val curriculumRepo: CurriculumRepository = CurriculumRepository(),
    private val aiMentorRepo: AiMentorRepository = AiMentorRepository()
) : ViewModel() {

    private val syncRetryTrigger = MutableStateFlow(0)
    private val dailyWordRetryTrigger = MutableStateFlow(0)
    private val quizResultsRetryTrigger = MutableStateFlow(0)
    private val _activeQuizLessonId = MutableStateFlow<String?>(null)
    private val _quizQuestionsRetryTrigger = MutableStateFlow(0)

    init {
        viewModelScope.launch {
            dailyWordRepo.ensureTodayWordExists(currentUserId)
        }
    }

    val firestoreProgressState: StateFlow<FirestoreProgressState> = syncRetryTrigger
        .flatMapLatest {
            firestoreProgressRepo.observeLessonProgress(currentUserId)
                .map<List<LessonProgressRecord>, FirestoreProgressState> { records ->
                    FirestoreProgressState.Synced(
                        records = records,
                        completedLessonIds = records.map { it.lessonId }.toSet()
                    )
                }
                .catch { error ->
                    val firestoreError = generateSequence(error) { it.cause }
                        .filterIsInstance<FirebaseFirestoreException>()
                        .firstOrNull()
                    if (firestoreError == null && error is CancellationException) throw error
                    val rootError = firestoreError ?: error
                    Log.w(TAG, "Error observing Firestore lessonProgress", rootError)
                    emit(
                        FirestoreProgressState.Error(
                            rootError.message ?: "تعذّرت مزامنة التقدم مع Firebase."
                        )
                    )
                }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = FirestoreProgressState.Loading
        )

    val dailyWordState: StateFlow<DailyWordUiState> = dailyWordRetryTrigger
        .flatMapLatest {
            val todayKey = DailyWordsCatalog.todayDateKey()
            combine<DailyWordRecord?, Int, DailyWordUiState>(
                dailyWordRepo.observeTodayWord(currentUserId, todayKey),
                dailyWordRepo.observeMasteredDailyWordsCount(currentUserId)
            ) { record, masteredCount ->
                if (record == null) {
                    val seed = DailyWordsCatalog.wordForToday()
                    DailyWordUiState.Ready(
                        word = DailyWordRecord(
                            dateKey = todayKey,
                            userId = currentUserId,
                            wordEn = seed.wordEn,
                            ipa = seed.ipa,
                            partOfSpeechAr = seed.partOfSpeechAr,
                            meaningAr = seed.meaningAr,
                            exampleEn = seed.exampleEn,
                            exampleAr = seed.exampleAr,
                            levelCode = seed.levelCode,
                            practiced = false,
                            listenCount = 0L
                        ),
                        masteredCount = masteredCount
                    )
                } else {
                    DailyWordUiState.Ready(
                        word = record,
                        masteredCount = masteredCount
                    )
                }
            }.catch { error ->
                val firestoreError = generateSequence(error) { it.cause }
                    .filterIsInstance<FirebaseFirestoreException>()
                    .firstOrNull()
                if (firestoreError == null && error is CancellationException) throw error
                val rootError = firestoreError ?: error
                Log.w(TAG, "Error observing Firestore dailyWord", rootError)
                emit(
                    DailyWordUiState.Error(
                        rootError.message ?: "تعذّر تحميل كلمة اليوم من Firebase."
                    )
                )
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = DailyWordUiState.Loading
        )

    val userProgress: StateFlow<UserProgress> = combine(
        progressStore.progressFlow,
        firestoreProgressState,
        dailyWordState
    ) { localPrefs, cloudState, wordState ->
        val cloudCompleted = when (cloudState) {
            is FirestoreProgressState.Synced -> cloudState.completedLessonIds
            else -> localPrefs.completedLessonIds
        }
        val masteredWords = (wordState as? DailyWordUiState.Ready)?.masteredCount ?: 0
        val extraCanDo = localPrefs.completedCanDoIds.size
        localPrefs.copy(
            completedLessonIds = cloudCompleted,
            stars = 60 + (cloudCompleted.size * 30) + (extraCanDo * 10) + (masteredWords * 15),
            diamonds = 250 + (cloudCompleted.size * 50)
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = UserProgress()
    )

    val audioState: StateFlow<AudioPlaybackState> = audioManager.state

    val allLessons: List<Lesson> get() = curriculumRepo.allLessons
    val audioLessons: List<AudioLessonTrack> get() = curriculumRepo.audioLessons
    val mindMapNodes: List<MindMapNode> get() = curriculumRepo.mindMapNodes
    val phoneticSounds: List<PhoneticSound> get() = curriculumRepo.phoneticSounds
    val minimalPairs: List<MinimalPair> get() = curriculumRepo.minimalPairs
    val studyRules: List<StudyRule> get() = curriculumRepo.studyRules
    val weeklyQuestions: List<String> get() = curriculumRepo.weeklyCheckInQuestions
    val placementQuestions: List<QuizQuestion> get() = curriculumRepo.placementTestQuestions

    private val _selectedLevelFilter = MutableStateFlow<CefrLevel?>(null)
    val selectedLevelFilter: StateFlow<CefrLevel?> = _selectedLevelFilter.asStateFlow()

    private val _selectedPhoneticCategory = MutableStateFlow<PhoneticCategory?>(null)
    val selectedPhoneticCategory: StateFlow<PhoneticCategory?> = _selectedPhoneticCategory.asStateFlow()

    private val _mentorTabIndex = MutableStateFlow(0)
    val mentorTabIndex: StateFlow<Int> = _mentorTabIndex.asStateFlow()

    private val _syncActionError = MutableStateFlow<String?>(null)
    val syncActionError: StateFlow<String?> = _syncActionError.asStateFlow()

    val quizResultsByLesson: StateFlow<Map<String, QuizResultRecord>> = quizResultsRetryTrigger
        .flatMapLatest {
            quizRepo.observeAllQuizResults(currentUserId)
                .catch { error ->
                    val firestoreError = generateSequence(error) { it.cause }
                        .filterIsInstance<FirebaseFirestoreException>()
                        .firstOrNull()
                    if (firestoreError == null && error is CancellationException) throw error
                    Log.w(TAG, "Error observing Firestore quizResults", firestoreError ?: error)
                    emit(emptyMap())
                }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = emptyMap()
        )

    val activeLessonQuizState: StateFlow<LessonQuizQuestionsState> = combine(
        _activeQuizLessonId,
        _quizQuestionsRetryTrigger
    ) { lessonId, _ -> lessonId }
        .flatMapLatest { lessonId ->
            if (lessonId.isNullOrBlank()) {
                flowOf(LessonQuizQuestionsState.Loading)
            } else {
                quizRepo.observeLessonQuizQuestions(currentUserId, lessonId)
                    .map<List<QuizQuestionRecord>, LessonQuizQuestionsState> { questions ->
                        LessonQuizQuestionsState.Ready(
                            lessonId = lessonId,
                            questions = questions
                        )
                    }
                    .catch { error ->
                        val firestoreError = generateSequence(error) { it.cause }
                            .filterIsInstance<FirebaseFirestoreException>()
                            .firstOrNull()
                        if (firestoreError == null && error is CancellationException) throw error
                        val rootError = firestoreError ?: error
                        Log.w(TAG, "Error observing Firestore quizQuestions", rootError)
                        emit(
                            LessonQuizQuestionsState.Error(
                                rootError.message ?: "تعذّر تحميل أسئلة الاختبار من Firebase."
                            )
                        )
                    }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = LessonQuizQuestionsState.Loading
        )

    private val _quizSubmissionState = MutableStateFlow<QuizSubmissionState>(QuizSubmissionState.Idle)
    val quizSubmissionState: StateFlow<QuizSubmissionState> = _quizSubmissionState.asStateFlow()

    private val _aiUiState = MutableStateFlow<AiUiState>(AiUiState.Idle)
    val aiUiState: StateFlow<AiUiState> = _aiUiState.asStateFlow()

    private val _adviceUiState = MutableStateFlow<AdviceUiState>(AdviceUiState.Idle)
    val adviceUiState: StateFlow<AdviceUiState> = _adviceUiState.asStateFlow()

    private val _mentorMessages = MutableStateFlow(
        listOf(
            MentorMessage(
                id = "welcome_1",
                isUser = false,
                text = "أهلاً بك في رفيق الإنجليزية! أنا مرشدك الذكي لتعلّم اللغة خطوة بخطوة بناءً على تقدمك ومستواك الحالي. اسألني عن أي قاعدة، أو اكتب لي جملة بالإنجليزية لأصححها لك."
            )
        )
    )
    val mentorMessages: StateFlow<List<MentorMessage>> = _mentorMessages.asStateFlow()

    fun getLesson(lessonId: String): Lesson? = curriculumRepo.lessonById(lessonId)

    fun audioTrackForLesson(lessonId: String): AudioLessonTrack =
        curriculumRepo.audioTrackForLesson(lessonId)
            ?: (getLesson(lessonId) ?: allLessons.first()).toAudioLessonTrack()

    fun retryFirestoreSync() {
        _syncActionError.value = null
        syncRetryTrigger.value += 1
    }

    fun retryDailyWordSync() {
        viewModelScope.launch {
            dailyWordRepo.ensureTodayWordExists(currentUserId)
            dailyWordRetryTrigger.value += 1
        }
    }

    fun listenToDailyWord(word: DailyWordRecord) {
        audioManager.speak("daily_word_${word.dateKey}", word.wordEn)
        viewModelScope.launch {
            dailyWordRepo.ensureTodayWordExists(currentUserId, word.dateKey)
            dailyWordRepo.recordListen(currentUserId, word.dateKey)
        }
    }

    fun toggleDailyWordPracticed(word: DailyWordRecord) {
        viewModelScope.launch {
            dailyWordRepo.ensureTodayWordExists(currentUserId, word.dateKey)
            dailyWordRepo.setPracticed(currentUserId, word.dateKey, !word.practiced)
        }
    }

    fun switchDailyWord(currentWord: DailyWordRecord) {
        val nextSeed = DailyWordsCatalog.nextWordAfter(currentWord.wordEn)
        viewModelScope.launch {
            dailyWordRepo.switchTodayWord(currentUserId, currentWord.dateKey, nextSeed)
        }
    }

    fun dismissSyncActionError() {
        _syncActionError.value = null
    }

    fun openLessonQuizSession(lesson: Lesson) {
        if (_activeQuizLessonId.value != lesson.id) {
            _activeQuizLessonId.value = lesson.id
            _quizSubmissionState.value = QuizSubmissionState.Idle
        }
        viewModelScope.launch {
            quizRepo.ensureLessonQuizSeeded(currentUserId, lesson)
        }
    }

    fun retryLessonQuizLoad(lesson: Lesson) {
        _activeQuizLessonId.value = lesson.id
        viewModelScope.launch {
            quizRepo.ensureLessonQuizSeeded(currentUserId, lesson)
            _quizQuestionsRetryTrigger.value += 1
        }
    }

    fun resetQuizSubmissionForRetake() {
        _quizSubmissionState.value = QuizSubmissionState.Idle
    }

    fun submitLessonQuiz(lesson: Lesson, correctCount: Int, totalCount: Int) {
        if (_quizSubmissionState.value is QuizSubmissionState.Submitting) return
        viewModelScope.launch {
            _quizSubmissionState.value = QuizSubmissionState.Submitting
            val result = quizRepo.submitQuizAttempt(
                userId = currentUserId,
                lesson = lesson,
                correctCount = correctCount,
                totalCount = totalCount
            )
            result.onSuccess { record ->
                _quizSubmissionState.value = QuizSubmissionState.Evaluated(record)
                if (record.passed) {
                    val markRes = firestoreProgressRepo.markLessonCompleted(
                        userId = currentUserId,
                        lessonId = lesson.id,
                        levelCode = lesson.level.code,
                        titleAr = lesson.titleAr
                    )
                    if (markRes.isSuccess) {
                        progressStore.markLessonCompleted(lesson.id)
                    }
                }
            }.onFailure { error ->
                _quizSubmissionState.value = QuizSubmissionState.Error(
                    error.localizedMessage ?: "تعذّر حفظ نتيجة الاختبار في Firebase. حاول مجدداً."
                )
            }
        }
    }

    fun setLevelFilter(level: CefrLevel?) {
        _selectedLevelFilter.value = level
    }

    fun setPhoneticCategory(category: PhoneticCategory?) {
        _selectedPhoneticCategory.value = category
    }

    fun selectMentorTab(index: Int) {
        _mentorTabIndex.value = index.coerceIn(0, 2)
    }

    fun speak(itemId: String, text: String) {
        audioManager.speak(itemId, text)
    }

    fun speakSequence(sequenceId: String, items: List<Pair<String, String>>) {
        audioManager.speakSequence(sequenceId, items)
    }

    fun playLessonAudioTrack(track: AudioLessonTrack) {
        audioManager.playAudioTrack(track)
    }

    fun nextAudioTrackSegment() {
        audioManager.nextTrackSegment()
    }

    fun previousAudioTrackSegment() {
        audioManager.previousTrackSegment()
    }

    fun stopAudio() {
        audioManager.stop()
    }

    fun toggleAudioSpeed() {
        audioManager.toggleSpeed()
    }

    fun toggleLessonCompleted(lessonId: String) {
        val lesson = getLesson(lessonId) ?: return
        val currentlyCompleted = lessonId in userProgress.value.completedLessonIds
        viewModelScope.launch {
            val result = if (currentlyCompleted) {
                firestoreProgressRepo.unmarkLessonCompleted(currentUserId, lesson.id)
            } else {
                firestoreProgressRepo.markLessonCompleted(
                    userId = currentUserId,
                    lessonId = lesson.id,
                    levelCode = lesson.level.code,
                    titleAr = lesson.titleAr
                )
            }
            if (result.isSuccess) {
                _syncActionError.value = null
                if (currentlyCompleted) {
                    if (lessonId in progressStore.currentCompletedIdsFallback(userProgress.value.completedLessonIds)) {
                        progressStore.toggleLessonCompleted(lessonId)
                    }
                } else {
                    progressStore.markLessonCompleted(lessonId)
                }
            } else {
                _syncActionError.value = result.exceptionOrNull()?.localizedMessage
                    ?: "تعذّر حفظ حالة الدرس في Firebase. حاول مجدداً."
            }
        }
    }

    fun markLessonCompleted(lessonId: String) {
        val lesson = getLesson(lessonId) ?: return
        viewModelScope.launch {
            val result = firestoreProgressRepo.markLessonCompleted(
                userId = currentUserId,
                lessonId = lesson.id,
                levelCode = lesson.level.code,
                titleAr = lesson.titleAr
            )
            if (result.isSuccess) {
                _syncActionError.value = null
                progressStore.markLessonCompleted(lessonId)
            } else {
                _syncActionError.value = result.exceptionOrNull()?.localizedMessage
                    ?: "تعذّر تسجيل إكمال الدرس في Firebase."
            }
        }
    }

    private fun ProgressDataStore.currentCompletedIdsFallback(current: Set<String>): Set<String> = current

    fun toggleCanDo(canDoId: String) {
        viewModelScope.launch {
            progressStore.toggleCanDo(canDoId)
        }
    }

    fun toggleBookmark(sentenceId: String) {
        viewModelScope.launch {
            progressStore.toggleBookmark(sentenceId)
        }
    }

    fun setStudyMode(mode: StudyMode) {
        viewModelScope.launch {
            progressStore.setStudyMode(mode)
        }
    }

    fun updateDailyReminder(enabled: Boolean, hour: Int, minute: Int) {
        viewModelScope.launch {
            progressStore.setDailyReminder(enabled, hour, minute)
            reminderScheduler.applySchedule(enabled, hour, minute)
        }
    }

    fun sendTestReminderNotification(): Boolean {
        return reminderScheduler.sendTestNotification()
    }

    fun savePlacementResult(correctCount: Int): CefrLevel {
        val level = curriculumRepo.calculatePlacementLevel(correctCount)
        viewModelScope.launch {
            progressStore.setAssessedLevel(level)
        }
        return level
    }

    fun nextUncompletedLessonTitle(): String? {
        val completed = userProgress.value.completedLessonIds
        return allLessons.firstOrNull { it.id !in completed }?.let { "${it.titleAr} (${it.level.code})" }
    }

    private fun buildProgressSnapshot(): LearnerProgressSnapshot {
        val progress = userProgress.value
        val completedTitles = allLessons
            .filter { it.id in progress.completedLessonIds }
            .map { "${it.titleAr} (${it.level.code})" }
        val nextTitle = allLessons
            .firstOrNull { it.id !in progress.completedLessonIds }
            ?.let { "${it.titleAr} (${it.level.code})" }

        return LearnerProgressSnapshot(
            assessedLevel = progress.assessedLevel,
            studyMode = progress.studyMode,
            completedLessonTitles = completedTitles,
            nextLessonTitle = nextTitle,
            totalLessons = allLessons.size,
            completedCanDoCount = progress.completedCanDoIds.size,
            bookmarkedCount = progress.bookmarkedSentenceIds.size,
            stars = progress.stars,
            diamonds = progress.diamonds
        )
    }

    fun refreshPersonalizedAdvice(force: Boolean = false) {
        val current = _adviceUiState.value
        if (current is AdviceUiState.Loading) return
        if (!force && current is AdviceUiState.Success) return

        viewModelScope.launch {
            _adviceUiState.value = AdviceUiState.Loading
            try {
                val advice = aiMentorRepo.generatePersonalizedAdvice(buildProgressSnapshot())
                _adviceUiState.value = AdviceUiState.Success(advice)
            } catch (e: TimeoutCancellationException) {
                Log.e(TAG, "Personalized advice timed out", e)
                _adviceUiState.value = AdviceUiState.Error(
                    "انتهت مهلة تحضير النصيحة المخصصة (60 ثانية). تحقق من الاتصال وأعد المحاولة."
                )
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "Failed to load personalized advice", e)
                _adviceUiState.value = AdviceUiState.Error(
                    e.localizedMessage ?: "تعذّر جلب النصيحة المخصصة من المرشد الذكي."
                )
            }
        }
    }

    fun sendMessageToMentor(rawText: String) {
        val prompt = rawText.trim()
        if (prompt.isEmpty() || _aiUiState.value is AiUiState.Loading || _aiUiState.value is AiUiState.Streaming) {
            return
        }

        val userMsg = MentorMessage(
            id = UUID.randomUUID().toString(),
            isUser = true,
            text = prompt
        )
        val historySnippet = _mentorMessages.value
            .takeLast(4)
            .joinToString("\n") { msg ->
                val role = if (msg.isUser) "المتعلم" else "المرشد"
                "$role: ${msg.text}"
            }

        _mentorMessages.value = _mentorMessages.value + userMsg
        _aiUiState.value = AiUiState.Loading

        viewModelScope.launch {
            var latestReply = ""
            try {
                aiMentorRepo.streamMentorReply(
                    userPrompt = prompt,
                    snapshot = buildProgressSnapshot(),
                    historySnippet = historySnippet
                ).collect { partial ->
                    latestReply = partial
                    _aiUiState.value = AiUiState.Streaming(partial)
                }

                _mentorMessages.value = _mentorMessages.value + MentorMessage(
                    id = UUID.randomUUID().toString(),
                    isUser = false,
                    text = latestReply
                )
                _aiUiState.value = AiUiState.Success
            } catch (e: TimeoutCancellationException) {
                Log.e(TAG, "Mentor generation timed out", e)
                val errorText = "انتهت مهلة الاتصال بالمرشد الذكي (60 ثانية). تحقق من الشبكة وحاول مرة أخرى."
                _aiUiState.value = AiUiState.Error(errorText)
                _mentorMessages.value = _mentorMessages.value + MentorMessage(
                    id = UUID.randomUUID().toString(),
                    isUser = false,
                    text = errorText,
                    isError = true
                )
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "Mentor generation failed", e)
                val errorText = e.localizedMessage ?: "تعذّر الوصول إلى المرشد الذكي حالياً."
                _aiUiState.value = AiUiState.Error(errorText)
                _mentorMessages.value = _mentorMessages.value + MentorMessage(
                    id = UUID.randomUUID().toString(),
                    isUser = false,
                    text = "خطأ في الاتصال بالمرشد الذكي: $errorText",
                    isError = true
                )
            }
        }
    }

    override fun onCleared() {
        audioManager.shutdown()
        super.onCleared()
    }
}
