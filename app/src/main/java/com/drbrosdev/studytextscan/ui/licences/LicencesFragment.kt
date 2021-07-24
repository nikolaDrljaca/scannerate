package com.drbrosdev.studytextscan.ui.licences

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.transition.Fade
import com.drbrosdev.studytextscan.R
import com.drbrosdev.studytextscan.databinding.FragmentLicencesBinding
import com.drbrosdev.studytextscan.ui.util.UserInterfaceUtil.Companion.allLicenceItems
import com.drbrosdev.studytextscan.util.updateWindowInsets
import com.drbrosdev.studytextscan.util.viewBinding

class LicencesFragment : Fragment(R.layout.fragment_licences) {

    private val binding: FragmentLicencesBinding by viewBinding()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateWindowInsets(binding.root)

        enterTransition = Fade()
        exitTransition = Fade()

        //TODO check!!!
        binding.apply {
            recyclerViewLicences.withModels {
                licenceListItem {
                    id("licence_list_item")
                    allLicenceItems.forEach { licenceListItem ->
                        licenceListItem(licenceListItem)
                    }
                }
            }
        }
    }

}