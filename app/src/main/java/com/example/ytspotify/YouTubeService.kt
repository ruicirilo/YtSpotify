package com.example.ytspotify

import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

class YouTubeService(private val apiKey: String) {

    private val client = OkHttpClient()

    fun getTrendingMusicVideos(): List<String> {
        val url = "https://www.googleapis.com/youtube/v3/videos?part=snippet&chart=mostPopular&regionCode=US&videoCategoryId=10&maxResults=20&key=$apiKey"
        val request = Request.Builder().url(url).build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw Exception("Erro ao buscar vídeos do YouTube")

            val jsonResponse = response.body?.string()
            val jsonObject = JSONObject(jsonResponse ?: "")
            val items = jsonObject.getJSONArray("items")
            val videoTitles = mutableListOf<String>()

            for (i in 0 until items.length()) {
                val video = items.getJSONObject(i)
                val snippet = video.getJSONObject("snippet")
                val title = snippet.getString("title")
                videoTitles.add(title)
            }

            return videoTitles
        }
    }
}
