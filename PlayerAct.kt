package com.example.lab_andr4

import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.ui.PlayerView

class PlayerAct: AppCompatActivity() {
    private var mediaPlayer: MediaPlayer? = null
    private var player: ExoPlayer? = null
    private lateinit var playerView: PlayerView
    private lateinit var fileNameTextView: TextView
    private var isVideo: Boolean = false
    private var lastPosition: Int = 0



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.player_main)

        playerView = findViewById(R.id.playerView)
        fileNameTextView = findViewById(R.id.fileNameTextView)

        isVideo = intent.getBooleanExtra("isVideo", false)
        val uriStr = intent.getStringExtra("uri")
        val urlStr = intent.getStringExtra("url")

        val displayName = uriStr?.let { Uri.parse(it).lastPathSegment } ?: urlStr?.let { Uri.parse(it).lastPathSegment } ?: "Невідомо"
        fileNameTextView.text = "Активний файл: $displayName"

        playerView.visibility = if (isVideo) View.VISIBLE else View.GONE

        val playButton = findViewById<Button>(R.id.playButton)
        val pauseButton = findViewById<Button>(R.id.pauseButton)
        val stopButton = findViewById<Button>(R.id.stopButton)

        playButton.setOnClickListener {
            if (isVideo) {
                if (player == null) {
                    // Перший запуск або після стопу — створення плеєра
                    val videoUri = Uri.parse(uriStr ?: urlStr)
                    player = ExoPlayer.Builder(this).build().also {
                        playerView.player = it
                        it.setMediaItem(MediaItem.fromUri(videoUri))
                        it.prepare()
                    }
                } else {
                    // Плеєр вже існує — просто продовжити
                    player?.seekTo(lastPosition.toLong())
                    player?.play()
                }
            } else {
                if (mediaPlayer == null) {
                    val audioUri = Uri.parse(uriStr ?: urlStr)
                    mediaPlayer = MediaPlayer().apply {
                        setDataSource(this@PlayerAct, audioUri)
                        prepare()
                        seekTo(lastPosition)
                        start()
                    }
                } else {
                    mediaPlayer?.seekTo(lastPosition)
                    mediaPlayer?.start()
                }
            }
        }

        pauseButton.setOnClickListener {
            if (isVideo) {
                // Перевірка на null перед отриманням позиції
                lastPosition = player?.getCurrentPosition()?.toInt() ?: 0
                player?.pause()
            } else {
                // Перевірка на null перед отриманням позиції
                lastPosition = mediaPlayer?.currentPosition ?: 0
                mediaPlayer?.pause()
            }
        }


        stopButton.setOnClickListener {
            if (isVideo) {
                player?.stop()
                player?.release()
                player = null
                playerView.player = null
            } else {
                mediaPlayer?.stop()
                mediaPlayer?.release()
                mediaPlayer = null
            }
            lastPosition = 0
        }

    }


    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
        player?.release()
    }
}