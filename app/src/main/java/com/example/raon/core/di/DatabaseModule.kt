package com.example.raon.core.di


import android.content.Context
import androidx.room.Room
import com.example.raon.core.database.AppDatabase
import com.example.raon.features.category.data.local.CategoryDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    // 1. AppDatabase 인스턴스를 제공하는 방법 (가장 먼저 필요)
    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "raon_database" // 👈 데이터베이스 파일명
        ).build()
    }

    // 2. ✨ CategoryDao를 제공하는 방법 (이 부분이 에러 해결의 핵심)
    @Provides
    @Singleton
    fun provideCategoryDao(appDatabase: AppDatabase): CategoryDao {
        // Hilt가 위에서 만들어준 AppDatabase 객체에서 categoryDao를 꺼내서 제공
        return appDatabase.categoryDao()
    }

    /*
    // 만약 다른 Dao가 있다면 이런 식으로 추가하면 됩니다.
    @Provides
    @Singleton
    fun provideUserDao(appDatabase: AppDatabase): UserDao {
        return appDatabase.userDao()
    }
    */
}