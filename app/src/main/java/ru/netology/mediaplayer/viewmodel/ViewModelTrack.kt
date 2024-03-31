package ru.netology.mediaplayer.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import ru.netology.mediaplayer.dto.Album
import ru.netology.mediaplayer.dto.Track
import ru.netology.mediaplayer.repository.TrackRepository
import ru.netology.mediaplayer.repository.TrackRepositoryImpl

class ViewModelTrack(
    application: Application,
) : AndroidViewModel(application) {
    private val repository: TrackRepository = TrackRepositoryImpl()
    private val _tracks = repository.getTracks()
    private val _header = repository.getAlbum()

    val tracks: MutableLiveData<List<Track>>
        get() = _tracks
    val header: MutableLiveData<Album>
        get() = _header

    val timeTrack = MutableLiveData<Int>()

    init {
        getAlbum()
    }

    fun play(track: Track) {
        repository.playTrack(track)
    }

    fun getAlbum() {
        repository.getAllSongs(object : TrackRepository.GetAllCallback {
            override fun onSuccess(songs: Album) {
                _header.postValue(songs)
            }

            override fun onError(e: Exception) {
                println("Error")
            }

        })
    }

    fun updateTracks() {
        repository.updateTracks(header.value?.tracks!!)
    }

    fun getTrack(id: Int) = repository.getSong(id)

    fun setTimeTrack(time: Int, id: Int) {
        repository.setTime(time, id)
    }

    fun stopTrack(id: Int) {
        repository.stopTrack(id)
    }

    fun blockTrack(id: Int, stop: Boolean) {
        repository.blockTrack(id, stop)
    }

    fun likeTrack(id: Int, like: Boolean) {
        repository.likeTrack(id, like)
    }

    fun getNextTrack(track: Track): Track {
        return repository.nextTrack(track)
    }
}