package com.mtm.uber_mimic.common.scheduler

import kotlinx.coroutines.CoroutineDispatcher

interface SchedulerProvider {
    fun main(): CoroutineDispatcher

    fun io(): CoroutineDispatcher
}