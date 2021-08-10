package com.drbrosdev.studytextscan.ui.onBoarding.screens

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.drbrosdev.studytextscan.R
import com.drbrosdev.studytextscan.databinding.FragmentOnBoardingScreen2Binding
import com.drbrosdev.studytextscan.util.updateWindowInsets

class OnBoardingScreen2 : Fragment(R.layout.fragment_on_boarding_screen2) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentOnBoardingScreen2Binding.bind(view)
        updateWindowInsets(binding.root)

        val viewPager = requireActivity().findViewById<ViewPager2>(R.id.view_pager)

        binding.apply {
            animationView.repeatCount = 2

            buttonNext.setOnClickListener {
                viewPager.currentItem = 2
            }

            buttonPrevious.setOnClickListener {
                viewPager.currentItem = 0
            }
        }
    }
}