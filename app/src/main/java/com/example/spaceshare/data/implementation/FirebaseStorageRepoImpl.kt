package com.example.spaceshare.data.implementation

import android.net.Uri
import android.util.Log
import com.example.spaceshare.data.repository.FirebaseStorageRepository
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject

class FirebaseStorageRepoImpl @Inject constructor(
    private val storage: FirebaseStorage
): FirebaseStorageRepository {
    override suspend fun uploadFile(folderPath: String, fileUri: Uri): String {
        return withContext(Dispatchers.IO) {
            val fileName = "${fileUri.lastPathSegment}_${UUID.randomUUID()}"
            val storageRef = storage
                .reference
                .child("$folderPath/$fileName")
            val upload = storageRef.putFile(fileUri).await()

            if (upload.task.isSuccessful) {
                fileName
            } else {
                val errorMsg = upload.task.exception?.message
                throw Exception("Failed to upload file: $errorMsg")
            }
        }
    }

    override suspend fun downloadFile(folderPath: String, fileUri: Uri) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteFile(folderPath: String, fileName: String) {
        return withContext(Dispatchers.IO) {
            val imageRef = storage.reference.child("$folderPath/$fileName")
            imageRef.delete()
                .addOnSuccessListener {
                    Log.i("FirebaseStorageRepoImpl", "File successfully deleted")
                }
                .addOnFailureListener { e ->
                    Log.e("FirebaseStorageRepoImpl", "Error deleting file: ${e.message}", e)
                }
        }
    }

}