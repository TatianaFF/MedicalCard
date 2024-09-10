package com.example.medicalcard.screens.visits

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.medicalcard.R
import com.example.medicalcard.adapters.ItemVisit
import com.example.medicalcard.adapters.VisitsAdapter
import com.example.medicalcard.databinding.FragmentVisitsBinding
import com.example.medicalcard.screens.BaseFragment
import com.example.medicalcard.screens.filter_dialog.FilterDialogFragment
import com.example.medicalcard.screens.profile_dialog.ProfileDialogFragment
import com.example.medicalcard.utils.getDateShort
import com.example.models.Visit
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset


@AndroidEntryPoint
class VisitsFragment : BaseFragment<FragmentVisitsBinding>() {

    private val viewModel: VisitsViewModel by viewModels()

    override fun getViewBinding() = FragmentVisitsBinding.inflate(layoutInflater)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        observeViewModel()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navHost = requireActivity().supportFragmentManager.findFragmentById(R.id.nav_fragment) as NavHostFragment
        val navController = navHost.navController

        initNotificationsBtn(navController)
        initFilterBtn()
        initSelectProfile()
        initSearchEdit()
        initCreateVisitBtn(navController)
        initClearFilterBtn()
    }

    override fun onResume() {
        super.onResume()

        viewModel.updateCurrentProfile()
    }

    private fun initNotificationsBtn(navController: NavController) {
        binding.btnNotifications.setOnClickListener { navController.navigate(VisitsFragmentDirections.actionVisitsFragmentToNotificationsFragment()) }
    }

    private fun initCreateVisitBtn(navController: NavController) {
        binding.btnCreateVisit.setOnClickListener {
            navController.navigate(VisitsFragmentDirections.actionVisitsFragmentToVisitEditFragment(idProfile = viewModel.currentProfile.value.toString()))
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.visits.collect { visits ->
                        if (visits.isNullOrEmpty()) {
                            binding.nestedTvNoVisits.visibility = View.VISIBLE
                            binding.rvVisits.visibility = View.GONE
                        } else {
                            initVisitAdapter(visits)
                            binding.rvVisits.visibility = View.VISIBLE
                            binding.nestedTvNoVisits.visibility = View.GONE
                        }
                    }
                }

                launch {
                    viewModel.currentNameProfile.collect { name ->
                        binding.tvNameProfile.text = name
                    }
                }

                launch {
                    viewModel.filterDatePeriod.collect { period ->
                        binding.containerFilterDates.visibility = if (period == null) View.GONE else View.VISIBLE
                        period?.let {
                            binding.tvDateFrom.text = getDateShort(it.first)
                            binding.tvDateTo.text = getDateShort(it.second)
                        }
                    }
                }
            }
        }
    }

    private fun initVisitAdapter(visits: List<Visit>) {
        val idsDateTitle = mutableSetOf<Long>()
        val idsDivider = mutableSetOf<Long>()
        var past: Visit = visits.first()

        for (visit in visits) {
            if (visits.first() == visit) {
                idsDateTitle.add(visit.id!!)
            }

            val datePast = LocalDateTime.ofEpochSecond(past.date, 0, ZoneOffset.UTC).toLocalDate()
            val dateCurrent = LocalDateTime.ofEpochSecond(visit.date, 0, ZoneOffset.UTC).toLocalDate()

            if (dateCurrent != datePast) {
                past = visit
                idsDateTitle.add(visit.id!!)
                idsDivider.add(visit.id!!)
            }
        }

        val itemVisits = visits.map { v ->
            val profileName = viewModel.profiles.value.first { it.id == v.profileId }.name
            ItemVisit.visitToItemVisit(v, profileName)
        }

        binding.rvVisits.adapter = VisitsAdapter(idsDateTitle, idsDivider, itemVisits, ::onClickVisit)
    }

    private fun onClickVisit(id: Long) {
        val navHost = requireActivity().supportFragmentManager.findFragmentById(R.id.nav_fragment) as NavHostFragment
        val navController = navHost.navController
        navController.navigate(VisitsFragmentDirections.actionVisitsFragmentToVisitEditFragment(idVisit = id.toString()))
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

    private fun initFilterBtn() {
        binding.btnFilter.setOnClickListener {
            val filterDialog = FilterDialogFragment()

            if (viewModel.filterDatePeriod.value != null) {
                val bundle =  bundleOf(FilterDialogFragment.DATE_FROM to viewModel.filterDatePeriod.value?.first?.toEpochDay(), FilterDialogFragment.DATE_TO to viewModel.filterDatePeriod.value?.second?.toEpochDay())
                filterDialog.arguments = bundle
            }

            filterDialog.show(parentFragmentManager, FilterDialogFragment.TAG)
        }

        setFragmentResultListener(FilterDialogFragment.REQUEST_KEY) { _, bundle ->
            val dateFrom = LocalDate.ofEpochDay(bundle.getLong(FilterDialogFragment.DATE_FROM))
            val dateTo = LocalDate.ofEpochDay(bundle.getLong(FilterDialogFragment.DATE_TO))

            viewModel.setFilterDatePeriod(Pair(dateFrom, dateTo))
        }
    }

    private fun initSelectProfile() {
        binding.containerProfile.setOnClickListener { onClickSelectProfile() }
    }

    private fun onClickSelectProfile() {
        val profilesDialog = ProfileDialogFragment(viewModel.currentProfile.value, viewModel.profiles.value) {
            viewModel.setCurrentProfile(
                null
            )
        }

        profilesDialog.show(parentFragmentManager, ProfileDialogFragment.TAG)

        setFragmentResultListener(ProfileDialogFragment.REQUEST_KEY_SELECT) { _, bundle ->
            viewModel.setCurrentProfile(bundle.getLong(ProfileDialogFragment.ID_PROFILE))
        }

        setFragmentResultListener(ProfileDialogFragment.REQUEST_KEY_EDIT) { _, bundle ->
            toEditProfile(bundle.getLong(ProfileDialogFragment.ID_PROFILE))
        }
    }

    private fun toEditProfile(id: Long) {
        val navHost = requireActivity().supportFragmentManager.findFragmentById(R.id.nav_fragment) as NavHostFragment
        val navController = navHost.navController
        navController.navigate(VisitsFragmentDirections.actionVisitsFragmentToEditProfileFragment(id.toString()))
    }

    private fun initClearFilterBtn() {
        binding.btnClearFilter.setOnClickListener {
            viewModel.setFilterDatePeriod(null)
        }
    }
}