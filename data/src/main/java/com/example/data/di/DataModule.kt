package com.example.data.di

import com.example.data.repository.AppRepository
import com.example.data.repository.AppRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface DataModule {
    @Binds
    fun bindsAppRepository(
        appRepository: AppRepositoryImpl,
    ): AppRepository
}