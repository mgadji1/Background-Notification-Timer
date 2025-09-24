package com.example.backgroundnotificationtimer

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Timer(
    val id : String,
    val name : String,
    var status : TimerStatus,
    var time : String
) : Parcelable
