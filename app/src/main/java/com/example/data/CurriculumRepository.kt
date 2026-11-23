package com.example.data

import com.example.model.AudioLessonTrack
import com.example.model.CefrLevel
import com.example.model.Lesson
import com.example.model.MindMapNode
import com.example.model.MinimalPair
import com.example.model.PhoneticSound
import com.example.model.QuizQuestion
import com.example.model.StudyRule

class CurriculumRepository {
    val allLessons: List<Lesson> by lazy {
        LessonsDataA1A2.lessons + LessonsDataB1C2.lessons
    }

    val audioLessons: List<AudioLessonTrack> by lazy {
        allLessons.map { it.toAudioLessonTrack() }
    }

    val mindMapNodes: List<MindMapNode> get() = MindMapData.nodes
    val phoneticSounds: List<PhoneticSound> get() = PhoneticsData.sounds
    val minimalPairs: List<MinimalPair> get() = PhoneticsData.minimalPairs
    val studyRules: List<StudyRule> get() = MentorData.goldenStudyRules
    val weeklyCheckInQuestions: List<String> get() = MentorData.weeklyCheckInQuestions
    val placementTestQuestions: List<QuizQuestion> get() = MentorData.placementTestQuestions

    fun lessonById(id: String): Lesson? = allLessons.find { it.id == id }

    fun audioTrackForLesson(lessonId: String): AudioLessonTrack? =
        audioLessons.find { it.lessonId == lessonId }

    fun lessonsByLevel(level: CefrLevel?): List<Lesson> =
        if (level == null) allLessons else allLessons.filter { it.level == level }

    fun calculatePlacementLevel(correctCount: Int): CefrLevel =
        MentorData.calculateCefrLevel(correctCount)
}
