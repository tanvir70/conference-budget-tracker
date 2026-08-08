package com.tanvir.conferencebudget.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
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
                            firestore.collection("users").document(uid).set(user, SetOptions.merge())
                        }
                    }
                    trySend(user)
                } else {
                    val defaultRole = if (firebaseUser.email?.equals("tanvirnis10@gmail.com", ignoreCase = true) == true || 
                                          firebaseUser.email?.contains("admin", ignoreCase = true) == true) {
                        User.ROLE_FINANCIAL_SECRETARY
                    } else {
                        User.ROLE_VOLUNTEER
                    }
                    val user = User(
                        uid = uid,
                        name = firebaseUser.displayName ?: firebaseUser.email?.substringBefore("@") ?: "User",
                        email = firebaseUser.email ?: "",
                        role = defaultRole,
                        avatarUrl = "avatar_1"
                    )
                    firestore.collection("users").document(uid).set(user, SetOptions.merge())
                    trySend(user)
                }
            }
        awaitClose { listener.remove() }
    }

    suspend fun saveUserProfile(user: User) {
        firestore.collection("users").document(user.uid).set(user, SetOptions.merge()).await()
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
        val data = mapOf("role" to newRole)
        firestore.collection("users").document(uid).set(data, SetOptions.merge()).await()
    }

    suspend fun updateUserName(uid: String, newName: String) {
        val data = mapOf("name" to newName)
        firestore.collection("users").document(uid).set(data, SetOptions.merge()).await()
    }

    suspend fun updateUserAvatar(uid: String, avatarUrl: String) {
        val data = mapOf("avatarUrl" to avatarUrl)
        firestore.collection("users").document(uid).set(data, SetOptions.merge()).await()
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
        val listener = firestore.collection("categories")
            .whereEqualTo("conferenceId", conferenceId)
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
        val docRef = firestore.collection("categories").add(category).await()
        return docRef.id
    }

    suspend fun deleteCategory(categoryId: String) {
        firestore.collection("categories").document(categoryId).delete().await()
    }

    suspend fun deleteCategory(conferenceId: String, categoryId: String) {
        deleteCategory(categoryId)
    }

    // ---------------- SUB-CATEGORIES ----------------

    fun getSubCategories(conferenceId: String): Flow<List<SubCategory>> = callbackFlow {
        val listener = firestore.collection("sub_categories")
            .whereEqualTo("conferenceId", conferenceId)
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
        val docRef = firestore.collection("sub_categories").add(subCategory).await()
        return docRef.id
    }

    suspend fun updateSubCategory(subCategory: SubCategory) {
        firestore.collection("sub_categories").document(subCategory.id).set(subCategory).await()
    }

    suspend fun deleteSubCategory(subCategoryId: String) {
        firestore.collection("sub_categories").document(subCategoryId).delete().await()
    }

    suspend fun deleteSubCategory(conferenceId: String, subCategoryId: String) {
        deleteSubCategory(subCategoryId)
    }

    // ---------------- SPENDING ENTRIES ----------------

    fun getSpendingEntries(conferenceId: String): Flow<List<SpendingEntry>> = callbackFlow {
        val listener = firestore.collection("spending_entries")
            .whereEqualTo("conferenceId", conferenceId)
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
        val docRef = firestore.collection("spending_entries").add(entry).await()
        return docRef.id
    }

    suspend fun deleteSpendingEntry(entryId: String) {
        firestore.collection("spending_entries").document(entryId).delete().await()
    }

    suspend fun deleteSpendingEntry(conferenceId: String, entryId: String) {
        deleteSpendingEntry(entryId)
    }

    // ---------------- PERSONS ----------------

    fun getPersons(conferenceId: String): Flow<List<Person>> = callbackFlow {
        val listener = firestore.collection("persons")
            .whereEqualTo("conferenceId", conferenceId)
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
        val docRef = firestore.collection("persons").add(person).await()
        return docRef.id
    }

    fun getPerson(personId: String): Flow<Person?> = callbackFlow {
        val listener = firestore.collection("persons").document(personId)
            .addSnapshotListener { snapshot, error ->
                if (snapshot != null && snapshot.exists()) {
                    trySend(snapshot.toObject(Person::class.java)?.copy(id = snapshot.id))
                } else {
                    trySend(null)
                }
            }
        awaitClose { listener.remove() }
    }

    // ---------------- CASH TRANSACTIONS ----------------

    fun getCashTransactions(personId: String): Flow<List<CashTransaction>> = callbackFlow {
        val listener = firestore.collection("cash_transactions")
            .whereEqualTo("personId", personId)
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

    fun getCashTransactions(conferenceId: String, personId: String): Flow<List<CashTransaction>> = getCashTransactions(personId)

    suspend fun addCashTransaction(transaction: CashTransaction): String {
        val docRef = firestore.collection("cash_transactions").add(transaction).await()
        return docRef.id
    }

    suspend fun addCashTransaction(conferenceId: String, personId: String, transaction: CashTransaction): String {
        return addCashTransaction(transaction.copy(conferenceId = conferenceId, personId = personId))
    }

    suspend fun deleteCashTransaction(transactionId: String) {
        firestore.collection("cash_transactions").document(transactionId).delete().await()
    }

    suspend fun deleteCashTransaction(conferenceId: String, personId: String, transactionId: String) {
        deleteCashTransaction(transactionId)
    }

    // ---------------- EXPENDITURES ----------------

    fun getExpenditures(personId: String): Flow<List<Expenditure>> = callbackFlow {
        val listener = firestore.collection("expenditures")
            .whereEqualTo("personId", personId)
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

    fun getExpenditures(conferenceId: String, personId: String): Flow<List<Expenditure>> = getExpenditures(personId)

    suspend fun addExpenditure(expenditure: Expenditure): String {
        val docRef = firestore.collection("expenditures").add(expenditure).await()
        return docRef.id
    }

    suspend fun addExpenditure(conferenceId: String, personId: String, expenditure: Expenditure): String {
        return addExpenditure(expenditure.copy(conferenceId = conferenceId, personId = personId))
    }

    suspend fun deleteExpenditure(expenditureId: String) {
        firestore.collection("expenditures").document(expenditureId).delete().await()
    }

    suspend fun deleteExpenditure(conferenceId: String, personId: String, expenditureId: String) {
        deleteExpenditure(expenditureId)
    }
}
