package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.material.icons.filled.Star
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.AccountCircle
import com.example.ui.theme.NeonCyan
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.model.AppCurrency
import com.example.model.UnitSystem
import com.example.ui.components.GlassButton
import com.example.ui.components.GlassCard
import com.example.ui.components.GlassIconButton
import com.example.ui.components.GlassSecondaryButton
import com.example.ui.components.GlassTextField
import com.example.ui.components.liquidGlass
import com.example.ui.theme.AmberGold
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
                            val adilAvatarUrl = "https://res.cloudinary.com/dkdzuyjc4/image/upload/c_thumb,g_face,w_600,h_600,z_0.75/v1792432515/1784644774412_nsmlyh.jpg"
                            val adilName = "Muhammad Adil Zaman"

                            Box(
                                modifier = Modifier
                                    .size(96.dp)
                                    .clip(CircleShape)
                                    .border(
                                        width = 2.5.dp,
                                        brush = Brush.sweepGradient(
                                            listOf(
                                                ElectricBlue,
                                                NeonCyan,
                                                AmberGold,
                                                WinnerGreen,
                                                ElectricBlue
                                            )
                                        ),
                                        shape = CircleShape
                                    )
                                    .padding(3.dp)
                            ) {
                                val avatarContext = androidx.compose.ui.platform.LocalContext.current
                                AsyncImage(
                                    model = coil.request.ImageRequest.Builder(avatarContext)
                                        .data(adilAvatarUrl)
                                        .crossfade(true)
                                        .setHeader("User-Agent", "Mozilla/5.0 (Linux; Android 14)")
                                        .build(),
                                    contentDescription = adilName,
                                    contentScale = ContentScale.Crop,
                                    alignment = Alignment.TopCenter,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(CircleShape)
                                )
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Text(
                                text = adilName,
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = 0.5.sp,
                                    color = Color.White
                                )
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = "malikmuhammadadilzaman@gmail.com",
                                style = MaterialTheme.typography.bodyMedium.copy(color = GlassTextSecondary)
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = if (userProfile.bio.isNotBlank()) "\"${userProfile.bio}\"" else "“Creator & Full-Stack Architect of AutoVerse Supercar Platform”",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = ElectricBlueLight,
                                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                ),
                                modifier = Modifier.padding(horizontal = 12.dp)
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            // Profile real details row (Phone, Location, Dream Car)
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(Color.White.copy(alpha = 0.05f))
                                    .border(1.dp, Color.White.copy(alpha = 0.10f), RoundedCornerShape(16.dp))
                                    .padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                if (userProfile.phoneNumber.isNotBlank()) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Phone,
                                            contentDescription = null,
                                            tint = ElectricBlueLight,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = userProfile.phoneNumber,
                                            style = MaterialTheme.typography.bodySmall.copy(color = GlassTextPrimary)
                                        )
                                    }
                                }

                                if (userProfile.location.isNotBlank()) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.LocationOn,
                                            contentDescription = null,
                                            tint = ElectricBlueLight,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = userProfile.location,
                                            style = MaterialTheme.typography.bodySmall.copy(color = GlassTextPrimary)
                                        )
                                    }
                                }

                                if (userProfile.dreamCar.isNotBlank()) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.DirectionsCar,
                                            contentDescription = null,
                                            tint = AmberGold,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "Dream Car: ${userProfile.dreamCar}",
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = Color.White,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

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

                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            GlassSecondaryButton(
                                text = "Edit Profile & Dream Car",
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

            // Magnificent Animated Creator & Visionary Signature Footer
            item {
                Spacer(modifier = Modifier.height(14.dp))
                AdilZamanSignatureFooter(
                    userAvatarUrl = "https://res.cloudinary.com/dkdzuyjc4/image/upload/c_thumb,g_face,w_600,h_600,z_0.75/v1792432515/1784644774412_nsmlyh.jpg",
                    userName = "Muhammad Adil Zaman"
                )
            }

            // Logout Button
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 20.dp)
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

        // Real Database Edit Profile Dialog with Glassmorphism
        if (showEditProfileDialog) {
            var tempName by remember { mutableStateOf(userProfile.name) }
            var tempEmail by remember { mutableStateOf(userProfile.email) }
            var tempPhone by remember { mutableStateOf(userProfile.phoneNumber) }
            var tempBio by remember { mutableStateOf(userProfile.bio) }
            var tempLocation by remember { mutableStateOf(userProfile.location) }
            var tempDreamCar by remember { mutableStateOf(userProfile.dreamCar) }

            Dialog(
                onDismissRequest = { showEditProfileDialog = false },
                properties = DialogProperties(usePlatformDefaultWidth = false)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.70f))
                        .clickable { showEditProfileDialog = false },
                    contentAlignment = Alignment.Center
                ) {
                    GlassCard(
                        modifier = Modifier
                            .fillMaxWidth(0.92f)
                            .clickable(enabled = false) {},
                        shape = RoundedCornerShape(28.dp),
                        backgroundColor = Color.White.copy(alpha = 0.12f),
                        borderColor = ElectricBlueLight.copy(alpha = 0.45f)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(22.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "Edit Real Profile",
                                        style = MaterialTheme.typography.titleLarge.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    )
                                    Text(
                                        text = "Persisted directly into Room SQLite Database",
                                        style = MaterialTheme.typography.bodySmall.copy(color = WinnerGreen)
                                    )
                                }
                                GlassIconButton(
                                    icon = Icons.Default.Close,
                                    contentDescription = "Close",
                                    onClick = { showEditProfileDialog = false },
                                    size = 36.dp
                                )
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            GlassTextField(
                                value = tempName,
                                onValueChange = { tempName = it },
                                placeholder = "Full Name"
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            GlassTextField(
                                value = tempEmail,
                                onValueChange = { tempEmail = it },
                                placeholder = "Email Address"
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            GlassTextField(
                                value = tempPhone,
                                onValueChange = { tempPhone = it },
                                placeholder = "Phone Number (e.g. +1 555-019-2834)",
                                leadingIcon = Icons.Default.Phone
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            GlassTextField(
                                value = tempLocation,
                                onValueChange = { tempLocation = it },
                                placeholder = "Location / City (e.g. Monaco & London)",
                                leadingIcon = Icons.Default.LocationOn
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            GlassTextField(
                                value = tempDreamCar,
                                onValueChange = { tempDreamCar = it },
                                placeholder = "Dream Hypercar (e.g. Ferrari SF90 XX)",
                                leadingIcon = Icons.Default.DirectionsCar
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            GlassTextField(
                                value = tempBio,
                                onValueChange = { tempBio = it },
                                placeholder = "Enthusiast Bio / Passion Statement",
                                modifier = Modifier.height(80.dp)
                            )

                            Spacer(modifier = Modifier.height(18.dp))

                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                GlassSecondaryButton(
                                    text = "Cancel",
                                    onClick = { showEditProfileDialog = false },
                                    modifier = Modifier.weight(1f)
                                )
                                GlassButton(
                                    text = "Save to Database",
                                    onClick = {
                                        if (tempName.isNotBlank() && tempEmail.isNotBlank()) {
                                            viewModel.updateProfile(
                                                name = tempName,
                                                email = tempEmail,
                                                phone = tempPhone,
                                                bio = tempBio,
                                                location = tempLocation,
                                                dreamCar = tempDreamCar
                                            )
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
        }

        // Glassmorphic Currency Picker Dialog
        if (showCurrencyDialog) {
            Dialog(
                onDismissRequest = { showCurrencyDialog = false },
                properties = DialogProperties(usePlatformDefaultWidth = false)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.70f))
                        .clickable { showCurrencyDialog = false },
                    contentAlignment = Alignment.Center
                ) {
                    GlassCard(
                        modifier = Modifier
                            .fillMaxWidth(0.92f)
                            .clickable(enabled = false) {},
                        shape = RoundedCornerShape(28.dp),
                        backgroundColor = Color.White.copy(alpha = 0.12f),
                        borderColor = ElectricBlueLight.copy(alpha = 0.45f)
                    ) {
                        Column(modifier = Modifier.padding(22.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Select Preferred Currency",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                )
                                GlassIconButton(
                                    icon = Icons.Default.Close,
                                    contentDescription = "Close",
                                    onClick = { showCurrencyDialog = false },
                                    size = 36.dp
                                )
                            }
                            Spacer(modifier = Modifier.height(14.dp))

                            AppCurrency.entries.forEach { curr ->
                                val isSelected = userProfile.preferredCurrency == curr
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(
                                            if (isSelected) ElectricBlue.copy(alpha = 0.22f)
                                            else Color.White.copy(alpha = 0.05f)
                                        )
                                        .border(
                                            1.dp,
                                            if (isSelected) ElectricBlueLight else Color.White.copy(alpha = 0.12f),
                                            RoundedCornerShape(14.dp)
                                        )
                                        .clickable {
                                            viewModel.updateCurrency(curr)
                                            showCurrencyDialog = false
                                        }
                                        .padding(vertical = 12.dp, horizontal = 14.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "${curr.symbol}  ${curr.label} (${curr.code})",
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

        // Glassmorphic Language Picker Dialog
        if (showLanguageDialog) {
            val languages = listOf("English", "Urdu (اردو)", "Arabic (العربية)")
            Dialog(
                onDismissRequest = { showLanguageDialog = false },
                properties = DialogProperties(usePlatformDefaultWidth = false)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.70f))
                        .clickable { showLanguageDialog = false },
                    contentAlignment = Alignment.Center
                ) {
                    GlassCard(
                        modifier = Modifier
                            .fillMaxWidth(0.92f)
                            .clickable(enabled = false) {},
                        shape = RoundedCornerShape(28.dp),
                        backgroundColor = Color.White.copy(alpha = 0.12f),
                        borderColor = ElectricBlueLight.copy(alpha = 0.45f)
                    ) {
                        Column(modifier = Modifier.padding(22.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Select Language",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                )
                                GlassIconButton(
                                    icon = Icons.Default.Close,
                                    contentDescription = "Close",
                                    onClick = { showLanguageDialog = false },
                                    size = 36.dp
                                )
                            }
                            Spacer(modifier = Modifier.height(14.dp))

                            languages.forEach { lang ->
                                val isSelected = userProfile.language == lang
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(
                                            if (isSelected) ElectricBlue.copy(alpha = 0.22f)
                                            else Color.White.copy(alpha = 0.05f)
                                        )
                                        .border(
                                            1.dp,
                                            if (isSelected) ElectricBlueLight else Color.White.copy(alpha = 0.12f),
                                            RoundedCornerShape(14.dp)
                                        )
                                        .clickable {
                                            viewModel.updateLanguage(lang)
                                            showLanguageDialog = false
                                        }
                                        .padding(vertical = 12.dp, horizontal = 14.dp),
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

@Composable
fun AdilZamanSignatureFooter(
    userAvatarUrl: String,
    userName: String
) {
    val infiniteTransition = rememberInfiniteTransition(label = "footer_anim")

    // Pulsing aura scale & alpha
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    val auraAlpha by infiniteTransition.animateFloat(
        initialValue = 0.30f,
        targetValue = 0.75f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "aura_alpha"
    )

    // Rotating gradient border angle
    val rotationAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(7500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "border_rotation"
    )

    val context = androidx.compose.ui.platform.LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 10.dp)
            .clip(RoundedCornerShape(28.dp))
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF101526),
                        Color(0xFF080C16)
                    )
                )
            )
            .border(
                width = 1.5.dp,
                brush = Brush.sweepGradient(
                    listOf(
                        ElectricBlue.copy(alpha = 0.7f),
                        NeonCyan,
                        AmberGold.copy(alpha = 0.8f),
                        WinnerGreen.copy(alpha = 0.7f),
                        ElectricBlue.copy(alpha = 0.7f)
                    )
                ),
                shape = RoundedCornerShape(28.dp)
            )
            .padding(22.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Animated Avatar Container with Glowing Aura
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(96.dp)
            ) {
                // Pulsing ambient glow aura behind avatar
                Box(
                    modifier = Modifier
                        .size(92.dp)
                        .graphicsLayer {
                            scaleX = pulseScale
                            scaleY = pulseScale
                        }
                        .clip(CircleShape)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    NeonCyan.copy(alpha = auraAlpha),
                                    ElectricBlue.copy(alpha = auraAlpha * 0.5f),
                                    Color.Transparent
                                )
                            )
                        )
                )

                // Rotating gradient border ring
                Box(
                    modifier = Modifier
                        .size(86.dp)
                        .graphicsLayer {
                            rotationZ = rotationAngle
                        }
                        .clip(CircleShape)
                        .border(
                            width = 2.5.dp,
                            brush = Brush.sweepGradient(
                                listOf(
                                    ElectricBlue,
                                    NeonCyan,
                                    AmberGold,
                                    WinnerGreen,
                                    ElectricBlue
                                )
                            ),
                            shape = CircleShape
                        )
                )

                // User Avatar Image
                Box(
                    modifier = Modifier
                        .size(76.dp)
                        .clip(CircleShape)
                        .border(1.5.dp, Color.White.copy(alpha = 0.5f), CircleShape)
                ) {
                    AsyncImage(
                        model = coil.request.ImageRequest.Builder(context)
                            .data(userAvatarUrl)
                            .crossfade(true)
                            .setHeader("User-Agent", "Mozilla/5.0 (Linux; Android 14)")
                            .build(),
                        contentDescription = userName,
                        contentScale = ContentScale.Crop,
                        alignment = Alignment.TopCenter,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                // Verified Badge floating at bottom right
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(WinnerGreen)
                        .border(1.5.dp, Color(0xFF080C16), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Verified",
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Subtitle Tag: CREATOR & ARCHITECT
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(
                        brush = Brush.horizontalGradient(
                            listOf(
                                ElectricBlue.copy(alpha = 0.25f),
                                NeonCyan.copy(alpha = 0.25f)
                            )
                        )
                    )
                    .border(1.dp, NeonCyan.copy(alpha = 0.45f), CircleShape)
                    .padding(horizontal = 14.dp, vertical = 5.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = AmberGold,
                        modifier = Modifier.size(13.dp)
                    )
                    Text(
                        text = "AUTOVERSE VISIONARY & CREATOR",
                        style = MaterialTheme.typography.labelSmall.copy(
                            letterSpacing = 1.8.sp,
                            fontWeight = FontWeight.Bold,
                            color = NeonCyan,
                            fontSize = 10.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // User Name in Exquisite Typography
            Text(
                text = userName,
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.2.sp,
                    color = Color.White
                )
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Lead Automotive Curator & Full-Stack Architect",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = ElectricBlueLight,
                    fontWeight = FontWeight.Medium,
                    fontSize = 12.sp
                )
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Badges row
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.White.copy(alpha = 0.06f))
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Text(
                        text = "Verified Driver",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = WinnerGreen,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 10.sp
                        )
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.White.copy(alpha = 0.06f))
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Text(
                        text = "Islamabad & London",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = GlassTextSecondary,
                            fontWeight = FontWeight.Normal,
                            fontSize = 10.sp
                        )
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.White.copy(alpha = 0.06f))
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Text(
                        text = "Ferrari Collector",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = AmberGold,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 10.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            HorizontalDivider(
                color = Color.White.copy(alpha = 0.08f),
                thickness = 1.dp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "“Engineered with passion for supercar collectors, racers, and design purists worldwide.”",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = GlassTextMuted,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    fontSize = 11.sp
                ),
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(WinnerGreen)
                )
                Text(
                    text = "AutoVerse 2026 • Live on Neon PostgreSQL Cloud",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = GlassTextMuted,
                        fontSize = 9.sp,
                        letterSpacing = 0.8.sp
                    )
                )
            }
        }
    }
}
