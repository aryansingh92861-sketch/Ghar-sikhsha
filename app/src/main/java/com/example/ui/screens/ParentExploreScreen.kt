package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.FilterList
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
import com.example.data.model.ChildProfile
import com.example.data.model.Teacher
import com.example.ui.components.TeacherCard
import com.example.ui.theme.*
import com.example.ui.viewmodel.FilterState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParentExploreScreen(
  teachers: List<Teacher>,
  childProfile: ChildProfile,
  filterState: FilterState,
  onSearchChange: (String) -> Unit,
  onSubjectChange: (String) -> Unit,
  onClassChange: (String) -> Unit,
  onToggleVerified: (Boolean) -> Unit,
  onSelectTeacher: (Teacher) -> Unit,
  onBookDemo: (Teacher) -> Unit,
  onEditChildProfile: () -> Unit
) {
  val subjects = listOf("All", "Mathematics", "Science", "English", "Coding", "Social Science", "Physics", "Chemistry")
  val classes = listOf("All", "Class 6", "Class 7", "Class 8", "Class 9", "Class 10", "Class 11", "Class 12")

  var showFilterSheet by remember { mutableStateOf(false) }

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background),
    contentPadding = PaddingValues(bottom = 80.dp)
  ) {
    // Hero Banner
    item {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 8.dp)
          .clip(RoundedCornerShape(16.dp))
          .background(RoyalIndigo)
      ) {
        Column {
          Image(
            painter = painterResource(id = R.drawable.home_tutoring_banner_1791270445432),
            contentDescription = "Home Coaching",
            contentScale = ContentScale.Crop,
            modifier = Modifier
              .fillMaxWidth()
              .height(130.dp)
          )
          Column(modifier = Modifier.padding(14.dp)) {
            Text(
              text = "Verified Home Offline Coaching",
              style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = Color.White
              )
            )
            Text(
              text = "Top teachers visit your home for 1-on-1 offline teaching. Police & degree verified.",
              style = MaterialTheme.typography.bodySmall.copy(
                color = Color.White.copy(alpha = 0.9f)
              )
            )
          }
        }
      }
    }

    // Active Child Profile Banner
    item {
      Surface(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 4.dp)
          .clip(RoundedCornerShape(12.dp))
          .clickable { onEditChildProfile() }
          .testTag("child_profile_banner"),
        color = SaffronContainer.copy(alpha = 0.7f),
        border = androidx.compose.foundation.BorderStroke(1.dp, AmberSaffron.copy(alpha = 0.5f))
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.School,
              contentDescription = null,
              tint = DarkSaffron,
              modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
              Text(
                text = "Child: ${childProfile.name} • ${childProfile.grade} (${childProfile.board})",
                style = MaterialTheme.typography.labelMedium.copy(
                  fontWeight = FontWeight.Bold,
                  color = DarkSaffron
                )
              )
              Text(
                text = "Home: ${childProfile.address.take(35)}...",
                style = MaterialTheme.typography.labelSmall.copy(
                  color = MaterialTheme.colorScheme.onSurfaceVariant,
                  fontSize = 10.sp
                )
              )
            }
          }
          Icon(
            imageVector = Icons.Default.Edit,
            contentDescription = "Edit Profile",
            tint = DarkSaffron,
            modifier = Modifier.size(16.dp)
          )
        }
      }
    }

    // Search Bar
    item {
      OutlinedTextField(
        value = filterState.searchQuery,
        onValueChange = onSearchChange,
        placeholder = { Text("Search by teacher name, subject, or locality...") },
        leadingIcon = {
          Icon(imageVector = Icons.Default.Search, contentDescription = "Search", tint = PrimaryIndigo)
        },
        trailingIcon = {
          IconButton(onClick = { showFilterSheet = !showFilterSheet }) {
            Icon(
              imageVector = Icons.Outlined.FilterList,
              contentDescription = "Filters",
              tint = if (filterState.verifiedOnly) VerifiedGreen else PrimaryIndigo
            )
          }
        },
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 8.dp)
          .testTag("parent_search_tutors_input"),
        singleLine = true
      )
    }

    // Subject Pills Row
    item {
      LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(vertical = 4.dp)
      ) {
        items(subjects) { subj ->
          val isSelected = filterState.selectedSubject == subj
          FilterChip(
            selected = isSelected,
            onClick = { onSubjectChange(subj) },
            label = { Text(subj, fontSize = 12.sp) },
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = RoyalIndigo,
              selectedLabelColor = Color.White
            ),
            modifier = Modifier.testTag("subject_chip_$subj")
          )
        }
      }
    }

    // Class Pills Row
    item {
      LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(vertical = 2.dp)
      ) {
        items(classes) { cls ->
          val isSelected = filterState.selectedClass == cls
          FilterChip(
            selected = isSelected,
            onClick = { onClassChange(cls) },
            label = { Text(cls, fontSize = 12.sp) },
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = AmberSaffron,
              selectedLabelColor = Color.White
            ),
            modifier = Modifier.testTag("class_chip_$cls")
          )
        }
      }
    }

    // Quick filter & results count
    item {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "${teachers.size} Verified Tutors Available",
          style = MaterialTheme.typography.titleSmall.copy(
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
          )
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = "Police Cleared Only",
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Switch(
            checked = filterState.verifiedOnly,
            onCheckedChange = onToggleVerified,
            modifier = Modifier.testTag("police_verified_filter_switch")
          )
        }
      }
    }

    // Empty state
    if (teachers.isEmpty()) {
      item {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(40.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Icon(
            imageVector = Icons.Default.SearchOff,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(54.dp)
          )
          Spacer(modifier = Modifier.height(10.dp))
          Text(
            text = "No tutors match the selected filters",
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
          )
          Text(
            text = "Try switching subject or class filter to view more home tutors.",
            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
          )
        }
      }
    } else {
      // List of teacher cards
      items(teachers) { teacher ->
        TeacherCard(
          teacher = teacher,
          onSelect = { onSelectTeacher(teacher) },
          onBookDemo = { onBookDemo(teacher) }
        )
      }
    }
  }
}
