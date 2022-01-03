package com.example.erwancastioni.todo

import android.app.Application
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.erwancastioni.todo.network.Api

class App: Application() {
    override fun onCreate() {
        super.onCreate()
        Api.setUpContext(this)
    }
}

fun <T> Fragment.getNavigationResult(key: String = "result") =
    findNavController().previousBackStackEntry?.savedStateHandle?.get<T>(key)

fun <T> Fragment.getNavigationResultLiveData(key: String = "result") =
    findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<T>(key)

fun <T> Fragment.setNavigationResultToPrev(result: T, key: String = "result") {
    findNavController().previousBackStackEntry?.savedStateHandle?.set(key, result)
}

fun <T> Fragment.setNavigationResultToNext(result: T, key: String = "result") {
    findNavController().currentBackStackEntry?.savedStateHandle?.set(key, result)
}