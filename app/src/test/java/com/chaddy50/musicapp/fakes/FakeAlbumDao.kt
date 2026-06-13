package com.chaddy50.musicapp.fakes

import com.chaddy50.musicapp.data.dao.AlbumDao
import com.chaddy50.musicapp.data.entity.Album
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class FakeAlbumDao(
    private val albums: MutableStateFlow<List<Album>> = MutableStateFlow(emptyList()),
) : AlbumDao {
    override fun getAlbumById(id: Long): Flow<Album?> =
        albums.map { list -> list.find { it.id == id } }

    override fun getAllAlbums(): Flow<List<Album>> = albums

    override fun getAlbumsForArtist(artistId: Long, shouldSortByCatalogueSortIndex: Boolean): Flow<List<Album>> =
        albums.map { list -> list.filter { it.artistId == artistId } }

    override fun getAlbumsForArtistInGenre(
        albumArtistId: Long,
        genreId: Long,
        shouldSortByCatalogueSortIndex: Boolean,
    ): Flow<List<Album>> =
        albums.map { list -> list.filter { it.artistId == albumArtistId } }

    override fun getAlbumName(albumId: Long): Flow<String?> =
        albums.map { list -> list.find { it.id == albumId }?.title }

    override fun getNumberOfAlbumsForAlbumArtist(albumArtistId: Long): Flow<Int> =
        albums.map { list -> list.count { it.artistId == albumArtistId } }

    override fun getNumberOfAlbumsForAlbumArtistInGenre(albumArtistId: Long, genreId: Long): Flow<Int> =
        albums.map { list -> list.count { it.artistId == albumArtistId } }

    override suspend fun insert(album: Album) = Unit
    override suspend fun update(album: Album) = Unit
    override suspend fun delete(album: Album) = Unit
    override fun getNumberOfAlbums(): Flow<Int> = albums.map { it.size }
}
