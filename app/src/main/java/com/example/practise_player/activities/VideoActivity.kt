package com.example.practise_player.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.practise_player.R


class VideoActivity : AppCompatActivity() {
    private var textView: TextView? = null
    private var imageView: ImageView? = null
    private var titleText: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video)
        imageView = findViewById(R.id.videoImage)
        titleText = findViewById(R.id.title_video)
        val videoUrl = intent.getStringExtra("url")
        val videoId = intent.getStringExtra("id")
        val videoTitle = intent.getStringExtra("title")
        Glide.with(this).load(Uri.parse(videoUrl)).into(imageView!!)
        titleText?.text = videoTitle
        textView = findViewById(R.id.startBtn)
        textView?.setOnClickListener {
            val intent = Intent(this, VideoPlayActivity::class.java)
            intent.putExtra("url", videoUrl)
            intent.putExtra("id", videoId)
            startActivity(intent)
        }
    }
}