package com.mtm.uber_mimic.domain.repo

import com.mtm.uber_mimic.domain.models.Source

interface SourceRepository {
    suspend fun getSources(): List<Source>

    suspend fun searchSources(keyword: String): List<Source>
}