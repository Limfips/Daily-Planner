package tgd.company.dailyplanner.ui.fragment

import android.app.DatePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import org.jetbrains.annotations.Nullable
import tgd.company.dailyplanner.R
import tgd.company.dailyplanner.databinding.FragmentEditEventBinding
import tgd.company.dailyplanner.ui.viewmodel.AppViewModel
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class EditEventFragment @Inject constructor(
    @Nullable
    var viewModel: AppViewModel?
) : Fragment(R.layout.fragment_edit_event) {

    private var _binding: FragmentEditEventBinding? = null
    private val binding get() = _binding!!

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
                binding.descriptionEditTextId.text.toString()
            ) {
                back()
            }
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


}