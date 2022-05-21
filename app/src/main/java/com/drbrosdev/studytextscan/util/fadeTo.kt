package com.drbrosdev.studytextscan.util

import android.view.View
import androidx.core.view.isVisible
import androidx.interpolator.view.animation.FastOutSlowInInterpolator

/**
 * Fade a view to visible or gone. This function is idempotent - it can be called over and over again with the same
 * value without affecting an in-progress animation.
 */
fun View.fadeTo(visible: Boolean, duration: Long = 500, startDelay: Long = 0, toAlpha: Float = 1f) {
    // Make this idempotent.
    val tagKey = "fadeTo".hashCode()
    if (visible == isVisible && animation == null && getTag(tagKey) == null) return
    if (getTag(tagKey) == visible) return

    setTag(tagKey, visible)
    setTag("fadeToAlpha".hashCode(), toAlpha)

    if (visible && alpha == 1f) alpha = 0f
    animate()
        .alpha(if (visible) toAlpha else 0f)
        .withStartAction {
            if (visible) isVisible = true
        }
        .withEndAction {
            setTag(tagKey, null)
            if (isAttachedToWindow && !visible) isVisible = false
        }
        .setInterpolator(FastOutSlowInInterpolator())
        .setDuration(duration)
        .setStartDelay(startDelay)
        .start()
}