package tgd.company.dailyplanner.service.di

import android.content.Context
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.drawable.Drawable
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.room.Room
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import tgd.company.dailyplanner.R
import tgd.company.dailyplanner.other.Constants.CUSTOM_EVENT_DATABASE_NAME
import tgd.company.dailyplanner.other.Constants.FILE_ITEM_DATABASE_NAME
import tgd.company.dailyplanner.other.Constants.USER_DATABASE_NAME
import tgd.company.dailyplanner.service.firebase.customevent.CustomEventFirestore
import tgd.company.dailyplanner.service.firebase.user.UserAuthentication
import tgd.company.dailyplanner.service.firebase.user.UserFirestore
import tgd.company.dailyplanner.service.repositories.customevent.DefaultCustomEventRepository
import tgd.company.dailyplanner.service.repositories.customevent.ICustomEventRepository
import tgd.company.dailyplanner.service.repositories.fileitem.DefaultFileItemRepository
import tgd.company.dailyplanner.service.repositories.fileitem.IFileItemRepository
import tgd.company.dailyplanner.service.repositories.user.DefaultUserRepository
import tgd.company.dailyplanner.service.repositories.user.IUserRepository
import tgd.company.dailyplanner.service.room.customevent.CustomEventDao
import tgd.company.dailyplanner.service.room.customevent.CustomEventDatabase
import tgd.company.dailyplanner.service.room.fileitem.FileItemDao
import tgd.company.dailyplanner.service.room.fileitem.FileItemDatabase
import tgd.company.dailyplanner.service.room.user.UserDao
import tgd.company.dailyplanner.service.room.user.UserDatabase
import javax.inject.Singleton

@ExperimentalCoroutinesApi
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

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
    fun provideFileItemDatabase(
            @ApplicationContext context: Context
    ) = Room.databaseBuilder(context, FileItemDatabase::class.java, FILE_ITEM_DATABASE_NAME).build()

    @Singleton
    @Provides
    fun provideFileItemDao(
            database: FileItemDatabase
    ) = database.fileItemDao()

    @Singleton
    @Provides
    fun provideGlideInstance(
            @ApplicationContext context: Context
    ): RequestManager {
        val drawablePlaceholder: Drawable = ResourcesCompat.getDrawable(
                context.resources,
                R.drawable.ic_baseline_insert_drive_file_24,
                context.theme
        )!!
        drawablePlaceholder.setTint(context.resources.getColor(
                R.color.grey,
                context.theme
        ))

        val drawableError: Drawable = ResourcesCompat.getDrawable(
                context.resources,
                R.drawable.ic_baseline_insert_drive_file_24,
                context.theme
        )!!
        drawableError.setTint(context.resources.getColor(
                R.color.grey,
                context.theme
        ))

        return Glide.with(context).applyDefaultRequestOptions(
                RequestOptions()
                        .placeholder(drawablePlaceholder)
                        .error(drawableError)
        )
    }

    @Singleton
    @Provides
    fun provideUserRepository(
            userAuthentication: UserAuthentication = UserAuthentication(),
            userFirestore: UserFirestore = UserFirestore(),
            userDao: UserDao
    ) = DefaultUserRepository(userAuthentication, userFirestore, userDao) as IUserRepository

    @Singleton
    @Provides
    fun provideCustomEventRepository(
        customEventFirestore: CustomEventFirestore = CustomEventFirestore(),
        customEventDao: CustomEventDao
    ) = DefaultCustomEventRepository(customEventFirestore, customEventDao) as ICustomEventRepository

    @Singleton
    @Provides
    fun provideFileItemRepository(
            fileItemDao: FileItemDao
    ) = DefaultFileItemRepository(fileItemDao) as IFileItemRepository

}