package net.cacheux.nvp.app

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import net.cacheux.nvp.app.repository.DatastorePreferencesRepository
import net.cacheux.nvp.app.repository.PenInfoRepository
import net.cacheux.nvp.app.repository.PreferencesRepository
import net.cacheux.nvp.app.repository.StopConditionProvider
import net.cacheux.nvp.app.repository.StorageRepository
import net.cacheux.nvplib.storage.DoseStorage
import net.cacheux.nvplib.storage.room.NvpDatabase
import net.cacheux.nvplib.storage.room.RoomDoseStorage
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [NvpModule::class]
)
class NvpTestModule {
    @Provides
    @Singleton
    fun providePenInfoRepository(): PenInfoRepository {
        return TestingPenInfoRepository()
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
            Room.inMemoryDatabaseBuilder(context, NvpDatabase::class.java).build()
        )
    }

    @Provides
    @Singleton
    fun providePreferencesRepository(@ApplicationContext context: Context): PreferencesRepository {
        return DatastorePreferencesRepository(context)
    }
}
