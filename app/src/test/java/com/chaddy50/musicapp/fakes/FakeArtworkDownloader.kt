package com.chaddy50.musicapp.fakes

import com.chaddy50.musicapp.data.util.IArtworkDownloader

class FakeArtworkDownloader(
    private val resultPath: String? = null,
) : IArtworkDownloader {
    var lastUrl: String? = null
    var downloadCount = 0

    override fun downloadArtwork(url: String?, directoryName: String, fileId: Long): String? {
        lastUrl = url
        downloadCount++
        return resultPath
    }
}
