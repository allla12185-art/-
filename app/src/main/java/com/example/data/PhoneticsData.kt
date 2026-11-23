package com.example.data

import com.example.model.MinimalPair
import com.example.model.PhoneticCategory
import com.example.model.PhoneticExample
import com.example.model.PhoneticSound

object PhoneticsData {
    val sounds: List<PhoneticSound> = listOf(
        PhoneticSound(
            id = "ph_schwa",
            symbol = "/ə/",
            nameEn = "The Schwa Sound",
            nameAr = "صوت الـ Schwa الخفيف (أهم صوت أمريكي)",
            category = PhoneticCategory.VOWELS_SCHWA,
            howToPronounceAr = "هو أكثر صوت شائع في الإنجليزية الأمريكية! صوت قصير جداً ومرتخٍ يخرج من منتصف الفم دون شد الشفتين (أشبه بفتحة خفيفة سريعة) في المقاطع غير المنبورة.",
            commonMistakeAr = "إشباع الحرف المتحرك ونطقه واضحاً ثقيلاً مثل قول (با-نا-نا) بدلاً من (بَـنا-نَـ).",
            examples = listOf(
                PhoneticExample("about", "/əˈbaʊt/", "أباوت (همزة خفيفة سريعة)", "Tell me about your plan.", "أخبرني عن خطتك."),
                PhoneticExample("banana", "/bəˈnænə/", "بَـنَـانَـه", "I had a banana for breakfast.", "تناولت موزة على الفطور."),
                PhoneticExample("support", "/səˈpɔːrt/", "سَـبورت", "Thank you for your support.", "شكراً لدعمك.")
            )
        ),
        PhoneticSound(
            id = "ph_short_long_i",
            symbol = "/ɪ/ vs /iː/",
            nameEn = "Short I vs Long EE",
            nameAr = "الفرق بين الكسرة الخاطفة والمد الطويل",
            category = PhoneticCategory.VOWELS_SCHWA,
            howToPronounceAr = "في /iː/ (مثل sheep) تبتسم الشفتان ويمتد الصوت. أما في /ɪ/ (مثل ship) فالفم مسترخٍ والصوت خاطف وقريب من فتحة مكسورة.",
            commonMistakeAr = "نطق sit مثل seat أو live مثل leave مما يغيّر المعنى كلياً.",
            examples = listOf(
                PhoneticExample("ship / sheep", "/ʃɪp/ — /ʃiːp/", "شِب (خاطفة) / شيب (ممدودة)", "The sheep is on the ship.", "الخروف على السفينة."),
                PhoneticExample("live / leave", "/lɪv/ — /liːv/", "لِف / ليف", "I live here, I don't want to leave.", "أعيش هنا ولا أريد المغادرة.")
            )
        ),
        PhoneticSound(
            id = "ph_th",
            symbol = "/θ/ & /ð/",
            nameEn = "Voiceless & Voiced TH",
            nameAr = "صوت الثاء والذال (TH)",
            category = PhoneticCategory.HARD_CONSONANTS,
            howToPronounceAr = "ضع طرف اللسان برفق بين الأسنان الأمامية: مع خروج هواء بدون اهتزاز الحنجرة ينتج /θ/ (ثاء مثل think)، ومع اهتزاز الحنجرة ينتج /ð/ (ذال مثل this).",
            commonMistakeAr = "تحويل الثاء إلى س/ت (sink بدلاً من think) أو الذال إلى ز/د (zis بدلاً من this).",
            examples = listOf(
                PhoneticExample("think", "/θɪŋk/", "ثينك (طرف اللسان)", "I think this is great.", "أعتقد أن هذا رائع."),
                PhoneticExample("three", "/θriː/", "ثري", "There are three books here.", "توجد ثلاثة كتب هنا."),
                PhoneticExample("weather", "/ˈweðər/", "ويذَر", "The weather is nice today.", "الطقس جميل اليوم.")
            )
        ),
        PhoneticSound(
            id = "ph_r_flap",
            symbol = "/ɹ/ & Flap T /ɾ/",
            nameEn = "American R & Flap T",
            nameAr = "الراء الأمريكية و الـ Flap T",
            category = PhoneticCategory.FLAP_LINKING,
            howToPronounceAr = "الراء الأمريكية لا تلامس سقف الحلق بل يلتف اللسان للخلف قليلاً. أما Flap T فتحدث عندما تقع الـ t بين صوتين متحركين فتُنطق كدال خفيفة سريعة جداً!",
            commonMistakeAr = "تكرار حرف الراء كما في العربية، أو نطق water بتاء ثقيلة متوقفة في الكلام السريع.",
            examples = listOf(
                PhoneticExample("water", "/ˈwɔːɾər/", "وورَر / وودَر خفيفة", "Can I have a glass of water?", "هل يمكنني كوب ماء؟"),
                PhoneticExample("better", "/ˈbeɾər/", "بيدَر خفيفة", "Practice makes you better every day.", "الممارسة تجعلك أفضل كل يوم."),
                PhoneticExample("really", "/ˈɹiːəli/", "ريلي (لسان ملفوف للخلف)", "I really appreciate your help.", "أقدّر مساعدتك حقاً.")
            )
        ),
        PhoneticSound(
            id = "ph_pb_vw",
            symbol = "/p/ vs /b/ • /v/ vs /w/",
            nameEn = "Popping P & Biting V",
            nameAr = "الـ P الانفجارية والـ V الملامسة للشفة",
            category = PhoneticCategory.HARD_CONSONANTS,
            howToPronounceAr = "حرف P يخرج معه دفعة هواء قوية (ضع ورقة أمام فمك لتتحرك!). وحرف V يتطلب ملامسة الأسنان العليا للشفة السفلى مع اهتزاز، بينما W يضم الشفتين كالدائرة.",
            commonMistakeAr = "نطق park مثل bark أو very مثل wery.",
            examples = listOf(
                PhoneticExample("paper", "/ˈpeɪpər/", "بيبر بهواء قوي", "Write it on a piece of paper.", "اكتبها على ورقة."),
                PhoneticExample("very well", "/ˈveri wel/", "ڤيري وِل", "She speaks very well.", "هي تتحدث بشكل ممتاز.")
            )
        ),
        PhoneticSound(
            id = "ph_stress_intonation",
            symbol = "ˈStress & ↗↘",
            nameEn = "Word Stress & Intonation",
            nameAr = "نبرة المقطع وتلوين السؤال",
            category = PhoneticCategory.STRESS_INTONATION,
            howToPronounceAr = "في الإنجليزية مقطع واحد من الكلمة يُنطق أعلى وأوضح (PRE-sent = هدية، pre-SENT = يقدّم). وأسئلة Yes/No يرتفع صوتها في النهاية ↗ بينما أسئلة Wh- تهبط ↘.",
            commonMistakeAr = "نطق جميع مقاطع الكلمة بنفس القوة، فيفقد السامع تمييز الكلمة.",
            examples = listOf(
                PhoneticExample("record", "/ˈrekərd/ vs /rɪˈkɔːrd/", "رِيكورد (اسم) / ريكورد (فعل)", "We will record a new record.", "سنسجل رقماً قياسياً جديداً."),
                PhoneticExample("Are you ready? ↗", "صعود في النهاية", "نغمة تصاعدية", "Are you ready to start?", "هل أنت مستعد للبدء؟")
            )
        ),
        PhoneticSound(
            id = "ph_reductions",
            symbol = "Gonna / Wanna",
            nameEn = "Natural Reductions & Linking",
            nameAr = "الاختصارات والربط في الحديث السريع",
            category = PhoneticCategory.REDUCTIONS,
            howToPronounceAr = "في الكلام الطبيعي المتصل تُدمج الكلمات: going to تصبح gonna، و want to تصبح wanna، و got to تصبح gotta، ونربط آخر ساكن بأول متحرك (turn_it_off).",
            commonMistakeAr = "ملاحظة: نستخدم هذه الاختصارات في السماع والكلام الودي، لكن في الكتابة الرسمية نكتبها كاملة (going to / want to).",
            examples = listOf(
                PhoneticExample("going to -> gonna", "/ˈɡʌnə/", "جَنا", "I'm gonna practice for ten minutes.", "سأتدرب لعشر دقائق."),
                PhoneticExample("want to -> wanna", "/ˈwɑːnə/", "وانا", "Do you wanna grab some coffee?", "هل تريد أن نشرب قهوة؟"),
                PhoneticExample("turn it off", "/tɜːrnɪˈtɔːf/", "تيرن إت أوف متصلة", "Please turn it off before sleeping.", "من فضلك أطفئه قبل النوم.")
            )
        )
    )

    val minimalPairs: List<MinimalPair> = listOf(
        MinimalPair(
            id = "mp_ship_sheep",
            soundA = "/ɪ/",
            soundB = "/iː/",
            wordA = "ship",
            ipaA = "/ʃɪp/",
            meaningA = "سفينة",
            wordB = "sheep",
            ipaB = "/ʃiːp/",
            meaningB = "خروف",
            tipAr = "اجعل ship قصيرة وخاطفة، وابتسم مع إطالة sheep."
        ),
        MinimalPair(
            id = "mp_pat_bat",
            soundA = "/p/",
            soundB = "/b/",
            wordA = "pat",
            ipaA = "/pæt/",
            meaningA = "يربّت",
            wordB = "bat",
            ipaB = "/bæt/",
            meaningB = "خفاش / مضرب",
            tipAr = "pat تخرج بدفعة هواء قوية، بينما bat تهتز في الحنجرة."
        ),
        MinimalPair(
            id = "mp_think_sink",
            soundA = "/θ/",
            soundB = "/s/",
            wordA = "think",
            ipaA = "/θɪŋk/",
            meaningA = "يفكر",
            wordB = "sink",
            ipaB = "/sɪŋk/",
            meaningB = "يغرق / حوض",
            tipAr = "لإخراج think يجب أن يلامس طرف لسانك الأسنان العليا."
        ),
        MinimalPair(
            id = "mp_van_fan",
            soundA = "/v/",
            soundB = "/f/",
            wordA = "van",
            ipaA = "/væn/",
            meaningA = "شاحنة صغيرة",
            wordB = "fan",
            ipaB = "/fæn/",
            meaningB = "مروحة / مشجع",
            tipAr = "المس الحنجرة بأصابعك: في van ستشعر باهتزاز واضح."
        ),
        MinimalPair(
            id = "mp_pen_pan",
            soundA = "/e/",
            soundB = "/æ/",
            wordA = "pen",
            ipaA = "/pen/",
            meaningA = "قلم",
            wordB = "pan",
            ipaB = "/pæn/",
            meaningB = "مقلاة",
            tipAr = "في pan افتح الفم أوسع للأسفل مع إمالة الصوت للأمام."
        )
    )
}
