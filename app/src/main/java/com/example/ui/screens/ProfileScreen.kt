package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.RateReview
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.model.AppCurrency
import com.example.model.UnitSystem
import com.example.ui.components.GlassButton
import com.example.ui.components.GlassCard
import com.example.ui.components.GlassIconButton
import com.example.ui.components.GlassSecondaryButton
import com.example.ui.components.GlassTextField
import com.example.ui.components.liquidGlass
import com.example.ui.theme.DeepBlack
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.ElectricBlueLight
import com.example.ui.theme.GlassBorderColor
import com.example.ui.theme.GlassTextMuted
import com.example.ui.theme.GlassTextPrimary
import com.example.ui.theme.GlassTextSecondary
import com.example.ui.theme.NeonRed
import com.example.ui.theme.WinnerGreen
import com.example.ui.viewmodel.AutoVerseViewModel

@Composable
fun ProfileScreen(
    viewModel: AutoVerseViewModel,
    onOpenCar: (String) -> Unit,
    onOpenAdmin: () -> Unit,
    onLogout: () -> Unit
) {
    val userProfile by viewModel.userProfile.collectAsState()
    val allCars by viewModel.cars.collectAsState()

    var showEditProfileDialog by remember { mutableStateOf(false) }
    var showCurrencyDialog by remember { mutableStateOf(false) }
    var showLanguageDialog by remember { mutableStateOf(false) }

    val recentCars = remember(userProfile.recentlyViewedCarIds, allCars) {
        userProfile.recentlyViewedCarIds.mapNotNull { id -> allCars.find { it.id == id } }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepBlack)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.statusBars)
                .testTag("profile_screen_scroll"),
            contentPadding = PaddingValues(bottom = 120.dp)
        ) {
            // Profile Card with Avatar & Name
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    GlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(26.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(90.dp)
                                    .clip(CircleShape)
                                    .border(2.dp, ElectricBlue, CircleShape)
                            ) {
                                AsyncImage(
                                    model = userProfile.avatarUrl,
                                    contentDescription = "User Avatar",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Text(
                                text = userProfile.name,
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )

                            Text(
                                text = userProfile.email,
                                style = MaterialTheme.typography.bodyMedium.copy(color = GlassTextSecondary)
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                Box(
                                    modifier = Modifier
                                        .liquidGlass(shape = CircleShape, backgroundColor = ElectricBlue.copy(alpha = 0.2f))
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = "Verified Enthusiast",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = ElectricBlueLight,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                }

                                if (userProfile.isAdmin) {
                                    Box(
                                        modifier = Modifier
                                            .liquidGlass(shape = CircleShape, backgroundColor = WinnerGreen.copy(alpha = 0.2f))
                                            .padding(horizontal = 12.dp, vertical = 6.dp)
                                    ) {
                                        Text(
                                            text = "Admin Mode",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = WinnerGreen,
                                                fontWeight = FontWeight.Bold
                                            )
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            GlassSecondaryButton(
                                text = "Edit Profile",
                                icon = Icons.Default.Edit,
                                onClick = { showEditProfileDialog = true }
                            )
                        }
                    }
                }
            }

            // Preferences & Settings Section
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "APP SETTINGS & PREFERENCES",
                        style = MaterialTheme.typography.labelSmall.copy(
                            letterSpacing = 1.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = GlassTextMuted
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            // Currency Switcher
                            SettingItemRow(
                                title = "Display Currency",
                                subtitle = "${userProfile.preferredCurrency.label} (${userProfile.preferredCurrency.symbol})",
                                icon = Icons.Default.AttachMoney,
                                onClick = { showCurrencyDialog = true }
                            )

                            HorizontalDivider(color = GlassBorderColor, modifier = Modifier.padding(vertical = 10.dp))

                            // Unit System (Metric vs Imperial)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Speed,
                                        contentDescription = null,
                                        tint = ElectricBlueLight,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = "Units of Measurement",
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontWeight = FontWeight.SemiBold,
                                                color = Color.White
                                            )
                                        )
                                        Text(
                                            text = if (userProfile.unitSystem == UnitSystem.METRIC) "Metric (km/h, km)" else "Imperial (mph, miles)",
                                            style = MaterialTheme.typography.bodySmall.copy(color = GlassTextSecondary)
                                        )
                                    }
                                }

                                Switch(
                                    checked = userProfile.unitSystem == UnitSystem.IMPERIAL,
                                    onCheckedChange = { isImperial ->
                                        viewModel.updateUnitSystem(if (isImperial) UnitSystem.IMPERIAL else UnitSystem.METRIC)
                                    },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = Color.White,
                                        checkedTrackColor = ElectricBlue,
                                        uncheckedThumbColor = GlassTextMuted,
                                        uncheckedTrackColor = Color.White.copy(alpha = 0.1f)
                                    )
                                )
                            }

                            HorizontalDivider(color = GlassBorderColor, modifier = Modifier.padding(vertical = 10.dp))

                            // Language
                            SettingItemRow(
                                title = "Language",
                                subtitle = userProfile.language,
                                icon = Icons.Default.Language,
                                onClick = { showLanguageDialog = true }
                            )

                            HorizontalDivider(color = GlassBorderColor, modifier = Modifier.padding(vertical = 10.dp))

                            // Admin Mode Toggle
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.AdminPanelSettings,
                                        contentDescription = null,
                                        tint = WinnerGreen,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = "Admin Publishing Panel",
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontWeight = FontWeight.SemiBold,
                                                color = Color.White
                                            )
                                        )
                                        Text(
                                            text = "Add, modify, and delete cars from database",
                                            style = MaterialTheme.typography.bodySmall.copy(color = GlassTextSecondary)
                                        )
                                    }
                                }

                                Switch(
                                    checked = userProfile.isAdmin,
                                    onCheckedChange = { viewModel.toggleAdminMode() },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = Color.White,
                                        checkedTrackColor = WinnerGreen
                                    )
                                )
                            }

                            if (userProfile.isAdmin) {
                                Spacer(modifier = Modifier.height(14.dp))
                                GlassButton(
                                    text = "Launch Car Management Portal",
                                    icon = Icons.Default.AdminPanelSettings,
                                    onClick = onOpenAdmin,
                                    modifier = Modifier.fillMaxWidth(),
                                    gradient = listOf(WinnerGreen, Color(0xFF00802B))
                                )
                            }
                        }
                    }
                }
            }

            // Recently Viewed Cars Carousel
            if (recentCars.isNotEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 22.dp)
                    ) {
                        Text(
                            text = "RECENTLY VIEWED CARS",
                            style = MaterialTheme.typography.labelSmall.copy(
                                letterSpacing = 1.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = GlassTextMuted
                            ),
                            modifier = Modifier.padding(horizontal = 20.dp)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 20.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(recentCars) { car ->
                                Box(
                                    modifier = Modifier
                                        .width(190.dp)
                                        .height(130.dp)
                                        .liquidGlass(
                                            shape = RoundedCornerShape(18.dp),
                                            backgroundColor = Color.White.copy(alpha = 0.08f)
                                        )
                                        .clickable { onOpenCar(car.id) }
                                ) {
                                    AsyncImage(
                                        model = car.images.firstOrNull(),
                                        contentDescription = car.fullName,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .align(Alignment.BottomCenter)
                                            .background(Color.Black.copy(alpha = 0.7f))
                                            .padding(8.dp)
                                    ) {
                                        Text(
                                            text = car.fullName,
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = Color.White,
                                                fontWeight = FontWeight.Bold
                                            ),
                                            maxLines = 1
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Logout Button
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 28.dp)
                ) {
                    GlassSecondaryButton(
                        text = "Sign Out of AutoVerse",
                        icon = Icons.Default.Logout,
                        onClick = onLogout,
                        accentColor = NeonRed,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        // Edit Profile Dialog
        if (showEditProfileDialog) {
            var tempName by remember { mutableStateOf(userProfile.name) }
            var tempEmail by remember { mutableStateOf(userProfile.email) }

            Dialog(onDismissRequest = { showEditProfileDialog = false }) {
                GlassCard(
                    modifier = Modifier.fillMaxWidth(0.95f),
                    shape = RoundedCornerShape(26.dp)
                ) {
                    Column(modifier = Modifier.padding(22.dp)) {
                        Text(
                            text = "Edit Profile",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        GlassTextField(
                            value = tempName,
                            onValueChange = { tempName = it },
                            placeholder = "Full Name"
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        GlassTextField(
                            value = tempEmail,
                            onValueChange = { tempEmail = it },
                            placeholder = "Email Address"
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            GlassSecondaryButton(
                                text = "Cancel",
                                onClick = { showEditProfileDialog = false },
                                modifier = Modifier.weight(1f)
                            )
                            GlassButton(
                                text = "Save",
                                onClick = {
                                    if (tempName.isNotBlank() && tempEmail.isNotBlank()) {
                                        viewModel.updateProfile(tempName, tempEmail)
                                        showEditProfileDialog = false
                                    }
                                },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }

        // Currency Picker Dialog
        if (showCurrencyDialog) {
            Dialog(onDismissRequest = { showCurrencyDialog = false }) {
                GlassCard(
                    modifier = Modifier.fillMaxWidth(0.95f),
                    shape = RoundedCornerShape(26.dp)
                ) {
                    Column(modifier = Modifier.padding(22.dp)) {
                        Text(
                            text = "Select Preferred Currency",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.height(14.dp))

                        AppCurrency.entries.forEach { curr ->
                            val isSelected = userProfile.preferredCurrency == curr
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        if (isSelected) ElectricBlue.copy(alpha = 0.2f)
                                        else Color.Transparent
                                    )
                                    .clickable {
                                        viewModel.updateCurrency(curr)
                                        showCurrencyDialog = false
                                    }
                                    .padding(vertical = 12.dp, horizontal = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = curr.label,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = if (isSelected) Color.White else GlassTextSecondary,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                )
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = ElectricBlueLight
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Language Picker Dialog
        if (showLanguageDialog) {
            val languages = listOf("English", "Urdu (اردو)", "Arabic (العربية)")
            Dialog(onDismissRequest = { showLanguageDialog = false }) {
                GlassCard(
                    modifier = Modifier.fillMaxWidth(0.95f),
                    shape = RoundedCornerShape(26.dp)
                ) {
                    Column(modifier = Modifier.padding(22.dp)) {
                        Text(
                            text = "Select Language",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.height(14.dp))

                        languages.forEach { lang ->
                            val isSelected = userProfile.language == lang
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        if (isSelected) ElectricBlue.copy(alpha = 0.2f)
                                        else Color.Transparent
                                    )
                                    .clickable {
                                        viewModel.updateLanguage(lang)
                                        showLanguageDialog = false
                                    }
                                    .padding(vertical = 12.dp, horizontal = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = lang,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = if (isSelected) Color.White else GlassTextSecondary,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                )
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = ElectricBlueLight
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SettingItemRow(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = ElectricBlueLight,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall.copy(color = GlassTextSecondary)
                )
            }
        }
        Text(
            text = "Change",
            style = MaterialTheme.typography.bodySmall.copy(
                color = ElectricBlueLight,
                fontWeight = FontWeight.Bold
            )
        )
    }
}
