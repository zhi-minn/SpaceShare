package com.example.spaceshare.data.implementation

import android.util.Log
import com.example.spaceshare.data.repository.SearchRepository
import com.example.spaceshare.models.SearchCriteria
import com.example.spaceshare.utils.MathUtil
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.Query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SearchRepoImpl @Inject constructor(
    private val db: FirebaseFirestore
): SearchRepository {
    companion object {
        private val TAG = this::class.simpleName
    }
    private val searchDB = db.collection("Search Example")
    override suspend fun search(criteria : SearchCriteria) : List<SearchCriteria> {
        return withContext(Dispatchers.IO) {
            try {
                val res = searchDB.whereGreaterThanOrEqualTo("UnitNum", criteria.unitNum)
                    .whereLessThanOrEqualTo("Time.0", criteria.startDate)
                    .whereGreaterThanOrEqualTo("Time.1", criteria.endDate)
                    .orderBy("Price", Query.Direction.ASCENDING)
                    .get()
                    .addOnSuccessListener {
                            querySnapshot ->
                        querySnapshot.documents.sortedBy {
                                doc ->
                            val docPoint: GeoPoint? = doc.getGeoPoint("location")
                            // #TODO NEED to consider When GEOPoint is not received.
                            val dist = docPoint?.let { MathUtil.calculateDistanceInKilometers(criteria.location, it) }
                            dist
                        }
                    }
                    .await()
                return@withContext res.documents.mapNotNull {
                        document ->
                    try {
                        document.toObject(SearchCriteria::class.java)
                    } catch (e: Exception) {
                        Log.e(SearchRepoImpl.TAG, "Error casting document to Search object: ${e.message}")
                        null
                    }
                }
            } catch (e: Exception) {
                Log.e(SearchRepoImpl.TAG, "Error reading Search document: ${e.message}")
                return@withContext emptyList()
            }
        }
    }
}