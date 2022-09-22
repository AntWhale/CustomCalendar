package com.boo.sample.mypractice

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate
import java.util.*

class CalendarUtil {
    companion object {
        @RequiresApi(Build.VERSION_CODES.O)
        var selectedDate : Calendar = Calendar.getInstance()
    }
}