package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.*
import com.example.ui.components.*
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.PrimaryIndigo
import com.example.ui.theme.RoyalIndigo
import com.example.ui.viewmodel.*

class MainActivity : ComponentActivity() {
  private val viewModel: MainViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MyApplicationTheme {
        MainAppContent(viewModel = viewModel)
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppContent(viewModel: MainViewModel) {
  val currentRole by viewModel.currentRole.collectAsStateWithLifecycle()
  val childProfile by viewModel.childProfile.collectAsStateWithLifecycle()
  val selectedCity by viewModel.selectedCity.collectAsStateWithLifecycle()
  val filteredTeachers by viewModel.filteredTeachers.collectAsStateWithLifecycle()
  val bookings by viewModel.bookings.collectAsStateWithLifecycle()
  val teacherKycs by viewModel.teacherKycs.collectAsStateWithLifecycle()
  val subscription by viewModel.teacherSubscription.collectAsStateWithLifecycle()
  val books by viewModel.books.collectAsStateWithLifecycle()
  val complaints by viewModel.complaints.collectAsStateWithLifecycle()
  val filterState by viewModel.filterState.collectAsStateWithLifecycle()
  val parentTab by viewModel.parentTab.collectAsStateWithLifecycle()
  val teacherReviews by viewModel.teacherReviews.collectAsStateWithLifecycle()

  val selectedTeacher by viewModel.selectedTeacher.collectAsStateWithLifecycle()
  val snackbarMessage by viewModel.snackbarMessage.collectAsStateWithLifecycle()

  val snackbarHostState = remember { SnackbarHostState() }

  // Dialog states
  var showLoginScreen by remember { mutableStateOf(false) }
  var showChildProfileDialog by remember { mutableStateOf(false) }
  var showSafetyModal by remember { mutableStateOf(false) }
  var bookingForPayment by remember { mutableStateOf<BookingRequest?>(null) }
  var bookingForAttendance by remember { mutableStateOf<BookingRequest?>(null) }
  var bookingForReview by remember { mutableStateOf<BookingRequest?>(null) }

  LaunchedEffect(snackbarMessage) {
    snackbarMessage?.let { msg ->
      snackbarHostState.showSnackbar(msg)
      viewModel.clearSnackbar()
    }
  }

  if (showLoginScreen) {
    LoginScreen(
      currentRole = currentRole,
      onRoleSelected = { role -> viewModel.switchRole(role) },
      onLoginSuccess = { userName, userEmailOrPhone ->
        viewModel.updateChildProfile(childProfile.copy(name = if (currentRole == UserRole.PARENT) userName else childProfile.name))
        showLoginScreen = false
      },
      onSkipOrContinue = { showLoginScreen = false }
    )
    return
  }

  Scaffold(
    modifier = Modifier.fillMaxSize(),
    snackbarHost = { SnackbarHost(snackbarHostState) },
    topBar = {
      AppHeader(
        currentRole = currentRole,
        onRoleSelected = { role -> viewModel.switchRole(role) },
        selectedLocality = childProfile.locality,
        onLocalityClick = { showChildProfileDialog = true },
        onSafetyClick = { showSafetyModal = true },
        onLoginClick = { showLoginScreen = true }
      )
    },
    bottomBar = {
      when (currentRole) {
        UserRole.PARENT -> {
          NavigationBar(
            containerColor = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            modifier = Modifier.testTag("parent_bottom_nav")
          ) {
            NavigationBarItem(
              selected = parentTab == ParentTab.EXPLORE,
              onClick = { viewModel.setParentTab(ParentTab.EXPLORE) },
              icon = { Icon(Icons.Default.Search, contentDescription = "Find Tutors") },
              label = { Text("Find Tutors", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
              modifier = Modifier.testTag("parent_nav_explore")
            )
            NavigationBarItem(
              selected = parentTab == ParentTab.MY_TUITIONS,
              onClick = { viewModel.setParentTab(ParentTab.MY_TUITIONS) },
              icon = {
                BadgedBox(
                  badge = {
                    if (bookings.isNotEmpty()) {
                      Badge { Text("${bookings.size}") }
                    }
                  }
                ) {
                  Icon(Icons.Default.School, contentDescription = "My Tuitions")
                }
              },
              label = { Text("My Tuitions", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
              modifier = Modifier.testTag("parent_nav_bookings")
            )
            NavigationBarItem(
              selected = parentTab == ParentTab.BOOK_STORE,
              onClick = { viewModel.setParentTab(ParentTab.BOOK_STORE) },
              icon = { Icon(Icons.Default.MenuBook, contentDescription = "Books Store") },
              label = { Text("Books Store", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
              modifier = Modifier.testTag("parent_nav_store")
            )
            NavigationBarItem(
              selected = parentTab == ParentTab.CHILD_PROFILE,
              onClick = { showChildProfileDialog = true },
              icon = { Icon(Icons.Default.Person, contentDescription = "Child") },
              label = { Text("Child Profile", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
              modifier = Modifier.testTag("parent_nav_child")
            )
          }
        }
        UserRole.TEACHER -> {
          // Bottom bar for teacher
          NavigationBar(
            containerColor = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            modifier = Modifier.testTag("teacher_bottom_nav")
          ) {
            NavigationBarItem(
              selected = true,
              onClick = { },
              icon = { Icon(Icons.Default.Inbox, contentDescription = "Dashboard") },
              label = { Text("Home Tuition Hub", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
            )
            NavigationBarItem(
              selected = false,
              onClick = { viewModel.buySubscription() },
              icon = { Icon(Icons.Default.CardMembership, contentDescription = "Pass") },
              label = { Text("₹199 Pass", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
            )
          }
        }
        UserRole.ADMIN -> {
          // Bottom bar for admin
          NavigationBar(
            containerColor = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            modifier = Modifier.testTag("admin_bottom_nav")
          ) {
            NavigationBarItem(
              selected = true,
              onClick = { },
              icon = { Icon(Icons.Default.AdminPanelSettings, contentDescription = "Admin") },
              label = { Text("Admin Console", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
            )
          }
        }
      }
    }
  ) { paddingValues ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues)
    ) {
      when (currentRole) {
        UserRole.PARENT -> {
          when (parentTab) {
            ParentTab.EXPLORE -> {
              ParentExploreScreen(
                teachers = filteredTeachers,
                childProfile = childProfile,
                filterState = filterState,
                onSearchChange = { viewModel.updateSearchQuery(it) },
                onSubjectChange = { viewModel.updateSelectedSubject(it) },
                onClassChange = { viewModel.updateSelectedClass(it) },
                onToggleVerified = { viewModel.toggleVerifiedOnly(it) },
                onSelectTeacher = { viewModel.selectTeacher(it) },
                onBookDemo = { viewModel.selectTeacher(it) },
                onEditChildProfile = { showChildProfileDialog = true }
              )
            }
            ParentTab.MY_TUITIONS -> {
              ParentBookingsScreen(
                bookings = bookings,
                onPayFee = { bookingForPayment = it },
                onRateTutor = { bookingForReview = it },
                onTriggerSos = { tutorName -> viewModel.triggerEmergencySos(tutorName) }
              )
            }
            ParentTab.BOOK_STORE -> {
              BookStoreScreen(
                books = books,
                onBuyBook = { viewModel.buyBook(it) }
              )
            }
            ParentTab.CHILD_PROFILE -> {
              ParentExploreScreen(
                teachers = filteredTeachers,
                childProfile = childProfile,
                filterState = filterState,
                onSearchChange = { viewModel.updateSearchQuery(it) },
                onSubjectChange = { viewModel.updateSelectedSubject(it) },
                onClassChange = { viewModel.updateSelectedClass(it) },
                onToggleVerified = { viewModel.toggleVerifiedOnly(it) },
                onSelectTeacher = { viewModel.selectTeacher(it) },
                onBookDemo = { viewModel.selectTeacher(it) },
                onEditChildProfile = { showChildProfileDialog = true }
              )
            }
          }
        }

        UserRole.TEACHER -> {
          TeacherDashboardScreen(
            bookings = bookings,
            subscription = subscription,
            onAcceptRequest = { id -> viewModel.acceptBooking(id) },
            onDeclineRequest = { id -> viewModel.declineBooking(id) },
            onMarkAttendance = { b -> bookingForAttendance = b },
            onBuySubscription = { viewModel.buySubscription() }
          )
        }

        UserRole.ADMIN -> {
          AdminPanelScreen(
            kycList = teacherKycs,
            bookings = bookings,
            complaints = complaints,
            onApproveKyc = { id -> viewModel.adminVerifyKyc(id, approved = true) },
            onRejectKyc = { id -> viewModel.adminVerifyKyc(id, approved = false) }
          )
        }
      }
    }
  }

  // Teacher Profile & Booking Modal
  selectedTeacher?.let { teacher ->
    TeacherDetailModal(
      teacher = teacher,
      childProfile = childProfile,
      reviews = teacherReviews[teacher.id] ?: emptyList(),
      onDismiss = { viewModel.selectTeacher(null) },
      onConfirmBooking = { plan, timeSlot, notes ->
        viewModel.sendBooking(teacher, plan, timeSlot, notes)
      }
    )
  }

  // Monthly Tuition Fee Payment Modal
  bookingForPayment?.let { booking ->
    MonthlyPaymentModal(
      booking = booking,
      onDismiss = { bookingForPayment = null },
      onConfirmPayment = {
        viewModel.payTuitionFee(booking.id)
        bookingForPayment = null
      }
    )
  }

  // Teacher Mark Attendance Modal
  bookingForAttendance?.let { booking ->
    MarkAttendanceModal(
      booking = booking,
      onDismiss = { bookingForAttendance = null },
      onSubmit = { topic, homework, isPresent ->
        viewModel.markAttendance(booking.id, topic, homework, isPresent)
        bookingForAttendance = null
      }
    )
  }

  // Child Profile & Address Editor Dialog
  if (showChildProfileDialog) {
    ChildProfileDialog(
      currentProfile = childProfile,
      onDismiss = { showChildProfileDialog = false },
      onSave = { updated ->
        viewModel.updateChildProfile(updated)
        showChildProfileDialog = false
      }
    )
  }

  // Safety & Emergency SOS Modal
  if (showSafetyModal) {
    SafetySosModal(
      onDismiss = { showSafetyModal = false },
      onTriggerSos = {
        viewModel.triggerEmergencySos("Active Home Tutor")
        showSafetyModal = false
      }
    )
  }

  // Rate & Review Teacher Dialog
  bookingForReview?.let { booking ->
    ReviewDialog(
      teacherName = booking.teacherName,
      onDismiss = { bookingForReview = null },
      onSubmit = { rating, reviewText ->
        viewModel.submitReview(booking.teacherId, rating, reviewText)
        bookingForReview = null
      }
    )
  }
}
