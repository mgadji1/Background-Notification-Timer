package com.example.backgroundnotificationtimer

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

const val ACTION_KEY = "action"

const val NEW_TIMER_KEY = "add_timer"
const val REMOVED_TIMER_ID_KEY = "remove_timer"
const val RUNNING_TIMER_ID_KEY = "running_timer_position"

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

        TimerStatusEditor.changeStatus(timer.status, holder.tvStatus, holder.itemView)

        holder.tvTime.text = timer.time

        holder.btnPlayAndPause.setOnClickListener {
            when (holder.btnPlayAndPause.tag.toString()) {
                "play" -> {
                    timer.status = TimerStatus.RUNNING

                    val color = ContextCompat.getColor(holder.itemView.context, R.color.blue_pause)
                    changeImageButton(holder.btnPlayAndPause, R.drawable.pause,
                        ColorStateList.valueOf(color), "pause")

                    TimerStatusEditor.changeStatus(timer.status, holder.tvStatus, holder.itemView)

                    startTimer(timer.id, holder.itemView.context)
                }

                "pause" -> {
                    timer.status = TimerStatus.PAUSED

                    val color = ContextCompat.getColor(holder.itemView.context, R.color.green_play)
                    changeImageButton(holder.btnPlayAndPause, R.drawable.play,
                        ColorStateList.valueOf(color), "play")

                    TimerStatusEditor.changeStatus(timer.status, holder.tvStatus, holder.itemView)
                }
            }
        }

        holder.btnDelete.setOnClickListener {
            showConfirmationDialog(holder)
        }
    }

    override fun getItemCount(): Int = timers.size

    fun addTimer(timer : Timer, context: Context) {
        timers.add(timer)
        notifyItemInserted(timers.lastIndex)

        addTimerToService(timer, context)
    }

    fun removeTimer(position: Int, context: Context) {
        val removedTimer = timers[position]

        timers.removeAt(position)
        notifyItemRemoved(position)

        removeTimerFromService(removedTimer.id, context)
    }

    fun updateTimer(timer: Timer, newTime : String) {
        val index = timers.indexOfFirst { it.id == timer.id }

        if (index != -1) {
            timers[index].time = newTime
            notifyItemChanged(index)
        }
    }

    private fun changeImageButton(imageButton : ImageButton, imageResource : Int,
                            color : ColorStateList, newTag : String) {
        imageButton.setImageResource(imageResource)
        imageButton.backgroundTintList = color
        imageButton.tag = newTag
    }

    private fun startTimer(id : String, context : Context) {
        val startTimerServiceIntent = Intent(context, TimerService::class.java).apply {
            putExtra(ACTION_KEY, "start")
            putExtra(RUNNING_TIMER_ID_KEY, id)
        }

        startService(context, startTimerServiceIntent)
    }

    private fun pauseTimer(context : Context) {
        val startTimerServiceIntent = Intent(context, TimerService::class.java).apply {
            putExtra(ACTION_KEY, "pause")
        }

        startService(context, startTimerServiceIntent)
    }

    private fun addTimerToService(timer : Timer, context : Context) {
        val addTimerServiceIntent = Intent(context, TimerService::class.java).apply {
            putExtra(ACTION_KEY, "add")
            putExtra(NEW_TIMER_KEY, timer)
        }

        startService(context, addTimerServiceIntent)
    }

    private fun removeTimerFromService(id : String, context : Context) {
        val removeTimerServiceIntent = Intent(context, TimerService::class.java).apply {
            putExtra(ACTION_KEY, "remove")
            putExtra(REMOVED_TIMER_ID_KEY, id)
        }

        startService(context, removeTimerServiceIntent)
    }

    private fun startService(context: Context, intent: Intent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent)
        } else {
            context.startService(intent)
        }
    }

    private fun showConfirmationDialog(holder : TimerViewHolder) {
        val dialog = AlertDialog.Builder(holder.itemView.context)
            .setTitle("Delete this item?")
            .setPositiveButton("Yes") { dialog, _ ->
                removeTimer(holder.bindingAdapterPosition, holder.itemView.context)
                dialog.dismiss()
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.cancel()
            }.create()

        dialog.show()
    }
}