package com.example.practise_player.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.practise_player.R
import com.example.practise_player.adapter.VideoAdapter
import com.example.practise_player.api.PexelsApiClient
import com.example.practise_player.models.Video
import com.google.android.material.appbar.MaterialToolbar
import java.util.ArrayList

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar: MaterialToolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val recyclerView: RecyclerView = findViewById(R.id.recycler)

        PexelsApiClient.getPopularVideos(object : PexelsApiClient.VideoResponseListener {
            override fun onVideoResponse(videoArrayList: ArrayList<Video>?) {
                runOnUiThread {
                    val adapter = VideoAdapter(this@MainActivity, videoArrayList)
                    recyclerView.adapter = adapter
                    adapter.setOnItemClickListener(object : VideoAdapter.OnItemClickListener {
                        override fun onClick(video: Video?) {
                            val intent = Intent(this@MainActivity, VideoActivity::class.java)
                            if (video != null) {
                                intent.putExtra("url", video.link)
                            }
                            if (video != null) {
                                intent.putExtra("id", video.id)
                            }
                            if (video != null) {
                                intent.putExtra("title", video.userName)
                            }
                            startActivity(intent)
                        }
                    })
                }
            }

            override fun onFailed(error: String?) {}

            override fun onFailure(errorMessage: String?) {
                Log.e("APIError", errorMessage!!)
            }
        })
    }
}