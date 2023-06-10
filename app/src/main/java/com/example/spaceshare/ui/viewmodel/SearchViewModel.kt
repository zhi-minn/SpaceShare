package com.example.spaceshare.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.spaceshare.models.Listing

class SearchViewModel : ViewModel() {

    private val _searchResults = MutableLiveData<List<Listing>>()
    val searchResults: LiveData<List<Listing>> get() = _searchResults

    // TODO: Any business logic for searching
}