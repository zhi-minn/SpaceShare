package com.example.spaceshare.data.repository

import android.net.Uri

interface FirebaseStorageRepository {

    suspend fun uploadFile(folderPath: String, fileUri: Uri)

    suspend fun downloadFile(folderPath: String, fileUri: Uri)
}