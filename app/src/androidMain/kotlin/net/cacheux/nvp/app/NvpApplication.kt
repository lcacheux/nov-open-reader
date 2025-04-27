package net.cacheux.nvp.app

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.HiltAndroidApp
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import net.cacheux.nvp.app.repository.DatastorePreferencesRepository
import net.cacheux.nvp.app.repository.NvpPenInfoRepository
import net.cacheux.nvp.app.repository.PenInfoRepository
import net.cacheux.nvp.app.repository.PreferencesRepository
import net.cacheux.nvp.app.repository.StopConditionProvider
import net.cacheux.nvp.app.repository.StorageRepository
import net.cacheux.nvplib.storage.DoseStorage
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
    fun provideStorageRepository(doseStorage: DoseStorage): StorageRepository {
        return StorageRepository(doseStorage)
    }

    @Provides
    @Singleton
    fun provideDoseStorage(@ApplicationContext context: Context): DoseStorage {
        return RoomDoseStorage(
            databaseBuilder(context).build()
        )
    }
    
    @Provides
    @Singleton
    fun providePreferencesRepository(@ApplicationContext context: Context): PreferencesRepository {
        return DatastorePreferencesRepository(context)
    }
}

@HiltAndroidApp
class NvpApplication: Application()