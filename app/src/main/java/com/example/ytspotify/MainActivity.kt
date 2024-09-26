package com.example.ytspotify

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private val youtubeApiKey = ""
    private val spotifyAccessToken = "BQAk7H_ED5b2p1akn02UKUDOAVNfhh2vgmtnGwWdD3jGeGMrJslCjwDm318i7H5tTjvk8lEWLYKpbvfk9TUn3tkYrI-_xCESk4mZgDSC2tQuB8b-R-wRGarSgDU0XHfE-ob7lQ8sLPyDX-1XUEJbKh4cQUHiAyRXKl7vXH5APgYl6RmPe5nerjamb8Nxdzuiq96aOU2WiYI_uuqmTVW6fKq02tCqmesRM3wZRocREqWI2nuyjRR48Ux40G1KbK-yTIWkMm-54KWe-P3KN7TTBgLflnNxIP95"
    private val spotifyUserId = ""

    private lateinit var statusMessage: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        statusMessage = findViewById(R.id.statusMessage)
        val createPlaylistButton: Button = findViewById(R.id.createPlaylistButton)

        // Inicializando os serviços
        val youtubeService = YouTubeService(youtubeApiKey)
        val spotifyService = SpotifyService(spotifyAccessToken)

        createPlaylistButton.setOnClickListener {
            // Executar o processo em uma corrotina
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    // Passo 1: Buscar músicas trending no YouTube
                    val trendingVideos = youtubeService.getTrendingMusicVideos()

                    // Passo 2: Buscar as faixas correspondentes no Spotify
                    val trackUris = spotifyService.searchTracks(trendingVideos)

                    // Passo 3: Criar uma playlist no Spotify
                    val playlistId = spotifyService.createPlaylist(spotifyUserId, "Trending Music Playlist")

                    // Passo 4: Adicionar as músicas à playlist
                    spotifyService.addTracksToPlaylist(playlistId, trackUris)

                    // Atualizar a UI
                    withContext(Dispatchers.Main) {
                        statusMessage.text = "Playlist criada com sucesso"
                        statusMessage.visibility = android.view.View.VISIBLE
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    // Mostrar erro na UI
                    withContext(Dispatchers.Main) {
                        statusMessage.text = "Erro ao criar playlist"
                        statusMessage.visibility = android.view.View.VISIBLE
                    }
                }
            }
        }
    }
}


