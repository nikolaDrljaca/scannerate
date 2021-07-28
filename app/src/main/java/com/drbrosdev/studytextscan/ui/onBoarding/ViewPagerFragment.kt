package com.drbrosdev.studytextscan.ui.onBoarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.drbrosdev.studytextscan.R
import com.drbrosdev.studytextscan.ui.onBoarding.screens.OnBoardingScreen1
import com.drbrosdev.studytextscan.ui.onBoarding.screens.OnBoardingScreen2
import com.drbrosdev.studytextscan.ui.onBoarding.screens.OnBoardingScreen3

/**
 * A simple [Fragment] subclass.
 * Use the [ViewPagerFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ViewPagerFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =  inflater.inflate(R.layout.fragment_view_pager, container, false)

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

        view.findViewById<ViewPager2>(R.id.viewPager).adapter = adapter

        return view
    }
}