package com.boo.sample.mypractice

import android.graphics.Color
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import java.time.LocalDate
import java.util.*
import kotlin.collections.ArrayList

class CalendarAdapter(val dayList : ArrayList<Date>, val onItemListener: OnItemListener) :
    RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.calendar_cell, parent, false)

        return CalendarViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: CalendarViewHolder, position: Int) {
        //날짜 변수에 담기
        val monthDate = dayList[position]

        //초기화
        var dateCalendar = Calendar.getInstance()

        //날짜 캘린더에 담기
        dateCalendar.time = monthDate

        //캘린더값 날짜 변수에 담기
        var dayNo = dateCalendar.get(Calendar.DAY_OF_MONTH)
        holder.dayText.text = dayNo.toString()

        //넘어온 날짜
        var iYear  = dateCalendar.get(Calendar.YEAR)
        var iMonth = dateCalendar.get(Calendar.MONTH) + 1
        var iDay   = dateCalendar.get(Calendar.DAY_OF_MONTH)

        //현재날짜
        var selectYear = CalendarUtil.selectedDate.get(Calendar.YEAR)
        var selectMonth = CalendarUtil.selectedDate.get(Calendar.MONTH) + 1
        var selectDay = CalendarUtil.selectedDate.get(Calendar.DAY_OF_MONTH)

        if(iYear == selectYear && iMonth == selectMonth) { //같다면 진한 색상
            holder.dayText.setTextColor(Color.parseColor("#000000")) //검정

            //현재 일자 비교해서 배경색상 변경
            if(selectDay == dayNo) {
                holder.itemView.setBackgroundColor(Color.LTGRAY)
            }

            //텍스트 색상 지정(토요일, 일요일)
            if((position + 1) % 7 == 0) { //토요일
                holder.dayText.setTextColor(Color.BLUE)
            } else if(position == 0 || position % 7 == 0) {
                holder.dayText.setTextColor(Color.RED)
            }
        } else { //다르다면 연한색
            holder.dayText.setTextColor(Color.parseColor("#B4B4B4"))

            //텍스트 색상 지정(토요일, 일요일)
            if((position + 1) % 7 == 0) { //토요일
                holder.dayText.setTextColor(Color.parseColor("#B4FFFF"))
            } else if(position == 0 || position % 7 == 0) {
                holder.dayText.setTextColor(Color.parseColor("#FFB4B4"))
            }
        }



        /*if(day == null) holder.dayText.text = ""
        else {
            holder.dayText.text = day.dayOfMonth.toString()

            //현재 날짜 색상 칠하기
            if(day == CalendarUtil.selectedDate) {
                holder.itemView.setBackgroundColor(Color.LTGRAY)
            }
        }*/



        //날짜 클릭 이벤트
        holder.bindClickListener(dateCalendar)
    }

    override fun getItemCount(): Int {
        return dayList.size
    }

    inner class CalendarViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        val dayText : TextView = itemView.findViewById(R.id.dayText)

        @RequiresApi(Build.VERSION_CODES.O)
        fun bindClickListener(dateCalendar : Calendar){
            itemView.setOnClickListener {
                var iYear  = dateCalendar.get(Calendar.YEAR)
                var iMonth = dateCalendar.get(Calendar.MONTH) + 1
                var iDay   = dateCalendar.get(Calendar.DAY_OF_MONTH)

                var yearMonDay = "$iYear 년 $iMonth 월 $iDay 일"

                Toast.makeText(itemView.context, yearMonDay, Toast.LENGTH_SHORT).show()

//                onItemListener.onItemClick(day)
            }
        }

    }
}