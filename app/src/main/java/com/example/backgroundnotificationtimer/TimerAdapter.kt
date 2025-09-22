package com.example.backgroundnotificationtimer

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class TimerAdapter(
    private val layoutInflater: LayoutInflater
) : RecyclerView.Adapter<TimerViewHolder>() {
    private val timers = mutableListOf<Timer>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TimerViewHolder {
        val view = layoutInflater.inflate(R.layout.timer, parent, false)
        return TimerViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: TimerViewHolder,
        position: Int
    ) {
        val timer = timers[position]

        holder.tvName.text = timer.name
        holder.tvStatus.text = when (timer.status) {
            TimerStatus.CREATED -> "Created"
            TimerStatus.RUNNING -> "Running"
            TimerStatus.PAUSED -> "Paused"
            TimerStatus.FINISHED -> "Finished"
        }
        holder.tvTime.text = timer.time

        holder.btnPlayAndPause.setOnClickListener {
            when (holder.btnPlayAndPause.tag) {
                "play" -> {
                    val color = ContextCompat.getColor(holder.itemView.context, R.color.blue_pause)
                    setListener(holder.btnPlayAndPause, R.drawable.pause,
                        ColorStateList.valueOf(color), "pause")
                }

                "pause" -> {
                    val color = ContextCompat.getColor(holder.itemView.context, R.color.green_play)
                    setListener(holder.btnPlayAndPause, R.drawable.play,
                        ColorStateList.valueOf(color), "play")
                }
            }
        }

        holder.btnStop.setOnClickListener {

        }
    }

    override fun getItemCount(): Int = timers.size

    fun addTimer(timer : Timer) {
        timers.add(timer)
        notifyItemInserted(timers.lastIndex)
    }

    private fun setListener(imageButton : ImageButton, imageResource : Int,
                            color : ColorStateList, newTag : String) {
        imageButton.setImageResource(imageResource)
        imageButton.backgroundTintList = color
        imageButton.tag = newTag
    }
}