package com.example.data

import com.example.model.CefrLevel
import com.example.model.DialogueLine
import com.example.model.GrammarPoint
import com.example.model.Lesson
import com.example.model.MindMapBranch
import com.example.model.QuizQuestion
import com.example.model.SentenceItem
import com.example.model.StorySegment

object LessonsDataA1A2 {
    val lessons: List<Lesson> = listOf(
        Lesson(
            id = "a1_greetings_be",
            titleAr = "التعارف وفعل الكينونة",
            titleEn = "Greetings & Verb to Be",
            level = CefrLevel.A1,
            unitAr = "الوحدة 1: من الصفر إلى البداية",
            durationMinutes = 12,
            summaryAr = "تعلّم كيف تقدّم نفسك بثقة باستخدام (am / is / are) وكيف تسأل عن الحال والعمل بأسلوب طبيعي.",
            goldenTipAr = "في الإنجليزية لا نقول «I teacher» كما في العربية «أنا معلم»؛ لا بد دائماً من وجود فعل الكينونة: I am a teacher.",
            branch = MindMapBranch.SPEAKING,
            grammarPoints = listOf(
                GrammarPoint(
                    titleAr = "فعل الكينونة في المضارع",
                    titleEn = "Verb to Be (am / is / are)",
                    simpleRuleAr = "نستخدم am مع I، و is مع المفرد (he, she, it)، و are مع الجمع و you. في الحديث اليومي نختصرها غالباً: I'm, He's, They're.",
                    formula = "Subject + am / is / are + Noun / Adjective",
                    commonArabMistakeAr = "حذف الفعل المساعد عند وصف الاسم أو الصفة مثل قول (He happy) والصحيح (He is happy).",
                    examples = listOf(
                        SentenceItem("g_be_1", "I am from Cairo, and I am a student.", "آي آم فروم كايرو، آند آي آم أ ستودنت", "أنا من القاهرة، وأنا طالب.", "التعريف"),
                        SentenceItem("g_be_2", "She is a smart doctor.", "شي إز أ سمارت دكتور", "هي طبيبة ذكية.", "الوصف"),
                        SentenceItem("g_be_3", "Are you ready for the lesson?", "آر يو ريدي فور ذا ليسون؟", "هل أنت مستعد للدرس؟", "السؤال")
                    )
                )
            ),
            dialogue = listOf(
                DialogueLine("Peggy", "Hello! I'm Peggy. What's your name?", "هالو! آيم بيجي. واتس يور نيم؟", "مرحباً! أنا بيجي. ما اسمك؟"),
                DialogueLine("Ali", "Hi Peggy, I'm Ali. Nice to meet you!", "هاي بيجي، آيم علي. نايس تو ميت يو!", "أهلاً بيجي، أنا علي. تشرفت بلقائك!"),
                DialogueLine("Peggy", "Nice to meet you too. What do you do?", "نايس تو ميت يو تو. وات دو يو دو؟", "تشرفت بك أيضاً. ماذا تعمل؟"),
                DialogueLine("Ali", "I'm a data analyst. How about you?", "آيم أ ديتا أناليست. هاو أباوت يو؟", "أنا محلل بيانات. وماذا عنك؟"),
                DialogueLine("Peggy", "I'm an English coach here.", "آيم آن إنجلش كوتش هير.", "أنا مدربة لغة إنجليزية هنا.")
            ),
            sentences = listOf(
                SentenceItem("a1_s1", "Good morning! How is everything?", "جود مورنينج! هاو إز إيفريثينج؟", "صباح الخير! كيف تسير الأمور؟", "التحيات"),
                SentenceItem("a1_s2", "I'm doing great, thank you.", "آيم دوينج جريت، ثانك يو.", "أنا بخير جداً، شكراً لك.", "التحيات"),
                SentenceItem("a1_s3", "Where are you from?", "وير آر يو فروم؟", "من أين أنت؟", "التعارف"),
                SentenceItem("a1_s4", "Could you speak a little slower, please?", "كود يو سبيك أ ليتل سلوَر، بليز؟", "هل يمكنك التحدث ببطء قليلاً من فضلك؟", "المحادثة")
            ),
            story = StorySegment(
                titleEn = "Ali's First Morning",
                titleAr = "صباح علي الأول",
                paragraphs = listOf(
                    "Ali wakes up early in a new city. Everything looks exciting and fresh." to "يستيقظ علي باكراً في مدينة جديدة. كل شيء يبدو مشوقاً ومنعشاً.",
                    "At the coffee shop, he smiles and says: 'Good morning! I'm Ali. One black coffee, please.'" to "في المقهى، يبتسم ويقول: «صباح الخير! أنا علي. قهوة سوداء واحدة من فضلك.»",
                    "The barista replies warmly: 'Welcome, Ali! Have a wonderful day!'" to "يرد الباريستا بود: «أهلاً بك يا علي! أتمنى لك يوماً رائعاً!»"
                ),
                moralOrTipAr = "الابتسامة مع ثلاث عبارات بسيطة (Good morning / Please / Thank you) تفتح لك أي حوار من اليوم الأول."
            ),
            quiz = listOf(
                QuizQuestion(
                    id = "q_a1_1",
                    promptAr = "اختر الجملة الصحيحة لوصف صديقك:",
                    englishPrompt = "___ a kind engineer.",
                    options = listOf("He is", "He are", "He", "He am"),
                    correctIndex = 0,
                    explanationAr = "مع الضمير المفرد المذكر He نستخدم دائماً is.",
                    level = CefrLevel.A1
                ),
                QuizQuestion(
                    id = "q_a1_2",
                    promptAr = "ماذا تعني عبارة «What do you do?»؟",
                    englishPrompt = "What do you do?",
                    options = listOf("ماذا تأكل؟", "ماذا تعمل؟", "أين تسكن؟", "كم عمرك؟"),
                    correctIndex = 1,
                    explanationAr = "عبارة What do you do? هي الصيغة الأشهر للسؤال عن الوظيفة.",
                    level = CefrLevel.A1
                )
            )
        ),
        Lesson(
            id = "a1_daily_routine",
            titleAr = "الروتين اليومي والمضارع البسيط",
            titleEn = "Daily Routine & Present Simple",
            level = CefrLevel.A1,
            unitAr = "الوحدة 2: يومي خطوة بخطوة",
            durationMinutes = 14,
            summaryAr = "تحدّث عن عاداتك اليومية وأوقاتك باستخدام المضارع البسيط مع قاعدة s للمفرد الغائب.",
            goldenTipAr = "تذكّر حرف الـ (s) مع He / She / It في المضارع البسيط: She drinks coffee، لكن بدونها مع I / You / We / They.",
            branch = MindMapBranch.GRAMMAR,
            grammarPoints = listOf(
                GrammarPoint(
                    titleAr = "المضارع البسيط للعادات",
                    titleEn = "Present Simple Tense",
                    simpleRuleAr = "يُستخدم للحديث عن العادات المتكررة والحقائق. نضيف s للفعل مع He / She / It، وفي النفي نستخدم don't أو doesn't.",
                    formula = "I/We/They + Verb  |  He/She/It + Verb + s",
                    commonArabMistakeAr = "نسيان s مع المفرد مثل (She work every day) أو وضع s بعد doesn't مثل (He doesn't works).",
                    examples = listOf(
                        SentenceItem("g_ps_1", "I wake up at six thirty every morning.", "آي ويك أب آت سيكس ثيرتي إيفري مورنينج", "أستيقظ الساعة السادسة والنصف كل صباح.", "الروتين"),
                        SentenceItem("g_ps_2", "Lisa studies English for twenty minutes.", "ليزا ستاديز إنجلش فور توينتي مينيتس", "ليزا تدرس الإنجليزية لمدة عشرين دقيقة.", "الروتين"),
                        SentenceItem("g_ps_3", "He doesn't drink tea at night.", "هي دازنت درينك تي آت نايت", "هو لا يشرب الشاي ليلاً.", "النفي")
                    )
                )
            ),
            dialogue = listOf(
                DialogueLine("Tom", "What time do you usually start work, Lisa?", "وات تايم دو يو يوجوالي ستارت وورك، ليزا؟", "في أي وقت تبدئين العمل عادةً يا ليزا؟"),
                DialogueLine("Lisa", "I start at nine, so I leave home at eight.", "آي ستارت آت ناين، سو آي ليف هوم آت إيت.", "أبدأ في التاسعة، لذا أغادر المنزل في الثامنة."),
                DialogueLine("Tom", "Do you study English after work?", "دو يو ستادي إنجلش آفتر وورك؟", "هل تدرسين الإنجليزية بعد العمل؟"),
                DialogueLine("Lisa", "Yes, every evening for twenty-five minutes!", "يس، إيفري إيفنينج فور توينتي فايف مينيتس!", "نعم، كل مساء لمدة ٢٥ دقيقة!")
            ),
            sentences = listOf(
                SentenceItem("a1_r1", "I brush my teeth and make breakfast.", "آي برَش ماي تيث آند ميك بريكفاست", "أفرّش أسناني وأحضّر الفطور.", "الروتين"),
                SentenceItem("a1_r2", "What time is it right now?", "وات تايم إز إت رايت ناو؟", "كم الساعة الآن؟", "الوقت"),
                SentenceItem("a1_r3", "It's a quarter past seven.", "إتس أ كوارتر باست سيفن", "الساعة السابعة والربع.", "الوقت"),
                SentenceItem("a1_r4", "I usually go to bed at eleven.", "آي يوجوالي جو تو بيد آت إليفن", "عادةً أذهب للنوم الساعة الحادية عشرة.", "الروتين")
            ),
            story = StorySegment(
                titleEn = "Twenty Minutes That Changed Everything",
                titleAr = "عشرون دقيقة غيّرت كل شيء",
                paragraphs = listOf(
                    "Tom used to study three hours on Friday, then quit for two weeks." to "اعتاد توم أن يذاكر ثلاث ساعات يوم الجمعة، ثم يتوقف لأسبوعين.",
                    "His mentor told him: 'Twenty focused minutes daily beats three hours once a month.'" to "قال له مرشده: «عشرون دقيقة مركزة يومياً تتفوق على ثلاث ساعات مرة في الشهر.»",
                    "After sixty days of small daily steps, Tom spoke clearly and without fear." to "بعد ستين يوماً من الخطوات اليومية الصغيرة، أصبح توم يتحدث بوضوح ودون خوف."
                ),
                moralOrTipAr = "الاستمرارية اليومية القصيرة أهم قاعدة في اكتساب اللغة."
            ),
            quiz = listOf(
                QuizQuestion(
                    id = "q_a1_r1",
                    promptAr = "أكمل الجملة بشكل صحيح:",
                    englishPrompt = "My sister ___ to work by bus.",
                    options = listOf("go", "goes", "going", "is go"),
                    correctIndex = 1,
                    explanationAr = "My sister مفرد غائب (She)، لذلك نضيف es للفعل go ليصبح goes.",
                    level = CefrLevel.A1
                ),
                QuizQuestion(
                    id = "q_a1_r2",
                    promptAr = "اختر النفي الصحيح:",
                    englishPrompt = "He ___ like spicy food.",
                    options = listOf("don't", "doesn't", "isn't", "no"),
                    correctIndex = 1,
                    explanationAr = "مع He نستخدم doesn't ثم الفعل في المصدر without s.",
                    level = CefrLevel.A1
                )
            )
        ),
        Lesson(
            id = "a2_past_travel",
            titleAr = "الماضي البسيط ومواقف السفر",
            titleEn = "Past Simple & Travel Stories",
            level = CefrLevel.A2,
            unitAr = "الوحدة 3: السفر والقصص",
            durationMinutes = 15,
            summaryAr = "تعلّم سرد ما حدث بالأمس باستخدام الأفعال المنتظمة والشاذة، وعبارات المطار والفندق.",
            goldenTipAr = "الأفعال الشاذة الأكثر استخداماً (went, saw, bought, took, had) احفظها داخل جمل قصيرة وليس كقائمة جامدة.",
            branch = MindMapBranch.STORY,
            grammarPoints = listOf(
                GrammarPoint(
                    titleAr = "زمن الماضي البسيط",
                    titleEn = "Past Simple (Regular & Irregular)",
                    simpleRuleAr = "يُستخدم لأفعال بدأت وانتهت في الماضي. في السؤال والنفي نستخدم did / didn't ويرجع الفعل لأصله.",
                    formula = "Subject + Verb-ed / Past Form  |  Did + Subject + Base Verb?",
                    commonArabMistakeAr = "ترك الفعل في الماضي بعد didn't مثل (I didn't went) والصحيح (I didn't go).",
                    examples = listOf(
                        SentenceItem("g_pa_1", "We arrived at the airport early.", "وي أرايفد آت ذا إيربورت إيرلي", "وصلنا إلى المطار مبكراً.", "السفر"),
                        SentenceItem("g_pa_2", "I didn't forget my passport.", "آي ديدنت فورجيت ماي باسبورت", "لم أنسَ جواز سفري.", "السفر"),
                        SentenceItem("g_pa_3", "Did you enjoy the trip?", "ديد يو إنجوي ذا تريب؟", "هل استمتعت بالرحلة؟", "السؤال")
                    )
                )
            ),
            dialogue = listOf(
                DialogueLine("Isabelle", "Welcome back, Ali! How was your flight?", "ويلكم باك، علي! هاو واز يور فلايت؟", "أهلاً بعودتك يا علي! كيف كانت رحلتك؟"),
                DialogueLine("Ali", "It was smooth, thanks! I got a window seat.", "إت واز سموذ، ثانكس! آي جوت أ ويندو سيت.", "كانت مريحة، شكراً! حصلت على مقعد بجانب النافذة."),
                DialogueLine("Isabelle", "Where did you stay in London?", "وير ديد يو ستاي إن لندن؟", "أين أقمت في لندن؟"),
                DialogueLine("Ali", "I stayed at a quiet hotel near the park.", "آي ستايد آت أ كوايت هوتيل نير ذا بارك.", "أقمت في فندق هادئ قرب الحديقة.")
            ),
            sentences = listOf(
                SentenceItem("a2_t1", "Where is the boarding gate, please?", "وير إز ذا بوردينج جيت، بليز؟", "أين بوابة الصعود للطائرة من فضلك؟", "المطار"),
                SentenceItem("a2_t2", "I have a reservation under the name Ali.", "آي هاف أ ريزيرفيشن أندر ذا نيم علي", "لدي حجز باسم علي.", "الفندق"),
                SentenceItem("a2_t3", "How much does a round-trip ticket cost?", "هاو ماتش داز أ راوند تريب تيكت كوست؟", "كم تكلف تذكرة الذهاب والعودة؟", "التذاكر"),
                SentenceItem("a2_t4", "Could you recommend a good local restaurant?", "كود يو ريكوميند أ جود لوكال ريستورانت؟", "هل يمكنك ترشيح مطعم محلي جيد؟", "المطعم")
            ),
            story = StorySegment(
                titleEn = "The Lost Suitcase",
                titleAr = "الحقيبة المفقودة",
                paragraphs = listOf(
                    "Last summer, Nora flew to Madrid. When she reached baggage claim, her blue suitcase was not there." to "في الصيف الماضي، سافرت نورا إلى مدريد. عندما وصلت لاستلام الأمتعة، لم تكن حقيبتها الزرقاء هناك.",
                    "Instead of panicking, she walked calmly to the help desk and explained what happened." to "بدلاً من الذعر، توجهت بهدوء إلى مكتب المساعدة وشرحت ما حدث.",
                    "Two hours later, the airline found her bag and delivered it straight to her hotel." to "بعد ساعتين، وجدت شركة الطيران حقيبتها وأوصلتها مباشرة إلى فندقها."
                ),
                moralOrTipAr = "حفظ 5 جمل هادئة للمواقف الطارئة في السفر يمنحك ثقة هائلة وقت الأزمات."
            ),
            quiz = listOf(
                QuizQuestion(
                    id = "q_a2_1",
                    promptAr = "اختر الصيغة الصحيحة في الماضي:",
                    englishPrompt = "She ___ a great photo yesterday.",
                    options = listOf("take", "took", "taked", "is taking"),
                    correctIndex = 1,
                    explanationAr = "الفعل take فعل شاذ وماضيه هو took.",
                    level = CefrLevel.A2
                ),
                QuizQuestion(
                    id = "q_a2_2",
                    promptAr = "صحّح السؤال التالي:",
                    englishPrompt = "Did he ___ the hotel reservation?",
                    options = listOf("confirmed", "confirming", "confirm", "confirms"),
                    correctIndex = 2,
                    explanationAr = "بعد Did يأتي الفعل دائماً في صورته الأساسية (Base Form): confirm.",
                    level = CefrLevel.A2
                )
            )
        ),
        Lesson(
            id = "a2_food_shopping",
            titleAr = "التسوق والمطاعم والكميات",
            titleEn = "Food, Shopping & Quantities",
            level = CefrLevel.A2,
            unitAr = "الوحدة 4: مواقف الحياة اليومية",
            durationMinutes = 13,
            summaryAr = "أتقن الفرق بين المعدود وغير المعدود (much / many)، واستخدم عبارات الطلب المهذب في المتاجر والمطاعم.",
            goldenTipAr = "استبدل «I want» بـ «I'd like» أو «Could I have» لتبدو أكثر لباقة واحترافية فوراً.",
            branch = MindMapBranch.VOCAB,
            grammarPoints = listOf(
                GrammarPoint(
                    titleAr = "المعدود وغير المعدود",
                    titleEn = "Countable vs Uncountable (some / any / much / many)",
                    simpleRuleAr = "نستخدم many مع الأشياء المعدودة (apples, books) و much مع غير المعدود (water, money, time). وتعمل a lot of مع الاثنين.",
                    formula = "How many + Plural Noun  |  How much + Uncountable Noun",
                    commonArabMistakeAr = "جمع الكلمات غير المعدودة مثل (informations, advices)؛ في الإنجليزية هي مفرد دائماً: information, advice.",
                    examples = listOf(
                        SentenceItem("g_cnt_1", "I'd like some fresh advice about this course.", "آيد لايك صم فريش أدفايس أباوت ذيس كورس", "أود بعض النصائح الجديدة حول هذه الدورة.", "النصائح"),
                        SentenceItem("g_cnt_2", "How much is this jacket?", "هاو ماتش إز ذيس جاكيت؟", "كم سعر هذا المعطف؟", "التسوق")
                    )
                )
            ),
            dialogue = listOf(
                DialogueLine("Waiter", "Are you ready to order?", "آر يو ريدي تو أوردر؟", "هل أنت مستعد للطلب؟"),
                DialogueLine("Sara", "Yes, please. I'd like grilled chicken with a side salad.", "يس، بليز. آيد لايك جريلد تشيكن وذ أ سايد سالاد.", "نعم من فضلك. أود دجاجاً مشوياً مع سلطة جانبية."),
                DialogueLine("Waiter", "Anything to drink?", "إنيثينج تو درينك؟", "أي شيء للشرب؟"),
                DialogueLine("Sara", "Just sparkling water with lemon, thanks.", "جاست سباركلينج ووتر وذ ليمون، ثانكس.", "فقط مياه غازية مع الليمون، شكراً.")
            ),
            sentences = listOf(
                SentenceItem("a2_f1", "Can I try this on in a medium size?", "كان آي تراي ذيس أون إن أ ميديم سايز؟", "هل يمكنني قياس هذا بمقاس متوسط؟", "التسوق"),
                SentenceItem("a2_f2", "Could we have the bill, please?", "كود وي هاف ذا بيل، بليز؟", "هل يمكننا الحصول على الفاتورة من فضلك؟", "المطعم"),
                SentenceItem("a2_f3", "Do you accept credit cards?", "دو يو أكسبت كريدت كاردز؟", "هل تقبلون البطاقات الائتمانية؟", "الدفع")
            ),
            story = StorySegment(
                titleEn = "The Polite Diner",
                titleAr = "الزبون اللبق",
                paragraphs = listOf(
                    "Karim used to point at menus and say: 'Give me this.'" to "اعتاد كريم أن يشير إلى القائمة ويقول: «أعطني هذا.»",
                    "When he learned 'Could I have...' and 'I'd like...', waiters began smiling and chatting with him warmly." to "عندما تعلّم عبارة «هل يمكنني الحصول على...» و«أود أن...»، بدأ النادلون يبتسمون ويتحدثون معه بود."
                ),
                moralOrTipAr = "اللباقة في الإنجليزية تعتمد على كلمات قصيرة مثل Could / Would / Please."
            ),
            quiz = listOf(
                QuizQuestion(
                    id = "q_a2_f1",
                    promptAr = "اختر الجملة الصحيحة نحوياً:",
                    englishPrompt = "She gave me great ___.",
                    options = listOf("advices", "an advice", "advice", "many advice"),
                    correctIndex = 2,
                    explanationAr = "كلمة advice غير معدودة في الإنجليزية ولا تُجمع ولا تأخذ an.",
                    level = CefrLevel.A2
                )
            )
        )
    )
}
