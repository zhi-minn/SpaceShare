package com.example.spaceshare.ui.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spaceshare.data.repository.FirebaseStorageRepository
import com.example.spaceshare.data.repository.UserRepository
import com.example.spaceshare.models.User
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

class ProfileViewModel @Inject constructor(
    private val userRepo: UserRepository,
    private val firebaseStorageRepo: FirebaseStorageRepository
) : ViewModel() {

    private val _userLiveData: MutableLiveData<User> = MutableLiveData()
    val userLiveData: LiveData<User> = _userLiveData

    // Government ID
    private val _fileNameLiveData = MutableLiveData<String>()
    val fileNameLiveData: LiveData<String> = _fileNameLiveData

    fun setFileName(fileName: Uri) {
        _fileNameLiveData.value = fileName.toString()
    }

    fun getUserById(userId: String) {
        viewModelScope.launch {
            userRepo.getUserById(userId)?.let {
                _userLiveData.value = it
            }
        }
    }

    fun updateUser(user: User) {
        viewModelScope.launch {
            _userLiveData.value = user
            userRepo.setUser(user)
        }
    }

    fun updateUserPhoto(imageUri: Uri, callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            // Delete current profile image if exists
            _userLiveData.value?.photoPath?.let {
                viewModelScope.async {
                    firebaseStorageRepo.deleteFile("profiles", it)
                }
            }

            val uploadTask = viewModelScope.async {
                try {
                    firebaseStorageRepo.uploadFile("profiles", imageUri)
                } catch (e: Exception) {
                    // TODO: Handle file upload exception here
                    null
                }
            }
            val imagePath = uploadTask.await()
            imagePath?.let { imagePath ->
                val curUser = _userLiveData.value
                curUser?.let {
                    it.photoPath = imagePath
                    _userLiveData.value = it
                    userRepo.setUser(it)
                }
            }
            callback(true)
        }
    }

    fun updateGovernmentId(imageUri: Uri, callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            _userLiveData.value?.governmentId?.let {
                println("deleting!")
                viewModelScope.async {
                    firebaseStorageRepo.deleteFile("ids", it)
                }
            }

            val uploadTask = viewModelScope.async {
                try {
                    firebaseStorageRepo.uploadFile("ids", imageUri)
                } catch (e: Exception) {
                    null
                }
            }

            val imagePath = uploadTask.await()
            imagePath?.let { imagePath ->
                val curUser = _userLiveData.value
                curUser?.let {
                    it.governmentId = imagePath
                    _userLiveData.value = it
                    userRepo.setUser(it)
                }
            }
            callback(true)
        }
    }
}