package com.example.spaceshare.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spaceshare.data.repository.PreferencesRepository
import com.example.spaceshare.models.Preferences
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import javax.inject.Inject

class PreferencesViewModel @Inject constructor(
    private val repo: PreferencesRepository
): ViewModel() {

    private val _preferencesLiveData: MutableLiveData<Preferences?> = MutableLiveData()
    val preferencesLiveData: LiveData<Preferences?> = _preferencesLiveData

    init {
        _preferencesLiveData.value = Preferences(FirebaseAuth.getInstance().currentUser?.uid)    }

    fun setRadius(radius: Int) {
        val curPreferences = _preferencesLiveData.value

        if (curPreferences != null) {
            curPreferences.radius = radius
            _preferencesLiveData.value = curPreferences
        } else {
            val preferences = Preferences(FirebaseAuth.getInstance().currentUser?.uid)
            preferences.radius = radius
            _preferencesLiveData.value = preferences
        }
    }

    fun updatePreferences() {
        viewModelScope.launch {
            val preferences = _preferencesLiveData.value
            if (preferences != null) {
                repo.updatePreferences(preferences)
            }
        }
    }
}
