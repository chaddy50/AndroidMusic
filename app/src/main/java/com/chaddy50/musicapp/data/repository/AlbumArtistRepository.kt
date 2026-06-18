package com.chaddy50.musicapp.data.repository

import com.chaddy50.musicapp.data.api.audioDb.IAudioDbRepository
import com.chaddy50.musicapp.data.dao.AlbumArtistDao
import com.chaddy50.musicapp.data.entity.AlbumArtist
import com.chaddy50.musicapp.utilities.stripArticles
import kotlinx.coroutines.flow.Flow

interface IAlbumArtistRepository {
    suspend fun findOrInsertAlbumArtist(albumArtistName: String): Long
}

class AlbumArtistRepository(
    private val albumArtistDao: AlbumArtistDao,
    private val audioDbRepository: IAudioDbRepository,
) : IAlbumArtistRepository {
    suspend fun insert(albumArtist: AlbumArtist) {
        albumArtistDao.insert(albumArtist)
    }

    suspend fun update(albumArtist: AlbumArtist) {
        albumArtistDao.update(albumArtist)
    }

    suspend fun delete(albumArtist: AlbumArtist) {
        albumArtistDao.delete(albumArtist)
    }

    fun getNumberOfAlbumArtists() = albumArtistDao.getNumberOfAlbumArtists()

    fun getAllAlbumArtists(): Flow<List<AlbumArtist>> = albumArtistDao.getAllAlbumArtists()

    fun getAlbumArtistName(albumArtistId: Long) = albumArtistDao.getAlbumArtistName(albumArtistId)

    fun getAlbumArtistById(albumArtistId: Long) = albumArtistDao.getAlbumArtistById(albumArtistId)

    fun getNumberOfAlbumArtistsForGenre(genreId: Long) = albumArtistDao.getNumberOfAlbumArtistsForGenre(genreId)

    override suspend fun findOrInsertAlbumArtist(
        albumArtistName: String,
    ): Long {
        val existingAlbumArtist = albumArtistDao.getAlbumArtistByName(albumArtistName)
        if (existingAlbumArtist != null) return existingAlbumArtist.id

        val newAlbumArtist = AlbumArtist(
            name = albumArtistName,
            sortName = stripArticles(albumArtistName),
        )
        albumArtistDao.insert(newAlbumArtist)
        return albumArtistDao.getAlbumArtistByName(albumArtistName)?.id ?: -1
    }

    fun getAlbumArtistsForGenre(genreId: Long): Flow<List<AlbumArtist>> {
        return albumArtistDao.getAlbumArtistsForGenreIds(listOf(genreId))
    }

    suspend fun getGenreIdsForAlbumArtist(albumArtistId: Long): List<Long> =
        albumArtistDao.getGenreIdsForAlbumArtist(albumArtistId)

    suspend fun getAlbumArtistsWithoutPortrait(): List<AlbumArtist> =
        albumArtistDao.getAlbumArtistsWithoutPortrait()

    suspend fun fetchAndUpdatePortrait(
        albumArtist: AlbumArtist,
    ) {
        val portraitPath = audioDbRepository.fetchArtistPortraitUrl(albumArtist.name)
        update(albumArtist.copy(portraitPath = portraitPath))
    }
}
