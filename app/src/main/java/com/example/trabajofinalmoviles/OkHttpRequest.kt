package com.example.trabajofinalmoviles

import okhttp3.*
import java.util.*
import java.util.Map


class OkHttpRequest(client: OkHttpClient) {
    internal var client = OkHttpClient()

    init {
        this.client = client
    }

    fun POST(url: String, parameters: HashMap<String, String>, callback: Callback): Call {
        var json = "{"
        val it = parameters.entries.iterator()
        while (it.hasNext()) {
            val pair = it.next() as Map.Entry<*, *>
            json += "\""+pair.key.toString()+"\""+":"+"\""+pair.value.toString()+"\","
        }

        json += "}"

        val client = OkHttpClient()

        val body: RequestBody = RequestBody.create(JSON, json)

        val request = Request.Builder()
            .url(url)
            .post(body)
            .build()

        val call = client.newCall(request)
        call.enqueue(callback)

        return call
    }

    fun GET(url: String, callback: Callback): Call {
        val request = Request.Builder()
            .url(url)
            .build()

        val call = client.newCall(request)
        call.enqueue(callback)
        return call
    }

    companion object {
        val JSON = MediaType.parse("application/json; charset=utf-8")
    }
}