package ru.netology.mediaplayer.repository

import androidx.lifecycle.MutableLiveData
import ru.netology.mediaplayer.dto.Album
import ru.netology.mediaplayer.dto.Track

interface TrackRepository {
    fun updateTracks(tracks: List<Track>)
    fun playTrack(track: Track)
    fun getTracks(): MutableLiveData<List<Track>>
    fun getSong(id: Int): Track?
    fun getAlbum(): MutableLiveData<Album>
    fun getAllSongs(callback: GetAllCallback)
    fun setTime(time: Int, id: Int)
    fun stopTrack(id: Int)
    fun likeTrack(id: Int, like: Boolean)
    fun blockTrack(id: Int, stop: Boolean)

    fun nextTrack(track:Track): Track

    interface GetAllCallback {
        fun onSuccess(songs: Album)
        fun onError(e: Exception)
    }
}