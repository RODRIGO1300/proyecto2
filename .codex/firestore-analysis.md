# Firestore Analysis

Collections used by the Android app:

- `Users/{uid}` from `AuthRepository.saveUserProfile()`.
- `Favoritos/{uid}_{idMeal}` from `FavoriteRepository`.
- `MealPlanner/{uid}_{dia}_{tiempo}` from `PlannerRepository`.

Queries used by the app:

- `Favoritos.whereEqualTo("idUsuario", userId)` for favorites and real-time favorite updates.
- `MealPlanner.whereEqualTo("idUsuario", userId)` for real-time weekly planner updates.

All Firestore data is user-owned and should require Firebase Authentication.
