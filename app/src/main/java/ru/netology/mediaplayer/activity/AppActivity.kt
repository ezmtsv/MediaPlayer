package ru.netology.mediaplayer.activity

import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.view.get
import ru.netology.mediaplayer.activity.player.MPlayer
import ru.netology.mediaplayer.adapter.AdapterHead
import ru.netology.mediaplayer.adapter.OnIteractionListener
import ru.netology.mediaplayer.adapter.AdapterTracks
import ru.netology.mediaplayer.adapter.OnListener
import ru.netology.mediaplayer.databinding.ActivityAppBinding
import ru.netology.mediaplayer.dto.Track
import ru.netology.mediaplayer.viewmodel.ViewModelTrack
import java.io.IOException

const val URL_LOAD_SONG =
    "https://raw.githubusercontent.com/netology-code/andad-homeworks/master/09_multimedia/data/"


class AppActivity : AppCompatActivity() {
    private var lastTrack: Int = 0
    private val pl = MPlayer()

    val viewModel: ViewModelTrack by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityAppBinding.inflate(layoutInflater)


        fun handleTrack(tr: Track) {
            if (!tr.playTrack) {
                viewModel.play(tr.copy(playTrack = true))

                pl.play(tr.file, object : MPlayer.GetInfo {
                    override fun getDuration(dut: Int) {
                        if (dut != 0) viewModel.timeTrack.value = dut
                    }

                    override fun onCompletionPlay() {
                        viewModel.stopTrack(id = lastTrack)
                        lastTrack = tr.id +1
                        pl.stopPlayer()
                        handleTrack(viewModel.getNextTrack(tr))
                    }

                })
            } else {
                viewModel.play(tr.copy(playTrack = false))
                pl.pausePlayer()
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
                    pl.stopPlayer()
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
            binding.swipeRefreshLayout.isRefreshing = false
        }
        viewModel.tracks.observe(this) { tr ->
            adapter.submitList(tr)
        }

        viewModel.timeTrack.observe(this) {
            viewModel.setTimeTrack(it, lastTrack)
        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.getAlbum()
        }
        binding.swipeRefreshLayout.setColorSchemeResources(
            android.R.color.holo_blue_bright,
            android.R.color.holo_green_light,
            android.R.color.holo_red_light,
        )
        setContentView(binding.root)
    }

    override fun onStop() {
        pl.stopPlayer()
        super.onStop()
    }

    override fun onDestroy() {
        pl.stopPlayer()
        super.onDestroy()
    }

}