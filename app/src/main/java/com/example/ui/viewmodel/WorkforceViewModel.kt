package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.CustomerSeedData
import com.example.data.local.WorkforceDatabase
import com.example.data.model.ActionPriority
import com.example.data.model.ActionStatus
import com.example.data.model.Assessment
import com.example.data.model.AssessmentStatus
import com.example.data.model.ChecklistQuestion
import com.example.data.model.ChecklistType
import com.example.data.model.ChemicalRecord
import com.example.data.model.CleaningProcedureStatus
import com.example.data.model.CleaningType
import com.example.data.model.ComplianceResponse
import com.example.data.model.Customer
import com.example.data.model.DigitalSignature
import com.example.data.model.Facility
import com.example.data.model.RemedialAction
import com.example.data.model.SectionId
import com.example.data.model.Site
import com.example.data.model.SyncStatus
import com.example.data.model.User
import com.example.data.model.UserRole
import com.example.data.repository.AssessmentRepository
import com.example.data.repository.ChemicalRepository
import com.example.data.repository.CloudSyncManager
import com.example.data.repository.RemedialActionRepository
import com.example.data.repository.SignatureRepository
import com.example.data.repository.UserRepository
import com.example.service.LocationService
import com.example.service.PdfReportService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import com.example.data.model.AssessmentBooking
import com.example.data.model.AssessmentTask
import com.example.data.model.AuditLogEvent
import com.example.data.model.BookingStatus
import com.example.data.model.CustomerMineAllocation
import com.example.data.model.FieldServiceRecord
import com.example.data.model.FieldTrip
import com.example.data.model.LiveGpsLocation
import com.example.data.model.StaffAvailability
import com.example.data.model.StaffMember
import com.example.data.model.StaffNotification
import com.example.data.model.StaffRole
import com.example.data.model.TaskPriority
import com.example.data.model.TaskStatus
import com.example.data.model.TripStatus
import com.example.data.repository.StaffAndOperationsRepository

sealed class AppScreen {
    object Splash : AppScreen()
    object Login : AppScreen()
    object Dashboard : AppScreen()
    object CustomerSiteSelect : AppScreen()
    data class AssessmentWizard(val assessmentId: String) : AppScreen()
    data class InspectionFormScreen(val assessmentId: String? = null) : AppScreen()
    data class ChemicalRegister(val assessmentId: String) : AppScreen()
    data class RemedialActions(val assessmentId: String? = null) : AppScreen()
    data class SignatureCapture(val assessmentId: String) : AppScreen()
    data class ReportPreview(val assessmentId: String) : AppScreen()
    object EquipmentRegister : AppScreen()
    object WebPortalPreview : AppScreen()
    object UserManagement : AppScreen()
    object CustomerManagement : AppScreen()
    object HealthSafetyAudit : AppScreen()
    object StaffAndFieldOps : AppScreen()
    object Settings : AppScreen()
}

class WorkforceViewModel(application: Application) : AndroidViewModel(application) {

    private val db = WorkforceDatabase.getDatabase(application)
    private val assessmentRepo = AssessmentRepository(db)
    private val chemicalRepo = ChemicalRepository(db)
    private val remedialRepo = RemedialActionRepository(db)
    private val signatureRepo = SignatureRepository(db)
    private val userRepo = UserRepository(db)
    val syncManager = CloudSyncManager(db)
    val pdfService = PdfReportService(application)
    val staffOpsRepo = StaffAndOperationsRepository(db)

    // Staff & Field Operations Flows
    val staffList = staffOpsRepo.staffList
    val taskList = staffOpsRepo.taskList
    val bookingList = staffOpsRepo.bookingList
    val allocationsList = staffOpsRepo.allocationsList
    val fieldServicesList = staffOpsRepo.fieldServices
    val fieldTripsList = staffOpsRepo.fieldTrips
    val liveGpsList = staffOpsRepo.liveGpsLocations
    val notificationsList = staffOpsRepo.notifications
    val auditLogsList = staffOpsRepo.auditLogs
    val isLiveGpsActive = staffOpsRepo.isLiveGpsTrackingActive

    // Navigation Backstack
    private val _screenStack = MutableStateFlow<List<AppScreen>>(listOf(AppScreen.Splash))
    val currentScreen: StateFlow<AppScreen> = MutableStateFlow(AppScreen.Splash)

    private val _currentScreenState = MutableStateFlow<AppScreen>(AppScreen.Splash)
    val screen: StateFlow<AppScreen> = _currentScreenState.asStateFlow()

    // Default Seed Users (Full Authorized Personnel List)
    val defaultUsers = listOf(
        User(
            id = "user_zen_admin",
            name = "Zen Administrator",
            email = "zen752316@gmail.com",
            role = UserRole.SYSTEM_ADMIN,
            registrationNumber = "SAIOH ROH-SUPER 001",
            password = "Password123!",
            phone = "+27 82 555 0199",
            department = "Executive Occupational Hygiene Lead"
        ),
        User(
            id = "user_assessor_1",
            name = "Nomvula Khumalo",
            email = "n.khumalo@workforcehealth.co.za",
            role = UserRole.FIELD_ASSESSOR,
            registrationNumber = "SAIOH OH-Asst 1892",
            password = "Password123!",
            phone = "+27 83 200 4819",
            department = "Field Operations & Baseline Surveys"
        ),
        User(
            id = "user_hygienist_1",
            name = "Riaan Steyn (ROH)",
            email = "r.steyn@workforcehealth.co.za",
            role = UserRole.OCCUPATIONAL_HYGIENIST,
            registrationNumber = "SAIOH ROH 0482",
            password = "Password123!",
            phone = "+27 82 911 3320",
            department = "Assessment Instructor / Legal Sign-off"
        ),
        User(
            id = "user_manager_1",
            name = "Pieter Botha (Mine Mgr)",
            email = "p.botha@letabacrushers.co.za",
            role = UserRole.MINE_MANAGEMENT,
            registrationNumber = "DME Cert GCC-2018-99",
            password = "Password123!",
            phone = "+27 15 307 8800",
            department = "Letaba Crushers (Pty) Ltd"
        ),
        User(
            id = "user_admin_1",
            name = "Admin GeekForge",
            email = "sysadmin@workforcehealth.co.za",
            role = UserRole.SYSTEM_ADMIN,
            registrationNumber = "SYSTEM-SUPERUSER",
            password = "Password123!",
            phone = "+27 11 000 9999",
            department = "IT & Digital Health Systems"
        )
    )

    val demoUsers = defaultUsers

    private val _currentUser = MutableStateFlow(defaultUsers[0])
    val currentUser: StateFlow<User> = _currentUser.asStateFlow()

    // Persistent Users from Room Database
    val allUsers: StateFlow<List<User>> = userRepo.getAllUsers()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), defaultUsers)

    // Data streams
    val allAssessments: StateFlow<List<Assessment>> = assessmentRepo.getAllAssessments()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val outstandingRemedialActions: StateFlow<List<RemedialAction>> = remedialRepo.getOutstandingActions()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Active Assessment State
    private val _activeAssessment = MutableStateFlow<Assessment?>(null)
    val activeAssessment: StateFlow<Assessment?> = _activeAssessment.asStateFlow()

    private val _activeQuestions = MutableStateFlow<List<ChecklistQuestion>>(emptyList())
    val activeQuestions: StateFlow<List<ChecklistQuestion>> = _activeQuestions.asStateFlow()

    private val _currentSectionIndex = MutableStateFlow(0)
    val currentSectionIndex: StateFlow<Int> = _currentSectionIndex.asStateFlow()

    private val _chemicalsList = MutableStateFlow<List<ChemicalRecord>>(emptyList())
    val chemicalsList: StateFlow<List<ChemicalRecord>> = _chemicalsList.asStateFlow()

    private val _deepProcedureStatus = MutableStateFlow(CleaningProcedureStatus("", CleaningType.DEEP_CLEANING))
    val deepProcedureStatus: StateFlow<CleaningProcedureStatus> = _deepProcedureStatus.asStateFlow()

    private val _routineProcedureStatus = MutableStateFlow(CleaningProcedureStatus("", CleaningType.ROUTINE_CLEANING))
    val routineProcedureStatus: StateFlow<CleaningProcedureStatus> = _routineProcedureStatus.asStateFlow()

    private val _signaturesList = MutableStateFlow<List<DigitalSignature>>(emptyList())
    val signaturesList: StateFlow<List<DigitalSignature>> = _signaturesList.asStateFlow()

    // Reference data (Observable Customer Accounts & Mine Sites)
    private val _customersList = MutableStateFlow<List<Customer>>(CustomerSeedData.customers)
    val customersList: StateFlow<List<Customer>> = _customersList.asStateFlow()

    private val _sitesList = MutableStateFlow<List<Site>>(CustomerSeedData.sites)
    val sitesList: StateFlow<List<Site>> = _sitesList.asStateFlow()

    private val _facilitiesList = MutableStateFlow<List<Facility>>(CustomerSeedData.facilities)
    val facilitiesList: StateFlow<List<Facility>> = _facilitiesList.asStateFlow()

    val customers: List<Customer> get() = _customersList.value
    val sites: List<Site> get() = _sitesList.value
    val facilities: List<Facility> get() = _facilitiesList.value
    val equipmentList = CustomerSeedData.defaultEquipment

    // Validation & Messages
    private val _snackBarMessage = MutableStateFlow<String?>(null)
    val snackBarMessage: StateFlow<String?> = _snackBarMessage.asStateFlow()

    // PDF Export & Sharing State
    private val _isExportingPdf = MutableStateFlow(false)
    val isExportingPdf: StateFlow<Boolean> = _isExportingPdf.asStateFlow()

    private val _lastExportedPath = MutableStateFlow<String?>(null)
    val lastExportedPath: StateFlow<String?> = _lastExportedPath.asStateFlow()

    init {
        seedInitialUsersAndAssessments()
    }

    private fun seedInitialUsersAndAssessments() {
        viewModelScope.launch {
            userRepo.seedInitialUsersIfEmpty(defaultUsers)
            seedInitialSampleAssessmentIfEmpty()
        }
    }

    private fun seedInitialSampleAssessmentIfEmpty() {
        viewModelScope.launch {
            // Check if there is already an assessment
            val existing = assessmentRepo.getAssessmentById("asm_letaba_demo_01")
            if (existing == null) {
                val demoAssessment = Assessment(
                    id = "asm_letaba_demo_01",
                    referenceNumber = "WFH-OH-2026-0042",
                    customerId = "cust_letaba",
                    customerName = "Letaba Crushers (Pty) Ltd",
                    siteId = "site_letaba_tzaneen",
                    siteName = "Letaba Crushers - Main Quarry & Plant",
                    facilityId = "fac_letaba_male_ch",
                    facilityName = "Main Change House — Male",
                    checklistType = ChecklistType.MALE_ABLUTION,
                    assessorId = demoUsers[0].id,
                    assessorName = demoUsers[0].name,
                    assessorRole = demoUsers[0].role.displayName,
                    assessmentDate = "2026-10-29",
                    assessmentTime = "09:30",
                    previousVisitDate = "2026-09-18",
                    previousRemedialActionsNotes = "Shower door hinges repaired on cubicle 3; hand wash posters replaced.",
                    hygienistSpecialInstructions = "Verify deep sanitisation certificate from approved contractor and verify SDS for degreasers.",
                    status = AssessmentStatus.IN_PROGRESS,
                    syncStatus = SyncStatus.PENDING_SYNC,
                    totalQuestions = 51,
                    answeredCount = 14,
                    nonCompliantCount = 2,
                    complianceScore = 85
                )
                assessmentRepo.createAssessment(demoAssessment)

                // Add sample chemicals
                chemicalRepo.addChemical(
                    ChemicalRecord(
                        id = "chem_deep_01",
                        assessmentId = demoAssessment.id,
                        cleaningType = CleaningType.DEEP_CLEANING,
                        chemicalName = "SaniCide 500 Disinfectant & Fungicide",
                        manufacturerOrSupplier = "Ecolab South Africa",
                        sdsReturned = true,
                        sdsDocumentRef = "SDS-ECOLAB-SAN-2025.pdf",
                        usageNotes = "Used in 1:50 dilution for fogging and deep pressure cleaning."
                    )
                )
                chemicalRepo.addChemical(
                    ChemicalRecord(
                        id = "chem_routine_01",
                        assessmentId = demoAssessment.id,
                        cleaningType = CleaningType.ROUTINE_CLEANING,
                        chemicalName = "ActiClean Pine Industrial Floor Gel",
                        manufacturerOrSupplier = "JohnsonDiversey SA",
                        sdsReturned = true,
                        sdsDocumentRef = "SDS-JD-PINE-992.pdf",
                        usageNotes = "Daily floor mopping twice per shift."
                    )
                )

                // Add a sample remedial action
                remedialRepo.createAction(
                    RemedialAction(
                        id = "rem_01_letaba",
                        actionReference = "REM-2026-011",
                        assessmentId = demoAssessment.id,
                        questionId = "Q_B_5_asm_letaba_demo_01",
                        questionNumber = 5,
                        sectionTitle = "SECTION B — SHOWERS",
                        findingDescription = "Traces of black mildew observed along grout on shower 4 rear wall.",
                        recommendedAction = "Apply anti-fungal bleach treatment and re-seal tile joints with anti-mildew silicone.",
                        responsiblePersonOrDept = "Plant Maintenance / Johan Steenkamp",
                        targetDate = "2026-11-05",
                        priority = ActionPriority.HIGH,
                        status = ActionStatus.OPEN
                    )
                )
            }
        }
    }

    fun navigateTo(screen: AppScreen) {
        _screenStack.value = _screenStack.value + screen
        _currentScreenState.value = screen
    }

    fun navigateBack(): Boolean {
        if (_screenStack.value.size > 1) {
            val newStack = _screenStack.value.dropLast(1)
            _screenStack.value = newStack
            _currentScreenState.value = newStack.last()
            return true
        }
        return false
    }

    fun switchUser(user: User) {
        _currentUser.value = user
        showNotification("Active user switched to ${user.name} (${user.role.displayName})")
    }

    fun loginWithEmailPassword(
        email: String,
        password: String,
        onResult: (success: Boolean, message: String?) -> Unit
    ) {
        viewModelScope.launch {
            val cleanEmail = email.trim().lowercase()
            val cleanPass = password.trim()
            if (cleanEmail.isBlank() || cleanPass.isBlank()) {
                onResult(false, "Please provide both email address and password.")
                return@launch
            }

            val user = userRepo.authenticate(cleanEmail, cleanPass)
            if (user != null) {
                _currentUser.value = user
                showNotification("Welcome back, ${user.name} (${user.role.displayName})")
                onResult(true, null)
            } else {
                // Check if user exists but password mismatch or deactivated
                val userFound = userRepo.getUserByEmail(cleanEmail)
                if (userFound != null) {
                    if (!userFound.isActive) {
                        onResult(false, "This user account has been deactivated. Please contact your system administrator.")
                    } else {
                        onResult(false, "Incorrect password entered for $cleanEmail. (Default: Password123!)")
                    }
                } else {
                    onResult(false, "No authorized user found with email '$cleanEmail'. You can register or choose from authorized personnel below.")
                }
            }
        }
    }

    fun saveOrUpdateUser(
        user: User,
        isNew: Boolean,
        onResult: (success: Boolean, message: String?) -> Unit
    ) {
        viewModelScope.launch {
            val cleanEmail = user.email.trim().lowercase()
            if (user.name.isBlank()) {
                onResult(false, "Full Name is required.")
                return@launch
            }
            if (cleanEmail.isBlank() || !cleanEmail.contains("@")) {
                onResult(false, "Please provide a valid email address.")
                return@launch
            }
            if (user.password.length < 4) {
                onResult(false, "Password must be at least 4 characters.")
                return@launch
            }

            if (isNew) {
                val existing = userRepo.getUserByEmail(cleanEmail)
                if (existing != null) {
                    onResult(false, "An authorized user with email '$cleanEmail' already exists.")
                    return@launch
                }
            }

            val userToSave = user.copy(
                email = cleanEmail,
                name = user.name.trim(),
                registrationNumber = user.registrationNumber.trim(),
                phone = user.phone.trim(),
                department = user.department.trim()
            )

            userRepo.saveUser(userToSave)

            // If we updated the currently logged in user, refresh _currentUser
            if (_currentUser.value.id == userToSave.id) {
                _currentUser.value = userToSave
            }

            val msg = if (isNew) "User '${userToSave.name}' created as ${userToSave.role.displayName}" else "User '${userToSave.name}' profile updated"
            showNotification(msg)
            onResult(true, null)
        }
    }

    fun deleteUser(
        userId: String,
        onResult: (success: Boolean, message: String?) -> Unit
    ) {
        viewModelScope.launch {
            val usersList = allUsers.value
            if (usersList.size <= 1) {
                onResult(false, "Cannot delete the only remaining user in the system.")
                return@launch
            }
            if (_currentUser.value.id == userId) {
                onResult(false, "You cannot delete the account you are currently logged in with. Please switch to another account first.")
                return@launch
            }
            userRepo.deleteUser(userId)
            showNotification("Authorized user deleted.")
            onResult(true, null)
        }
    }

    fun logout() {
        showNotification("Signed out of session.")
        _screenStack.value = listOf(AppScreen.Login)
        _currentScreenState.value = AppScreen.Login
    }

    // Customer Accounts & Mine Sites CRUD Operations
    fun saveOrUpdateCustomer(
        customer: Customer,
        isNew: Boolean,
        onResult: (success: Boolean, message: String?) -> Unit
    ) {
        if (customer.name.isBlank()) {
            onResult(false, "Registered Entity Name is required.")
            return
        }
        val currentList = _customersList.value.toMutableList()
        if (isNew) {
            val exists = currentList.any { it.name.equals(customer.name.trim(), ignoreCase = true) || (customer.registrationNumber.isNotBlank() && it.registrationNumber.equals(customer.registrationNumber.trim(), ignoreCase = true)) }
            if (exists) {
                onResult(false, "A customer account with this name or registration number already exists.")
                return
            }
            currentList.add(0, customer)
            _customersList.value = currentList
            showNotification("Customer Account '${customer.name}' created.")
            onResult(true, null)
        } else {
            val idx = currentList.indexOfFirst { it.id == customer.id }
            if (idx != -1) {
                currentList[idx] = customer
                _customersList.value = currentList
                showNotification("Customer Account '${customer.name}' updated.")
                onResult(true, null)
            } else {
                currentList.add(customer)
                _customersList.value = currentList
                showNotification("Customer Account saved.")
                onResult(true, null)
            }
        }
    }

    fun deleteCustomer(
        customerId: String,
        onResult: (success: Boolean, message: String?) -> Unit
    ) {
        val currentList = _customersList.value.toMutableList()
        if (currentList.size <= 1) {
            onResult(false, "Cannot delete the only customer account in the system.")
            return
        }
        val removed = currentList.removeAll { it.id == customerId }
        if (removed) {
            _customersList.value = currentList
            // Cascade delete associated sites and facilities
            val sitesToDelete = _sitesList.value.filter { it.customerId == customerId }.map { it.id }
            _sitesList.value = _sitesList.value.filter { it.customerId != customerId }
            _facilitiesList.value = _facilitiesList.value.filter { it.siteId !in sitesToDelete }
            showNotification("Customer account and associated properties deleted.")
            onResult(true, null)
        } else {
            onResult(false, "Customer account not found.")
        }
    }

    fun saveOrUpdateSite(
        site: Site,
        isNew: Boolean,
        onResult: (success: Boolean, message: String?) -> Unit
    ) {
        if (site.siteName.isBlank()) {
            onResult(false, "Site Property Name is required.")
            return
        }
        val currentList = _sitesList.value.toMutableList()
        if (isNew) {
            currentList.add(0, site)
            _sitesList.value = currentList
            showNotification("Mine Site Property '${site.siteName}' created.")
            onResult(true, null)
        } else {
            val idx = currentList.indexOfFirst { it.id == site.id }
            if (idx != -1) {
                currentList[idx] = site
                _sitesList.value = currentList
                showNotification("Mine Site Property '${site.siteName}' updated.")
                onResult(true, null)
            } else {
                currentList.add(site)
                _sitesList.value = currentList
                showNotification("Mine Site Property saved.")
                onResult(true, null)
            }
        }
    }

    fun deleteSite(
        siteId: String,
        onResult: (success: Boolean, message: String?) -> Unit
    ) {
        val currentList = _sitesList.value.toMutableList()
        val removed = currentList.removeAll { it.id == siteId }
        if (removed) {
            _sitesList.value = currentList
            _facilitiesList.value = _facilitiesList.value.filter { it.siteId != siteId }
            showNotification("Mine Site Property deleted.")
            onResult(true, null)
        } else {
            onResult(false, "Site property not found.")
        }
    }

    fun saveOrUpdateFacility(
        facility: Facility,
        isNew: Boolean,
        onResult: (success: Boolean, message: String?) -> Unit
    ) {
        if (facility.name.isBlank()) {
            onResult(false, "Facility Name is required.")
            return
        }
        val currentList = _facilitiesList.value.toMutableList()
        if (isNew) {
            currentList.add(0, facility)
            _facilitiesList.value = currentList
            showNotification("Ablution facility '${facility.name}' registered.")
            onResult(true, null)
        } else {
            val idx = currentList.indexOfFirst { it.id == facility.id }
            if (idx != -1) {
                currentList[idx] = facility
                _facilitiesList.value = currentList
                showNotification("Ablution facility '${facility.name}' updated.")
                onResult(true, null)
            } else {
                currentList.add(facility)
                _facilitiesList.value = currentList
                showNotification("Ablution facility saved.")
                onResult(true, null)
            }
        }
    }

    fun deleteFacility(
        facilityId: String,
        onResult: (success: Boolean, message: String?) -> Unit
    ) {
        val currentList = _facilitiesList.value.toMutableList()
        val removed = currentList.removeAll { it.id == facilityId }
        if (removed) {
            _facilitiesList.value = currentList
            showNotification("Ablution facility removed.")
            onResult(true, null)
        } else {
            onResult(false, "Ablution facility not found.")
        }
    }


    fun startNewAssessment(
        customer: Customer,
        site: Site,
        facility: Facility
    ) {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val now = Date()
        val randomSuffix = (1000..9999).random()

        val newAssessment = Assessment(
            id = "asm_${UUID.randomUUID().toString().take(8)}",
            referenceNumber = "WFH-OH-${SimpleDateFormat("yyyyMM", Locale.getDefault()).format(now)}-$randomSuffix",
            customerId = customer.id,
            customerName = customer.name,
            siteId = site.id,
            siteName = site.siteName,
            facilityId = facility.id,
            facilityName = facility.name,
            checklistType = facility.checklistType,
            assessorId = _currentUser.value.id,
            assessorName = _currentUser.value.name,
            assessorRole = _currentUser.value.role.displayName,
            assessmentDate = dateFormat.format(now),
            assessmentTime = timeFormat.format(now),
            previousVisitDate = facility.lastAssessmentDate,
            status = AssessmentStatus.DRAFT,
            syncStatus = SyncStatus.OFFLINE
        )

        viewModelScope.launch {
            assessmentRepo.createAssessment(newAssessment)
            loadAssessment(newAssessment.id)
            navigateTo(AppScreen.AssessmentWizard(newAssessment.id))
        }
    }

    fun loadAssessment(assessmentId: String) {
        viewModelScope.launch {
            val a = assessmentRepo.getAssessmentById(assessmentId)
            _activeAssessment.value = a
            _currentSectionIndex.value = 0

            // Observe questions
            launch {
                assessmentRepo.getQuestionsForAssessment(assessmentId).collect {
                    _activeQuestions.value = it
                }
            }
            // Observe chemicals
            launch {
                chemicalRepo.getChemicals(assessmentId).collect {
                    _chemicalsList.value = it
                }
            }
            // Observe procedure statuses
            launch {
                chemicalRepo.getProcedureStatuses(assessmentId).collect { list ->
                    list.find { it.cleaningType == CleaningType.DEEP_CLEANING }?.let { _deepProcedureStatus.value = it }
                    list.find { it.cleaningType == CleaningType.ROUTINE_CLEANING }?.let { _routineProcedureStatus.value = it }
                }
            }
            // Observe signatures
            launch {
                signatureRepo.getSignatures(assessmentId).collect {
                    _signaturesList.value = it
                }
            }
        }
    }

    fun setSectionIndex(index: Int) {
        if (index in 0..6) {
            _currentSectionIndex.value = index
        }
    }

    fun nextSection() {
        if (_currentSectionIndex.value < 6) {
            _currentSectionIndex.value += 1
        }
    }

    fun previousSection() {
        if (_currentSectionIndex.value > 0) {
            _currentSectionIndex.value -= 1
        }
    }

    fun answerQuestion(
        questionId: String,
        response: ComplianceResponse,
        remarks: String = ""
    ) {
        val q = _activeQuestions.value.find { it.id == questionId } ?: return
        val updated = q.copy(
            response = response,
            remarks = if (remarks.isNotEmpty()) remarks else q.remarks
        )
        viewModelScope.launch {
            assessmentRepo.updateQuestion(updated)
            // Reload active assessment stats
            _activeAssessment.value = assessmentRepo.getAssessmentById(q.assessmentId)
        }
    }

    fun updateQuestionRemarks(questionId: String, remarks: String) {
        val q = _activeQuestions.value.find { it.id == questionId } ?: return
        viewModelScope.launch {
            assessmentRepo.updateQuestion(q.copy(remarks = remarks))
        }
    }

    fun attachPhotoToQuestion(
        questionId: String,
        uriString: String,
        caption: String,
        lat: Double? = null,
        lon: Double? = null
    ) {
        val q = _activeQuestions.value.find { it.id == questionId } ?: return
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        val updated = q.copy(
            photoUri = uriString,
            photoTimestamp = dateFormat.format(Date()),
            photoCaption = caption,
            geoLatitude = lat ?: q.geoLatitude,
            geoLongitude = lon ?: q.geoLongitude
        )
        viewModelScope.launch {
            assessmentRepo.updateQuestion(updated)
            showNotification("Photo evidence attached to ${q.sectionId.name.removePrefix("SECTION_")}.Q${q.questionNumber}")
        }
    }

    fun stampGeoCoordinates(
        questionId: String,
        lat: Double,
        lon: Double,
        accuracy: Float? = null
    ) {
        val q = _activeQuestions.value.find { it.id == questionId } ?: return
        val timeStamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
        viewModelScope.launch {
            assessmentRepo.updateQuestion(
                q.copy(
                    geoLatitude = lat,
                    geoLongitude = lon,
                    geoAccuracy = accuracy ?: q.geoAccuracy ?: 5.0f,
                    geoTimestamp = timeStamp
                )
            )
            showNotification("GPS Recorded: ${String.format(Locale.US, "%.5f, %.5f", lat, lon)}")
        }
    }

    fun acquireDeviceGpsForQuestion(questionId: String) {
        val loc = LocationService.getCurrentGpsLocation(getApplication())
        stampGeoCoordinates(questionId, loc.latitude, loc.longitude, loc.accuracyMeters)
    }

    fun updateQuestionRemarkWithDetails(
        questionId: String,
        remarks: String,
        inputMode: String = "TYPED",
        lat: Double? = null,
        lon: Double? = null,
        accuracy: Float? = null
    ) {
        val q = _activeQuestions.value.find { it.id == questionId } ?: return
        val timeStamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
        viewModelScope.launch {
            assessmentRepo.updateQuestion(
                q.copy(
                    remarks = remarks,
                    remarkInputMode = inputMode,
                    geoLatitude = lat ?: q.geoLatitude,
                    geoLongitude = lon ?: q.geoLongitude,
                    geoAccuracy = accuracy ?: q.geoAccuracy,
                    geoTimestamp = if (lat != null) timeStamp else q.geoTimestamp
                )
            )
        }
    }

    fun createRemedialAction(
        question: ChecklistQuestion,
        description: String,
        action: String,
        responsible: String,
        targetDate: String,
        priority: ActionPriority
    ) {
        val randomSuffix = (100..999).random()
        val remedial = RemedialAction(
            id = "rem_${UUID.randomUUID().toString().take(8)}",
            actionReference = "REM-2026-$randomSuffix",
            assessmentId = question.assessmentId,
            questionId = question.id,
            questionNumber = question.questionNumber,
            sectionTitle = question.sectionId.title,
            findingDescription = description,
            recommendedAction = action,
            responsiblePersonOrDept = responsible,
            targetDate = targetDate,
            priority = priority,
            photoUri = question.photoUri
        )
        viewModelScope.launch {
            remedialRepo.createAction(remedial)
            assessmentRepo.updateQuestion(question.copy(hasRemedialAction = true, correctiveActionNotes = action))
            showNotification("Remedial Action ${remedial.actionReference} logged!")
        }
    }

    fun updateRemedialAction(action: RemedialAction) {
        viewModelScope.launch {
            remedialRepo.createAction(action)
        }
    }

    fun addChemical(
        cleaningType: CleaningType,
        name: String,
        supplier: String,
        sdsReturned: Boolean,
        notes: String
    ) {
        val current = _activeAssessment.value ?: return
        val chem = ChemicalRecord(
            id = "chem_${UUID.randomUUID().toString().take(8)}",
            assessmentId = current.id,
            cleaningType = cleaningType,
            chemicalName = name,
            manufacturerOrSupplier = supplier,
            sdsReturned = sdsReturned,
            sdsDocumentRef = if (sdsReturned) "SDS-$name-Verified.pdf" else "Missing SDS",
            usageNotes = notes
        )
        viewModelScope.launch {
            chemicalRepo.addChemical(chem)
            showNotification("Chemical '$name' added to register")
        }
    }

    fun updateProcedureStatus(
        cleaningType: CleaningType,
        obtained: Boolean,
        returned: Boolean,
        contractor: String,
        remarks: String
    ) {
        val current = _activeAssessment.value ?: return
        val status = CleaningProcedureStatus(
            assessmentId = current.id,
            cleaningType = cleaningType,
            copyOfProcedureObtained = obtained,
            copyOfProcedureReturned = returned,
            contractorOrServiceOrg = contractor,
            remarks = remarks
        )
        viewModelScope.launch {
            chemicalRepo.saveProcedureStatus(status)
            if (cleaningType == CleaningType.DEEP_CLEANING) {
                _deepProcedureStatus.value = status
            } else {
                _routineProcedureStatus.value = status
            }
            showNotification("Procedure documentation status saved")
        }
    }

    fun saveSignature(role: String, name: String, designation: String, signatureData: String) {
        val current = _activeAssessment.value ?: return
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val now = Date()

        val signature = DigitalSignature(
            id = "sig_${UUID.randomUUID().toString().take(8)}",
            assessmentId = current.id,
            signerRole = role,
            signerName = name,
            signerDesignation = designation,
            signaturePath = signatureData,
            signedDate = dateFormat.format(now),
            signedTime = timeFormat.format(now),
            declarationAccepted = true
        )

        viewModelScope.launch {
            signatureRepo.saveSignature(signature)
            showNotification("Digital signature captured for $name ($role)")
        }
    }

    fun submitAssessment(): Boolean {
        val current = _activeAssessment.value ?: return false
        val unanswered = _activeQuestions.value.filter { it.isMandatory && it.response == ComplianceResponse.NOT_ASSESSED }
        if (unanswered.isNotEmpty()) {
            showNotification("Cannot submit: ${unanswered.size} required questions unanswered!")
            return false
        }

        // Compulsory answer by remarks or dictation for all questions across all sections
        val missingRemarks = _activeQuestions.value.filter { it.remarks.isBlank() }
        if (missingRemarks.isNotEmpty()) {
            showNotification("Cannot submit: ${missingRemarks.size} questions require a compulsory remark or voice dictation!")
            return false
        }

        viewModelScope.launch {
            val ok = assessmentRepo.submitForReview(current.id)
            if (ok) {
                _activeAssessment.value = assessmentRepo.getAssessmentById(current.id)
                showNotification("Assessment submitted for Occupational Hygienist review!")
            }
        }
        return true
    }

    /**
     * Auto-populates compulsory remarks for questions that do not have remarks yet.
     * Useful for rapid auditing and testing compliance.
     */
    fun autofillCompulsoryRemarks(defaultRemark: String = "Compliant & in good order as inspected on site.") {
        val current = _activeAssessment.value ?: return
        viewModelScope.launch {
            var updatedCount = 0
            val updatedList = _activeQuestions.value.map { q ->
                if (q.remarks.isBlank()) {
                    updatedCount++
                    val remarkText = if (q.isDefectOrNonCompliant) {
                        "Non-conformance identified during inspection. Rectification logged."
                    } else {
                        defaultRemark
                    }
                    val updatedQ = q.copy(
                        remarks = remarkText,
                        remarkInputMode = "TYPED"
                    )
                    assessmentRepo.updateQuestion(updatedQ)
                    updatedQ
                } else {
                    q
                }
            }
            _activeQuestions.value = updatedList
            showNotification("Updated $updatedCount question(s) with compulsory remarks.")
        }
    }


    fun approveAssessment(comments: String) {
        val current = _activeAssessment.value ?: return
        viewModelScope.launch {
            assessmentRepo.approveAssessment(current.id, _currentUser.value.name, comments)
            _activeAssessment.value = assessmentRepo.getAssessmentById(current.id)
            showNotification("Assessment approved by ${_currentUser.value.name}")
        }
    }

    fun returnAssessment(comments: String) {
        val current = _activeAssessment.value ?: return
        viewModelScope.launch {
            assessmentRepo.returnForCorrection(current.id, _currentUser.value.name, comments)
            _activeAssessment.value = assessmentRepo.getAssessmentById(current.id)
            showNotification("Assessment returned for correction")
        }
    }

    fun syncCloud() {
        viewModelScope.launch {
            val success = syncManager.triggerManualSync()
            if (success) {
                showNotification("Cloud sync completed. Data updated.")
            } else {
                showNotification("Sync offline: Check network connectivity.")
            }
        }
    }

    fun toggleOfflineMode() {
        viewModelScope.launch {
            syncManager.toggleNetworkConnectivity()
            if (syncManager.isOnline.value) {
                showNotification("Online: Restoring connectivity and uploading queued items...")
            } else {
                showNotification("Offline Mode: Field records queued locally in Room DB")
            }
        }
    }

    fun syncToWordPressNew(
        assessment: Assessment? = null,
        onComplete: ((com.example.data.repository.WordPressSyncReceipt) -> Unit)? = null
    ) {
        viewModelScope.launch {
            val target = assessment ?: _activeAssessment.value
            val questions = _activeQuestions.value
            val actions = if (target != null) remedialRepo.getActionsForAssessmentSync(target.id) else emptyList()
            val score = target?.complianceScore?.toDouble() ?: 85.0
            val facility = target?.facilityName ?: "Letaba Crushers - Main Ablution"
            val receipt = syncManager.syncDirectToWordPressNew(
                assessment = target,
                checklistAnswers = questions,
                remedialActions = actions,
                calculatedScore = score,
                facilityName = facility
            )
            showNotification("WordPress Sync (https://wfhsa.com/new/): ${receipt.message.take(65)}")
            onComplete?.invoke(receipt)
        }
    }

    suspend fun obtainPdfFile(target: Assessment? = null): File {
        val current = target ?: _activeAssessment.value
            ?: throw IllegalStateException("No active inspection assessment found")

        val questions = if (target != null && target.id != _activeAssessment.value?.id) {
            assessmentRepo.getQuestionsForAssessment(target.id).firstOrNull() ?: emptyList()
        } else {
            _activeQuestions.value.ifEmpty {
                assessmentRepo.getQuestionsForAssessment(current.id).firstOrNull() ?: emptyList()
            }
        }

        val chemicals = if (target != null && target.id != _activeAssessment.value?.id) {
            chemicalRepo.getChemicals(target.id).firstOrNull() ?: emptyList()
        } else {
            _chemicalsList.value.ifEmpty {
                chemicalRepo.getChemicals(current.id).firstOrNull() ?: emptyList()
            }
        }

        val deepProc = _deepProcedureStatus.value
        val routineProc = _routineProcedureStatus.value
        val sigs = _signaturesList.value
        val actions = remedialRepo.getActionsForAssessmentSync(current.id)

        return pdfService.generateInspectionPdf(
            assessment = current,
            questions = questions,
            chemicals = chemicals,
            deepProcedure = deepProc,
            routineProcedure = routineProc,
            signatures = sigs,
            remedialActions = actions
        )
    }

    fun savePdfToPublicDownloads(target: Assessment? = null, onSaved: ((File) -> Unit)? = null) {
        viewModelScope.launch {
            try {
                _isExportingPdf.value = true
                showNotification("Generating & saving structured PDF document...")
                val file = obtainPdfFile(target)
                val result = pdfService.savePdfToPublicDownloads(file)
                result.onSuccess { savedFile ->
                    _lastExportedPath.value = savedFile.absolutePath
                    showNotification("✓ PDF Saved locally to Downloads/WorkForceHealthSA/${savedFile.name}")
                    onSaved?.invoke(savedFile)
                }.onFailure { err ->
                    showNotification("Save Failed: ${err.message}. File kept in app Documents.")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                showNotification("Export Error: ${e.message}")
            } finally {
                _isExportingPdf.value = false
            }
        }
    }

    fun sharePdfReport(target: Assessment? = null) {
        viewModelScope.launch {
            try {
                _isExportingPdf.value = true
                showNotification("Preparing PDF report for sharing...")
                val file = obtainPdfFile(target)
                pdfService.sharePdf(file)
                showNotification("PDF shared: ${file.name}")
            } catch (e: Exception) {
                e.printStackTrace()
                showNotification("Share Error: ${e.message}")
            } finally {
                _isExportingPdf.value = false
            }
        }
    }

    fun openPdfReport(target: Assessment? = null) {
        viewModelScope.launch {
            try {
                _isExportingPdf.value = true
                showNotification("Opening structured PDF document...")
                val file = obtainPdfFile(target)
                pdfService.openPdf(file)
            } catch (e: Exception) {
                e.printStackTrace()
                showNotification("Open Error: ${e.message}")
            } finally {
                _isExportingPdf.value = false
            }
        }
    }

    fun printPdfReport(target: Assessment? = null) {
        viewModelScope.launch {
            try {
                _isExportingPdf.value = true
                showNotification("Sending PDF report to print spooler...")
                val file = obtainPdfFile(target)
                pdfService.printPdf(file)
            } catch (e: Exception) {
                e.printStackTrace()
                showNotification("Print Error: ${e.message}")
            } finally {
                _isExportingPdf.value = false
            }
        }
    }

    fun generatePdfReport(openAfter: Boolean = true, shareAfter: Boolean = false) {
        val current = _activeAssessment.value ?: return
        viewModelScope.launch {
            try {
                _isExportingPdf.value = true
                showNotification("Generating professional PDF inspection report...")
                val file = obtainPdfFile(current)
                showNotification("PDF Report Generated: ${file.name}")

                if (shareAfter) {
                    pdfService.sharePdf(file)
                } else if (openAfter) {
                    pdfService.openPdf(file)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                showNotification("PDF Generation Error: ${e.message}")
            } finally {
                _isExportingPdf.value = false
            }
        }
    }

    fun showNotification(message: String) {
        _snackBarMessage.value = message
    }

    fun clearNotification() {
        _snackBarMessage.value = null
    }

    // --- STAFF & FIELD OPERATIONS ACTIONS ---
    fun toggleLiveGpsTracking(): Boolean {
        val active = staffOpsRepo.toggleLiveGpsTracking()
        showNotification(if (active) "Real-time GPS tracking active on device." else "GPS tracking stopped.")
        return active
    }

    fun addOrUpdateStaff(staff: StaffMember) {
        staffOpsRepo.addOrUpdateStaff(staff)
        showNotification("Staff profile for ${staff.fullName} saved.")
    }

    fun deactivateStaff(staffId: String) {
        staffOpsRepo.deactivateStaff(staffId)
        showNotification("Staff profile deactivated.")
    }

    fun createTask(task: AssessmentTask) {
        staffOpsRepo.createTask(task)
        showNotification("Task ${task.referenceNumber} assigned to ${task.assignedStaffName}.")
    }

    fun updateTaskStatus(taskId: String, status: TaskStatus, notes: String = "") {
        staffOpsRepo.updateTaskStatus(taskId, status, notes)
        showNotification("Task status updated to ${status.label}.")
    }

    fun reassignTask(taskId: String, newStaffId: String, newStaffName: String, notes: String = "") {
        staffOpsRepo.reassignTask(taskId, newStaffId, newStaffName, notes)
        showNotification("Task successfully reassigned to $newStaffName.")
    }

    fun createBooking(booking: AssessmentBooking): Boolean {
        val noConflict = staffOpsRepo.createBooking(booking)
        if (noConflict) {
            showNotification("Assessment visit booked for ${booking.bookingDate}.")
        } else {
            showNotification("Warning: Schedule conflict detected for ${booking.assignedAssessorName}!")
        }
        return noConflict
    }

    fun updateBookingStatus(bookingId: String, status: BookingStatus) {
        staffOpsRepo.updateBookingStatus(bookingId, status)
        showNotification("Booking status set to ${status.label}.")
    }

    fun allocateCustomerMine(allocation: CustomerMineAllocation) {
        staffOpsRepo.allocateCustomerMine(allocation)
        showNotification("Assigned ${allocation.assessorName} to ${allocation.siteName}.")
    }

    fun startFieldTrip(
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
        val trip = staffOpsRepo.startTrip(
            assessorId, assessorName, customerName, mineSiteName, destinationAddress,
            startOdo, startLat, startLng, vehicleReg, notes
        )
        showNotification("Field Trip ${trip.id} started. GPS tracking active.")
        return trip
    }

    fun endFieldTrip(
        tripId: String,
        endOdo: Int?,
        endLat: Double,
        endLng: Double,
        notes: String = ""
    ): FieldTrip? {
        val trip = staffOpsRepo.endTrip(tripId, endOdo, endLat, endLng, notes)
        if (trip != null) {
            showNotification("Field Trip completed: ${trip.totalDistanceKm} km logged.")
        }
        return trip
    }

    fun markNotificationAsRead(id: String) {
        staffOpsRepo.markNotificationAsRead(id)
    }

    fun clearAllNotifications(staffId: String) {
        staffOpsRepo.clearAllNotifications(staffId)
        showNotification("Notifications cleared.")
    }
}
