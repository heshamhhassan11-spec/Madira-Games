package com.example.ui.util

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log
import com.example.data.model.SoundSettings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SoundManager(
    private val context: Context,
    private val soundSettings: StateFlow<SoundSettings>
) {
    private var toneGen: ToneGenerator? = null
    private val vibrator: Vibrator? by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            vibratorManager?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
    }

    private val scope = CoroutineScope(Dispatchers.Default)

    init {
        try {
            toneGen = ToneGenerator(AudioManager.STREAM_MUSIC, (soundSettings.value.volume * 100).toInt())
        } catch (e: Exception) {
            Log.e("SoundManager", "Failed to create ToneGenerator", e)
        }
    }

    fun playSound(soundKey: String) {
        val settings = soundSettings.value
        if (!settings.soundEffectsEnabled) return

        triggerHaptics(settings)

        scope.launch {
            try {
                val theme = settings.selectedSoundTheme
                val vol = (settings.volume * 100).toInt().coerceIn(1, 100)
                
                // Re-create/update generator with current volume before every sound
                toneGen?.release()
                toneGen = ToneGenerator(AudioManager.STREAM_MUSIC, vol)

                when (soundKey) {
                    "correct" -> playCorrectThemeSound(toneGen, theme)
                    "wrong" -> playWrongThemeSound(toneGen, theme)
                    "goal" -> playGoalThemeSound(toneGen, theme)
                    "click" -> playClickThemeSound(toneGen, theme)
                    "fanfare" -> playFanfareThemeSound(toneGen, theme)
                    else -> toneGen?.startTone(ToneGenerator.TONE_PROP_BEEP, 100)
                }
            } catch (e: Exception) {
                Log.e("SoundManager", "Error playing sound key: $soundKey", e)
            }
        }
    }

    private fun playCorrectThemeSound(tg: ToneGenerator?, theme: String) {
        when (theme) {
            "stadium_roar" -> {
                // High whistle + double high tone
                tg?.startTone(ToneGenerator.TONE_SUP_RINGTONE, 150)
                Thread.sleep(100)
                tg?.startTone(ToneGenerator.TONE_DTMF_D, 200)
            }
            "samba_drums" -> {
                // Energetic percussion double tap
                tg?.startTone(ToneGenerator.TONE_PROP_ACK, 80)
                Thread.sleep(80)
                tg?.startTone(ToneGenerator.TONE_PROP_ACK, 120)
            }
            "champions_anthem" -> {
                // Triumphant rising chord
                tg?.startTone(ToneGenerator.TONE_DTMF_1, 100)
                Thread.sleep(80)
                tg?.startTone(ToneGenerator.TONE_DTMF_5, 100)
                Thread.sleep(80)
                tg?.startTone(ToneGenerator.TONE_DTMF_A, 250)
            }
            "commentator_hype" -> {
                // Rapid whistle burst
                tg?.startTone(ToneGenerator.TONE_PROP_BEEP2, 120)
                Thread.sleep(90)
                tg?.startTone(ToneGenerator.TONE_PROP_PROMPT, 150)
            }
            "cyber_stadium" -> {
                // Arcade powerup sweep
                tg?.startTone(ToneGenerator.TONE_CDMA_PIP, 80)
                Thread.sleep(60)
                tg?.startTone(ToneGenerator.TONE_CDMA_HIGH_PBX_L, 180)
            }
            else -> tg?.startTone(ToneGenerator.TONE_PROP_ACK, 150)
        }
    }

    private fun playWrongThemeSound(tg: ToneGenerator?, theme: String) {
        when (theme) {
            "stadium_roar" -> {
                // Referee wrong whistle
                tg?.startTone(ToneGenerator.TONE_SUP_ERROR, 250)
            }
            "samba_drums" -> {
                tg?.startTone(ToneGenerator.TONE_CDMA_ABBR_ALERT, 200)
            }
            "champions_anthem" -> {
                tg?.startTone(ToneGenerator.TONE_PROP_NACK, 250)
            }
            "commentator_hype" -> {
                tg?.startTone(ToneGenerator.TONE_CDMA_SOFT_ERROR_LITE, 220)
            }
            "cyber_stadium" -> {
                tg?.startTone(ToneGenerator.TONE_SUP_CONGESTION, 200)
            }
            else -> tg?.startTone(ToneGenerator.TONE_SUP_ERROR, 200)
        }
    }

    private fun playGoalThemeSound(tg: ToneGenerator?, theme: String) {
        when (theme) {
            "stadium_roar" -> {
                // Stadium Goal Horn!
                repeat(3) {
                    tg?.startTone(ToneGenerator.TONE_DTMF_D, 180)
                    Thread.sleep(120)
                }
            }
            "samba_drums" -> {
                repeat(4) {
                    tg?.startTone(ToneGenerator.TONE_PROP_ACK, 70)
                    Thread.sleep(70)
                }
                tg?.startTone(ToneGenerator.TONE_DTMF_A, 300)
            }
            "champions_anthem" -> {
                val notes = listOf(ToneGenerator.TONE_DTMF_1, ToneGenerator.TONE_DTMF_3, ToneGenerator.TONE_DTMF_5, ToneGenerator.TONE_DTMF_8, ToneGenerator.TONE_DTMF_A)
                notes.forEach { note ->
                    tg?.startTone(note, 120)
                    Thread.sleep(100)
                }
            }
            "commentator_hype" -> {
                repeat(2) {
                    tg?.startTone(ToneGenerator.TONE_PROP_BEEP2, 150)
                    Thread.sleep(100)
                }
                tg?.startTone(ToneGenerator.TONE_DTMF_D, 350)
            }
            "cyber_stadium" -> {
                repeat(3) {
                    tg?.startTone(ToneGenerator.TONE_CDMA_HIGH_PBX_L, 100)
                    Thread.sleep(80)
                }
            }
            else -> tg?.startTone(ToneGenerator.TONE_DTMF_A, 400)
        }
    }

    private fun playClickThemeSound(tg: ToneGenerator?, theme: String) {
        tg?.startTone(ToneGenerator.TONE_PROP_BEEP, 50)
    }

    private fun playFanfareThemeSound(tg: ToneGenerator?, theme: String) {
        playGoalThemeSound(tg, theme)
    }

    private fun triggerHaptics(settings: SoundSettings) {
        if (!settings.hapticFeedbackEnabled) return
        try {
            vibrator?.let { vib ->
                if (vib.hasVibrator()) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        vib.vibrate(VibrationEffect.createOneShot(40, VibrationEffect.DEFAULT_AMPLITUDE))
                    } else {
                        @Suppress("DEPRECATION")
                        vib.vibrate(40)
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("SoundManager", "Failed to trigger vibration", e)
        }
    }

    fun release() {
        toneGen?.release()
        toneGen = null
    }
}

