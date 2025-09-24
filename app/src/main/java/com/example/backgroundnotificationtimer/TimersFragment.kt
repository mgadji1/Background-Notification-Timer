package com.example.backgroundnotificationtimer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.Locale
import java.util.UUID

class TimersFragment : Fragment() {

    private val timeReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val timer = intent?.getParcelableExtra<Timer>(UPDATED_TIMER_KEY)
            val newTime = intent?.getStringExtra(UPDATED_TIMER_NEW_TIME_KEY)

            if (timer != null && newTime != null) {
                adapter.updateTimer(timer, newTime)
            }
        }
    }
    private lateinit var adapter : TimerAdapter

    override fun onStart() {
        super.onStart()
        ContextCompat.registerReceiver(
            requireContext(),
            timeReceiver,
            IntentFilter("timer_updated"),
            ContextCompat.RECEIVER_EXPORTED
        )
    }

    override fun onStop() {
        super.onStop()
        requireContext().unregisterReceiver(timeReceiver)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_timers, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = TimerAdapter(layoutInflater)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)

        (recyclerView.itemAnimator as? SimpleItemAnimator)?.supportsChangeAnimations = false

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        val btnAdd = view.findViewById<FloatingActionButton>(R.id.btnAdd)

        btnAdd.setOnClickListener {
            parentFragmentManager
                .beginTransaction()
                .hide(this)
                .add(R.id.fragment_container, NewTimerFragment.newInstance())
                .addToBackStack(null)
                .commit()
        }

        parentFragmentManager.setFragmentResultListener(NEW_TIMER_RESULT_KEY, viewLifecycleOwner) { _, bundle ->
            createTimer(bundle)
        }
    }

    private fun createTimer(bundle: Bundle) {
        val name = bundle.getString(TIMER_NAME_KEY)!!

        val seconds = bundle.getInt(TIMER_SECONDS_KEY)
        val minutes = bundle.getInt(TIMER_MINUTES_KEY)
        val hours = bundle.getInt(TIMER_HOURS_KEY)

        val status = TimerStatus.CREATED

        val time = "${formatTime(hours)}:${formatTime(minutes)}:${formatTime(seconds)}"

        val id = generateTimerId()

        val newTimer = Timer(
            id,
            name,
            status,
            time
        )

        adapter.addTimer(newTimer, requireContext())
    }

    private fun formatTime(value : Int) : String = String.format(Locale.getDefault(), "%02d", value)

    companion object {
        @JvmStatic
        fun newInstance() = TimersFragment()
    }

    private fun generateTimerId() : String = UUID.randomUUID().toString()
}