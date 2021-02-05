package tgd.company.dailyplanner.service.room

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
import tgd.company.dailyplanner.data.user.User
import tgd.company.dailyplanner.getOrAwaitValue
import tgd.company.dailyplanner.service.room.user.UserDao
import tgd.company.dailyplanner.service.room.user.UserDatabase
import javax.inject.Inject
import javax.inject.Named

@ExperimentalCoroutinesApi
@SmallTest
@HiltAndroidTest
class UserDaoTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Inject
    @Named("user_test_db")
    lateinit var database: UserDatabase
    private lateinit var dao: UserDao

    @Before
    fun setup() {
        hiltRule.inject()
        dao = database.userDao()
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun insertUser() = runBlockingTest {
        val user = User("TEST_UID", "TEST_NAME")
        dao.insertUser(user)

        val authUser = dao.observeUser("TEST_UID").getOrAwaitValue()
        assertThat(authUser).isNotNull()
        assertThat(authUser).isEqualTo(user)
    }

    @Test
    fun deleteUser() = runBlockingTest{
        val user = User("TEST_UID", "TEST_NAME")
        dao.insertUser(user)
        dao.deleteUser(user)

        val authUser = dao.observeUser("TEST_UID").getOrAwaitValue()
        assertThat(authUser).isNull()
    }
}