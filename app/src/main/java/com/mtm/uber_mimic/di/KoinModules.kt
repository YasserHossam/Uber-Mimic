package com.mtm.uber_mimic.di

import com.mtm.uber_mimic.data.destinations.FoursquareApi
import com.mtm.uber_mimic.data.destinations.FoursquareLocationRepository
import com.mtm.uber_mimic.data.destinations.mappers.DefaultFoursquareLocationMapper
import com.mtm.uber_mimic.data.destinations.mappers.FoursquareLocationMapper
import com.mtm.uber_mimic.data.sources.FirestoreLocationRepository
import com.mtm.uber_mimic.data.sources.mappers.DefaultFirestoreLocationMapper
import com.mtm.uber_mimic.data.sources.mappers.FirestoreLocationMapper
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
import org.koin.core.qualifier.named
import org.koin.core.scope.get
import org.koin.dsl.module
import retrofit2.Retrofit

val mainModule = module {
    single<SchedulerProvider> { DefaultSchedulerProvider }
}

val requestRideModule = module {
    val sourceRepoName = "source_repo"
    val destinationRepoName = "destination_repo"

    val sourcesUseCase = "sources_use_case"
    val destinationsUseCase = "destinations_use_case"

    scope<RequestRideActivity> {

        /* Helpers */
        scoped<LocationHelper> { DefaultLocationHelper(get<RequestRideActivity>()) }

        /* Apis */
        factory<FoursquareApi> {
            val retrofit: Retrofit = get(Retrofit::class.java)
            retrofit.create(FoursquareApi::class.java)
        }

        /* Repos */
        factory<FirestoreLocationMapper> { DefaultFirestoreLocationMapper }
        factory<LocationRepository>(named(sourceRepoName)) { FirestoreLocationRepository(get()) }

        factory<FoursquareLocationMapper> { DefaultFoursquareLocationMapper }
        factory<LocationRepository>(named(destinationRepoName)) {
            FoursquareLocationRepository(get(), get(), get())
        }

        /* Use cases */
        factory<GetLocationsUseCase>(named(sourcesUseCase)) {
            DefaultGetLocationsUseCase(get(named(sourceRepoName)), get())
        }

        factory<GetLocationsUseCase>(named(destinationsUseCase)) {
            DefaultGetLocationsUseCase(get(named(destinationRepoName)), get())
        }

        /* Mappers */
        factory<LocationModelMapper> { DefaultLocationModelMapper }

        /* ViewModel */
        viewModel {
            RequestRideViewModel(
                get(),
                get(named(sourcesUseCase)),
                get(named(destinationsUseCase)),
                get()
            )
        }
    }
}