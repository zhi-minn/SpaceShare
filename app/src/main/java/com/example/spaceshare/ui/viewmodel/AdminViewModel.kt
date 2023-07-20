package com.example.spaceshare.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spaceshare.data.repository.FirebaseStorageRepository
import com.example.spaceshare.data.repository.UserRepository
import com.example.spaceshare.models.User
import kotlinx.coroutines.launch
import javax.inject.Inject

class AdminViewModel  @Inject constructor(
    private val userRepo: UserRepository,
    private val firebaseStorageRepo: FirebaseStorageRepository
) : ViewModel() {

    private val _userEntriesLiveData: MutableLiveData<ArrayList<User>> = MutableLiveData()
    val userEntriesLiveData: LiveData<ArrayList<User>> = _userEntriesLiveData

    fun getAllUserEntries() {
        viewModelScope.launch {
            val userList = ArrayList<User>()
            val userEntries = userRepo.getAllUsers()
            for (user in userEntries) {
                if (user.governmentId != null && user.isVerified == 0) {
                    userList.add(user)
                }
            }
            _userEntriesLiveData.value = userList
        }
    }

    fun updateUserVerifiedStatus(userId: String, status: Int) {
        viewModelScope.launch {
            val curUser = userRepo.getUserById(userId)
            if (curUser != null) {
                curUser.isVerified = status
                userRepo.setUser(curUser)
            }
        }
    }
}