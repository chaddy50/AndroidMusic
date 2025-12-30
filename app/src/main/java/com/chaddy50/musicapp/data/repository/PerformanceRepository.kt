package com.chaddy50.musicapp.data.repository

import com.chaddy50.musicapp.data.dao.PerformanceDao
import com.chaddy50.musicapp.data.entity.Performance

class PerformanceRepository(
    private val performanceDao: PerformanceDao
) {
    suspend fun insert(performance: Performance): Int {
        return performanceDao.insert(performance).toInt()
    }

    suspend fun update(performance: Performance) {
        performanceDao.update(performance)
    }

    suspend fun delete(performance: Performance) {
        performanceDao.delete(performance)
    }

    fun getPerformanceById(id: Int) = performanceDao.getPerformanceById(id)

    fun getPerformancesForAlbum(albumId: Int) = performanceDao.getPerformancesForAlbum(albumId)

    fun getNumberOfPerformancesForAlbum(albumId: Int) = performanceDao.getNumberOfPerformancesForAlbum(albumId)
}