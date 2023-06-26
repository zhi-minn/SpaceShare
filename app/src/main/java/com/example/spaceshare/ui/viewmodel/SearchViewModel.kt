package com.example.spaceshare.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SearchViewModel : ViewModel() {

    var spaceRequired = MutableLiveData<Double>(0.0)

    private var spaceLowerLimit: Double = 0.0
    private var spaceUpperLimit: Double = 100.0


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

}