package com.drbrosdev.studytextscan.ui.home

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import androidx.activity.result.contract.ActivityResultContracts.GetContent
import androidx.core.os.bundleOf
import androidx.core.view.doOnPreDraw
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.drbrosdev.studytextscan.R
import com.drbrosdev.studytextscan.databinding.FragmentScanHomeBinding
import com.drbrosdev.studytextscan.service.textFilter.FilterTextServiceImpl
import com.drbrosdev.studytextscan.util.collectFlow
import com.drbrosdev.studytextscan.util.collectStateFlow
import com.drbrosdev.studytextscan.util.createLoadingDialog
import com.drbrosdev.studytextscan.util.getColor
import com.drbrosdev.studytextscan.util.showSnackbarShort
import com.drbrosdev.studytextscan.util.updateWindowInsets
import com.drbrosdev.studytextscan.util.viewBinding
import com.google.android.material.transition.MaterialSharedAxis
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizerOptions
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeScanFragment : Fragment(R.layout.fragment_scan_home) {
    private val binding: FragmentScanHomeBinding by viewBinding()
    private val viewModel: HomeViewModel by viewModel()

    private val selectImageRequest = registerForActivityResult(GetContent()) { uri ->
        if (uri != null) {
            viewModel.showLoadingDialog()
            scanText(uri) { scannedText, filteredTextList ->
                viewModel.createScan(scannedText, filteredTextList)
            }
        }
    }

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
        /*
        Wait to animate recyclerView until the fragments view has been inflated.
         */
        postponeEnterTransition()
        view.doOnPreDraw { startPostponedEnterTransition() }
        /*
        Create loading dialog.
         */
        val loadingDialog = createLoadingDialog()

        collectStateFlow(viewModel.viewState) { state ->
            binding.apply {
                linearLayoutEmpty.isVisible = state.isEmpty

                recyclerViewScans.withModels {
                    scanTopBar {
                        id("scan_top_bar")
                        onInfoClicked {
                            exitTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true)
                            reenterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)

                            findNavController().navigate(R.id.action_homeScanFragment_to_infoFragment)
                        }
                        onPdfListClicked {

                        }
                    }
                    scanHeader {
                        id("scan_header")
                        numOfScans(getString(R.string.num_of_scans, state.itemCount))
                    }

                    if (state.pinnedScans.isNotEmpty()) {
                        listHeader {
                            id("pinned_header")
                            headerTitle(getString(R.string.header_pinned))
                        }
                        state.pinnedScans.forEach {
                            scanListItem {
                                id(it.scanId)
                                scan(it)
                                onScanClicked {
                                    exitTransition = MaterialSharedAxis(MaterialSharedAxis.X, true)
                                    reenterTransition = MaterialSharedAxis(MaterialSharedAxis.X, false)
                                    val arg = bundleOf("scan_id" to it.scanId.toInt())
                                    findNavController().navigate(
                                        R.id.action_homeScanFragment_to_detailScanFragment,
                                        arg
                                    )
                                }
                            }
                        }
                    }

                    if (state.otherScans.isNotEmpty()) {
                        listHeader {
                            id("others_header")
                            headerTitle(getString(R.string.headers_other))
                        }
                        state.otherScans.forEach {
                            scanListItem {
                                id(it.scanId)
                                scan(it)
                                onScanClicked {
                                    exitTransition = MaterialSharedAxis(MaterialSharedAxis.X, true)
                                    reenterTransition = MaterialSharedAxis(MaterialSharedAxis.X, false)
                                    val arg = bundleOf("scan_id" to it.scanId.toInt())
                                    findNavController().navigate(
                                        R.id.action_homeScanFragment_to_detailScanFragment,
                                        arg
                                    )
                                }
                            }
                        }
                    }
                }

                /*
                Scrolling listener to hide the create scan FAB
                 */
                recyclerViewScans.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                        if (dy > 0) {
                            buttonCreateScan.hide()
                        }
                        if (dy < 0) { buttonCreateScan.show() }
                    }
                })
            }
        }

        collectFlow(viewModel.events) { homeEvents ->
            when (homeEvents) {
                is HomeEvents.ShowCurrentScanSaved -> {
                    loadingDialog.hide()
                    val arg = bundleOf("scan_id" to homeEvents.id, "is_created" to 1)
                    findNavController().navigate(
                        R.id.action_homeScanFragment_to_detailScanFragment,
                        arg
                    )
                }
                is HomeEvents.ShowLoadingDialog -> {
                    loadingDialog.show()
                }
                is HomeEvents.ShowScanEmpty -> {
                    loadingDialog.hide()
                    showSnackbarShort(
                        message = getString(R.string.no_text_found),
                        anchor = binding.buttonCreateScan
                    )
                }
            }
        }

        /*
        RecyclerView setup
         */
        binding.apply {
            recyclerViewScans.apply {
                /*
                Gives an animation to all list items individually
                 */
                layoutAnimation =
                    AnimationUtils.loadLayoutAnimation(requireContext(), R.anim.layout_anim)

                buttonCreateScan.setOnClickListener {
                    exitTransition = MaterialSharedAxis(MaterialSharedAxis.X, true)
                    reenterTransition = MaterialSharedAxis(MaterialSharedAxis.X, false)
                    /*
                    Code above is just a placeholder for initial testing,
                    create button should
                        -launch image request
                        -show loading dialog while processing
                        -once processing is finished, insert into db and save id (db insert returns this as long)
                        -send an event after all is done to navigate to detail screen
                     */

                    selectImageRequest.launch("image/*")
                }
                /*
                Sets the animation to loop only 3 times and then stop as to not be too annoying.
                 */
                animationView.repeatCount = 2
            }
        }
    }

    private fun scanText(uri: Uri, action: (String, List<Pair<String, String>>) -> Unit) {
        val completeText = StringBuilder()
        val filterService: FilterTextServiceImpl by inject()
        val list = mutableListOf<Pair<String, String>>()
        try {
            val image = InputImage.fromFilePath(requireContext(), uri)
            val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

            recognizer.process(image)
                .addOnCompleteListener { task ->
                    val scannedText = task.result
                    for (block in scannedText.textBlocks) {
                        Log.d("DEBUGn", "scanText: block - ${block.text}")
                        completeText.append(block.text)
                        for (line in block.lines) {
                            Log.d("DEBUGn", "scanText: line - ${line.text}")
                            for (element in line.elements) {
                                Log.d("DEBUGn", "scanText: element - ${element.text}")
                                list.addAll(filterService.filterTextForEmails(element.text))
                                list.addAll(filterService.filterTextForPhoneNumbers(element.text))
                                list.addAll(filterService.filterTextForLinks(element.text))
                            }
                        }
                    }
                    action(completeText.toString(), list)
                }
                .addOnFailureListener { e -> throw e }
        } catch (e: Exception) {
            Log.e("DEBUGn", "scanText: ", e)
        }
    }
}