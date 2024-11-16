package net.cacheux.nvp.app

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.HiltAndroidApp
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import net.cacheux.nvp.app.repository.NvpPenInfoRepository
import net.cacheux.nvplib.storage.room.RoomDoseStorage
import net.cacheux.nvplib.storage.room.databaseBuilder
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NvpModule {
    @Provides
    @Singleton
    fun providePenInfoRepository(stopConditionProvider: StopConditionProvider): PenInfoRepository {
        return NvpPenInfoRepository(
            stopConditionProvider
        )
    }

    @Provides
    @Singleton
    fun provideStopConditionProvider(storageRepository: StorageRepository): StopConditionProvider {
        return storageRepository
    }

    @Provides
    @Singleton
    fun provideStorageRepository(@ApplicationContext context: Context): StorageRepository {
        return StorageRepository(RoomDoseStorage(
            databaseBuilder(context).build()
            /*Room.databaseBuilder<NvpDatabase>(
                context.applicationContext,
                context.getDatabasePath("nvp.db").absolutePath
            ).build()*/
        ))
    }
}

@HiltAndroidApp
class NvpApplication: Application()