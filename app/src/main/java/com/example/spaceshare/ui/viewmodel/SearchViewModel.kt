package com.example.spaceshare.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.spaceshare.interfaces.LocationInterface
import com.google.android.gms.maps.model.LatLng

class SearchViewModel : ViewModel(), LocationInterface {

    var spaceRequired = MutableLiveData<Double>(0.0)

    private val spaceLowerLimit: Double = 0.0
    private val spaceUpperLimit: Double = 100.0

    var location: MutableLiveData<LatLng> = MutableLiveData<LatLng>()

    fun incrementSpaceRequired() {
        if (spaceRequired.value?.plus(0.5)!! < spaceUpperLimit)
            spaceRequired.value = spaceRequired.value?.plus(0.5)
    }

    fun decrementSpaceRequired() {
        if (spaceRequired.value?.minus(0.5)!! >= spaceLowerLimit)
            spaceRequired.value = spaceRequired.value?.minus(0.5)
    }

    fun getSpaceRequiredText(): String {
        return spaceRequired.toString()
    }

    override fun setLocation(latLng: LatLng) {
        location.value = latLng
    }

}