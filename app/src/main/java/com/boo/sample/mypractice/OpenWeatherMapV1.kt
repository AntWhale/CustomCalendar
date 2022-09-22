package com.boo.sample.mypractice

import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import java.util.regex.Pattern

class OpenWeatherMapV1 {
    companion object {
        const val URL = "http://api.openweathermap.org/data/2.5/weather?q=London&APPID="
    }

    fun run() {
        val source = Observable.just(URL + "6d776655988e47c32f2457a7253051b5")
            .map(OkHttpHelper::getWithLog)
            .subscribeOn(Schedulers.io())
            .share()
            .observeOn(Schedulers.newThread())

        source.map(this::parseTemperature).subscribe { println(it) }
        source.map(this::parseCityName).subscribe { println(it) }
        source.map(this::parseCountry).subscribe { println(it) }


        Thread.sleep(3000)
    }

    private fun parseTemperature(json: String): String{
        return parse(json, "\"temp\":[0-9]*.[0-9]*")
    }

    private fun parseCityName(json: String) : String {
        return parse(json, "\"name\":\"[a-zA-Z]*\"")
    }

    private fun parseCountry(json: String) : String {
        return parse(json, "\"country\":\"[a-zA-Z]*\"")
    }

    private fun parse(json: String, regex: String): String{
        val pattern = Pattern.compile(regex)
        val match = pattern.matcher(json)
        if(match.find()) {
            return match.group()
        }
        return "N/A"
    }
}