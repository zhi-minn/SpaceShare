package com.example.spaceshare.interfaces

import com.google.android.gms.maps.model.LatLng

interface LocationInterface {
    fun setLocation(latLng: LatLng)
}