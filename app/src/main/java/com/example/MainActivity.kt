package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.AssessmentWizardScreen
import com.example.ui.screens.ChemicalRegisterScreen
import com.example.ui.screens.CustomerSiteSelectionScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.EquipmentRegisterScreen
import com.example.ui.screens.LoginScreen
import com.example.ui.screens.RemedialActionsScreen
import com.example.ui.screens.ReportPreviewScreen
import com.example.ui.screens.SignatureScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.screens.CustomerSiteManagementScreen
import com.example.ui.screens.HealthSafetyAuditScreen
import com.example.ui.screens.UserManagementScreen
import com.example.ui.screens.WebPortalPreviewScreen
import com.example.ui.screens.StaffAndFieldOperationsScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.WorkforceViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                WorkforceApp()
            }
        }
    }
}

@Composable
fun WorkforceApp(
    viewModel: WorkforceViewModel = viewModel()
) {
    val currentScreen by viewModel.screen.collectAsState()
    val snackBarMessage by viewModel.snackBarMessage.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(snackBarMessage) {
        snackBarMessage?.let { msg ->
            snackbarHostState.showSnackbar(
                message = msg,
                duration = SnackbarDuration.Short
            )
            viewModel.clearNotification()
        }
    }

    // Hardware and gesture BackHandler
    BackHandler(enabled = currentScreen !is AppScreen.Splash && currentScreen !is AppScreen.Dashboard) {
        val handled = viewModel.navigateBack()
        if (!handled) {
            viewModel.navigateTo(AppScreen.Dashboard)
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (val screen = currentScreen) {
                is AppScreen.Splash -> {
                    SplashScreen(
                        onContinue = { viewModel.navigateTo(AppScreen.Login) }
                    )
                }

                is AppScreen.Login -> {
                    LoginScreen(
                        viewModel = viewModel,
                        onLoginSuccess = { viewModel.navigateTo(AppScreen.Dashboard) }
                    )
                }

                is AppScreen.Dashboard -> {
                    DashboardScreen(viewModel = viewModel)
                }

                is AppScreen.CustomerSiteSelect -> {
                    CustomerSiteSelectionScreen(
                        viewModel = viewModel,
                        onBack = { viewModel.navigateBack() }
                    )
                }

                is AppScreen.AssessmentWizard -> {
                    AssessmentWizardScreen(
                        viewModel = viewModel,
                        assessmentId = screen.assessmentId,
                        onBack = { viewModel.navigateBack() }
                    )
                }

                is AppScreen.InspectionFormScreen -> {
                    com.example.ui.components.InspectionForm(
                        initialAssessment = viewModel.activeAssessment.collectAsState().value,
                        onSaveDraft = { _, _ ->
                            viewModel.showNotification("Audit draft saved locally.")
                            viewModel.navigateBack()
                        },
                        onSubmitInspection = { _, _, score ->
                            viewModel.showNotification("Audit finalized! Score: " + String.format(java.util.Locale.US, "%.1f%%", score))
                            viewModel.navigateBack()
                        },
                        onSyncToWordPressNew = { assessment, questions, score, facility ->
                            viewModel.syncManager.syncDirectToWordPressNew(
                                assessment = assessment,
                                checklistAnswers = questions,
                                calculatedScore = score,
                                facilityName = facility
                            )
                        },
                        onClose = { viewModel.navigateBack() }
                    )
                }

                is AppScreen.ChemicalRegister -> {
                    ChemicalRegisterScreen(
                        viewModel = viewModel,
                        assessmentId = screen.assessmentId,
                        onBack = { viewModel.navigateBack() }
                    )
                }

                is AppScreen.RemedialActions -> {
                    RemedialActionsScreen(
                        viewModel = viewModel,
                        assessmentId = screen.assessmentId,
                        onBack = { viewModel.navigateBack() }
                    )
                }

                is AppScreen.SignatureCapture -> {
                    SignatureScreen(
                        viewModel = viewModel,
                        assessmentId = screen.assessmentId,
                        onBack = { viewModel.navigateBack() }
                    )
                }

                is AppScreen.ReportPreview -> {
                    ReportPreviewScreen(
                        viewModel = viewModel,
                        assessmentId = screen.assessmentId,
                        onBack = { viewModel.navigateBack() }
                    )
                }

                is AppScreen.EquipmentRegister -> {
                    EquipmentRegisterScreen(
                        viewModel = viewModel,
                        onBack = { viewModel.navigateBack() }
                    )
                }

                is AppScreen.WebPortalPreview -> {
                    WebPortalPreviewScreen(
                        viewModel = viewModel,
                        onBack = { viewModel.navigateBack() }
                    )
                }

                is AppScreen.UserManagement -> {
                    UserManagementScreen(
                        viewModel = viewModel,
                        onBack = { viewModel.navigateBack() }
                    )
                }

                is AppScreen.CustomerManagement -> {
                    CustomerSiteManagementScreen(
                        viewModel = viewModel,
                        onBack = { viewModel.navigateBack() }
                    )
                }

                is AppScreen.HealthSafetyAudit -> {
                    HealthSafetyAuditScreen(
                        viewModel = viewModel,
                        onBack = { viewModel.navigateBack() }
                    )
                }

                is AppScreen.StaffAndFieldOps -> {
                    StaffAndFieldOperationsScreen(
                        viewModel = viewModel,
                        onBack = { viewModel.navigateBack() }
                    )
                }

                is AppScreen.Settings -> {
                    DashboardScreen(viewModel = viewModel)
                }
            }
        }
    }
}
