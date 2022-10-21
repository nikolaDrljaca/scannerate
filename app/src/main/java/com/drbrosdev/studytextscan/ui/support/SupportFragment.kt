package com.drbrosdev.studytextscan.ui.support

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.drbrosdev.studytextscan.ui.support.theme.ScannerateTheme
import com.drbrosdev.studytextscan.util.updateWindowInsets
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.flow.collect
import org.koin.androidx.viewmodel.ext.android.viewModel

class SupportFragment : BottomSheetDialogFragment() {
    private val viewModel: SupportViewModel by viewModel()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        dialog.setOnShowListener {
            val bottom = it as BottomSheetDialog
            val bottomSheet = bottom.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.setBackgroundColor(Color.TRANSPARENT)
        }
        return dialog
    }

    @OptIn(ExperimentalLifecycleComposeApi::class)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            transitionName = "support_frag_compose"
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)

            setContent {
                ScannerateTheme {
                    val state by viewModel.state.collectAsStateWithLifecycle()

                    LaunchedEffect(Unit) {
                        viewModel.queryProducts()
                    }

                    LaunchedEffect(viewModel.events) {
                        viewModel.events.collect {
                            when (it) {
                                is SupportEvents.ErrorOccured -> {
                                    Log.d("DEBUGn", "error occured ${it.message} || ${it.debugMessage}")
                                }
                                SupportEvents.NavigateToReward -> {

                                }
                            }
                        }
                    }

                    SupportScreen(
                        modifier = Modifier.padding(top = 16.dp),
                        state = state,
                        onProductSelected = { viewModel.selectProduct(it.productId) },
                        onVendorSelected = viewModel::selectVendor,
                        onSupportClicked = { viewModel.makePurchase(requireActivity()) }
                    )
                }
            }
        }
    }
}