package com.example.practise_player.api

import com.example.practise_player.models.Video
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

import java.util.ArrayList
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response


object PexelsApiClient {
    private const val API = "563492ad6f91700001000001f8018fa3798844489979ded127c25470"
    private const val BASE_URL = "https://api.pexels.com/videos/popular?page=1&per_page=50"
    fun getPopularVideos(videoResponseListener: VideoResponseListener) {
        val client = OkHttpClient()
        val request: Request = Request.Builder()
            .url(BASE_URL)
            .addHeader("Authorization", API)
            .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                videoResponseListener.onFailed("Request Failed: " + e.message)
            }

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    try {
                        val responseBody = response.body!!.string()
                        val jsonObject = JSONObject(responseBody)
                        val jsonArray = jsonObject.getJSONArray("videos")
                        val arrayList: ArrayList<Video> = ArrayList<Video>()
                        for (i in 0 until jsonArray.length()) {
                            val jsonObject1 = jsonArray.getJSONObject(i)
                            val videoUrl = jsonObject1.getJSONArray("video_files").getJSONObject(0)
                                .getString("link")
                            val video = Video()
                            video.link = videoUrl
                            video.id = jsonObject1.getString("id")
                            video.duration = jsonObject1.getInt("duration")
                            video.userName = jsonObject1.getJSONObject("user").getString("name")
                            video.userUrl = jsonObject1.getJSONObject("user").getString("url")
                            arrayList.add(video)
                        }
                        videoResponseListener.onVideoResponse(arrayList)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }
            }
        })
    }

    interface VideoResponseListener {
        fun onVideoResponse(arrayList: ArrayList<Video>?)
        fun onFailed(error: String?)
        fun onFailure(errorMessage: String?)
    }
}
