package com.drbrosdev.studytextscan.ui.home

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Canvas
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import android.view.animation.AnimationUtils
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.doOnPreDraw
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.epoxy.EpoxyTouchHelper
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.drbrosdev.studytextscan.R
import com.drbrosdev.studytextscan.databinding.FragmentScanHomeBinding
import com.drbrosdev.studytextscan.ui.home.reward.setupComposeSnackbar
import com.drbrosdev.studytextscan.util.*
import com.google.android.material.transition.MaterialSharedAxis
import com.google.mlkit.vision.common.InputImage
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.activityViewModel

class HomeScanFragment : Fragment(R.layout.fragment_scan_home) {
    private val binding: FragmentScanHomeBinding by viewBinding(FragmentScanHomeBinding::bind)
    private val viewModel: HomeViewModel by activityViewModel()
//    private val billingClient: BillingClientService by inject()

    private val selectImageRequest = registerForActivityResult(CropImageContract()) {
        if (it.isSuccessful) {
            it.uriContent?.let { handleImage(it) }
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (it) {
                //permission granted. Continue workflow
                //selectImageRequest.launch(cropImageCameraOptions)
                selectImageRequest.launch(cropImageCameraOptions)
            } else {
                //Provide explanation on why the permission is needed. AlertDialog maybe?
                viewModel.handlePermissionDenied()
            }
        }

    private val cropImageGalleryOptions = CropImageContractOptions(
        null,
        CropImageOptions().apply {
            imageSourceIncludeCamera = false
            imageSourceIncludeGallery = true
        })

    private val cropImageCameraOptions = CropImageContractOptions(
        null,
        CropImageOptions().apply {
            imageSourceIncludeCamera = true
            imageSourceIncludeGallery = false
        })

    override fun onResume() {
        super.onResume()
//        billingClient.retryToConsumePurchases()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateWindowInsets(binding.root)
        viewModel.onHomeFrag()
        postponeEnterTransition()
        view.doOnPreDraw { startPostponedEnterTransition() }

        /*
        collectFlow(billingClient.purchaseFlow) {
            when (it) {
                is PurchaseResult.Failure -> Unit
                is PurchaseResult.Success -> viewModel.showReward()
            }
        }
         */

        collectFlow(viewModel.state) { state ->
            binding.apply {
                linearLayoutEmpty.isVisible = state.isEmpty

                recyclerViewScans.withModels {
                    scanTopBar {
                        id("scan_top_bar")
                        onInfoClicked {
                            exitTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true)
                            reenterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)

                            findNavController().navigate(R.id.to_about_fragment).also {
                                viewModel.moveAwayFromScreen()
                            }
                        }
                    }
                    scanHeader {
                        id("scan_header")
                        numOfScans(getString(R.string.num_of_scans, state.itemCount))
                    }

                    if (state.isLoading)
                        loadingScans { id("loading_scans") }

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
                                    reenterTransition =
                                        MaterialSharedAxis(MaterialSharedAxis.X, false)
                                    //val arg = bundleOf("scan_id" to it.scanId.toInt())
                                    val action = HomeScanFragmentDirections.toDetailScanFragment(it.scanId.toInt(), 0)
                                    findNavController().safeNav(action).also {
                                        viewModel.moveAwayFromScreen()
                                    }
                                }
                            }
                        }
                    }

                    if (state.scans.isNotEmpty()) {
                        listHeader {
                            id("others_header")
                            headerTitle(getString(R.string.headers_other))
                        }
                        state.scans.forEach {
                            scanListItem {
                                id(it.scanId)
                                scan(it)
                                onScanClicked {
                                    exitTransition = MaterialSharedAxis(MaterialSharedAxis.X, true)
                                    reenterTransition =
                                        MaterialSharedAxis(MaterialSharedAxis.X, false)
                                    val action = HomeScanFragmentDirections.toDetailScanFragment(it.scanId.toInt(), 0)
                                    findNavController().safeNav(action).also {
                                        viewModel.moveAwayFromScreen()
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        binding.apply {
            recyclerViewScans.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    if (dy > 0) {
                        buttonCameraScan.hide()
                        buttonGalleryScan.hide()
                    }
                    if (dy < 0) {
                        buttonCameraScan.show()
                        buttonGalleryScan.show()
                    }
                }
            })

            val delete = ContextCompat.getDrawable(
                requireContext(),
                R.drawable.ic_round_delete_white_24
            )
            EpoxyTouchHelper.initSwiping(recyclerViewScans)
                .left()
                .withTarget(ScanListItemEpoxyModel::class.java)
                .andCallbacks(object : EpoxyTouchHelper.SwipeCallbacks<ScanListItemEpoxyModel>() {
                    override fun onSwipeCompleted(
                        model: ScanListItemEpoxyModel?,
                        itemView: View?,
                        position: Int,
                        direction: Int
                    ) {
                        model?.let {
                            viewModel.deleteScan(it.scan)
                        }
                    }

                    override fun onSwipeProgressChanged(
                        model: ScanListItemEpoxyModel?,
                        itemView: View?,
                        swipeProgress: Float,
                        canvas: Canvas?
                    ) {
                        itemView?.let { view ->
                            view.alpha = swipeProgress + 1
                            val itemHeight = view.bottom - view.top
                            delete?.setTint(getColor(R.color.error_red))

                            val iconTop = view.top + (itemHeight - delete!!.intrinsicHeight) / 2
                            val iconMargin = (itemHeight - delete.intrinsicHeight) / 2
                            val iconLeft = view.right - iconMargin - delete.intrinsicWidth
                            val iconRight = view.right - iconMargin
                            val iconBottom = iconTop + delete.intrinsicHeight

                            delete.setBounds(iconLeft, iconTop, iconRight, iconBottom)
                            delete.draw(canvas!!)
                        }
                    }
                })
        }

        collectFlow(viewModel.events) { homeEvents ->
            when (homeEvents) {
                is HomeEvents.ShowCurrentScanSaved -> {
                    val action = HomeScanFragmentDirections.toDetailScanFragment(homeEvents.id, 1)
                    findNavController().safeNav(action)
                }
                is HomeEvents.ShowLoadingDialog -> {
                    binding.cardViewLoading.animate().translationX(0f)
                    binding.buttonCameraScan.isEnabled = false
                    binding.buttonGalleryScan.isEnabled = false
                }
                is HomeEvents.ShowScanEmpty -> {
                    showSnackbarShort(
                        message = getString(R.string.no_text_found),
                        anchor = binding.buttonCameraScan
                    )
                    binding.cardViewLoading.animate().translationX(-1000f)
                    binding.buttonCameraScan.isEnabled = true
                    binding.buttonGalleryScan.isEnabled = true
                }
                is HomeEvents.ShowUndoDeleteScan -> {
                    showSnackbarLongWithAction(
                        message = getString(R.string.scan_deleted),
                        anchor = binding.buttonCameraScan,
                        actionText = getString(R.string.undo)
                    ) {
                        viewModel.insertScan(homeEvents.scan)
                    }
                }
                is HomeEvents.ShowOnboarding -> {
                    findNavController().navigate(R.id.action_homeScanFragment_to_viewPagerFragment)
                }
                is HomeEvents.ShowErrorWhenScanning -> {
                    showSnackbarShort(
                        message = getString(R.string.something_went_wrong),
                        anchor = binding.buttonCameraScan
                    )
                }
                is HomeEvents.ShowPermissionInfo -> {
                    showCameraPermissionInfoDialog()
                }
                is HomeEvents.ShowSupportDialog -> {
//                    findNavController().navigate(R.id.action_homeScanFragment_to_supportFragment)
                }
            }
        }

        binding.setupComposeSnackbar(
            rewardCount = viewModel.showReward,
            onRewardShown = { viewModel.rewardShown() }
        )

        binding.apply {
            recyclerViewScans.apply {
                layoutAnimation =
                    AnimationUtils.loadLayoutAnimation(requireContext(), R.anim.layout_anim)

                buttonCameraScan.setOnClickListener {
                    exitTransition = MaterialSharedAxis(MaterialSharedAxis.X, true)
                    reenterTransition = MaterialSharedAxis(MaterialSharedAxis.X, false)

                    when {
                        ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                                == PackageManager.PERMISSION_GRANTED -> {
                            selectImageRequest.launch(cropImageCameraOptions)
                        }
                        else -> {
                            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
                        }
                    }
                }

                buttonGalleryScan.setOnClickListener {
                    exitTransition = MaterialSharedAxis(MaterialSharedAxis.X, true)
                    reenterTransition = MaterialSharedAxis(MaterialSharedAxis.X, false)
                    selectImageRequest.launch(cropImageGalleryOptions)
                }
                animationView.repeatCount = 2
            }
        }

        requireActivity().apply {
            if (intent.action == Intent.ACTION_SEND) {
                if (intent.type?.startsWith("image") == true) {
                    handleIntent(intent)
                }
            }
        }
    }

    private fun handleIntent(intent: Intent) {
        (intent.getParcelableExtra<Parcelable>(Intent.EXTRA_STREAM) as? Uri)?.let {
            handleImage(it)
        }
        /*
        After the intent is handled, set the action to "" so it does not trigger again.
         */
        intent.action = ""
    }

    private fun handleImage(uri: Uri) {
        val image = InputImage.fromFilePath(requireContext(), uri)
        viewModel.handleScan(image)
    }
}