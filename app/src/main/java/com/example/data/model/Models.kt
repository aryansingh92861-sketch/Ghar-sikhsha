package com.example.data.model

enum class UserRole(val label: String, val hindiLabel: String) {
  PARENT("Parent / अभिभावक", "अभिभावक"),
  TEACHER("Teacher / शिक्षक", "शिक्षक"),
  ADMIN("Admin / व्यवस्थापक", "व्यवस्थापक")
}

data class ChildProfile(
  val id: String = "child_1",
  val name: String = "Aarav Sharma",
  val grade: String = "Class 9",
  val board: String = "CBSE",
  val subjects: List<String> = listOf("Mathematics", "Science"),
  val address: String = "Flat 402, Green Valley Apts, Sector 62",
  val locality: String = "Sector 62, Noida",
  val pincode: String = "201309"
)

data class CoachingPlan(
  val id: String,
  val title: String,
  val hindiTitle: String,
  val daysPerWeek: Int,
  val hoursPerSession: Double,
  val monthlyFee: Int,
  val description: String,
  val isPopular: Boolean = false
)

data class Teacher(
  val id: String,
  val name: String,
  val gender: String, // "Male", "Female"
  val qualification: String,
  val experienceYears: Int,
  val subjects: List<String>,
  val classes: List<String>,
  val localities: List<String>,
  val radiusKm: Int,
  val baseMonthlyFee: Int,
  val rating: Double,
  val reviewCount: Int,
  val isIdVerified: Boolean,
  val isPoliceVerified: Boolean,
  val isDegreeVerified: Boolean,
  val bio: String,
  val demoClassAvailable: Boolean = true,
  val phone: String,
  val totalStudentsTaught: Int,
  val plans: List<CoachingPlan>,
  val safetyScore: Int = 98 // out of 100
)

enum class BookingStatus(val label: String, val hindiLabel: String) {
  REQUESTED("Request Sent", "अनुरोध भेजा गया"),
  ACCEPTED("Accepted by Tutor", "स्वीकार किया गया"),
  REJECTED("Declined", "अस्वीकृत"),
  ACTIVE("Active Tuition", "सक्रिय कोचिंग"),
  COMPLETED("Completed", "पूर्ण")
}

enum class PaymentStatus(val label: String) {
  PENDING("Payment Pending"),
  PAID_SECURE("Paid (Protected Escrow)"),
  TRANSFERRED_TO_TEACHER("Transferred to Teacher")
}

data class AttendanceRecord(
  val id: String,
  val date: String,
  val time: String,
  val isPresent: Boolean,
  val topicCovered: String,
  val homeworkGiven: String,
  val verifiedByParent: Boolean,
  val otpCode: String = "4821"
)

data class BookingRequest(
  val id: String,
  val parentName: String,
  val parentPhone: String,
  val childName: String,
  val childClass: String,
  val board: String,
  val subjects: List<String>,
  val address: String,
  val locality: String,
  val teacherId: String,
  val teacherName: String,
  val selectedPlan: CoachingPlan,
  val preferredTime: String,
  val status: BookingStatus,
  val requestDate: String,
  val paymentStatus: PaymentStatus,
  val monthlyFee: Int,
  val attendanceList: List<AttendanceRecord> = emptyList(),
  val parentNotes: String = ""
)

data class TeacherKyc(
  val teacherId: String,
  val teacherName: String,
  val phone: String,
  val aadhaarNumberMasked: String,
  val degreeName: String,
  val university: String,
  val policeVerificationCertificateNumber: String,
  val status: KycStatus,
  val submittedDate: String
)

enum class KycStatus(val label: String) {
  PENDING("Pending Verification"),
  APPROVED("Approved & Verified"),
  REJECTED("Rejected")
}

data class TeacherSubscription(
  val id: String = "sub_teacher_1",
  val teacherId: String,
  val planName: String = "6-Month Unlimited Leads Pass",
  val fee: Int = 199,
  val validityMonths: Int = 6,
  val isActive: Boolean = true,
  val startDate: String = "01 Oct 2026",
  val expiryDate: String = "01 Apr 2027",
  val leadsReceived: Int = 18
)

data class BookProduct(
  val id: String,
  val title: String,
  val author: String,
  val standardClass: String,
  val subject: String,
  val price: Int,
  val originalPrice: Int,
  val discountPercent: Int,
  val description: String,
  val rating: Double,
  val inStock: Boolean = true
)

data class ComplaintReport(
  val id: String,
  val reporterRole: UserRole,
  val reporterName: String,
  val againstName: String,
  val reason: String,
  val description: String,
  val date: String,
  val status: String // "Open", "Resolved"
)

data class ReviewItem(
  val id: String,
  val parentName: String,
  val rating: Double,
  val comment: String,
  val date: String,
  val studentGrade: String
)
