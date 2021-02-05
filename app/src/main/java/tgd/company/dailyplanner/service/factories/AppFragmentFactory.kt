package tgd.company.dailyplanner.service.factories

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import tgd.company.dailyplanner.ui.fragment.LoginFragment
import javax.inject.Inject

class AppFragmentFactory @Inject constructor(
): FragmentFactory() {

    override fun instantiate(classLoader: ClassLoader, className: String): Fragment {
        return when(className) {
            LoginFragment::class.java.name -> LoginFragment(null)
            else -> super.instantiate(classLoader, className)
        }
    }
}