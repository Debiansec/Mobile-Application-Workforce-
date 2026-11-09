package com.example.data.repository

import com.example.data.local.WorkforceDatabase
import com.example.data.model.AssessmentBooking
import com.example.data.model.AssessmentTask
import com.example.data.model.AuditLogEvent
import com.example.data.model.BookingStatus
import com.example.data.model.CustomerMineAllocation
import com.example.data.model.FieldServiceCategory
import com.example.data.model.FieldServiceRecord
import com.example.data.model.FieldTrip
import com.example.data.model.IncompleteAssessmentItem
import com.example.data.model.IncompleteReason
import com.example.data.model.LiveGpsLocation
import com.example.data.model.StaffAvailability
import com.example.data.model.StaffMember
import com.example.data.model.StaffNotification
import com.example.data.model.StaffRole
import com.example.data.model.TaskPriority
import com.example.data.model.TaskStatus
import com.example.data.model.TripStatus
import com.example.data.local.entity.StaffEntity
import com.example.data.local.entity.TaskAssignmentEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class StaffAndOperationsRepository(private val db: WorkforceDatabase? = null) {

    private val repositoryScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private val _staffList = MutableStateFlow<List<StaffMember>>(seedStaff())
    val staffList: StateFlow<List<StaffMember>> = _staffList.asStateFlow()

    private val _taskList = MutableStateFlow<List<AssessmentTask>>(seedTasks())
    val taskList: StateFlow<List<AssessmentTask>> = _taskList.asStateFlow()

    init {
        db?.let { database ->
            repositoryScope.launch {
                try {
                    // Seed initial Room staff if table is empty
                    if (database.staffDao().countStaff() == 0) {
                        val initialStaff = seedStaff().map { StaffEntity.fromDomain(it) }
                        database.staffDao().insertAllStaff(initialStaff)
                    }
                    // Seed initial Room tasks if table is empty
                    if (database.taskAssignmentDao().countTasks() == 0) {
                        val initialTasks = seedTasks().map { TaskAssignmentEntity.fromDomain(it) }
                        database.taskAssignmentDao().insertAllTasks(initialTasks)
                    }

                    // Observe Room staff table continuously for reactive UI updates
                    launch {
                        database.staffDao().getAllStaff().collectLatest { entities ->
                            if (entities.isNotEmpty()) {
                                _staffList.value = entities.map { it.toDomain() }
                            }
                        }
                    }

                    // Observe Room tasks table continuously for reactive UI updates
                    launch {
                        database.taskAssignmentDao().getAllTasks().collectLatest { entities ->
                            if (entities.isNotEmpty()) {
                                _taskList.value = entities.map { it.toDomain() }
                            }
                        }
                    }
                } catch (_: Exception) {
                    // Keep in-memory StateFlow functional if database transaction is delayed
                }
            }
        }
    }

    private val _bookingList = MutableStateFlow<List<AssessmentBooking>>(seedBookings())
    val bookingList: StateFlow<List<AssessmentBooking>> = _bookingList.asStateFlow()

    private val _allocationsList = MutableStateFlow<List<CustomerMineAllocation>>(seedAllocations())
    val allocationsList: StateFlow<List<CustomerMineAllocation>> = _allocationsList.asStateFlow()

    private val _fieldServices = MutableStateFlow<List<FieldServiceRecord>>(seedFieldServices())
    val fieldServices: StateFlow<List<FieldServiceRecord>> = _fieldServices.asStateFlow()

    private val _fieldTrips = MutableStateFlow<List<FieldTrip>>(seedTrips())
    val fieldTrips: StateFlow<List<FieldTrip>> = _fieldTrips.asStateFlow()

    private val _liveGpsLocations = MutableStateFlow<List<LiveGpsLocation>>(seedGpsLocations())
    val liveGpsLocations: StateFlow<List<LiveGpsLocation>> = _liveGpsLocations.asStateFlow()

    private val _notifications = MutableStateFlow<List<StaffNotification>>(seedNotifications())
    val notifications: StateFlow<List<StaffNotification>> = _notifications.asStateFlow()

    private val _auditLogs = MutableStateFlow<List<AuditLogEvent>>(seedAuditLogs())
    val auditLogs: StateFlow<List<AuditLogEvent>> = _auditLogs.asStateFlow()

    private val _isLiveGpsTrackingActive = MutableStateFlow(false)
    val isLiveGpsTrackingActive: StateFlow<Boolean> = _isLiveGpsTrackingActive.asStateFlow()

    fun toggleLiveGpsTracking(): Boolean {
        _isLiveGpsTrackingActive.value = !_isLiveGpsTrackingActive.value
        logAuditEvent(
            operator = "Current Assessor",
            action = if (_isLiveGpsTrackingActive.value) "GPS_TRACKING_STARTED" else "GPS_TRACKING_STOPPED",
            entity = "GPS Telemetry",
            details = "Assessor location broadcast status set to ${_isLiveGpsTrackingActive.value}"
        )
        return _isLiveGpsTrackingActive.value
    }

    // --- STAFF MANAGEMENT ---
    fun addOrUpdateStaff(staff: StaffMember) {
        val current = _staffList.value.toMutableList()
        val index = current.indexOfFirst { it.id == staff.id }
        if (index >= 0) {
            current[index] = staff
        } else {
            current.add(0, staff)
        }
        _staffList.value = current
        logAuditEvent("Administrator", "STAFF_SAVED", "StaffMember", "${staff.fullName} (${staff.role.displayName}) updated")

        db?.let { database ->
            repositoryScope.launch {
                database.staffDao().insertStaff(StaffEntity.fromDomain(staff))
            }
        }
    }

    fun setStaffAvailability(staffId: String, status: StaffAvailability) {
        _staffList.value = _staffList.value.map {
            if (it.id == staffId) it.copy(availabilityStatus = status) else it
        }
        logAuditEvent("Operations Manager", "AVAILABILITY_CHANGE", "StaffMember", "Staff $staffId set to ${status.label}")

        db?.let { database ->
            repositoryScope.launch {
                database.staffDao().updateAvailability(staffId, status.name)
            }
        }
    }

    fun deactivateStaff(staffId: String) {
        _staffList.value = _staffList.value.map {
            if (it.id == staffId) it.copy(isActive = false, availabilityStatus = StaffAvailability.UNAVAILABLE) else it
        }
        logAuditEvent("Administrator", "STAFF_DEACTIVATED", "StaffMember", "Staff account $staffId deactivated")

        db?.let { database ->
            repositoryScope.launch {
                database.staffDao().deactivateStaff(staffId)
            }
        }
    }

    // --- TASK ASSIGNMENTS ---
    fun createTask(task: AssessmentTask) {
        val current = _taskList.value.toMutableList()
        current.add(0, task)
        _taskList.value = current

        // Send internal notification to assigned assessor
        createNotification(
            recipientStaffId = task.assignedStaffId,
            title = "New Task Assigned: ${task.referenceNumber}",
            message = "You have been assigned to ${task.title} at ${task.mineSiteName}. Due: ${task.dueDate}",
            category = "Task Assignment"
        )

        logAuditEvent("Operations Manager", "TASK_CREATED", "AssessmentTask", "${task.referenceNumber} assigned to ${task.assignedStaffName}")

        db?.let { database ->
            repositoryScope.launch {
                database.taskAssignmentDao().insertTask(TaskAssignmentEntity.fromDomain(task))
            }
        }
    }

    fun updateTaskStatus(taskId: String, status: TaskStatus, notes: String = "") {
        _taskList.value = _taskList.value.map {
            if (it.id == taskId) {
                it.copy(status = status, completionNotes = if (notes.isNotBlank()) notes else it.completionNotes)
            } else it
        }
        logAuditEvent("Assessor", "TASK_STATUS_UPDATED", "AssessmentTask", "Task $taskId changed to ${status.label}")

        db?.let { database ->
            repositoryScope.launch {
                database.taskAssignmentDao().updateTaskStatus(taskId, status.name, notes)
            }
        }
    }

    fun reassignTask(taskId: String, newStaffId: String, newStaffName: String, notes: String = "") {
        var updatedInstructions = ""
        _taskList.value = _taskList.value.map {
            if (it.id == taskId) {
                updatedInstructions = if (notes.isNotBlank()) "${it.instructions}\nReassignment note: $notes" else it.instructions
                it.copy(
                    assignedStaffId = newStaffId,
                    assignedStaffName = newStaffName,
                    status = TaskStatus.ASSIGNED,
                    instructions = updatedInstructions
                )
            } else it
        }

        createNotification(
            recipientStaffId = newStaffId,
            title = "Task Reassigned To You",
            message = "Task has been reassigned to you. Please review requirements.",
            category = "Task Assignment"
        )

        logAuditEvent("Operations Manager", "TASK_REASSIGNED", "AssessmentTask", "Task $taskId transferred to $newStaffName")

        db?.let { database ->
            repositoryScope.launch {
                database.taskAssignmentDao().reassignTask(taskId, newStaffId, newStaffName, updatedInstructions)
            }
        }
    }

    // --- CALENDAR & BOOKINGS ---
    fun createBooking(booking: AssessmentBooking): Boolean {
        // Detect scheduling conflict for assigned assessor on same date/time
        val conflict = _bookingList.value.any {
            it.assignedAssessorId == booking.assignedAssessorId &&
            it.bookingDate == booking.bookingDate &&
            it.id != booking.id &&
            it.status != BookingStatus.CANCELLED
        }

        val current = _bookingList.value.toMutableList()
        current.add(0, booking)
        _bookingList.value = current

        createNotification(
            recipientStaffId = booking.assignedAssessorId,
            title = "New Inspection Booking: ${booking.siteName}",
            message = "Scheduled on ${booking.bookingDate} at ${booking.arrivalTime}. Contact: ${booking.appointmentContact}",
            category = "Booking"
        )

        logAuditEvent("Scheduler", "BOOKING_CREATED", "AssessmentBooking", "${booking.id} booked at ${booking.siteName} for ${booking.assignedAssessorName}")
        return !conflict
    }

    fun updateBookingStatus(bookingId: String, status: BookingStatus) {
        _bookingList.value = _bookingList.value.map {
            if (it.id == bookingId) it.copy(status = status) else it
        }
        logAuditEvent("Scheduler", "BOOKING_STATUS_CHANGED", "AssessmentBooking", "$bookingId updated to ${status.label}")
    }

    // --- CUSTOMER & MINE ALLOCATIONS ---
    fun allocateCustomerMine(allocation: CustomerMineAllocation) {
        val current = _allocationsList.value.toMutableList()
        current.add(0, allocation)
        _allocationsList.value = current

        // Update staff member's assigned site list
        _staffList.value = _staffList.value.map { staff ->
            if (staff.id == allocation.assessorId) {
                staff.copy(
                    assignedCustomerIds = (staff.assignedCustomerIds + allocation.customerId).distinct(),
                    assignedSiteIds = (staff.assignedSiteIds + allocation.siteId).distinct()
                )
            } else staff
        }

        logAuditEvent("Management", "ALLOCATION_ASSIGNED", "CustomerMineAllocation", "${allocation.assessorName} allocated to ${allocation.siteName}")
    }

    // --- FIELD TRIPS (START / END TRIP) ---
    fun startTrip(
        assessorId: String,
        assessorName: String,
        customerName: String,
        mineSiteName: String,
        destinationAddress: String,
        startOdo: Int,
        startLat: Double,
        startLng: Double,
        vehicleReg: String,
        notes: String = ""
    ): FieldTrip {
        val tripId = "TRP-${SimpleDateFormat("yyyyMMdd-HHmm", Locale.US).format(Date())}"
        val newTrip = FieldTrip(
            id = tripId,
            assessorId = assessorId,
            assessorName = assessorName,
            customerName = customerName,
            mineSiteName = mineSiteName,
            destinationAddress = destinationAddress,
            vehicleRegistration = vehicleReg,
            startOdometer = startOdo,
            startLat = startLat,
            startLng = startLng,
            startDateTime = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date()),
            status = TripStatus.IN_PROGRESS,
            tripNotes = notes
        )

        val current = _fieldTrips.value.toMutableList()
        current.add(0, newTrip)
        _fieldTrips.value = current

        // Set staff availability to TRAVELLING
        setStaffAvailability(assessorId, StaffAvailability.TRAVELLING)

        // Broadcast GPS
        updateGpsPosition(assessorId, assessorName, startLat, startLng, 3.5f, "Departed for $mineSiteName", true, tripId)

        logAuditEvent("Assessor", "TRIP_STARTED", "FieldTrip", "Trip $tripId started by $assessorName to $mineSiteName")
        return newTrip
    }

    fun endTrip(
        tripId: String,
        endOdo: Int?,
        endLat: Double,
        endLng: Double,
        notes: String = ""
    ): FieldTrip? {
        var completedTrip: FieldTrip? = null
        val nowFormatted = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())

        _fieldTrips.value = _fieldTrips.value.map { trip ->
            if (trip.id == tripId) {
                val distance = if (endOdo != null && endOdo >= trip.startOdometer) {
                    (endOdo - trip.startOdometer).toDouble()
                } else {
                    48.5 // Estimated transit km
                }
                val updated = trip.copy(
                    endOdometer = endOdo,
                    endLat = endLat,
                    endLng = endLng,
                    endDateTime = nowFormatted,
                    totalDistanceKm = distance,
                    totalDurationMinutes = 65,
                    status = TripStatus.COMPLETED,
                    tripNotes = if (notes.isNotBlank()) "${trip.tripNotes}\nEnd notes: $notes" else trip.tripNotes
                )
                completedTrip = updated
                updated
            } else trip
        }

        completedTrip?.let { trip ->
            setStaffAvailability(trip.assessorId, StaffAvailability.ON_ASSIGNMENT)
            updateGpsPosition(trip.assessorId, trip.assessorName, endLat, endLng, 4.0f, "Arrived at ${trip.mineSiteName}", false, null)
            logAuditEvent("Assessor", "TRIP_COMPLETED", "FieldTrip", "Trip ${trip.id} completed. Distance: ${trip.totalDistanceKm}km")
        }

        return completedTrip
    }

    // --- LIVE GPS BROADCAST ---
    fun updateGpsPosition(
        staffId: String,
        staffName: String,
        lat: Double,
        lng: Double,
        accuracy: Float,
        address: String,
        isTravelling: Boolean = false,
        activeTripId: String? = null
    ) {
        val current = _liveGpsLocations.value.toMutableList()
        val index = current.indexOfFirst { it.staffId == staffId }
        val updated = LiveGpsLocation(
            staffId = staffId,
            staffName = staffName,
            latitude = lat,
            longitude = lng,
            accuracy = accuracy,
            timestamp = System.currentTimeMillis(),
            address = address,
            isTravelling = isTravelling,
            activeTripId = activeTripId
        )

        if (index >= 0) {
            current[index] = updated
        } else {
            current.add(0, updated)
        }
        _liveGpsLocations.value = current
    }

    // --- NOTIFICATIONS ---
    fun createNotification(recipientStaffId: String, title: String, message: String, category: String) {
        val notification = StaffNotification(
            id = "NOTIF-${UUID.randomUUID().toString().take(8)}",
            recipientStaffId = recipientStaffId,
            title = title,
            message = message,
            category = category,
            timestamp = System.currentTimeMillis(),
            isRead = false
        )
        val current = _notifications.value.toMutableList()
        current.add(0, notification)
        _notifications.value = current
    }

    fun markNotificationAsRead(id: String) {
        _notifications.value = _notifications.value.map {
            if (it.id == id) it.copy(isRead = true) else it
        }
    }

    fun clearAllNotifications(staffId: String) {
        _notifications.value = _notifications.value.filter { it.recipientStaffId != staffId }
    }

    // --- AUDIT LOGS ---
    fun logAuditEvent(operator: String, action: String, entity: String, details: String) {
        val event = AuditLogEvent(
            id = "LOG-${UUID.randomUUID().toString().take(8)}",
            timestamp = System.currentTimeMillis(),
            operatorName = operator,
            action = action,
            targetEntity = entity,
            details = details
        )
        val current = _auditLogs.value.toMutableList()
        current.add(0, event)
        _auditLogs.value = current
    }

    // --- SEED DATA ---
    private fun seedStaff(): List<StaffMember> = listOf(
        StaffMember(
            id = "STF-1001",
            fullName = "Nomsa Khumalo",
            employeeNumber = "EMP-2023-042",
            email = "n.khumalo@workforcehealth.co.za",
            phone = "+27 (0)82 491 8820",
            jobTitle = "Occupational Hygiene Assistant",
            department = "Occupational Hygiene & Safety",
            role = StaffRole.OCCUPATIONAL_HYGIENE_ASSISTANT,
            qualifications = listOf("National Diploma Environmental Health", "SAIOH Cert #4092", "Mine Gas & Dust Sampling"),
            ohRegistrationNumber = "SAIOH-ROHA-2023-4092",
            assignedCustomerIds = listOf("CUST_LETABA", "CUST_AFRIMAT"),
            assignedSiteIds = listOf("SITE_LETABA", "SITE_AFRIMAT"),
            employmentStatus = "Permanent Full-time",
            availabilityStatus = StaffAvailability.AVAILABLE,
            workingHours = "07:00 - 16:30 (Mon-Fri)",
            completedTasksCount = 48,
            outstandingTasksCount = 3
        ),
        StaffMember(
            id = "STF-1002",
            fullName = "Pieter Van Der Merwe",
            employeeNumber = "EMP-2021-018",
            email = "p.vdmerwe@workforcehealth.co.za",
            phone = "+27 (0)83 552 9104",
            jobTitle = "Occupational Hygienist",
            department = "Mining Environmental Engineering",
            role = StaffRole.OCCUPATIONAL_HYGIENIST,
            qualifications = listOf("BTech Environmental Health", "SAIOH ROH #1820", "Chamber of Mines Intermediate MEC"),
            ohRegistrationNumber = "SAIOH-ROH-2021-1820",
            assignedCustomerIds = listOf("CUST_DWARSRIVIER", "CUST_GRASVALLY"),
            assignedSiteIds = listOf("SITE_DWARSRIVIER", "SITE_GRASVALLY"),
            employmentStatus = "Permanent Full-time",
            availabilityStatus = StaffAvailability.ON_ASSIGNMENT,
            workingHours = "06:30 - 16:00 (Mon-Fri)",
            completedTasksCount = 82,
            outstandingTasksCount = 2
        ),
        StaffMember(
            id = "STF-1003",
            fullName = "Dr. Allan Sithole",
            employeeNumber = "EMP-2019-005",
            email = "a.sithole@workforcehealth.co.za",
            phone = "+27 (0)82 890 1445",
            jobTitle = "Senior Occupational Hygienist & Lead Reviewer",
            department = "Executive Technical Oversight",
            role = StaffRole.SENIOR_OCCUPATIONAL_HYGIENIST,
            qualifications = listOf("PhD Occupational Hygiene", "Fellow SAIOH", "Chamber of Mines Certificate in MEC"),
            ohRegistrationNumber = "SAIOH-FROH-2019-005",
            assignedCustomerIds = listOf("CUST_LETABA", "CUST_AFRIMAT", "CUST_DWARSRIVIER"),
            assignedSiteIds = listOf("SITE_LETABA", "SITE_AFRIMAT", "SITE_DWARSRIVIER"),
            employmentStatus = "Permanent Full-time",
            availabilityStatus = StaffAvailability.AVAILABLE,
            workingHours = "08:00 - 17:00",
            completedTasksCount = 140,
            outstandingTasksCount = 1
        ),
        StaffMember(
            id = "STF-1004",
            fullName = "Thabo Ndlovu",
            employeeNumber = "EMP-2024-089",
            email = "t.ndlovu@workforcehealth.co.za",
            phone = "+27 (0)71 209 4432",
            jobTitle = "Field Hygiene Assessor",
            department = "Field Operations",
            role = StaffRole.OCCUPATIONAL_HYGIENE_ASSISTANT,
            qualifications = listOf("BSc Environmental Sciences", "SAIOH Candidate #5120"),
            ohRegistrationNumber = "SAIOH-ROHA-2024-5120",
            assignedCustomerIds = listOf("CUST_LETABA"),
            assignedSiteIds = listOf("SITE_LETABA"),
            employmentStatus = "Contract Assessor",
            availabilityStatus = StaffAvailability.TRAVELLING,
            workingHours = "07:00 - 16:00",
            completedTasksCount = 24,
            outstandingTasksCount = 4
        ),
        StaffMember(
            id = "STF-1005",
            fullName = "Johan Botha",
            employeeNumber = "EMP-2020-011",
            email = "j.botha@workforcehealth.co.za",
            phone = "+27 (0)82 991 3004",
            jobTitle = "Operations & Scheduling Manager",
            department = "Field Logistics & Compliance",
            role = StaffRole.OPERATIONS_MANAGER,
            qualifications = listOf("BCom Logistics Management", "OHS Act & MHSA Statutory Compliance Auditor"),
            ohRegistrationNumber = "COMPL-AUD-2020-011",
            assignedCustomerIds = listOf("CUST_LETABA", "CUST_AFRIMAT", "CUST_DWARSRIVIER", "CUST_GRASVALLY"),
            assignedSiteIds = emptyList(),
            employmentStatus = "Permanent Management",
            availabilityStatus = StaffAvailability.AVAILABLE,
            workingHours = "07:30 - 17:00",
            completedTasksCount = 95,
            outstandingTasksCount = 0
        ),
        StaffMember(
            id = "STF-1006",
            fullName = "Sarah Mokoena",
            employeeNumber = "EMP-2022-031",
            email = "s.mokoena@workforcehealth.co.za",
            phone = "+27 (0)11 892 4001",
            jobTitle = "System & Statutory Data Administrator",
            department = "IT & Systems Governance",
            role = StaffRole.SYSTEM_ADMIN,
            qualifications = listOf("BSc Computer Science", "Certified Information Security Manager"),
            ohRegistrationNumber = "IT-SYS-ADMIN-2022",
            assignedCustomerIds = emptyList(),
            assignedSiteIds = emptyList(),
            employmentStatus = "Permanent Management",
            availabilityStatus = StaffAvailability.AVAILABLE,
            workingHours = "08:00 - 16:30",
            completedTasksCount = 110,
            outstandingTasksCount = 0
        )
    )

    private fun seedTasks(): List<AssessmentTask> = listOf(
        AssessmentTask(
            id = "TSK-001",
            referenceNumber = "TSK-2026-0042",
            title = "Monthly Ablution Facilities Sanitation Audit (Male)",
            description = "Statutory MHSA monthly audit of Male Change House Complex #1 including mould checks, water flow and chemical register.",
            assignedStaffId = "STF-1001",
            assignedStaffName = "Nomsa Khumalo",
            customerOrgId = "CUST_LETABA",
            customerOrgName = "Letaba Crushers (Pty) Ltd",
            mineSiteId = "SITE_LETABA",
            mineSiteName = "Letaba Quarry Tzaneen",
            assessmentType = "Monthly ablution facilities checklist *Male",
            facilityLocation = "Change House Complex #1 (Male Main)",
            priority = TaskPriority.HIGH,
            assignmentDate = "2026-11-01",
            scheduledDate = "2026-11-08",
            startTime = "08:30",
            expectedCompletionDate = "2026-11-08",
            dueDate = "2026-11-09",
            instructions = "Compulsory voice remarks and GPS photo evidence required on mould criteria.",
            status = TaskStatus.IN_PROGRESS
        ),
        AssessmentTask(
            id = "TSK-002",
            referenceNumber = "TSK-2026-0043",
            title = "Follow-up Remedial Action Verification (Section C Toilets)",
            description = "Inspect repaired flush valve mechanism and sanitising chemical dispenser on toilet cubicles #3 and #4.",
            assignedStaffId = "STF-1001",
            assignedStaffName = "Nomsa Khumalo",
            customerOrgId = "CUST_LETABA",
            customerOrgName = "Letaba Crushers (Pty) Ltd",
            mineSiteId = "SITE_LETABA",
            mineSiteName = "Letaba Quarry Tzaneen",
            assessmentType = "Corrective Action Verification",
            facilityLocation = "Plant Workshop Change House",
            priority = TaskPriority.URGENT,
            assignmentDate = "2026-11-03",
            scheduledDate = "2026-11-08",
            startTime = "11:00",
            expectedCompletionDate = "2026-11-08",
            dueDate = "2026-11-08",
            instructions = "Verify with maintenance foreman signature before closing remedial action.",
            status = TaskStatus.ASSIGNED
        ),
        AssessmentTask(
            id = "TSK-003",
            referenceNumber = "TSK-2026-0044",
            title = "Statutory Quarterly Dust & Silica Particulate Survey",
            description = "Quarterly personal sampling on haul truck drivers and primary crusher operators.",
            assignedStaffId = "STF-1002",
            assignedStaffName = "Pieter Van Der Merwe",
            customerOrgId = "CUST_AFRIMAT",
            customerOrgName = "Afrimat Aggregates (Pty) Ltd",
            mineSiteId = "SITE_AFRIMAT",
            mineSiteName = "Afrimat Kuipersbult Quarry",
            assessmentType = "Airborne Dust & Particulate Monitoring",
            facilityLocation = "Open Cast Pit & Secondary Sizing Screen",
            priority = TaskPriority.NORMAL,
            assignmentDate = "2026-11-05",
            scheduledDate = "2026-11-12",
            startTime = "07:00",
            expectedCompletionDate = "2026-11-12",
            dueDate = "2026-11-14",
            instructions = "Deploy GilAir Plus personal sampling pumps calibrated to 2.2 L/min with Higgins-Dewell cyclones.",
            status = TaskStatus.SCHEDULED
        ),
        AssessmentTask(
            id = "TSK-004",
            referenceNumber = "TSK-2026-0045",
            title = "Full Occupational Hygiene Review & Statutory Sign-Off",
            description = "Final statutory technical review of 28-page Master Template assessment report for Dwarsrivier Chrome.",
            assignedStaffId = "STF-1003",
            assignedStaffName = "Dr. Allan Sithole",
            customerOrgId = "CUST_DWARSRIVIER",
            customerOrgName = "Dwarsrivier Chrome Mine (Assore)",
            mineSiteId = "SITE_DWARSRIVIER",
            mineSiteName = "Dwarsrivier Chrome Mine",
            assessmentType = "Statutory Master Assessment Review",
            facilityLocation = "South Shaft Complex",
            priority = TaskPriority.HIGH,
            assignmentDate = "2026-11-06",
            scheduledDate = "2026-11-10",
            startTime = "09:00",
            expectedCompletionDate = "2026-11-10",
            dueDate = "2026-11-11",
            instructions = "Verify legal annexures, calibration certificates and DMR submission format.",
            status = TaskStatus.PENDING_REVIEW
        )
    )

    private fun seedBookings(): List<AssessmentBooking> = listOf(
        AssessmentBooking(
            id = "BKG-2026-018",
            customerId = "CUST_LETABA",
            customerName = "Letaba Crushers (Pty) Ltd",
            siteId = "SITE_LETABA",
            siteName = "Letaba Quarry Tzaneen",
            assessmentType = "Monthly ablution facilities checklist *Male",
            facilityLocation = "Main Change House Complex",
            assignedAssessorId = "STF-1001",
            assignedAssessorName = "Nomsa Khumalo",
            additionalTeamMembers = "Thabo Ndlovu (Assistant)",
            bookingDate = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date()),
            arrivalTime = "08:30",
            departureTime = "15:30",
            estimatedDurationHours = 7.0,
            appointmentContact = "Mr. J. Du Plessis (Mine Manager)",
            contactTelephone = "+27 (0)15 307 4000",
            specialInstructions = "Report to security gate for breathalyzer test before accessing plant area.",
            bookingNotes = "Quarterly calibration check on sound level meter AZ 87786 confirmed.",
            status = BookingStatus.CONFIRMED
        ),
        AssessmentBooking(
            id = "BKG-2026-019",
            customerId = "CUST_AFRIMAT",
            customerName = "Afrimat Aggregates (Pty) Ltd",
            siteId = "SITE_AFRIMAT",
            siteName = "Afrimat Kuipersbult Quarry",
            assessmentType = "Airborne Dust & Particulate Monitoring",
            facilityLocation = "Pit Workshops & Crusher Circuit",
            assignedAssessorId = "STF-1002",
            assignedAssessorName = "Pieter Van Der Merwe",
            bookingDate = "2026-11-12",
            arrivalTime = "07:00",
            departureTime = "16:00",
            estimatedDurationHours = 9.0,
            appointmentContact = "Kobus Nel (Safety Officer)",
            contactTelephone = "+27 (0)14 763 2190",
            specialInstructions = "Full PPE compulsory: Hardhat with lamp bracket, steel-toe boots, earplugs & high-vis reflective vest.",
            bookingNotes = "Assessor vehicle DF 42 LK GP cleared for quarry haul road entry.",
            status = BookingStatus.BOOKED
        ),
        AssessmentBooking(
            id = "BKG-2026-020",
            customerId = "CUST_DWARSRIVIER",
            customerName = "Dwarsrivier Chrome Mine",
            siteId = "SITE_DWARSRIVIER",
            siteName = "Dwarsrivier Chrome Mine",
            assessmentType = "Thermal Environment & WBGT Heat Stress",
            facilityLocation = "Underground Ventilation Shaft 3",
            assignedAssessorId = "STF-1003",
            assignedAssessorName = "Dr. Allan Sithole",
            bookingDate = "2026-11-15",
            arrivalTime = "08:00",
            departureTime = "14:30",
            estimatedDurationHours = 6.5,
            appointmentContact = "Mpho Mashaba (Ventilation Engineer)",
            contactTelephone = "+27 (0)13 230 5000",
            specialInstructions = "Underground induction valid passport required.",
            status = BookingStatus.PENDING_CONFIRMATION
        )
    )

    private fun seedAllocations(): List<CustomerMineAllocation> = listOf(
        CustomerMineAllocation(
            id = "ALC-101",
            assessorId = "STF-1001",
            assessorName = "Nomsa Khumalo",
            customerId = "CUST_LETABA",
            customerName = "Letaba Crushers (Pty) Ltd",
            siteId = "SITE_LETABA",
            siteName = "Letaba Quarry Tzaneen",
            allocatedDate = "2026-01-15",
            allocatedBy = "Johan Botha (Operations Mgr)",
            notes = "Lead assessor for all monthly change house and sanitation compliance audits."
        ),
        CustomerMineAllocation(
            id = "ALC-102",
            assessorId = "STF-1001",
            assessorName = "Nomsa Khumalo",
            customerId = "CUST_AFRIMAT",
            customerName = "Afrimat Aggregates (Pty) Ltd",
            siteId = "SITE_AFRIMAT",
            siteName = "Afrimat Kuipersbult Quarry",
            allocatedDate = "2026-03-01",
            allocatedBy = "Johan Botha (Operations Mgr)",
            notes = "Ablution inspections and environmental sanitising verification."
        ),
        CustomerMineAllocation(
            id = "ALC-103",
            assessorId = "STF-1002",
            assessorName = "Pieter Van Der Merwe",
            customerId = "CUST_DWARSRIVIER",
            customerName = "Dwarsrivier Chrome Mine",
            siteId = "SITE_DWARSRIVIER",
            siteName = "Dwarsrivier Chrome Mine",
            allocatedDate = "2026-02-10",
            allocatedBy = "Dr. Allan Sithole (Senior OH)",
            notes = "Appointed Occupational Hygienist under MHSA Regulation 9.2."
        )
    )

    private fun seedFieldServices(): List<FieldServiceRecord> = listOf(
        FieldServiceRecord(
            id = "FS-01",
            customerId = "CUST_LETABA",
            siteId = "SITE_LETABA",
            customerName = "Letaba Crushers (Pty) Ltd",
            siteName = "Letaba Quarry Tzaneen",
            category = FieldServiceCategory.ABLUTION_HYGIENE,
            title = "Change House Sanitation & Potable Water Quality Check",
            assignedAssessorId = "STF-1001",
            assignedAssessorName = "Nomsa Khumalo",
            scheduledDate = "2026-11-08",
            status = "In Progress",
            notes = "Survey covering 4 complexes and chemical register verification."
        ),
        FieldServiceRecord(
            id = "FS-02",
            customerId = "CUST_AFRIMAT",
            siteId = "SITE_AFRIMAT",
            customerName = "Afrimat Aggregates",
            siteName = "Kuipersbult Quarry",
            category = FieldServiceCategory.NOISE_SURVEY,
            title = "Sound Pressure Level Mapping (Crusher Plant)",
            assignedAssessorId = "STF-1002",
            assignedAssessorName = "Pieter Van Der Merwe",
            scheduledDate = "2026-11-12",
            status = "Scheduled",
            notes = "Type 1 sound level meter survey at 85 dBA boundary contours."
        ),
        FieldServiceRecord(
            id = "FS-03",
            customerId = "CUST_DWARSRIVIER",
            siteId = "SITE_DWARSRIVIER",
            customerName = "Dwarsrivier Chrome Mine",
            siteName = "Dwarsrivier Shafts",
            category = FieldServiceCategory.THERMAL_ENVIRONMENT,
            title = "WBGT Thermal Stress & Heat Stress Index Survey",
            assignedAssessorId = "STF-1003",
            assignedAssessorName = "Dr. Allan Sithole",
            scheduledDate = "2026-11-15",
            status = "Scheduled",
            notes = "Underground haulage headings and refuge chamber environments."
        )
    )

    private fun seedTrips(): List<FieldTrip> = listOf(
        FieldTrip(
            id = "TRP-2026-081",
            assessorId = "STF-1001",
            assessorName = "Nomsa Khumalo",
            customerName = "Letaba Crushers (Pty) Ltd",
            mineSiteName = "Letaba Quarry Tzaneen",
            destinationAddress = "Farm Tarentaalrant 24LT, Tzaneen, Limpopo",
            vehicleRegistration = "DF 42 LK GP",
            startOdometer = 142580,
            startLat = -23.8211,
            startLng = 30.1633,
            startAddress = "WFHSA Regional Hub, Tzaneen",
            startDateTime = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date(System.currentTimeMillis() - 7200000)),
            totalDistanceKm = 38.4,
            totalDurationMinutes = 52,
            status = TripStatus.IN_PROGRESS,
            tripNotes = "Arrived at Letaba Quarry main gate. Vehicle inspection completed.",
            linkedAssessmentId = "ASM_LETABA_001"
        ),
        FieldTrip(
            id = "TRP-2026-080",
            assessorId = "STF-1002",
            assessorName = "Pieter Van Der Merwe",
            customerName = "Dwarsrivier Chrome Mine",
            mineSiteName = "Dwarsrivier Chrome Mine",
            destinationAddress = "Farm Dwarsrivier 372KT, Steelpoort Valley",
            vehicleRegistration = "KH 88 MN GP",
            startOdometer = 98400,
            endOdometer = 98514,
            startLat = -24.9281,
            startLng = 30.1256,
            startAddress = "Burgersfort Engineering Depot",
            endLat = -24.9281,
            endLng = 30.1256,
            endAddress = "Dwarsrivier North Shaft Complex",
            startDateTime = "2026-11-06 07:30",
            endDateTime = "2026-11-06 16:45",
            totalDistanceKm = 114.0,
            totalDurationMinutes = 110,
            status = TripStatus.COMPLETED,
            tripNotes = "Trip logged without incidents. Odometer verified."
        )
    )

    private fun seedGpsLocations(): List<LiveGpsLocation> = listOf(
        LiveGpsLocation(
            staffId = "STF-1001",
            staffName = "Nomsa Khumalo",
            latitude = -23.8211,
            longitude = 30.1633,
            accuracy = 3.8f,
            address = "Letaba Crushers (Pty) Ltd, Farm Tarentaalrant 24LT, Tzaneen",
            isTravelling = false,
            activeTripId = "TRP-2026-081",
            currentDestination = "Change House Complex #1 (Male)"
        ),
        LiveGpsLocation(
            staffId = "STF-1002",
            staffName = "Pieter Van Der Merwe",
            latitude = -24.9281,
            longitude = 30.1256,
            accuracy = 4.2f,
            address = "Dwarsrivier Chrome Mine, Steelpoort Valley",
            isTravelling = false,
            currentDestination = "South Shaft Administration"
        ),
        LiveGpsLocation(
            staffId = "STF-1004",
            staffName = "Thabo Ndlovu",
            latitude = -23.7076,
            longitude = 27.5794,
            accuracy = 5.0f,
            address = "R510 Road en route to Kuipersbult Quarry, Lephalale",
            isTravelling = true,
            currentDestination = "Afrimat Kuipersbult Quarry"
        )
    )

    private fun seedNotifications(): List<StaffNotification> = listOf(
        StaffNotification(
            id = "NOTIF-1",
            recipientStaffId = "STF-1001",
            title = "Task Assignment: Monthly Ablution Audit",
            message = "You have been assigned to audit Change House Complex #1 at Letaba Crushers.",
            category = "Task Assignment",
            timestamp = System.currentTimeMillis() - 3600000,
            isRead = false
        ),
        StaffNotification(
            id = "NOTIF-2",
            recipientStaffId = "STF-1001",
            title = "Calendar Booking Confirmed: Letaba Crushers",
            message = "Booking confirmed for today at 08:30 with Mine Manager Mr. J. Du Plessis.",
            category = "Booking",
            timestamp = System.currentTimeMillis() - 7200000,
            isRead = false
        ),
        StaffNotification(
            id = "NOTIF-3",
            recipientStaffId = "STF-1001",
            title = "Action Required: Section A Q5 Finding",
            message = "Mould finding logged. Ensure photographic evidence and corrective action details are saved.",
            category = "Reminder",
            timestamp = System.currentTimeMillis() - 1800000,
            isRead = false
        )
    )

    private fun seedAuditLogs(): List<AuditLogEvent> = listOf(
        AuditLogEvent(
            id = "LOG-01",
            timestamp = System.currentTimeMillis() - 7200000,
            operatorName = "Nomsa Khumalo",
            action = "TRIP_STARTED",
            targetEntity = "FieldTrip",
            details = "Trip TRP-2026-081 started to Letaba Crushers with vehicle DF 42 LK GP"
        ),
        AuditLogEvent(
            id = "LOG-02",
            timestamp = System.currentTimeMillis() - 14400000,
            operatorName = "Johan Botha",
            action = "TASK_ASSIGNED",
            targetEntity = "AssessmentTask",
            details = "Task TSK-2026-0042 assigned to Nomsa Khumalo"
        ),
        AuditLogEvent(
            id = "LOG-03",
            timestamp = System.currentTimeMillis() - 28800000,
            operatorName = "Sarah Mokoena",
            action = "USER_ROLE_PROVISIONED",
            targetEntity = "UserEntity",
            details = "Role permissions verified for Occupational Hygiene Assistant"
        )
    )
}
