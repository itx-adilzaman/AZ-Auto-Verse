package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CompareArrows
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.CompareArrows
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.GlassBorderHighlight
import com.example.ui.theme.GlassTextMuted
import com.example.ui.theme.GlassTextPrimary
import com.example.ui.theme.NeonRed

enum class NavTab(
    val title: String,
    val activeIcon: ImageVector,
    val inactiveIcon: ImageVector,
    val tag: String
) {
    HOME("Home", Icons.Filled.Home, Icons.Outlined.Home, "tab_home"),
    EXPLORE("Explore", Icons.Filled.Explore, Icons.Outlined.Explore, "tab_explore"),
    COMPARE("Compare", Icons.Filled.CompareArrows, Icons.Outlined.CompareArrows, "tab_compare"),
    FAVORITES("Saved", Icons.Filled.Bookmark, Icons.Outlined.BookmarkBorder, "tab_favorites"),
    PROFILE("Profile", Icons.Filled.Person, Icons.Outlined.Person, "tab_profile")
}

@Composable
fun LiquidNavBar(
    currentTab: NavTab,
    onTabSelected: (NavTab) -> Unit,
    modifier: Modifier = Modifier,
    compareCount: Int = 0,
    favoritesCount: Int = 0
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.navigationBars)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        // Floating pill container with deep liquid glass refraction
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(68.dp)
                .shadow(
                    elevation = 20.dp,
                    shape = CircleShape,
                    ambientColor = Color.Black.copy(alpha = 0.6f),
                    spotColor = ElectricBlue.copy(alpha = 0.25f)
                )
                .clip(CircleShape)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xE6141824),
                            Color(0xF00A0C14)
                        )
                    )
                )
                .border(
                    width = 1.dp,
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            GlassBorderHighlight.copy(alpha = 0.45f),
                            Color.White.copy(alpha = 0.12f)
                        )
                    ),
                    shape = CircleShape
                )
                .padding(horizontal = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                NavTab.entries.forEach { tab ->
                    val isSelected = tab == currentTab
                    val iconScale by animateFloatAsState(
                        targetValue = if (isSelected) 1.15f else 1f,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessMediumLow
                        ),
                        label = "tabScale_${tab.name}"
                    )
                    val iconColor by animateColorAsState(
                        targetValue = if (isSelected) ElectricBlue else GlassTextMuted,
                        label = "tabColor_${tab.name}"
                    )
                    val textColor by animateColorAsState(
                        targetValue = if (isSelected) GlassTextPrimary else GlassTextMuted.copy(alpha = 0.6f),
                        label = "tabTextColor_${tab.name}"
                    )

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp)
                            .clip(CircleShape)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = { onTabSelected(tab) }
                            )
                            .testTag(tab.tag),
                        contentAlignment = Alignment.Center
                    ) {
                        // Sliding active pill background
                        if (isSelected) {
                            Box(
                                modifier = Modifier
                                    .size(width = 54.dp, height = 48.dp)
                                    .clip(CircleShape)
                                    .background(
                                        brush = Brush.radialGradient(
                                            colors = listOf(
                                                ElectricBlue.copy(alpha = 0.32f),
                                                ElectricBlue.copy(alpha = 0.08f),
                                                Color.Transparent
                                            )
                                        )
                                    )
                                    .border(
                                        width = 0.8.dp,
                                        brush = Brush.verticalGradient(
                                            listOf(
                                                ElectricBlue.copy(alpha = 0.6f),
                                                ElectricBlue.copy(alpha = 0.15f)
                                            )
                                        ),
                                        shape = CircleShape
                                    )
                            )
                        }

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            val badgeCount = when (tab) {
                                NavTab.COMPARE -> compareCount
                                NavTab.FAVORITES -> favoritesCount
                                else -> 0
                            }

                            BadgedBox(
                                badge = {
                                    if (badgeCount > 0) {
                                        Badge(
                                            containerColor = NeonRed,
                                            contentColor = Color.White
                                        ) {
                                            Text(
                                                text = if (badgeCount > 9) "9+" else "$badgeCount",
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = if (isSelected) tab.activeIcon else tab.inactiveIcon,
                                    contentDescription = tab.title,
                                    tint = iconColor,
                                    modifier = Modifier
                                        .size(22.dp)
                                        .graphicsLayer {
                                            scaleX = iconScale
                                            scaleY = iconScale
                                        }
                                )
                            }

                            Spacer(modifier = Modifier.height(2.dp))

                            Text(
                                text = tab.title,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 10.sp,
                                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                                    color = textColor
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}
