package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Teacher
import com.example.ui.theme.*

@Composable
fun TeacherCard(
  teacher: Teacher,
  onSelect: () -> Unit,
  onBookDemo: () -> Unit
) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp, vertical = 6.dp)
      .clickable { onSelect() }
      .testTag("teacher_card_${teacher.id}"),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surface
    ),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp)
    ) {
      // Top row: Avatar, Info, Rating
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
      ) {
        // Avatar circle
        Box(
          modifier = Modifier
            .size(54.dp)
            .clip(CircleShape)
            .background(
              if (teacher.gender == "Female") Color(0xFFFCE4EC) else IndigoContainer
            )
            .border(
              width = 1.5.dp,
              color = if (teacher.isPoliceVerified) VerifiedGreen else PrimaryIndigo,
              shape = CircleShape
            ),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = if (teacher.gender == "Female") Icons.Default.Face3 else Icons.Default.Person,
            contentDescription = teacher.name,
            tint = if (teacher.gender == "Female") Color(0xFFC2185B) else PrimaryIndigo,
            modifier = Modifier.size(32.dp)
          )
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Name, Qualification, Badges
        Column(modifier = Modifier.weight(1f)) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
          ) {
            Text(
              text = teacher.name,
              style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
              ),
              maxLines = 1,
              overflow = TextOverflow.Ellipsis
            )

            StarRatingBar(
              rating = teacher.rating,
              reviewCount = teacher.reviewCount
            )
          }

          Text(
            text = teacher.qualification,
            style = MaterialTheme.typography.bodySmall.copy(
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              fontWeight = FontWeight.Medium
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )

          Spacer(modifier = Modifier.height(4.dp))

          // Verification badges row
          Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            if (teacher.isIdVerified) {
              VerificationBadge(title = "ID Verified", icon = Icons.Default.CheckCircle)
            }
            if (teacher.isPoliceVerified) {
              VerificationBadge(title = "Police Cleared", icon = Icons.Outlined.Security)
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Experience, Radius, and Safety score bar
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(8.dp))
          .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
          .padding(horizontal = 10.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.WorkspacePremium,
            contentDescription = null,
            tint = AmberSaffron,
            modifier = Modifier.size(15.dp)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = "${teacher.experienceYears}+ Yrs Exp",
            style = MaterialTheme.typography.labelSmall.copy(
              fontWeight = FontWeight.SemiBold
            )
          )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Outlined.LocationOn,
            contentDescription = null,
            tint = PrimaryIndigo,
            modifier = Modifier.size(15.dp)
          )
          Spacer(modifier = Modifier.width(3.dp))
          Text(
            text = "Within ${teacher.radiusKm} km radius",
            style = MaterialTheme.typography.labelSmall.copy(
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.Shield,
            contentDescription = null,
            tint = VerifiedGreen,
            modifier = Modifier.size(14.dp)
          )
          Spacer(modifier = Modifier.width(3.dp))
          Text(
            text = "${teacher.safetyScore}% Safety Score",
            style = MaterialTheme.typography.labelSmall.copy(
              fontWeight = FontWeight.Bold,
              color = VerifiedGreen
            )
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Subjects taught chips
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        teacher.subjects.take(3).forEach { subject ->
          SuggestionChip(
            onClick = {},
            label = {
              Text(
                text = subject,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
              )
            },
            shape = RoundedCornerShape(8.dp),
            colors = SuggestionChipDefaults.suggestionChipColors(
              containerColor = IndigoContainer.copy(alpha = 0.6f),
              labelColor = PrimaryIndigo
            ),
            border = null,
            modifier = Modifier.height(26.dp)
          )
        }
        if (teacher.subjects.size > 3) {
          Text(
            text = "+${teacher.subjects.size - 3} more",
            style = MaterialTheme.typography.labelSmall.copy(
              color = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            modifier = Modifier.align(Alignment.CenterVertically)
          )
        }
      }

      Spacer(modifier = Modifier.height(12.dp))
      HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
      Spacer(modifier = Modifier.height(10.dp))

      // Bottom Row: Monthly Fee & Action buttons
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "Monthly Coaching Fee",
            style = MaterialTheme.typography.labelSmall.copy(
              fontSize = 10.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          )
          Row(verticalAlignment = Alignment.Bottom) {
            Text(
              text = "₹${teacher.baseMonthlyFee}",
              style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = RoyalIndigo
              )
            )
            Text(
              text = " / month",
              style = MaterialTheme.typography.labelSmall.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            )
          }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          OutlinedButton(
            onClick = onBookDemo,
            shape = RoundedCornerShape(10.dp),
            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
            colors = ButtonDefaults.outlinedButtonColors(
              contentColor = AmberSaffron
            ),
            border = ButtonDefaults.outlinedButtonBorder.copy(
              brush = androidx.compose.ui.graphics.SolidColor(AmberSaffron)
            ),
            modifier = Modifier
              .height(38.dp)
              .testTag("book_demo_button_${teacher.id}")
          ) {
            Text(
              text = "Free Demo",
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold
            )
          }

          Button(
            onClick = onSelect,
            shape = RoundedCornerShape(10.dp),
            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
            colors = ButtonDefaults.buttonColors(
              containerColor = RoyalIndigo
            ),
            modifier = Modifier
              .height(38.dp)
              .testTag("view_profile_button_${teacher.id}")
          ) {
            Text(
              text = "View Plans",
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold
            )
          }
        }
      }
    }
  }
}
