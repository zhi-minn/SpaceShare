package com.example.spaceshare.data.repository

import com.example.spaceshare.models.SearchCriteria
import com.example.spaceshare.models.User

interface SearchRepository {

    suspend fun search(criteria : SearchCriteria) : List<SearchCriteria>
}