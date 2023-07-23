package com.example.spaceshare.data.implementation

import android.util.Log
import com.example.spaceshare.data.dao.UserDao
import com.example.spaceshare.data.repository.UserRepository
import com.example.spaceshare.models.User
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class UserRepoImpl @Inject constructor(
    db: FirebaseFirestore,
    private val userDao: UserDao
): UserRepository {

    companion object {
        private val TAG = this::class.simpleName
    }

    private val userCollection = db.collection("users")

    private val adminCollection = db.collection("admins")

    override suspend fun setUser(user: User): String {
        return withContext(Dispatchers.IO) {
            val deferred = CompletableDeferred<String>()

            userDao.insertUser(user)

            userCollection.document(user.id)
                .set(user)
                .addOnSuccessListener {
                    Log.d(TAG, "Created user with id ${user.id}")
                    deferred.complete(user.id)
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Error creating user", e)
                    deferred.completeExceptionally(e)
                }

            deferred.await()
        }
    }

    override suspend fun getUserById(userId: String): User? {
    return withContext(Dispatchers.IO) {
        userDao.getUserById(userId)
            ?: try {
                val snapshot = userCollection.document(userId).get().await()
                val firestoreUser = snapshot.toObject(User::class.java)

                if (firestoreUser != null) {
                    userDao.insertUser(firestoreUser)
                }

                firestoreUser
            }  catch (e: Exception) {
                Log.e(TAG, "Error fetching user from Firestore: ${e.message}", e)
                null
            }
        }
    }

    override suspend fun getAllUsers(): List<User> = withContext(Dispatchers.IO) {
        try {
            val result = userCollection
                .get()
                .await()

            return@withContext result.documents.mapNotNull { document ->
                try {
                    val user = document.toObject(User::class.java)
                    user
                } catch (e: Exception) {
                    Log.e(UserRepoImpl.TAG, "Error casting document to User object: ${e.message}")
                    null
                }
            }
        } catch (e: Exception) {
            Log.e(UserRepoImpl.TAG, "Error reading users document: ${e.message}")
            return@withContext emptyList()
        }
    }

    override suspend fun getUserVerifiedStatus(userId: String): Long = withContext(Dispatchers.IO) {
        try {
            val result = userCollection.document(userId)
                .get()
                .await()
            return@withContext result.data?.get("verified") as Long
        } catch (e: Exception) {
            Log.e(UserRepoImpl.TAG, "Error reading users document: ${e.message}")
            return@withContext 0
        }
    }

    override suspend fun updateUserVerifiedStatus(userId: String, status: Int) {
        val userRef = userCollection.document(userId)
        userRef.update("verified", status)
    }

    override suspend fun getAdminUsers(): List<String> = withContext(Dispatchers.IO) {
        try {
            val result = adminCollection.document("admins")
                .get()
                .await()
            return@withContext result.data?.get("data") as List<String>
        } catch (e: Exception) {
            return@withContext emptyList()
        }
    }
}