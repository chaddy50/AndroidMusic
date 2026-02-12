package com.chaddy50.musicapp.data.repository

import com.chaddy50.musicapp.data.dao.ComposerDao
import com.chaddy50.musicapp.data.entity.Composer
import kotlinx.coroutines.flow.Flow

class ComposerRepository(private val composerDao: ComposerDao) {
    suspend fun insert(composer: Composer): Int {
        return composerDao.insert(composer).toInt()
    }

    fun getComposerForAlbumArtist(albumArtistId: Int): Flow<Composer?> {
        return composerDao.getComposerForAlbumArtist(albumArtistId)
    }
}
