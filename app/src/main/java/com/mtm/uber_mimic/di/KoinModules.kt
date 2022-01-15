package com.mtm.uber_mimic.di

import com.mtm.uber_mimic.data.FirestoreLocationRepository
import com.mtm.uber_mimic.data.mappers.DefaultFirestoreLocationMapper
import com.mtm.uber_mimic.data.mappers.FirestoreLocationMapper
import com.mtm.uber_mimic.domain.repo.LocationRepository
import com.mtm.uber_mimic.domain.usecase.DefaultGetLocationsUseCase
import com.mtm.uber_mimic.domain.usecase.GetLocationsUseCase
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
        factory<FirestoreLocationMapper> { DefaultFirestoreLocationMapper }
        factory<LocationRepository> { FirestoreLocationRepository(get()) }

        /* Use cases */
        factory<GetLocationsUseCase> { DefaultGetLocationsUseCase(get(), get()) }

        /* Helpers */
        scoped<LocationHelper> { DefaultLocationHelper(get<RequestRideActivity>()) }

        /* Mappers */
        factory<LocationModelMapper> { DefaultLocationModelMapper }

        /* ViewModel */
        viewModel { RequestRideViewModel(get(), get(), get()) }
    }
}