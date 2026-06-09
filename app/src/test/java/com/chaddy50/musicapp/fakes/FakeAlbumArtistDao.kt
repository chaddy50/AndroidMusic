package com.chaddy50.musicapp.fakes

import com.chaddy50.musicapp.data.dao.AlbumArtistDao
import com.chaddy50.musicapp.data.entity.AlbumArtist
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class FakeAlbumArtistDao(
    private val albumArtists: MutableStateFlow<List<AlbumArtist>> = MutableStateFlow(emptyList()),
) : AlbumArtistDao {
    override fun getAlbumArtistsForGenreIds(genreIds: List<Long>): Flow<List<AlbumArtist>> =
        albumArtists.map { list -> list.filter { it.genreId in genreIds } }

    override fun getAlbumArtistById(albumArtistId: Long): Flow<AlbumArtist?> =
        albumArtists.map { list -> list.find { it.id == albumArtistId } }

    override fun getAlbumArtistName(albumArtistId: Long): Flow<String?> =
        albumArtists.map { list -> list.find { it.id == albumArtistId }?.name }

    override fun getNumberOfAlbumArtistsForGenre(genreId: Long): Flow<Int> =
        albumArtists.map { list -> list.count { it.genreId == genreId } }

    override suspend fun insert(albumArtist: AlbumArtist): Long = TODO()
    override suspend fun update(albumArtist: AlbumArtist) = TODO()
    override suspend fun delete(albumArtist: AlbumArtist) = TODO()
    override fun getNumberOfAlbumArtists(): Flow<Int> = TODO()
    override fun getAlbumsForArtist(artistId: Long): Flow<List<AlbumArtist>> = TODO()
    override fun getAllAlbumArtists(): Flow<List<AlbumArtist>> = TODO()
    override fun getAlbumArtistByName(albumArtistName: String): AlbumArtist? = TODO()
}
