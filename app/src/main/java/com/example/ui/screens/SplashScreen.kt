package com.example.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.WorkforceDarkBg
import com.example.ui.theme.WorkforceOrange
import com.example.ui.theme.WorkforceRed
import com.example.ui.theme.WorkforceTextMuted
import com.example.ui.theme.WorkforceTextPrimary
import com.example.ui.theme.WorkforceTextSecondary

@Composable
fun SplashScreen(
    onContinue: () -> Unit
) {
    val alphaAnim = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        alphaAnim.animateTo(1f, animationSpec = tween(900))
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(WorkforceDarkBg)
            .testTag("splash_screen_container")
    ) {
        // Mining Hero Background Art
        Image(
            painter = painterResource(id = R.drawable.img_mining_hero),
            contentDescription = "WorkForce Mining Background",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(380.dp)
                .alpha(0.35f)
        )

        // Gradient overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            WorkforceDarkBg.copy(alpha = 0.85f),
                            WorkforceDarkBg
                        ),
                        startY = 100f
                    )
                )
        )

        // Central Branding Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .alpha(alphaAnim.value),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom
        ) {
            Surface(
                modifier = Modifier
                    .size(110.dp)
                    .clip(RoundedCornerShape(22.dp)),
                color = Color.White,
                shadowElevation = 8.dp
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_workforce_logo),
                    contentDescription = "WorkForce Health SA Logo",
                    modifier = Modifier.padding(12.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "WORKFORCE HEALTH SA",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.2.sp,
                    color = WorkforceTextPrimary
                ),
                textAlign = TextAlign.Center
            )

            Text(
                text = "OCCUPATIONAL HYGIENE DIGITAL PLATFORM",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp,
                    color = WorkforceOrange
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 6.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "SAFER WORKPLACES  |  HEALTHIER PEOPLE  |  A STRONGER TOMORROW",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = WorkforceTextSecondary,
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 0.5.sp
                ),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(36.dp))

            // Initial Module Badge
            Surface(
                color = WorkforceRed.copy(alpha = 0.15f),
                shape = RoundedCornerShape(20.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, WorkforceRed.copy(alpha = 0.5f))
            ) {
                Text(
                    text = "INITIAL MODULE: MONTHLY ABLUTION FACILITIES (MALE)",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = WorkforceRed,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            Button(
                onClick = onContinue,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .testTag("splash_enter_button"),
                colors = ButtonDefaults.buttonColors(containerColor = WorkforceRed),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "ENTER WORKFORCE PLATFORM",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp
                    )
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Offline-First Engine • Mining & Industrial Compliance",
                style = MaterialTheme.typography.bodySmall.copy(color = WorkforceTextMuted)
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
