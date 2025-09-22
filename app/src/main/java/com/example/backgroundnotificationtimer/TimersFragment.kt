package com.example.backgroundnotificationtimer

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.Locale

class TimersFragment : Fragment() {
    private lateinit var adapter : TimerAdapter

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
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        val btnAdd = view.findViewById<FloatingActionButton>(R.id.btnAdd)

        btnAdd.setOnClickListener {
            parentFragmentManager
                .beginTransaction()
                .replace(R.id.fragment_container, NewTimerFragment.newInstance())
                .addToBackStack(null)
                .commit()
        }

        parentFragmentManager.setFragmentResultListener(NEW_TIMER_RESULT_KEY, this) { _, bundle ->
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

        val newTimer = Timer(
            name,
            status,
            time
        )

        adapter.addTimer(newTimer)
    }

    private fun formatTime(value : Int) : String = String.format(Locale.getDefault(), "%02d", value)

    companion object {
        @JvmStatic
        fun newInstance() = TimersFragment()
    }
}