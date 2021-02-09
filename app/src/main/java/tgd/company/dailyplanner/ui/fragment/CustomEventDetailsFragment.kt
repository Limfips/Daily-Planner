package tgd.company.dailyplanner.ui.fragment

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.jetbrains.annotations.Nullable
import tgd.company.dailyplanner.R
import tgd.company.dailyplanner.databinding.FragmentCustomEventDetailsBinding
import tgd.company.dailyplanner.other.Constants.GRID_SPAN_COUNT
import tgd.company.dailyplanner.service.adapters.FileItemAdapter
import tgd.company.dailyplanner.ui.viewmodel.AppViewModel
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class CustomEventDetailsFragment @Inject constructor(
        @Nullable
        var viewModel: AppViewModel?,
        private val fileItemAdapter: FileItemAdapter
) : Fragment(R.layout.fragment_custom_event_details) {

    private var _binding: FragmentCustomEventDetailsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCustomEventDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = viewModel ?: ViewModelProvider(requireActivity()).get(AppViewModel::class.java)
        subscribeToObservers()
        setupRecyclerView()

        binding.btnDelete.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setMessage(resources.getString(R.string.delete_message_item))
                .setNegativeButton(resources.getString(R.string.cancel)) { dialog, which -> }
                .setPositiveButton(resources.getString(R.string.accept)) { dialog, which ->
                    viewModel!!.deleteEvent()
                }.show()
        }

        binding.btnEdit.setOnClickListener {
            findNavController().navigate(
                MainFragmentDirections.actionMainFragmentToEditEventFragment()
            )
        }

        fileItemAdapter.setOnItemClickListener {
//            startActivity(openFileIntent(Uri.parse(it.roomUrl), requireActivity()))
        }
    }

    private fun setupRecyclerView() {
        fileItemAdapter.setVisibleDeleteIcon(false)
        binding.rvFileItems.apply {
            adapter = fileItemAdapter
            layoutManager = GridLayoutManager(requireContext(), GRID_SPAN_COUNT)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun subscribeToObservers() {
        // создаём список временных промежутков (с шагом в 1 час)
        // и создаёт формат даты
        val items = resources.getStringArray(R.array.time_list)
        val sdf = SimpleDateFormat(getString(R.string.basic_date_format), Locale.ROOT)
        val editCustomEventCalendar = Calendar.getInstance()

        viewModel!!.selectedCustomEvent.observe(viewLifecycleOwner) { customEvent ->
            editCustomEventCalendar.timeInMillis = customEvent?.date_start ?: 0

            binding.tvName.text = "Name: ${customEvent?.name}"
            binding.tvDate.text = "Date: ${sdf.format(editCustomEventCalendar.time)}"
            binding.tvTime.text = "Time: ${items[editCustomEventCalendar.get(Calendar.HOUR_OF_DAY)]}"
            binding.tvDescription.text = "Description: ${customEvent?.description}"

            if (customEvent != null) {
                viewModel!!.observeFileItems(customEvent.id!!).observe(viewLifecycleOwner) { files ->
                    if (files != null) {
                        fileItemAdapter.fileItems = files
                    }
                }
            }
        }
    }
}