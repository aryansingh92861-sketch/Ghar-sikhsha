package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Close
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
import com.example.data.model.*
import com.example.ui.theme.*

@Composable
fun AdminPanelScreen(
  kycList: List<TeacherKyc>,
  bookings: List<BookingRequest>,
  complaints: List<ComplaintReport>,
  onApproveKyc: (String) -> Unit,
  onRejectKyc: (String) -> Unit
) {
  var selectedAdminTab by remember { mutableStateOf(0) } // 0: KYC Verification, 1: Bookings & Revenue, 2: Complaints & Safety

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .padding(horizontal = 16.dp),
    contentPadding = PaddingValues(top = 12.dp, bottom = 90.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    // Header & KPIs
    item {
      Column {
        Text(
          text = "GharShiksha Admin Command Center",
          style = MaterialTheme.typography.titleLarge.copy(
            fontWeight = FontWeight.Bold,
            color = RoyalIndigo
          )
        )
        Text(
          text = "Platform oversight, teacher KYC document approval & monetization analytics",
          style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
        )
      }
    }

    // Platform Revenue & Stats Cards
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        Card(
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(containerColor = IndigoContainer),
          modifier = Modifier.weight(1f)
        ) {
          Column(modifier = Modifier.padding(12.dp)) {
            Text("6-Mo Passes", style = MaterialTheme.typography.labelSmall.copy(color = PrimaryIndigo))
            Text("₹24,875", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = RoyalIndigo))
            Text("125 Tutors × ₹199", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp))
          }
        }

        Card(
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(containerColor = SaffronContainer),
          modifier = Modifier.weight(1f)
        ) {
          Column(modifier = Modifier.padding(12.dp)) {
            Text("Books Store Sales", style = MaterialTheme.typography.labelSmall.copy(color = DarkSaffron))
            Text("₹18,420", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = DarkSaffron))
            Text("NCERT & Question Banks", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp))
          }
        }

        Card(
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(containerColor = GreenContainer),
          modifier = Modifier.weight(1f)
        ) {
          Column(modifier = Modifier.padding(12.dp)) {
            Text("Verified Tutors", style = MaterialTheme.typography.labelSmall.copy(color = VerifiedGreen))
            Text("98.4%", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = VerifiedGreen))
            Text("100% Police Cleared", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp))
          }
        }
      }
    }

    // Admin Navigation Tabs
    item {
      TabRow(
        selectedTabIndex = selectedAdminTab,
        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        indicator = {}
      ) {
        Tab(
          selected = selectedAdminTab == 0,
          onClick = { selectedAdminTab = 0 },
          text = { Text("KYC Approvals (${kycList.filter { it.status == KycStatus.PENDING }.size})", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
        )
        Tab(
          selected = selectedAdminTab == 1,
          onClick = { selectedAdminTab = 1 },
          text = { Text("All Bookings (${bookings.size})", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
        )
        Tab(
          selected = selectedAdminTab == 2,
          onClick = { selectedAdminTab = 2 },
          text = { Text("Safety Desk (${complaints.size})", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
        )
      }
    }

    // Tab 0: KYC Documents Verification Desk
    if (selectedAdminTab == 0) {
      item {
        Text(
          text = "Teacher Identity & Police Verification Queue",
          style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
        )
        Text(
          text = "Review Aadhaar, university degrees, and police verification reports before home deployment.",
          style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
        )
      }

      items(kycList) { kyc ->
        Card(
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("kyc_item_${kyc.teacherId}")
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column {
                Text(
                  text = kyc.teacherName,
                  style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                  text = "Phone: ${kyc.phone} • Submitted: ${kyc.submittedDate}",
                  style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                )
              }

              Surface(
                shape = RoundedCornerShape(6.dp),
                color = when (kyc.status) {
                  KycStatus.APPROVED -> GreenContainer
                  KycStatus.PENDING -> SaffronContainer
                  KycStatus.REJECTED -> Color(0xFFFFEBEE)
                }
              ) {
                Text(
                  text = kyc.status.label,
                  style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = when (kyc.status) {
                      KycStatus.APPROVED -> VerifiedGreen
                      KycStatus.PENDING -> DarkSaffron
                      KycStatus.REJECTED -> ErrorRed
                    }
                  ),
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
              }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Document detail blocks
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
              modifier = Modifier.fillMaxWidth()
            ) {
              Column(modifier = Modifier.padding(10.dp)) {
                Text("🪪 Aadhaar: ${kyc.aadhaarNumberMasked} (Verified via UIDAI)", style = MaterialTheme.typography.bodySmall)
                Text("🎓 Degree: ${kyc.degreeName} - ${kyc.university}", style = MaterialTheme.typography.bodySmall)
                Text("🛡️ Police Clearance: ${kyc.policeVerificationCertificateNumber}", style = MaterialTheme.typography.bodySmall)
              }
            }

            if (kyc.status == KycStatus.PENDING) {
              Spacer(modifier = Modifier.height(10.dp))
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                OutlinedButton(
                  onClick = { onRejectKyc(kyc.teacherId) },
                  shape = RoundedCornerShape(8.dp),
                  colors = ButtonDefaults.outlinedButtonColors(contentColor = ErrorRed),
                  modifier = Modifier
                    .weight(1f)
                    .testTag("admin_reject_kyc_${kyc.teacherId}")
                ) {
                  Icon(imageVector = Icons.Outlined.Close, contentDescription = null, modifier = Modifier.size(16.dp))
                  Spacer(modifier = Modifier.width(4.dp))
                  Text("Reject")
                }

                Button(
                  onClick = { onApproveKyc(kyc.teacherId) },
                  shape = RoundedCornerShape(8.dp),
                  colors = ButtonDefaults.buttonColors(containerColor = VerifiedGreen),
                  modifier = Modifier
                    .weight(1f)
                    .testTag("admin_approve_kyc_${kyc.teacherId}")
                ) {
                  Icon(imageVector = Icons.Outlined.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                  Spacer(modifier = Modifier.width(4.dp))
                  Text("Verify & Approve")
                }
              }
            }
          }
        }
      }
    }

    // Tab 1: All Bookings across platform
    if (selectedAdminTab == 1) {
      items(bookings) { b ->
        Card(
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(12.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text(
                text = "${b.childName} ↔ ${b.teacherName}",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
              )
              Text(
                text = "₹${b.monthlyFee}/mo",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = RoyalIndigo)
              )
            }
            Text(
              text = "${b.childClass} (${b.board}) • Address: ${b.locality}",
              style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
            )
            Text(
              text = "Status: ${b.status.label} • Payment: ${b.paymentStatus.label}",
              style = MaterialTheme.typography.labelSmall.copy(color = VerifiedGreen)
            )
          }
        }
      }
    }

    // Tab 2: Safety & Complaints
    if (selectedAdminTab == 2) {
      if (complaints.isEmpty()) {
        item {
          Text("No active safety complaints reported.", style = MaterialTheme.typography.bodySmall)
        }
      } else {
        items(complaints) { c ->
          Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(modifier = Modifier.padding(12.dp)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Text(text = c.reason, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = ErrorRed))
                Text(text = c.status, style = MaterialTheme.typography.labelSmall.copy(color = VerifiedGreen))
              }
              Text(text = "Reported by: ${c.reporterName} against ${c.againstName}", style = MaterialTheme.typography.bodySmall)
              Text(text = c.description, style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
            }
          }
        }
      }
    }
  }
}
