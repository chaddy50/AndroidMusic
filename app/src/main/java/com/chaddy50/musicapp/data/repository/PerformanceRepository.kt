package com.chaddy50.musicapp.data.repository

import com.chaddy50.musicapp.data.dao.PerformanceDao
import com.chaddy50.musicapp.data.entity.Performance

interface IPerformanceRepository {
    suspend fun insert(performance: Performance): Long
    suspend fun findByAlbumAndArtist(albumId: Long, artistId: Long): Long?
}

class PerformanceRepository(
    private val performanceDao: PerformanceDao
) : IPerformanceRepository {
    override suspend fun insert(performance: Performance): Long {
        return performanceDao.insert(performance)
    }

    suspend fun update(performance: Performance) {
        performanceDao.update(performance)
    }

    suspend fun delete(performance: Performance) {
        performanceDao.delete(performance)
    }

    fun getPerformanceById(id: Long) = performanceDao.getPerformanceById(id)

    fun getPerformancesForAlbum(albumId: Long) = performanceDao.getPerformancesForAlbum(albumId)

    fun getPerformancesForAlbumForGenre(albumId: Long, genreId: Long) = performanceDao.getPerformancesForAlbumForGenre(albumId, genreId)

    fun getNumberOfPerformancesForAlbum(albumId: Long) = performanceDao.getNumberOfPerformancesForAlbum(albumId)

    override suspend fun findByAlbumAndArtist(albumId: Long, artistId: Long): Long? =
        performanceDao.findByAlbumAndArtist(albumId, artistId)
}