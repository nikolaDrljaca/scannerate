package com.drbrosdev.studytextscan.ui.detailscan

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.drbrosdev.studytextscan.R
import com.drbrosdev.studytextscan.databinding.FragmentScanDetailBinding
import com.drbrosdev.studytextscan.util.getColor
import com.drbrosdev.studytextscan.util.updateWindowInsets
import com.drbrosdev.studytextscan.util.viewBinding
import com.google.android.material.transition.MaterialSharedAxis

class DetailScanFragment: Fragment(R.layout.fragment_scan_detail) {
    private val binding: FragmentScanDetailBinding by viewBinding()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.X, true)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.X, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateWindowInsets(binding.root)
        /*
        Set color of navbar to match the bottom bar color.
         */
        requireActivity().window.navigationBarColor = getColor(R.color.bottom_bar_color)

        binding.apply {
            imageViewBack.setOnClickListener {
                findNavController().navigateUp()
            }
        }

    }
}