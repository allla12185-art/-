package com.example.data

import com.example.model.CefrLevel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class DailyWordSeed(
    val wordEn: String,
    val ipa: String,
    val partOfSpeechAr: String,
    val meaningAr: String,
    val exampleEn: String,
    val exampleAr: String,
    val levelCode: String
)

object DailyWordsCatalog {
    val entries: List<DailyWordSeed> = listOf(
        DailyWordSeed(
            wordEn = "Resilient",
            ipa = "/rɪˈzɪljənt/",
            partOfSpeechAr = "صفة",
            meaningAr = "مرن / قادر على التكيّف والنهوض",
            exampleEn = "Consistent learners stay resilient even on busy days.",
            exampleAr = "المتعلّمون المواظبون يبقون مرنين حتى في الأيام المزدحمة.",
            levelCode = CefrLevel.B1.code
        ),
        DailyWordSeed(
            wordEn = "Curious",
            ipa = "/ˈkjʊriəs/",
            partOfSpeechAr = "صفة",
            meaningAr = "فضولي / شغوف بالمعرفة",
            exampleEn = "Stay curious and ask one new question in English every day.",
            exampleAr = "ابقَ شغوفاً بالمعرفة واسأل سؤالاً جديداً بالإنجليزية كل يوم.",
            levelCode = CefrLevel.A2.code
        ),
        DailyWordSeed(
            wordEn = "Confident",
            ipa = "/ˈkɑːnfɪdənt/",
            partOfSpeechAr = "صفة",
            meaningAr = "واثق من نفسه",
            exampleEn = "Speaking aloud for five minutes makes you more confident.",
            exampleAr = "التحدث بصوت مسموع لخمس دقائق يجعلك أكثر ثقة.",
            levelCode = CefrLevel.A2.code
        ),
        DailyWordSeed(
            wordEn = "Momentum",
            ipa = "/moʊˈmentəm/",
            partOfSpeechAr = "اسم",
            meaningAr = "زخم / قوة اندفاع للاستمرار",
            exampleEn = "Completing one short lesson builds strong daily momentum.",
            exampleAr = "إكمال درس واحد قصير يبني زخماً يومياً قوياً.",
            levelCode = CefrLevel.B2.code
        ),
        DailyWordSeed(
            wordEn = "Grateful",
            ipa = "/ˈɡreɪtfəl/",
            partOfSpeechAr = "صفة",
            meaningAr = "ممتن / شاكر",
            exampleEn = "I am grateful for every small step I take forward.",
            exampleAr = "أنا ممتن لكل خطوة صغيرة أخطوها نحو الأمام.",
            levelCode = CefrLevel.A1.code
        ),
        DailyWordSeed(
            wordEn = "Articulate",
            ipa = "/ɑːrˈtɪkjələt/",
            partOfSpeechAr = "صفة",
            meaningAr = "فصيح / واضح التعبير",
            exampleEn = "Reading aloud helps you become an articulate speaker.",
            exampleAr = "القراءة بصوت عالٍ تساعدك على أن تصبح متحدثاً واضح التعبير.",
            levelCode = CefrLevel.C1.code
        ),
        DailyWordSeed(
            wordEn = "Breakthrough",
            ipa = "/ˈbreɪkθruː/",
            partOfSpeechAr = "اسم",
            meaningAr = "انطلاقة نوعية / إنجاز مفصلي",
            exampleEn = "Understanding native speech felt like a real breakthrough.",
            exampleAr = "فهم حديث الناطقين الأصليين بدا وكأنه انطلاقة حقيقية.",
            levelCode = CefrLevel.B2.code
        ),
        DailyWordSeed(
            wordEn = "Inspire",
            ipa = "/ɪnˈspaɪər/",
            partOfSpeechAr = "فعل",
            meaningAr = "يُلهم / يشجّع",
            exampleEn = "Great teachers inspire students to believe in themselves.",
            exampleAr = "المعلمون الرائعون يُلهمون الطلاب ليؤمنوا بأنفسهم.",
            levelCode = CefrLevel.B1.code
        ),
        DailyWordSeed(
            wordEn = "Habit",
            ipa = "/ˈhæbɪt/",
            partOfSpeechAr = "اسم",
            meaningAr = "عادة يومية",
            exampleEn = "Listening to English in the morning is a healthy habit.",
            exampleAr = "الاستماع للإنجليزية في الصباح عادة صحية مفيدة.",
            levelCode = CefrLevel.A1.code
        ),
        DailyWordSeed(
            wordEn = "Fluency",
            ipa = "/ˈfluːənsi/",
            partOfSpeechAr = "اسم",
            meaningAr = "طلاقة لغوية",
            exampleEn = "Fluency grows naturally when you practice a little each day.",
            exampleAr = "تنموا الطلاقة بشكل طبيعي عندما تتدرّب قليلاً كل يوم.",
            levelCode = CefrLevel.B1.code
        ),
        DailyWordSeed(
            wordEn = "Eloquent",
            ipa = "/ˈeləkwənt/",
            partOfSpeechAr = "صفة",
            meaningAr = "بليغ / ساحر البيان",
            exampleEn = "She wrote an eloquent summary of the entire story.",
            exampleAr = "كتبت ملخصاً بليغاً للقصة بأكملها.",
            levelCode = CefrLevel.C1.code
        ),
        DailyWordSeed(
            wordEn = "Serendipity",
            ipa = "/ˌserənˈdɪpəti/",
            partOfSpeechAr = "اسم",
            meaningAr = "صدفة سعيدة غير متوقعة",
            exampleEn = "Finding a great study partner was pure serendipity.",
            exampleAr = "العثور على شريك دراسة رائع كان صدفة سعيدة بحتة.",
            levelCode = CefrLevel.C2.code
        )
    )

    fun todayDateKey(): String {
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        return formatter.format(Date())
    }

    fun wordForToday(preferredLevel: CefrLevel? = null): DailyWordSeed {
        val dayOfYear = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
        val pool = if (preferredLevel != null) {
            entries.filter { it.levelCode == preferredLevel.code }.ifEmpty { entries }
        } else {
            entries
        }
        return pool[dayOfYear % pool.size]
    }

    fun nextWordAfter(currentWordEn: String): DailyWordSeed {
        val currentIdx = entries.indexOfFirst { it.wordEn.equals(currentWordEn, ignoreCase = true) }
        val nextIdx = if (currentIdx == -1) 0 else (currentIdx + 1) % entries.size
        return entries[nextIdx]
    }
}
