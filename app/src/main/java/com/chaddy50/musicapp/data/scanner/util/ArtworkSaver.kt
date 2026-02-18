package com.chaddy50.musicapp.data.scanner.util

import android.content.ContentUris
import android.content.Context
import android.graphics.Bitmap
import android.provider.MediaStore
import android.util.Size
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

interface IArtworkSaver {
    fun loadAndSaveArtwork(trackId: Long, entityId: Long): String?
}

class ArtworkSaver(private val context: Context) : IArtworkSaver {
    override fun loadAndSaveArtwork(trackId: Long, entityId: Long): String? {
        val bitmap = loadThumbnail(trackId) ?: return null
        return saveToFile(bitmap, entityId)
    }

    private fun loadThumbnail(trackId: Long): Bitmap? {
        val uri = ContentUris.withAppendedId(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            trackId
        )

        return try {
            context.contentResolver.loadThumbnail(
                uri,
                Size(800, 800),
                null
            )
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    private fun saveToFile(bitmap: Bitmap, id: Long): String? {
        val directory = File(context.filesDir, "album_artwork")
        if (!directory.exists()) {
            directory.mkdirs()
        }

        val file = File(directory, "$id.jpg")
        return try {
            FileOutputStream(file).use { outputStream ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
            }
            file.absolutePath
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }
}
