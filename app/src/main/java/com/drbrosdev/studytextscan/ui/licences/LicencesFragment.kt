package com.drbrosdev.studytextscan.ui.licences

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.transition.Fade
import android.view.View
import androidx.fragment.app.Fragment
import com.drbrosdev.studytextscan.R
import com.drbrosdev.studytextscan.databinding.FragmentLicencesBinding
import com.drbrosdev.studytextscan.util.updateWindowInsets
import com.drbrosdev.studytextscan.util.viewBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class LicencesFragment : Fragment(R.layout.fragment_licences) {

    private val binding: FragmentLicencesBinding by viewBinding(FragmentLicencesBinding::bind)
    private val viewModel: LicenceViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateWindowInsets(binding.root)

        enterTransition = Fade()
        returnTransition = Fade()

        binding.apply {
            recyclerViewLicences.withModels {
                viewModel.allLicenceItems.forEach {
                    licenceListItem {
                        id("licence_list_item${it.order}")
                        licenceListItem(it)
                        onLicenceListItemClicked {
                            val intent = Intent(Intent.ACTION_VIEW).apply {
                                data = Uri.parse(it.link)
                            }
                            startActivity(intent)
                        }
                    }
                }
            }
        }
    }

}