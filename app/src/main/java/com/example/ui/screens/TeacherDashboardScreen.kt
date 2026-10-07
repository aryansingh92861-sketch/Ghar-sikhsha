package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.*
import com.example.ui.theme.*

@Composable
fun TeacherDashboardScreen(
  bookings: List<BookingRequest>,
  subscription: TeacherSubscription,
  onAcceptRequest: (String) -> Unit,
  onDeclineRequest: (String) -> Unit,
  onMarkAttendance: (BookingRequest) -> Unit,
  onBuySubscription: () -> Unit
) {
  val pendingRequests = bookings.filter { it.status == BookingStatus.REQUESTED }
  val activeStudents = bookings.filter { it.status == BookingStatus.ACTIVE || it.status == BookingStatus.ACCEPTED }

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .padding(horizontal = 16.dp),
    contentPadding = PaddingValues(top = 12.dp, bottom = 90.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    // Teacher Hero Card & KYC Status
    item {
      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column {
          Image(
            painter = painterResource(id = R.drawable.teacher_hero_1791270467136),
            contentDescription = "Teacher Verified Hero",
            contentScale = ContentScale.Crop,
            modifier = Modifier
              .fillMaxWidth()
              .height(115.dp)
          )
          Column(modifier = Modifier.padding(14.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                  modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(IndigoContainer),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Teacher",
                    tint = PrimaryIndigo,
                    modifier = Modifier.size(26.dp)
                  )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                  Text(
                    text = "Sunil Verma (M.Sc, B.Ed)",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                  )
                  Text(
                    text = "Sector 62, Noida • 6 km teaching radius",
                    style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                  )
                }
              }

              Surface(
                shape = RoundedCornerShape(8.dp),
                color = GreenContainer
              ) {
                Row(
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Icon(imageVector = Icons.Default.Verified, contentDescription = null, tint = VerifiedGreen, modifier = Modifier.size(14.dp))
                  Spacer(modifier = Modifier.width(4.dp))
                  Text(
                    text = "KYC Approved",
                    style = MaterialTheme.typography.labelSmall.copy(
                      fontWeight = FontWeight.Bold,
                      color = VerifiedGreen
                    )
                  )
                }
              }
            }
          }
        }
      }
    }

    // Business Model: 6-Month Subscription Card (₹199 / 6 Months Unlimited Lead Pass)
    item {
      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = RoyalIndigo),
        modifier = Modifier
          .fillMaxWidth()
          .testTag("teacher_subscription_card")
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                text = "Teacher Membership Pass",
                style = MaterialTheme.typography.labelSmall.copy(color = Color.White.copy(alpha = 0.8f))
              )
              Text(
                text = "₹199 / 6 Months Unlimited Leads",
                style = MaterialTheme.typography.titleMedium.copy(
                  fontWeight = FontWeight.Bold,
                  color = AmberSaffron
                )
              )
            }

            Surface(
              shape = RoundedCornerShape(6.dp),
              color = if (subscription.isActive) GreenContainer else Color(0xFFFFEBEE)
            ) {
              Text(
                text = if (subscription.isActive) "ACTIVE" else "EXPIRED",
                style = MaterialTheme.typography.labelSmall.copy(
                  fontWeight = FontWeight.Bold,
                  color = if (subscription.isActive) VerifiedGreen else ErrorRed
                ),
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
              )
            }
          }

          Spacer(modifier = Modifier.height(8.dp))
          Text(
            text = "Valid till ${subscription.expiryDate} • Received ${subscription.leadsReceived} parent booking leads this cycle.",
            style = MaterialTheme.typography.bodySmall.copy(color = Color.White.copy(alpha = 0.9f))
          )

          Spacer(modifier = Modifier.height(10.dp))
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "Get 100% direct home tuition bookings without commissions",
              style = MaterialTheme.typography.labelSmall.copy(color = Color.White.copy(alpha = 0.7f), fontSize = 10.sp),
              modifier = Modifier.weight(1f)
            )
            Button(
              onClick = onBuySubscription,
              colors = ButtonDefaults.buttonColors(containerColor = AmberSaffron),
              shape = RoundedCornerShape(8.dp),
              contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
              modifier = Modifier.testTag("renew_subscription_button")
            ) {
              Text("Renew Pass", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color.Black)
            }
          }
        }
      }
    }

    // Monthly Earnings & Overview
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        Surface(
          shape = RoundedCornerShape(12.dp),
          color = MaterialTheme.colorScheme.surface,
          tonalElevation = 2.dp,
          modifier = Modifier.weight(1f)
        ) {
          Column(modifier = Modifier.padding(12.dp)) {
            Text("Monthly Earnings", style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
            Text("₹9,600", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = RoyalIndigo))
            Text("Direct bank settlement", style = MaterialTheme.typography.labelSmall.copy(color = VerifiedGreen, fontSize = 10.sp))
          }
        }

        Surface(
          shape = RoundedCornerShape(12.dp),
          color = MaterialTheme.colorScheme.surface,
          tonalElevation = 2.dp,
          modifier = Modifier.weight(1f)
        ) {
          Column(modifier = Modifier.padding(12.dp)) {
            Text("Active Students", style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
            Text("${activeStudents.size} Enrolled", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = DarkSaffron))
            Text("4.9 ★ Rating (42 reviews)", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp))
          }
        }
      }
    }

    // Section: New Parent Booking Requests
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "New Parent Tuition Requests (${pendingRequests.size})",
          style = MaterialTheme.typography.titleMedium.copy(
            fontWeight = FontWeight.Bold,
            color = RoyalIndigo
          )
        )
      }
    }

    if (pendingRequests.isEmpty()) {
      item {
        Surface(
          shape = RoundedCornerShape(12.dp),
          color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
          modifier = Modifier.fillMaxWidth()
        ) {
          Text(
            text = "No pending requests. All student requests have been responded to.",
            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
            modifier = Modifier.padding(16.dp)
          )
        }
      }
    } else {
      items(pendingRequests) { req ->
        Card(
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("teacher_pending_request_${req.id}")
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Column {
                Text(
                  text = "Parent: ${req.parentName}",
                  style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                  text = "Student: ${req.childName} • ${req.childClass} (${req.board})",
                  style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                )
              }
              Text(
                text = "₹${req.monthlyFee}/mo",
                style = MaterialTheme.typography.titleMedium.copy(
                  fontWeight = FontWeight.Bold,
                  color = RoyalIndigo
                )
              )
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(
              text = "📍 Home: ${req.address}",
              style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium)
            )
            Text(
              text = "⏰ Preferred Slot: ${req.preferredTime}",
              style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
            )

            if (req.parentNotes.isNotBlank()) {
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = "Note: \"${req.parentNotes}\"",
                style = MaterialTheme.typography.bodySmall.copy(color = DarkSaffron)
              )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              OutlinedButton(
                onClick = { onDeclineRequest(req.id) },
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = ErrorRed),
                modifier = Modifier
                  .weight(1f)
                  .testTag("decline_request_button_${req.id}")
              ) {
                Icon(imageVector = Icons.Outlined.Close, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Decline")
              }

              Button(
                onClick = { onAcceptRequest(req.id) },
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = VerifiedGreen),
                modifier = Modifier
                  .weight(1f)
                  .testTag("accept_request_button_${req.id}")
              ) {
                Icon(imageVector = Icons.Outlined.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Accept Tuition")
              }
            }
          }
        }
      }
    }

    // Section: Active Students & Attendance Log
    item {
      Spacer(modifier = Modifier.height(8.dp))
      Text(
        text = "Active Students & Home Visit Log (${activeStudents.size})",
        style = MaterialTheme.typography.titleMedium.copy(
          fontWeight = FontWeight.Bold,
          color = RoyalIndigo
        )
      )
    }

    items(activeStudents) { student ->
      Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier
          .fillMaxWidth()
          .testTag("active_student_card_${student.id}")
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                text = student.childName,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
              )
              Text(
                text = "${student.childClass} (${student.board}) • ${student.selectedPlan.title}",
                style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
              )
            }

            Button(
              onClick = { onMarkAttendance(student) },
              colors = ButtonDefaults.buttonColors(containerColor = PrimaryIndigo),
              shape = RoundedCornerShape(8.dp),
              contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
              modifier = Modifier.testTag("mark_attendance_action_${student.id}")
            ) {
              Icon(imageVector = Icons.Default.FactCheck, contentDescription = null, modifier = Modifier.size(14.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text("Mark Class", fontSize = 12.sp)
            }
          }

          Spacer(modifier = Modifier.height(8.dp))
          Text(
            text = "Address: ${student.address}",
            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
          )

          // Attendance count badge
          Spacer(modifier = Modifier.height(6.dp))
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = Icons.Default.EventAvailable, contentDescription = null, tint = VerifiedGreen, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "${student.attendanceList.size} classes completed this month",
              style = MaterialTheme.typography.labelSmall.copy(color = VerifiedGreen, fontWeight = FontWeight.Bold)
            )
          }
        }
      }
    }
  }
}
