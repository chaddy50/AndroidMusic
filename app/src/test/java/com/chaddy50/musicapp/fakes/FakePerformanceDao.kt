package com.chaddy50.musicapp.fakes

import com.chaddy50.musicapp.data.dao.PerformanceDao
import com.chaddy50.musicapp.data.entity.Performance
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class FakePerformanceDao(
    private val performances: MutableStateFlow<List<Performance>> = MutableStateFlow(emptyList()),
) : PerformanceDao {
    override fun getPerformanceById(id: Long): Flow<Performance> =
        performances.map { list -> list.first { it.id == id } }

    override fun getPerformancesForAlbum(albumId: Long): Flow<List<Performance>> =
        performances.map { list -> list.filter { it.albumId == albumId } }

    override fun getPerformancesForAlbumForGenre(albumId: Long, genreId: Long): Flow<List<Performance>> =
        performances.map { list -> list.filter { it.albumId == albumId && it.genreId == genreId } }

    override fun getNumberOfPerformancesForAlbum(albumId: Long): Flow<Int> =
        performances.map { list -> list.count { it.albumId == albumId } }

    override suspend fun findByAlbumAndArtist(albumId: Long, artistId: Long): Long? =
        performances.value.find { it.albumId == albumId && it.artistId == artistId }?.id

    override suspend fun insert(performance: Performance): Long = 0L
    override suspend fun update(performance: Performance) = Unit
    override suspend fun delete(performance: Performance) = Unit
}
