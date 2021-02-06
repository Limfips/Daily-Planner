package tgd.company.dailyplanner.service.factories

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import com.google.firebase.firestore.auth.User
import tgd.company.dailyplanner.repositories.FakeCustomEventRepositoryAndroidTest
import tgd.company.dailyplanner.repositories.FakeUserRepositoryAndroidTest
import tgd.company.dailyplanner.ui.fragment.LoginFragment
import tgd.company.dailyplanner.ui.fragment.MainFragment
import tgd.company.dailyplanner.ui.viewmodel.AppViewModel
import tgd.company.dailyplanner.ui.viewmodel.customevent.CustomEventViewModel
import tgd.company.dailyplanner.ui.viewmodel.user.UserViewModel
import javax.inject.Inject


class AppTestFragmentFactory @Inject constructor(
): FragmentFactory() {

    override fun instantiate(classLoader: ClassLoader, className: String): Fragment {
        return when (className) {
            LoginFragment::class.java.name -> LoginFragment(UserViewModel(FakeUserRepositoryAndroidTest()))
            MainFragment::class.java.name -> MainFragment(AppViewModel((FakeCustomEventRepositoryAndroidTest()), FakeUserRepositoryAndroidTest()))
            else -> super.instantiate(classLoader, className)
        }
    }
}