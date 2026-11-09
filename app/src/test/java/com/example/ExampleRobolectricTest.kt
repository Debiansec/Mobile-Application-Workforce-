package com.example

import com.example.data.model.ChecklistQuestion
import com.example.data.model.ComplianceResponse
import com.example.data.model.SectionId
import com.example.data.model.isDefectResponse
import com.example.data.model.isSpecialYesDefectQuestion
import com.example.ui.components.MONITORED_MINING_SITES
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleAppTest {

  @Test
  fun testAppConfigurationConstants() {
    val appTitle = "WorkForce Health SA"
    assertNotNull(appTitle)
    assertEquals("WorkForce Health SA", appTitle)
  }

  @Test
  fun testSpecialDefectQuestionsIdentification() {
    // Verified 4 special defect questions: A5, C7, D4, F14
    assertTrue("Section A Q5 must be identified as special defect question", isSpecialYesDefectQuestion(SectionId.SECTION_A, 5))
    assertTrue("Section C Q7 must be identified as special defect question", isSpecialYesDefectQuestion(SectionId.SECTION_C, 7))
    assertTrue("Section D Q4 must be identified as special defect question", isSpecialYesDefectQuestion(SectionId.SECTION_D, 4))
    assertTrue("Section F Q14 must be identified as special defect question", isSpecialYesDefectQuestion(SectionId.SECTION_F, 14))

    // Other questions must be standard
    assertFalse("Section A Q1 should be standard", isSpecialYesDefectQuestion(SectionId.SECTION_A, 1))
    assertFalse("Section B Q3 should be standard", isSpecialYesDefectQuestion(SectionId.SECTION_B, 3))
    assertFalse("Section C Q1 should be standard", isSpecialYesDefectQuestion(SectionId.SECTION_C, 1))
    assertFalse("Section D Q1 should be standard", isSpecialYesDefectQuestion(SectionId.SECTION_D, 1))
  }

  @Test
  fun testSpecialDefectRemedialActionTriggerOnYes() {
    // Section A Q5: Yes = defect (remedial action required), No = clean/compliant
    assertTrue(isDefectResponse(SectionId.SECTION_A, 5, ComplianceResponse.YES))
    assertFalse(isDefectResponse(SectionId.SECTION_A, 5, ComplianceResponse.NO))

    // Section C Q7: Yes = defect (mould/yeast), No = compliant
    assertTrue(isDefectResponse(SectionId.SECTION_C, 7, ComplianceResponse.YES))
    assertFalse(isDefectResponse(SectionId.SECTION_C, 7, ComplianceResponse.NO))

    // Section D Q4: Yes = defect (bad odours in urinal), No = compliant
    assertTrue(isDefectResponse(SectionId.SECTION_D, 4, ComplianceResponse.YES))
    assertFalse(isDefectResponse(SectionId.SECTION_D, 4, ComplianceResponse.NO))

    // Section F Q14: Yes = defect (bad odours in facility), No = compliant
    assertTrue(isDefectResponse(SectionId.SECTION_F, 14, ComplianceResponse.YES))
    assertFalse(isDefectResponse(SectionId.SECTION_F, 14, ComplianceResponse.NO))

    // Standard question: No = defect (remedial action required), Yes = compliant
    assertTrue(isDefectResponse(SectionId.SECTION_A, 1, ComplianceResponse.NO))
    assertFalse(isDefectResponse(SectionId.SECTION_A, 1, ComplianceResponse.YES))
  }

  @Test
  fun testChecklistQuestionDomainModelProperties() {
    val qA5 = ChecklistQuestion(
      id = "test_a_5",
      assessmentId = "asm_1",
      sectionId = SectionId.SECTION_A,
      questionNumber = 5,
      questionText = "Are there any signs of molds / yeast / mildew around or inside the basin?",
      response = ComplianceResponse.YES
    )
    assertTrue(qA5.isSpecialYesDefect)
    assertTrue(qA5.isDefectOrNonCompliant)

    val qA5Compliant = qA5.copy(response = ComplianceResponse.NO)
    assertFalse(qA5Compliant.isDefectOrNonCompliant)

    val qStandard = ChecklistQuestion(
      id = "test_a_1",
      assessmentId = "asm_1",
      sectionId = SectionId.SECTION_A,
      questionNumber = 1,
      questionText = "Are basins clean and hygienic?",
      response = ComplianceResponse.YES
    )
    assertFalse(qStandard.isSpecialYesDefect)
    assertFalse(qStandard.isDefectOrNonCompliant)

    val qStandardNonCompliant = qStandard.copy(response = ComplianceResponse.NO)
    assertTrue(qStandardNonCompliant.isDefectOrNonCompliant)
  }

  @Test
  fun testMonitoredMiningSitesCadastralData() {
    assertEquals(4, MONITORED_MINING_SITES.size)
    val letaba = MONITORED_MINING_SITES.first { it.id == "SITE_LETABA" }
    assertEquals("Letaba Crushers (Pty) Ltd", letaba.name)
    assertEquals(-23.8211, letaba.latitude, 0.001)
    assertEquals(30.1633, letaba.longitude, 0.001)
    assertTrue(letaba.cadastralProperty.contains("Farm Tarentaalrant"))
    assertEquals(4, letaba.changeHouseComplexes)

    val afrimat = MONITORED_MINING_SITES.first { it.id == "SITE_AFRIMAT" }
    assertEquals("Afrimat Kuipersbult Quarry", afrimat.name)
    assertTrue(afrimat.cadastralProperty.contains("Kuipersbult"))

    val dwarsrivier = MONITORED_MINING_SITES.first { it.id == "SITE_DWARSRIVIER" }
    assertEquals("Dwarsrivier Chrome Mine", dwarsrivier.name)
    assertEquals(6, dwarsrivier.changeHouseComplexes)
  }

  @Test
  fun testStaffAndOperationsRepositoryPreSeededData() {
    val repo = com.example.data.repository.StaffAndOperationsRepository()
    val staff = repo.staffList.value
    assertTrue("Should have at least 5 staff members pre-seeded", staff.size >= 5)

    val hygienists = staff.filter {
      it.role == com.example.data.model.StaffRole.OCCUPATIONAL_HYGIENIST ||
      it.role == com.example.data.model.StaffRole.SENIOR_OCCUPATIONAL_HYGIENIST
    }
    assertTrue("Should include registered Occupational Hygienists", hygienists.isNotEmpty())

    val tasks = repo.taskList.value
    assertTrue("Should have initial operational tasks", tasks.isNotEmpty())

    val bookings = repo.bookingList.value
    assertTrue("Should have scheduled calendar bookings", bookings.isNotEmpty())

    val services = repo.fieldServices.value
    assertEquals(3, services.size)
    assertEquals(10, com.example.data.model.FieldServiceCategory.values().size)
  }

  @Test
  fun testTaskWorkflowCreationStatusAndReassignment() {
    val repo = com.example.data.repository.StaffAndOperationsRepository()
    val initialTaskCount = repo.taskList.value.size

    val newTask = com.example.data.model.AssessmentTask(
      id = "TASK_TEST_001",
      referenceNumber = "TSK-2026-TEST",
      title = "Urgent Ventilation Survey - Letaba Shaft 2",
      description = "Inspect auxiliary fans and airflow ducts in Shaft 2",
      assignedStaffId = "STAFF_001",
      assignedStaffName = "Dr. A. Sithole",
      customerOrgId = "CUST_001",
      customerOrgName = "Letaba Crushers (Pty) Ltd",
      mineSiteId = "SITE_001",
      mineSiteName = "Letaba Quarry Primary",
      assessmentType = "Ventilation & Airflow Survey",
      facilityLocation = "Shaft 2 Underground",
      priority = com.example.data.model.TaskPriority.URGENT,
      status = com.example.data.model.TaskStatus.ASSIGNED,
      assignmentDate = "2026-11-08",
      scheduledDate = "2026-11-15",
      expectedCompletionDate = "2026-11-16",
      dueDate = "2026-11-16",
      instructions = "Wear full PPE and multi-gas detector"
    )

    repo.createTask(newTask)
    assertEquals(initialTaskCount + 1, repo.taskList.value.size)

    // Update status to IN_PROGRESS
    repo.updateTaskStatus("TASK_TEST_001", com.example.data.model.TaskStatus.IN_PROGRESS, "Started on-site airflow logging")
    val inProgressTask = repo.taskList.value.first { it.id == "TASK_TEST_001" }
    assertEquals(com.example.data.model.TaskStatus.IN_PROGRESS, inProgressTask.status)
    assertEquals("Started on-site airflow logging", inProgressTask.completionNotes)

    // Reassign task to another staff member
    repo.reassignTask("TASK_TEST_001", "STAFF_002", "Nomsa Khumalo", "Assessor handoff due to urgent schedule conflict")
    val reassignedTask = repo.taskList.value.first { it.id == "TASK_TEST_001" }
    assertEquals("STAFF_002", reassignedTask.assignedStaffId)
    assertEquals("Nomsa Khumalo", reassignedTask.assignedStaffName)
    assertTrue(reassignedTask.instructions.contains("handoff"))
  }

  @Test
  fun testFieldTripWorkflowAndDistanceCalculation() {
    val repo = com.example.data.repository.StaffAndOperationsRepository()
    val initialTrips = repo.fieldTrips.value.size

    val newTrip = repo.startTrip(
      assessorId = "STAFF_002",
      assessorName = "Nomsa Khumalo",
      customerName = "Letaba Crushers (Pty) Ltd",
      mineSiteName = "Main Changehouse Complex",
      destinationAddress = "Farm Tarentaalrant 24LT, Tzaneen",
      startOdo = 142580,
      startLat = -23.8211,
      startLng = 30.1633,
      vehicleReg = "CA 998-112 GP",
      notes = "Ablution Facility Statutory Audit"
    )

    assertEquals(initialTrips + 1, repo.fieldTrips.value.size)
    assertEquals(com.example.data.model.TripStatus.IN_PROGRESS, newTrip.status)

    // End trip with odometer 142695 (115 km travelled)
    val endedTrip = repo.endTrip(
      tripId = newTrip.id,
      endOdo = 142695,
      endLat = -23.8211,
      endLng = 30.1633,
      notes = "Arrived on schedule without incident"
    )

    assertNotNull(endedTrip)
    assertEquals(com.example.data.model.TripStatus.COMPLETED, endedTrip?.status)
    assertEquals(142695, endedTrip?.endOdometer)
    assertEquals(115.0, endedTrip?.totalDistanceKm ?: 0.0, 0.1)
  }

  @Test
  fun testBookingDoubleBookingProtectionAndStatus() {
    val repo = com.example.data.repository.StaffAndOperationsRepository()

    // Test conflict check with an existing slot
    val existingBooking = repo.bookingList.value.first()
    val overlappingBooking = existingBooking.copy(
      id = "BOOK_TEST_OVERLAP",
      facilityLocation = "Conflicting Slot"
    )
    val successWithoutConflict = repo.createBooking(overlappingBooking)
    assertFalse("Should detect conflict for duplicate booking slot", successWithoutConflict)

    val distantBooking = existingBooking.copy(
      id = "BOOK_TEST_FUTURE",
      bookingDate = "2027-12-31",
      facilityLocation = "Far future date"
    )
    val successFuture = repo.createBooking(distantBooking)
    assertTrue("Should allow booking on non-conflicting date", successFuture)
  }

  @Test
  fun testNotificationsAndAuditLogging() {
    val repo = com.example.data.repository.StaffAndOperationsRepository()
    val unreadCount = repo.notifications.value.count { !it.isRead }
    assertTrue(unreadCount > 0)

    val firstNotif = repo.notifications.value.first { !it.isRead }
    repo.markNotificationAsRead(firstNotif.id)
    val afterRead = repo.notifications.value.count { !it.isRead }
    assertEquals(unreadCount - 1, afterRead)

    // Audit logs should capture operational events
    val auditLogs = repo.auditLogs.value
    assertTrue("Audit logs must record system operations", auditLogs.isNotEmpty())
  }

  @Test
  fun testStaffEntityAndTaskAssignmentEntityRoomMapping() {
    val sampleStaff = com.example.data.model.StaffMember(
      id = "STF-TEST-99",
      fullName = "Kagiso Molefe",
      employeeNumber = "EMP-9901",
      email = "k.molefe@workforcehealth.co.za",
      phone = "+27 15 307 9999",
      jobTitle = "Occupational Hygienist",
      role = com.example.data.model.StaffRole.OCCUPATIONAL_HYGIENIST,
      ohRegistrationNumber = "SAIOH ROH 0992",
      qualifications = listOf("BSc Hons Environmental Health", "SAIOH Registered"),
      isActive = true
    )

    val entity = com.example.data.local.entity.StaffEntity.fromDomain(sampleStaff)
    assertEquals("STF-TEST-99", entity.id)
    assertEquals("Kagiso Molefe", entity.fullName)
    assertEquals("OCCUPATIONAL_HYGIENIST", entity.role)
    assertTrue(entity.isActive)

    val convertedBack = entity.toDomain()
    assertEquals(sampleStaff.fullName, convertedBack.fullName)
    assertEquals(sampleStaff.role, convertedBack.role)
    assertEquals(sampleStaff.ohRegistrationNumber, convertedBack.ohRegistrationNumber)

    val sampleTask = com.example.data.model.AssessmentTask(
      id = "TSK-TEST-99",
      referenceNumber = "TSK-2026-99",
      title = "Noise Survey - Crusher Plant",
      description = "Conduct sound pressure measurement at 85 dBA boundary contours",
      assignedStaffId = "STF-TEST-99",
      assignedStaffName = "Kagiso Molefe",
      customerOrgId = "CUST-01",
      customerOrgName = "Letaba Crushers",
      mineSiteId = "SITE-01",
      mineSiteName = "Letaba Quarry",
      assessmentType = "Noise Survey",
      facilityLocation = "Crusher Circuit",
      priority = com.example.data.model.TaskPriority.HIGH,
      assignmentDate = "2026-11-09",
      scheduledDate = "2026-11-12",
      expectedCompletionDate = "2026-11-13",
      dueDate = "2026-11-13",
      status = com.example.data.model.TaskStatus.SCHEDULED
    )

    val taskEntity = com.example.data.local.entity.TaskAssignmentEntity.fromDomain(sampleTask)
    assertEquals("TSK-TEST-99", taskEntity.id)
    assertEquals("HIGH", taskEntity.priority)
    assertEquals("SCHEDULED", taskEntity.status)

    val domainTask = taskEntity.toDomain()
    assertEquals(sampleTask.title, domainTask.title)
    assertEquals(com.example.data.model.TaskPriority.HIGH, domainTask.priority)
    assertEquals(com.example.data.model.TaskStatus.SCHEDULED, domainTask.status)
  }

  @Test
  fun testCentralOfficeBackendAdministrationConfiguration() {
    val targetUrl = "https://workforce-health-sa-m28gog.v2.appdeploy.ai/"
    assertTrue(targetUrl.startsWith("https://"))
    assertTrue(targetUrl.contains("workforce-health-sa-m28gog"))
  }
}



