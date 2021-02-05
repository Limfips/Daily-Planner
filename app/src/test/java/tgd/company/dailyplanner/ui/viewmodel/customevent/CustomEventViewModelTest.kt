package tgd.company.dailyplanner.ui.viewmodel.customevent

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import tgd.company.dailyplanner.MainCoroutineRule
import tgd.company.dailyplanner.data.customevent.CustomEvent
import tgd.company.dailyplanner.getOrAwaitValueTest
import tgd.company.dailyplanner.repositories.FakeCustomEventRepository

@ExperimentalCoroutinesApi
class CustomEventViewModelTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private lateinit var viewModel: CustomEventViewModel

    @Before
    fun setup() {
        viewModel = CustomEventViewModel(FakeCustomEventRepository())
    }

    @Test
    fun `save data in local db`() = runBlockingTest {
        val testUserUid = "TEST_USER_UID"
        val customEvent = CustomEvent(
            testUserUid,
            0,
            0,
            "name_1",
            "dec_1",
            1
        )

        viewModel.saveEvent(customEvent)

        val localData = viewModel.observeCustomEvents(testUserUid).getOrAwaitValueTest()
        assertThat(localData).contains(customEvent)
    }

    @Test
    fun `save data in local db and server update`() = runBlockingTest {
        val testUserUid = "TEST_USER_UID"
        val customEvent = CustomEvent(
            testUserUid,
            0,
            0,
            "name_1",
            "dec_1",
            1
        )

        viewModel.saveEvent(customEvent)
        var localData = viewModel.observeCustomEvents(testUserUid).getOrAwaitValueTest()
        assertThat(localData).contains(customEvent)

        viewModel.saveDataOnServer(testUserUid, localData)
        viewModel.deleteEvent(customEvent)

        localData = viewModel.observeCustomEvents(testUserUid).getOrAwaitValueTest()
        assertThat(localData).isNull()

        viewModel.loadDataInServer(testUserUid)

        localData = viewModel.observeCustomEvents(testUserUid).getOrAwaitValueTest()
        assertThat(localData).contains(customEvent)
    }
}