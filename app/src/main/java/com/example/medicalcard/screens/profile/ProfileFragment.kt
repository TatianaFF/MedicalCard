package com.example.medicalcard.screens.profile

import androidx.fragment.app.viewModels
import android.os.Bundle
import com.example.medicalcard.databinding.FragmentProfileBinding
import com.example.medicalcard.screens.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment : BaseFragment<FragmentProfileBinding>() {

    private val viewModel: ProfileViewModel by viewModels()

    override fun getViewBinding() = FragmentProfileBinding.inflate(layoutInflater)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Use the ViewModel
    }
}