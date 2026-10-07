package com.example.data.firestore

import android.util.Log
import com.example.data.model.*
import com.example.data.repository.AppRepository
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * DatabaseHelper singleton to manage Cloud Firestore CRUD operations
 * for tutor profiles and home offline coaching bookings.
 */
object DatabaseHelper {
  private const val TAG = "DatabaseHelper"
  private const val COLLECTION_TUTORS = "tutors"
  private const val COLLECTION_BOOKINGS = "bookings"

  // Lazy safe instance of FirebaseFirestore
  val firestore: FirebaseFirestore?
    get() = try {
      FirebaseFirestore.getInstance()
    } catch (e: Exception) {
      Log.w(TAG, "Firestore not initialized or offline: ${e.message}")
      null
    }

  // --- TUTOR PROFILE CRUD OPERATIONS ---

  /**
   * Create or update tutor profile in 'tutors' collection
   */
  suspend fun createOrUpdateTutor(teacher: Teacher): Result<Unit> {
    val db = firestore ?: return Result.failure(IllegalStateException("Firestore is not available"))
    return try {
      val data = teacherToMap(teacher)
      db.collection(COLLECTION_TUTORS).document(teacher.id).set(data, SetOptions.merge()).awaitTask()
      Result.success(Unit)
    } catch (e: Exception) {
      Log.e(TAG, "Error saving tutor ${teacher.id}: ${e.message}")
      Result.failure(e)
    }
  }

  /**
   * Fetch a single tutor profile by ID with real-time updates
   */
  fun getTutor(tutorId: String): Flow<Teacher?> = callbackFlow {
    val db = firestore
    if (db == null) {
      trySend(null)
      close()
      return@callbackFlow
    }

    val subscription = db.collection(COLLECTION_TUTORS).document(tutorId)
      .addSnapshotListener { snapshot, error ->
        if (error != null) {
          Log.e(TAG, "Error listening to tutor $tutorId", error)
          trySend(null)
          return@addSnapshotListener
        }
        val teacher = snapshot?.let { mapToTeacher(it) }
        trySend(teacher)
      }

    awaitClose { subscription.remove() }
  }

  /**
   * Fetch all verified/active tutors with real-time snapshot updates
   */
  fun getAllTutors(): Flow<List<Teacher>> = callbackFlow {
    val db = firestore
    if (db == null) {
      trySend(emptyList())
      close()
      return@callbackFlow
    }

    val subscription = db.collection(COLLECTION_TUTORS)
      .addSnapshotListener { snapshot, error ->
        if (error != null) {
          Log.e(TAG, "Error listening to tutors collection", error)
          trySend(emptyList())
          return@addSnapshotListener
        }
        val tutors = snapshot?.documents?.mapNotNull { mapToTeacher(it) } ?: emptyList()
        trySend(tutors)
      }

    awaitClose { subscription.remove() }
  }

  /**
   * Delete tutor profile
   */
  suspend fun deleteTutor(tutorId: String): Result<Unit> {
    val db = firestore ?: return Result.failure(IllegalStateException("Firestore is not available"))
    return try {
      db.collection(COLLECTION_TUTORS).document(tutorId).delete().awaitTask()
      Result.success(Unit)
    } catch (e: Exception) {
      Log.e(TAG, "Error deleting tutor $tutorId", e)
      Result.failure(e)
    }
  }

  /**
   * Update tutor KYC verification status
   */
  suspend fun updateTutorVerification(
    tutorId: String,
    isIdVerified: Boolean,
    isPoliceVerified: Boolean,
    isDegreeVerified: Boolean
  ): Result<Unit> {
    val db = firestore ?: return Result.failure(IllegalStateException("Firestore is not available"))
    return try {
      db.collection(COLLECTION_TUTORS).document(tutorId).update(
        mapOf(
          "isIdVerified" to isIdVerified,
          "isPoliceVerified" to isPoliceVerified,
          "isDegreeVerified" to isDegreeVerified
        )
      ).awaitTask()
      Result.success(Unit)
    } catch (e: Exception) {
      Log.e(TAG, "Error updating tutor verification $tutorId", e)
      Result.failure(e)
    }
  }

  // --- BOOKING CRUD OPERATIONS ---

  /**
   * Create a new home tuition booking request in 'bookings' collection
   */
  suspend fun createBooking(booking: BookingRequest): Result<String> {
    val db = firestore ?: return Result.failure(IllegalStateException("Firestore is not available"))
    return try {
      val data = bookingToMap(booking)
      val docRef = db.collection(COLLECTION_BOOKINGS).document(booking.id)
      docRef.set(data).awaitTask()
      Result.success(booking.id)
    } catch (e: Exception) {
      Log.e(TAG, "Error creating booking: ${e.message}")
      Result.failure(e)
    }
  }

  /**
   * Get single booking by ID with real-time updates
   */
  fun getBooking(bookingId: String): Flow<BookingRequest?> = callbackFlow {
    val db = firestore
    if (db == null) {
      trySend(null)
      close()
      return@callbackFlow
    }

    val subscription = db.collection(COLLECTION_BOOKINGS).document(bookingId)
      .addSnapshotListener { snapshot, error ->
        if (error != null) {
          Log.e(TAG, "Error listening to booking $bookingId", error)
          trySend(null)
          return@addSnapshotListener
        }
        val booking = snapshot?.let { mapToBooking(it) }
        trySend(booking)
      }

    awaitClose { subscription.remove() }
  }

  /**
   * Get all bookings across the platform (for Admin or monitor)
   */
  fun getAllBookings(): Flow<List<BookingRequest>> = callbackFlow {
    val db = firestore
    if (db == null) {
      trySend(emptyList())
      close()
      return@callbackFlow
    }

    val subscription = db.collection(COLLECTION_BOOKINGS)
      .addSnapshotListener { snapshot, error ->
        if (error != null) {
          Log.e(TAG, "Error listening to all bookings", error)
          trySend(emptyList())
          return@addSnapshotListener
        }
        val list = snapshot?.documents?.mapNotNull { mapToBooking(it) } ?: emptyList()
        trySend(list)
      }

    awaitClose { subscription.remove() }
  }

  /**
   * Get bookings filtered by teacher ID
   */
  fun getBookingsByTeacher(teacherId: String): Flow<List<BookingRequest>> = callbackFlow {
    val db = firestore
    if (db == null) {
      trySend(emptyList())
      close()
      return@callbackFlow
    }

    val subscription = db.collection(COLLECTION_BOOKINGS)
      .whereEqualTo("teacherId", teacherId)
      .addSnapshotListener { snapshot, error ->
        if (error != null) {
          Log.e(TAG, "Error listening to teacher bookings", error)
          trySend(emptyList())
          return@addSnapshotListener
        }
        val list = snapshot?.documents?.mapNotNull { mapToBooking(it) } ?: emptyList()
        trySend(list)
      }

    awaitClose { subscription.remove() }
  }

  /**
   * Update booking status (e.g. ACCEPTED, REJECTED, ACTIVE)
   */
  suspend fun updateBookingStatus(bookingId: String, status: BookingStatus): Result<Unit> {
    val db = firestore ?: return Result.failure(IllegalStateException("Firestore is not available"))
    return try {
      db.collection(COLLECTION_BOOKINGS).document(bookingId).update("status", status.name).awaitTask()
      Result.success(Unit)
    } catch (e: Exception) {
      Log.e(TAG, "Error updating booking status $bookingId: ${e.message}")
      Result.failure(e)
    }
  }

  /**
   * Update monthly fee payment status (e.g. PAID_SECURE)
   */
  suspend fun updatePaymentStatus(bookingId: String, paymentStatus: PaymentStatus): Result<Unit> {
    val db = firestore ?: return Result.failure(IllegalStateException("Firestore is not available"))
    return try {
      db.collection(COLLECTION_BOOKINGS).document(bookingId).update(
        mapOf(
          "paymentStatus" to paymentStatus.name,
          "status" to BookingStatus.ACTIVE.name
        )
      ).awaitTask()
      Result.success(Unit)
    } catch (e: Exception) {
      Log.e(TAG, "Error updating payment status: ${e.message}")
      Result.failure(e)
    }
  }

  /**
   * Append attendance record to booking
   */
  suspend fun addAttendanceRecord(bookingId: String, record: AttendanceRecord): Result<Unit> {
    val db = firestore ?: return Result.failure(IllegalStateException("Firestore is not available"))
    return try {
      val recordMap = mapOf(
        "id" to record.id,
        "date" to record.date,
        "time" to record.time,
        "isPresent" to record.isPresent,
        "topicCovered" to record.topicCovered,
        "homeworkGiven" to record.homeworkGiven,
        "verifiedByParent" to record.verifiedByParent,
        "otpCode" to record.otpCode
      )
      db.collection(COLLECTION_BOOKINGS).document(bookingId).update(
        "attendanceList", FieldValue.arrayUnion(recordMap)
      ).awaitTask()
      Result.success(Unit)
    } catch (e: Exception) {
      Log.e(TAG, "Error adding attendance: ${e.message}")
      Result.failure(e)
    }
  }

  /**
   * Delete booking
   */
  suspend fun deleteBooking(bookingId: String): Result<Unit> {
    val db = firestore ?: return Result.failure(IllegalStateException("Firestore is not available"))
    return try {
      db.collection(COLLECTION_BOOKINGS).document(bookingId).delete().awaitTask()
      Result.success(Unit)
    } catch (e: Exception) {
      Log.e(TAG, "Error deleting booking $bookingId", e)
      Result.failure(e)
    }
  }

  // --- MAPPING HELPERS ---

  private fun teacherToMap(t: Teacher): Map<String, Any?> = mapOf(
    "id" to t.id,
    "name" to t.name,
    "gender" to t.gender,
    "qualification" to t.qualification,
    "experienceYears" to t.experienceYears,
    "subjects" to t.subjects,
    "classes" to t.classes,
    "localities" to t.localities,
    "radiusKm" to t.radiusKm,
    "baseMonthlyFee" to t.baseMonthlyFee,
    "rating" to t.rating,
    "reviewCount" to t.reviewCount,
    "isIdVerified" to t.isIdVerified,
    "isPoliceVerified" to t.isPoliceVerified,
    "isDegreeVerified" to t.isDegreeVerified,
    "bio" to t.bio,
    "demoClassAvailable" to t.demoClassAvailable,
    "phone" to t.phone,
    "totalStudentsTaught" to t.totalStudentsTaught,
    "safetyScore" to t.safetyScore,
    "plans" to t.plans.map { p ->
      mapOf(
        "id" to p.id,
        "title" to p.title,
        "hindiTitle" to p.hindiTitle,
        "daysPerWeek" to p.daysPerWeek,
        "hoursPerSession" to p.hoursPerSession,
        "monthlyFee" to p.monthlyFee,
        "description" to p.description,
        "isPopular" to p.isPopular
      )
    }
  )

  @Suppress("UNCHECKED_CAST")
  private fun mapToTeacher(doc: DocumentSnapshot): Teacher? {
    if (!doc.exists()) return null
    return try {
      val data = doc.data ?: return null
      val plansData = data["plans"] as? List<Map<String, Any?>> ?: emptyList()
      val plans = plansData.map { p ->
        CoachingPlan(
          id = p["id"] as? String ?: "p1",
          title = p["title"] as? String ?: "Regular Plan",
          hindiTitle = p["hindiTitle"] as? String ?: "नियमित कक्षा",
          daysPerWeek = (p["daysPerWeek"] as? Number)?.toInt() ?: 5,
          hoursPerSession = (p["hoursPerSession"] as? Number)?.toDouble() ?: 1.0,
          monthlyFee = (p["monthlyFee"] as? Number)?.toInt() ?: 4800,
          description = p["description"] as? String ?: "",
          isPopular = p["isPopular"] as? Boolean ?: false
        )
      }

      Teacher(
        id = doc.id,
        name = data["name"] as? String ?: "Verified Tutor",
        gender = data["gender"] as? String ?: "Male",
        qualification = data["qualification"] as? String ?: "M.Sc, B.Ed",
        experienceYears = (data["experienceYears"] as? Number)?.toInt() ?: 5,
        subjects = data["subjects"] as? List<String> ?: listOf("Mathematics"),
        classes = data["classes"] as? List<String> ?: listOf("Class 9", "Class 10"),
        localities = data["localities"] as? List<String> ?: listOf("Sector 62"),
        radiusKm = (data["radiusKm"] as? Number)?.toInt() ?: 5,
        baseMonthlyFee = (data["baseMonthlyFee"] as? Number)?.toInt() ?: 4500,
        rating = (data["rating"] as? Number)?.toDouble() ?: 4.8,
        reviewCount = (data["reviewCount"] as? Number)?.toInt() ?: 10,
        isIdVerified = data["isIdVerified"] as? Boolean ?: true,
        isPoliceVerified = data["isPoliceVerified"] as? Boolean ?: true,
        isDegreeVerified = data["isDegreeVerified"] as? Boolean ?: true,
        bio = data["bio"] as? String ?: "Dedicated offline home tutor.",
        demoClassAvailable = data["demoClassAvailable"] as? Boolean ?: true,
        phone = data["phone"] as? String ?: "+91 98765 00000",
        totalStudentsTaught = (data["totalStudentsTaught"] as? Number)?.toInt() ?: 50,
        plans = if (plans.isNotEmpty()) plans else emptyList(),
        safetyScore = (data["safetyScore"] as? Number)?.toInt() ?: 98
      )
    } catch (e: Exception) {
      Log.e(TAG, "Failed to parse teacher doc ${doc.id}", e)
      null
    }
  }

  private fun bookingToMap(b: BookingRequest): Map<String, Any?> = mapOf(
    "id" to b.id,
    "parentName" to b.parentName,
    "parentPhone" to b.parentPhone,
    "childName" to b.childName,
    "childClass" to b.childClass,
    "board" to b.board,
    "subjects" to b.subjects,
    "address" to b.address,
    "locality" to b.locality,
    "teacherId" to b.teacherId,
    "teacherName" to b.teacherName,
    "preferredTime" to b.preferredTime,
    "status" to b.status.name,
    "requestDate" to b.requestDate,
    "paymentStatus" to b.paymentStatus.name,
    "monthlyFee" to b.monthlyFee,
    "parentNotes" to b.parentNotes,
    "plan" to mapOf(
      "id" to b.selectedPlan.id,
      "title" to b.selectedPlan.title,
      "hindiTitle" to b.selectedPlan.hindiTitle,
      "daysPerWeek" to b.selectedPlan.daysPerWeek,
      "hoursPerSession" to b.selectedPlan.hoursPerSession,
      "monthlyFee" to b.selectedPlan.monthlyFee,
      "description" to b.selectedPlan.description,
      "isPopular" to b.selectedPlan.isPopular
    ),
    "attendanceList" to b.attendanceList.map { a ->
      mapOf(
        "id" to a.id,
        "date" to a.date,
        "time" to a.time,
        "isPresent" to a.isPresent,
        "topicCovered" to a.topicCovered,
        "homeworkGiven" to a.homeworkGiven,
        "verifiedByParent" to a.verifiedByParent,
        "otpCode" to a.otpCode
      )
    }
  )

  @Suppress("UNCHECKED_CAST")
  private fun mapToBooking(doc: DocumentSnapshot): BookingRequest? {
    if (!doc.exists()) return null
    return try {
      val data = doc.data ?: return null
      val planData = data["plan"] as? Map<String, Any?>
      val plan = planData?.let {
        CoachingPlan(
          id = it["id"] as? String ?: "p1",
          title = it["title"] as? String ?: "Regular Plan",
          hindiTitle = it["hindiTitle"] as? String ?: "नियमित कक्षा",
          daysPerWeek = (it["daysPerWeek"] as? Number)?.toInt() ?: 5,
          hoursPerSession = (it["hoursPerSession"] as? Number)?.toDouble() ?: 1.0,
          monthlyFee = (it["monthlyFee"] as? Number)?.toInt() ?: 4800,
          description = it["description"] as? String ?: "",
          isPopular = it["isPopular"] as? Boolean ?: false
        )
      } ?: CoachingPlan("p1", "Regular Plan", "नियमित कक्षा", 5, 1.0, 4800, "")

      val attData = data["attendanceList"] as? List<Map<String, Any?>> ?: emptyList()
      val attendanceList = attData.map { a ->
        AttendanceRecord(
          id = a["id"] as? String ?: "att_1",
          date = a["date"] as? String ?: "",
          time = a["time"] as? String ?: "",
          isPresent = a["isPresent"] as? Boolean ?: true,
          topicCovered = a["topicCovered"] as? String ?: "",
          homeworkGiven = a["homeworkGiven"] as? String ?: "",
          verifiedByParent = a["verifiedByParent"] as? Boolean ?: true,
          otpCode = a["otpCode"] as? String ?: "4821"
        )
      }

      val statusStr = data["status"] as? String ?: BookingStatus.REQUESTED.name
      val bookingStatus = try { BookingStatus.valueOf(statusStr) } catch (_: Exception) { BookingStatus.REQUESTED }

      val payStr = data["paymentStatus"] as? String ?: PaymentStatus.PENDING.name
      val paymentStatus = try { PaymentStatus.valueOf(payStr) } catch (_: Exception) { PaymentStatus.PENDING }

      BookingRequest(
        id = doc.id,
        parentName = data["parentName"] as? String ?: "Parent",
        parentPhone = data["parentPhone"] as? String ?: "",
        childName = data["childName"] as? String ?: "Student",
        childClass = data["childClass"] as? String ?: "Class 9",
        board = data["board"] as? String ?: "CBSE",
        subjects = data["subjects"] as? List<String> ?: listOf("Mathematics"),
        address = data["address"] as? String ?: "",
        locality = data["locality"] as? String ?: "",
        teacherId = data["teacherId"] as? String ?: "",
        teacherName = data["teacherName"] as? String ?: "",
        selectedPlan = plan,
        preferredTime = data["preferredTime"] as? String ?: "",
        status = bookingStatus,
        requestDate = data["requestDate"] as? String ?: "",
        paymentStatus = paymentStatus,
        monthlyFee = (data["monthlyFee"] as? Number)?.toInt() ?: plan.monthlyFee,
        attendanceList = attendanceList,
        parentNotes = data["parentNotes"] as? String ?: ""
      )
    } catch (e: Exception) {
      Log.e(TAG, "Failed to parse booking doc ${doc.id}", e)
      null
    }
  }
}

/**
 * Extension helper to await Task with coroutine cancellation support
 */
suspend fun <T> Task<T>.awaitTask(): T = suspendCancellableCoroutine { cont ->
  addOnSuccessListener { result ->
    if (cont.isActive) cont.resume(result)
  }
  addOnFailureListener { exception ->
    if (cont.isActive) cont.resumeWithException(exception)
  }
  addOnCanceledListener {
    cont.cancel()
  }
}
