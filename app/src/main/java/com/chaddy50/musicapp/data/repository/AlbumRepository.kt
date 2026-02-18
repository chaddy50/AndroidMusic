package com.chaddy50.musicapp.data.repository

import com.chaddy50.musicapp.data.dao.AlbumDao
import com.chaddy50.musicapp.data.entity.Album

interface IAlbumRepository {
    suspend fun insert(album: Album)
}

class AlbumRepository(private val albumDao: AlbumDao) : IAlbumRepository {
    override suspend fun insert(album: Album) {
        albumDao.insert(album)
    }

    suspend fun update(album: Album) {
        albumDao.update(album)
    }

    suspend fun delete(album: Album) {
        albumDao.delete(album)
    }

    fun getNumberOfAlbums() = albumDao.getNumberOfAlbums()

    fun getAllAlbums() = albumDao.getAllAlbums()

    fun getAlbumsForArtist(
        albumArtistId: Long,
        shouldSortByCatalogueNumber: Boolean,
    ) = albumDao.getAlbumsForArtist(albumArtistId, shouldSortByCatalogueNumber)

    fun getAlbumsForArtistInGenre(
        albumArtistId: Long,
        genreId: Long,
        shouldSortByCatalogueNumber: Boolean,
    ) = albumDao.getAlbumsForArtistInGenre(albumArtistId, genreId, shouldSortByCatalogueNumber)

    fun getAlbumById(id: Long) = albumDao.getAlbumById(id)

    fun getAlbumName(albumId: Long) = albumDao.getAlbumName(albumId)

    fun getNumberOfAlbumsForAlbumArtist(albumArtistId: Long) = albumDao.getNumberOfAlbumsForAlbumArtist(albumArtistId)

    fun getNumberOfAlbumsForAlbumArtistInGenre(albumArtistId: Long, genreId: Long) = albumDao.getNumberOfAlbumsForAlbumArtistInGenre(albumArtistId, genreId)
}
