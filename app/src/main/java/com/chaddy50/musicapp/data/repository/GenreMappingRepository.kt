package com.chaddy50.musicapp.data.repository

import com.chaddy50.musicapp.data.dao.GenreMappingDao
import com.chaddy50.musicapp.data.entity.GenreMapping

class GenreMappingRepository(
    private val genreMappingDao: GenreMappingDao
) {
    suspend fun getAllMappingsAsMap(): Map<String, String> {
        return genreMappingDao.getAllGenreMappings().associate { it.subGenreName to it.parentGenreName }
    }

    suspend fun getClassicalGenreNames(): Set<String> {
        return genreMappingDao.getClassicalGenreNames().toSet()
    }

    suspend fun saveClassicalGenreNames(selectedNames: Set<String>) {
        genreMappingDao.deleteAllClassicalMappings()
        val mappings = selectedNames.map { GenreMapping(subGenreName = it, parentGenreName = "Classical") }
        genreMappingDao.insertAll(mappings)
    }

    suspend fun seedDefaultClassicalMappingsIfEmpty() {
        val allMappings = genreMappingDao.getAllGenreMappings()
        if (allMappings.isNotEmpty()) return

        val defaults = listOf(
            "Solo Piano", "Symphony", "String Quartet", "Piano Concerto",
            "Ballet", "Cello Concerto", "Horn with Orchestra", "Orchestra and Piano",
            "Orchestral", "Piano Quartet", "Piano Trio", "Piano with Orchestra",
            "Violin Concerto", "Violin Sonata", "Organ and Orchestra",
            "Piano and Orchestra", "Violin and Harp", "Cello Sonata",
            "Clarinet Quintet", "Clarinet Sonata", "Clarinet Trio",
            "Concerto for Violin, Cello, and Orchestra", "Horn Trio",
            "Piano Quintet", "Piano for Four Hands", "String Quintet",
            "String Sextet", "Viola Sonata",
        ).map { GenreMapping(subGenreName = it, parentGenreName = "Classical") }
        genreMappingDao.insertAll(defaults)
    }
}