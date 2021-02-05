package tgd.company.dailyplanner.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import tgd.company.dailyplanner.service.room.customevent.CustomEventDatabase
import tgd.company.dailyplanner.service.room.user.UserDatabase
import javax.inject.Named

@Module
@InstallIn(SingletonComponent::class)
object TestAppModule {

    @Provides
    @Named("user_test_db")
    fun provideInMemoryUserDb(@ApplicationContext context: Context) =
            Room.inMemoryDatabaseBuilder(context, UserDatabase::class.java)
                    .allowMainThreadQueries()
                    .build()

    @Provides
    @Named("custom_event_test_db")
    fun provideInMemoryCEDb(@ApplicationContext context: Context) =
            Room.inMemoryDatabaseBuilder(context, CustomEventDatabase::class.java)
                    .allowMainThreadQueries()
                    .build()
}