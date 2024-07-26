package com.example.domain.di

import com.example.data.repository.AppRepository
import com.example.domain.DeleteVisitByIdUseCase
import com.example.domain.GetVisitByIdUseCase
import com.example.domain.GetVisitsUseCase
import com.example.domain.InsertVisitUseCase
import com.example.domain.UpdateVisitUseCase
import dagger.Module
import dagger.Provides

@Module
class DomainModule {
    @Provides
    fun bindsInsertVisitUseCase(appRepository: AppRepository): InsertVisitUseCase {
        return InsertVisitUseCase(appRepository)
    }

    @Provides
    fun bindsUpdateVisitUseCase(appRepository: AppRepository): UpdateVisitUseCase {
        return UpdateVisitUseCase(appRepository)
    }

    @Provides
    fun bindsGetVisitsUseCase(appRepository: AppRepository): GetVisitsUseCase {
        return GetVisitsUseCase(appRepository)
    }

    @Provides
    fun bindsGetVisitByIdUseCase(appRepository: AppRepository): GetVisitByIdUseCase {
        return GetVisitByIdUseCase(appRepository)
    }

    @Provides
    fun bindsDeleteVisitByIdUseCase(appRepository: AppRepository): DeleteVisitByIdUseCase {
        return DeleteVisitByIdUseCase(appRepository)
    }
}