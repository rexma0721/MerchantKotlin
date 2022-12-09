package com.vroomvroom.fooddeliverys.view.ui.orders

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.navArgs
import com.vroomvroom.fooddeliverys.R
import com.vroomvroom.fooddeliverys.databinding.FragmentReviewBottomSheetBinding
import com.vroomvroom.fooddeliverys.utils.ClickType
import com.vroomvroom.fooddeliverys.view.resource.Resource
import com.vroomvroom.fooddeliverys.view.ui.base.BaseBottomSheetFragment
import com.vroomvroom.fooddeliverys.view.ui.common.CommonAlertDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class ReviewBottomSheetFragment : BaseBottomSheetFragment<FragmentReviewBottomSheetBinding>(
    FragmentReviewBottomSheetBinding::inflate
) {

    private val args: ReviewBottomSheetFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeReviewLiveData()

        binding.btnSubmit.setOnClickListener {
            val rate = binding.ratingBar.rating.toInt()
            val comment = binding.inputEditText.text?.toString().orEmpty()
            ordersViewModel.createReview(args.orderId, args.merchantId, rate, comment)
        }
    }

    private fun observeReviewLiveData() {
        ordersViewModel.isReviewCreated.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Loading -> {
                    isCancelable = false
                    binding.progressIndicator.visibility = View.VISIBLE
                    binding.btnSubmit.isEnabled = false
                }
                is Resource.Success -> {
                    mainActivityViewModel.reviewed.postValue(true)
                    dismiss()
                }
                is Resource.Error -> {
                    binding.progressIndicator.visibility = View.GONE
                    binding.btnSubmit.isEnabled = true
                    isCancelable = true
                    initAlertDialog()
                }
            }
        }
    }

    private fun initAlertDialog() {
        val dialog = CommonAlertDialog(
            requireActivity()
        )
        dialog.show(
            getString(R.string.network_error),
            getString(R.string.network_error_message),
            getString(R.string.cancel),
            getString(R.string.retry)
        ) { type ->
            when (type) {
                ClickType.POSITIVE -> {
                    val rate = binding.ratingBar.rating.toInt()
                    val comment = binding.inputEditText.text?.toString().orEmpty()
                    ordersViewModel.createReview(args.orderId, args.merchantId, rate, comment)
                    dialog.dismiss()
                }
                ClickType.NEGATIVE -> dialog.dismiss()
            }
        }
    }

}