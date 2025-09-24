package com.example.backgroundnotificationtimer

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale

const val UPDATED_TIMER_KEY = "update_timer_position"
const val UPDATED_TIMER_NEW_TIME_KEY = "new_time"

class TimerService : Service() {
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private val timerJobs = mutableMapOf<String, Job>()

    private val timers = mutableListOf<Timer>()
    private val timersMap = mutableMapOf<String, Timer>()

    private val channelId = "timer_channel"

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notification = createNotification()

        startForeground(1, notification)

        val action = intent?.getStringExtra(ACTION_KEY) ?: "None"

        when (action) {
            "add" -> addTimer(intent)
            "start" -> startTimer(intent)
            "pause" -> {}
            "remove" -> removeTimer(intent)
        }

        return START_STICKY
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelName = "Timer Channel"

            val channel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_DEFAULT
            )

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createNotification() : Notification {
        val intent = Intent(this, MainActivity::class.java)

        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
                PendingIntent.FLAG_IMMUTABLE else 0
        )

        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("Timer App")
            .setContentText("Time has gone")
            .setContentIntent(pendingIntent)
            .setSmallIcon(R.drawable.timer)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()
    }

    private fun addTimer(intent: Intent?) {
        val timer = intent?.getParcelableExtra<Timer>(NEW_TIMER_KEY)

        if (timer != null) {
            timers.add(timer)
            timersMap.put(timer.id, timer)
        }
    }

    private fun removeTimer(intent: Intent?) {
        val id = intent?.getStringExtra(REMOVED_TIMER_ID_KEY)

        if (id != null && timersMap.contains(id)) {
            val removedTimer = timersMap[id]!!

            timerJobs[removedTimer.id]?.cancel()
            timerJobs.remove(removedTimer.id)

            timers.remove(removedTimer)

            timersMap.remove(id)
        }
    }

    private fun startTimer(intent: Intent?) {
        val id = intent?.getStringExtra(RUNNING_TIMER_ID_KEY)

        if (id != null && timersMap.contains(id)) {
            val timer = timersMap[id]!!

            val time = timer.time

            val timeParts = time.split(":")

            val hours = timeParts[0].toInt()
            val minutes = timeParts[1].toInt()
            val seconds = timeParts[2].toInt()

            val job = createJob(hours, minutes, seconds, timer, id)

            timerJobs[timer.name] = job
        }
    }

    private fun createJob(
        hours : Int, minutes : Int, seconds : Int,
        timer : Timer,
        id : String
    ) : Job = serviceScope.launch {
            var totalSeconds = getSecondsFromTime(hours, minutes, seconds)
            while (totalSeconds > 0) {
                delay(1000L)
                totalSeconds--
                timer.time = getTimeFromSeconds(totalSeconds)
                updateTimer(timer, timer.time)
            }

            timer.status = TimerStatus.FINISHED
            updateTimer(timer, timer.time)
        }

    private fun getSecondsFromTime(hours : Int, minutes : Int, seconds : Int) : Int =
        hours * 3600 + minutes * 60 + seconds

    private fun getTimeFromSeconds(totalSeconds : Int) : String {
        val hours = totalSeconds / 3600
        val minutes = (totalSeconds - hours * 3600) / 60
        val seconds = totalSeconds - hours * 3600 - minutes * 60

        val formattedHours = String.format(Locale.getDefault(), "%02d", hours)
        val formattedMinutes = String.format(Locale.getDefault(), "%02d", minutes)
        val formattedSeconds = String.format(Locale.getDefault(), "%02d", seconds)

        val time = "$formattedHours:$formattedMinutes:$formattedSeconds"

        return time
    }

    private fun updateTimer(timer: Timer, newTime : String) {
        val intent = Intent("timer_updated")

        intent.putExtra(UPDATED_TIMER_KEY, timer)
        intent.putExtra(UPDATED_TIMER_NEW_TIME_KEY, newTime)

        sendBroadcast(intent)
    }
}