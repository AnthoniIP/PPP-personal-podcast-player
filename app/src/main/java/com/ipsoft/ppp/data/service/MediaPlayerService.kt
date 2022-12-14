package com.ipsoft.ppp.data.service

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import androidx.media.MediaBrowserServiceCompat
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector
import com.google.android.exoplayer2.upstream.cache.CacheDataSource
import com.ipsoft.ppp.constant.AppConstants
import com.ipsoft.ppp.data.exoplayer.*
import com.ipsoft.ppp.ui.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import javax.inject.Inject

@AndroidEntryPoint
class MediaPlayerService : MediaBrowserServiceCompat() {
    @Inject
    lateinit var dataSourceFactory: CacheDataSource.Factory

    @Inject
    lateinit var exoPlayer: SimpleExoPlayer

    @Inject
    lateinit var mediaSource: PodcastMediaSource

    private val serviceJob = Job()
    private val serviceScope = CoroutineScope(Dispatchers.Main + serviceJob)

    private lateinit var mediaSession: MediaSessionCompat
    private lateinit var mediaSessionConnector: MediaSessionConnector

    private lateinit var mediaPlayerNotificationManager: MediaPlayerNotificationManager

    private var currentPlayingMedia: MediaMetadataCompat? = null

    private var isPlayerInitialized = false

    var isForegroundService: Boolean = false

    companion object {
        private const val TAG = "MediaPlayerService"

        var currentDuration: Long = 0L
            private set
    }

    override fun onCreate() {
        super.onCreate()
        val activityPendingIntent = Intent(this, MainActivity::class.java)
            .apply {
                action = AppConstants.ACTION_PODCAST_NOTIFICATION_CLICK
            }
            .let {
                PendingIntent.getActivity(
                    this,
                    0,
                    it,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
                )
            }

        mediaSession = MediaSessionCompat(this, TAG).apply {
            setSessionActivity(activityPendingIntent)
            isActive = true
        }

        val mediaPlaybackPreparer = MediaPlaybackPreparer(mediaSource) { mediaMetadata ->
            currentPlayingMedia = mediaMetadata
            preparePlayer(mediaSource.mediaMetadataEpisodes, mediaMetadata, true)
        }
        mediaSessionConnector = MediaSessionConnector(mediaSession).apply {
            setPlaybackPreparer(mediaPlaybackPreparer)
            setQueueNavigator(MediaPlayerQueueNavigator(mediaSession, mediaSource))
            setPlayer(exoPlayer)
        }

        this.sessionToken = mediaSession.sessionToken

        mediaPlayerNotificationManager = MediaPlayerNotificationManager(
            this,
            mediaSession.sessionToken,
            MediaPlayerNotificationListener(this),
        ) {
            currentDuration = exoPlayer.duration
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int =
        Service.START_STICKY

    override fun onCustomAction(action: String, extras: Bundle?, result: Result<Bundle>) {
        super.onCustomAction(action, extras, result)
        when (action) {
            AppConstants.START_MEDIA_PLAYBACK_ACTION -> {
                mediaPlayerNotificationManager.showNotification(exoPlayer)
            }

            AppConstants.REFRESH_MEDIA_BROWSER_CHILDREN -> {
                mediaSource.refresh()
                notifyChildrenChanged(AppConstants.MEDIA_ROOT_ID)
            }

            else -> Unit
        }
    }

    override fun onGetRoot(
        clientPackageName: String,
        clientUid: Int,
        rootHints: Bundle?,
    ): BrowserRoot = BrowserRoot(AppConstants.MEDIA_ROOT_ID, null)

    override fun onLoadChildren(
        parentId: String,
        result: Result<MutableList<MediaBrowserCompat.MediaItem>>,
    ) {
        when (parentId) {
            AppConstants.MEDIA_ROOT_ID -> {
                val resultsSent = mediaSource.whenReady { isInitialized ->
                    if (isInitialized) {

                        result.sendResult(mediaSource.asMediaItems())
                        if (!isPlayerInitialized && mediaSource.mediaMetadataEpisodes.isNotEmpty()) {
                            isPlayerInitialized = true
                        }
                    } else {
                        result.sendResult(null)
                    }
                }
                if (!resultsSent) {
                    result.detach()
                }
            }

            else -> Unit
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
        exoPlayer.release()
    }

    private fun preparePlayer(
        mediaMetaData: List<MediaMetadataCompat>,
        itemToPlay: MediaMetadataCompat?,
        playWhenReady: Boolean,
    ) {
        val indexToPlay = if (currentPlayingMedia == null) 0 else mediaMetaData.indexOf(itemToPlay)
        exoPlayer.setMediaSource(mediaSource.asMediaSource(dataSourceFactory))
        exoPlayer.prepare()
        exoPlayer.seekTo(indexToPlay, 0L)
        exoPlayer.playWhenReady = playWhenReady
    }
}
