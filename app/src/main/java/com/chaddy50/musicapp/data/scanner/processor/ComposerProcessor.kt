package com.chaddy50.musicapp.data.scanner.processor

import com.chaddy50.musicapp.data.repository.ComposerRepository

class ComposerProcessor(
    private val composerRepository: ComposerRepository
) {
    private val processedComposers = mutableSetOf<Long>()

    suspend fun process(
        isClassical: Boolean,
        albumArtistId: Long,
        albumArtistName: String
    ) {
        if (!isClassical) return
        if (processedComposers.contains(albumArtistId)) return

        processedComposers.add(albumArtistId)
        composerRepository.fetchAndInsertComposer(albumArtistId, albumArtistName)
    }
}