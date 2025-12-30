package com.chaddy50.musicapp.data.repository

import com.chaddy50.musicapp.data.dao.AlbumDao
import com.chaddy50.musicapp.data.entity.Album

class AlbumRepository(private val albumDao: AlbumDao) {
    suspend fun insert(album: Album) {
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

    fun getAlbumsForArtist(albumArtistId: Int) = albumDao.getAlbumsForArtist(albumArtistId)

    fun getAlbumsForArtistInGenre(albumArtistId: Int, genreId: Int) = albumDao.getAlbumsForArtistInGenre(albumArtistId, genreId)

    fun getAlbumById(id: Int) = albumDao.getAlbumById(id)

    fun getAlbumName(albumId: Int) = albumDao.getAlbumName(albumId)

    fun getNumberOfAlbumsForAlbumArtist(albumArtistId: Int) = albumDao.getNumberOfAlbumsForAlbumArtist(albumArtistId)

    fun getNumberOfAlbumsForAlbumArtistInGenre(albumArtistId: Int, genreId: Int) = albumDao.getNumberOfAlbumsForAlbumArtistInGenre(albumArtistId, genreId)
}
