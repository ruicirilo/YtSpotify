package com.example.ytspotify

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONArray
import org.json.JSONObject

class SpotifyService(private val accessToken: String) {

    private val client = OkHttpClient()
    private val mediaType = "application/json".toMediaTypeOrNull()

    fun createPlaylist(userId: String, name: String): String {
        val url = "https://api.spotify.com/v1/users/$userId/playlists"
        val jsonBody = JSONObject().apply {
            put("name", name)
            put("description", "Playlist criada a partir das músicas trending do YouTube.")
            put("public", true)
        }
        val body = RequestBody.create(mediaType, jsonBody.toString())

        val request = Request.Builder()
            .url(url)
            .addHeader("Authorization", "Bearer $accessToken")
            .post(body)
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw Exception("Erro ao criar playlist no Spotify")

            val jsonResponse = JSONObject(response.body?.string() ?: "")
            return jsonResponse.getString("id")
        }
    }

    fun searchTracks(trackNames: List<String>): List<String> {
        val trackUris = mutableListOf<String>()

        trackNames.forEach { track ->
            val url = "https://api.spotify.com/v1/search?q=$track&type=track&limit=1"
            val request = Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer $accessToken")
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) return@forEach

                val jsonResponse = JSONObject(response.body?.string() ?: "")
                val tracks = jsonResponse.getJSONObject("tracks").getJSONArray("items")

                if (tracks.length() > 0) {
                    val trackUri = tracks.getJSONObject(0).getString("uri")
                    trackUris.add(trackUri)
                }
            }
        }

        return trackUris
    }

    fun addTracksToPlaylist(playlistId: String, trackUris: List<String>) {
        val url = "https://api.spotify.com/v1/playlists/$playlistId/tracks"
        val jsonBody = JSONObject().apply {
            put("uris", JSONArray(trackUris))
        }
        val body = RequestBody.create(mediaType, jsonBody.toString())

        val request = Request.Builder()
            .url(url)
            .addHeader("Authorization", "Bearer $accessToken")
            .post(body)
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw Exception("Erro ao adicionar músicas à playlist no Spotify")
        }
    }
}
