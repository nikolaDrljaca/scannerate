package com.drbrosdev.studytextscan.ui.licences

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.transition.Fade
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import com.drbrosdev.studytextscan.R
import com.drbrosdev.studytextscan.databinding.FragmentLicencesBinding
import com.drbrosdev.studytextscan.ui.support.theme.ScannerateTheme
import com.drbrosdev.studytextscan.util.updateWindowInsets
import com.drbrosdev.studytextscan.util.viewBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class LicencesFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enterTransition = Fade()
        returnTransition = Fade()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            updateWindowInsets(this)
            transitionName = "licenses_frag"
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)

            setContent {
                ScannerateTheme {
                    ScannerateLicensesScreen(
                        modifier = Modifier,
                        onLicenseClick = ::openLink
                    )
                }
            }
        }
    }

    private fun openLink(link: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
        startActivity(intent)
    }
}