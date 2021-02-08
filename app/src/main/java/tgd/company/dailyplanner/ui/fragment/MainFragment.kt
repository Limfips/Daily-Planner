package tgd.company.dailyplanner.ui.fragment

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.applandeo.materialcalendarview.EventDay
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import org.jetbrains.annotations.Nullable
import tgd.company.dailyplanner.R
import tgd.company.dailyplanner.data.customevent.CustomEvent
import tgd.company.dailyplanner.databinding.FragmentMainBinding
import tgd.company.dailyplanner.service.adapters.CustomEventAdapter
import tgd.company.dailyplanner.ui.viewmodel.AppViewModel
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class MainFragment @Inject constructor(
        @Nullable
        var viewModel: AppViewModel?
) : Fragment(R.layout.fragment_main) {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    private val events: MutableLiveData<List<EventDay>> = MutableLiveData(listOf())

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
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

        val adapter = CustomEventAdapter(requireContext())
        binding.recyclerview.adapter = adapter

        BottomSheetBehavior
                .from(binding.bottomSheetBehavior)
                .addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                    @SuppressLint("SwitchIntDef")
                    override fun onStateChanged(bottomSheet: View, newState: Int) {}
                    override fun onSlide(bottomSheet: View, slideOffset: Float) {
                        if (slideOffset > 0.1f) {
                            binding.mainFragmentRoot.setBackgroundColor(Color.parseColor("#70000000"))
                        } else {
                            binding.mainFragmentRoot.setBackgroundColor(Color.parseColor("#00000000"))
                        }
                    }
                })


        binding.closeBehaviorView.setOnClickListener {
            BottomSheetBehavior
                    .from(binding.bottomSheetBehavior)
                    .state = BottomSheetBehavior.STATE_HIDDEN
        }

        binding.bottomAppBar.setNavigationOnClickListener {
            BottomSheetBehavior
                    .from(binding.bottomSheetBehavior)
                    .state = BottomSheetBehavior.STATE_EXPANDED
        }

        binding.bottomAppBar.setOnMenuItemClickListener { menuItem ->
            when(menuItem.itemId) {
                R.id.loadOnServer -> {
                    viewModel!!.loadDataInServer { result ->
                        if (result) {
                            showSnackbar("Данные загружены c сервера")
                        } else {
                            showSnackbar("Ошибка при загрузки данных")
                        }
                    }
                    true
                }
                R.id.saveOnServer -> {
                    viewModel!!.saveDataOnServer { result ->
                        if (result) {
                            showSnackbar("Данные загружены на сервер")
                        } else {
                            showSnackbar("Ошибка при отправки данных")
                        }
                    }
                    true
                }
                 else -> false
            }
        }

        events.observe(viewLifecycleOwner, {
            binding.calendarView.setEvents(it)
        })

        viewModel!!.selectedDay.observe(viewLifecycleOwner) {
            viewModel!!.observeCustomEvents(viewModel!!.getCurrentUser()!!.uid).observe(viewLifecycleOwner) { list ->
                viewModel!!.updateEvents(list)
                updateList(list, adapter, it)
            }
        }

        if (viewModel!!.isStart) {
            viewModel!!.setSelectedDay(binding.calendarView.firstSelectedDate)
            viewModel!!.isStart = false
        } else {
            binding.calendarView.setDate(viewModel!!.selectedDay.value)
        }

        binding.calendarView.setOnDayClickListener {
            viewModel!!.setSelectedDay(it.calendar)
        }

        binding.addNewButtonId.setOnClickListener {
            findNavController().navigate(
                MainFragmentDirections.actionMainFragmentToCreateFragment()
            )
        }

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                requireActivity().finish()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(callback)
    }

    private fun showSnackbar(text: String) {
        Snackbar.make(
                requireView(),
                text,
                Snackbar.LENGTH_LONG
        ).setAnchorView(binding.addNewButtonId).show()
    }

    private fun subscribeToObservers() {

    }

    private fun updateList(
            list: List<CustomEvent>,
            adapter: CustomEventAdapter,
            dayCalendar: Calendar
    ) {
        val cal = Calendar.getInstance()
        val color = resources.getColor(R.color.basic_color_event, requireContext().theme)
        val dc = ResourcesCompat.getDrawable(
                resources, R.drawable.ic_baseline_event_available_24, requireContext().theme
        )
        dc?.setTint(color)
        events.value = list.map {
            val date: Calendar = cal.clone() as Calendar
            date.time = Date(it.date_start)
            EventDay(date, dc, color)
        }.toList()

        val startSearch = dayCalendar.clone() as Calendar
        val finishSearch = dayCalendar.clone() as Calendar

        startSearch.set(Calendar.HOUR_OF_DAY, 0)
        startSearch.set(Calendar.MINUTE, 0)

        finishSearch.set(Calendar.HOUR_OF_DAY, 23)
        finishSearch.set(Calendar.MINUTE, 59)

        list.let { values ->
            adapter.submitList(
                    values.filter { customEvent ->
                        customEvent.date_start >= startSearch.time.time
                                && customEvent.date_finish <= finishSearch.time.time

                    }.toList().sortedBy { it.date_start }
            )
        }
    }
}