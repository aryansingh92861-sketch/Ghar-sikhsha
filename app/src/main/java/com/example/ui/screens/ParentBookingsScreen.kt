package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Event
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Payment
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BookingRequest
import com.example.data.model.BookingStatus
import com.example.data.model.PaymentStatus
import com.example.ui.theme.*

@Composable
fun ParentBookingsScreen(
  bookings: List<BookingRequest>,
  onPayFee: (BookingRequest) -> Unit,
  onRateTutor: (BookingRequest) -> Unit,
  onTriggerSos: (String) -> Unit
) {
  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .padding(horizontal = 16.dp),
    contentPadding = PaddingValues(top = 12.dp, bottom = 90.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "My Home Tuitions & Classes",
            style = MaterialTheme.typography.titleLarge.copy(
              fontWeight = FontWeight.Bold,
              color = RoyalIndigo
            )
          )
          Text(
            text = "Track daily attendance, topics taught, and monthly fee protection",
            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
          )
        }
      }
    }

    if (bookings.isEmpty()) {
      item {
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          modifier = Modifier
            .fillMaxWidth()
            .padding(top = 40.dp)
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Icon(
              imageVector = Icons.Outlined.Event,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.onSurfaceVariant,
              modifier = Modifier.size(54.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
              text = "No Home Tuition Bookings Yet",
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
            Text(
              text = "Explore verified home tutors and request a free demo class for your child.",
              style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
            )
          }
        }
      }
    } else {
      items(bookings) { booking ->
        BookingCard(
          booking = booking,
          onPayFee = { onPayFee(booking) },
          onRateTutor = { onRateTutor(booking) },
          onSos = { onTriggerSos(booking.teacherName) }
        )
      }
    }
  }
}

@Composable
fun BookingCard(
  booking: BookingRequest,
  onPayFee: () -> Unit,
  onRateTutor: () -> Unit,
  onSos: () -> Unit
) {
  var showAttendanceHistory by remember { mutableStateOf(false) }

  Card(
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    modifier = Modifier
      .fillMaxWidth()
      .testTag("booking_card_${booking.id}")
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      // Top status row
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Surface(
          shape = RoundedCornerShape(6.dp),
          color = when (booking.status) {
            BookingStatus.ACTIVE -> GreenContainer
            BookingStatus.ACCEPTED -> SaffronContainer
            BookingStatus.REQUESTED -> IndigoContainer
            BookingStatus.REJECTED -> Color(0xFFFFEBEE)
            BookingStatus.COMPLETED -> MaterialTheme.colorScheme.surfaceVariant
          }
        ) {
          Text(
            text = "${booking.status.label} (${booking.status.hindiLabel})",
            style = MaterialTheme.typography.labelSmall.copy(
              fontWeight = FontWeight.Bold,
              color = when (booking.status) {
                BookingStatus.ACTIVE -> VerifiedGreen
                BookingStatus.ACCEPTED -> DarkSaffron
                BookingStatus.REQUESTED -> PrimaryIndigo
                BookingStatus.REJECTED -> ErrorRed
                BookingStatus.COMPLETED -> MaterialTheme.colorScheme.onSurfaceVariant
              }
            ),
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
          )
        }

        // Fee status pill
        Surface(
          shape = RoundedCornerShape(6.dp),
          color = if (booking.paymentStatus == PaymentStatus.PAID_SECURE) GreenContainer else Color(0xFFFFF3E0)
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              imageVector = if (booking.paymentStatus == PaymentStatus.PAID_SECURE) Icons.Default.Lock else Icons.Outlined.Payment,
              contentDescription = null,
              tint = if (booking.paymentStatus == PaymentStatus.PAID_SECURE) VerifiedGreen else DarkSaffron,
              modifier = Modifier.size(12.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = booking.paymentStatus.label,
              style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = if (booking.paymentStatus == PaymentStatus.PAID_SECURE) VerifiedGreen else DarkSaffron
              )
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Teacher & Student details
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
      ) {
        Column {
          Text(
            text = "Teacher: ${booking.teacherName}",
            style = MaterialTheme.typography.titleMedium.copy(
              fontWeight = FontWeight.Bold,
              color = RoyalIndigo
            )
          )
          Text(
            text = "Student: ${booking.childName} • ${booking.childClass} (${booking.board})",
            style = MaterialTheme.typography.bodySmall.copy(
              fontWeight = FontWeight.Medium
            )
          )
          Text(
            text = "Subjects: ${booking.subjects.joinToString(", ")}",
            style = MaterialTheme.typography.bodySmall.copy(
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          )
        }

        Column(horizontalAlignment = Alignment.End) {
          Text(
            text = "₹${booking.monthlyFee}/mo",
            style = MaterialTheme.typography.titleMedium.copy(
              fontWeight = FontWeight.ExtraBold,
              color = RoyalIndigo
            )
          )
          Text(
            text = booking.selectedPlan.title,
            style = MaterialTheme.typography.labelSmall.copy(
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              fontSize = 10.sp
            )
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Location & Timing
      Surface(
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(10.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = Icons.Default.LocationOn, contentDescription = null, tint = PrimaryIndigo, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "Home Address: ${booking.address}",
              style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium)
            )
          }
          Spacer(modifier = Modifier.height(2.dp))
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = Icons.Default.Schedule, contentDescription = null, tint = DarkSaffron, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "Timing Slot: ${booking.preferredTime}",
              style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Attendance Summary
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.FactCheck,
            contentDescription = null,
            tint = VerifiedGreen,
            modifier = Modifier.size(18.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "${booking.attendanceList.size} Home Classes Logged",
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold)
          )
        }

        TextButton(
          onClick = { showAttendanceHistory = !showAttendanceHistory },
          contentPadding = PaddingValues(horizontal = 6.dp, vertical = 0.dp)
        ) {
          Text(
            text = if (showAttendanceHistory) "Hide History" else "View Class Log",
            style = MaterialTheme.typography.labelMedium.copy(color = PrimaryIndigo, fontWeight = FontWeight.Bold)
          )
        }
      }

      // Expandable Attendance list
      if (showAttendanceHistory) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(GreenContainer.copy(alpha = 0.3f))
            .padding(10.dp),
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          if (booking.attendanceList.isEmpty()) {
            Text(
              text = "No classes logged yet. Classes will appear here once teacher checks in.",
              style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
            )
          } else {
            booking.attendanceList.forEach { att ->
              Surface(
                shape = RoundedCornerShape(6.dp),
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier.fillMaxWidth()
              ) {
                Column(modifier = Modifier.padding(8.dp)) {
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                  ) {
                    Text(
                      text = "${att.date} • ${att.time}",
                      style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                      text = "Verified OTP ✓",
                      style = MaterialTheme.typography.labelSmall.copy(color = VerifiedGreen, fontWeight = FontWeight.Bold)
                    )
                  }
                  Text(
                    text = "Topic Taught: ${att.topicCovered}",
                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurface)
                  )
                  Text(
                    text = "Homework: ${att.homeworkGiven}",
                    style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                  )
                }
              }
            }
          }
        }
        Spacer(modifier = Modifier.height(10.dp))
      }

      // Actions row: Pay Fee / Rate Tutor / SOS
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        if (booking.paymentStatus == PaymentStatus.PENDING) {
          Button(
            onClick = onPayFee,
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(containerColor = RoyalIndigo),
            modifier = Modifier
              .weight(1f)
              .testTag("pay_fee_button_${booking.id}")
          ) {
            Icon(imageVector = Icons.Default.Payment, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Pay Monthly Fee", fontWeight = FontWeight.Bold, fontSize = 12.sp)
          }
        } else {
          OutlinedButton(
            onClick = onRateTutor,
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
              .weight(1f)
              .testTag("rate_tutor_button_${booking.id}")
          ) {
            Icon(imageVector = Icons.Default.Star, contentDescription = null, tint = AmberSaffron, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Rate Teacher", fontSize = 12.sp)
          }
        }

        IconButton(
          onClick = onSos,
          modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFFFFEBEE))
            .testTag("parent_sos_call_button")
        ) {
          Icon(
            imageVector = Icons.Default.Emergency,
            contentDescription = "Safety Alert",
            tint = ErrorRed,
            modifier = Modifier.size(20.dp)
          )
        }
      }
    }
  }
}
