package com.example.rangegen.util

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.view.HapticFeedbackConstants
import android.view.View

object HapticsHelper {

    fun performHaptic(context: Context, enabled: Boolean, isError: Boolean = false) {
        if (!enabled) return

        try {
            val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vibratorManager?.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            }

            if (vibrator != null && vibrator.hasVibrator()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    if (isError) {
                        vibrator.vibrate(
                            VibrationEffect.createWaveform(longArrayOf(0, 40, 60, 40), -1)
                        )
                    } else {
                        vibrator.vibrate(
                            VibrationEffect.createOneShot(25, VibrationEffect.DEFAULT_AMPLITUDE)
                        )
                    }
                } else {
                    @Suppress("DEPRECATION")
                    vibrator.vibrate(if (isError) 100L else 25L)
                }
            }
        } catch (_: Exception) {
            // Ignore if vibration fails or permission not available
        }
    }
}
