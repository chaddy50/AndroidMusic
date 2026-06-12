package com.chaddy50.musicapp.fakes

import com.chaddy50.musicapp.data.dao.AlbumArtistDao
import com.chaddy50.musicapp.data.entity.AlbumArtist
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class FakeAlbumArtistDao(
    private val albumArtists: MutableStateFlow<List<AlbumArtist>> = MutableStateFlow(emptyList()),
) : AlbumArtistDao {
    val insertedArtists = mutableListOf<AlbumArtist>()
    val updatedArtists = mutableListOf<AlbumArtist>()
    var nextInsertId = 1L
    var lookupAfterInsertReturnsNull = false

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
        albumArtists.map { list -> list.filter { it.genreId in genreIds } }

    override fun getAlbumArtistById(albumArtistId: Long): Flow<AlbumArtist?> =
        albumArtists.map { list -> list.find { it.id == albumArtistId } }

    override fun getAlbumArtistName(albumArtistId: Long): Flow<String?> =
        albumArtists.map { list -> list.find { it.id == albumArtistId }?.name }

    override fun getNumberOfAlbumArtistsForGenre(genreId: Long): Flow<Int> =
        albumArtists.map { list -> list.count { it.genreId == genreId } }

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
}
