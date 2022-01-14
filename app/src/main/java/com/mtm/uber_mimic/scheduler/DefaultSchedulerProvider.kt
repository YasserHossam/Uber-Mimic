package com.mtm.uber_mimic.scheduler

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class DefaultSchedulerProvider : SchedulerProvider {
    override fun main() = Dispatchers.Main

    override fun io() = Dispatchers.IO
}