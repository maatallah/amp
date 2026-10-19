package com.automusicplayer.media

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.LibraryResult
import androidx.media3.session.MediaLibraryService
import androidx.media3.session.MediaSession
import com.automusicplayer.car.AutoCarAppService
import com.automusicplayer.database.AppDatabase
import com.google.common.collect.ImmutableList
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.SettableFuture
import java.util.concurrent.ForkJoinPool

class PlaybackService : MediaLibraryService() {
    private var mediaLibrarySession: MediaLibrarySession? = null
    private lateinit var player: ExoPlayer
    private lateinit var audioManager: AudioManager
    private var audioFocusRequest: AudioFocusRequest? = null
    private var originalVolume: Float = 1.0f

    companion object {
        private const val ROOT_ID = "__ROOT__"
        private const val CATEGORY_PLAYLISTS = "__PLAYLISTS__"
        private const val CATEGORY_TRACKS = "__TRACKS__"
    }

    private val powerStateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                Intent.ACTION_POWER_DISCONNECTED -> {
                    if (::player.isInitialized && player.isPlaying) {
                        player.pause()
                    }
                }
                Intent.ACTION_POWER_CONNECTED -> {
                    if (::player.isInitialized && !player.isPlaying) {
                        player.prepare()
                    }
                }
            }
        }
    }

    private val audioFocusChangeListener = AudioManager.OnAudioFocusChangeListener { focusChange ->
        when (focusChange) {
            AudioManager.AUDIOFOCUS_LOSS -> {
                if (player.isPlaying) {
                    player.pause()
                }
                abandonAudioFocus()
            }
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
                if (player.isPlaying) {
                    player.pause()
                }
            }
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> {
                originalVolume = player.volume
                player.volume = originalVolume * 0.2f
            }
            AudioManager.AUDIOFOCUS_GAIN -> {
                player.volume = originalVolume
                if (!player.isPlaying) {
                    player.play()
                }
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        player = ExoPlayer.Builder(this).build()

        player.addListener(object : androidx.media3.common.Player.Listener {
            override fun onMediaItemTransition(mediaItem: androidx.media3.common.MediaItem?, reason: Int) {
                if (reason == androidx.media3.common.Player.MEDIA_ITEM_TRANSITION_REASON_AUTO || 
                    reason == androidx.media3.common.Player.MEDIA_ITEM_TRANSITION_REASON_PLAYLIST_CHANGED) {
                    mediaItem?.mediaId?.let { trackIdString ->
                        logTrackPlaybackTelemetry(trackIdString)
                    }
                }
            }
        })

        val powerFilter = IntentFilter().apply {
            addAction(Intent.ACTION_POWER_CONNECTED)
            addAction(Intent.ACTION_POWER_DISCONNECTED)
        }
        registerReceiver(powerStateReceiver, powerFilter)

        val intent = Intent(this, AutoCarAppService::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        mediaLibrarySession = MediaLibrarySession.Builder(
            this,
            player,
            LibraryCallback()
        ).setSessionActivity(pendingIntent).build()

        requestAudioFocus()
    }

    private fun logTrackPlaybackTelemetry(trackIdString: String) {
        val trackId = trackIdString.toLongOrNull() ?: return
        ForkJoinPool.commonPool().execute {
            try {
                val dao = AppDatabase.getInstance(applicationContext).musicDao()
                dao.updateTrackTelemetry(trackId, System.currentTimeMillis())
            } catch (e: Exception) {
                // Ignore telemetry logging failures
            }
        }
    }

    private inner class LibraryCallback : MediaLibrarySession.Callback {
        override fun onGetLibraryRoot(
            session: MediaLibrarySession,
            browser: MediaSession.ControllerInfo,
            params: LibraryParams?
        ): ListenableFuture<LibraryResult<MediaItem>> {
            val rootItem = MediaItem.Builder()
                .setMediaId(ROOT_ID)
                .setMediaMetadata(
                    MediaMetadata.Builder()
                        .setTitle("Auto Music Root")
                        .setIsPlayable(false)
                        .setIsBrowsable(true)
                        .setFolderType(MediaMetadata.FOLDER_TYPE_MIXED)
                        .build()
                )
                .build()
            return Futures.immediateFuture(LibraryResult.ofItem(rootItem, params))
        }

        override fun onGetChildren(
            session: MediaLibrarySession,
            browser: MediaSession.ControllerInfo,
            parentId: String,
            page: Int,
            pageSize: Int,
            params: LibraryParams?
        ): ListenableFuture<LibraryResult<ImmutableList<MediaItem>>> {
            return when (parentId) {
                ROOT_ID -> {
                    val categories = listOf(
                        MediaItem.Builder()
                            .setMediaId(CATEGORY_TRACKS)
                            .setMediaMetadata(
                                MediaMetadata.Builder()
                                    .setTitle("All Tracks")
                                    .setIsPlayable(false)
                                    .setIsBrowsable(true)
                                    .setFolderType(MediaMetadata.FOLDER_TYPE_ALBUMS)
                                    .build()
                            )
                            .build()
                    )
                    Futures.immediateFuture(LibraryResult.ofItemList(ImmutableList.copyOf(categories), params))
                }
                CATEGORY_TRACKS -> {
                    val settableFuture = SettableFuture.create<LibraryResult<ImmutableList<MediaItem>>>()
                    ForkJoinPool.commonPool().execute {
                        try {
                            val dao = AppDatabase.getInstance(applicationContext).musicDao()
                            val tracks = dao.getAllTracks()
                            val mediaItems = tracks.map { track ->
                                MediaItem.Builder()
                                    .setMediaId(track.id.toString())
                                    .setUri(Uri.parse(track.filePath))
                                    .setMediaMetadata(
                                        MediaMetadata.Builder()
                                            .setTitle(track.title)
                                            .setIsPlayable(true)
                                            .setIsBrowsable(false)
                                            .build()
                                    )
                                    .build()
                            }
                            settableFuture.set(LibraryResult.ofItemList(ImmutableList.copyOf(mediaItems), params))
                        } catch (e: Exception) {
                            settableFuture.set(LibraryResult.ofItemList(ImmutableList.of(), params))
                        }
                    }
                    settableFuture
                }
                else -> {
                    Futures.immediateFuture(LibraryResult.ofItemList(ImmutableList.of(), params))
                }
            }
        }
    }

    private fun requestAudioFocus() {
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_MEDIA)
            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
            .build()

        audioFocusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
            .setAudioAttributes(audioAttributes)
            .setOnAudioFocusChangeListener(audioFocusChangeListener)
            .build()

        audioFocusRequest?.let { audioManager.requestAudioFocus(it) }
    }

    private fun abandonAudioFocus() {
        audioFocusRequest?.let { audioManager.abandonAudioFocusRequest(it) }
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaLibrarySession? {
        return mediaLibrarySession
    }

    override fun onDestroy() {
        try {
            unregisterReceiver(powerStateReceiver)
        } catch (e: Exception) {
            // Receiver was already unregistered or not fully initialized
        }
        abandonAudioFocus()
        mediaLibrarySession?.run {
            player.release()
            release()
            mediaLibrarySession = null
        }
        super.onDestroy()
    }
}
