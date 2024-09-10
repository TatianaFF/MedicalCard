package com.example.medicalcard.screens.edit_visit

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.OpenableColumns
import android.util.Log
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.medicalcard.BuildConfig
import com.example.medicalcard.R
import com.example.medicalcard.adapters.FilesAdapter
import com.example.medicalcard.adapters.NotificationsAdapter
import com.example.medicalcard.databinding.FragmentEditVisitBinding
import com.example.medicalcard.screens.BaseFragment
import com.example.medicalcard.screens.notify_dialog.NotifyDialogFragment
import com.example.medicalcard.utils.getDateWithDayWeekString
import com.example.medicalcard.utils.getTimeString
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.File
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Calendar


@AndroidEntryPoint
class VisitEditFragment : BaseFragment<FragmentEditVisitBinding>() {
    override fun getViewBinding() = FragmentEditVisitBinding.inflate(layoutInflater)
    private val viewModel: VisitEditViewModel by viewModels()
    private val idVisit: Long?
        get() = arguments?.let { VisitEditFragmentArgs.fromBundle(it).idVisit }?.toLongOrNull()

    private val idProfile: Long?
        get() = arguments?.let { VisitEditFragmentArgs.fromBundle(it).idProfile }?.toLongOrNull()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setDataVisit()
        initValues()
        observeViewModel()
    }

    private fun setDataVisit() {
        idVisit?.let {
            try {
                viewModel.initData(it)
            } catch (e: Exception) {
                Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
            }
            binding.tvTitle.text = getString(R.string.edit_visit_screen)
        }
    }

    private fun initValues() {
        val navHost = requireActivity().supportFragmentManager.findFragmentById(R.id.nav_fragment) as NavHostFragment
        val navController = navHost.navController

        initDatePicker()
        initTimePicker()

        initSaveBtn(navController)
        initCloseBtn(navController)
        initDeleteBtn(navController)
        initAddNotifyBtn()
        initAddFileBtn()
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.dateTimeVisit.collect { dateTimeVisit ->
                        binding.btnDate.text = getDateWithDayWeekString(dateTimeVisit.toLocalDate())
                        binding.btnTime.text = getTimeString(dateTimeVisit)
                    }
                }

                launch {
                    viewModel.dateTimeNotifications.collect { notifications ->
                        binding.rvNotifications.adapter =
                            NotificationsAdapter(notifications.map { it.second }, ::onClickDeleteNotify)
                    }
                }

                launch {
                    viewModel.files.collect {
                        binding.rvFiles.adapter = FilesAdapter(it, ::onClickDeleteFile, ::openPdf, ::downloadFile)
                    }
                }

                launch {
                    viewModel.nameVisit.collect { name ->
                        name?.let {
                            binding.editNameVisit.setText(it)
                        }
                    }
                }

                launch {
                    viewModel.commentVisit.collect { comment ->
                        comment?.let {
                            binding.editCommentVisit.setText(it)
                        }
                    }
                }

                launch {
                    viewModel.profileVisit.collect { profile ->
                        profile?.let { p ->
                            viewModel.profiles.value?.let { profiles ->
                                binding.spinner.setSelection(profiles.indexOfFirst { p.id == it.id })
                            }
                        }
                    }
                }

                launch {
                    viewModel.profiles.collect { profiles ->
                        profiles?.let {
                            idProfile?.let { id ->
                                Log.e("idProfile", idProfile.toString())
                                Log.e("profiles", profiles.toString())
                                viewModel.setProfileVisit(profiles.first { p -> p.id == id })
                            }

                            val profileNames = profiles.map { it.name }

                            val adapter = ArrayAdapter(requireContext(), R.layout.item_profile_name, R.id.tv_title, profileNames)
                            binding.spinner.setAdapter(adapter)

                            binding.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                                override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                                    viewModel.setProfileVisit(profiles[position])
                                }

                                override fun onNothingSelected(parent: AdapterView<*>) {}
                            }
                        }
                    }
                }
            }
        }
    }

    private fun onClickDeleteNotify(index: Int) {
        val notifi = viewModel.dateTimeNotifications.value[index]
        val id = notifi.first
        id?.let {
            viewModel.deleteNotification(Pair(it, notifi.second))
        }
        viewModel.deleteDateTimeNotifyFromState(index)
    }

    private fun onClickDeleteFile(uri: Uri) {
        deleteFile(uri)
    }

    private fun openPdf(uri: Uri) {
        val directory = requireContext().getExternalFilesDir(null)

        val _uri = FileProvider.getUriForFile(requireContext(), BuildConfig.APPLICATION_ID + ".provider", File(directory, getFileName(uri)))

        val intent = Intent(Intent.ACTION_VIEW).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            if (getTypeFile(_uri) == "pdf") setDataAndType(_uri, "application/pdf")
            else setDataAndType(_uri, "image/*")
        }
        try {
            requireContext().startActivity(Intent.createChooser(intent, "Select app"))
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(context, "Приложения для чтения", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getTypeFile(uri: Uri): String {
        val cR = requireContext().contentResolver
        val mime = MimeTypeMap.getSingleton()
        val type = mime.getExtensionFromMimeType(cR.getType(uri))
        return requireNotNull(type) { "Ошибка при определении типа файла" }
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
            viewModel.setDateVisit(
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
            viewModel.setTimeVisit(timePicker.hour, timePicker.minute)
        }
    }

    private fun initSaveBtn(navController: NavController) {
        binding.btnSave.setOnClickListener {
            viewModel.setNameVisit(binding.editNameVisit.text.toString())
            viewModel.setCommentVisit(binding.editCommentVisit.text.toString())
            try {
                if (idVisit == null)
                    viewModel.insertVisit()
                else viewModel.updateVisit(
                    requireNotNull(idVisit) { "Ошибка при определении идентификатора" }
                )
                navController.navigateUp()
            } catch (e: Exception) {
                Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun initCloseBtn(navController: NavController) {
        binding.btnClose.setOnClickListener { navController.navigateUp() }
    }

    private fun initDeleteBtn(navController: NavController) {
        idVisit?.let {  id ->
            binding.btnDeleteVisit.visibility = View.VISIBLE
            binding.btnDeleteVisit.setOnClickListener {
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Удалить визит")
                    .setMessage("Вы действительно хотите удалить визит?")
                    .setNegativeButton("Отмена") { dialog, which ->
                        dialog.dismiss()
                    }
                    .setPositiveButton("Удалить") { dialog, which ->
                        viewModel.deleteVisitById(id)
                        navController.navigateUp()
                    }
                    .show()
            }
        }
    }

    private fun initAddNotifyBtn() {
        binding.btnAddNotify.setOnClickListener {
            NotifyDialogFragment().show(parentFragmentManager, NotifyDialogFragment.TAG)
        }

        setFragmentResultListener(NotifyDialogFragment.REQUEST_KEY) { _, bundle ->
            val dateTimeNotify = LocalDateTime.ofInstant(
                Instant.ofEpochSecond(bundle.getLong(NotifyDialogFragment.DATE_TIME)),
                ZoneId.systemDefault()
            )
            viewModel.addDateTimeNotify(null, dateTimeNotify)
        }
    }

    private fun initAddFileBtn() {
        binding.btnAddFile.setOnClickListener {
            pickFile()
        }
    }

    private fun saveFile(uri: Uri) {
        var resultURI = uri
        try {
            if (ContentResolver.SCHEME_CONTENT == uri.scheme) {
                val cr: ContentResolver = requireContext().contentResolver
                val nameFile = getFileName(uri)
                val directory = requireContext().getExternalFilesDir(null)
                val file = File(directory, nameFile)
                val input = cr.openInputStream(uri)
                file.outputStream().use { stream ->
                    input?.copyTo(stream)
                }
                input?.close()
                resultURI = Uri.fromFile(file)

                if (viewModel.files.value.contains(resultURI)) throw Exception("Файл с таким именем уже прикреплен к визиту")

                viewModel.addFile(resultURI)
            }
        } catch (e: Exception) {
            Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun getFileName(uri: Uri): String {
        var result: String? = null
        if (uri.scheme == "content") {
            requireContext().contentResolver.query(uri, null, null, null, null).use { cursor ->
                if (cursor != null && cursor.moveToFirst()) {
                    val columnIndex: Int = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (columnIndex != -1) {
                        result = cursor.getString(columnIndex)
                    }
                }
            }
        }
        if (result == null) {
            result = uri.lastPathSegment
        }
        return requireNotNull(result) { "Ошибка при определении названия файла" }
    }

    private fun deleteFile(uri: Uri) {
        try {
            val directory = requireContext().getExternalFilesDir(null)
            val file = File(directory, getFileName(uri))
            if (file.exists()) file.delete()

            viewModel.deleteFile(Uri.fromFile(file))

        } catch (e: Exception) {
            Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
        }
    }

    private val requestPickFileLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.data?.let { uri ->
                    saveFile(uri)
                }
            }
        }

    private fun pickFile() {
        val types = arrayOf("application/pdf", "image/*")

        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "*/*"
            putExtra(Intent.EXTRA_MIME_TYPES, types)
            putExtra(Intent.EXTRA_LOCAL_ONLY, true)
                .addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
                .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        requestPickFileLauncher.launch(intent)
    }

    private fun downloadFile(uriFile: Uri) {
        try {
            val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val nameFile = getFileName(uriFile)
            val cr = requireContext().contentResolver
            val file = File(path, nameFile)

            if (file.exists()) throw RuntimeException("Файл с таким именем уже существует в папке Downloads")

            val input = cr.openInputStream(uriFile)
            file.outputStream().use { stream ->
                input?.copyTo(stream)
            }
            input?.close()

            Toast.makeText(requireContext(), "Файл скачан в папку Downloads", Toast.LENGTH_SHORT).show()

        } catch (e: Exception) {
            Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
        }
    }
}