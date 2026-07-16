package com.example.proyeto2.repository

import com.example.proyeto2.models.favorite.FavoriteMeal
import com.example.proyeto2.models.planner.MealPlanSlot
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

class PlannerRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    private val collection = firestore.collection("MealPlanner")

    fun observePlanner(onResult: (Result<List<MealPlanSlot>>) -> Unit): ListenerRegistration? {
        val userId = auth.currentUser?.uid
        if (userId.isNullOrBlank()) {
            onResult(Result.failure(IllegalStateException("Inicia sesion para ver tu planeador.")))
            return null
        }

        return collection.whereEqualTo("idUsuario", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    onResult(Result.failure(error))
                    return@addSnapshotListener
                }

                val slots = snapshot?.documents
                    ?.mapNotNull { it.toObject(MealPlanSlot::class.java) }
                    .orEmpty()
                onResult(Result.success(slots))
            }
    }

    fun assignMeal(
        day: String,
        mealTime: String,
        favorite: FavoriteMeal,
        comments: String = "",
        onResult: (Result<Unit>) -> Unit
    ) {
        val userId = auth.currentUser?.uid
        if (userId.isNullOrBlank()) {
            onResult(Result.failure(IllegalStateException("Inicia sesion para asignar platillos.")))
            return
        }

        val slot = MealPlanSlot(
            idUsuario = userId,
            dia = day,
            tiempo = mealTime,
            idMeal = favorite.idMeal,
            nombre = favorite.nombre,
            imagen = favorite.imagen,
            comentarios = comments,
            fecha = System.currentTimeMillis()
        )

        collection.document("${userId}_${day}_$mealTime")
            .set(slot)
            .addOnSuccessListener { onResult(Result.success(Unit)) }
            .addOnFailureListener { onResult(Result.failure(it)) }
    }

    fun updateComments(
        day: String,
        mealTime: String,
        comments: String,
        onResult: (Result<Unit>) -> Unit
    ) {
        val userId = auth.currentUser?.uid
        if (userId.isNullOrBlank()) {
            onResult(Result.failure(IllegalStateException("Inicia sesion para editar comentarios.")))
            return
        }

        collection.document("${userId}_${day}_$mealTime")
            .update("comentarios", comments)
            .addOnSuccessListener { onResult(Result.success(Unit)) }
            .addOnFailureListener { onResult(Result.failure(it)) }
    }

    fun clearSlot(day: String, mealTime: String, onResult: (Result<Unit>) -> Unit) {
        val userId = auth.currentUser?.uid
        if (userId.isNullOrBlank()) {
            onResult(Result.failure(IllegalStateException("Inicia sesion para editar tu planeador.")))
            return
        }

        collection.document("${userId}_${day}_$mealTime")
            .delete()
            .addOnSuccessListener { onResult(Result.success(Unit)) }
            .addOnFailureListener { onResult(Result.failure(it)) }
    }
}
