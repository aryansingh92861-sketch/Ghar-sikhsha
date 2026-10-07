package com.example.data.repository

import com.example.data.firestore.DatabaseHelper
import com.example.data.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class AppRepository {

  private val scope = CoroutineScope(Dispatchers.IO)

  private val _currentRole = MutableStateFlow(UserRole.PARENT)
  val currentRole: StateFlow<UserRole> = _currentRole.asStateFlow()

  private val _childProfile = MutableStateFlow(ChildProfile())
  val childProfile: StateFlow<ChildProfile> = _childProfile.asStateFlow()

  private val _selectedCity = MutableStateFlow("Noida & NCR")
  val selectedCity: StateFlow<String> = _selectedCity.asStateFlow()

  private val _teachers = MutableStateFlow<List<Teacher>>(getInitialTeachers())
  val teachers: StateFlow<List<Teacher>> = _teachers.asStateFlow()

  private val _bookings = MutableStateFlow<List<BookingRequest>>(getInitialBookings())
  val bookings: StateFlow<List<BookingRequest>> = _bookings.asStateFlow()

  private val _teacherKycs = MutableStateFlow<List<TeacherKyc>>(getInitialKycs())
  val teacherKycs: StateFlow<List<TeacherKyc>> = _teacherKycs.asStateFlow()

  private val _teacherSubscription = MutableStateFlow(
    TeacherSubscription(teacherId = "t1", isActive = true, leadsReceived = 14)
  )
  val teacherSubscription: StateFlow<TeacherSubscription> = _teacherSubscription.asStateFlow()

  private val _books = MutableStateFlow<List<BookProduct>>(getInitialBooks())
  val books: StateFlow<List<BookProduct>> = _books.asStateFlow()

  private val _complaints = MutableStateFlow<List<ComplaintReport>>(getInitialComplaints())
  val complaints: StateFlow<List<ComplaintReport>> = _complaints.asStateFlow()

  private val _teacherReviews = MutableStateFlow<Map<String, List<ReviewItem>>>(getInitialReviews())
  val teacherReviews: StateFlow<Map<String, List<ReviewItem>>> = _teacherReviews.asStateFlow()

  fun setRole(role: UserRole) {
    _currentRole.value = role
  }

  fun updateChildProfile(profile: ChildProfile) {
    _childProfile.value = profile
  }

  fun setSelectedCity(city: String) {
    _selectedCity.value = city
  }

  fun sendBookingRequest(
    teacher: Teacher,
    plan: CoachingPlan,
    preferredTime: String,
    parentNotes: String
  ): BookingRequest {
    val child = _childProfile.value
    val newBooking = BookingRequest(
      id = "book_" + System.currentTimeMillis().toString().takeLast(5),
      parentName = "Rajesh Sharma",
      parentPhone = "+91 98765 43210",
      childName = child.name,
      childClass = child.grade,
      board = child.board,
      subjects = child.subjects,
      address = child.address,
      locality = child.locality,
      teacherId = teacher.id,
      teacherName = teacher.name,
      selectedPlan = plan,
      preferredTime = preferredTime,
      status = BookingStatus.REQUESTED,
      requestDate = getTodayFormattedDate(),
      paymentStatus = PaymentStatus.PENDING,
      monthlyFee = plan.monthlyFee,
      parentNotes = parentNotes
    )
    _bookings.value = listOf(newBooking) + _bookings.value
    scope.launch {
      DatabaseHelper.createBooking(newBooking)
    }
    return newBooking
  }

  fun updateBookingStatus(bookingId: String, status: BookingStatus) {
    _bookings.value = _bookings.value.map {
      if (it.id == bookingId) it.copy(status = status) else it
    }
    scope.launch {
      DatabaseHelper.updateBookingStatus(bookingId, status)
    }
  }

  fun payMonthlyFee(bookingId: String) {
    _bookings.value = _bookings.value.map {
      if (it.id == bookingId) {
        it.copy(
          paymentStatus = PaymentStatus.PAID_SECURE,
          status = BookingStatus.ACTIVE
        )
      } else it
    }
    scope.launch {
      DatabaseHelper.updatePaymentStatus(bookingId, PaymentStatus.PAID_SECURE)
    }
  }

  fun addAttendance(
    bookingId: String,
    topicCovered: String,
    homeworkGiven: String,
    isPresent: Boolean
  ) {
    val record = AttendanceRecord(
      id = "att_" + System.currentTimeMillis().toString().takeLast(4),
      date = getTodayFormattedDate(),
      time = "5:00 PM - 6:00 PM",
      isPresent = isPresent,
      topicCovered = topicCovered,
      homeworkGiven = homeworkGiven,
      verifiedByParent = true
    )
    _bookings.value = _bookings.value.map {
      if (it.id == bookingId) {
        it.copy(attendanceList = listOf(record) + it.attendanceList)
      } else it
    }
    scope.launch {
      DatabaseHelper.addAttendanceRecord(bookingId, record)
    }
  }

  fun buyTeacherSubscription(): TeacherSubscription {
    val updated = _teacherSubscription.value.copy(
      isActive = true,
      startDate = getTodayFormattedDate(),
      expiryDate = "06 Apr 2027",
      leadsReceived = 0
    )
    _teacherSubscription.value = updated
    return updated
  }

  fun updateKycStatus(teacherId: String, newStatus: KycStatus) {
    _teacherKycs.value = _teacherKycs.value.map {
      if (it.teacherId == teacherId) it.copy(status = newStatus) else it
    }
    // Also reflect in teacher list
    _teachers.value = _teachers.value.map {
      if (it.id == teacherId) {
        val isApprove = newStatus == KycStatus.APPROVED
        it.copy(
          isIdVerified = isApprove,
          isDegreeVerified = isApprove,
          isPoliceVerified = isApprove
        )
      } else it
    }
  }

  fun submitReview(teacherId: String, rating: Double, comment: String) {
    val current = _teacherReviews.value[teacherId] ?: emptyList()
    val newReview = ReviewItem(
      id = "rev_" + System.currentTimeMillis().toString().takeLast(4),
      parentName = "Sunita Sharma",
      rating = rating,
      comment = comment,
      date = getTodayFormattedDate(),
      studentGrade = _childProfile.value.grade
    )
    val updatedMap = _teacherReviews.value.toMutableMap()
    updatedMap[teacherId] = listOf(newReview) + current
    _teacherReviews.value = updatedMap

    // update teacher rating score
    _teachers.value = _teachers.value.map {
      if (it.id == teacherId) {
        val newAvg = ((it.rating * it.reviewCount) + rating) / (it.reviewCount + 1)
        it.copy(
          rating = Math.round(newAvg * 10.0) / 10.0,
          reviewCount = it.reviewCount + 1
        )
      } else it
    }
  }

  fun fileComplaint(against: String, reason: String, description: String) {
    val report = ComplaintReport(
      id = "comp_" + System.currentTimeMillis().toString().takeLast(4),
      reporterRole = _currentRole.value,
      reporterName = if (_currentRole.value == UserRole.PARENT) "Rajesh Sharma" else "Sunil Verma",
      againstName = against,
      reason = reason,
      description = description,
      date = getTodayFormattedDate(),
      status = "Open"
    )
    _complaints.value = listOf(report) + _complaints.value
  }

  private fun getTodayFormattedDate(): String {
    val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    return sdf.format(Date())
  }

  companion object {
    private fun getInitialTeachers(): List<Teacher> {
      val defaultPlans = listOf(
        CoachingPlan(
          id = "p1",
          title = "Alternate Days (3 Days/Week)",
          hindiTitle = "सप्ताह में 3 दिन (1 घंटा/दिन)",
          daysPerWeek = 3,
          hoursPerSession = 1.0,
          monthlyFee = 3200,
          description = "Mon, Wed, Fri - Concept building + Doubt clearing",
          isPopular = false
        ),
        CoachingPlan(
          id = "p2",
          title = "Regular Focus (5 Days/Week)",
          hindiTitle = "सप्ताह में 5 दिन (1 घंटा/दिन)",
          daysPerWeek = 5,
          hoursPerSession = 1.0,
          monthlyFee = 4800,
          description = "Mon to Fri - Daily chapter completion, revision & weekly tests",
          isPopular = true
        ),
        CoachingPlan(
          id = "p3",
          title = "Mastery Intensive (6 Days/Week)",
          hindiTitle = "सप्ताह में 6 दिन (1.5 घंटा/दिन)",
          daysPerWeek = 6,
          hoursPerSession = 1.5,
          monthlyFee = 7200,
          description = "Comprehensive board prep, sample paper solving & doubt clearance",
          isPopular = false
        )
      )

      return listOf(
        Teacher(
          id = "t1",
          name = "Sunil Verma",
          gender = "Male",
          qualification = "M.Sc. (Physics), B.Ed (Delhi University)",
          experienceYears = 8,
          subjects = listOf("Mathematics", "Science", "Physics"),
          classes = listOf("Class 8", "Class 9", "Class 10", "Class 11"),
          localities = listOf("Sector 62", "Sector 63", "Indirapuram", "Noida Sector 50"),
          radiusKm = 6,
          baseMonthlyFee = 4800,
          rating = 4.9,
          reviewCount = 42,
          isIdVerified = true,
          isPoliceVerified = true,
          isDegreeVerified = true,
          bio = "8+ years of dedicated offline home tuition experience. Former coaching faculty specializing in NCERT clarity, CBSE board preparation, and personalized student pacing.",
          phone = "+91 98112 34567",
          totalStudentsTaught = 135,
          plans = defaultPlans,
          safetyScore = 99
        ),
        Teacher(
          id = "t2",
          name = "Pooja Mishra",
          gender = "Female",
          qualification = "M.Sc. (Mathematics), CTET Qualified",
          experienceYears = 6,
          subjects = listOf("Mathematics", "All Subjects", "English"),
          classes = listOf("Class 5", "Class 6", "Class 7", "Class 8", "Class 9"),
          localities = listOf("Sector 62", "Indirapuram", "Vasundhara", "Vaishali"),
          radiusKm = 5,
          baseMonthlyFee = 4200,
          rating = 4.8,
          reviewCount = 38,
          isIdVerified = true,
          isPoliceVerified = true,
          isDegreeVerified = true,
          bio = "Patient and caring female home tutor for primary and middle school students. Focus on strong foundational concepts, neat notebook work, and exam confidence.",
          phone = "+91 98223 45678",
          totalStudentsTaught = 95,
          plans = defaultPlans.map { it.copy(monthlyFee = (it.monthlyFee * 0.9).toInt()) },
          safetyScore = 100
        ),
        Teacher(
          id = "t3",
          name = "Dr. Ankit Srivastava",
          gender = "Male",
          qualification = "Ph.D (Chemistry), Ex-Kota Senior Faculty",
          experienceYears = 11,
          subjects = listOf("Chemistry", "Science", "Biology"),
          classes = listOf("Class 9", "Class 10", "Class 11", "Class 12", "NEET Foundation"),
          localities = listOf("Sector 62", "Sector 50", "Sector 76", "Noida Extension"),
          radiusKm = 8,
          baseMonthlyFee = 5500,
          rating = 4.9,
          reviewCount = 64,
          isIdVerified = true,
          isPoliceVerified = true,
          isDegreeVerified = true,
          bio = "Top-rated chemistry and science specialist for Class 9-12 and NEET foundation. In-depth conceptual clarity with visual models and systematic chapter tests.",
          phone = "+91 98334 56789",
          totalStudentsTaught = 210,
          plans = defaultPlans.map { it.copy(monthlyFee = (it.monthlyFee * 1.15).toInt()) },
          safetyScore = 98
        ),
        Teacher(
          id = "t4",
          name = "Neha Aggarwal",
          gender = "Female",
          qualification = "B.Tech (Computer Science), Certified Teacher",
          experienceYears = 5,
          subjects = listOf("Coding", "Mathematics", "English"),
          classes = listOf("Class 6", "Class 7", "Class 8", "Class 9", "Class 10"),
          localities = listOf("Sector 62", "Indirapuram", "Noida Sector 52"),
          radiusKm = 4,
          baseMonthlyFee = 3800,
          rating = 4.7,
          reviewCount = 29,
          isIdVerified = true,
          isPoliceVerified = false,
          isDegreeVerified = true,
          bio = "Friendly tutor making Maths & Python coding exciting for young learners through practical real-life examples and regular parent progress reports.",
          phone = "+91 98445 67890",
          totalStudentsTaught = 70,
          plans = defaultPlans.map { it.copy(monthlyFee = (it.monthlyFee * 0.85).toInt()) },
          safetyScore = 95
        ),
        Teacher(
          id = "t5",
          name = "Rameshwar Sharma",
          gender = "Male",
          qualification = "M.A. (English & History), B.Ed",
          experienceYears = 14,
          subjects = listOf("English", "Social Science", "Hindi", "All Subjects"),
          classes = listOf("Class 6", "Class 7", "Class 8", "Class 9", "Class 10"),
          localities = listOf("Sector 62", "Indirapuram", "Kaushambi"),
          radiusKm = 7,
          baseMonthlyFee = 4000,
          rating = 4.8,
          reviewCount = 51,
          isIdVerified = true,
          isPoliceVerified = true,
          isDegreeVerified = true,
          bio = "Veteran educator with 14 years teaching English grammar, composition, and Social Studies. Specializes in building student expression and scoring 90%+ in board exams.",
          phone = "+91 98556 78901",
          totalStudentsTaught = 260,
          plans = defaultPlans.map { it.copy(monthlyFee = (it.monthlyFee * 0.95).toInt()) },
          safetyScore = 99
        )
      )
    }

    private fun getInitialBookings(): List<BookingRequest> {
      val defaultPlans = getInitialTeachers()[0].plans
      return listOf(
        BookingRequest(
          id = "book_101",
          parentName = "Rajesh Sharma",
          parentPhone = "+91 98765 43210",
          childName = "Aarav Sharma",
          childClass = "Class 9",
          board = "CBSE",
          subjects = listOf("Mathematics", "Science"),
          address = "Flat 402, Green Valley Apts, Sector 62",
          locality = "Sector 62, Noida",
          teacherId = "t1",
          teacherName = "Sunil Verma",
          selectedPlan = defaultPlans[1], // 5 days / week
          preferredTime = "5:00 PM - 6:00 PM (Mon-Fri)",
          status = BookingStatus.ACTIVE,
          requestDate = "28 Sep 2026",
          paymentStatus = PaymentStatus.PAID_SECURE,
          monthlyFee = 4800,
          attendanceList = listOf(
            AttendanceRecord(
              id = "att_1",
              date = "05 Oct 2026",
              time = "5:00 PM - 6:00 PM",
              isPresent = true,
              topicCovered = "Quadratic Equations - NCERT Exercise 4.2 solving",
              homeworkGiven = "Solve Q5 to Q8 in notebook",
              verifiedByParent = true
            ),
            AttendanceRecord(
              id = "att_2",
              date = "04 Oct 2026",
              time = "5:00 PM - 6:00 PM",
              isPresent = true,
              topicCovered = "Physics - Gravitation, Universal Law derivation",
              homeworkGiven = "Revise Page 132 formulas",
              verifiedByParent = true
            ),
            AttendanceRecord(
              id = "att_3",
              date = "03 Oct 2026",
              time = "5:00 PM - 6:00 PM",
              isPresent = true,
              topicCovered = "Mathematics - Quadratic Equations Basics",
              homeworkGiven = "Read NCERT Example 1 to 4",
              verifiedByParent = true
            )
          ),
          parentNotes = "Please focus on building speed in mathematics calculations."
        ),
        BookingRequest(
          id = "book_102",
          parentName = "Meenakshi Gupta",
          parentPhone = "+91 98991 22334",
          childName = "Rhea Gupta",
          childClass = "Class 8",
          board = "ICSE",
          subjects = listOf("Mathematics"),
          address = "House 89, Judges Colony, Sector 62",
          locality = "Sector 62, Noida",
          teacherId = "t1",
          teacherName = "Sunil Verma",
          selectedPlan = defaultPlans[0], // 3 days / week
          preferredTime = "4:00 PM - 5:00 PM (Alternate)",
          status = BookingStatus.REQUESTED,
          requestDate = "05 Oct 2026",
          paymentStatus = PaymentStatus.PENDING,
          monthlyFee = 3200,
          parentNotes = "Looking for a demo class this Saturday."
        )
      )
    }

    private fun getInitialKycs(): List<TeacherKyc> {
      return listOf(
        TeacherKyc(
          teacherId = "t1",
          teacherName = "Sunil Verma",
          phone = "+91 98112 34567",
          aadhaarNumberMasked = "XXXX-XXXX-8921",
          degreeName = "M.Sc. Physics (DU)",
          university = "University of Delhi",
          policeVerificationCertificateNumber = "UP-POL-2026-90412",
          status = KycStatus.APPROVED,
          submittedDate = "20 Sep 2026"
        ),
        TeacherKyc(
          teacherId = "t2",
          teacherName = "Pooja Mishra",
          phone = "+91 98223 45678",
          aadhaarNumberMasked = "XXXX-XXXX-3419",
          degreeName = "M.Sc. Mathematics",
          university = "CCS University",
          policeVerificationCertificateNumber = "UP-POL-2026-77821",
          status = KycStatus.APPROVED,
          submittedDate = "22 Sep 2026"
        ),
        TeacherKyc(
          teacherId = "t4",
          teacherName = "Neha Aggarwal",
          phone = "+91 98445 67890",
          aadhaarNumberMasked = "XXXX-XXXX-6110",
          degreeName = "B.Tech Computer Science",
          university = "AKTU",
          policeVerificationCertificateNumber = "PENDING_VERIFICATION",
          status = KycStatus.PENDING,
          submittedDate = "04 Oct 2026"
        )
      )
    }

    private fun getInitialBooks(): List<BookProduct> {
      return listOf(
        BookProduct(
          id = "b1",
          title = "NCERT Class 9 All-in-One Science & Maths Combo",
          author = "NCERT Publications (Latest 2026-27 Edition)",
          standardClass = "Class 9",
          subject = "Maths & Science",
          price = 450,
          originalPrice = 600,
          discountPercent = 25,
          description = "Official textbook combo essential for home offline tuition. Includes solved exemplar problems.",
          rating = 4.9
        ),
        BookProduct(
          id = "b2",
          title = "CBSE Class 10 Chapterwise 10-Year Solved Papers",
          author = "Arihant Academic Experts",
          standardClass = "Class 10",
          subject = "All Subjects",
          price = 380,
          originalPrice = 520,
          discountPercent = 27,
          description = "Must-have question bank for Class 10 board exam home tuition practice.",
          rating = 4.8
        ),
        BookProduct(
          id = "b3",
          title = "Mastering Maths Formula Hand Book & Practice Sheets",
          author = "GharShiksha Academic Council",
          standardClass = "Class 8 to 10",
          subject = "Mathematics",
          price = 199,
          originalPrice = 300,
          discountPercent = 33,
          description = "Quick formula charts, mental maths tricks, and 20 offline home test sheets with answer keys.",
          rating = 4.9
        ),
        BookProduct(
          id = "b4",
          title = "NEET Foundation Chemistry & Biology Workbook",
          author = "Dr. Ankit Srivastava",
          standardClass = "Class 9 & 10",
          subject = "Science",
          price = 299,
          originalPrice = 450,
          discountPercent = 34,
          description = "Strengthen fundamentals for future medical competitive exams with detailed diagram worksheets.",
          rating = 4.7
        )
      )
    }

    private fun getInitialComplaints(): List<ComplaintReport> {
      return listOf(
        ComplaintReport(
          id = "c1",
          reporterRole = UserRole.PARENT,
          reporterName = "Manoj Saxena",
          againstName = "Rajesh Tutor (ID: t9)",
          reason = "Tutor arrived 30 mins late twice",
          description = "Teacher informed late about traffic delay. Requesting schedule adjustment.",
          date = "02 Oct 2026",
          status = "Resolved"
        )
      )
    }

    private fun getInitialReviews(): Map<String, List<ReviewItem>> {
      return mapOf(
        "t1" to listOf(
          ReviewItem(
            id = "r1",
            parentName = "Vikas Singhal",
            rating = 5.0,
            comment = "Sunil sir has been teaching my son Maths at our home for 4 months. He is punctual, explains derivations step-by-step, and our child's test scores jumped from 62% to 89%!",
            date = "22 Sep 2026",
            studentGrade = "Class 9 CBSE"
          ),
          ReviewItem(
            id = "r2",
            parentName = "Anita Gupta",
            rating = 4.8,
            comment = "Very sincere and polite teacher. He maintains daily attendance and shares weekly feedback with parents. Highly recommended for home offline tuition.",
            date = "15 Sep 2026",
            studentGrade = "Class 10 CBSE"
          )
        ),
        "t2" to listOf(
          ReviewItem(
            id = "r3",
            parentName = "Deepak Rawat",
            rating = 5.0,
            comment = "Pooja ma'am is extremely patient with our daughter in Class 7. She made Maths her favorite subject. Very safe and dependable teacher.",
            date = "29 Sep 2026",
            studentGrade = "Class 7 CBSE"
          )
        )
      )
    }
  }
}
