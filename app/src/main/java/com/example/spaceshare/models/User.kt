package com.example.spaceshare.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "first_name") var firstName: String,
    @ColumnInfo(name = "last_name") var lastName: String,
    @ColumnInfo(name = "photo_url") var photoPath: String? = null,
    @ColumnInfo(name = "phone_number") var phoneNumber: String = "",
    @ColumnInfo(name = "verified") var isVerified: Int = 0,
    @ColumnInfo(name = "fcm_token") var fcmToken: String = "",
    @ColumnInfo(name = "government_id_url") var governmentId: String? = null
) {
    constructor() : this("", "", "", null, "", 0, "", null)
}


