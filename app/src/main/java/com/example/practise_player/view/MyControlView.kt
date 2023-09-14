package com.example.practise_player.view

import android.content.Context
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.ProgressBar
import android.widget.TextView
import com.example.practise_player.R
import com.example.practise_player.models.Info
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.Timeline

class MyControlView(context: Context, player: Player?) : FrameLayout(context),
    Player.Listener {
    private val prevBtn: TextView
    private val nextBtn: TextView
    private val progressBar: ProgressBar
    private var player: Player? = null
    private var isVideoReady = false
    private var isButtonClicked = false
    private var startTime: Long = 0
    private var endTime: Long = 0
    private var currentProgress = 0
    private var selectedButton: Int = 0
    private var videoInfo: Info? = null
    val currentPosition = player?.currentPosition ?: 0
    private var previousPosition: Long = 0

    init {
        LayoutInflater.from(context).inflate(R.layout.custom_elements, this)
        prevBtn = findViewById<TextView>(R.id.prevBtn)
        nextBtn = findViewById<TextView>(R.id.nextBtn)
        progressBar = findViewById<ProgressBar>(R.id.progressBar)
        prevBtn.visibility = INVISIBLE
        nextBtn.visibility = INVISIBLE
        progressBar.visibility = INVISIBLE
    }

    fun setVideoInfo(videoInfo: Info) {
        this.videoInfo = videoInfo
        prevBtn.text = videoInfo.textBtn1
        nextBtn.text = videoInfo.textBtn2
        startTime = videoInfo.startTime
        endTime = videoInfo.endTime
    }

    fun setButton1ClickListener(listener: OnClickListener) {
        prevBtn.setOnClickListener { view ->
            listener.onClick(view)
            selectedButton = 1
            if (videoInfo != null) {
                val interval1Start = videoInfo!!.button1IntervalStart
                val interval1End = videoInfo!!.button1IntervalEnd
                if (currentPosition in interval1Start..interval1End) {
                    player?.seekTo(interval1Start)
                } else {
                    // Пользователь пытается перемотать на невыбранный интервал, возвращаем ползунок
                    player?.seekTo(previousPosition)
                }
            }
            hideControls()
        }
    }

    fun setButton2ClickListener(listener: OnClickListener) {
        nextBtn.setOnClickListener { view ->
            listener.onClick(view)
            selectedButton = 2
            if (videoInfo != null) {
                val interval2Start = videoInfo!!.button2IntervalStart
                val interval2End = videoInfo!!.button2IntervalEnd
                if (currentPosition in interval2Start..interval2End) {
                    player?.seekTo(interval2Start)
                } else {
                    // Пользователь пытается перемотать на невыбранный интервал, возвращаем ползунок
                    player?.seekTo(previousPosition)
                }
            }
            hideControls()
        }
    }

    private fun hideControls() {
        isButtonClicked = true
        prevBtn.visibility = INVISIBLE
        nextBtn.visibility = INVISIBLE
        progressBar.visibility = INVISIBLE
    }

    fun attachPlayer(player: Player) {
        this.player = player
        player.addListener(this)
    }

    override fun onTimelineChanged(timeline: Timeline, reason: Int) {
        if (reason == Player.EVENT_TIMELINE_CHANGED) {
            isVideoReady = true
        }
    }

    override fun onPlaybackStateChanged(playbackState: Int) {
        super<Player.Listener>.onPlaybackStateChanged(playbackState)
        if (playbackState == Player.STATE_READY && isVideoReady) {
            val duration = player!!.duration
            object : CountDownTimer(duration - startTime, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    val currentPosition = player!!.currentPosition
                    if (currentPosition in startTime..endTime && player!!.isPlaying && !isButtonClicked) {
                        // Оставляем логику отображения контролов как есть
                        prevBtn.visibility = VISIBLE
                        nextBtn.visibility = VISIBLE
                        progressBar.visibility = VISIBLE
                        val remainingTime = endTime - currentPosition
                        currentProgress =
                            (100 - remainingTime * 100 / (endTime - startTime)).toInt()
                        progressBar.progress = currentProgress
                    } else {
                        if (currentPosition < startTime) isButtonClicked = false
                        prevBtn.visibility = INVISIBLE
                        nextBtn.visibility = INVISIBLE
                        progressBar.visibility = INVISIBLE
                    }

                    // Дополнительная логика для кнопок 1 и 2
                    if (isButtonClicked) {
                        when (selectedButton) {
                            1 -> {
                                // Логика для выбора кнопки 1
                                if (currentPosition in startTime..endTime) {
                                    val interval1Start = videoInfo!!.button1IntervalStart
                                    val interval1End = videoInfo!!.button1IntervalEnd
                                    if (currentPosition >= interval1Start && currentPosition <= interval1End) {
                                        // Действия для интервала 1
                                        player?.seekTo(interval1Start)
                                    } else {
                                        // Пользователь пытается перемотать на невыбранный интервал, возвращаем ползунок
                                        player?.seekTo(previousPosition)
                                    }
                                }
                            }
                            2 -> {
                                // Логика для выбора кнопки 2
                                if (currentPosition in startTime..endTime) {
                                    val interval2Start = videoInfo!!.button2IntervalStart
                                    val interval2End = videoInfo!!.button2IntervalEnd
                                    if (currentPosition >= interval2Start && currentPosition <= interval2End) {
                                        // Действия для интервала 2
                                        player?.seekTo(interval2Start)
                                    } else {
                                        // Пользователь пытается перемотать на невыбранный интервал, возвращаем ползунок
                                        player?.seekTo(previousPosition)
                                    }
                                }
                            }
                        }
                    }

                    // Обновляем предыдущую позицию ползунка при перемещении
                    previousPosition = currentPosition
                }

                override fun onFinish() {
                    progressBar.progress = 100
                }
            }.start()
        }
    }
}