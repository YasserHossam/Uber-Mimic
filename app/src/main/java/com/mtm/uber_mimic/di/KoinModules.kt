package com.mtm.uber_mimic.di

import com.mtm.uber_mimic.data.FirestoreSourceRepository
import com.mtm.uber_mimic.data.mappers.DefaultFirestoreSourceMapper
import com.mtm.uber_mimic.data.mappers.FirestoreSourceMapper
import com.mtm.uber_mimic.domain.repo.SourceRepository
import com.mtm.uber_mimic.domain.usecase.GetSourcesUseCase
import com.mtm.uber_mimic.scheduler.DefaultSchedulerProvider
import com.mtm.uber_mimic.scheduler.SchedulerProvider
import com.mtm.uber_mimic.tools.location.DefaultLocationHelper
import com.mtm.uber_mimic.tools.location.LocationHelper
import com.mtm.uber_mimic.ui.activities.RequestRideActivity
import com.mtm.uber_mimic.ui.models.mappers.DefaultLocationModelMapper
import com.mtm.uber_mimic.ui.models.mappers.LocationModelMapper
import com.mtm.uber_mimic.ui.viewmodel.RequestRideViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val mainModule = module {
    single<SchedulerProvider> { DefaultSchedulerProvider }
}

val requestRideModule = module {
    scope<RequestRideActivity> {
        /* Repos */
        factory<FirestoreSourceMapper> { DefaultFirestoreSourceMapper }
        factory<SourceRepository> { FirestoreSourceRepository(get()) }

        /* Use cases */
        factory { GetSourcesUseCase(get(), get()) }

        /* Helpers */
        scoped<LocationHelper> { DefaultLocationHelper(get<RequestRideActivity>()) }

        /* Mappers */
        factory<LocationModelMapper> { DefaultLocationModelMapper }

        /* ViewModel */
        viewModel { RequestRideViewModel(get(), get(), get()) }
    }
}