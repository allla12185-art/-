package com.example.data

import com.example.model.CefrLevel
import com.example.model.DialogueLine
import com.example.model.GrammarPoint
import com.example.model.Lesson
import com.example.model.MindMapBranch
import com.example.model.QuizQuestion
import com.example.model.SentenceItem
import com.example.model.StorySegment

object LessonsDataB1C2 {
    val lessons: List<Lesson> = listOf(
        Lesson(
            id = "b1_present_perfect",
            titleAr = "المضارع التام وتجارب الحياة",
            titleEn = "Present Perfect vs Past Simple",
            level = CefrLevel.B1,
            unitAr = "الوحدة 5: ربط الماضي بالحاضر",
            durationMinutes = 16,
            summaryAr = "تعرّف على الفارق الجوهري بين حدث انتهى في وقت محدد (Past Simple) وتجربة أو أثر ممتد للحاضر (Present Perfect).",
            goldenTipAr = "إذا ذكرت وقتاً محدداً في الماضي (yesterday, in 2022) استخدم الماضي البسيط فوراً. أما للتجارب بدون وقت محدد فاستخدم have/has + P.P.",
            branch = MindMapBranch.GRAMMAR,
            grammarPoints = listOf(
                GrammarPoint(
                    titleAr = "المضارع التام",
                    titleEn = "Present Perfect (have / has + V3)",
                    simpleRuleAr = "نستخدمه للخبرات (ever/never) والأحداث التي لها أثر الآن، ومع since (بداية المدة) و for (طول المدة).",
                    formula = "Subject + have / has + Past Participle (V3)",
                    commonArabMistakeAr = "استخدام since مع عدد السنوات مثل (since 3 years) والصحيح (for 3 years).",
                    examples = listOf(
                        SentenceItem("g_pp_1", "I have worked in this company for four years.", "آي هاف ووركد إن ذيس كومباني فور فور ييرز", "أعمل في هذه الشركة منذ أربع سنوات.", "العمل"),
                        SentenceItem("g_pp_2", "Have you ever visited Istanbul?", "هاف يو إفر فيزيتد إسطنبول؟", "هل سبق لك زيارة إسطنبول؟", "الخبرات"),
                        SentenceItem("g_pp_3", "She has lived here since 2021.", "شي هاز ليفد هير سنس ٢٠٢١", "هي تسكن هنا منذ عام ٢٠٢١.", "الزمن")
                    )
                )
            ),
            dialogue = listOf(
                DialogueLine("Interviewer", "How long have you worked as a data analyst?", "هاو لونج هاف يو ووركد آز أ ديتا أناليست؟", "منذ متى وأنت تعمل كمحلل بيانات؟"),
                DialogueLine("Ali", "I've worked in analytics for three years.", "آيف ووركد إن أناليتيكس فور ثري ييرز.", "أعمل في تحليل البيانات منذ ثلاث سنوات."),
                DialogueLine("Interviewer", "Have you led any big projects yet?", "هاف يو ليد إني بيج بروجيكتس يت؟", "هل قدت أي مشاريع كبيرة حتى الآن؟"),
                DialogueLine("Ali", "Yes, I've just completed a sales dashboard for our regional team.", "يس، آيف جاست كومبليتد أ سيلز داشبورد فور أور ريجونال تيم.", "نعم، لقد أتممت للتو لوحة مبيعات لفريقنا الإقليمي.")
            ),
            sentences = listOf(
                SentenceItem("b1_s1", "In my opinion, consistency matters more than speed.", "إن ماي أوبينيون، كونسيستنسي ماترز مور ذان سبيد", "في رأيي، الاستمرارية أهم من السرعة.", "إبداء الرأي"),
                SentenceItem("b1_s2", "I see your point, however we need more data.", "آي سي يور بوينت، هاوإفر وي نيد مور ديتا", "أتفهم وجهة نظرك، ومع ذلك نحتاج لمزيد من البيانات.", "النقاش"),
                SentenceItem("b1_s3", "I've been learning English since last year.", "آيف بين ليرنينج إنجلش سنس لاست يير", "أتعلم الإنجليزية منذ العام الماضي.", "التجربة")
            ),
            story = StorySegment(
                titleEn = "The Turning Point Interview",
                titleAr = "مقابلة نقطة التحول",
                paragraphs = listOf(
                    "Ali used to memorize answers before interviews, which made him sound stiff." to "اعتاد علي حفظ الإجابات قبل المقابلات، مما جعله يبدو متكلفاً.",
                    "Once he mastered Present Perfect storytelling—connecting past achievements to today's goals—the conversation felt natural." to "بمجرد أن أتقن سرد إنجازاته بالربط بين الماضي والحاضر، أصبح الحوار طبيعياً ومقنعاً."
                ),
                moralOrTipAr = "اربط كل إنجاز سابق بما أضافه لك اليوم في مقابلات العمل."
            ),
            quiz = listOf(
                QuizQuestion(
                    id = "q_b1_1",
                    promptAr = "اختر الأداة الزمنية الصحيحة:",
                    englishPrompt = "We have known each other ___ ten years.",
                    options = listOf("since", "for", "ago", "from"),
                    correctIndex = 1,
                    explanationAr = "مع طول المدة الزمنية (ten years) نستخدم for.",
                    level = CefrLevel.B1
                ),
                QuizQuestion(
                    id = "q_b1_2",
                    promptAr = "أي جملة صحيحة؟",
                    englishPrompt = "Choose the accurate sentence:",
                    options = listOf(
                        "I have seen him yesterday.",
                        "I saw him yesterday.",
                        "I seen him yesterday.",
                        "I am seeing him yesterday."
                    ),
                    correctIndex = 1,
                    explanationAr = "وجود yesterday يحدد الزمن الماضي فنجبر على استخدام الماضي البسيط saw.",
                    level = CefrLevel.B1
                )
            )
        ),
        Lesson(
            id = "b2_phrasal_work",
            titleAr = "الأفعال المركبة ولغة العمل",
            titleEn = "Phrasal Verbs & Workplace Fluency",
            level = CefrLevel.B2,
            unitAr = "الوحدة 6: الاحتراف في العمل",
            durationMinutes = 16,
            summaryAr = "تعلّم أشهر الأفعال المركبة (figure out, look into, put off) والمبني للمجهول لصياغة تقارير ومناقشات احترافية.",
            goldenTipAr = "المتحدث الأصلي يستخدم الأفعال المركبة في الحديث اليومي والاجتماعات أكثر من الكلمات اللاتينية الطويلة.",
            branch = MindMapBranch.VOCAB,
            grammarPoints = listOf(
                GrammarPoint(
                    titleAr = "أفعال مركبة لا غنى عنها",
                    titleEn = "Essential Phrasal Verbs",
                    simpleRuleAr = "الفعل المركب يتكون من فعل + حرف جر يعطي معنى جديداً تماماً. احفظه بمثال واقعي من بيئة العمل أو الحياة.",
                    formula = "Verb + Particle (up / out / into / off)",
                    commonArabMistakeAr = "ترجمة الفعل المركب حرفياً مثل (give up = يعطي فوق) بينما معناها الحقيقي (يستسلم).",
                    examples = listOf(
                        SentenceItem("g_pv_1", "Let's figure out why the numbers dropped.", "ليتس فيجر أوت واي ذا نمبرز دروبد", "دعنا نكتشف سبب انخفاض الأرقام.", "العمل"),
                        SentenceItem("g_pv_2", "Please look into this client request today.", "بليز لوك إنتو ذيس كلاينت ريكويست توداي", "يرجى فحص طلب هذا العميل اليوم.", "العمل"),
                        SentenceItem("g_pv_3", "Don't put off today's practice until tomorrow.", "دونت بوت أوف تودايز براكتس أنتيل تومورو", "لا تؤجّل تدريب اليوم إلى الغد.", "الإنتاجية")
                    )
                )
            ),
            dialogue = listOf(
                DialogueLine("Manager", "Did you wrap up the Q3 report?", "ديد يو راب أب ذا كيو ثري ريبورت؟", "هل أنهيت تقرير الربع الثالث؟"),
                DialogueLine("Peggy", "Almost! I'm following up with the finance team right now.", "أولموست! آيم فولوينج أب وذ ذا فاينانس تيم رايت ناو.", "تقريباً! أتابع الآن مع فريق المالية."),
                DialogueLine("Manager", "Great, let's touch base at four.", "جريت، ليتس تاتش بيس آت فور.", "ممتاز، لنتواصل سريعاً في الرابعة.")
            ),
            sentences = listOf(
                SentenceItem("b2_s1", "To put it briefly, our strategy paid off.", "تو بوت إت بريفلي، أور ستراتيجي بيد أوف", "باختصار، استراتيجيتنا أتت بثمارها.", "العروض"),
                SentenceItem("b2_s2", "We need to come up with a practical solution.", "وي نيد تو كم أب وذ أ براكتيكال سولوشن", "نحتاج إلى ابتكار حل عملي.", "الحلول"),
                SentenceItem("b2_s3", "The new policy will be implemented next Monday.", "ذا نيو بوليسي ويل بي إمبليمنتد نيكست مانديه", "سيتم تطبيق السياسة الجديدة الاثنين القادم.", "المبني للمجهول")
            ),
            story = StorySegment(
                titleEn = "The 3-Minute Briefing",
                titleAr = "الإيجاز الثلاثي",
                paragraphs = listOf(
                    "When Peggy started leading international meetings, she noticed executives preferred clear, concise updates." to "عندما بدأت بيجي قيادة الاجتماعات الدولية، لاحظت أن المديرين يفضلون التحديثات الواضحة والمختصرة.",
                    "By replacing long paragraphs with 'Here is what we found, why it matters, and our next step', her influence doubled." to "باستبدال الفقرات الطويلة بثلاث نقاط: ما وجدناه، أهميته، والخطوة التالية، تضاعف تأثيرها."
                ),
                moralOrTipAr = "الوضوح والاختصار هما علامة المستوى B2 الحقيقي."
            ),
            quiz = listOf(
                QuizQuestion(
                    id = "q_b2_1",
                    promptAr = "ما معنى «put off» في العمل؟",
                    englishPrompt = "We had to put off the meeting until Friday.",
                    options = listOf("نُلغي للأبد", "نُؤجّل", "نبدأ مبكراً", "نُسرع"),
                    correctIndex = 1,
                    explanationAr = "put off تعني postpone أي يؤجّل.",
                    level = CefrLevel.B2
                )
            )
        ),
        Lesson(
            id = "c1_persuasion_inversion",
            titleAr = "البلاغة والإقناع والتقديم",
            titleEn = "Advanced Persuasion & Inversion",
            level = CefrLevel.C1,
            unitAr = "الوحدة 7: مستوى الإجادة",
            durationMinutes = 18,
            summaryAr = "ارتقِ بأسلوبك عبر التقديم البلاغي (Not only... but also) وأدوات الربط المتقدمة للتأثير والإقناع.",
            goldenTipAr = "استخدام جملة واحدة بأسلوب Inversion في عرض تقديمي أو مقال يمنح لغتك ثقلاً أكاديمياً رفيعاً.",
            branch = MindMapBranch.GRAMMAR,
            grammarPoints = listOf(
                GrammarPoint(
                    titleAr = "التقديم للتوكيد",
                    titleEn = "Negative Inversion",
                    simpleRuleAr = "عند بدء الجملة بعبارة نفي مثل Not only أو Never أو Rarely، نعكس الفعل المساعد قبل الفاعل تماماً مثل صيغة السؤال.",
                    formula = "Not only + Auxiliary + Subject + Verb, but also...",
                    commonArabMistakeAr = "نسيان عكس الفعل المساعد بعد Not only مثل قول (Not only she speaks...) والصحيح (Not only does she speak...).",
                    examples = listOf(
                        SentenceItem("g_inv_1", "Not only does this habit save time, but it also boosts confidence.", "نوت أونلي داز ذيس هابيت سيف تايم، بت إت أولسو بوستس كونفيدنس", "لا توفر هذه العادة الوقت فحسب، بل تعزز الثقة أيضاً.", "البلاغة"),
                        SentenceItem("g_inv_2", "Seldom have I seen such remarkable progress.", "سيلدم هاف آي سين ساتش ريماركابل بروجرس", "قلّما رأيت مثل هذا التقدم الملحوظ.", "التوكيد")
                    )
                )
            ),
            dialogue = listOf(
                DialogueLine("Director", "How do we justify investing in this training program?", "هاو دو وي جاستيفاي إنفيستينج إن ذيس ترينينج بروجرام؟", "كيف نبرر الاستثمار في برنامج التدريب هذا؟"),
                DialogueLine("Nora", "Not only does it bridge the communication gap, but it also cuts onboarding time in half.", "نوت أونلي داز إت بريدج ذا كوميونيكيشن جاب، بت إت أولسو كاتس أونبوردينج تايم إن هاف.", "لا يسد فجوة التواصل فحسب، بل يختصر وقت التهيئة إلى النصف.")
            ),
            sentences = listOf(
                SentenceItem("c1_s1", "Notwithstanding the initial challenges, the launch succeeded.", "نوتويثستاندينج ذا إنيشال تشالينجز، ذا لونش سكسيدد", "على الرغم من التحديات الأولية، نجح الإطلاق.", "الأسلوب الرسمي"),
                SentenceItem("c1_s2", "It is imperative that we address the root cause.", "إت إز إمبيراتيف ذات وي أدريس ذا روت كوز", "من الضروري للغاية أن نعالج السبب الجذري.", "الإقناع")
            ),
            story = StorySegment(
                titleEn = "The Power of Nuance",
                titleAr = "قوة الدقة التعبيرية",
                paragraphs = listOf(
                    "At C1, knowing a thousand synonyms matters less than choosing the exact word that matches the tone." to "في مستوى C1، لا تكمن القوة في كثرة المترادفات بقدر اختيار الكلمة التي تناسب الموقف بدقة.",
                    "Saying 'mitigate risk' instead of 'make danger small' instantly signals mastery in boardrooms." to "استبدال العبارات العامة بتعبيرات دقيقة مثل mitigate risk يعكس نضجاً لغوياً فورياً."
                ),
                moralOrTipAr = "درّب أذنك على الفروق الدقيقة بين الرسمي والودي."
            ),
            quiz = listOf(
                QuizQuestion(
                    id = "q_c1_1",
                    promptAr = "اختر التركيب الصحيح بعد Never:",
                    englishPrompt = "Never ___ such a clear explanation.",
                    options = listOf("I have heard", "have I heard", "I heard", "did I heard"),
                    correctIndex = 1,
                    explanationAr = "بعد Never في بداية الجملة للتأكيد، نقدم الفعل المساعد have على الفاعل I.",
                    level = CefrLevel.C1
                )
            )
        ),
        Lesson(
            id = "c2_idioms_mastery",
            titleAr = "الإتقان والتعبيرات الاصطلاحية",
            titleEn = "C2 Idioms, Humor & Native Flow",
            level = CefrLevel.C2,
            unitAr = "الوحدة 8: قمة الإتقان",
            durationMinutes = 16,
            summaryAr = "أتقن التعبيرات المجازية الذكية (Idioms) التي تجعل لغتك سلسة وعميقة مثل المتحدثين الأصليين.",
            goldenTipAr = "التعبير الاصطلاحي لا يُترجم كلمة بكلمة؛ افهم الصورة الذهنية خلفه واستخدمه في السياق المناسب.",
            branch = MindMapBranch.SPEAKING,
            grammarPoints = listOf(
                GrammarPoint(
                    titleAr = "صياغة التشبيهات والاصطلاحات",
                    titleEn = "Idiomatic Precision & Register Shifting",
                    simpleRuleAr = "الإتقان التام يعني القدرة على استخدام العبارات المجازية بذكاء دون مبالغة، والانتقال السلس بين الحوار الذكي واللغة الرسمية.",
                    formula = "Context + Idiom + Natural Rhythm",
                    commonArabMistakeAr = "حشر اصطلاحات قديمة أو رسمية جداً في محادثة عادية، أو ترجمة الأمثال العربية حرفياً إلى الإنجليزية.",
                    examples = listOf(
                        SentenceItem("g_c2_1", "Let's not beat around the bush; here is the core issue.", "ليتس نوت بيت أراوند ذا بوش؛ هير إز ذا كور إيشو", "دعنا لا نلف وندور؛ إليك جوهر المشكلة.", "الاصطلاحات"),
                        SentenceItem("g_c2_2", "You hit the nail on the head with that observation.", "يو هيت ذا نيل أون ذا هيد وذ ذات أوبزيرفيشن", "لقد أصبت كبد الحقيقة بتلك الملاحظة.", "الاصطلاحات")
                    )
                )
            ),
            dialogue = listOf(
                DialogueLine("Alex", "That deadline looked impossible, yet you pulled it off!", "ذات ديدلاين لوكد إمبوسيبل، يت يو بولد إت أوف!", "بدا ذلك الموعد مستحيلاً، ومع ذلك أنجزته ببراعة!"),
                DialogueLine("Sara", "Honestly, it took burning the midnight oil, but it was worth every minute.", "أونستلي، إت توك بيرنينج ذا ميدنايت أويل، بت إت واز وورث إيفري مينيت.", "بصراحة، تطلّب الأمر السهر والعمل الدؤوب، لكنه استحق كل دقيقة.")
            ),
            sentences = listOf(
                SentenceItem("c2_s1", "It's a blessing in disguise.", "إتس أ بليسينج إن ديسجايز", "رُب ضارة نافعة.", "الاصطلاحات"),
                SentenceItem("c2_s2", "Let's play it by ear depending on the feedback.", "ليتس بلاي إت باي إير ديبيندينج أون ذا فيدباك", "لنتصرف حسب مجريات الأمور بناءً على التقييم.", "الاصطلاحات"),
                SentenceItem("c2_s3", "Once in a blue moon do we get an opportunity this rare.", "ونس إن أ بلو مون دو وي جيت آن أوبورتيونيتي ذيس رير", "في النادر جداً أن نحصل على فرصة بهذه الندرة.", "الاصطلاحات")
            ),
            story = StorySegment(
                titleEn = "Speaking Without Translating",
                titleAr = "التفكير بالإنجليزية مباشرة",
                paragraphs = listOf(
                    "The moment you stop translating word-for-word in your head, English stops feeling like a puzzle and becomes music." to "في اللحظة التي تتوقف فيها عن الترجمة الحرفية داخل ذهنك، تصبح الإنجليزية انسياباً طبيعياً لا أحجية.",
                    "Every idiom carries a tiny story—remember the picture, and the words come effortlessly." to "كل تعبير اصطلاحي يحمل صورة ذهنية مصغرة؛ تذكّر الصورة فتنساب الكلمات بلا عناء."
                ),
                moralOrTipAr = "استمع، كرّر بالإيقاع، وفكّر بالصور والمعاني مباشرة."
            ),
            quiz = listOf(
                QuizQuestion(
                    id = "q_c2_1",
                    promptAr = "ما معنى عبارة «hit the nail on the head»؟",
                    englishPrompt = "You really hit the nail on the head.",
                    options = listOf("أخطأت التقدير", "أصبت الهدف بدقة تماماً", "تأخرت عن الموعد", "تجاهلت المشكلة"),
                    correctIndex = 1,
                    explanationAr = "تعني أنك وصفت السبب أو الحل بدقة متناهية.",
                    level = CefrLevel.C2
                )
            )
        )
    )
}
