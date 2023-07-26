package com.example.spaceshare.models

data class Shortlist (
    val userId: String? = null,
    val listingIds: MutableList<String> = mutableListOf(),
){
}