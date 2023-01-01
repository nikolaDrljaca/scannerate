package com.drbrosdev.studytextscan.ui.info

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.transition.Fade
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.drbrosdev.studytextscan.BuildConfig
import com.drbrosdev.studytextscan.R
import com.drbrosdev.studytextscan.databinding.FragmentInfoBinding
import com.drbrosdev.studytextscan.util.showShortToast
import com.drbrosdev.studytextscan.util.updateWindowInsets
import com.drbrosdev.studytextscan.util.viewBinding
import com.google.android.material.transition.MaterialSharedAxis
import com.google.android.play.core.review.ReviewManager
import org.koin.android.ext.android.inject

class InfoFragment : Fragment(R.layout.fragment_info) {
    private val binding: FragmentInfoBinding by viewBinding(FragmentInfoBinding::bind)
    private val reviewManager: ReviewManager by inject()

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

            ibLinkedInOgnjen.setOnClickListener {
                val page = Uri.parse(getString(R.string.ognjen_linkedin_url))
                startActivity(Intent(Intent.ACTION_VIEW, page))
            }

            tvRateApp.setOnClickListener {
                val reviewRequest = reviewManager.requestReviewFlow()
                reviewRequest.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val reviewInfo = task.result
                        val flow = reviewManager.launchReviewFlow(requireActivity(), reviewInfo)
                    }
                }
            }

            tvReportBug.setOnClickListener {
                val addresses =
                    arrayOf(getString(R.string.mail_nikola), getString(R.string.mail_ognjen))
                val intent = Intent(Intent.ACTION_SENDTO).apply {
                    data = Uri.parse("mailto:")
                    putExtra(Intent.EXTRA_EMAIL, addresses)
                    putExtra(Intent.EXTRA_SUBJECT, "Scannerate - Bug Report")
                }
                startActivity(intent)
            }

            tvSupport.setOnClickListener {
                findNavController().navigate(R.id.action_infoFragment_to_supportFragment)
            }

            tvShare.setOnClickListener {
                val shareIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(
                        Intent.EXTRA_TEXT,
                        "Checkout this text scanning app - Scannerate: https://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}"
                    )
                    type = "text/plain"
                }
                val intent = Intent.createChooser(shareIntent, null)
                startActivity(intent)
            }

            tvLicenses.setOnClickListener {
                findNavController().navigate(R.id.action_infoFragment_to_licencesFragment)
                reenterTransition = Fade()
            }

        }

    }
}