package tgd.company.dailyplanner.ui.viewmodel.user

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import tgd.company.dailyplanner.MainCoroutineRule
import tgd.company.dailyplanner.getOrAwaitValueTest
import tgd.company.dailyplanner.other.Status
import tgd.company.dailyplanner.repositories.FakeUserRepository

@ExperimentalCoroutinesApi
class UserViewModelTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private lateinit var viewModel: UserViewModel

    @Before
    fun setup() {
        viewModel = UserViewModel(FakeUserRepository())
    }

    @Test
    fun `signUp - insert empty login, return error`() = runBlockingTest{
        viewModel.signUp("", "password", "name")
        assertThat(viewModel.getCurrentUser()).isNull()
    }

    @Test
    fun `signUp - insert empty password, return error`() = runBlockingTest{
        viewModel.signUp("login", "", "name")

        assertThat(viewModel.getCurrentUser()).isNull()
    }

    @Test
    fun `signUp - insert empty name, return error`() = runBlockingTest{
        viewModel.signUp("login", "password", "")

        assertThat(viewModel.getCurrentUser()).isNull()
    }

    @Test
    fun `signUp - insert valid fields, return true`() = runBlockingTest{
        viewModel.signUp("login", "password", "name")

        assertThat(viewModel.getCurrentUser()).isNotNull()
    }

    @Test
    fun `signIn - insert valid fields, return true`() = runBlockingTest {
        viewModel.signUp("login", "password", "name")

        viewModel.signIn("login", "password")

        assertThat(viewModel.getCurrentUser()).isNotNull()
    }

    @Test
    fun `signIn - insert invalid login, return error`() = runBlockingTest {
        viewModel.signUp("login", "password", "name")

        viewModel.signIn("login123", "password")
        assertThat(viewModel.getCurrentUser()).isNotNull()
    }

    @Test
    fun `signIn - insert invalid password, return error`() = runBlockingTest {
        viewModel.signUp("login", "password", "name")

        viewModel.signIn("login", "password124")
        assertThat(viewModel.getCurrentUser()).isNotNull()
    }

    @Test
    fun `delete - delete user data`() = runBlockingTest{
        val user = viewModel.signUp("login", "password", "name")

        assertThat(viewModel.getCurrentUser()).isNotNull()
        assertThat(viewModel.getCurrentUser()).isEqualTo(user.data!!)

        viewModel.deleteUser()

        assertThat(viewModel.getCurrentUser()).isNull()
    }
}