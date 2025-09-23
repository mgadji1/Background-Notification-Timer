package com.example.backgroundnotificationtimer

import android.view.View
import android.widget.TextView

class TimerStatusEditor {
    companion object {
        fun changeStatus(status : TimerStatus, textView : TextView, itemView : View) {
            when (status) {
                TimerStatus.CREATED -> {
                    textView.text = "Created"
                    textView.setTextColor(itemView.context.getColor(R.color.created_state))
                }
                TimerStatus.RUNNING -> {
                    textView.text = "Running"
                    textView.setTextColor(itemView.context.getColor(R.color.running_state))
                }
                TimerStatus.PAUSED -> {
                    textView.text = "Paused"
                    textView.setTextColor(itemView.context.getColor(R.color.paused_state))
                }
                TimerStatus.FINISHED -> {
                    textView.text = "Finished"
                    textView.setTextColor(itemView.context.getColor(R.color.finished_state))
                }
            }
        }
    }
}