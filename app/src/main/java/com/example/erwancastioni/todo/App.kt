package com.example.erwancastioni.todo

import android.app.Application
import com.example.erwancastioni.todo.network.Api

class App: Application() {
    override fun onCreate() {
        super.onCreate()
        Api.setUpContext(this)
    }
}