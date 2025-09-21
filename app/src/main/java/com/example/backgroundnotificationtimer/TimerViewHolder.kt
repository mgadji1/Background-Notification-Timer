package com.example.backgroundnotificationtimer

import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TimerViewHolder(containerView : View) : RecyclerView.ViewHolder(containerView) {
    val tvName = containerView.findViewById<TextView>(R.id.tvName)
    val tvStatus = containerView.findViewById<TextView>(R.id.tvStatus)
    val tvTime = containerView.findViewById<TextView>(R.id.tvTime)

    val btnPlayAndPause = containerView.findViewById<ImageButton>(R.id.btnPlayAndPause)
    val btnStop = containerView.findViewById<ImageButton>(R.id.btnStop)
}