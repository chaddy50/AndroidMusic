package com.chaddy50.musicapp.data.api.openOpus

import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class FindComposerByNameTest {

    private val beethoven = OpenOpusComposer(
        id = 145,
        name = "Beethoven",
        completeName = "Ludwig van Beethoven",
        birthDate = "1770-12-17",
        deathDate = "1827-03-26",
        epoch = "Romantic",
        portraitUrl = null,
    )

    private val saintSaens = OpenOpusComposer(
        id = 167,
        name = "Saint-Saëns",
        completeName = "Camille Saint-Saëns",
        birthDate = "1835-10-09",
        deathDate = "1921-12-16",
        epoch = "Romantic",
        portraitUrl = null,
    )

    private val bach = OpenOpusComposer(
        id = 98,
        name = "Bach",
        completeName = "Johann Sebastian Bach",
        birthDate = "1685-03-31",
        deathDate = "1750-07-28",
        epoch = "Baroque",
        portraitUrl = null,
    )

    private fun emptyResponse() = ComposerSearchResponse(status = null, composers = null)
    private fun responseWith(composer: OpenOpusComposer) =
        ComposerSearchResponse(status = null, composers = listOf(composer))

    @Test
    fun exactMatchReturnsComposer() = runTest {
        val service = FakeOpenOpusService { responseWith(beethoven) }
        val repository = OpenOpusRepository(service)

        val result = repository.findComposerByName("Beethoven")

        assertEquals(beethoven, result)
        assertEquals(listOf("Beethoven"), service.queries)
    }

    @Test
    fun noMatchReturnsNull() = runTest {
        val service = FakeOpenOpusService { emptyResponse() }
        val repository = OpenOpusRepository(service)

        val result = repository.findComposerByName("Nobody")

        assertNull(result)
    }

    @Test
    fun strippedDiacriticsMatchOnSecondAttempt() = runTest {
        val responses = mutableListOf(
            emptyResponse(),         // "Saint-Saëns" — no match
            responseWith(saintSaens) // "Saint-Saens" — match
        )
        val service = FakeOpenOpusService { responses.removeFirst() }
        val repository = OpenOpusRepository(service)

        val result = repository.findComposerByName("Saint-Saëns")

        assertEquals(saintSaens, result)
        assertEquals(listOf("Saint-Saëns", "Saint-Saens"), service.queries)
    }

    @Test
    fun skipsDiacriticStepWhenNameHasNone() = runTest {
        val responses = mutableListOf(
            emptyResponse(),    // "Bach" — no match
            responseWith(bach)  // "Bach" word search — match
        )
        val service = FakeOpenOpusService { responses.removeFirst() }
        val repository = OpenOpusRepository(service)

        val result = repository.findComposerByName("Bach")

        assertEquals(bach, result)
        // Should skip diacritics step since "Bach" == stripDiacritics("Bach")
        assertEquals(listOf("Bach", "Bach"), service.queries)
    }

    @Test
    fun wordByWordSearchTriesEachWord() = runTest {
        val responses = mutableListOf(
            emptyResponse(), // "Johann Sebastian Bach" — no match
            emptyResponse(), // "Johann" — no match
            emptyResponse(), // "Sebastian" — no match
            responseWith(bach) // "Bach" — match
        )
        val service = FakeOpenOpusService { responses.removeFirst() }
        val repository = OpenOpusRepository(service)

        val result = repository.findComposerByName("Johann Sebastian Bach")

        assertEquals(bach, result)
        assertEquals(
            listOf("Johann Sebastian Bach", "Johann", "Sebastian", "Bach"),
            service.queries
        )
    }

    @Test
    fun wordByWordSplitsOnHyphens() = runTest {
        val responses = mutableListOf(
            emptyResponse(), // "Saint-Saëns" — no match
            emptyResponse(), // "Saint-Saens" — no match (diacritics stripped)
            emptyResponse(), // "Saint" — no match
            responseWith(saintSaens) // "Saens" — match
        )
        val service = FakeOpenOpusService { responses.removeFirst() }
        val repository = OpenOpusRepository(service)

        val result = repository.findComposerByName("Saint-Saëns")

        assertEquals(saintSaens, result)
        assertEquals(
            listOf("Saint-Saëns", "Saint-Saens", "Saint", "Saens"),
            service.queries
        )
    }

    @Test
    fun allStrategiesFailReturnsNull() = runTest {
        val service = FakeOpenOpusService { emptyResponse() }
        val repository = OpenOpusRepository(service)

        val result = repository.findComposerByName("Saint-Saëns")

        assertNull(result)
        // exact, diacritics-stripped, "Saint", "Saens"
        assertEquals(4, service.queries.size)
    }
}

private class FakeOpenOpusService(
    private val onSearch: () -> ComposerSearchResponse,
) : OpenOpusService {
    val queries = mutableListOf<String>()

    override suspend fun searchComposers(query: String): ComposerSearchResponse {
        queries.add(query)
        return onSearch()
    }
}