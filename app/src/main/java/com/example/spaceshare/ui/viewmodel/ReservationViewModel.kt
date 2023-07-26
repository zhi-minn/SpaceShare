package com.example.spaceshare.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spaceshare.data.repository.ReservationRepository
import com.example.spaceshare.manager.SharedPreferencesManager.isHostMode
import com.example.spaceshare.models.Chat
import com.example.spaceshare.models.Listing
import com.example.spaceshare.models.Reservation
import com.example.spaceshare.models.ReservationStatus
import com.example.spaceshare.models.User
import com.example.spaceshare.models.toInt
import com.google.firebase.Timestamp
import kotlinx.coroutines.launch
import javax.inject.Inject

class ReservationViewModel @Inject constructor(
    private val repo: ReservationRepository
): ViewModel() {

    private val _reservationLiveData: MutableLiveData<List<Reservation>> = MutableLiveData()
    val reservationLiveData: LiveData<List<Reservation>> = _reservationLiveData

    private val _listingReserved: MutableLiveData<Boolean> = MutableLiveData()
    val listingReserved: LiveData<Boolean> = _listingReserved

    private val _userInfoLiveData: MutableLiveData<User> = MutableLiveData()
    val userInfoLiveData: LiveData<User> = _userInfoLiveData

    private val _setStatusSuccess: MutableLiveData<Boolean> = MutableLiveData()
    val setStatusSuccess: LiveData<Boolean> = _setStatusSuccess

    fun fetchReservations(user: User) {
        viewModelScope.launch {
            val reservations = repo.fetchReservations(user, isHostMode.value ?: false)
            _reservationLiveData.value = reservations
        }
    }

    fun reserveListing(reservation: Reservation) {
        viewModelScope.launch {
            if (reservation.hostId == reservation.clientId) {
                throw Exception("hostId cannot be the same as clientId")
            }

            try {
                repo.createReservation(reservation)
                _listingReserved.value = true

            } catch (e: Exception) {
                _listingReserved.value = false
                null
            }
        }
    }

    fun fetchUserInfo(id: String) {
        viewModelScope.launch {
            val info = repo.fetchUser(id)
            _userInfoLiveData.value = info!!
        }
    }

    fun sendMessage(chat : Chat) {
        viewModelScope.launch {
//            try {
            repo.setChat(chat)
//                _chat.value = true
//
//            } catch (e: Exception) {
//                _chat.value = false
//                null
//            }
        }
    }

    fun setReservationStatus(reservation : Reservation, status : ReservationStatus) {
        viewModelScope.launch {
            try {
                repo.setReservationStatus(reservation, status)
                _setStatusSuccess.value = true

            } catch (e: Exception) {
                _setStatusSuccess.value = false
                null
            }
        }
    }

    fun setReservationRated(reservation : Reservation, rated : Boolean) {
        viewModelScope.launch {
            try {
                repo.setReservationRated(reservation, rated)

            } catch (e: Exception) {
                null
            }
        }
    }
}