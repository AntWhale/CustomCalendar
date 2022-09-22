package com.boo.sample.mypractice

import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException


object OkHttpHelper {
    val TAG = this::class.java.simpleName
    private val client: OkHttpClient = OkHttpClient()
    var ERROR = "ERROR"

    @Throws(IOException::class)
    operator fun get(url: String): String? {
        val request: Request = Request.Builder()
            .url(url)
            .build()
        return try {
            val res: Response = client.newCall(request).execute()
            res.body?.string()
        } catch (e: IOException) {
            Log.e(TAG, e.message!!)
            throw e
        }
    }

    @Throws(IOException::class)
    fun getT(url: String): String? {
        val request: Request = Request.Builder()
            .url(url)
            .build()
        return try {
            val res: Response = client.newCall(request).execute()
            res.body?.string()
        } catch (e: IOException) {
            Log.e(TAG, e.message!!)
            throw e
        }
    }

    @Throws(IOException::class)
    fun getWithLog(url: String): String? {
        Log.d(TAG, "OkHttp call URL = $url")
        return OkHttpHelper[url]
    }
}