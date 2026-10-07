package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserRole
import com.example.ui.theme.*

@Composable
fun AppHeader(
  currentRole: UserRole,
  onRoleSelected: (UserRole) -> Unit,
  selectedLocality: String,
  onLocalityClick: () -> Unit,
  onSafetyClick: () -> Unit,
  onLoginClick: () -> Unit
) {
  Surface(
    color = MaterialTheme.colorScheme.surface,
    tonalElevation = 3.dp,
    shadowElevation = 2.dp
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .statusBarsPadding()
        .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        // App Logo & Locality
        Column {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(RoyalIndigo),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.School,
                contentDescription = "GharShiksha Logo",
                tint = Color.White,
                modifier = Modifier.size(22.dp)
              )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
              Text(
                text = "घर शिक्षा",
                style = MaterialTheme.typography.titleMedium.copy(
                  fontWeight = FontWeight.Bold,
                  color = RoyalIndigo
                )
              )
              Text(
                text = "GharShiksha • Home Offline Coaching",
                style = MaterialTheme.typography.labelSmall.copy(
                  fontSize = 10.sp,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              )
            }
          }

          Spacer(modifier = Modifier.height(4.dp))
          Row(
            modifier = Modifier
              .clip(RoundedCornerShape(6.dp))
              .background(IndigoContainer.copy(alpha = 0.5f))
              .clickable { onLocalityClick() }
              .padding(horizontal = 8.dp, vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              imageVector = Icons.Default.LocationOn,
              contentDescription = "Location",
              tint = PrimaryIndigo,
              modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = selectedLocality,
              style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.SemiBold,
                color = PrimaryIndigo
              )
            )
            Icon(
              imageVector = Icons.Default.ArrowDropDown,
              contentDescription = "Change",
              tint = PrimaryIndigo,
              modifier = Modifier.size(16.dp)
            )
          }
        }

        // Safety Shield & Login Buttons
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          IconButton(
            onClick = onSafetyClick,
            modifier = Modifier
              .clip(RoundedCornerShape(12.dp))
              .background(GreenContainer)
              .testTag("safety_shield_button")
          ) {
            Icon(
              imageVector = Icons.Default.VerifiedUser,
              contentDescription = "Suraksha Kavach",
              tint = VerifiedGreen,
              modifier = Modifier.size(24.dp)
            )
          }

          IconButton(
            onClick = onLoginClick,
            modifier = Modifier
              .clip(RoundedCornerShape(12.dp))
              .background(IndigoContainer)
              .testTag("header_login_button")
          ) {
            Icon(
              imageVector = Icons.Default.AccountCircle,
              contentDescription = "Login / Account",
              tint = RoyalIndigo,
              modifier = Modifier.size(24.dp)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Role Switcher Chips Bar (Parents, Teachers, Admin)
      Surface(
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(3.dp),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          UserRole.values().forEach { role ->
            val isSelected = currentRole == role
            Box(
              modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(10.dp))
                .background(
                  if (isSelected) {
                    when (role) {
                      UserRole.PARENT -> RoyalIndigo
                      UserRole.TEACHER -> AmberSaffron
                      UserRole.ADMIN -> Color(0xFF37474F)
                    }
                  } else Color.Transparent
                )
                .clickable { onRoleSelected(role) }
                .padding(vertical = 7.dp)
                .testTag("role_chip_${role.name}"),
              contentAlignment = Alignment.Center
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
              ) {
                Icon(
                  imageVector = when (role) {
                    UserRole.PARENT -> Icons.Default.FamilyRestroom
                    UserRole.TEACHER -> Icons.Default.Person
                    UserRole.ADMIN -> Icons.Default.AdminPanelSettings
                  },
                  contentDescription = role.label,
                  tint = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                  modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = when (role) {
                    UserRole.PARENT -> "Parent App"
                    UserRole.TEACHER -> "Teacher App"
                    UserRole.ADMIN -> "Admin Desk"
                  },
                  style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 11.sp
                  ),
                  maxLines = 1,
                  overflow = TextOverflow.Ellipsis
                )
              }
            }
          }
        }
      }
    }
  }
}

@Composable
fun VerificationBadge(
  title: String,
  icon: ImageVector = Icons.Default.CheckCircle,
  isVerified: Boolean = true
) {
  Row(
    modifier = Modifier
      .clip(RoundedCornerShape(6.dp))
      .background(if (isVerified) GreenContainer else Color(0xFFFFEBEE))
      .padding(horizontal = 6.dp, vertical = 2.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Icon(
      imageVector = if (isVerified) icon else Icons.Default.HourglassEmpty,
      contentDescription = title,
      tint = if (isVerified) VerifiedGreen else ErrorRed,
      modifier = Modifier.size(12.dp)
    )
    Spacer(modifier = Modifier.width(3.dp))
    Text(
      text = title,
      style = MaterialTheme.typography.labelSmall.copy(
        fontSize = 10.sp,
        fontWeight = FontWeight.SemiBold,
        color = if (isVerified) VerifiedGreen else ErrorRed
      )
    )
  }
}

@Composable
fun StarRatingBar(
  rating: Double,
  reviewCount: Int
) {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = Modifier
      .clip(RoundedCornerShape(6.dp))
      .background(Color(0xFFFFF8E1))
      .padding(horizontal = 6.dp, vertical = 2.dp)
  ) {
    Icon(
      imageVector = Icons.Default.Star,
      contentDescription = "Rating",
      tint = Color(0xFFFFB300),
      modifier = Modifier.size(14.dp)
    )
    Spacer(modifier = Modifier.width(3.dp))
    Text(
      text = String.format("%.1f", rating),
      style = MaterialTheme.typography.labelMedium.copy(
        fontWeight = FontWeight.Bold,
        color = Color(0xFFF57F17),
        fontSize = 12.sp
      )
    )
    if (reviewCount > 0) {
      Text(
        text = " ($reviewCount)",
        style = MaterialTheme.typography.labelSmall.copy(
          color = Color(0xFF9E9E9E),
          fontSize = 11.sp
        )
      )
    }
  }
}

@Composable
fun SectionHeader(
  title: String,
  hindiSubtitle: String? = null,
  actionText: String? = null,
  onActionClick: (() -> Unit)? = null
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp, vertical = 8.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Column {
      Text(
        text = title,
        style = MaterialTheme.typography.titleMedium.copy(
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onSurface
        )
      )
      if (hindiSubtitle != null) {
        Text(
          text = hindiSubtitle,
          style = MaterialTheme.typography.labelSmall.copy(
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        )
      }
    }
    if (actionText != null && onActionClick != null) {
      TextButton(
        onClick = onActionClick,
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
      ) {
        Text(
          text = actionText,
          style = MaterialTheme.typography.labelMedium.copy(
            fontWeight = FontWeight.SemiBold,
            color = PrimaryIndigo
          )
        )
      }
    }
  }
}
