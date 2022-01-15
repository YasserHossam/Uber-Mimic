package com.mtm.uber_mimic.common.scheduler

import kotlinx.coroutines.Dispatchers

object DefaultSchedulerProvider : SchedulerProvider {
    override fun main() = Dispatchers.Main

    override fun io() = Dispatchers.IO
}