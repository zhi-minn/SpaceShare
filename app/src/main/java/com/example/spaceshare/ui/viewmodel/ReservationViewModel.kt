package com.example.spaceshare.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spaceshare.data.repository.ReservationRepository
import com.example.spaceshare.manager.SharedPreferencesManager.isHostMode
import com.example.spaceshare.models.Listing
import com.example.spaceshare.models.Reservation
import com.example.spaceshare.models.User
import kotlinx.coroutines.launch
import javax.inject.Inject

class ReservationViewModel @Inject constructor(
    private val repo: ReservationRepository
): ViewModel() {

    private val _reservationLiveData: MutableLiveData<List<Reservation>> = MutableLiveData()
    val reservationLiveData: LiveData<List<Reservation>> = _reservationLiveData

    private val _listingLiveData: MutableLiveData<List<Listing>> = MutableLiveData()
    val listingLiveData: LiveData<List<Listing>> = _listingLiveData

    fun fetchReservations(user: User) {
        viewModelScope.launch {
            val reservations = repo.fetchReservations(user, isHostMode.value ?: false)
            _reservationLiveData.value = reservations
        }
    }

//    fun fetchListingsBasedOnReservations() {
//
//    }
}