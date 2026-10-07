package com.example.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.AmberSaffron
import com.example.ui.theme.RoyalIndigo

@Composable
fun ReviewDialog(
  teacherName: String,
  onDismiss: () -> Unit,
  onSubmit: (Double, String) -> Unit
) {
  var selectedRating by remember { mutableStateOf(5.0) }
  var reviewText by remember { mutableStateOf("") }

  Dialog(onDismissRequest = onDismiss) {
    Surface(
      shape = RoundedCornerShape(20.dp),
      color = MaterialTheme.colorScheme.surface,
      tonalElevation = 6.dp,
      modifier = Modifier
        .fillMaxWidth()
        .testTag("review_dialog")
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
            text = "Rate $teacherName",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
          )
          IconButton(onClick = onDismiss) {
            Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
          }
        }

        Spacer(modifier = Modifier.height(10.dp))
        Text(
          text = "How was the teacher's punctuality, concept clarity, and teaching quality during home offline classes?",
          style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Star rating bar
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.Center
        ) {
          (1..5).forEach { star ->
            Icon(
              imageVector = if (star <= selectedRating) Icons.Default.Star else Icons.Outlined.Star,
              contentDescription = "Star $star",
              tint = if (star <= selectedRating) AmberSaffron else Color.LightGray,
              modifier = Modifier
                .size(40.dp)
                .clickable { selectedRating = star.toDouble() }
                .padding(4.dp)
            )
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        OutlinedTextField(
          value = reviewText,
          onValueChange = { reviewText = it },
          label = { Text("Write your review for other parents") },
          placeholder = { Text("e.g., Very disciplined, patient with doubts, solved all NCERT exercises.") },
          modifier = Modifier
            .fillMaxWidth()
            .testTag("review_text_input"),
          maxLines = 3
        )

        Spacer(modifier = Modifier.height(18.dp))

        Button(
          onClick = {
            val text = if (reviewText.isBlank()) "Excellent home tutor. Highly recommended!" else reviewText
            onSubmit(selectedRating, text)
          },
          colors = ButtonDefaults.buttonColors(containerColor = RoyalIndigo),
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .testTag("submit_review_button")
        ) {
          Text("Submit Review", fontWeight = FontWeight.Bold)
        }
      }
    }
  }
}
