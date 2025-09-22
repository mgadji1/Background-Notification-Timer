package com.example.backgroundnotificationtimer

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.NumberPicker
import android.widget.Toast
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.Locale

const val TIMER_NAME_KEY = "timer_name"
const val TIMER_SECONDS_KEY = "seconds"
const val TIMER_MINUTES_KEY = "minutes"
const val TIMER_HOURS_KEY = "hours"
const val NEW_TIMER_RESULT_KEY = "new_timer"

class NewTimerFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_new_timer, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val timePickerSeconds = view.findViewById<NumberPicker>(R.id.timePickerSeconds)
        val timePickerMinutes = view.findViewById<NumberPicker>(R.id.timePickerMinutes)
        val timePickerHours = view.findViewById<NumberPicker>(R.id.timePickerHours)

        tuneTimePicker(timePickerSeconds)
        tuneTimePicker(timePickerMinutes)
        tuneTimePicker(timePickerHours)

        val btnOk = view.findViewById<FloatingActionButton>(R.id.btnOk)

        val edTimerName = view.findViewById<EditText>(R.id.edTimerName)

        btnOk.setOnClickListener {
            val name = edTimerName.text.toString()

            if (name.isNotEmpty()) {
                sendResult(name, timePickerSeconds, timePickerMinutes, timePickerHours)
            } else {
                Toast.makeText(requireContext(), "Name cannot be empty", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun sendResult(
        name : String,
        timePickerSeconds : NumberPicker,
        timePickerMinutes : NumberPicker,
        timePickerHours : NumberPicker,
    ) {
        val seconds = timePickerSeconds.value
        val minutes = timePickerMinutes.value
        val hours = timePickerHours.value

        val result = Bundle().apply {
            putString(TIMER_NAME_KEY, name)
            putInt(TIMER_SECONDS_KEY, seconds)
            putInt(TIMER_MINUTES_KEY, minutes)
            putInt(TIMER_HOURS_KEY, hours)
        }

        parentFragmentManager.setFragmentResult(NEW_TIMER_RESULT_KEY, result)
        parentFragmentManager.popBackStack()
    }

    private fun tuneTimePicker(timePicker : NumberPicker) {
        timePicker.minValue = 0
        timePicker.maxValue = 59
        timePicker.setFormatter { value -> String.format(Locale.getDefault(), "%02d", value) }
    }

    companion object {
        @JvmStatic
        fun newInstance() = NewTimerFragment()
    }
}