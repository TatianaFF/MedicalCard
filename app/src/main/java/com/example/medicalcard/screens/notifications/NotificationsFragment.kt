package com.example.medicalcard.screens.notifications

import androidx.fragment.app.viewModels
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.medicalcard.R
import com.example.medicalcard.adapters.PastNotificationsAdapter
import com.example.medicalcard.databinding.FragmentNotificationsBinding
import com.example.medicalcard.screens.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class NotificationsFragment : BaseFragment<FragmentNotificationsBinding>() {

    private val viewModel: NotificationsViewModel by viewModels()

    override fun getViewBinding() = FragmentNotificationsBinding.inflate(layoutInflater)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        observeViewModel()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initValues()
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.notifications.collect { notifications ->
                        notifications?.let {
                            val pair = notifications.map { Pair(it.first, it.second) }
                            binding.rvNotifications.adapter = PastNotificationsAdapter(pair)
                        }
                    }
                }
            }
        }
    }

    private fun initValues() {
        val navHost = requireActivity().supportFragmentManager.findFragmentById(R.id.nav_fragment) as NavHostFragment
        val navController = navHost.navController

        initArrowBack(navController)
        initDeleteNotifications()
    }

    private fun initArrowBack(navController: NavController) {
        binding.btnBack.setOnClickListener {
            navController.navigateUp()
        }
    }

    private fun initDeleteNotifications() {
        binding.btnClear.setOnClickListener {
            viewModel.deleteNotifications()
        }
    }
}