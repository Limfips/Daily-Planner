package tgd.company.dailyplanner.ui.fragment

import android.app.DatePickerDialog
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.RequestManager
import org.jetbrains.annotations.Nullable
import tgd.company.dailyplanner.R
import tgd.company.dailyplanner.data.fileitem.FileItem
import tgd.company.dailyplanner.databinding.FragmentEditEventBinding
import tgd.company.dailyplanner.other.Constants
import tgd.company.dailyplanner.service.adapters.FileItemAdapter
import tgd.company.dailyplanner.ui.dialog.ImagePickDialog
import tgd.company.dailyplanner.ui.viewmodel.AppViewModel
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

class EditEventFragment @Inject constructor(
        @Nullable
        var viewModel: AppViewModel?,
        private val fileItemAdapter: FileItemAdapter,
        private val glide: RequestManager
) : Fragment(R.layout.fragment_edit_event), ImagePickDialog.NoticeDialogListener {

    private var _binding: FragmentEditEventBinding? = null
    private val binding get() = _binding!!

    private val tmpFiles = ArrayList<FileItem>()
    private val _files = MutableLiveData<List<FileItem>>()
    private val files: LiveData<List<FileItem>> = _files

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditEventBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = viewModel ?: ViewModelProvider(requireActivity()).get(AppViewModel::class.java)

        setupRecyclerView()

        viewModel!!.observeFileItems(viewModel!!.selectedCustomEvent.value!!.id!!)
                .observe(viewLifecycleOwner) {
                    if (it != null) {
                        tmpFiles.addAll(it)
                        _files.postValue(tmpFiles)
                    }
                }

        files.observe(viewLifecycleOwner) {
            if (it != null) {
                fileItemAdapter.fileItems = it
            }
        }

        // создаём список временных промежутков (с шагом в 1 час)
        // и создаёт формат даты
        val items = resources.getStringArray(R.array.time_list)
        val sdf = SimpleDateFormat(getString(R.string.basic_date_format), Locale.ROOT)

        viewModel!!.newCustomEventCalendar.observe(viewLifecycleOwner) {
            binding.dateTextViewId.setText(sdf.format(it.time))
        }

        binding.nameEditTextId.setText(viewModel!!.selectedCustomEvent.value!!.name)
        binding.descriptionEditTextId.setText(viewModel!!.selectedCustomEvent.value!!.description)

        val editCustomEventCalendar = Calendar.getInstance()
        editCustomEventCalendar.timeInMillis = viewModel!!.selectedCustomEvent.value!!.date_start

        viewModel!!.setNewCustomEventCalendar(editCustomEventCalendar)

        setTimeAdapter(items, editCustomEventCalendar.get(Calendar.HOUR_OF_DAY))
        setDateListener()

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                back()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(callback)

        binding.canselButtonId.setOnClickListener { back() }
        binding.createButtonId.setOnClickListener {
            if (checkInvalidFields()) return@setOnClickListener
            val timeIndex = items.indexOf(binding.timeTextViewId.text.toString())
            viewModel!!.updateCustomEvent(
                timeIndex,
                binding.nameEditTextId.text.toString(),
                binding.descriptionEditTextId.text.toString(),
                tmpFiles
            ) {
                back()
            }
        }

        binding.btnAddFile.setOnClickListener {
            showNoticeDialog()
        }

        fileItemAdapter.setonDeleteItemClickListener { item ->
            tmpFiles.remove(item)
            _files.postValue(tmpFiles)
            fileItemAdapter.notifyDataSetChanged()
        }

        fileItemAdapter.setOnItemClickListener {
//            startActivity(openFileIntent(Uri.parse(it.roomUrl), requireActivity()))
        }
    }

    private fun setupRecyclerView() {
        fileItemAdapter.setVisibleDeleteIcon(true)
        binding.rvFileItems.apply {
            adapter = fileItemAdapter
            layoutManager = GridLayoutManager(requireContext(), Constants.GRID_SPAN_COUNT)
        }
    }

    // Метод проверяет чтобы все поля были заполнены, кроме описания, оно не обязательное.
    private fun checkInvalidFields(): Boolean {
        binding.nameTextFieldId.error = null
        binding.timeTextFieldId.error = null

        if (binding.nameEditTextId.text.toString().isEmpty()) {
            binding.nameTextFieldId.error = getString(R.string.error_name_text)
            return true
        }
        if (binding.timeTextViewId.text.toString().isEmpty()) {
            binding.timeTextFieldId.error = getString(R.string.error_name_text)
            return true
        }
        return false
    }

    // метод для инициализирования дата пикера
    private fun setDateListener() {
        val newCal = viewModel!!.newCustomEventCalendar.value!!.clone() as Calendar
        val dateSetListener =
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                newCal.set(Calendar.YEAR, year)
                newCal.set(Calendar.MONTH, monthOfYear)
                newCal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                viewModel!!.setNewCustomEventCalendar(newCal)
            }

        binding.dateTextFieldId.setEndIconOnClickListener {
            DatePickerDialog(
                    requireActivity(), dateSetListener,
                    viewModel!!.newCustomEventCalendar.value!!.get(Calendar.YEAR),
                    viewModel!!.newCustomEventCalendar.value!!.get(Calendar.MONTH),
                    viewModel!!.newCustomEventCalendar.value!!.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }

    private fun setTimeAdapter(items: Array<String>, timeIndex: Int) {
        val adapter = ArrayAdapter(requireContext(), R.layout.list_item, items)
        (binding.timeTextFieldId.editText as? AutoCompleteTextView)?.setText(items[timeIndex])
        (binding.timeTextFieldId.editText as? AutoCompleteTextView)?.setAdapter(adapter)
    }

    private fun back() {
        findNavController().popBackStack()
        viewModel!!.setNewCustomEventCalendar(viewModel!!.selectedDay.value!!)
    }

    private fun showNoticeDialog() {
        val dialog = ImagePickDialog(this, glide)
        dialog.show(requireActivity().supportFragmentManager, "NoticeDialogFragment")
    }

    override fun onDialogPositiveClick(imageName: String, imageUri: String) {
        tmpFiles.add(
            FileItem(
                name = imageName,
                roomUrl = imageUri,
                customEventId = viewModel!!.selectedCustomEvent.value!!.id,
                userUid = viewModel!!.selectedCustomEvent.value!!.userUid
            )
        )
        _files.postValue(tmpFiles)
        fileItemAdapter.notifyDataSetChanged()
    }

    override fun onDialogNegativeClick() {
    }


}