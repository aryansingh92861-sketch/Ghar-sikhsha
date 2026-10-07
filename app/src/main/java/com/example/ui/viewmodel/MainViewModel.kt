package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.*
import com.example.data.repository.AppRepository
import kotlinx.coroutines.flow.*

enum class ParentTab(val title: String) {
  EXPLORE("Find Tutors"),
  MY_TUITIONS("My Bookings"),
  BOOK_STORE("Study Books"),
  CHILD_PROFILE("Child Profile")
}

enum class TeacherTab(val title: String) {
  REQUESTS("Requests"),
  SCHEDULE("Classes & Attendance"),
  SUBSCRIPTION("6-Mo Pass"),
  KYC_PROFILE("My KYC")
}

enum class AdminTab(val title: String) {
  DASHBOARD("Overview"),
  KYC_VERIFICATION("Tutor KYC"),
  TUITIONS("All Bookings"),
  COMPLAINTS("Safety & Disputes")
}

data class FilterState(
  val searchQuery: String = "",
  val selectedSubject: String = "All",
  val selectedClass: String = "All",
  val maxFee: Int = 8000,
  val verifiedOnly: Boolean = false,
  val genderFilter: String = "All",
  val sortBy: String = "Rating" // "Rating", "Fee: Low to High", "Experience"
)

class MainViewModel(
  private val repository: AppRepository = AppRepository()
) : ViewModel() {

  val currentRole: StateFlow<UserRole> = repository.currentRole
  val childProfile: StateFlow<ChildProfile> = repository.childProfile
  val selectedCity: StateFlow<String> = repository.selectedCity
  val teachers: StateFlow<List<Teacher>> = repository.teachers
  val bookings: StateFlow<List<BookingRequest>> = repository.bookings
  val teacherKycs: StateFlow<List<TeacherKyc>> = repository.teacherKycs
  val teacherSubscription: StateFlow<TeacherSubscription> = repository.teacherSubscription
  val books: StateFlow<List<BookProduct>> = repository.books
  val complaints: StateFlow<List<ComplaintReport>> = repository.complaints
  val teacherReviews: StateFlow<Map<String, List<ReviewItem>>> = repository.teacherReviews

  private val _parentTab = MutableStateFlow(ParentTab.EXPLORE)
  val parentTab: StateFlow<ParentTab> = _parentTab.asStateFlow()

  private val _teacherTab = MutableStateFlow(TeacherTab.REQUESTS)
  val teacherTab: StateFlow<TeacherTab> = _teacherTab.asStateFlow()

  private val _adminTab = MutableStateFlow(AdminTab.DASHBOARD)
  val adminTab: StateFlow<AdminTab> = _adminTab.asStateFlow()

  private val _filterState = MutableStateFlow(FilterState())
  val filterState: StateFlow<FilterState> = _filterState.asStateFlow()

  private val _selectedTeacher = MutableStateFlow<Teacher?>(null)
  val selectedTeacher: StateFlow<Teacher?> = _selectedTeacher.asStateFlow()

  private val _snackbarMessage = MutableStateFlow<String?>(null)
  val snackbarMessage: StateFlow<String?> = _snackbarMessage.asStateFlow()

  // Filtered teachers list computed via Flow
  val filteredTeachers: StateFlow<List<Teacher>> = combine(repository.teachers, _filterState) { list: List<Teacher>, filter: FilterState ->
    list.filter { teacher ->
      val matchesSearch = filter.searchQuery.isBlank() ||
        teacher.name.contains(filter.searchQuery, ignoreCase = true) ||
        teacher.subjects.any { it.contains(filter.searchQuery, ignoreCase = true) } ||
        teacher.localities.any { it.contains(filter.searchQuery, ignoreCase = true) }

      val matchesSubject = filter.selectedSubject == "All" ||
        teacher.subjects.contains(filter.selectedSubject) ||
        teacher.subjects.contains("All Subjects")

      val matchesClass = filter.selectedClass == "All" ||
        teacher.classes.contains(filter.selectedClass)

      val matchesFee = teacher.baseMonthlyFee <= filter.maxFee

      val matchesVerified = !filter.verifiedOnly || (teacher.isIdVerified && teacher.isDegreeVerified)

      val matchesGender = filter.genderFilter == "All" || teacher.gender == filter.genderFilter

      matchesSearch && matchesSubject && matchesClass && matchesFee && matchesVerified && matchesGender
    }.sortedWith { a, b ->
      when (filter.sortBy) {
        "Rating" -> b.rating.compareTo(a.rating)
        "Fee: Low to High" -> a.baseMonthlyFee.compareTo(b.baseMonthlyFee)
        "Experience" -> b.experienceYears.compareTo(a.experienceYears)
        else -> b.rating.compareTo(a.rating)
      }
    }
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  fun switchRole(role: UserRole) {
    repository.setRole(role)
    _snackbarMessage.value = "Switched to ${role.label}"
  }

  fun setParentTab(tab: ParentTab) {
    _parentTab.value = tab
  }

  fun setTeacherTab(tab: TeacherTab) {
    _teacherTab.value = tab
  }

  fun setAdminTab(tab: AdminTab) {
    _adminTab.value = tab
  }

  fun updateSearchQuery(query: String) {
    _filterState.value = _filterState.value.copy(searchQuery = query)
  }

  fun updateSelectedSubject(subject: String) {
    _filterState.value = _filterState.value.copy(selectedSubject = subject)
  }

  fun updateSelectedClass(grade: String) {
    _filterState.value = _filterState.value.copy(selectedClass = grade)
  }

  fun updateMaxFee(fee: Int) {
    _filterState.value = _filterState.value.copy(maxFee = fee)
  }

  fun toggleVerifiedOnly(verified: Boolean) {
    _filterState.value = _filterState.value.copy(verifiedOnly = verified)
  }

  fun updateGenderFilter(gender: String) {
    _filterState.value = _filterState.value.copy(genderFilter = gender)
  }

  fun updateSortBy(sort: String) {
    _filterState.value = _filterState.value.copy(sortBy = sort)
  }

  fun selectTeacher(teacher: Teacher?) {
    _selectedTeacher.value = teacher
  }

  fun updateChildProfile(profile: ChildProfile) {
    repository.updateChildProfile(profile)
    _snackbarMessage.value = "Child profile & home address updated!"
  }

  fun sendBooking(teacher: Teacher, plan: CoachingPlan, timeSlot: String, notes: String) {
    repository.sendBookingRequest(teacher, plan, timeSlot, notes)
    _selectedTeacher.value = null
    _parentTab.value = ParentTab.MY_TUITIONS
    _snackbarMessage.value = "Booking request sent to ${teacher.name}! Teacher will confirm shortly."
  }

  fun acceptBooking(bookingId: String) {
    repository.updateBookingStatus(bookingId, BookingStatus.ACCEPTED)
    _snackbarMessage.value = "Tuition request accepted! Parent notified."
  }

  fun declineBooking(bookingId: String) {
    repository.updateBookingStatus(bookingId, BookingStatus.REJECTED)
    _snackbarMessage.value = "Request declined."
  }

  fun payTuitionFee(bookingId: String) {
    repository.payMonthlyFee(bookingId)
    _snackbarMessage.value = "Monthly fee paid securely! Tuition is now active."
  }

  fun markAttendance(bookingId: String, topic: String, homework: String, isPresent: Boolean) {
    repository.addAttendance(bookingId, topic, homework, isPresent)
    _snackbarMessage.value = if (isPresent) "Attendance marked & Parent verified via OTP!" else "Marked as absent."
  }

  fun buySubscription() {
    repository.buyTeacherSubscription()
    _snackbarMessage.value = "6-Month Unlimited Leads Pass activated for ₹199! Valid for 180 days."
  }

  fun adminVerifyKyc(teacherId: String, approved: Boolean) {
    val status = if (approved) KycStatus.APPROVED else KycStatus.REJECTED
    repository.updateKycStatus(teacherId, status)
    _snackbarMessage.value = if (approved) "Teacher KYC Verified & Approved!" else "Teacher KYC Rejected."
  }

  fun submitReview(teacherId: String, rating: Double, comment: String) {
    repository.submitReview(teacherId, rating, comment)
    _snackbarMessage.value = "Thank you! Rating & review submitted."
  }

  fun buyBook(book: BookProduct) {
    _snackbarMessage.value = "Ordered '${book.title}' for ₹${book.price}! Delivered to home in 2 days."
  }

  fun fileComplaint(against: String, reason: String, desc: String) {
    repository.fileComplaint(against, reason, desc)
    _snackbarMessage.value = "Report submitted to Safety Desk. Our team is investigating."
  }

  fun triggerEmergencySos(teacherName: String) {
    _snackbarMessage.value = "EMERGENCY ALERT: Local helpline & parent emergency contacts alerted with live location!"
  }

  fun clearSnackbar() {
    _snackbarMessage.value = null
  }
}
