package com.mtm.uber_mimic.domain.usecase

import com.mtm.uber_mimic.domain.exceptions.GetSourcesException
import com.mtm.uber_mimic.domain.models.Source
import com.mtm.uber_mimic.domain.repo.SourceRepository
import com.mtm.uber_mimic.scheduler.SchedulerProvider
import kotlinx.coroutines.withContext
import timber.log.Timber

class GetSourcesUseCase(
    private val sourceRepository: SourceRepository,
    private val schedulerProvider: SchedulerProvider
) {
    suspend operator fun invoke(keyword: String = ""): List<Source> {
        return withContext(schedulerProvider.io()) {
            try {
                return@withContext if (keyword.isEmpty())
                    sourceRepository.getSources()
                else
                    sourceRepository.searchSources(keyword)
            } catch (throwable: Throwable) {
                Timber.e(throwable)
                throw GetSourcesException()
            }
        }
    }
}