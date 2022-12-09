package com.vroomvroom.fooddeliverys.view.ui.orders

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.vroomvroom.fooddeliverys.R
import com.vroomvroom.fooddeliverys.databinding.FragmentOrderDetailBinding
import com.vroomvroom.fooddeliverys.data.model.order.OrderDto
import com.vroomvroom.fooddeliverys.data.model.order.Status
import com.vroomvroom.fooddeliverys.utils.Constants.SUCCESS
import com.vroomvroom.fooddeliverys.utils.Constants.FORMAT_DD_MMM_YYYY_HH_MM_SS
import com.vroomvroom.fooddeliverys.utils.Utils.formatStringToDate
import com.vroomvroom.fooddeliverys.utils.Utils.safeNavigate
import com.vroomvroom.fooddeliverys.utils.Utils.toUppercase
import com.vroomvroom.fooddeliverys.view.resource.Resource
import com.vroomvroom.fooddeliverys.view.ui.base.BaseFragment
import com.vroomvroom.fooddeliverys.view.ui.orders.adapter.OrderProductAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class OrderDetailFragment : BaseFragment<FragmentOrderDetailBinding>(
    FragmentOrderDetailBinding::inflate
) {

    private val args: OrderDetailFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        navController = findNavController()
        val currentBackStackEntry = navController.currentBackStackEntry
        val savedStateHandle = currentBackStackEntry?.savedStateHandle
        savedStateHandle?.getLiveData<Boolean>(SUCCESS)
            ?.observe(currentBackStackEntry) { isCancelled ->
                if (isCancelled) navController.popBackStack()
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.appBarLayout.toolbar.apply {
            setupToolbar()
            setNavigationOnClickListener {
                if (prevDestinationId == R.id.commonCompleteFragment) {
                    navController.safeNavigate(R.id.action_orderDetailFragment_to_homeFragment)
                } else {
                    navController.popBackStack()
                }
            }
        }
        ordersViewModel.getOrder(args.id)
        observeOrder()
        observeReviewed()
        onBackPressed()

    }

    private fun observeOrder() {
        ordersViewModel.order.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Loading -> {
                    binding.orderDetailLayout.visibility = View.GONE
                    binding.shimmerLayout.visibility = View.VISIBLE
                    binding.shimmerLayout.startShimmer()
                }
                is Resource.Success -> {
                    binding.commonNoticeLayout.hideNotice()
                    binding.orderDetailLayout.visibility = View.VISIBLE
                    val order = response.data
                    ordersViewModel.merchantId = order.merchant!!._id
                    binding.statusTv.text = order.status!!.label
                        .toUppercase()
                        .replace(",", " ")
                    updateButtonModify(order)
                    updateViewsOnDataReady(order)
                    binding.orderMerchantLayout.setOnClickListener {
                        navController.navigate(
                            OrderDetailFragmentDirections.actionGlobalToMerchantFragment(
                                order.merchant._id
                            )
                        )
                    }
                }
                is Resource.Error -> {
                    binding.orderDetailLayout.visibility = View.GONE
                    binding.shimmerLayout.visibility = View.GONE
                    binding.shimmerLayout.stopShimmer()
                    binding.commonNoticeLayout.showNetworkError {
                        ordersViewModel.getOrder(args.id)
                    }
                }
            }
        }
    }

    private fun updateButtonModify(order: OrderDto) {
        when (order.status!!.ordinal) {
            Status.PENDING.ordinal -> {
                binding.btnModifyOrder.text = getString(R.string.cancel)
                binding.btnModifyOrder.setOnClickListener {
                    navController.navigate(
                        OrderDetailFragmentDirections.actionOrderDetailFragmentToCancelBottomSheetFragment(order._id)
                    )
                }
            }
            Status.CONFIRMED.ordinal -> {
                binding.btnModifyOrder.text = getString(R.string.change_address)
                binding.btnModifyOrder.setOnClickListener {
                    navController.navigate(
                        OrderDetailFragmentDirections.actionOrderDetailFragmentToAddressesFragment(order._id)
                    )
                }
            }
            Status.DELIVERED.ordinal -> {
                if (order.reviewed) {
                    binding.btnModifyOrder.visibility = View.GONE
                } else {
                    binding.btnModifyOrder.text = getString(R.string.write_review)
                    binding.btnModifyOrder.setOnClickListener {
                        reviewNavigation(order._id)
                    }
                    reviewNavigation(order._id)
                }
            }
            else -> {
                binding.btnModifyOrder.visibility = View.GONE
            }
        }
    }

    private fun observeReviewed() {
        mainActivityViewModel.reviewed.observe(viewLifecycleOwner) { reviewed ->
            if (reviewed) {
                binding.btnModifyOrder.visibility = View.GONE
            }
        }
    }

    private fun reviewNavigation(orderId: String) {
        navController.navigate(
            OrderDetailFragmentDirections
                .actionOrderDetailFragmentToReviewBottomSheetFragment(ordersViewModel.merchantId, orderId)
        )
    }

    private fun updateViewsOnDataReady(order: OrderDto) {
        binding.apply {
            this.order = order
            subTotalTv.text =
                getString(R.string.peso, "%.2f".format(order.order_detail!!.totalPrice))
            deliveryFee.text =
                getString(R.string.peso, "%.2f".format(order.order_detail.deliveryFee))
            val total = order.order_detail.totalPrice + order.order_detail.deliveryFee
            totalTv.text = getString(R.string.peso, "%.2f".format(total))
            orderProductRv.adapter = OrderProductAdapter(order.order_detail.products!!)
            placedDate.text =
                getString(R.string.placed_on,
                    formatStringToDate(order.created_at, FORMAT_DD_MMM_YYYY_HH_MM_SS))
            shimmerLayout.visibility = View.GONE
            shimmerLayout.stopShimmer()
        }
    }

    private fun onBackPressed() {
        requireActivity().onBackPressedDispatcher
            .addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (prevDestinationId == R.id.commonCompleteFragment) {
                        navController.safeNavigate(R.id.action_orderDetailFragment_to_homeFragment)
                    } else {
                        navController.popBackStack()
                    }
                }
            })
    }
}