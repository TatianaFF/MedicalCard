package com.example.medicalcard.screens.profile_dialog

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.graphics.drawable.toDrawable
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.medicalcard.R
import com.example.medicalcard.adapters.ProfilesAdapter
import com.example.medicalcard.databinding.FragmentProfileDialogBinding
import com.example.medicalcard.screens.BaseBottomSheetDialogFragment
import com.example.models.Profile
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.launch


class ProfileDialogFragment(
    private val idCurrentProfile: Long?,
    private val profiles: List<Profile>,
    private val onClickAllShowVisits: () -> Unit
) : BaseBottomSheetDialogFragment<FragmentProfileDialogBinding>() {

    override fun getViewBinding() = FragmentProfileDialogBinding.inflate(layoutInflater)

    private val viewModel: ProfileDialogViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.setCurrentProfile(idCurrentProfile)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navHost = requireActivity().supportFragmentManager.findFragmentById(R.id.nav_fragment) as NavHostFragment
        val navController = navHost.navController

        initCreateProfileBtn(navController)
        initShowAllVisits()
        observeViewModel()
    }

    private fun initCreateProfileBtn(navController: NavController) {
        binding.btnCreateProfile.setOnClickListener {
            navController.navigate(R.id.editProfileFragment)
            dismiss()
        }
    }

    private fun initShowAllVisits() {
        binding.tvAllVisits.setOnClickListener {
            viewModel.setCurrentProfile(null)
            onClickAllShowVisits()
        }
    }

    private fun observeViewModel(){
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.currentProfile.collect { idCur ->
                        if (idCur == null) binding.tvAllVisits.setBackgroundResource(R.drawable.rounded_edittext) else binding.tvAllVisits.setBackgroundColor(Color.TRANSPARENT)
                        binding.rvProfiles.adapter = ProfilesAdapter(idCur, profiles, ::onClickProfile, ::onClickEditProfile)
                    }
                }
            }
        }
    }

    private fun onClickProfile(id: Long) {
        viewModel.setCurrentProfile(id)
        setFragmentResult(REQUEST_KEY_SELECT, bundleOf(ID_PROFILE to id))
    }

    private fun onClickEditProfile(id: Long) {
        setFragmentResult(REQUEST_KEY_EDIT, bundleOf(ID_PROFILE to id))
        dismiss()
    }

    companion object {
        const val TAG = "ProfileDialogFragment"
        const val REQUEST_KEY_SELECT = "REQUEST_KEY_SELECT"
        const val REQUEST_KEY_EDIT = "REQUEST_KEY_EDIT"
        const val ID_PROFILE = "ID_PROFILE"
    }
}