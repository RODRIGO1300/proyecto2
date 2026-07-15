package com.example.proyeto2.repository

import com.example.proyeto2.models.favorite.FavoriteMeal
import com.example.proyeto2.models.meal.Meal
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

class FavoriteRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    private val collection = firestore.collection("Favoritos")

    fun addFavorite(
        meal: Meal,
        note: String = "",
        onResult: (Result<Unit>) -> Unit
    ) {
        val userId = auth.currentUser?.uid
        val mealId = meal.idMeal

        if (userId.isNullOrBlank() || mealId.isNullOrBlank()) {
            onResult(Result.failure(IllegalStateException("No se pudo identificar el usuario o la receta.")))
            return
        }

        val favorite = FavoriteMeal(
            idUsuario = userId,
            idMeal = mealId,
            nombre = meal.name.orEmpty(),
            imagen = meal.imageUrl.orEmpty(),
            categoria = meal.category.orEmpty(),
            pais = meal.area.orEmpty(),
            nota = note,
            fecha = System.currentTimeMillis()
        )

        collection.document("${userId}_$mealId")
            .set(favorite)
            .addOnSuccessListener { onResult(Result.success(Unit)) }
            .addOnFailureListener { onResult(Result.failure(it)) }
    }

    fun loadFavorites(onResult: (Result<List<FavoriteMeal>>) -> Unit) {
        val userId = auth.currentUser?.uid
        if (userId.isNullOrBlank()) {
            onResult(Result.failure(IllegalStateException("Inicia sesion para ver tus favoritos.")))
            return
        }

        collection.whereEqualTo("idUsuario", userId)
            .get()
            .addOnSuccessListener { snapshot ->
                val favorites = snapshot.documents
                    .mapNotNull { it.toObject(FavoriteMeal::class.java) }
                    .sortedByDescending { it.fecha }
                onResult(Result.success(favorites))
            }
            .addOnFailureListener { onResult(Result.failure(it)) }
    }

    fun observeFavorites(onResult: (Result<List<FavoriteMeal>>) -> Unit): ListenerRegistration? {
        val userId = auth.currentUser?.uid
        if (userId.isNullOrBlank()) {
            onResult(Result.failure(IllegalStateException("Inicia sesion para ver tus favoritos.")))
            return null
        }

        return collection.whereEqualTo("idUsuario", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    onResult(Result.failure(error))
                    return@addSnapshotListener
                }

                val favorites = snapshot?.documents
                    ?.mapNotNull { it.toObject(FavoriteMeal::class.java) }
                    ?.sortedByDescending { it.fecha }
                    .orEmpty()

                onResult(Result.success(favorites))
            }
    }

    fun removeFavorite(idMeal: String, onResult: (Result<Unit>) -> Unit) {
        val userId = auth.currentUser?.uid
        if (userId.isNullOrBlank()) {
            onResult(Result.failure(IllegalStateException("Inicia sesion para eliminar favoritos.")))
            return
        }

        collection.document("${userId}_$idMeal")
            .delete()
            .addOnSuccessListener { onResult(Result.success(Unit)) }
            .addOnFailureListener { onResult(Result.failure(it)) }
    }
}
