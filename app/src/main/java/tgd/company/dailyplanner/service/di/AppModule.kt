package tgd.company.dailyplanner.service.di

import android.content.Context
import androidx.room.Room
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import tgd.company.dailyplanner.other.Constants.CUSTOM_EVENT_DATABASE_NAME
import tgd.company.dailyplanner.other.Constants.USER_DATABASE_NAME
import tgd.company.dailyplanner.service.firebase.customevent.CustomEventFirestore
import tgd.company.dailyplanner.service.firebase.user.UserAuthentication
import tgd.company.dailyplanner.service.firebase.user.UserFirestore
import tgd.company.dailyplanner.service.repositories.customevent.DefaultCustomEventRepository
import tgd.company.dailyplanner.service.repositories.customevent.ICustomEventRepository
import tgd.company.dailyplanner.service.repositories.user.DefaultUserRepository
import tgd.company.dailyplanner.service.repositories.user.IUserRepository
import tgd.company.dailyplanner.service.room.customevent.CustomEventDao
import tgd.company.dailyplanner.service.room.customevent.CustomEventDatabase
import tgd.company.dailyplanner.service.room.user.UserDao
import tgd.company.dailyplanner.service.room.user.UserDatabase
import javax.inject.Singleton

@ExperimentalCoroutinesApi
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideFirestoreInstance() = Firebase.firestore

    @Singleton
    @Provides
    fun provideAuthInstance() = Firebase.auth

    @Singleton
    @Provides
    fun provideUserDatabase(
            @ApplicationContext context: Context
    ) = Room.databaseBuilder(context, UserDatabase::class.java, USER_DATABASE_NAME).build()

    @Singleton
    @Provides
    fun provideUserDao(
            database: UserDatabase
    ) = database.userDao()

    @Singleton
    @Provides
    fun provideCustomEventDatabase(
            @ApplicationContext context: Context
    ) = Room.databaseBuilder(context, CustomEventDatabase::class.java, CUSTOM_EVENT_DATABASE_NAME).build()

    @Singleton
    @Provides
    fun provideCustomEventDao(
            database: CustomEventDatabase
    ) = database.customEventDao()

    @Singleton
    @Provides
    fun provideUserRepository(
        userAuthentication: UserAuthentication,
        userFirestore: UserFirestore,
        userDao: UserDao
    ) = DefaultUserRepository(userAuthentication, userFirestore, userDao) as IUserRepository


    @Singleton
    @Provides
    fun provideCustomEventRepository(
        customEventFirestore: CustomEventFirestore,
        customEventDao: CustomEventDao
    ) = DefaultCustomEventRepository(customEventFirestore, customEventDao) as ICustomEventRepository

}