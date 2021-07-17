package com.drbrosdev.studytextscan.ui.info

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.drbrosdev.studytextscan.BuildConfig
import com.drbrosdev.studytextscan.R
import com.drbrosdev.studytextscan.databinding.FragmentInfoBinding
import com.drbrosdev.studytextscan.util.showShortToast
import com.drbrosdev.studytextscan.util.updateWindowInsets
import com.drbrosdev.studytextscan.util.viewBinding
import com.google.android.material.transition.MaterialSharedAxis

class InfoFragment: Fragment(R.layout.fragment_info) {
    private val binding: FragmentInfoBinding by viewBinding()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateWindowInsets(binding.root)

        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)

        binding.apply {
            val text = BuildConfig.VERSION_NAME
            tvVersion.text = text

            ibGitNikola.setOnClickListener {
                val page = Uri.parse(getString(R.string.git_nikola))
                startActivity(Intent(Intent.ACTION_VIEW, page))
            }

            ibGitOgnjen.setOnClickListener {
                val page = Uri.parse(getString(R.string.git_ognjen))
                startActivity(Intent(Intent.ACTION_VIEW, page))
            }

            tvRateApp.setOnClickListener {
                val page = Uri.parse("https://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}")
                startActivity(Intent(Intent.ACTION_VIEW, page))
            }

            tvPrivacy.setOnClickListener {
                //TODO change when policy is created
                val page = Uri.parse("https://nikoladrljaca.github.io/bitcodept/privacy-policy")
                startActivity(Intent(Intent.ACTION_VIEW, page))
            }

            tvReportBug.setOnClickListener {
                //TODO check which string is used!
                val addresses = arrayOf(getString(R.string.mail_nikola), getString(R.string.mail_ognjen))
                val intent = Intent(Intent.ACTION_SENDTO).apply {
                    data = Uri.parse("mailto:")
                    putExtra(Intent.EXTRA_EMAIL, addresses)
                    putExtra(Intent.EXTRA_SUBJECT, "bug_report")
                }
                startActivity(intent)
            }

            tvSupport.setOnClickListener {
                //TODO: Complete setup, checkout buy me a coffee site, or setup a github page and have it there
                showShortToast("Support clicked")
            }

            tvShare.setOnClickListener {
                //TODO check which text is used
                val shareIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, "share_text")
                    type = "text/plain"
                }
                val intent = Intent.createChooser(shareIntent, null)
                startActivity(intent)
            }

            tvLicenses.setOnClickListener {
                //TODO check what is here to be done
//                findNavController().navigate(R.id.action_homeScanFragment_to_detailScanFragment)
//                reenterTransition = Fade()
                showShortToast("Licences clicked")
            }

        }

    }
}