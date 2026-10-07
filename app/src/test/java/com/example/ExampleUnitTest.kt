package com.example

import com.example.data.firestore.DatabaseHelper
import com.example.data.model.BookingStatus
import com.example.data.model.PaymentStatus
import com.example.data.repository.AppRepository
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {
  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun testAppRepositoryInitialData() {
    val repo = AppRepository()
    val teachers = repo.teachers.value
    assertTrue("Should have initial teachers seeded", teachers.isNotEmpty())
    val firstTeacher = teachers.first()
    assertEquals("Sunil Verma", firstTeacher.name)
    assertTrue("Tutor should have coaching plans", firstTeacher.plans.isNotEmpty())
  }

  @Test
  fun testCreateAndPayBooking() {
    val repo = AppRepository()
    val teacher = repo.teachers.value.first()
    val plan = teacher.plans.first()

    val booking = repo.sendBookingRequest(teacher, plan, "5:00 PM - 6:00 PM", "Need maths help")
    assertNotNull(booking)
    assertEquals(BookingStatus.REQUESTED, booking.status)
    assertEquals(PaymentStatus.PENDING, booking.paymentStatus)

    repo.payMonthlyFee(booking.id)
    val updated = repo.bookings.value.firstOrNull { it.id == booking.id }
    assertNotNull(updated)
    assertEquals(PaymentStatus.PAID_SECURE, updated?.paymentStatus)
    assertEquals(BookingStatus.ACTIVE, updated?.status)
  }

  @Test
  fun testDatabaseHelperSingletonReference() {
    assertNotNull("DatabaseHelper object should be accessible", DatabaseHelper)
  }
}
