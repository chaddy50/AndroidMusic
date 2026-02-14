package com.chaddy50.musicapp.data.scanner.util

import android.content.ContentUris
import android.content.Context
import android.media.MediaMetadataRetriever
import android.provider.MediaStore

class MetadataResolver(private val context: Context) {
    private val metadataRetriever = MediaMetadataRetriever()
    private var hasRetrieverBeenInitializedForTrack = false

    fun resetForNextTrack() {
        hasRetrieverBeenInitializedForTrack = false
    }

    fun release() {
        metadataRetriever.release()
    }

    fun getYear(cursorData: CursorData): String {
        var year = cursorData.year
        if (year.isNullOrBlank() || year == "0") {
            initializeMetadataRetrieverIfNeeded(cursorData.trackId)
            year = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DATE)
        }
        if (year.isNullOrBlank() || year == "0") {
            year = "Unknown Year"
        }
        if (year != "Unknown Year") {
            year = year.take(4)
        }
        return year
    }

    fun getTrackNumber(cursorData: CursorData): Int {
        var trackNumber = cursorData.trackNumber ?: -1
        if (trackNumber < 1) {
            initializeMetadataRetrieverIfNeeded(cursorData.trackId)
            val trackNumberAsString = metadataRetriever.extractMetadata(
                MediaMetadataRetriever.METADATA_KEY_CD_TRACK_NUMBER
            )
            trackNumber = if (trackNumberAsString?.contains("/") == true) {
                trackNumberAsString.substringBefore("/").toIntOrNull() ?: -1
            } else {
                trackNumberAsString?.toIntOrNull() ?: -1
            }
        }
        return trackNumber
    }

    private fun initializeMetadataRetrieverIfNeeded(trackId: Long) {
        if (hasRetrieverBeenInitializedForTrack) {
            return
        }

        val contentUri = ContentUris.withAppendedId(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            trackId
        )
        context.contentResolver.openAssetFileDescriptor(contentUri, "r")?.use { afd ->
            metadataRetriever.setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
        }
        hasRetrieverBeenInitializedForTrack = true
    }
}