package com.chaddy50.froh.fakes

import com.chaddy50.froh.data.dao.GenreMappingDao
import com.chaddy50.froh.data.entity.GenreMapping

class FakeGenreMappingDao : GenreMappingDao {
    val mappings = mutableListOf<GenreMapping>()

    override suspend fun insertAll(mappings: List<GenreMapping>) {
        for (mapping in mappings) {
            this.mappings.removeAll { it.subGenreName == mapping.subGenreName }
            this.mappings.add(mapping)
        }
    }

    override suspend fun getAllGenreMappings(): List<GenreMapping> = mappings.toList()

    override suspend fun getClassicalGenreNames(): List<String> =
        mappings.filter { it.parentGenreName == "Classical" }.map { it.subGenreName }

    override suspend fun deleteAllClassicalMappings() {
        mappings.removeAll { it.parentGenreName == "Classical" }
    }
}
