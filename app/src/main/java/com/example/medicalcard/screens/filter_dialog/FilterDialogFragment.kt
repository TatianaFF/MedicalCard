package com.example.medicalcard.screens.filter_dialog

import android.annotation.SuppressLint
import androidx.fragment.app.viewModels
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.medicalcard.databinding.FragmentFilterBinding
import com.example.medicalcard.screens.BaseBottomSheetDialogFragment
import com.example.medicalcard.utils.getDateShort
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.Calendar

class FilterDialogFragment : BaseBottomSheetDialogFragment<FragmentFilterBinding>() {

    override fun getViewBinding() = FragmentFilterBinding.inflate(layoutInflater)

    private val viewModel: FilterViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            viewModel.setDateFrom(LocalDate.ofEpochDay(it.getLong(DATE_FROM)))
            viewModel.setDateTo(LocalDate.ofEpochDay(it.getLong(DATE_TO)))
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initValues()
        observeViewModel()
    }

    private fun initValues() {
        initDatePicker()
        initSaveBtn()
    }

    @SuppressLint("SetTextI18n")
    private fun initDatePicker() {
        val calendarMin = Calendar.getInstance().apply { add(Calendar.YEAR, -5) }
        val calendarMax = Calendar.getInstance().apply { add(Calendar.YEAR, +5) }

        val constraints = CalendarConstraints.Builder()
            .setStart(calendarMin.timeInMillis)
            .setEnd(calendarMax.timeInMillis)
            .build()

        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Выберите дату")
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .setCalendarConstraints(constraints)
            .build()

        binding.btnFrom.setOnClickListener {
            datePicker.show(parentFragmentManager, DP_FROM)
        }

        binding.btnTo.setOnClickListener {
            datePicker.show(parentFragmentManager, DP_TO)
        }

        datePicker.addOnPositiveButtonClickListener {
            val date = Calendar.getInstance().apply { timeInMillis = it }

            if (datePicker.tag == DP_FROM)
                viewModel.setDateFrom(
                    LocalDate.of(
                        date.get(Calendar.YEAR),
                        date.get(Calendar.MONTH) + 1,
                        date.get(Calendar.DAY_OF_MONTH),
                    )
                )
            else if (datePicker.tag == DP_TO)
                viewModel.setDateTo(
                    LocalDate.of(
                        date.get(Calendar.YEAR),
                        date.get(Calendar.MONTH) + 1,
                        date.get(Calendar.DAY_OF_MONTH),
                    )
                )
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.dateFrom.collect { dateFrom ->
                        binding.btnFrom.text = getDateShort(dateFrom)
                    }
                }

                launch {
                    viewModel.dateTo.collect { dateTo ->
                        binding.btnTo.text = getDateShort(dateTo)
                    }
                }
            }
        }
    }

    private fun initSaveBtn() {
        binding.btnSave.setOnClickListener {
            val dateFrom = viewModel.dateFrom.value.toEpochDay()
            val dateTo = viewModel.dateTo.value.toEpochDay()

            if (dateFrom > dateTo) Toast.makeText(
                requireContext(),
                "Начало периода не может быть больше конца",
                Toast.LENGTH_SHORT
            ).show()
            else {
                setFragmentResult(REQUEST_KEY, bundleOf(DATE_FROM to dateFrom, DATE_TO to dateTo))
                dismiss()
            }
        }
    }

    companion object {
        const val TAG = "FilterBottomSheet"
        const val REQUEST_KEY = "rkFilter"
        const val DATE_FROM = "date_from"
        const val DATE_TO = "date_to"

        private const val DP_FROM = "dpFrom"
        private const val DP_TO = "dpTo"
    }
}