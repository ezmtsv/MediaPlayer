package ru.netology.mediaplayer.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.netology.mediaplayer.databinding.CardTrackBinding
import ru.netology.mediaplayer.dto.Track

interface OnIteractionListener {
    fun play(track: Track)
    fun like(track: Track)
    fun block(track: Track)
}

class AdapterTracks(
    private val onIteractionListener: OnIteractionListener,
) : ListAdapter<Track, TrackViewHolder>(CallBackItem()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val binding = CardTrackBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TrackViewHolder(binding, onIteractionListener)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        val track = getItem(position)
        holder.bind(track)
    }
}

class TrackViewHolder(
    private val binding: CardTrackBinding,
    private val onIteractionListener: OnIteractionListener,
) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(track: Track) {
        with(binding) {
            song.text = track.file
            play.isChecked = track.playTrack
            iconLike.isChecked = track.like
            iconStop.isChecked = track.blockPlay
            timeTrack.text = getTimeTrack(track.time)

            iconStop.setOnClickListener {
                onIteractionListener.block(track)
            }

            iconLike.setOnClickListener {
                onIteractionListener.like(track)
            }

            play.setOnClickListener {
                onIteractionListener.play(track)
            }
            root.setOnClickListener {
                onIteractionListener.play(track)
            }

        }
    }

    private fun getTimeTrack(time: Int): String =
        if (time == 0) {
            "..."
        } else {
            "${time / 1000 / 60}:${time / 1000 % 60}"
        }


}

class CallBackItem : DiffUtil.ItemCallback<Track>() {
    override fun areItemsTheSame(oldItem: Track, newItem: Track): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Track, newItem: Track): Boolean {
        return oldItem == newItem
    }

}
