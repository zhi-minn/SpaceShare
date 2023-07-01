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

    override suspend fun createUser(user: User): String {
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

}