package ru.netology.mediaplayer.adapter

import ru.netology.mediaplayer.databinding.ActivityAppBinding
import ru.netology.mediaplayer.dto.Album

interface OnListener{
    fun play()
}

class AdapterHead (private val binding: ActivityAppBinding, private val listener: OnListener)  {
    fun bind(header: Album) {
         with(binding) {
            nameAlbum.text = header.title
            nameAuthor.text = header.subtitle
            publish.text = header.published
            iconPlay.isChecked = header.playTrackAlbum
            iconPlay.setOnClickListener {
                listener.play()
            }
        }
    }
}


