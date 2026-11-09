package com.example.data.remote

import com.example.data.model.Assessment
import com.example.data.model.ChecklistQuestion
import com.example.data.model.ChemicalRecord
import com.example.data.model.Customer
import com.example.data.model.Site
import com.example.data.model.RemedialAction
import com.example.data.model.DigitalSignature
import com.example.data.model.StaffMember
import com.example.data.model.AssessmentTask
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

// --- AUTHENTICATION ---
data class LoginRequest(
    val email: String,
    val password: String,
    val deviceFingerprint: String = "Android-WFHSA"
)

data class LoginResponse(
    val token: String,
    val refreshToken: String? = null,
    val user: RemoteUser? = null,
    val expiresAt: Long? = null
)

data class RemoteUser(
    val id: String,
    val email: String,
    val fullName: String,
    val role: String,
    val registrationNumber: String? = null,
    val organizationId: String? = null
)

// --- SYNC BATCH ---
data class SyncBatchRequest(
    val deviceId: String,
    val assessorId: String,
    val assessments: List<Assessment> = emptyList(),
    val checklistAnswers: List<ChecklistQuestion> = emptyList(),
    val remedialActions: List<RemedialAction> = emptyList(),
    val chemicals: List<ChemicalRecord> = emptyList(),
    val signatures: List<DigitalSignature> = emptyList(),
    val staffProfiles: List<StaffMember> = emptyList(),
    val tasks: List<AssessmentTask> = emptyList(),
    val timestamp: Long = System.currentTimeMillis()
)

data class SyncBatchResponse(
    val success: Boolean,
    val message: String,
    val syncedCount: Int = 0,
    val serverTimestamp: Long = System.currentTimeMillis(),
    val serverStatus: String = "OK",
    val customerUpdates: List<Customer> = emptyList(),
    val siteUpdates: List<Site> = emptyList(),
    val taskUpdates: List<AssessmentTask> = emptyList(),
    val staffUpdates: List<StaffMember> = emptyList()
)

data class SyncChangesResponse(
    val serverTimestamp: Long,
    val customers: List<Customer> = emptyList(),
    val sites: List<Site> = emptyList(),
    val tasks: List<AssessmentTask> = emptyList(),
    val staff: List<StaffMember> = emptyList(),
    val approvedAssessments: List<String> = emptyList(),
    val rejectedAssessments: Map<String, String> = emptyMap()
)

data class WfhsaHealthResponse(
    val status: String,
    val platform: String = "WORKFORCE HEALTH SA Central Office",
    val version: String = "2.0.0",
    val endpoint: String = "https://workforce-health-sa-m28gog.v2.appdeploy.ai/",
    val databaseConnected: Boolean = true
)

interface WfhsaApiService {

    // Central Office Server Health & Status
    @GET("api/health")
    suspend fun checkHealth(
        @Header("X-API-Key") apiKey: String? = null
    ): Response<WfhsaHealthResponse>

    // Authentication
    @POST("api/auth/login")
    suspend fun login(
        @Body request: LoginRequest,
        @Header("X-API-Key") apiKey: String? = null
    ): Response<LoginResponse>

    // Synchronisation Push & Pull
    @POST("api/sync/push")
    suspend fun pushSyncBatch(
        @Body request: SyncBatchRequest,
        @Header("Authorization") authHeader: String? = null,
        @Header("X-API-Key") apiKey: String? = null
    ): Response<SyncBatchResponse>

    @GET("api/sync/changes")
    suspend fun getSyncChanges(
        @Query("since") sinceTimestamp: Long,
        @Header("Authorization") authHeader: String? = null,
        @Header("X-API-Key") apiKey: String? = null
    ): Response<SyncChangesResponse>

    // Customers
    @GET("api/customers")
    suspend fun getCustomers(
        @Header("X-API-Key") apiKey: String? = null
    ): Response<List<Customer>>

    @POST("api/customers")
    suspend fun createCustomer(
        @Body customer: Customer,
        @Header("X-API-Key") apiKey: String? = null
    ): Response<Customer>

    // Assessments & Reports
    @GET("api/assessments")
    suspend fun getAssessments(
        @Query("customer_id") customerId: String? = null,
        @Query("status") status: String? = null,
        @Header("X-API-Key") apiKey: String? = null
    ): Response<List<Assessment>>

    @POST("api/assessments")
    suspend fun submitAssessment(
        @Body assessment: Assessment,
        @Header("X-API-Key") apiKey: String? = null
    ): Response<Assessment>

    @POST("api/reports/{id}/submit")
    suspend fun submitReport(
        @Path("id") assessmentId: String,
        @Body payload: Map<String, String>,
        @Header("X-API-Key") apiKey: String? = null
    ): Response<Map<String, Any>>

    @POST("api/assessments/{id}/approve")
    suspend fun approveAssessment(
        @Path("id") assessmentId: String,
        @Body approvalData: Map<String, String>,
        @Header("X-API-Key") apiKey: String? = null
    ): Response<Map<String, Any>>

    // Staff & Tasks
    @GET("api/staff")
    suspend fun getStaffProfiles(
        @Header("X-API-Key") apiKey: String? = null
    ): Response<List<StaffMember>>

    @POST("api/staff")
    suspend fun createOrUpdateStaff(
        @Body staff: StaffMember,
        @Header("X-API-Key") apiKey: String? = null
    ): Response<StaffMember>

    @GET("api/tasks")
    suspend fun getTasks(
        @Query("staff_id") staffId: String? = null,
        @Header("X-API-Key") apiKey: String? = null
    ): Response<List<AssessmentTask>>

    @POST("api/tasks")
    suspend fun createTask(
        @Body task: AssessmentTask,
        @Header("X-API-Key") apiKey: String? = null
    ): Response<AssessmentTask>

    @PUT("api/tasks/{id}")
    suspend fun updateTask(
        @Path("id") taskId: String,
        @Body task: AssessmentTask,
        @Header("X-API-Key") apiKey: String? = null
    ): Response<AssessmentTask>

    // Dynamic URL fallback (e.g. for WordPress or direct appdeploy instances)
    @POST
    suspend fun syncToUrl(
        @retrofit2.http.Url url: String,
        @Body request: SyncBatchRequest,
        @Header("Authorization") authHeader: String? = null,
        @Header("X-API-Key") apiKey: String? = null
    ): Response<SyncBatchResponse>
}
