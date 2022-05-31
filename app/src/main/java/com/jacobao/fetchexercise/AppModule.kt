package com.jacobao.fetchexercise

import com.jacobao.fetchexercise.data.HiringRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    @Singleton
    fun provideHiringRepository() = HiringRepository()
}