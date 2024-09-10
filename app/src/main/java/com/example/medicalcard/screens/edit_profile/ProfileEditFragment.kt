package com.example.medicalcard.screens.edit_profile

import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.medicalcard.R
import com.example.medicalcard.databinding.FragmentEditProfileBinding
import com.example.medicalcard.screens.BaseFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProfileEditFragment : BaseFragment<FragmentEditProfileBinding>() {
    override fun getViewBinding() = FragmentEditProfileBinding.inflate(layoutInflater)

    private val viewModel: ProfileEditViewModel by viewModels()

    private val idProfile: Long?
        get() = arguments?.let { ProfileEditFragmentArgs.fromBundle(it).idProfile }?.toLongOrNull()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setDataProfile()
        initValues()
        observeViewModel()
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.nameProfile.collect { name ->
                        name?.let {
                            binding.editNamePerson.setText(it)
                        }
                    }
                }
            }
        }

//        lifecycleScope.launch {
//            repeatOnLifecycle(Lifecycle.State.STARTED) {
//                viewModel.idProfile.collect { id ->
//                    id?.let {
//                        Log.e("id", it.toString())
////                        viewModel.fetchProfileData(it)
//                    }
//                }
//            }
//        }
    }

    private fun setDataProfile() {
        idProfile?.let {
            try {
                viewModel.fetchProfileData(it)
            } catch (e: Exception) {
                Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
            }
            binding.topAppBar.setTitle(getString(R.string.edit_profile_screen))
        }
    }

    private fun initValues() {
        val navHost = requireActivity().supportFragmentManager.findFragmentById(R.id.nav_fragment) as NavHostFragment
        val navController = navHost.navController

        initWelcome()
        initSavePersonBtn(navController)
        initDeleteProfileBtn(navController)
        initCloseBtn(navController)
    }

    private fun initWelcome() {
        if (viewModel.isFirstAppStart()) {
            binding.tvWelcome.visibility = View.VISIBLE
            binding.topAppBar.visibility =View.GONE
        } else {
            binding.tvWelcome.visibility = View.GONE
            binding.topAppBar.visibility =View.VISIBLE
        }
    }

    private fun initCloseBtn(navController: NavController) {
        binding.topAppBar.setNavigationOnClickListener { navController.navigateUp() }
    }

    private fun initSavePersonBtn(navController: NavController) {
        binding.btnSavePerson.setOnClickListener {
            viewModel.setNameProfile(binding.editNamePerson.text.toString())
            try {
                if (idProfile == null) {
                    viewModel.insertProfile()
                } else viewModel.updateProfile(
                    requireNotNull(idProfile) { "Ошибка при определении идентификатора" }
                )
                navController.navigateUp()
            } catch (e: Exception) {
                Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun initDeleteProfileBtn(navController: NavController) {
        idProfile?.let {  id ->
            if (id != 0L) {
                binding.btnDeleteProfile.visibility = View.VISIBLE
                binding.btnDeleteProfile.setOnClickListener {
                    MaterialAlertDialogBuilder(requireContext())
                        .setTitle("Удалить профиль")
                        .setMessage("При удалении профиля также будут удалены все визиты этого профиля. Вы действительно хотите удалить профиль? ")
                        .setNegativeButton("Отмена") { dialog, which ->
                            dialog.dismiss()
                        }
                        .setPositiveButton("Удалить") { dialog, which ->
                            viewModel.deleteProfileById(id)
                            viewModel.setCurrentProfile(0)
                            navController.navigateUp()
                        }
                        .show()
                }
            } else {
                binding.btnDeleteProfile.visibility = View.GONE
            }
        }
    }
}