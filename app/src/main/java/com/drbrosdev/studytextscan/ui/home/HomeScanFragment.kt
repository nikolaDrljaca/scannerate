package com.drbrosdev.studytextscan.ui.home

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.drbrosdev.studytextscan.R
import com.drbrosdev.studytextscan.databinding.FragmentScanHomeBinding
import com.drbrosdev.studytextscan.persistence.repository.ScanRepository
import com.drbrosdev.studytextscan.service.pdfExport.PdfExportServiceImpl
import com.drbrosdev.studytextscan.util.createLoadingDialog
import com.drbrosdev.studytextscan.util.getColor
import com.drbrosdev.studytextscan.util.updateWindowInsets
import com.drbrosdev.studytextscan.util.viewBinding
import com.google.android.material.transition.MaterialSharedAxis
import org.koin.android.ext.android.inject

class HomeScanFragment : Fragment(R.layout.fragment_scan_home) {
    private val binding: FragmentScanHomeBinding by viewBinding()
    private val pdfExportService: PdfExportServiceImpl by inject()
    private val scanRepository: ScanRepository by inject()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        /*
        App draws UI behind system bars (status bar, navigation bar).
        This is to update padding to screen elements to fit regardless of system bar sizes.

        This is necessary in every fragment.
         */
        updateWindowInsets(binding.root)
        /*
        Set nav bar color back to transparent
         */
        requireActivity().window.navigationBarColor = getColor(android.R.color.transparent)

        binding.apply {
            imageViewInfo.setOnClickListener {
                //enter exit like activity
                exitTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true)
                reenterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)

                findNavController().navigate(R.id.action_homeScanFragment_to_infoFragment)
            }

            imageViewSettings.setOnClickListener {
                /*
                Only Testing purposes
                 */
                val loadingDialog = createLoadingDialog()
                loadingDialog.show()
            }

            buttonCreateScan.setOnClickListener {
                //enter/exit from the side
                exitTransition = MaterialSharedAxis(MaterialSharedAxis.X, true)
                reenterTransition = MaterialSharedAxis(MaterialSharedAxis.X, false)

                findNavController().navigate(R.id.action_homeScanFragment_to_detailScanFragment)

                /*
                Code above is just a placeholder for initial testing,
                create button should
                    -launch image request
                    -show loading dialog while processing
                    -once processing is finished, insert into db and save id (db insert returns this as long)
                    -send an event after all is done to navigate to detail screen
                 */
            }

            /*
            todo set layout animator to ListAdapter
            recyclerView.layoutAnimation = AnimationUtils.loadLayoutAnimation(requireContext(), R.anim.layout_anim)

            todo also dont forget to add
            postponeEnterTransition()
            view.doOnPreDraw{ startPostponedEnterTransition() }
             */

            /*
            todo set padding on list items
            recyclerView.addItemDecoration(MarginItemDecoration(resources.getDimensionPixelSize(R.dimen.list_item_padding)
             */

            //temporary for testing purposes
            recyclerViewScans.isVisible = false
            /*
            Sets the animation to loop only 3 times and then stop as to not be too annoying.
             */
            animationView.repeatCount = 3
        }
    }
}