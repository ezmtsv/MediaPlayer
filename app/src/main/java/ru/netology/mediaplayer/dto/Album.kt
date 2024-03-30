package ru.netology.mediaplayer.dto

data class Album(
    val id: Int = 0,
    val title: String = "",
    val subtitle: String = "",
    val artist: String = "",
    val published: String = "",
    val genre: String = "",
    val tracks: List<Track> = emptyList(),
    val playTrackAlbum: Boolean = false,
)

data class Track(
    val id: Int = 0,
    val file: String = "",
    val like: Boolean = false,
    val playTrack: Boolean = false,
    val time: Int = 0,
    val blockPlay: Boolean = false,
)
