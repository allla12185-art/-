package com.example.data

import android.content.Context
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import com.example.model.AudioLessonTrack
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

data class AudioPlaybackState(
    val isReady: Boolean = false,
    val activeItemId: String? = null,
    val activeTrackId: String? = null,
    val currentSegmentIndex: Int = 0,
    val totalSegments: Int = 0,
    val currentSegmentSpeaker: String? = null,
    val currentSegmentEnglish: String? = null,
    val currentSegmentArabic: String? = null,
    val isSlowSpeed: Boolean = false,
    val ttsUnavailable: Boolean = false
)

class AudioPlayerManager(context: Context) {
    private val _state = MutableStateFlow(AudioPlaybackState())
    val state: StateFlow<AudioPlaybackState> = _state.asStateFlow()

    private var tts: TextToSpeech? = null
    private var pendingQueue: MutableList<Pair<String, String>> = mutableListOf()
    private var activeTrack: AudioLessonTrack? = null

    init {
        tts = TextToSpeech(context.applicationContext) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val engine = tts
                val res = engine?.setLanguage(Locale.US)
                val unavailable =
                    res == TextToSpeech.LANG_MISSING_DATA || res == TextToSpeech.LANG_NOT_SUPPORTED
                engine?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                    override fun onStart(utteranceId: String?) {
                        _state.value = _state.value.copy(activeItemId = utteranceId)
                    }

                    override fun onDone(utteranceId: String?) {
                        val track = activeTrack
                        if (track != null) {
                            val nextIdx = _state.value.currentSegmentIndex + 1
                            if (nextIdx < track.segments.size) {
                                playTrackSegment(track, nextIdx)
                            } else {
                                clearTrackState()
                            }
                        } else if (pendingQueue.isNotEmpty()) {
                            val next = pendingQueue.removeAt(0)
                            speakSingleInternal(next.first, next.second, TextToSpeech.QUEUE_FLUSH)
                        } else {
                            _state.value = _state.value.copy(activeItemId = null)
                        }
                    }

                    @Deprecated("Deprecated in Java")
                    override fun onError(utteranceId: String?) {
                        pendingQueue.clear()
                        clearTrackState()
                    }
                })
                _state.value = _state.value.copy(
                    isReady = !unavailable,
                    ttsUnavailable = unavailable
                )
            } else {
                _state.value = _state.value.copy(isReady = false, ttsUnavailable = true)
            }
        }
    }

    fun toggleSpeed() {
        val nextSlow = !_state.value.isSlowSpeed
        _state.value = _state.value.copy(isSlowSpeed = nextSlow)
    }

    fun speak(itemId: String, text: String) {
        val currentActive = _state.value.activeItemId
        if (currentActive == itemId) {
            stop()
            return
        }
        activeTrack = null
        pendingQueue.clear()
        _state.value = _state.value.copy(
            activeTrackId = null,
            currentSegmentIndex = 0,
            totalSegments = 0,
            currentSegmentSpeaker = null,
            currentSegmentEnglish = null,
            currentSegmentArabic = null
        )
        speakSingleInternal(itemId, text, TextToSpeech.QUEUE_FLUSH)
    }

    fun speakSequence(sequenceId: String, items: List<Pair<String, String>>) {
        if (_state.value.activeItemId == sequenceId || items.any { it.first == _state.value.activeItemId }) {
            stop()
            return
        }
        activeTrack = null
        pendingQueue.clear()
        _state.value = _state.value.copy(
            activeTrackId = null,
            currentSegmentIndex = 0,
            totalSegments = 0,
            currentSegmentSpeaker = null,
            currentSegmentEnglish = null,
            currentSegmentArabic = null
        )
        if (items.isEmpty()) return
        val first = items.first()
        if (items.size > 1) {
            pendingQueue.addAll(items.drop(1))
        }
        speakSingleInternal(first.first, first.second, TextToSpeech.QUEUE_FLUSH)
    }

    fun playAudioTrack(track: AudioLessonTrack, startIndex: Int = 0) {
        if (_state.value.activeTrackId == track.trackId && startIndex == 0) {
            stop()
            return
        }
        if (track.segments.isEmpty()) return
        pendingQueue.clear()
        activeTrack = track
        val clamped = startIndex.coerceIn(0, track.segments.lastIndex)
        playTrackSegment(track, clamped)
    }

    fun nextTrackSegment() {
        val track = activeTrack ?: return
        val nextIdx = (_state.value.currentSegmentIndex + 1).coerceAtMost(track.segments.lastIndex)
        playTrackSegment(track, nextIdx)
    }

    fun previousTrackSegment() {
        val track = activeTrack ?: return
        val prevIdx = (_state.value.currentSegmentIndex - 1).coerceAtLeast(0)
        playTrackSegment(track, prevIdx)
    }

    private fun playTrackSegment(track: AudioLessonTrack, index: Int) {
        val segment = track.segments.getOrNull(index) ?: return
        _state.value = _state.value.copy(
            activeTrackId = track.trackId,
            currentSegmentIndex = index,
            totalSegments = track.segments.size,
            currentSegmentSpeaker = segment.speakerOrLabel,
            currentSegmentEnglish = segment.englishText,
            currentSegmentArabic = segment.arabicMeaning
        )
        speakSingleInternal(segment.id, segment.englishText, TextToSpeech.QUEUE_FLUSH)
    }

    private fun speakSingleInternal(itemId: String, text: String, queueMode: Int) {
        val engine = tts ?: return
        if (!_state.value.isReady) return
        val rate = if (_state.value.isSlowSpeed) 0.70f else 1.0f
        engine.setSpeechRate(rate)
        engine.setPitch(1.0f)
        val params = Bundle().apply {
            putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, itemId)
        }
        _state.value = _state.value.copy(activeItemId = itemId)
        engine.speak(text, queueMode, params, itemId)
    }

    private fun clearTrackState() {
        activeTrack = null
        _state.value = _state.value.copy(
            activeItemId = null,
            activeTrackId = null,
            currentSegmentIndex = 0,
            totalSegments = 0,
            currentSegmentSpeaker = null,
            currentSegmentEnglish = null,
            currentSegmentArabic = null
        )
    }

    fun stop() {
        pendingQueue.clear()
        tts?.stop()
        clearTrackState()
    }

    fun shutdown() {
        pendingQueue.clear()
        activeTrack = null
        tts?.stop()
        tts?.shutdown()
        tts = null
    }
}
