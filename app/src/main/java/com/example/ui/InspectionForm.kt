package com.example.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.data.model.Assessment
import com.example.data.model.ChecklistQuestion
import com.example.data.repository.WordPressSyncReceipt
import com.example.ui.components.InspectionForm as InternalInspectionForm

/**
 * Top-level reusable InspectionForm composable
 * Allows importing directly via `import com.example.ui.InspectionForm`
 */
@Composable
fun InspectionForm(
    modifier: Modifier = Modifier,
    initialAssessment: Assessment? = null,
    initialQuestions: List<ChecklistQuestion> = emptyList(),
    onSaveDraft: (Assessment, List<ChecklistQuestion>) -> Unit = { _, _ -> },
    onSubmitInspection: (Assessment, List<ChecklistQuestion>, Double) -> Unit = { _, _, _ -> },
    onSyncToWordPressNew: (suspend (Assessment, List<ChecklistQuestion>, Double, String) -> WordPressSyncReceipt)? = null,
    onClose: (() -> Unit)? = null
) {
    InternalInspectionForm(
        modifier = modifier,
        initialAssessment = initialAssessment,
        initialQuestions = initialQuestions,
        onSaveDraft = onSaveDraft,
        onSubmitInspection = onSubmitInspection,
        onSyncToWordPressNew = onSyncToWordPressNew,
        onClose = onClose
    )
}
