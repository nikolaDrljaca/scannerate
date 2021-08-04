package com.drbrosdev.studytextscan.ui.onBoarding

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.drbrosdev.studytextscan.R
import com.drbrosdev.studytextscan.databinding.FragmentViewPagerBinding
import com.drbrosdev.studytextscan.ui.onBoarding.screens.OnBoardingScreen1
import com.drbrosdev.studytextscan.ui.onBoarding.screens.OnBoardingScreen2
import com.drbrosdev.studytextscan.ui.onBoarding.screens.OnBoardingScreen3

/**
 * A simple [Fragment] subclass.
 * Use the [ViewPagerFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ViewPagerFragment : Fragment(R.layout.fragment_view_pager) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentViewPagerBinding.bind(view)

        val fragmentList = arrayListOf(
            OnBoardingScreen1(),
            OnBoardingScreen2(),
            OnBoardingScreen3()
        )

        val adapter = ViewPagerAdapter(
            fragmentList,
            requireActivity().supportFragmentManager,
            lifecycle
        )

        binding.apply {
            viewPager.adapter = adapter
        }
    }
}