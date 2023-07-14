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
    @ColumnInfo(name = "is_verified") var isVerified: Boolean = false,
    @ColumnInfo(name = "fcm_token") var fcmToken: String = ""
) {
    constructor() : this("", "", "", null, "", false, "")
}
