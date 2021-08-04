package com.drbrosdev.studytextscan.ui.onBoarding.screens

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.drbrosdev.studytextscan.R
import com.drbrosdev.studytextscan.databinding.FragmentOnBoardingScreen1Binding

class OnBoardingScreen1 : Fragment(R.layout.fragment_on_boarding_screen1) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentOnBoardingScreen1Binding.bind(view)

        val viewPager = requireActivity().findViewById<ViewPager2>(R.id.view_pager)

        binding.apply {
            button1.setOnClickListener {
                viewPager.currentItem = 1
            }
        }
    }

}