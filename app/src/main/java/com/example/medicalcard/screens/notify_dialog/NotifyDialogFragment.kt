package com.example.medicalcard.screens.notify_dialog

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.medicalcard.databinding.FragmentNotifyDialogBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch
import com.example.medicalcard.utils.getDateWithDayWeekString
import com.example.medicalcard.utils.getTimeString
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import java.time.Instant
import java.time.ZoneId
import java.util.Calendar


class NotifyDialogFragment : DialogFragment() {

    private var _binding: FragmentNotifyDialogBinding? = null
    private val binding get() = _binding!!

    private val viewModel: NotifyDialogViewModel by viewModels()

    @SuppressLint("UseGetLayoutInflater")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = FragmentNotifyDialogBinding.inflate(LayoutInflater.from(context))
        return MaterialAlertDialogBuilder(requireContext(), theme).apply {
            setView(binding.root)
        }.create()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = binding.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initValues()
        observeViewModel()
    }

    private fun initValues() {
        initDatePicker()
        initTimePicker()
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

        binding.btnDate.setOnClickListener {
            datePicker.show(parentFragmentManager, "datePicker")
        }

        datePicker.addOnPositiveButtonClickListener {
            val date = Calendar.getInstance().apply { timeInMillis = it }
            viewModel.setDateNotify(
                date.get(Calendar.DAY_OF_MONTH),
                date.get(Calendar.MONTH) + 1,
                date.get(Calendar.YEAR)
            )
        }
    }

    @SuppressLint("SetTextI18n")
    private fun initTimePicker() {
        val timePicker =
            MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setHour(12)
                .setMinute(10)
                .setTitleText("Выберите время")
                .build()

        binding.btnTime.setOnClickListener {
            timePicker.show(childFragmentManager, "timePicker")
        }

        timePicker.addOnPositiveButtonClickListener {
            viewModel.setTimeNotify(timePicker.hour, timePicker.minute)
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.dateTimeNotify.collect { dateTimeNotify ->
                        binding.btnDate.text = getDateWithDayWeekString(dateTimeNotify.toLocalDate())
                        binding.btnTime.text = getTimeString(dateTimeNotify)
                    }
                }
            }
        }
    }

    private fun initSaveBtn() {
        binding.btnSave.setOnClickListener {
            val dateTime = viewModel.dateTimeNotify.value.toEpochSecond(
                ZoneId
                    .systemDefault()
                    .rules
                    .getOffset(
                        Instant.now()
                    )
            )
            setFragmentResult(REQUEST_KEY, bundleOf(DATE_TIME to dateTime))
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "NOTIFY_DIALOG"
        const val REQUEST_KEY = "REQUEST_KEY"
        const val DATE_TIME = "DATE_TIME"
    }
}