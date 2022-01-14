package com.mtm.uber_mimic.di

import com.mtm.uber_mimic.tools.location.DefaultLocationHelper
import com.mtm.uber_mimic.tools.location.LocationHelper
import com.mtm.uber_mimic.ui.activities.RequestRideActivity
import com.mtm.uber_mimic.ui.viewmodel.RequestRideViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val requestRideModule = module {
    scope<RequestRideActivity> {
        scoped<LocationHelper> { DefaultLocationHelper(get<RequestRideActivity>()) }
        viewModel { RequestRideViewModel(get()) }
    }
}