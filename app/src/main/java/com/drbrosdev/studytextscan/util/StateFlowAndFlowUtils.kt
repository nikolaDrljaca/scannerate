package com.drbrosdev.studytextscan.util

import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

/*
Grab dependencies here:
    * repeatOnLifeCycle: 'androidx.lifecycle:lifecycle-runtime-ktx:2.4.0-alpha02'

    Keep in mind the dep version will change and may be different at the time you are
    looking at this.
 */
inline fun <T> Fragment.collectFlow(state: Flow<T>, crossinline render: ((T) -> Unit)) {
    viewLifecycleOwner.lifecycleScope.launch {
        viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            state.collect { state ->
                render(state)
            }
        }
    }
}

/*
Similarly to the collect functions above, these are not scoped to the fragment
but to the Flow/StateFlow itself, requiring a lifecycle owner as a parameter.
The lambda passed in is still essentially the same thing as above.

-In terms of collecting UI state, this feels like livedata, where you call
    livedata.observe(viewLifecycleOwner) { render... }
In this case its:
    stateflow.observe(viewLifecycleOwner) { action... }


For further information check these great articles explaining these topics:
    https://medium.com/androiddevelopers/a-safer-way-to-collect-flows-from-android-uis-23080b1f8bda
    https://medium.com/androiddevelopers/repeatonlifecycle-api-design-story-8670d1a7d333

The code itself here originates from the second article.
 */
inline fun <T> Flow<T>.collectAndLaunchIn(
    owner: LifecycleOwner,
    minActiveState: Lifecycle.State = Lifecycle.State.STARTED,
    crossinline block: suspend CoroutineScope.(T) -> Unit
) = owner.lifecycleScope.launch {
    owner.repeatOnLifecycle(minActiveState) {
        collect { block(it) }
    }
}