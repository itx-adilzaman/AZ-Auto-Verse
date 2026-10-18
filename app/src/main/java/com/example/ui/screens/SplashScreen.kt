package com.example.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ElectricCar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.components.liquidGlass
import com.example.ui.theme.DeepBlack
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.ElectricBlueLight
import com.example.ui.theme.GlassTextMuted
import com.example.ui.theme.GlassTextPrimary
import com.example.ui.theme.NeonRed
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onSplashComplete: () -> Unit
) {
    val alphaAnim = remember { Animatable(0f) }
    val scaleAnim = remember { Animatable(0.85f) }
    val glowAnim = remember { Animatable(0.2f) }

    LaunchedEffect(Unit) {
        alphaAnim.animateTo(1f, animationSpec = tween(900, easing = FastOutSlowInEasing))
        scaleAnim.animateTo(1f, animationSpec = tween(900, easing = FastOutSlowInEasing))
        glowAnim.animateTo(
            targetValue = 0.8f,
            animationSpec = infiniteRepeatable(
                animation = tween(1400, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            )
        )
    }

    LaunchedEffect(Unit) {
        delay(2500)
        onSplashComplete()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        ElectricBlue.copy(alpha = 0.18f),
                        DeepBlack,
                        Color(0xFF030406)
                    ),
                    radius = 1200f
                )
            )
            .clickable { onSplashComplete() }
            .testTag("splash_screen"),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .graphicsLayer {
                    alpha = alphaAnim.value
                    scaleX = scaleAnim.value
                    scaleY = scaleAnim.value
                }
        ) {
            // Glowing metallic logo badge
            Box(
                modifier = Modifier
                    .size(130.dp)
                    .shadow(
                        elevation = (28 * glowAnim.value).dp,
                        shape = RoundedCornerShape(32.dp),
                        ambientColor = ElectricBlue,
                        spotColor = ElectricBlueLight
                    )
                    .clip(RoundedCornerShape(32.dp))
                    .border(
                        width = 1.5.dp,
                        brush = Brush.verticalGradient(
                            listOf(
                                Color.White.copy(alpha = 0.7f),
                                ElectricBlue.copy(alpha = 0.5f),
                                Color.Transparent
                            )
                        ),
                        shape = RoundedCornerShape(32.dp)
                    )
                    .background(Color(0xFF0A0D15)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_autoverse_logo),
                    contentDescription = "AutoVerse Logo",
                    modifier = Modifier.size(116.dp)
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // App Name with glow
            Text(
                text = "AUTOVERSE",
                style = MaterialTheme.typography.displayMedium.copy(
                    fontWeight = FontWeight.Black,
                    letterSpacing = 6.sp,
                    color = GlassTextPrimary
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Tagline in liquid glass pill
            Box(
                modifier = Modifier
                    .liquidGlass(
                        shape = CircleShape,
                        backgroundColor = Color.White.copy(alpha = 0.08f)
                    )
                    .padding(horizontal = 18.dp, vertical = 6.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(ElectricBlue)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "THE GLOBAL CAR ENCYCLOPEDIA",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = ElectricBlueLight,
                            letterSpacing = 2.sp,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(60.dp))

            CircularProgressIndicator(
                color = ElectricBlue,
                strokeWidth = 2.dp,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Tap anywhere to skip",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = GlassTextMuted.copy(alpha = 0.5f),
                    fontSize = 11.sp
                )
            )
        }
    }
}
