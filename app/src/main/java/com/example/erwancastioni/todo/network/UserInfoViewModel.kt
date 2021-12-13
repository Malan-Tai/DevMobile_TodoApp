package com.example.erwancastioni.todo.network

import android.content.ContentResolver
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.erwancastioni.todo.tasklist.Task
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UserInfoViewModel : ViewModel() {
    private val repository = UserInfoRepository()

    private val _userInfo = MutableStateFlow<UserInfo>(UserInfo("", "", "", null))
    public val userInfo: StateFlow<UserInfo> = _userInfo

    fun loadUserInfo() {
        viewModelScope.launch {
            _userInfo.value = repository.loadUserInfo() ?: UserInfo("", "", "", null)
        }
    }

    fun editAvatar(contentResolver: ContentResolver, imageUri: Uri) {
        viewModelScope.launch {
            _userInfo.value = repository.updateAvatar(contentResolver, imageUri) ?: _userInfo.value
        }
    }
}