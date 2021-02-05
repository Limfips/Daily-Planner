package tgd.company.dailyplanner.service.room.customevent

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.filters.SmallTest
import com.google.common.truth.Truth.assertThat
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import tgd.company.dailyplanner.data.customevent.CustomEvent
import tgd.company.dailyplanner.getOrAwaitValue
import javax.inject.Inject
import javax.inject.Named

@ExperimentalCoroutinesApi
@SmallTest
@HiltAndroidTest
class CustomEventDaoTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Inject
    @Named("custom_event_test_db")
    lateinit var database: CustomEventDatabase
    private lateinit var dao: CustomEventDao

    @Before
    fun setup() {
        hiltRule.inject()
        dao = database.customEventDao()
    }

    @After
    fun teardown() {
        database.close()
    }


    @Test
    fun insertCustomEvent() = runBlockingTest {
        val userUid1 = "TEST_USER_UID_1"
        val userUid2 = "TEST_USER_UID_2"
        val userUid3 = "TEST_USER_UID_3"
        val customEvent1 = CustomEvent(
            userUid1,
            0,
            0,
            "event_1",
            "desc_1",
            1
        )
        val customEvent2 = CustomEvent(
            userUid1,
            0,
            0,
            "event_2",
            "desc_2",
            2
        )
        val customEvent3 = CustomEvent(
            userUid2,
            0,
            0,
            "event_3",
            "desc_3",
            3
        )
        val customEvent4 = CustomEvent(
            userUid2,
            0,
            0,
            "event_4",
            "desc_4",
            4
        )
        val customEvent5 = CustomEvent(
            userUid3,
            0,
            0,
            "event_5",
            "desc_5",
            5
        )

        dao.insertCustomEvent(customEvent1)
        dao.insertCustomEvent(customEvent2)
        dao.insertCustomEvent(customEvent3)
        dao.insertCustomEvent(customEvent4)
        dao.insertCustomEvent(customEvent5)

        var events = dao.observeCustomEvents(userUid1).getOrAwaitValue()
        assertThat(events.size).isEqualTo(2)
        assertThat(events).contains(customEvent1)
        assertThat(events).contains(customEvent2)

        events = dao.observeCustomEvents(userUid2).getOrAwaitValue()
        assertThat(events.size).isEqualTo(2)
        assertThat(events).contains(customEvent3)
        assertThat(events).contains(customEvent4)

        events = dao.observeCustomEvents(userUid3).getOrAwaitValue()
        assertThat(events.size).isEqualTo(1)
        assertThat(events).contains(customEvent5)
    }

    @Test
    fun deleteCustomEvent() = runBlockingTest {
        val userUid = "TEST_USER_UID_1"
        val customEvent = CustomEvent(
            userUid,
            0,
            0,
            "event_1",
            "desc_1"
        )

        dao.insertCustomEvent(customEvent)
        dao.deleteCustomEvent(customEvent)

        val events = dao.observeCustomEvents(userUid).getOrAwaitValue()
        assertThat(events).doesNotContain(customEvent)
    }
}