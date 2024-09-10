package com.example.domain.di

import com.example.data.repository.AppRepository
import com.example.domain.DeleteNotificationByIdUseCase
import com.example.domain.DeleteProfileByIdUseCase
import com.example.domain.DeleteVisitByIdUseCase
import com.example.domain.GetNotificationsByIdVisitUseCase
import com.example.domain.GetPastNotificationsUseCase
import com.example.domain.GetProfileByIdUseCase
import com.example.domain.GetProfilesUseCase
import com.example.domain.GetVisitByIdUseCase
import com.example.domain.GetVisitsByIdProfileUseCase
import com.example.domain.GetVisitsUseCase
import com.example.domain.InsertNotificationUseCase
import com.example.domain.InsertProfileUseCase
import com.example.domain.InsertVisitUseCase
import com.example.domain.UpdateNotificationUseCase
import com.example.domain.UpdateProfileUseCase
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

    @Provides
    fun bindsInsertNotificationUseCase(appRepository: AppRepository): InsertNotificationUseCase {
        return InsertNotificationUseCase(appRepository)
    }

    @Provides
    fun bindsGetNotificationsByIdVisitUseCase(appRepository: AppRepository): GetNotificationsByIdVisitUseCase {
        return GetNotificationsByIdVisitUseCase(appRepository)
    }

    @Provides
    fun bindsDeleteNotificationByIdUseCase(appRepository: AppRepository): DeleteNotificationByIdUseCase {
        return DeleteNotificationByIdUseCase(appRepository)
    }

    @Provides
    fun bindsUpdateNotificationUseCase(appRepository: AppRepository): UpdateNotificationUseCase {
        return UpdateNotificationUseCase(appRepository)
    }

    @Provides
    fun bindsGetPastNotificationsUseCase(appRepository: AppRepository): GetPastNotificationsUseCase {
        return GetPastNotificationsUseCase(appRepository)
    }

    @Provides
    fun bindsInsertProfileUseCase(appRepository: AppRepository): InsertProfileUseCase {
        return InsertProfileUseCase(appRepository)
    }

    @Provides
    fun bindsUpdateProfileUseCaseUseCase(appRepository: AppRepository): UpdateProfileUseCase {
        return UpdateProfileUseCase(appRepository)
    }

    @Provides
    fun bindsDeleteProfileByIdUseCaseUseCase(appRepository: AppRepository): DeleteProfileByIdUseCase {
        return DeleteProfileByIdUseCase(appRepository)
    }

    @Provides
    fun bindsGetVisitsByIdProfileUseCase(appRepository: AppRepository): GetVisitsByIdProfileUseCase {
        return GetVisitsByIdProfileUseCase(appRepository)
    }

    @Provides
    fun bindsGetProfilesUseCaseUseCase(appRepository: AppRepository): GetProfilesUseCase {
        return GetProfilesUseCase(appRepository)
    }

    @Provides
    fun bindsGetProfileByIdUseCase(appRepository: AppRepository): GetProfileByIdUseCase {
        return GetProfileByIdUseCase(appRepository)
    }
}