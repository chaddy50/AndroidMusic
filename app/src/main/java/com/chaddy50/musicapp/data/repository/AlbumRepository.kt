package com.chaddy50.musicapp.data.repository

import com.chaddy50.musicapp.data.dao.AlbumDao
import com.chaddy50.musicapp.data.entity.Album

class AlbumRepository(private val albumDao: AlbumDao) {

    fun getAllAlbums() = albumDao.getAllAlbums()

    fun getAlbumsForArtist(albumArtistId: Int) = albumDao.getAlbumsForArtist(albumArtistId)

    fun getAlbumById(id: Int) = albumDao.getAlbumById(id)

    fun getAlbumName(albumId: Int) = albumDao.getAlbumName(albumId)

    suspend fun insert(album: Album) {
        albumDao.insert(album)
    }

    suspend fun update(album: Album) {
        albumDao.update(album)
    }

    suspend fun delete(album: Album) {
        albumDao.delete(album)
    }
}
