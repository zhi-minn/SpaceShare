package com.example.spaceshare.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spaceshare.data.repository.UserRepository
import com.example.spaceshare.models.User
import kotlinx.coroutines.launch
import javax.inject.Inject

class ProfileViewModel @Inject constructor(
    private val userRepo: UserRepository
) : ViewModel() {

    private val _userLiveData: MutableLiveData<User> = MutableLiveData()
    val userLiveData: LiveData<User> = _userLiveData

    fun getUserById(userId: String) {
        viewModelScope.launch {
            userRepo.getUserById(userId)?.let {
                _userLiveData.value = it
            }
        }
    }
}