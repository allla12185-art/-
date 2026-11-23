package com.example.ui

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.filled.AccountTree
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.outlined.AccountTree
import androidx.compose.material.icons.outlined.GraphicEq
import androidx.compose.material.icons.outlined.Psychology
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.credentials.CredentialManager
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.R
import com.example.data.AudioPlayerManager
import com.example.data.FirestoreDailyWordRepository
import com.example.data.FirestoreProgressRepository
import com.example.data.FirestoreQuizRepository
import com.example.data.ProgressDataStore
import com.example.notifications.DailyReminderScheduler
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.LessonDetailScreen
import com.example.ui.screens.LessonsScreen
import com.example.ui.screens.MentorScreen
import com.example.ui.screens.MindMapScreen
import com.example.ui.screens.PhoneticsScreen
import com.example.ui.screens.signOutUser
import com.example.viewmodel.MainViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

object AppRoutes {
    const val ROUTE_MIND_MAP = "mind_map"
    const val ROUTE_LESSONS = "lessons"
    const val ROUTE_PHONETICS = "phonetics"
    const val ROUTE_MENTOR = "mentor"
    const val ROUTE_LESSON_DETAIL = "lesson_detail/{lessonId}"

    fun lessonDetailRoute(lessonId: String): String = "lesson_detail/$lessonId"
}

private data class TopDestination(
    val route: String,
    val labelResId: Int,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String
)

private val topDestinations = listOf(
    TopDestination(
        route = AppRoutes.ROUTE_MIND_MAP,
        labelResId = R.string.nav_mind_map,
        selectedIcon = Icons.Filled.AccountTree,
        unselectedIcon = Icons.Outlined.AccountTree,
        testTag = "nav_mind_map"
    ),
    TopDestination(
        route = AppRoutes.ROUTE_LESSONS,
        labelResId = R.string.nav_lessons,
        selectedIcon = Icons.AutoMirrored.Filled.MenuBook,
        unselectedIcon = Icons.AutoMirrored.Outlined.MenuBook,
        testTag = "nav_lessons"
    ),
    TopDestination(
        route = AppRoutes.ROUTE_PHONETICS,
        labelResId = R.string.nav_phonetics,
        selectedIcon = Icons.Filled.GraphicEq,
        unselectedIcon = Icons.Outlined.GraphicEq,
        testTag = "nav_phonetics"
    ),
    TopDestination(
        route = AppRoutes.ROUTE_MENTOR,
        labelResId = R.string.nav_mentor,
        selectedIcon = Icons.Filled.Psychology,
        unselectedIcon = Icons.Outlined.Psychology,
        testTag = "nav_mentor"
    )
)

internal fun FirebaseAuth.authStateFlow(): Flow<FirebaseUser?> = callbackFlow {
    val listener = FirebaseAuth.AuthStateListener { auth ->
        trySend(auth.currentUser)
    }
    addAuthStateListener(listener)
    awaitClose { removeAuthStateListener(listener) }
}

@Composable
fun RafeeqApp(auth: FirebaseAuth = Firebase.auth) {
    val currentUser by auth.authStateFlow().collectAsStateWithLifecycle(initialValue = auth.currentUser)
    val user = currentUser

    if (user == null) {
        AuthScreen(onAuthSuccess = {})
    } else {
        key(user.uid) {
            AuthenticatedRafeeqApp(
                currentUserId = user.uid,
                userDisplayName = user.displayName,
                userEmail = user.email
            )
        }
    }
}

@Composable
private fun AuthenticatedRafeeqApp(
    currentUserId: String,
    userDisplayName: String?,
    userEmail: String?
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val credentialManager = remember(context) { CredentialManager.create(context) }

    val viewModel: MainViewModel = viewModel(
        key = currentUserId,
        factory = viewModelFactory {
            initializer {
                val app = checkNotNull(this[APPLICATION_KEY]) {
                    "APPLICATION_KEY missing from CreationExtras"
                }
                val databaseId = app.getString(R.string.firestore_database_id)
                val db = FirebaseFirestore.getInstance(databaseId)
                MainViewModel(
                    currentUserId = currentUserId,
                    firestoreProgressRepo = FirestoreProgressRepository(db),
                    dailyWordRepo = FirestoreDailyWordRepository(db),
                    quizRepo = FirestoreQuizRepository(db),
                    progressStore = ProgressDataStore(app),
                    audioManager = AudioPlayerManager(app),
                    reminderScheduler = DailyReminderScheduler(app)
                )
            }
        }
    )

    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    val userProgress by viewModel.userProgress.collectAsStateWithLifecycle()
    val firestoreProgressState by viewModel.firestoreProgressState.collectAsStateWithLifecycle()
    val dailyWordState by viewModel.dailyWordState.collectAsStateWithLifecycle()
    val quizResultsByLesson by viewModel.quizResultsByLesson.collectAsStateWithLifecycle()
    val activeLessonQuizState by viewModel.activeLessonQuizState.collectAsStateWithLifecycle()
    val quizSubmissionState by viewModel.quizSubmissionState.collectAsStateWithLifecycle()
    val syncActionError by viewModel.syncActionError.collectAsStateWithLifecycle()
    val audioState by viewModel.audioState.collectAsStateWithLifecycle()
    val selectedLevelFilter by viewModel.selectedLevelFilter.collectAsStateWithLifecycle()
    val selectedPhoneticCategory by viewModel.selectedPhoneticCategory.collectAsStateWithLifecycle()
    val aiUiState by viewModel.aiUiState.collectAsStateWithLifecycle()
    val adviceUiState by viewModel.adviceUiState.collectAsStateWithLifecycle()
    val mentorMessages by viewModel.mentorMessages.collectAsStateWithLifecycle()
    val mentorTabIndex by viewModel.mentorTabIndex.collectAsStateWithLifecycle()

    val isTopLevelRoute = topDestinations.any { it.route == currentRoute }

    fun navigateTopLevel(route: String) {
        navController.navigate(route) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val useNavigationRail = maxWidth >= 600.dp

        Scaffold(
            contentWindowInsets = WindowInsets.safeDrawing,
            bottomBar = {
                if (isTopLevelRoute && !useNavigationRail) {
                    NavigationBar {
                        topDestinations.forEach { dest ->
                            val selected = currentRoute == dest.route
                            NavigationBarItem(
                                selected = selected,
                                onClick = { navigateTopLevel(dest.route) },
                                modifier = Modifier.testTag(dest.testTag),
                                icon = {
                                    Icon(
                                        imageVector = if (selected) dest.selectedIcon else dest.unselectedIcon,
                                        contentDescription = stringResource(dest.labelResId)
                                    )
                                },
                                label = { Text(stringResource(dest.labelResId)) }
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                if (isTopLevelRoute && useNavigationRail) {
                    NavigationRail {
                        topDestinations.forEach { dest ->
                            val selected = currentRoute == dest.route
                            NavigationRailItem(
                                selected = selected,
                                onClick = { navigateTopLevel(dest.route) },
                                modifier = Modifier.testTag(dest.testTag),
                                icon = {
                                    Icon(
                                        imageVector = if (selected) dest.selectedIcon else dest.unselectedIcon,
                                        contentDescription = stringResource(dest.labelResId)
                                    )
                                },
                                label = { Text(stringResource(dest.labelResId)) }
                            )
                        }
                    }
                }

                NavHost(
                    navController = navController,
                    startDestination = AppRoutes.ROUTE_MIND_MAP,
                    modifier = Modifier.weight(1f)
                ) {
                    composable(AppRoutes.ROUTE_MIND_MAP) {
                        MindMapScreen(
                            nodes = viewModel.mindMapNodes,
                            totalLessons = viewModel.allLessons.size,
                            userProgress = userProgress,
                            onToggleStudyMode = viewModel::setStudyMode,
                            onOpenLesson = { lessonId ->
                                navController.navigate(AppRoutes.lessonDetailRoute(lessonId))
                            },
                            onOpenPhonetics = { navigateTopLevel(AppRoutes.ROUTE_PHONETICS) },
                            onOpenPlacementTest = {
                                viewModel.selectMentorTab(1)
                                navigateTopLevel(AppRoutes.ROUTE_MENTOR)
                            },
                            onOpenStudyPlan = {
                                viewModel.selectMentorTab(2)
                                navigateTopLevel(AppRoutes.ROUTE_MENTOR)
                            },
                            onOpenMentor = {
                                viewModel.selectMentorTab(0)
                                navigateTopLevel(AppRoutes.ROUTE_MENTOR)
                            },
                            onUpdateDailyReminder = viewModel::updateDailyReminder,
                            onSendTestReminder = viewModel::sendTestReminderNotification,
                            dailyWordState = dailyWordState,
                            onListenDailyWord = viewModel::listenToDailyWord,
                            onSpeakText = viewModel::speak,
                            onToggleDailyWordPracticed = viewModel::toggleDailyWordPracticed,
                            onSwitchDailyWord = viewModel::switchDailyWord,
                            onRetryDailyWord = viewModel::retryDailyWordSync
                        )
                    }

                    composable(AppRoutes.ROUTE_LESSONS) {
                        val trackMap = viewModel.audioLessons.associateBy { it.lessonId }
                        LessonsScreen(
                            lessons = viewModel.allLessons,
                            audioTracksByLesson = trackMap,
                            quizResultsByLesson = quizResultsByLesson,
                            audioState = audioState,
                            firestoreProgressState = firestoreProgressState,
                            syncActionError = syncActionError,
                            selectedLevel = selectedLevelFilter,
                            completedLessonIds = userProgress.completedLessonIds,
                            onSelectLevel = viewModel::setLevelFilter,
                            onToggleComplete = viewModel::toggleLessonCompleted,
                            onRetrySync = viewModel::retryFirestoreSync,
                            onDismissSyncError = viewModel::dismissSyncActionError,
                            onPlayTrack = viewModel::playLessonAudioTrack,
                            onNextSegment = viewModel::nextAudioTrackSegment,
                            onPrevSegment = viewModel::previousAudioTrackSegment,
                            onToggleSpeed = viewModel::toggleAudioSpeed,
                            onOpenLesson = { lessonId ->
                                navController.navigate(AppRoutes.lessonDetailRoute(lessonId))
                            }
                        )
                    }

                    composable(AppRoutes.ROUTE_PHONETICS) {
                        PhoneticsScreen(
                            sounds = viewModel.phoneticSounds,
                            minimalPairs = viewModel.minimalPairs,
                            selectedCategory = selectedPhoneticCategory,
                            audioState = audioState,
                            onSelectCategory = viewModel::setPhoneticCategory,
                            onSpeak = viewModel::speak,
                            onToggleSpeed = viewModel::toggleAudioSpeed
                        )
                    }

                    composable(AppRoutes.ROUTE_MENTOR) {
                        MentorScreen(
                            messages = mentorMessages,
                            aiUiState = aiUiState,
                            adviceUiState = adviceUiState,
                            audioState = audioState,
                            userProgress = userProgress,
                            totalLessons = viewModel.allLessons.size,
                            nextLessonTitle = viewModel.nextUncompletedLessonTitle(),
                            studyRules = viewModel.studyRules,
                            weeklyQuestions = viewModel.weeklyQuestions,
                            placementQuestions = viewModel.placementQuestions,
                            activeTab = mentorTabIndex,
                            onSelectTab = viewModel::selectMentorTab,
                            onRefreshAdvice = viewModel::refreshPersonalizedAdvice,
                            onSpeak = viewModel::speak,
                            onSendMessage = viewModel::sendMessageToMentor,
                            onSetStudyMode = viewModel::setStudyMode,
                            onToggleCanDo = viewModel::toggleCanDo,
                            onSavePlacementScore = viewModel::savePlacementResult,
                            onFilterLessonsByLevel = { lvl ->
                                viewModel.setLevelFilter(lvl)
                                navigateTopLevel(AppRoutes.ROUTE_LESSONS)
                            },
                            userDisplayName = userDisplayName,
                            userEmail = userEmail,
                            onSignOut = {
                                viewModel.stopAudio()
                                signOutUser(credentialManager, scope)
                            },
                            onUpdateDailyReminder = viewModel::updateDailyReminder,
                            onSendTestReminder = viewModel::sendTestReminderNotification
                        )
                    }

                    composable(
                        route = AppRoutes.ROUTE_LESSON_DETAIL,
                        arguments = listOf(
                            navArgument("lessonId") { type = NavType.StringType }
                        )
                    ) { entry ->
                        val lessonId = entry.arguments?.getString("lessonId").orEmpty()
                        val lesson = viewModel.getLesson(lessonId) ?: viewModel.allLessons.first()
                        LessonDetailScreen(
                            lesson = lesson,
                            isCompleted = lesson.id in userProgress.completedLessonIds,
                            bookmarkedSentenceIds = userProgress.bookmarkedSentenceIds,
                            audioState = audioState,
                            quizQuestionsState = activeLessonQuizState,
                            quizSubmissionState = quizSubmissionState,
                            savedQuizResult = quizResultsByLesson[lesson.id],
                            onEnsureQuizLoaded = { viewModel.openLessonQuizSession(lesson) },
                            onRetryQuizLoad = { viewModel.retryLessonQuizLoad(lesson) },
                            onSubmitQuiz = { correctCount, totalCount ->
                                viewModel.submitLessonQuiz(lesson, correctCount, totalCount)
                            },
                            onResetQuizForRetake = viewModel::resetQuizSubmissionForRetake,
                            onBack = {
                                viewModel.stopAudio()
                                navController.popBackStack()
                            },
                            onToggleComplete = { viewModel.toggleLessonCompleted(lesson.id) },
                            onMarkCompleted = { viewModel.markLessonCompleted(lesson.id) },
                            onToggleBookmark = viewModel::toggleBookmark,
                            onSpeak = viewModel::speak,
                            onSpeakSequence = viewModel::speakSequence,
                            onToggleSpeed = viewModel::toggleAudioSpeed
                        )
                    }
                }
            }
        }
    }
}
