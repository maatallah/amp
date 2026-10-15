package com.automusicplayer.bridge

import android.media.MediaMetadataRetriever
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod
import com.facebook.react.bridge.ReadableArray
import com.facebook.react.bridge.Promise
import com.automusicplayer.database.AppDatabase
import com.automusicplayer.database.ArtistEntity
import com.automusicplayer.database.AlbumEntity
import com.automusicplayer.database.GenreEntity
import com.automusicplayer.database.TrackEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class MusicScannerModule(reactContext: ReactApplicationContext) : ReactContextBaseJavaModule(reactContext) {

    override fun getName(): String {
        return "MusicScannerModule"
    }

    @ReactMethod
    fun scanDirectories(pathArray: ReadableArray, promise: Promise) {
        val paths = mutableListOf<String>()
        for (i in 0 until pathArray.size()) {
            pathArray.getString(i)?.let { paths.add(it) }
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val db = AppDatabase.getInstance(reactApplicationContext)
                val dao = db.musicDao()
                var scannedCount = 0
                val retriever = MediaMetadataRetriever()

                for (dirPath in paths) {
                    val dir = File(dirPath)
                    if (dir.exists() && dir.isDirectory) {
                        dir.walkTopDown().forEach { file ->
                            if (file.isFile && isAudioFile(file.extension)) {
                                try {
                                    retriever.setDataSource(file.absolutePath)
                                    val title = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE) ?: file.nameWithoutExtension
                                    val artistName = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST) ?: "Unknown Artist"
                                    val albumName = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM) ?: "Unknown Album"
                                    val durationStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                                    val duration = durationStr?.toLongOrNull() ?: 0L
                                    val bitrateStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE)
                                    val bitrate = bitrateStr?.toIntOrNull()
                                    val yearStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_YEAR)
                                    val year = yearStr?.toIntOrNull()

                                    // Insert Artist
                                    val artistId = dao.insertArtist(ArtistEntity(name = artistName))
                                    // Insert Album
                                    val albumId = dao.insertAlbum(AlbumEntity(name = albumName, artPath = null, year = year))
                                    // Insert Genre (default to Unknown if missing)
                                    val genreId = dao.insertGenre(GenreEntity(name = "Unknown Genre", ignored = false))

                                    // Insert Track
                                    val track = TrackEntity(
                                        filePath = file.absolutePath,
                                        title = title,
                                        duration = duration,
                                        bitrate = bitrate,
                                        fileSize = file.length(),
                                        dateAdded = System.currentTimeMillis(),
                                        lastPlayed = null,
                                        playCount = 0,
                                        albumId = albumId,
                                        artistId = artistId,
                                        genreId = genreId
                                    )
                                    dao.insertTrack(track)
                                    scannedCount++
                                } catch (e: Exception) {
                                    // Fault tolerance for malformed or corrupted audio files
                                    val fallbackTrack = TrackEntity(
                                        filePath = file.absolutePath,
                                        title = file.nameWithoutExtension,
                                        duration = 0L,
                                        bitrate = null,
                                        fileSize = file.length(),
                                        dateAdded = System.currentTimeMillis(),
                                        lastPlayed = null,
                                        playCount = 0,
                                        albumId = null,
                                        artistId = null,
                                        genreId = null
                                    )
                                    dao.insertTrack(fallbackTrack)
                                    scannedCount++
                                }
                            }
                        }
                    }
                }
                retriever.release()
                promise.resolve("Background scan completed successfully. Processed: $scannedCount tracks.")
            } catch (e: Exception) {
                promise.reject("SCAN_ERROR", e.message, e)
            }
        }
    }

    private fun isAudioFile(ext: String): Boolean {
        val audioExts = listOf("mp3", "flac", "aac", "wav", "m4a", "ogg")
        return audioExts.contains(ext.lowercase())
    }
}
