package com.vroomvroom.fooddeliverys.view.ui.auth

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.navigation.fragment.findNavController
import com.google.firebase.FirebaseException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.vroomvroom.fooddeliverys.R
import com.vroomvroom.fooddeliverys.databinding.FragmentPhoneVerificationBinding
import com.vroomvroom.fooddeliverys.utils.Constants
import com.vroomvroom.fooddeliverys.utils.Utils.hideSoftKeyboard
import com.vroomvroom.fooddeliverys.view.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
@ExperimentalCoroutinesApi
class PhoneVerificationFragment : BaseFragment<FragmentPhoneVerificationBinding>(
    FragmentPhoneVerificationBinding::inflate
) {

    private var number: String? = null
    private lateinit var verificationIds: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        navController = findNavController()
        val currentBackStackEntry = navController.currentBackStackEntry
        val savedStateHandle = currentBackStackEntry?.savedStateHandle
        savedStateHandle?.getLiveData<Boolean>(Constants.SUCCESS)
            ?.observe(currentBackStackEntry) { isCancelled ->
                if (isCancelled) navController.popBackStack()
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.appBarLayout.toolbar.setupToolbar()

//        observeOtpGenerateConfirmation()

        binding.btnGetOtp.setOnClickListener {
            requireActivity().hideSoftKeyboard()
            val editTextValue = binding.phoneNumberEditTxt.text
            if (editTextValue.isNullOrBlank()) {
                showShortToast(getString(R.string.empty_number_message))
                return@setOnClickListener
            }
            number = "+52${editTextValue}"
            loadingDialog.show(getString(R.string.loading))
            binding.btnGetOtp.isEnabled = false
          try {
              val options = PhoneAuthOptions.newBuilder(Firebase.auth)
                  .setPhoneNumber(number!!)
                  .setTimeout(30L, TimeUnit.SECONDS)
                  .setActivity(this.requireActivity())
                  .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                      override fun onCodeSent(
                          verificationId: String,
                          forceResendingToken: PhoneAuthProvider.ForceResendingToken
                      ) {
                          // Save the verification id somewhere
                          // ...
                          verificationIds = verificationId
                          Log.v("verificationId", verificationId)
                          // The corresponding whitelisted code above should be used to complete sign-in.
                          loadingDialog.dismiss()
                          binding.btnGetOtp.isEnabled = true
                          authViewModel.resetOtpLiveData()
                          navController.navigate(PhoneVerificationFragmentDirections.
                          actionPhoneVerificationFragmentToCodeVerificationFragment(number.orEmpty(), verificationIds))
                      }

                      override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
                          // Sign in with the credential
                          // ...
                      }

                      override fun onVerificationFailed(e: FirebaseException) {
                          // ...
                          loadingDialog.dismiss()
                          binding.btnGetOtp.isEnabled = true
                          showShortToast(R.string.invalid_phone_number)
                      }
                  })
                  .build()
              PhoneAuthProvider.verifyPhoneNumber(options)
          } catch (e: Exception) {
              loadingDialog.dismiss()
              binding.btnGetOtp.isEnabled = true
              showShortToast(R.string.invalid_phone_number)
              Log.e("error", e.message.toString())
          }
//            authViewModel.registerPhoneNumber(number.orEmpty())
        }
    }

    private fun observeOtpGenerateConfirmation() {
//        authViewModel.isOtpSent.observe(viewLifecycleOwner) { response ->
//            when (response) {
//                is Resource.Loading -> {
//                    loadingDialog.show(getString(R.string.loading))
//                    binding.btnGetOtp.isEnabled = false
//                }
//                is Resource.Success -> {
//                    loadingDialog.dismiss()
//                    binding.btnGetOtp.isEnabled = true
//                    authViewModel.resetOtpLiveData()
//                    navController.navigate(PhoneVerificationFragmentDirections.
//                    actionPhoneVerificationFragmentToCodeVerificationFragment(number.orEmpty()))
//                }
//                is Resource.Error -> {
//                    loadingDialog.dismiss()
//                    binding.btnGetOtp.isEnabled = true
//                    showShortToast(R.string.invalid_phone_number)
//                }
//            }
//        }
    }

    override fun onResume() {
        super.onResume()
        authViewModel.resetPhoneRegistration()
    }


}