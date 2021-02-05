package tgd.company.dailyplanner.ui.fragment

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.jetbrains.annotations.Nullable
import tgd.company.dailyplanner.R
import tgd.company.dailyplanner.databinding.CreateFragmentBinding
import tgd.company.dailyplanner.ui.viewmodel.AppViewModel
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class CreateFragment @Inject constructor(
    @Nullable
    private var viewModel: AppViewModel?
): Fragment(R.layout.create_fragment) {

    private var _binding: CreateFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = CreateFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    @ExperimentalCoroutinesApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel = viewModel ?: ViewModelProvider(requireActivity()).get(AppViewModel::class.java)

        val items = resources.getStringArray(R.array.time_list)
        val sdf = SimpleDateFormat(getString(R.string.basic_date_format), Locale.ROOT)

        viewModel!!.newCustomEventCalendar.observe(viewLifecycleOwner) {
            binding.dateTextViewId.setText(sdf.format(it.time))
        }


        viewModel!!.setNewCustomEventCalendar(viewModel!!.selectedDay.value!!)

        setTimeAdapter(items)
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
            viewModel!!.insertCustomEvent(
                timeIndex,
                binding.nameEditTextId.text.toString(),
                binding.descriptionEditTextId.text.toString()
            ) {
                Snackbar.make(
                    requireView(),
                    getString(R.string.successful_created_event_message),
                    Snackbar.LENGTH_LONG
                ).show()
                back()
            }
        }
    }

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

    private fun setTimeAdapter(items: Array<String>) {
        val adapter = ArrayAdapter(requireContext(), R.layout.list_item, items)
        (binding.timeTextFieldId.editText as? AutoCompleteTextView)?.setAdapter(adapter)
    }

    private fun back() {
        viewModel!!.setNewCustomEventCalendar(viewModel!!.selectedDay.value!!)
        findNavController().popBackStack()
    }


}