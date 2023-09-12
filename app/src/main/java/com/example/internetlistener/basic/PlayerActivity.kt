package com.example.internetlistener.basic

import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.Player.STATE_ENDED
import androidx.media3.common.Player.STATE_READY
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.hls.HlsMediaSource
import androidx.media3.exoplayer.source.MediaSource
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import com.example.internetlistener.databinding.ActivityPlayerBinding

@UnstableApi class PlayerActivity : AppCompatActivity() {
    private val mediaUrl = "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4"
    private val binding by lazy {
        ActivityPlayerBinding.inflate(layoutInflater)
    }

    private var player: ExoPlayer?= null
    // Create a data source factory.
    private val dataSourceFactory: DataSource.Factory = DefaultHttpDataSource.Factory()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initPlayer()
    }

    override fun onPause() {
        super.onPause()
        pause()
    }

    override fun onResume() {
        super.onResume()
        play()
    }

    override fun onDestroy() {
        super.onDestroy()
        releasePlayer()
    }

    private fun initPlayer(){
        player = ExoPlayer.Builder(this)
            .build()
            .apply {
                val source = if (mediaUrl.contains("m3u8"))
                    getHlsMediaSource()
                else
                    getProgressiveMediaSource()

                setMediaSource(source)
                prepare()
                binding.playerView.player = player
                addListener(playerListener)
            }
    }

    private fun getHlsMediaSource(): MediaSource {
        // Create a HLS media source pointing to a playlist uri.
        return HlsMediaSource.Factory(dataSourceFactory).
        createMediaSource(MediaItem.fromUri(mediaUrl))
    }

    private fun getProgressiveMediaSource(): MediaSource{
        // Create a Regular media source pointing to a playlist uri.
        return ProgressiveMediaSource.Factory(dataSourceFactory)
            .createMediaSource(MediaItem.fromUri(Uri.parse(mediaUrl)))
    }

    private fun releasePlayer(){
        player?.apply {
            playWhenReady = false
            release()
        }
        player = null
    }

    private fun pause(){
        player?.playWhenReady = false
    }

    private fun play(){
        player?.playWhenReady = true
    }

    private fun restartPlayer(){
        player?.seekTo(0)
        player?.playWhenReady = true
    }

    private val playerListener = object: Player.Listener {
        override fun onPlaybackStateChanged(playbackState: Int) {
            super.onPlaybackStateChanged(playbackState)
            when(playbackState){
                STATE_ENDED -> restartPlayer()
                STATE_READY -> play()
            }
        }
    }
}