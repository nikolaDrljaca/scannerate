package com.drbrosdev.studytextscan.ui.onBoarding.screens

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.drbrosdev.studytextscan.R
import com.drbrosdev.studytextscan.databinding.FragmentOnBoardingScreen3Binding

class OnBoardingScreen3 : Fragment(R.layout.fragment_on_boarding_screen3) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentOnBoardingScreen3Binding.bind(view)

        binding.apply {
            button3.setOnClickListener {

            }
        }
    }

    private fun onBoardingFinished() {
        val sharedPref = requireActivity().getSharedPreferences("onBoarding", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putBoolean("finished", true)
        editor.apply()
    }
}