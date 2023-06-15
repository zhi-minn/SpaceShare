package com.example.spaceshare.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import javax.inject.Inject

class MainViewModel @Inject constructor(): ViewModel() {

    private val _isHostModeLiveData = MutableLiveData<Boolean>()
    val isHostModeLiveData: LiveData<Boolean> = _isHostModeLiveData

    fun setIsHostMode(isHostMode: Boolean) {
        _isHostModeLiveData.value = isHostMode
    }
}