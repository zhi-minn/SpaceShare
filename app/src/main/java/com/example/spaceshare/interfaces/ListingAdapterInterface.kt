package com.example.spaceshare.interfaces

import com.example.spaceshare.models.Listing

interface ListingAdapterInterface {

    fun removeItem(listing: Listing, position: Int)

    fun viewItem(listing: Listing)
}