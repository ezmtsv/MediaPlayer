package ru.netology.mediaplayer.repository

import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import ru.netology.mediaplayer.dto.Album
import ru.netology.mediaplayer.dto.Track
import java.io.IOException
import java.util.concurrent.TimeUnit

class TrackRepositoryImpl : TrackRepository {
    private var tracks = listOf<Track>()
    private var album = Album()
    private val data: MutableLiveData<List<Track>> = MutableLiveData(tracks)
    private var header: MutableLiveData<Album> = MutableLiveData(album)
    private val gson = Gson()
    private val typeToken = object : TypeToken<Album>() {}

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .build()

    companion object {
        private const val BASE_URL =
            "https://github.com/netology-code/andad-homeworks/raw/master/09_multimedia/data/album.json"
    }

    override fun updateTracks(tracks: List<Track>) {
        this.tracks = tracks
        data.value = this.tracks
    }

    override fun playTrack(track: Track) {
        tracks = tracks.map { tr ->
            if (tr.id == track.id) {
                tr.copy(playTrack = track.playTrack, time = track.time)
            } else tr
        }
        data.value = tracks
        album = album.copy(playTrackAlbum = track.playTrack, tracks = tracks)
        header.value = album

    }


    override fun getTracks(): MutableLiveData<List<Track>> = data
    override fun getSong(id: Int): Track? =
        tracks.find { it.id == id }

    override fun getAlbum(): MutableLiveData<Album> = header

    override fun getAllSongs(callback: TrackRepository.GetAllCallback) {
        val request: Request = Request.Builder()
            .url(BASE_URL)
            .build()

        client.newCall(request)
            .enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    callback.onError(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    val body = response.body?.string() ?: throw RuntimeException("body is null")
                    try {
                        album = gson.fromJson(body, typeToken.type)
                        callback.onSuccess(album)
                    } catch (e: Exception) {
                        callback.onError(e)
                    }
                }
            })
    }

    override fun setTime(time: Int, id: Int) {
        tracks = tracks.map { tr ->
            if (tr.id == id) {
                getSong(id)?.copy(time = time)!!
            } else tr
        }
        data.value = tracks
    }

    override fun stopTrack(id: Int) {
        tracks = tracks.map { tr ->
            if (tr.id == id) {
                getSong(id)?.copy(playTrack = false)!!
            } else tr
        }
        data.value = tracks
        album = album.copy(playTrackAlbum = false, tracks = tracks)
        header.value = album
    }

    override fun likeTrack(id: Int, like: Boolean) {
        tracks = tracks.map { tr ->
            if (tr.id == id) {
                getSong(id)?.copy(like = like)!!
            } else tr
        }
        data.value = tracks
        album = album.copy(tracks = tracks)
        header.value = album
    }

    override fun blockTrack(id: Int, stop: Boolean) {
        tracks = tracks.map { tr ->
            if (tr.id == id) {
                getSong(id)?.copy(blockPlay = stop)!!
            } else tr
        }
        data.value = tracks
        album = album.copy(tracks = tracks)
        header.value = album
    }

    override fun nextTrack(track: Track): Track =
        tracks.elementAtOrElse(track.id) { getSong(1) }!!

}