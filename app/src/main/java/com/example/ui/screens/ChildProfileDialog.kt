package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.data.model.ChildProfile
import com.example.ui.theme.RoyalIndigo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChildProfileDialog(
  currentProfile: ChildProfile,
  onDismiss: () -> Unit,
  onSave: (ChildProfile) -> Unit
) {
  var name by remember { mutableStateOf(currentProfile.name) }
  var grade by remember { mutableStateOf(currentProfile.grade) }
  var board by remember { mutableStateOf(currentProfile.board) }
  var address by remember { mutableStateOf(currentProfile.address) }
  var locality by remember { mutableStateOf(currentProfile.locality) }

  val classes = listOf("Class 5", "Class 6", "Class 7", "Class 8", "Class 9", "Class 10", "Class 11", "Class 12")
  val boards = listOf("CBSE", "ICSE", "State Board", "IB / Cambridge")

  Dialog(onDismissRequest = onDismiss) {
    Surface(
      shape = RoundedCornerShape(20.dp),
      color = MaterialTheme.colorScheme.surface,
      tonalElevation = 6.dp,
      modifier = Modifier
        .fillMaxWidth()
        .testTag("child_profile_dialog")
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(20.dp)
          .verticalScroll(rememberScrollState())
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "Child Profile & Home Address",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
          )
          IconButton(onClick = onDismiss) {
            Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
          value = name,
          onValueChange = { name = it },
          label = { Text("Student / Child Name") },
          modifier = Modifier
            .fillMaxWidth()
            .testTag("child_name_input")
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text("Select Class", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold))
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          classes.take(4).forEach { cls ->
            FilterChip(
              selected = grade == cls,
              onClick = { grade = cls },
              label = { Text(cls) }
            )
          }
        }
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          classes.drop(4).forEach { cls ->
            FilterChip(
              selected = grade == cls,
              onClick = { grade = cls },
              label = { Text(cls) }
            )
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text("Education Board", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold))
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          boards.forEach { b ->
            FilterChip(
              selected = board == b,
              onClick = { board = b },
              label = { Text(b) }
            )
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
          value = address,
          onValueChange = { address = it },
          label = { Text("Complete Home Address (where tutor will visit)") },
          modifier = Modifier
            .fillMaxWidth()
            .testTag("home_address_input")
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
          value = locality,
          onValueChange = { locality = it },
          label = { Text("Locality / Sector") },
          modifier = Modifier
            .fillMaxWidth()
            .testTag("locality_input")
        )

        Spacer(modifier = Modifier.height(18.dp))

        Button(
          onClick = {
            onSave(
              currentProfile.copy(
                name = name,
                grade = grade,
                board = board,
                address = address,
                locality = locality
              )
            )
          },
          colors = ButtonDefaults.buttonColors(containerColor = RoyalIndigo),
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .testTag("save_child_profile_button")
        ) {
          Text("Save & Find Tutors", fontWeight = FontWeight.Bold)
        }
      }
    }
  }
}
