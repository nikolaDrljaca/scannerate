package com.drbrosdev.studytextscan.ui.home

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.drbrosdev.studytextscan.R
import com.drbrosdev.studytextscan.databinding.FragmentScanHomeBinding
import com.drbrosdev.studytextscan.util.getColor
import com.drbrosdev.studytextscan.util.updateWindowInsets
import com.drbrosdev.studytextscan.util.viewBinding
import com.google.android.material.transition.MaterialSharedAxis

class HomeScanFragment: Fragment(R.layout.fragment_scan_home) {
    private val binding: FragmentScanHomeBinding by viewBinding()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        /*
        App draws UI behind system bars (status bar, navigation bar).
        This is to update padding to screen elements to fit regardless of system bar sizes.

        This is necessary in every fragment.
         */
        updateWindowInsets(binding.root)
        requireActivity().window.navigationBarColor = getColor(android.R.color.transparent)

        binding.apply {
            imageViewInfo.setOnClickListener {
                //enter exit like activity
                exitTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true)
                reenterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)

                findNavController().navigate(R.id.action_homeScanFragment_to_infoFragment)
            }

            buttonCreateScan.setOnClickListener {
                //enter/exit from the side
                exitTransition = MaterialSharedAxis(MaterialSharedAxis.X, true)
                reenterTransition = MaterialSharedAxis(MaterialSharedAxis.X, false)

                findNavController().navigate(R.id.action_homeScanFragment_to_detailScanFragment)
            }

            //temporary for testing purposes
            recyclerViewScans.isVisible = false
        }
    }
}