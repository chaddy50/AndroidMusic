package com.chaddy50.musicapp.data.repository

import com.chaddy50.musicapp.data.entity.GenreMapping
import com.chaddy50.musicapp.fakes.FakeGenreMappingDao
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GenreMappingRepositoryTest {

    @Test
    fun getClassicalGenreNamesReturnsEmptySetWhenNoMappings() = runTest {
        val dao = FakeGenreMappingDao()
        val repo = GenreMappingRepository(dao)
        assertEquals(emptySet<String>(), repo.getClassicalGenreNames())
    }

    @Test
    fun getClassicalGenreNamesReturnsOnlyClassicalMappings() = runTest {
        val dao = FakeGenreMappingDao()
        dao.mappings.addAll(listOf(
            GenreMapping("Symphony", "Classical"),
            GenreMapping("Heavy Metal", "Rock"),
            GenreMapping("Solo Piano", "Classical"),
        ))
        val repo = GenreMappingRepository(dao)
        assertEquals(setOf("Symphony", "Solo Piano"), repo.getClassicalGenreNames())
    }

    @Test
    fun saveClassicalGenreNamesReplacesExisting() = runTest {
        val dao = FakeGenreMappingDao()
        dao.mappings.add(GenreMapping("Symphony", "Classical"))
        dao.mappings.add(GenreMapping("Heavy Metal", "Rock"))

        val repo = GenreMappingRepository(dao)
        repo.saveClassicalGenreNames(setOf("Solo Piano", "Ballet"))

        val classicalNames = dao.getClassicalGenreNames()
        assertEquals(2, classicalNames.size)
        assertTrue("Solo Piano" in classicalNames)
        assertTrue("Ballet" in classicalNames)
        // Non-classical mapping should be preserved
        assertTrue(dao.mappings.any { it.subGenreName == "Heavy Metal" && it.parentGenreName == "Rock" })
    }

    @Test
    fun saveClassicalGenreNamesWithEmptySetClearsAll() = runTest {
        val dao = FakeGenreMappingDao()
        dao.mappings.add(GenreMapping("Symphony", "Classical"))
        val repo = GenreMappingRepository(dao)
        repo.saveClassicalGenreNames(emptySet())
        assertEquals(0, dao.getClassicalGenreNames().size)
    }

    @Test
    fun seedDefaultsOnlyWhenTableIsEmpty() = runTest {
        val dao = FakeGenreMappingDao()
        val repo = GenreMappingRepository(dao)
        repo.seedDefaultClassicalMappingsIfEmpty()
        val names = repo.getClassicalGenreNames()
        assertTrue(names.contains("Symphony"))
        assertTrue(names.contains("Solo Piano"))
        assertTrue(names.size > 20)
    }

    @Test
    fun seedDefaultsDoesNothingWhenMappingsExist() = runTest {
        val dao = FakeGenreMappingDao()
        dao.mappings.add(GenreMapping("CustomGenre", "Classical"))
        val repo = GenreMappingRepository(dao)
        repo.seedDefaultClassicalMappingsIfEmpty()
        // Should not have added defaults
        assertEquals(1, dao.mappings.size)
        assertEquals("CustomGenre", dao.mappings[0].subGenreName)
    }
}
