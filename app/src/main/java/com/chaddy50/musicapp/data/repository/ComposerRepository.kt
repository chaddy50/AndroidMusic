package com.chaddy50.musicapp.data.repository

import com.chaddy50.musicapp.data.api.openOpus.OpenOpusRepository
import com.chaddy50.musicapp.data.dao.ComposerDao
import com.chaddy50.musicapp.data.entity.Composer
import com.chaddy50.musicapp.data.util.ArtworkDownloader
import kotlinx.coroutines.flow.Flow

interface IComposerRepository {
    suspend fun fetchAndInsertComposer(albumArtistId: Long, albumArtistName: String)
}

class ComposerRepository(
    private val composerDao: ComposerDao,
    private val openOpusRepository: OpenOpusRepository,
    private val artworkDownloader: ArtworkDownloader,
) : IComposerRepository {
    suspend fun insert(composer: Composer): Int {
        return composerDao.insert(composer).toInt()
    }

    override suspend fun fetchAndInsertComposer(
        albumArtistId: Long,
        albumArtistName: String,
    ) {
        if (albumArtistName.isEmpty()) return
        try {
            val openOpusComposer = openOpusRepository.findComposerByName(albumArtistName) ?: return
            val portraitPath = artworkDownloader.downloadArtwork(
                openOpusComposer.portraitUrl, "composer_portraits",albumArtistId
            )
            insert(
                Composer(
                    albumArtistId = albumArtistId,
                    openOpusId = openOpusComposer.id,
                    completeName = openOpusComposer.completeName,
                    birthYear = openOpusComposer.birthDate?.take(4),
                    deathYear = openOpusComposer.deathDate?.take(4),
                    epoch = openOpusComposer.epoch,
                    portraitPath = portraitPath,
                )
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getComposerForAlbumArtist(albumArtistId: Long): Flow<Composer?> {
        return composerDao.getComposerForAlbumArtist(albumArtistId)
    }
}
