package com.example.medicalcard.screens.visits

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.medicalcard.R
import com.example.medicalcard.adapters.VisitsAdapter
import com.example.medicalcard.databinding.FragmentVisitsBinding
import com.example.medicalcard.screens.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class VisitsFragment : BaseFragment<FragmentVisitsBinding>() {

    private val viewModel: VisitsViewModel by viewModels()

    override fun getViewBinding() = FragmentVisitsBinding.inflate(layoutInflater)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fetchVisits()
        observeViewModel()
    }

    private fun fetchVisits() {
        try {
            viewModel.fetchVisits()
        } catch (e: Exception) {
            Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initValues()
    }

    private fun initValues() {
        val navHost = requireActivity().supportFragmentManager.findFragmentById(R.id.nav_fragment) as NavHostFragment
        val navController = navHost.navController

        initSearchEdit()
        initCreateVisitBtn(navController)
    }

    private fun initCreateVisitBtn(navController: NavController) {
        binding.btnCreateVisit.setOnClickListener {
            navController.navigate(VisitsFragmentDirections.actionVisitsFragmentToVisitEditFragment())
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.visits.collect { visits ->
                    binding.rv.adapter = VisitsAdapter(visits, ::onClickVisit)
                }
            }
        }
    }

    private fun onClickVisit(id: Long) {
        val navHost = requireActivity().supportFragmentManager.findFragmentById(R.id.nav_fragment) as NavHostFragment
        val navController = navHost.navController
        navController.navigate(VisitsFragmentDirections.actionVisitsFragmentToVisitEditFragment(id.toString()))
    }

    private fun initSearchEdit() {
        binding.editSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                viewModel.setSearchText(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        });
    }
}