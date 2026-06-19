package com.chaddy50.froh.fakes

import com.chaddy50.froh.data.dao.AlbumArtistDao
import com.chaddy50.froh.data.entity.AlbumArtist
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class FakeAlbumArtistDao(
    private val albumArtists: MutableStateFlow<List<AlbumArtist>> = MutableStateFlow(emptyList()),
    initialGenreMap: Map<Long, Set<Long>> = emptyMap(),
) : AlbumArtistDao {
    val insertedArtists = mutableListOf<AlbumArtist>()
    val updatedArtists = mutableListOf<AlbumArtist>()
    var nextInsertId = 1L
    var lookupAfterInsertReturnsNull = false

    // Maps albumArtistId -> set of genreIds (simulates the track-based genre join)
    private val artistGenreMap = initialGenreMap.mapValues { it.value.toMutableSet() }.toMutableMap()

    fun setGenresForArtist(albumArtistId: Long, genreIds: Set<Long>) {
        artistGenreMap[albumArtistId] = genreIds.toMutableSet()
    }

    override suspend fun insert(albumArtist: AlbumArtist): Long {
        insertedArtists.add(albumArtist)
        val id = nextInsertId++
        if (!lookupAfterInsertReturnsNull) {
            albumArtists.value = albumArtists.value + albumArtist.copy(id = id)
        }
        return id
    }

    override suspend fun update(albumArtist: AlbumArtist) {
        updatedArtists.add(albumArtist)
        albumArtists.value = albumArtists.value.map {
            if (it.id == albumArtist.id) albumArtist else it
        }
    }

    override fun getAlbumArtistByName(albumArtistName: String): AlbumArtist? =
        albumArtists.value.find { it.name == albumArtistName }

    override fun getAlbumArtistsForGenreIds(genreIds: List<Long>): Flow<List<AlbumArtist>> =
        albumArtists.map { list ->
            list.filter { artist ->
                if (artist.id !in artistGenreMap) {
                    true // No explicit mapping — treat as belonging to all genres
                } else {
                    artistGenreMap[artist.id]!!.any { it in genreIds }
                }
            }
        }

    override fun getAlbumArtistById(albumArtistId: Long): Flow<AlbumArtist?> =
        albumArtists.map { list -> list.find { it.id == albumArtistId } }

    override fun getAlbumArtistName(albumArtistId: Long): Flow<String?> =
        albumArtists.map { list -> list.find { it.id == albumArtistId }?.name }

    override fun getNumberOfAlbumArtistsForGenre(genreId: Long): Flow<Int> =
        albumArtists.map { list ->
            list.count { artist ->
                if (artist.id !in artistGenreMap) {
                    true // No explicit mapping — treat as belonging to all genres
                } else {
                    genreId in artistGenreMap[artist.id]!!
                }
            }
        }

    override suspend fun updatePortraitPath(id: Long, portraitPath: String?) {
        albumArtists.value = albumArtists.value.map {
            if (it.id == id) it.copy(portraitPath = portraitPath) else it
        }
    }

    override suspend fun delete(albumArtist: AlbumArtist) = Unit
    override fun getNumberOfAlbumArtists(): Flow<Int> =
        albumArtists.map { it.size }
    override fun getAlbumsForArtist(artistId: Long): Flow<List<AlbumArtist>> =
        albumArtists.map { list -> list.filter { it.id == artistId } }
    override fun getAllAlbumArtists(): Flow<List<AlbumArtist>> = albumArtists

    override suspend fun getAlbumArtistsWithoutPortrait(): List<AlbumArtist> =
        albumArtists.value.filter { it.portraitPath == null }

    override suspend fun getGenreIdsForAlbumArtist(albumArtistId: Long): List<Long> =
        artistGenreMap[albumArtistId]?.toList() ?: emptyList()
}
