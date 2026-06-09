package com.chaddy50.musicapp.fakes

import com.chaddy50.musicapp.data.dao.ComposerDao
import com.chaddy50.musicapp.data.entity.Composer
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class FakeComposerDao(
    private val composers: MutableStateFlow<List<Composer>> = MutableStateFlow(emptyList()),
) : ComposerDao {
    override suspend fun insert(composer: Composer): Long = 0L

    override fun getComposerForAlbumArtist(albumArtistId: Long): Flow<Composer?> =
        composers.map { list -> list.find { it.albumArtistId == albumArtistId } }
}
