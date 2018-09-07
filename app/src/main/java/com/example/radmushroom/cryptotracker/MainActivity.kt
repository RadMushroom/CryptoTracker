package com.example.radmushroom.cryptotracker

import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.*
import java.io.IOException

class MainActivity : AppCompatActivity(), SwipeRefreshLayout.OnRefreshListener {

    private lateinit var linearLayoutManager : LinearLayoutManager
    private lateinit var adapter: CryptoAdapter
    private val client by lazy { OkHttpClient() }
    private val request by lazy {
        Request.Builder().url(Constants.apiURL).build()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        linearLayoutManager = LinearLayoutManager(this)
        cryptoRV.layoutManager = linearLayoutManager
        adapter = CryptoAdapter()
        cryptoRV.adapter = adapter

        mSwipeRefreshLayout.setOnRefreshListener(this)
        showCurrency()
    }

    private fun showCurrency() {

        mSwipeRefreshLayout.isRefreshing = true

        client.newCall(request).enqueue(object : Callback{
            override fun onFailure(call: Call, e: IOException) {
                Log.e("Request", e.localizedMessage)
            }

            override fun onResponse(call: Call, response: Response?) {
                val body = response?.body()?.string()
                Log.e("Response", body)
                val gson = Gson()
                val cryptoCoins : List<CryptoModel> = gson.fromJson(body, object : TypeToken<List<CryptoModel>>() {}.type)

                runOnUiThread{
                    adapter.updateData(cryptoCoins)
                    mSwipeRefreshLayout.isRefreshing = false
                }

            }

        })
    }

    override fun onRefresh() {
        showCurrency()
    }
}
