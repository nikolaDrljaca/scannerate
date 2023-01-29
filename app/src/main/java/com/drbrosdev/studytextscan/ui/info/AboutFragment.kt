package com.drbrosdev.studytextscan.ui.info

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.drbrosdev.studytextscan.R
import com.drbrosdev.studytextscan.ui.info.components.SupportAction
import com.drbrosdev.studytextscan.ui.support.theme.ScannerateTheme
import com.drbrosdev.studytextscan.util.safeNav
import com.drbrosdev.studytextscan.util.updateWindowInsets
import com.google.android.material.transition.MaterialSharedAxis

class AboutFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            updateWindowInsets(this)
            transitionName = "about_frag"
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)

            setContent {
                ScannerateTheme {
                    ScannerateAboutScreen(
                        modifier = Modifier,
                        onActionClick = { handleActions(it) },
                        onLinkClick = { linkRes ->
                            val page = Uri.parse(getString(linkRes))
                            startActivity(Intent(Intent.ACTION_VIEW, page))
                        }
                    )
                }
            }
        }
    }

    private fun handleActions(supportAction: SupportAction) {
        when (supportAction) {
            SupportAction.Licenses -> {
                exitTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true)
                reenterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)
                val action = AboutFragmentDirections.toLicensesFragment()
                findNavController().safeNav(action)
            }
            SupportAction.SupportDev -> {
                val action = AboutFragmentDirections.toSupportFragment()
                findNavController().safeNav(action)
            }
            SupportAction.BugReport -> {
                val addresses =
                    arrayOf(getString(R.string.mail_nikola), getString(R.string.mail_ognjen))
                val intent = Intent(Intent.ACTION_SENDTO).apply {
                    data = Uri.parse("mailto:")
                    putExtra(Intent.EXTRA_EMAIL, addresses)
                    putExtra(Intent.EXTRA_SUBJECT, "Scannerate - Bug Report")
                }
                startActivity(intent)
            }
            SupportAction.Rate -> {
                val page =
                    Uri.parse("https://play.google.com/store/apps/details?id=com.drbrosdev.studytextscan")
                val intent = Intent(Intent.ACTION_VIEW, page)
                startActivity(intent)
            }
            SupportAction.Share -> {
                val shareIntent = Intent().apply {
                    this.action = Intent.ACTION_SEND
                    putExtra(
                        Intent.EXTRA_TEXT,
                        "Checkout this text scanning app - Scannerate: https://play.google.com/store/apps/details?id=com.drbrosdev.studytextscan"
                    )
                    type = "text/plain"
                }
                val intent = Intent.createChooser(shareIntent, null)
                startActivity(intent)
            }
        }
    }
}