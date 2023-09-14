package com.example.practise_player.activities

import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.OrientationEventListener
import android.view.View
import android.view.WindowManager
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import com.example.practise_player.R
import com.example.practise_player.models.Info
import com.example.practise_player.view.MyControlView
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import org.json.JSONArray
import org.json.JSONException
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader


class VideoPlayActivity : AppCompatActivity(), Player.Listener {
    private var player: SimpleExoPlayer? = null
    private var playerView: PlayerView? = null
    private var progressBar: ProgressBar? = null
    private var videoInfo: Info? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_play)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR

        val url = intent.getStringExtra("url")
        val videoId = intent.getStringExtra("id")

        progressBar = findViewById(R.id.progressBar)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        try {
            setupPlayer(videoId)
        } catch (e: IOException) {
            throw RuntimeException(e)
        }

        addMp4Files(url)

        if (savedInstanceState != null) {
            val restoredMedia = savedInstanceState.getInt("mediaItem", 0)
            val seekTime = savedInstanceState.getLong("SeekTime", 0)
            player?.seekTo(restoredMedia, seekTime)
            player?.play()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putLong("SeekTime", player?.currentPosition ?: 0)
        outState.putInt("mediaItem", player?.currentMediaItemIndex ?: 0)
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            playerView?.systemUiVisibility = (
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            or View.SYSTEM_UI_FLAG_FULLSCREEN
                            or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    )
        }
    }

    @Throws(IOException::class)
    private fun jsonReader(): List<Info> {
        val videoInfoList: MutableList<Info> = ArrayList()
        try {
            val assetManager = assets
            val inputStream = assetManager.open("file.json")
            val reader = BufferedReader(InputStreamReader(inputStream))
            val jsonString = StringBuilder()
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                jsonString.append(line)
            }
            val jsonArray = JSONArray(jsonString.toString())
            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)
                val videoInfo = Info()
                videoInfo.id = jsonObject.getString("video_id")
                videoInfo.textBtn1 = jsonObject.getString("text_btn1")
                videoInfo.textBtn2 = jsonObject.getString("text_btn2")
                videoInfo.startTime = jsonObject.getLong("start_time")
                videoInfo.endTime = jsonObject.getLong("end_time")
                videoInfo.button1IntervalStart = jsonObject.getLong("button1_interval_start")
                videoInfo.button1IntervalEnd = jsonObject.getLong("button1_interval_end")
                videoInfo.button2IntervalStart = jsonObject.getLong("button2_interval_start")
                videoInfo.button2IntervalEnd = jsonObject.getLong("button2_interval_end")
                videoInfoList.add(videoInfo)
            }
            reader.close()
            inputStream.close()
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return videoInfoList
    }

    @Throws(IOException::class)
    private fun setupPlayer(videoId: String?) {
        val videoInfoList: List<Info> = jsonReader()
        player = SimpleExoPlayer.Builder(this).build()
        playerView = findViewById(R.id.video_view)
        playerView?.player = player
        player?.addListener(this)
        val controlView = MyControlView(this, player)
        controlView.setButton1ClickListener(View.OnClickListener {})

        // Устанавливаем слушатель для второй кнопки
        controlView.setButton2ClickListener(View.OnClickListener {
            // Логика обработки нажатия на вторую кнопку
            // ...
        })
        playerView?.addView(controlView)
        Log.i(videoId, " videoId $videoId")
        for (videoInfo in videoInfoList) {
            if (videoInfo.id == videoId) {
                controlView.setVideoInfo(videoInfo)
                break
            }
        }
        controlView.attachPlayer(player!!)
        val orientationEventListener: OrientationEventListener =
            object : OrientationEventListener(this) {
                override fun onOrientationChanged(orientation: Int) {
                    if (orientation == OrientationEventListener.ORIENTATION_UNKNOWN) {
                        return
                    }
                    val newOrientation: Int = when {
                        orientation >= 45 && orientation < 135 -> ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE
                        orientation >= 135 && orientation < 225 -> ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT
                        orientation >= 225 && orientation < 315 -> ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                        else -> ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                    }
                    requestedOrientation = newOrientation
                }
            }
        orientationEventListener.enable()
    }

    private fun addMp4Files(url: String?) {
        val dataSourceFactory = DefaultDataSourceFactory(
            this,
            Util.getUserAgent(this, "player")
        )
        val mediaItem = MediaItem.fromUri(Uri.parse(url))
        val mediaSource = ProgressiveMediaSource.Factory(dataSourceFactory)
            .createMediaSource(mediaItem)
        player?.setMediaSource(mediaSource)
        player?.prepare()
    }

    override fun onPlaybackStateChanged(playbackState: Int) {
        when (playbackState) {
            Player.STATE_BUFFERING -> progressBar?.visibility = View.VISIBLE
            Player.STATE_READY -> progressBar?.visibility = View.INVISIBLE
            Player.STATE_ENDED -> {}
            Player.STATE_IDLE -> {}
        }
    }

    override fun onStop() {
        super.onStop()
        player?.release()
    }

    override fun onDestroy() {
        super.onDestroy()
        player?.release()
    }
}