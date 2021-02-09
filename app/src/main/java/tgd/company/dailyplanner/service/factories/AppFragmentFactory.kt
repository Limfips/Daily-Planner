package tgd.company.dailyplanner.service.factories

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import com.bumptech.glide.RequestManager
import tgd.company.dailyplanner.service.adapters.FileItemAdapter
import tgd.company.dailyplanner.ui.fragment.*
import javax.inject.Inject

class AppFragmentFactory @Inject constructor(
        private val fileItemAdapter: FileItemAdapter,
        private val glide: RequestManager
): FragmentFactory() {

    override fun instantiate(classLoader: ClassLoader, className: String): Fragment {
        return when(className) {
            LoginFragment::class.java.name -> LoginFragment(null)
            MainFragment::class.java.name -> MainFragment(null)
            CreateFragment::class.java.name -> CreateFragment(null)
            CustomEventDetailsFragment::class.java.name -> CustomEventDetailsFragment(
                    null,
                    fileItemAdapter
            )
            EditEventFragment::class.java.name -> EditEventFragment(
                null,
                fileItemAdapter,
                glide
            )
            else -> super.instantiate(classLoader, className)
        }
    }
}