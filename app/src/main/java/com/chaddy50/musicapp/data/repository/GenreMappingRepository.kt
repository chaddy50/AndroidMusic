package com.chaddy50.musicapp.data.repository

import com.chaddy50.musicapp.data.dao.GenreMappingDao

class GenreMappingRepository(
    private val genreMappingDao: GenreMappingDao
) {
    suspend fun getAllMappingsAsMap(): Map<String, String> {
        return genreMappingDao.getAllGenreMappings().associate { it.subGenreName to it.parentGenreName }
    }
}