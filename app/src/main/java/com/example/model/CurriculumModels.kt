package com.example.model

enum class CefrLevel(
    val code: String,
    val titleAr: String,
    val subtitleAr: String,
    val canDoGoals: List<String>
) {
    A1(
        code = "A1",
        titleAr = "التأسيس الأول",
        subtitleAr = "البداية من الصفر وبناء الجمل اليومية",
        canDoGoals = listOf(
            "التعريف بنفسي والترحيب بالآخرين بثقة",
            "استخدام الضمائر وفعل الكينونة (am / is / are) بدون أخطاء",
            "التحدث عن الروتين اليومي والأوقات والأرقام",
            "الطلب في المطعم والسؤال عن الأسعار والاتجاهات"
        )
    ),
    A2(
        code = "A2",
        titleAr = "التوسع اليومي",
        subtitleAr = "حكاية المواقف الماضية والتواصل العملي",
        canDoGoals = listOf(
            "وصف أحداث الماضي البسيط وقصص السفر",
            "المقارنة بين الأشياء والتعبير عن الخطط المستقبلية",
            "التعامل في المطار والعيادة والتسوق بسلاسة",
            "فهم الحوارات القصيرة والرد بجمل مترابطة"
        )
    ),
    B1(
        code = "B1",
        titleAr = "الاستقلالية والطلاقة",
        subtitleAr = "النقاش في العمل والسرد المتصل",
        canDoGoals = listOf(
            "إبداء الرأي والموافقة والاعتراض بأسلوب مهذب",
            "الربط بين الماضي والحاضر (Present Perfect)",
            "إجراء مكالمة عمل أو مقابلة وظيفية مبدئية",
            "استخدام الجمل الشرطية (If) والالتزامات (must / should)"
        )
    ),
    B2(
        code = "B2",
        titleAr = "المستوى المهني",
        subtitleAr = "تحليل الأفكار وفهم التفاصيل الدقيقة",
        canDoGoals = listOf(
            "تقديم عروض مهنية وشرح البيانات والنتائج",
            "استخدام الأفعال المركبة (Phrasal Verbs) بطبيعية",
            "فهم المبني للمجهول وأسلوب التقارير الرسمية",
            "متابعة البودكاست والأفلام دون ترجمة حرفية"
        )
    ),
    C1(
        code = "C1",
        titleAr = "الإجادة المتقدمة",
        subtitleAr = "الإقناع، الأسلوب الأكاديمي، والمرونة العالية",
        canDoGoals = listOf(
            "صياغة حجج مقنعة باستخدام أدوات الربط المتقدمة",
            "استخدام التراكيب البلاغية والشرطية المتقدمة (Inversion)",
            "التعبير الدقيق عن الفروق الدقيقة بين المفردات",
            "كتابة رسائل وتقارير تنفيذية احترافية"
        )
    ),
    C2(
        code = "C2",
        titleAr = "الإتقان التام",
        subtitleAr = "التعبيرات الاصطلاحية وسرعة البديهة",
        canDoGoals = listOf(
            "فهم التلميحات الثقافية والتعبيرات الاصطلاحية (Idioms)",
            "تلخيص النقاشات المعقدة وإعادة صياغتها ببلاغة",
            "التحدث بإيقاع ونبرة قريبة جداً من المتحدث الأصلي",
            "التبديل السلس بين الأسلوب الرسمي والودي"
        )
    )
}

enum class MindMapBranch(val titleAr: String) {
    HUB("المحور"),
    GRAMMAR("القواعد"),
    SPEAKING("المحادثة"),
    PHONETICS("الصوتيات"),
    VOCAB("الجمل والمفردات"),
    STORY("القصص")
}

enum class StudyMode(val titleAr: String, val minutes: Int, val descriptionAr: String) {
    NORMAL(
        titleAr = "يوم عادي (25 دقيقة)",
        minutes = 25,
        descriptionAr = "قاعدة مبسطة + حوار ناطق + 5 جمل + تمرين صوتي أو اختبار قصير"
    ),
    HEAVY(
        titleAr = "يوم مضغوط (5 دقائق)",
        minutes = 5,
        descriptionAr = "مراجعة سريعة: استمع لـ 3 جمل أساسية وحل سؤالاً واحداً للحفاظ على السلسلة"
    )
}

data class SentenceItem(
    val id: String,
    val english: String,
    val transliterationAr: String,
    val arabic: String,
    val category: String
)

data class DialogueLine(
    val speaker: String,
    val english: String,
    val transliterationAr: String,
    val arabic: String
)

data class GrammarPoint(
    val titleAr: String,
    val titleEn: String,
    val simpleRuleAr: String,
    val formula: String,
    val commonArabMistakeAr: String,
    val examples: List<SentenceItem>
)

data class StorySegment(
    val titleEn: String,
    val titleAr: String,
    val paragraphs: List<Pair<String, String>>,
    val moralOrTipAr: String
)

data class QuizQuestion(
    val id: String,
    val promptAr: String,
    val englishPrompt: String,
    val options: List<String>,
    val correctIndex: Int,
    val explanationAr: String,
    val level: CefrLevel
)

data class AudioTrackSegment(
    val id: String,
    val speakerOrLabel: String,
    val englishText: String,
    val arabicMeaning: String,
    val approxSeconds: Int
)

data class AudioLessonTrack(
    val trackId: String,
    val lessonId: String,
    val titleAr: String,
    val titleEn: String,
    val level: CefrLevel,
    val accentLabel: String = "US English",
    val segments: List<AudioTrackSegment>
) {
    val totalDurationSeconds: Int
        get() = segments.sumOf { it.approxSeconds }
}

data class Lesson(
    val id: String,
    val titleAr: String,
    val titleEn: String,
    val level: CefrLevel,
    val unitAr: String,
    val durationMinutes: Int,
    val summaryAr: String,
    val goldenTipAr: String,
    val branch: MindMapBranch,
    val grammarPoints: List<GrammarPoint>,
    val dialogue: List<DialogueLine>,
    val sentences: List<SentenceItem>,
    val story: StorySegment,
    val quiz: List<QuizQuestion>
) {
    fun toAudioLessonTrack(): AudioLessonTrack {
        val dialogueSegments = dialogue.mapIndexed { idx, line ->
            AudioTrackSegment(
                id = "${id}_track_dl_$idx",
                speakerOrLabel = line.speaker,
                englishText = line.english,
                arabicMeaning = line.arabic,
                approxSeconds = (line.english.split(" ").size / 2).coerceIn(4, 12)
            )
        }
        val sentenceSegments = sentences.take(3).mapIndexed { idx, sent ->
            AudioTrackSegment(
                id = "${id}_track_st_$idx",
                speakerOrLabel = sent.category,
                englishText = sent.english,
                arabicMeaning = sent.arabic,
                approxSeconds = (sent.english.split(" ").size / 2).coerceIn(4, 10)
            )
        }
        return AudioLessonTrack(
            trackId = "track_$id",
            lessonId = id,
            titleAr = titleAr,
            titleEn = titleEn,
            level = level,
            segments = dialogueSegments + sentenceSegments
        )
    }
}

enum class PhoneticCategory(val titleAr: String, val subtitleAr: String) {
    VOWELS_SCHWA("الحركات والـ Schwa /ə/", "سر وضوح النطق الأمريكي"),
    HARD_CONSONANTS("الحروف الصعبة للعرب", "TH / R / P-B / V-W / S-SH"),
    FLAP_LINKING("الربط والـ Flap T", "كيف تنطق الكلمات متصلة بطبيعية"),
    STRESS_INTONATION("النبر والتنغيم", "تلوين الصوت للسؤال والتأكيد"),
    REDUCTIONS("الاختصارات اليومية", "Gonna / Wanna / Gotta")
}

data class PhoneticExample(
    val word: String,
    val ipa: String,
    val arabicApprox: String,
    val sentence: String,
    val meaningAr: String
)

data class PhoneticSound(
    val id: String,
    val symbol: String,
    val nameEn: String,
    val nameAr: String,
    val category: PhoneticCategory,
    val howToPronounceAr: String,
    val commonMistakeAr: String,
    val examples: List<PhoneticExample>
)

data class MinimalPair(
    val id: String,
    val soundA: String,
    val soundB: String,
    val wordA: String,
    val ipaA: String,
    val meaningA: String,
    val wordB: String,
    val ipaB: String,
    val meaningB: String,
    val tipAr: String
)

data class MindMapNode(
    val id: String,
    val lessonId: String?,
    val titleAr: String,
    val titleEn: String,
    val subtitleAr: String,
    val level: CefrLevel?,
    val branch: MindMapBranch,
    val xFraction: Float,
    val yFraction: Float,
    val parentId: String?,
    val isRoot: Boolean = false
)

data class MentorMessage(
    val id: String,
    val isUser: Boolean,
    val text: String,
    val isError: Boolean = false
)

data class StudyRule(
    val number: Int,
    val titleAr: String,
    val bodyAr: String
)
