package com.example.data

import android.util.Log
import com.example.model.CefrLevel
import com.example.model.StudyMode
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.Schema
import com.google.firebase.ai.type.content
import com.google.firebase.ai.type.generationConfig
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withTimeout
import org.json.JSONObject

private const val TAG = "AiMentorRepository"
private const val MODEL_NAME = "gemini-3.8-flash"

data class LearnerProgressSnapshot(
    val assessedLevel: CefrLevel?,
    val studyMode: StudyMode,
    val completedLessonTitles: List<String>,
    val nextLessonTitle: String?,
    val totalLessons: Int,
    val completedCanDoCount: Int,
    val bookmarkedCount: Int,
    val stars: Int,
    val diamonds: Int
) {
    fun toPromptContext(): String {
        val levelText = assessedLevel?.let { "${it.code} (${it.titleAr})" } ?: "غير محدد بعد (نقطة البداية A1)"
        val completedText = if (completedLessonTitles.isEmpty()) {
            "لم يكمل أي درس بعد (في البداية)"
        } else {
            completedLessonTitles.joinToString("، ")
        }
        val nextText = nextLessonTitle ?: "أتم جميع الدروس الأساسية — مرحلة الإتقان والمراجعة"
        return """
            ملف تقدم المتعلم الحالي:
            - المستوى المقيّم: $levelText
            - نمط الخطة اليومية: ${studyMode.titleAr}
            - الدروس المكتملة (${completedLessonTitles.size} من $totalLessons): $completedText
            - الدرس التالي المقترح في الخارطة: $nextText
            - الأهداف المحققة (Can-Do): $completedCanDoCount | الجمل المحفوظة: $bookmarkedCount | النجوم: $stars
        """.trimIndent()
    }
}

data class PersonalizedAdvice(
    val headlineAr: String,
    val personalizedTipAr: String,
    val practiceSentenceEn: String,
    val practiceHintAr: String,
    val nextStepActionAr: String
)

class AiMentorRepository {
    private val chatModel by lazy {
        Firebase.ai.generativeModel(
            modelName = MODEL_NAME,
            systemInstruction = content {
                text(
                    """
                    أنت "المرشد الذكي" لتعليم اللغة الإنجليزية للناطقين بالعربية داخل تطبيق رحلة الإنجليزية.
                    قواعد التوجيه الشخصي:
                    1. استخدم بيانات تقدم المتعلم (مستواه CEFR، دروسه المكتملة، درسه التالي، ووضعه اليومي 25 دقيقة أو 5 دقائق) لتخصيص الشرح والأمثلة.
                    2. اشرح ببساطة ووضوح بالعربية، وقدّم الأمثلة الإنجليزية واضحة مع نطق عربي تقريبي مختصر عند الحاجة.
                    3. إذا كتب المتعلم جملة إنجليزية، شجّعه أولاً ثم صحّح الأخطاء بلطف مع ذكر السبب في سطرين واقترح صياغة طبيعية تناسب مستواه.
                    4. حافظ على ردود عملية، موجزة، ومنظمة تناسب شاشة الهاتف.
                    """.trimIndent()
                )
            }
        )
    }

    private val adviceSchema by lazy {
        Schema.obj(
            properties = mapOf(
                "headlineAr" to Schema.string(description = "Short encouraging Arabic title tailored to the learner's current stage"),
                "personalizedTipAr" to Schema.string(description = "2-3 concise Arabic sentences of educational advice tailored strictly to the learner's level, completed lessons, and study mode"),
                "practiceSentenceEn" to Schema.string(description = "One practical English sentence at the exact difficulty of the learner's current/next lesson"),
                "practiceHintAr" to Schema.string(description = "Brief Arabic pronunciation or usage tip for the practice sentence"),
                "nextStepActionAr" to Schema.string(description = "One clear actionable next step in Arabic for today's session")
            )
        )
    }

    private val adviceModel by lazy {
        Firebase.ai.generativeModel(
            modelName = MODEL_NAME,
            generationConfig = generationConfig {
                responseMimeType = "application/json"
                responseSchema = adviceSchema
            },
            systemInstruction = content {
                text(
                    """
                    أنت مرشد تعليمي خبير للغة الإنجليزية للناطقين بالعربية.
                    تُنشئ نصيحة يومية مخصصة بصيغة JSON بناءً على تقدم المتعلم الفعلي ومستواه ووضع مذاكرته (25 دقيقة أو 5 دقائق).
                    تجنّب العبارات العامة؛ خاطب مستوى المتعلم تحديداً واقترح جملة تدريب واحدة مناسبة لمستواه.
                    """.trimIndent()
                )
            }
        )
    }

    suspend fun generatePersonalizedAdvice(snapshot: LearnerProgressSnapshot): PersonalizedAdvice {
        val prompt = buildString {
            appendLine(snapshot.toPromptContext())
            appendLine()
            append("قدّم نصيحة تعليمية مخصصة ومباشرة لهذا المتعلم الآن مع جملة إنجليزية للتدريب الصوتي تناسب مستواه وخطته اليومية.")
        }

        try {
            val response = withTimeout(60_000L) {
                adviceModel.generateContent(prompt)
            }
            val rawJson = response.text?.takeIf { it.isNotBlank() }
                ?: throw IllegalStateException("لم يُرجع المرشد الذكي نصيحة. حاول مرة أخرى.")
            val json = JSONObject(rawJson)
            return PersonalizedAdvice(
                headlineAr = json.getString("headlineAr"),
                personalizedTipAr = json.getString("personalizedTipAr"),
                practiceSentenceEn = json.getString("practiceSentenceEn"),
                practiceHintAr = json.getString("practiceHintAr"),
                nextStepActionAr = json.getString("nextStepActionAr")
            )
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Failed to generate personalized advice", e)
            throw e
        }
    }

    fun streamMentorReply(
        userPrompt: String,
        snapshot: LearnerProgressSnapshot,
        historySnippet: String
    ): Flow<String> = flow {
        val contextualPrompt = buildString {
            appendLine(snapshot.toPromptContext())
            if (historySnippet.isNotBlank()) {
                appendLine()
                appendLine("سياق المحادثة السابقة:")
                appendLine(historySnippet)
            }
            appendLine()
            appendLine("رسالة المتعلم الحالية:")
            append(userPrompt)
        }

        val accumulated = StringBuilder()
        try {
            withTimeout(60_000L) {
                chatModel.generateContentStream(contextualPrompt).collect { chunk ->
                    val piece = chunk.text.orEmpty()
                    if (piece.isNotEmpty()) {
                        accumulated.append(piece)
                        emit(accumulated.toString())
                    }
                }
            }
            if (accumulated.isBlank()) {
                throw IllegalStateException("لم يُرجع المرشد نصاً. يرجى المحاولة مرة أخرى.")
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Firebase AI Mentor streaming failed", e)
            throw e
        }
    }
}
