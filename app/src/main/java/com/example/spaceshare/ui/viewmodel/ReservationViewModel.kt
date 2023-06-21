package com.example.spaceshare.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spaceshare.data.repository.ReservationRepository
import com.example.spaceshare.models.Reservation
import com.example.spaceshare.models.User
import kotlinx.coroutines.launch
import javax.inject.Inject

class ReservationViewModel @Inject constructor(
    private val repo: ReservationRepository
): ViewModel() {

    private val _reservationLiveData: MutableLiveData<List<Reservation>> = MutableLiveData()
    val listingsLiveData: LiveData<List<Reservation>> = _reservationLiveData

    fun fetchReservations(user: User, asClient: Boolean) {
        viewModelScope.launch {
            val reservations = repo.fetchReservations(user, asClient)
            _reservationLiveData.value = reservations
        }
    }
}