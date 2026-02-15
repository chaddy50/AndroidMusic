package com.chaddy50.musicapp.data.scanner.processor

import com.chaddy50.musicapp.data.repository.GenreRepository
import com.chaddy50.musicapp.data.scanner.util.CursorData

private val GENRES_WITHOUT_ARTIST_ARTWORK = listOf("Anime", "Movie", "Video Game")

class GenreProcessor(
    private val genreRepository: GenreRepository,
) {
    private var parentGenreIdCache: MutableMap<String, Long> = mutableMapOf()
    private val genreIdCache: MutableMap<String, Long> = mutableMapOf()
    private var classicalGenreMappings: Map<String, String> = emptyMap()
    private val GENRE_CLASSICAL = "Classical"

    suspend fun process(
        cursorData: CursorData
    ): GenreProcessorResponse {
        val genreName = cursorData.genreName ?: "Unknown Genre"
        val parentGenreId = getParentGenreId(genreName)
        val isClassical = classicalGenreMappings[genreName] == GENRE_CLASSICAL

        val genreId = genreIdCache.getOrPut(genreName) {
            genreRepository.findOrInsertGenreByName(genreName, parentGenreId)
        }

        return GenreProcessorResponse(
            genreId,
            genreName,
            parentGenreId,
            isClassical,
        )
    }

    private fun getParentGenreId(genreName: String): Long? {
        val parentGenreName = classicalGenreMappings[genreName] ?: return null
        return parentGenreIdCache[parentGenreName]
    }

    suspend fun setUpClassicalMappings() {
        // This is how I should eventually do this once the mappings are configurable by the user
//        genreMappings = genreMappingRepository.getAllMappingsAsMap()
//        val parentGenreNames = genreMappings.values.distinct()
//        for (parentGenreName in parentGenreNames) {
//            val parentId = genreRepository.findOrInsertGenreByName(parentGenreName)
//            parentGenreIdCache[parentGenreName] = parentId
//        }

        // For now, hard coding everything
        classicalGenreMappings = mapOf(
            "Solo Piano" to "Classical",
            "Symphony" to "Classical",
            "String Quartet" to "Classical",
            "Piano Concerto" to "Classical",
            "Symphony" to "Classical",
            "Ballet" to "Classical",
            "Cello Concerto" to "Classical",
            "Horn with Orchestra" to "Classical",
            "Orchestra and Piano" to "Classical",
            "Orchestral" to "Classical",
            "Piano Quartet" to "Classical",
            "Piano Trio" to "Classical",
            "Piano with Orchestra" to "Classical",
            "Violin Concerto" to "Classical",
            "Violin Sonata" to "Classical",
            "Organ and Orchestra" to "Classical",
            "Piano and Orchestra" to "Classical",
            "Violin and Harp" to "Classical",
            "Cello Sonata" to "Classical",
            "Clarinet Quintet" to "Classical",
            "Clarinet Sonata" to "Classical",
            "Clarinet Trio" to "Classical",
            "Concerto for Violin, Cello, and Orchestra" to "Classical",
            "Horn Trio" to "Classical",
            "Piano Quintet" to "Classical",
            "Piano for Four Hands" to "Classical",
            "String Quintet" to "Classical",
            "String Sextet" to "Classical",
            "Viola Sonata" to "Classical",
        )

        val classicalId = genreRepository.findOrInsertGenreByName(GENRE_CLASSICAL)
        parentGenreIdCache[GENRE_CLASSICAL] = classicalId
    }
}

data class GenreProcessorResponse(
    val genreId: Long,
    val genreName: String,
    val parentGenreId: Long?,
    val isClassical: Boolean,
)

fun shouldFetchArtistArtworkForGenre(genreName: String?): Boolean {
    if (genreName == null) return false
    return genreName !in GENRES_WITHOUT_ARTIST_ARTWORK
}