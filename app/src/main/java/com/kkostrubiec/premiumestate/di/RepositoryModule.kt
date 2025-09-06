package com.kkostrubiec.premiumestate.di

import com.kkostrubiec.premiumestate.data.repository.PropertyRepositoryImpl
import com.kkostrubiec.premiumestate.domain.repository.PropertyRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun propertyRepository(
        propertyRepositoryImpl: PropertyRepositoryImpl
    ): PropertyRepository
}
