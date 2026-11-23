package com.example.data

import com.example.model.CefrLevel
import com.example.model.MindMapBranch
import com.example.model.MindMapNode

object MindMapData {
    // Ordered journey path from Stage 1 (Bottom/Start) -> Bridge -> Stage 2 -> Stage 3 -> Fluency City
    val nodes: List<MindMapNode> = listOf(
        // Stage 1: Foundation (المستوى الأول: البداية والتأسيس)
        MindMapNode(
            id = "step_1_greetings",
            lessonId = "a1_greetings_be",
            titleAr = "الدرس 1: الحروف والتعارف",
            titleEn = "Letters & Greetings",
            subtitleAr = "البداية من الصفر وفعل الكينونة am / is / are",
            level = CefrLevel.A1,
            branch = MindMapBranch.SPEAKING,
            xFraction = 0.24f,
            yFraction = 0.88f,
            parentId = null
        ),
        MindMapNode(
            id = "step_2_words",
            lessonId = "a1_daily_routine",
            titleAr = "الدرس 2: الكلمات والروتين",
            titleEn = "Words & Routine",
            subtitleAr = "عاداتك اليومية والمضارع البسيط",
            level = CefrLevel.A1,
            branch = MindMapBranch.GRAMMAR,
            xFraction = 0.72f,
            yFraction = 0.79f,
            parentId = "step_1_greetings"
        ),
        MindMapNode(
            id = "step_3_grammar",
            lessonId = "a2_past_travel",
            titleAr = "الدرس 3: القواعد البسيطة",
            titleEn = "Simple Grammar & Travel",
            subtitleAr = "الماضي البسيط وحكايات السفر والمطار",
            level = CefrLevel.A2,
            branch = MindMapBranch.STORY,
            xFraction = 0.34f,
            yFraction = 0.70f,
            parentId = "step_2_words"
        ),
        MindMapNode(
            id = "step_4_sentences",
            lessonId = "a2_food_shopping",
            titleAr = "الدرس 4: جمل قصيرة",
            titleEn = "Short Sentences",
            subtitleAr = "الطلب المهذب في المطعم والتسوق",
            level = CefrLevel.A2,
            branch = MindMapBranch.VOCAB,
            xFraction = 0.76f,
            yFraction = 0.61f,
            parentId = "step_3_grammar"
        ),
        MindMapNode(
            id = "step_5_placement",
            lessonId = "placement_test",
            titleAr = "الدرس 5: اختبار المستوى",
            titleEn = "Level Check",
            subtitleAr = "اختبر مستواك من A1 إلى C2 قبل عبور الجسر",
            level = CefrLevel.A2,
            branch = MindMapBranch.HUB,
            xFraction = 0.48f,
            yFraction = 0.51f,
            parentId = "step_4_sentences"
        ),
        // Stage 2: Reading & Conversation (المستوى الثاني: القراءة والمحادثة)
        MindMapNode(
            id = "step_6_phonetics",
            lessonId = "phonetics_studio",
            titleAr = "الدرس 6: مختبر الصوتيات",
            titleEn = "American Phonetics",
            subtitleAr = "Schwa و TH و Flap T وأزواج التشابه",
            level = CefrLevel.B1,
            branch = MindMapBranch.PHONETICS,
            xFraction = 0.25f,
            yFraction = 0.40f,
            parentId = "step_5_placement"
        ),
        MindMapNode(
            id = "step_7_present_perfect",
            lessonId = "b1_present_perfect",
            titleAr = "الدرس 7: المحادثة والخبرات",
            titleEn = "Present Perfect Flow",
            subtitleAr = "ربط الماضي بالحاضر والتعبير عن الرأي",
            level = CefrLevel.B1,
            branch = MindMapBranch.GRAMMAR,
            xFraction = 0.72f,
            yFraction = 0.31f,
            parentId = "step_6_phonetics"
        ),
        MindMapNode(
            id = "step_8_workplace",
            lessonId = "b2_phrasal_work",
            titleAr = "الدرس 8: القراءة والعمل",
            titleEn = "Workplace Fluency",
            subtitleAr = "الأفعال المركبة ولغة الاجتماعات",
            level = CefrLevel.B2,
            branch = MindMapBranch.VOCAB,
            xFraction = 0.30f,
            yFraction = 0.22f,
            parentId = "step_7_present_perfect"
        ),
        // Stage 3: Advanced & Fluency City (المستوى الثالث: المتقدم -> مدينة الطلاقة)
        MindMapNode(
            id = "step_9_persuasion",
            lessonId = "c1_persuasion_inversion",
            titleAr = "الدرس 9: البلاغة والإقناع",
            titleEn = "Advanced Persuasion",
            subtitleAr = "أسلوب التقديم والكتابة الاحترافية",
            level = CefrLevel.C1,
            branch = MindMapBranch.GRAMMAR,
            xFraction = 0.72f,
            yFraction = 0.14f,
            parentId = "step_8_workplace"
        ),
        MindMapNode(
            id = "step_10_mastery",
            lessonId = "c2_idioms_mastery",
            titleAr = "الدرس 10: مدينة الطلاقة",
            titleEn = "Fluency City C2",
            subtitleAr = "التعبيرات الاصطلاحية وسلاسة المتحدث الأصلي",
            level = CefrLevel.C2,
            branch = MindMapBranch.SPEAKING,
            xFraction = 0.44f,
            yFraction = 0.06f,
            parentId = "step_9_persuasion",
            isRoot = true
        )
    )
}
