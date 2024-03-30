package ru.netology.mediaplayer.activity

import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.view.get
import ru.netology.mediaplayer.adapter.AdapterHead
import ru.netology.mediaplayer.adapter.OnIteractionListener
import ru.netology.mediaplayer.adapter.AdapterTracks
import ru.netology.mediaplayer.adapter.OnListener
import ru.netology.mediaplayer.databinding.ActivityAppBinding
import ru.netology.mediaplayer.dto.Track
import ru.netology.mediaplayer.viewmodel.ViewModelTrack
import java.io.IOException

class AppActivity : AppCompatActivity() {
    private var player: MediaPlayer? = null
    private var lastTrack: Int = 0

    companion object {
        private const val URL_LOAD_SONG =
            "https://raw.githubusercontent.com/netology-code/andad-homeworks/master/09_multimedia/data/"
    }

    val viewModel: ViewModelTrack by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityAppBinding.inflate(layoutInflater)


        fun handleTrack(tr: Track) {
            if (!tr.playTrack) {
                viewModel.play(tr.copy(playTrack = true))
                play(tr.file)
            } else {
                viewModel.play(tr.copy(playTrack = false))
                pausePlayer()
            }
        }

        val adapterHeader = AdapterHead(binding, object : OnListener {
            override fun play() {
                viewModel.getTrack(lastTrack)?.let { handleTrack(it) }
            }

        })

        val adapter = AdapterTracks(object : OnIteractionListener {
            override fun play(track: Track) {
                if (lastTrack == track.id) {
                    handleTrack(track)
                } else {
                    viewModel.stopTrack(id = lastTrack)
                    lastTrack = track.id
                    stopPlayer()
                    handleTrack(track)

                }

            }

            override fun like(track: Track) {
                viewModel.likeTrack(track.id, !track.like)
            }

            override fun block(track: Track) {
                viewModel.blockTrack(track.id, !track.blockPlay)
            }
        })


        binding.list.adapter = adapter
        viewModel.header.observe(this) {
            adapterHeader.bind(it)
            viewModel.updateTracks()
        }
        viewModel.tracks.observe(this) { tr ->
            adapter.submitList(tr)
        }

        viewModel.timeTrack.observe(this) {
            viewModel.setTimeTrack(it, lastTrack)
        }
        setContentView(binding.root)
    }

    private fun play(link: String) {
        try {
            if (player == null) {
                player = MediaPlayer().apply {
                    this.setDataSource(
                        "$URL_LOAD_SONG$link"
                    )
                    this.setOnPreparedListener {
                        it.start()
                        viewModel.timeTrack.value = it.duration
                    }
                    this.prepareAsync()
                }
            } else {
                player?.start()
            }

        } catch (e: IOException) {
            println(e)
        }
    }

    private fun pausePlayer() {
        try {
            if (player?.isPlaying == true) player?.pause()
        } catch (e: IOException) {
            println(e)
        }

    }

    private fun stopPlayer() {
        try {
            if (player != null) {
                player!!.release()
                player = null
            }
        } catch (e: IOException) {
            println(e)
        }

    }

    override fun onStop() {
        stopPlayer()
        super.onStop()
    }
}