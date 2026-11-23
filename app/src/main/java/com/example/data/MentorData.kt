package com.example.data

import com.example.model.CefrLevel
import com.example.model.QuizQuestion
import com.example.model.StudyRule

object MentorData {
    val goldenStudyRules: List<StudyRule> = listOf(
        StudyRule(1, "الاستمرارية فوق الكثافة", "25 دقيقة كل يوم أفضل بـ 10 مرات من 3 ساعات في نهاية الأسبوع."),
        StudyRule(2, "احفظ بالجملة لا بالكلمة", "لا تحفظ كلمة منفردة أبداً؛ احفظها داخل جملة تسمعها وتنطقها بصوت مسموع."),
        StudyRule(3, "الظلال الصوتي (Shadowing)", "استمع للجملة ببطء ثم بالسرعة الطبيعية، ورددها بنفس النبرة والتنغيم."),
        StudyRule(4, "التدرج بالذكاء", "إذا نسيت شيئاً فهذا طبيعي؛ راجع أخطاءك كل جمعة بثلاثة أسئلة سريعة."),
        StudyRule(5, "خطة اليوم المضغوط", "في الأيام المزدحمة شغّل وضع الـ 5 دقائق ولا تكسر سلسلتك أبداً.")
    )

    val weeklyCheckInQuestions: List<String> = listOf(
        "ما أكثر قاعدة أو صوت شعرت أنه أصبح أسهل عليك هذا الأسبوع؟",
        "هل استخدمت وضع الـ 5 دقائق في الأيام المضغوطة دون انقطاع؟",
        "ما الموقف الواقعي الذي تريد أن تتمرّن عليه مع المرشد الذكي الأسبوع القادم؟"
    )

    val placementTestQuestions: List<QuizQuestion> = listOf(
        QuizQuestion(
            id = "pt_1",
            promptAr = "السؤال 1 (المستوى التأسيسي A1):",
            englishPrompt = "Hello, my name is Tariq and I ___ from Jordan.",
            options = listOf("am", "is", "are", "be"),
            correctIndex = 0,
            explanationAr = "الضمير I يأخذ دائماً am.",
            level = CefrLevel.A1
        ),
        QuizQuestion(
            id = "pt_2",
            promptAr = "السؤال 2 (المستوى التأسيسي A1):",
            englishPrompt = "She ___ coffee every morning before work.",
            options = listOf("drink", "drinks", "drinking", "drank every"),
            correctIndex = 1,
            explanationAr = "العادة اليومية مع She تأخذ المضارع البسيط مضافاً له s.",
            level = CefrLevel.A1
        ),
        QuizQuestion(
            id = "pt_3",
            promptAr = "السؤال 3 (المستوى التوسعي A2):",
            englishPrompt = "Did you ___ your ticket online last night?",
            options = listOf("booked", "booking", "book", "books"),
            correctIndex = 2,
            explanationAr = "بعد Did يأتي الفعل في المصدر المجرد book.",
            level = CefrLevel.A2
        ),
        QuizQuestion(
            id = "pt_4",
            promptAr = "السؤال 4 (المستوى المتوسط B1):",
            englishPrompt = "I have lived in Alexandria ___ five years.",
            options = listOf("since", "for", "ago", "during"),
            correctIndex = 1,
            explanationAr = "للمدة المحسوبة (five years) نستخدم for.",
            level = CefrLevel.B1
        ),
        QuizQuestion(
            id = "pt_5",
            promptAr = "السؤال 5 (المستوى المهني B2):",
            englishPrompt = "We had to ___ the launch until the bugs were fixed.",
            options = listOf("put off", "take on", "give in", "look after"),
            correctIndex = 0,
            explanationAr = "put off تعني يؤجل.",
            level = CefrLevel.B2
        ),
        QuizQuestion(
            id = "pt_6",
            promptAr = "السؤال 6 (المستوى المتقدم C1–C2):",
            englishPrompt = "Not only ___ the deadline, but she also exceeded expectations.",
            options = listOf("she met", "did she meet", "she did meet", "met she"),
            correctIndex = 1,
            explanationAr = "تبدأ الجملة بـ Not only للتوكيد فنقدم الفعل المساعد did قبل الفاعل.",
            level = CefrLevel.C1
        )
    )

    fun calculateCefrLevel(correctCount: Int): CefrLevel = when {
        correctCount <= 1 -> CefrLevel.A1
        correctCount == 2 -> CefrLevel.A2
        correctCount == 3 -> CefrLevel.B1
        correctCount == 4 -> CefrLevel.B2
        correctCount == 5 -> CefrLevel.C1
        else -> CefrLevel.C2
    }
}
