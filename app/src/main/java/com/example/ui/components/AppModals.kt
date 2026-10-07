package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Security
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
import androidx.compose.ui.window.Dialog
import com.example.data.model.*
import com.example.ui.theme.*

@Composable
fun TeacherDetailModal(
  teacher: Teacher,
  childProfile: ChildProfile,
  reviews: List<ReviewItem>,
  onDismiss: () -> Unit,
  onConfirmBooking: (CoachingPlan, String, String) -> Unit
) {
  var selectedPlan by remember { mutableStateOf(teacher.plans.firstOrNull() ?: teacher.plans[0]) }
  var selectedTimeSlot by remember { mutableStateOf("5:00 PM - 6:00 PM (Evening)") }
  var parentNotes by remember { mutableStateOf("") }
  var selectedTab by remember { mutableStateOf(0) } // 0: Booking & Plans, 1: Reviews & Qualifications

  val timeSlots = listOf(
    "3:30 PM - 4:30 PM (After School)",
    "5:00 PM - 6:00 PM (Evening)",
    "6:30 PM - 7:30 PM (Late Evening)",
    "8:00 AM - 9:00 AM (Morning)"
  )

  Dialog(onDismissRequest = onDismiss) {
    Surface(
      shape = RoundedCornerShape(20.dp),
      color = MaterialTheme.colorScheme.surface,
      tonalElevation = 6.dp,
      modifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight(0.9f)
        .testTag("teacher_detail_modal")
    ) {
      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(16.dp)
      ) {
        // Modal Header
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(46.dp)
                .clip(CircleShape)
                .background(IndigoContainer),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = if (teacher.gender == "Female") Icons.Default.Face3 else Icons.Default.Person,
                contentDescription = teacher.name,
                tint = PrimaryIndigo,
                modifier = Modifier.size(28.dp)
              )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = teacher.name,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
              )
              Text(
                text = teacher.qualification,
                style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
              )
            }
          }
          IconButton(onClick = onDismiss) {
            Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Tab Selector (Plans vs Credentials)
        TabRow(
          selectedTabIndex = selectedTab,
          containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
          indicator = {}
        ) {
          Tab(
            selected = selectedTab == 0,
            onClick = { selectedTab = 0 },
            text = { Text("Choose Plan & Book", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
          )
          Tab(
            selected = selectedTab == 1,
            onClick = { selectedTab = 1 },
            text = { Text("Reviews (${reviews.size}) & Trust", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
          )
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (selectedTab == 0) {
          // Booking & Plans Content
          LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            item {
              Text(
                text = "1. Select Monthly Home Coaching Plan",
                style = MaterialTheme.typography.titleSmall.copy(
                  fontWeight = FontWeight.Bold,
                  color = RoyalIndigo
                )
              )
            }

            items(teacher.plans) { plan ->
              val isPlanSelected = selectedPlan.id == plan.id
              Surface(
                shape = RoundedCornerShape(12.dp),
                color = if (isPlanSelected) IndigoContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                border = if (isPlanSelected) androidx.compose.foundation.BorderStroke(2.dp, PrimaryIndigo) else null,
                modifier = Modifier
                  .fillMaxWidth()
                  .clickable { selectedPlan = plan }
                  .testTag("plan_option_${plan.id}")
              ) {
                Column(modifier = Modifier.padding(12.dp)) {
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Column {
                      Text(
                        text = plan.title,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                      )
                      Text(
                        text = plan.hindiTitle,
                        style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                      )
                    }
                    Text(
                      text = "₹${plan.monthlyFee}/mo",
                      style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = RoyalIndigo
                      )
                    )
                  }
                  Spacer(modifier = Modifier.height(4.dp))
                  Text(
                    text = plan.description,
                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                  )
                }
              }
            }

            item {
              Spacer(modifier = Modifier.height(6.dp))
              Text(
                text = "2. Preferred Offline Home Visit Timing",
                style = MaterialTheme.typography.titleSmall.copy(
                  fontWeight = FontWeight.Bold,
                  color = RoyalIndigo
                )
              )
            }

            items(timeSlots) { slot ->
              val isSelected = selectedTimeSlot == slot
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .clip(RoundedCornerShape(8.dp))
                  .background(if (isSelected) SaffronContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                  .clickable { selectedTimeSlot = slot }
                  .padding(horizontal = 10.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                RadioButton(
                  selected = isSelected,
                  onClick = { selectedTimeSlot = slot },
                  colors = RadioButtonDefaults.colors(selectedColor = DarkSaffron)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = slot,
                  style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                  )
                )
              }
            }

            item {
              Spacer(modifier = Modifier.height(6.dp))
              Text(
                text = "3. Student & Home Address Details",
                style = MaterialTheme.typography.titleSmall.copy(
                  fontWeight = FontWeight.Bold,
                  color = RoyalIndigo
                )
              )

              Surface(
                shape = RoundedCornerShape(10.dp),
                color = GreenContainer.copy(alpha = 0.5f),
                modifier = Modifier.fillMaxWidth()
              ) {
                Column(modifier = Modifier.padding(10.dp)) {
                  Text(
                    text = "Student: ${childProfile.name} (${childProfile.grade} - ${childProfile.board})",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                  )
                  Text(
                    text = "Address: ${childProfile.address}, ${childProfile.locality}",
                    style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                  )
                }
              }
            }

            item {
              Spacer(modifier = Modifier.height(4.dp))
              OutlinedTextField(
                value = parentNotes,
                onValueChange = { parentNotes = it },
                label = { Text("Special requirements / child's weak areas") },
                placeholder = { Text("e.g., Needs special help in Geometry & Formula practice") },
                modifier = Modifier
                  .fillMaxWidth()
                  .testTag("parent_booking_notes_input"),
                maxLines = 2
              )
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          // Bottom Action
          Button(
            onClick = {
              onConfirmBooking(selectedPlan, selectedTimeSlot, parentNotes)
            },
            colors = ButtonDefaults.buttonColors(containerColor = RoyalIndigo),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
              .fillMaxWidth()
              .height(48.dp)
              .testTag("confirm_booking_request_button")
          ) {
            Icon(imageVector = Icons.Default.Send, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "Send Home Tuition Request (₹${selectedPlan.monthlyFee}/mo)",
              fontWeight = FontWeight.Bold,
              fontSize = 13.sp
            )
          }

        } else {
          // Reviews and Trust Credentials Content
          LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            item {
              // Trust Card
              Surface(
                shape = RoundedCornerShape(12.dp),
                color = GreenContainer,
                modifier = Modifier.fillMaxWidth()
              ) {
                Column(modifier = Modifier.padding(12.dp)) {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                      imageVector = Icons.Default.VerifiedUser,
                      contentDescription = null,
                      tint = VerifiedGreen,
                      modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                      text = "100% Background & Police Verified",
                      style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = VerifiedGreen
                      )
                    )
                  }
                  Spacer(modifier = Modifier.height(6.dp))
                  Text(
                    text = "• Government Aadhaar ID Authenticated\n• Degree & Educational Certificates Checked\n• Local Police Station Verification Certificate Active\n• 100% Offline Home Safety Protocol Checked",
                    style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF1B5E20))
                  )
                }
              }
            }

            item {
              Text(
                text = "About the Teacher",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
              )
              Text(
                text = teacher.bio,
                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
              )
            }

            item {
              Text(
                text = "Parent Ratings & Reviews",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
              )
            }

            if (reviews.isEmpty()) {
              item {
                Text(
                  text = "No reviews yet. Be the first to book a demo class!",
                  style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                )
              }
            } else {
              items(reviews) { rev ->
                Surface(
                  shape = RoundedCornerShape(10.dp),
                  color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                  modifier = Modifier.fillMaxWidth()
                ) {
                  Column(modifier = Modifier.padding(10.dp)) {
                    Row(
                      modifier = Modifier.fillMaxWidth(),
                      horizontalArrangement = Arrangement.SpaceBetween,
                      verticalAlignment = Alignment.CenterVertically
                    ) {
                      Text(
                        text = "${rev.parentName} (${rev.studentGrade})",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                      )
                      StarRatingBar(rating = rev.rating, reviewCount = 0)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                      text = rev.comment,
                      style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurface)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                      text = rev.date,
                      style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 9.sp)
                    )
                  }
                }
              }
            }
          }
        }
      }
    }
  }
}

@Composable
fun MonthlyPaymentModal(
  booking: BookingRequest,
  onDismiss: () -> Unit,
  onConfirmPayment: () -> Unit
) {
  Dialog(onDismissRequest = onDismiss) {
    Surface(
      shape = RoundedCornerShape(20.dp),
      color = MaterialTheme.colorScheme.surface,
      tonalElevation = 6.dp,
      modifier = Modifier
        .fillMaxWidth()
        .testTag("monthly_payment_modal")
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(20.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "Monthly Coaching Fee Payment",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
          )
          IconButton(onClick = onDismiss) {
            Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
          }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Student and Teacher detail
        Surface(
          shape = RoundedCornerShape(12.dp),
          color = IndigoContainer.copy(alpha = 0.4f),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(12.dp)) {
            Text(
              text = "Tutor: ${booking.teacherName}",
              style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = RoyalIndigo)
            )
            Text(
              text = "Student: ${booking.childName} • ${booking.selectedPlan.title}",
              style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
            )
            Text(
              text = "Home offline schedule: ${booking.preferredTime}",
              style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
            )
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Price breakdown
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Text("Monthly Tuition (1 Month)")
          Text("₹${booking.monthlyFee}", fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(6.dp))
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Text("Platform Safety & Escrow Fee")
          Text("₹0 (FREE Promo)", color = VerifiedGreen, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(8.dp))
        HorizontalDivider()
        Spacer(modifier = Modifier.height(8.dp))
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text("Total Payable", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
          Text(
            "₹${booking.monthlyFee}",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold, color = RoyalIndigo)
          )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Escrow guarantee badge
        Surface(
          shape = RoundedCornerShape(8.dp),
          color = GreenContainer,
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(imageVector = Icons.Default.Shield, contentDescription = null, tint = VerifiedGreen, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "Money-Back & Escrow Protected. Money is held securely by GharShiksha and disbursed after regular classes.",
              style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF1B5E20), fontSize = 11.sp)
            )
          }
        }

        Spacer(modifier = Modifier.height(18.dp))

        Button(
          onClick = onConfirmPayment,
          colors = ButtonDefaults.buttonColors(containerColor = RoyalIndigo),
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .testTag("pay_via_upi_button")
        ) {
          Icon(imageVector = Icons.Default.Payment, contentDescription = null, modifier = Modifier.size(18.dp))
          Spacer(modifier = Modifier.width(8.dp))
          Text("Pay ₹${booking.monthlyFee} via UPI / Card / NetBanking", fontWeight = FontWeight.Bold)
        }
      }
    }
  }
}

@Composable
fun MarkAttendanceModal(
  booking: BookingRequest,
  onDismiss: () -> Unit,
  onSubmit: (String, String, Boolean) -> Unit
) {
  var topicCovered by remember { mutableStateOf("") }
  var homeworkGiven by remember { mutableStateOf("") }
  var isPresent by remember { mutableStateOf(true) }

  Dialog(onDismissRequest = onDismiss) {
    Surface(
      shape = RoundedCornerShape(20.dp),
      color = MaterialTheme.colorScheme.surface,
      tonalElevation = 6.dp,
      modifier = Modifier
        .fillMaxWidth()
        .testTag("mark_attendance_modal")
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(20.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "Mark Today's Home Class",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
          )
          IconButton(onClick = onDismiss) {
            Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
          }
        }

        Text(
          text = "Student: ${booking.childName} • ${booking.locality}",
          style = MaterialTheme.typography.labelMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Status switch
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          OutlinedButton(
            onClick = { isPresent = true },
            colors = ButtonDefaults.outlinedButtonColors(
              containerColor = if (isPresent) GreenContainer else Color.Transparent
            ),
            modifier = Modifier.weight(1f)
          ) {
            Text("Present (कक्षा हुई)", color = if (isPresent) VerifiedGreen else MaterialTheme.colorScheme.onSurface)
          }

          OutlinedButton(
            onClick = { isPresent = false },
            colors = ButtonDefaults.outlinedButtonColors(
              containerColor = if (!isPresent) Color(0xFFFFEBEE) else Color.Transparent
            ),
            modifier = Modifier.weight(1f)
          ) {
            Text("Absent / Off", color = if (!isPresent) ErrorRed else MaterialTheme.colorScheme.onSurface)
          }
        }

        if (isPresent) {
          Spacer(modifier = Modifier.height(10.dp))
          OutlinedTextField(
            value = topicCovered,
            onValueChange = { topicCovered = it },
            label = { Text("Topic Covered Today") },
            placeholder = { Text("e.g., NCERT Ch 4 Quadratic Equations Ex 4.3") },
            modifier = Modifier
              .fillMaxWidth()
              .testTag("attendance_topic_input")
          )

          Spacer(modifier = Modifier.height(8.dp))
          OutlinedTextField(
            value = homeworkGiven,
            onValueChange = { homeworkGiven = it },
            label = { Text("Homework Given to Student") },
            placeholder = { Text("e.g., Solve Q1 to Q5 in register") },
            modifier = Modifier
              .fillMaxWidth()
              .testTag("attendance_homework_input")
          )

          Spacer(modifier = Modifier.height(10.dp))
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = IndigoContainer.copy(alpha = 0.5f),
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(
              modifier = Modifier.padding(8.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(imageVector = Icons.Default.VpnKey, contentDescription = null, tint = PrimaryIndigo, modifier = Modifier.size(18.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "Parent OTP Auto-Verification active for home attendance.",
                style = MaterialTheme.typography.labelSmall.copy(color = PrimaryIndigo)
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
          onClick = {
            val topic = if (topicCovered.isBlank()) "Standard Chapter Revision" else topicCovered
            val hw = if (homeworkGiven.isBlank()) "Revise class notes" else homeworkGiven
            onSubmit(topic, hw, isPresent)
          },
          colors = ButtonDefaults.buttonColors(containerColor = RoyalIndigo),
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .testTag("submit_attendance_button")
        ) {
          Text("Submit & Notify Parent", fontWeight = FontWeight.Bold)
        }
      }
    }
  }
}

@Composable
fun SafetySosModal(
  onDismiss: () -> Unit,
  onTriggerSos: () -> Unit
) {
  Dialog(onDismissRequest = onDismiss) {
    Surface(
      shape = RoundedCornerShape(20.dp),
      color = MaterialTheme.colorScheme.surface,
      tonalElevation = 6.dp,
      modifier = Modifier
        .fillMaxWidth()
        .testTag("safety_sos_modal")
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(20.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = Icons.Default.Shield, contentDescription = null, tint = VerifiedGreen, modifier = Modifier.size(28.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "सुरक्षा कवच (Safety First)",
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
          }
          IconButton(onClick = onDismiss) {
            Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
          }
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text(
          text = "GharShiksha Home Tuition Safety Guidelines:",
          style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
        )
        Text(
          text = "1. Every home tutor has undergone government ID verification & police clearance.\n2. Classes are monitored via daily in-app OTP check-in.\n3. In case of any dispute or safety concern during an offline visit, reach our 24/7 dedicated helpline.",
          style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // SOS Button
        Button(
          onClick = onTriggerSos,
          colors = ButtonDefaults.buttonColors(containerColor = ErrorRed),
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .testTag("trigger_sos_button")
        ) {
          Icon(imageVector = Icons.Default.Emergency, contentDescription = null, tint = Color.White)
          Spacer(modifier = Modifier.width(8.dp))
          Text("Emergency Alert / Call 112 Helpline", fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(8.dp))
        OutlinedButton(
          onClick = onDismiss,
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Text("Everything is Safe • Close")
        }
      }
    }
  }
}
