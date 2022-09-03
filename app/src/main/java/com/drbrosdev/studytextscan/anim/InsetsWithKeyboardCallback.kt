package com.drbrosdev.studytextscan.anim

import android.os.Build
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.core.view.OnApplyWindowInsetsListener
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsAnimationCompat
import androidx.core.view.WindowInsetsCompat
import org.koin.core.KoinApplication.Companion.init

class InsetsWithKeyboardCallback(window: Window) : OnApplyWindowInsetsListener,
    WindowInsetsAnimationCompat.Callback(DISPATCH_MODE_CONTINUE_ON_SUBTREE) {

    private var defferedInsets = false
    private var view: View? = null
    private var lastWindowInsets: WindowInsetsCompat? = null

    init {
        // For better support for devices API 29 and lower
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
            @Suppress("DEPRECATION")
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        }
    }

    override fun onApplyWindowInsets(v: View, insets: WindowInsetsCompat): WindowInsetsCompat {
        view = v
        lastWindowInsets = insets
        val types = when {
            // When the deferred flag is enabled, we only use the systemBars() insets
            defferedInsets -> WindowInsetsCompat.Type.systemBars()
            // When the deferred flag is disabled, we use combination of the the systemBars() and ime() insets
            else -> WindowInsetsCompat.Type.systemBars() + WindowInsetsCompat.Type.ime()
        }

        val typeInsets = insets.getInsets(types)
        v.setPadding(typeInsets.left, typeInsets.top, typeInsets.right, typeInsets.bottom)
        return WindowInsetsCompat.CONSUMED
    }

    override fun onProgress(
        insets: WindowInsetsCompat,
        runningAnimations: MutableList<WindowInsetsAnimationCompat>
    ): WindowInsetsCompat {
        return insets
    }

    override fun onPrepare(animation: WindowInsetsAnimationCompat) {
        if (animation.typeMask and WindowInsetsCompat.Type.ime() != 0) {
            // When the IME is not visible, we defer the WindowInsetsCompat.Type.ime() insets
            defferedInsets = true
        }
    }

    override fun onEnd(animation: WindowInsetsAnimationCompat) {
        if (defferedInsets && (animation.typeMask and WindowInsetsCompat.Type.ime()) != 0) {
            // When the IME animation has finished and the IME inset has been deferred, we reset the flag
            defferedInsets = false

            // We dispatch insets manually because if we let the
            // normal dispatch cycle handle it, this will happen too late and cause a visual flicker
            // So we dispatch the latest WindowInsets to the view
            if (lastWindowInsets != null && view != null) {
                ViewCompat.dispatchApplyWindowInsets(view!!, lastWindowInsets!!)
            }
        }
    }
}