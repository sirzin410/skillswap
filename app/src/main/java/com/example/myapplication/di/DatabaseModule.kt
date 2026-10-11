package com.example.myapplication.di

import android.content.Context
import androidx.room.Room
import com.example.myapplication.data.local.SkillSwapDao
import com.example.myapplication.data.local.SkillSwapDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): SkillSwapDatabase {
        return Room.databaseBuilder(
            context,
            SkillSwapDatabase::class.java,
            "skill_swap_db"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    fun provideSkillSwapDao(database: SkillSwapDatabase): SkillSwapDao {
        return database.skillSwapDao()
    }
}
