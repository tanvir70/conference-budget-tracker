package com.tanvir.conferencebudget.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.tanvir.conferencebudget.data.model.CashTransaction
import com.tanvir.conferencebudget.data.model.Category
import com.tanvir.conferencebudget.data.model.Conference
import com.tanvir.conferencebudget.data.model.Expenditure
import com.tanvir.conferencebudget.data.model.Person
import com.tanvir.conferencebudget.data.model.SpendingEntry
import com.tanvir.conferencebudget.data.model.SubCategory
import com.tanvir.conferencebudget.data.model.User
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirestoreRepository {
    val firestore = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()

    // ---------------- AUTH & USERS ----------------

    fun getCurrentUserFlow(): Flow<User?> = callbackFlow {
        val firebaseUser = auth.currentUser
        if (firebaseUser == null) {
            trySend(null)
            close()
            return@callbackFlow
        }
        val uid = firebaseUser.uid
        val listener = firestore.collection("users").document(uid)
            .addSnapshotListener { snapshot, error ->
                if (snapshot != null && snapshot.exists()) {
                    var user = snapshot.toObject(User::class.java)
                    if (user != null && (user.email.equals("tanvirnis10@gmail.com", ignoreCase = true) || user.email.contains("admin", ignoreCase = true))) {
                        if (user.role != User.ROLE_FINANCIAL_SECRETARY) {
                            user = user.copy(role = User.ROLE_FINANCIAL_SECRETARY)
                            firestore.collection("users").document(uid).set(user)
                        }
                    }
                    trySend(user)
                } else {
                    val defaultRole = if (firebaseUser.email?.equals("tanvirnis10@gmail.com", ignoreCase = true) == true || 
                                          firebaseUser.email?.contains("admin", ignoreCase = true) == true) {
                        User.ROLE_FINANCIAL_SECRETARY
                    } else {
                        User.ROLE_FINANCIAL_SECRETARY
                    }
                    val user = User(
                        uid = uid,
                        name = firebaseUser.displayName ?: firebaseUser.email?.substringBefore("@") ?: "Admin User",
                        email = firebaseUser.email ?: "",
                        role = defaultRole,
                        avatarUrl = "avatar_1"
                    )
                    firestore.collection("users").document(uid).set(user)
                    trySend(user)
                }
            }
        awaitClose { listener.remove() }
    }

    suspend fun saveUserProfile(user: User) {
        firestore.collection("users").document(user.uid).set(user).await()
    }

    fun getAllUsers(): Flow<List<User>> = callbackFlow {
        val listener = firestore.collection("users")
            .addSnapshotListener { snapshot, error ->
                if (snapshot != null) {
                    val list = snapshot.documents.mapNotNull { it.toObject(User::class.java) }
                    trySend(list)
                } else {
                    trySend(emptyList())
                }
            }
        awaitClose { listener.remove() }
    }

    suspend fun updateUserRole(uid: String, newRole: String) {
        firestore.collection("users").document(uid).update("role", newRole).await()
    }

    suspend fun updateUserName(uid: String, newName: String) {
        firestore.collection("users").document(uid).update("name", newName).await()
    }

    suspend fun updateUserAvatar(uid: String, avatarUrl: String) {
        firestore.collection("users").document(uid).update("avatarUrl", avatarUrl).await()
    }

    // ---------------- CONFERENCES ----------------

    fun getConferences(): Flow<List<Conference>> = callbackFlow {
        val listener = firestore.collection("conferences")
            .addSnapshotListener { snapshot, error ->
                if (snapshot != null) {
                    val list = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(Conference::class.java)?.copy(id = doc.id)
                    }
                    trySend(list)
                } else {
                    trySend(emptyList())
                }
            }
        awaitClose { listener.remove() }
    }

    suspend fun addConference(conference: Conference): String {
        val docRef = firestore.collection("conferences").add(conference).await()
        return docRef.id
    }

    suspend fun deleteConference(conferenceId: String) {
        firestore.collection("conferences").document(conferenceId).delete().await()
    }

    // ---------------- CATEGORIES ----------------

    fun getCategories(conferenceId: String): Flow<List<Category>> = callbackFlow {
        val listener = firestore.collection("conferences").document(conferenceId)
            .collection("categories")
            .orderBy("order", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (snapshot != null) {
                    val list = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(Category::class.java)?.copy(id = doc.id)
                    }
                    trySend(list)
                } else {
                    trySend(emptyList())
                }
            }
        awaitClose { listener.remove() }
    }

    suspend fun addCategory(category: Category): String {
        val docRef = firestore.collection("conferences").document(category.conferenceId)
            .collection("categories").add(category).await()
        return docRef.id
    }

    suspend fun deleteCategory(conferenceId: String, categoryId: String) {
        firestore.collection("conferences").document(conferenceId)
            .collection("categories").document(categoryId).delete().await()
    }

    // ---------------- SUB-CATEGORIES ----------------

    fun getSubCategories(conferenceId: String): Flow<List<SubCategory>> = callbackFlow {
        val listener = firestore.collection("conferences").document(conferenceId)
            .collection("subCategories")
            .addSnapshotListener { snapshot, error ->
                if (snapshot != null) {
                    val list = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(SubCategory::class.java)?.copy(id = doc.id)
                    }
                    trySend(list)
                } else {
                    trySend(emptyList())
                }
            }
        awaitClose { listener.remove() }
    }

    suspend fun addSubCategory(subCategory: SubCategory): String {
        val docRef = firestore.collection("conferences").document(subCategory.conferenceId)
            .collection("subCategories").add(subCategory).await()
        return docRef.id
    }

    suspend fun updateSubCategory(subCategory: SubCategory) {
        firestore.collection("conferences").document(subCategory.conferenceId)
            .collection("subCategories").document(subCategory.id).set(subCategory).await()
    }

    suspend fun deleteSubCategory(conferenceId: String, subCategoryId: String) {
        firestore.collection("conferences").document(conferenceId)
            .collection("subCategories").document(subCategoryId).delete().await()
    }

    // ---------------- SPENDING ENTRIES ----------------

    fun getSpendingEntries(conferenceId: String): Flow<List<SpendingEntry>> = callbackFlow {
        val listener = firestore.collection("conferences").document(conferenceId)
            .collection("spendingEntries")
            .addSnapshotListener { snapshot, error ->
                if (snapshot != null) {
                    val list = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(SpendingEntry::class.java)?.copy(id = doc.id)
                    }
                    trySend(list)
                } else {
                    trySend(emptyList())
                }
            }
        awaitClose { listener.remove() }
    }

    suspend fun addSpendingEntry(entry: SpendingEntry): String {
        val docRef = firestore.collection("conferences").document(entry.conferenceId)
            .collection("spendingEntries").add(entry).await()
        return docRef.id
    }

    suspend fun deleteSpendingEntry(conferenceId: String, entryId: String) {
        firestore.collection("conferences").document(conferenceId)
            .collection("spendingEntries").document(entryId).delete().await()
    }

    // ---------------- PERSONS ----------------

    fun getPersons(conferenceId: String): Flow<List<Person>> = callbackFlow {
        val listener = firestore.collection("conferences").document(conferenceId)
            .collection("persons")
            .addSnapshotListener { snapshot, error ->
                if (snapshot != null) {
                    val list = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(Person::class.java)?.copy(id = doc.id)
                    }
                    trySend(list)
                } else {
                    trySend(emptyList())
                }
            }
        awaitClose { listener.remove() }
    }

    suspend fun addPerson(person: Person): String {
        val docRef = firestore.collection("conferences").document(person.conferenceId)
            .collection("persons").add(person).await()
        return docRef.id
    }

    // ---------------- CASH TRANSACTIONS ----------------

    fun getCashTransactions(conferenceId: String, personId: String): Flow<List<CashTransaction>> = callbackFlow {
        val listener = firestore.collection("conferences").document(conferenceId)
            .collection("persons").document(personId)
            .collection("cashTransactions")
            .addSnapshotListener { snapshot, error ->
                if (snapshot != null) {
                    val list = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(CashTransaction::class.java)?.copy(id = doc.id)
                    }
                    trySend(list)
                } else {
                    trySend(emptyList())
                }
            }
        awaitClose { listener.remove() }
    }

    suspend fun addCashTransaction(conferenceId: String, personId: String, tx: CashTransaction) {
        firestore.collection("conferences").document(conferenceId)
            .collection("persons").document(personId)
            .collection("cashTransactions").add(tx).await()
    }

    suspend fun deleteCashTransaction(conferenceId: String, personId: String, txId: String) {
        firestore.collection("conferences").document(conferenceId)
            .collection("persons").document(personId)
            .collection("cashTransactions").document(txId).delete().await()
    }

    // ---------------- EXPENDITURES ----------------

    fun getExpenditures(conferenceId: String, personId: String): Flow<List<Expenditure>> = callbackFlow {
        val listener = firestore.collection("conferences").document(conferenceId)
            .collection("persons").document(personId)
            .collection("expenditures")
            .addSnapshotListener { snapshot, error ->
                if (snapshot != null) {
                    val list = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(Expenditure::class.java)?.copy(id = doc.id)
                    }
                    trySend(list)
                } else {
                    trySend(emptyList())
                }
            }
        awaitClose { listener.remove() }
    }

    suspend fun addExpenditure(conferenceId: String, personId: String, exp: Expenditure) {
        firestore.collection("conferences").document(conferenceId)
            .collection("persons").document(personId)
            .collection("expenditures").add(exp).await()
    }

    suspend fun deleteExpenditure(conferenceId: String, personId: String, expId: String) {
        firestore.collection("conferences").document(conferenceId)
            .collection("persons").document(personId)
            .collection("expenditures").document(expId).delete().await()
    }
}
