package com.boo.sample.mypractice

import android.os.Build
import android.os.Build.VERSION_CODES.O
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

class CalendarActivity : AppCompatActivity(), OnItemListener {
    private val monthYearText by lazy { findViewById<TextView>(R.id.monthYearText) }
    private val preBtn by lazy { findViewById<ImageButton>(R.id.pre_btn) }
    private val nextBtn by lazy { findViewById<ImageButton>(R.id.next_btn) }
    private lateinit var selectedDate : LocalDate
    private val recyclerView by lazy { findViewById<RecyclerView>(R.id.recyclerView) }

    private lateinit var calendar : Calendar

    @RequiresApi(O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar)

        /*selectedDate = LocalDate.now()

        //초기화
        calendar = Calendar.getInstance()*/

        //화면 설정
        setMonthView()

        //이전 달 버튼 이벤트
        preBtn.setOnClickListener {
//            CalendarUtil.selectedDate = CalendarUtil.selectedDate.minusMonths(1)
//            calendar.add(Calendar.MONTH, -1)    //현재 달 -1
            CalendarUtil.selectedDate.add(Calendar.MONTH, -1)
            setMonthView()
        }

        //다음 달 버튼 이벤트
        nextBtn.setOnClickListener {
//            CalendarUtil.selectedDate = CalendarUtil.selectedDate.plusMonths(1)
//            calendar.add(Calendar.MONTH, 1)     //현재 달 +1
            CalendarUtil.selectedDate.add(Calendar.MONTH, 1)
            setMonthView()
        }
    }

    @RequiresApi(O)
    private fun setMonthView() {
        //년 월 텍스트뷰 세팅
        monthYearText.text = monthYearFromDate(CalendarUtil.selectedDate)

        //해당 월 날짜 가져오기
        val dayList = daysInMonthArray()

        //어댑터 데이터 적용
        val adapter = CalendarAdapter(dayList, this)

        //레이아웃 설정(열 7개)
        val manager = GridLayoutManager(applicationContext, 7)

        //레이아웃 적용
        recyclerView.layoutManager = manager

        //어댑터 적용
        recyclerView.adapter = adapter
    }

    @RequiresApi(O)
    private fun daysInMonthArray() : ArrayList<Date> {
        val dayList = arrayListOf<Date>()
        /*val yearMonth = YearMonth.from(date)

        //해당 월 마지막 날짜 가져오기(예: 28, 30, 31)
        val lastDay = yearMonth.lengthOfMonth()

        //해당 월의 첫 번째 날 가져오기(예: 4월 1일)
        val firstDay = CalendarUtil.selectedDate.withDayOfMonth(1)

        //첫 번째 날 요일 가져오기(월:1, ... ,일:7)
        val dayOfWeek = firstDay.dayOfWeek.value*/

        val monthCalendar = CalendarUtil.selectedDate.clone() as Calendar

        //1일로 셋팅
        monthCalendar[Calendar.DAY_OF_MONTH] = 1

        //해당 달의 1일의 요일[1:일요일, 2:월요일 ... 7일:토요일]
        val firstDayOfMonth = monthCalendar[Calendar.DAY_OF_WEEK] - 1

        //요일 숫자만큼 이전 날짜 추가
        //예: 6월1일이 수요일이면 3만큼 이전날짜 셋팅
        monthCalendar.add(Calendar.DAY_OF_MONTH, -firstDayOfMonth)

        while(dayList.size < 42) {
            dayList.add(monthCalendar.time)

            monthCalendar.add(Calendar.DAY_OF_MONTH, 1)
        }


        /*for(i in 1..42) {
            if( i <= dayOfWeek || i > lastDay + dayOfWeek) dayList.add(null)
            else dayList.add(LocalDate.of(CalendarUtil.selectedDate.year, CalendarUtil.selectedDate.monthValue, i - dayOfWeek))
        }*/

        return dayList
    }



    @RequiresApi(O)
    private fun monthYearFromDate(calendar: Calendar) : String{
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1

        return "$month 월 $year"
    }

    @RequiresApi(O)
    private fun yearMonthFromDate(date: LocalDate) : String{
        val formatter = DateTimeFormatter.ofPattern("yyyy년 MM월")
        return date.format(formatter)
    }

    //인터페이스 구현(날짜 어댑터에서 넘겨준 날짜를 받는다)
    @RequiresApi(O)
    override fun onItemClick(dayText: String) {
        val yearMonDay = yearMonthFromDate(selectedDate) + " " + dayText + "일"
        Toast.makeText(this, yearMonDay, Toast.LENGTH_LONG).show()
    }


}