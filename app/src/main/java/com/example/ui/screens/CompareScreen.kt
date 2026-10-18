package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CompareArrows
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.model.Car
import com.example.ui.components.GlassButton
import com.example.ui.components.GlassCard
import com.example.ui.components.GlassIconButton
import com.example.ui.components.GlassSecondaryButton
import com.example.ui.components.NavTab
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
fun CompareScreen(
    viewModel: AutoVerseViewModel,
    onOpenCar: (String) -> Unit
) {
    val userProfile by viewModel.userProfile.collectAsState()
    val allCars by viewModel.cars.collectAsState()
    val currency = userProfile.preferredCurrency

    val compareCars = remember(userProfile.compareCarIds, allCars) {
        userProfile.compareCarIds.mapNotNull { id -> allCars.find { it.id == id } }
    }

    var showCarPicker by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepBlack)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.statusBars)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Head-to-Head Compare",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = GlassTextPrimary
                        )
                    )
                    Text(
                        text = "Compare up to 3 cars with live winner highlights",
                        style = MaterialTheme.typography.bodySmall.copy(color = GlassTextMuted)
                    )
                }

                if (compareCars.isNotEmpty()) {
                    Text(
                        text = "Clear All",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = NeonRed,
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier
                            .clickable { viewModel.clearCompare() }
                            .padding(6.dp)
                    )
                }
            }

            if (compareCars.isEmpty()) {
                // Empty state
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 120.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                                .background(ElectricBlue.copy(alpha = 0.15f))
                                .border(1.dp, ElectricBlue.copy(alpha = 0.4f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CompareArrows,
                                contentDescription = null,
                                tint = ElectricBlueLight,
                                modifier = Modifier.size(42.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        Text(
                            text = "No Cars in Comparison",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Select any car across the encyclopedia and tap 'Compare' to analyze stats side-by-side.",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = GlassTextSecondary,
                                textAlign = TextAlign.Center
                            )
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        GlassButton(
                            text = "Select First Car",
                            icon = Icons.Default.Add,
                            onClick = { showCarPicker = true }
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("compare_table_scroll"),
                    contentPadding = PaddingValues(bottom = 120.dp)
                ) {
                    // Car Selector Slot Cards
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            for (i in 0 until 3) {
                                val car = compareCars.getOrNull(i)
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(150.dp)
                                        .liquidGlass(
                                            shape = RoundedCornerShape(18.dp),
                                            backgroundColor = Color.White.copy(alpha = 0.08f)
                                        )
                                ) {
                                    if (car != null) {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .clickable { onOpenCar(car.id) }
                                        ) {
                                            AsyncImage(
                                                model = car.images.firstOrNull(),
                                                contentDescription = car.fullName,
                                                contentScale = ContentScale.Crop,
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(80.dp)
                                                    .clip(RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp))
                                            )
                                            Column(modifier = Modifier.padding(6.dp)) {
                                                Text(
                                                    text = car.brand.uppercase(),
                                                    style = MaterialTheme.typography.labelSmall.copy(
                                                        fontSize = 9.sp,
                                                        color = ElectricBlueLight,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                )
                                                Text(
                                                    text = car.model,
                                                    style = MaterialTheme.typography.bodySmall.copy(
                                                        fontWeight = FontWeight.Bold,
                                                        color = Color.White,
                                                        fontSize = 11.sp
                                                    ),
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                                Text(
                                                    text = currency.formatPrice(car.priceUsd),
                                                    style = MaterialTheme.typography.bodySmall.copy(
                                                        color = GlassTextSecondary,
                                                        fontSize = 10.sp
                                                    )
                                                )
                                            }
                                        }

                                        // Remove button
                                        Box(
                                            modifier = Modifier
                                                .align(Alignment.TopEnd)
                                                .padding(4.dp)
                                        ) {
                                            GlassIconButton(
                                                icon = Icons.Default.Close,
                                                contentDescription = "Remove",
                                                onClick = { viewModel.removeFromCompare(car.id) },
                                                size = 26.dp,
                                                tint = Color.White,
                                                backgroundColor = Color.Black.copy(alpha = 0.6f)
                                            )
                                        }
                                    } else {
                                        // Empty Slot to add a car
                                        Column(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .clickable { showCarPicker = true },
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.Center
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(36.dp)
                                                    .clip(CircleShape)
                                                    .background(Color.White.copy(alpha = 0.1f)),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Add,
                                                    contentDescription = "Add Car",
                                                    tint = ElectricBlueLight,
                                                    modifier = Modifier.size(20.dp)
                                                )
                                            }
                                            Spacer(modifier = Modifier.height(6.dp))
                                            Text(
                                                text = "Add Car",
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    color = GlassTextSecondary,
                                                    fontSize = 11.sp
                                                )
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Comparison Metric Rows
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // 0-100 km/h (lowest wins)
                            val bestAcc = compareCars.minOfOrNull { it.acceleration0to100 }
                            ComparisonRow(
                                title = "0-100 km/h (Acceleration)",
                                cars = compareCars,
                                valueExtractor = { "${it.acceleration0to100}s" },
                                isWinner = { it.acceleration0to100 == bestAcc }
                            )

                            // Horsepower (highest wins)
                            val bestHp = compareCars.maxOfOrNull { it.horsepower }
                            ComparisonRow(
                                title = "Horsepower",
                                cars = compareCars,
                                valueExtractor = { "${it.horsepower} HP" },
                                isWinner = { it.horsepower == bestHp }
                            )

                            // Top Speed (highest wins)
                            val bestSpeed = compareCars.maxOfOrNull { it.topSpeedKmh }
                            ComparisonRow(
                                title = "Top Speed",
                                cars = compareCars,
                                valueExtractor = { "${it.topSpeedKmh} km/h" },
                                isWinner = { it.topSpeedKmh == bestSpeed }
                            )

                            // Torque (highest wins)
                            val bestTorque = compareCars.maxOfOrNull { it.torqueNm }
                            ComparisonRow(
                                title = "Torque",
                                cars = compareCars,
                                valueExtractor = { "${it.torqueNm} Nm" },
                                isWinner = { it.torqueNm == bestTorque }
                            )

                            // Price (lowest MSRP wins)
                            val bestPrice = compareCars.minOfOrNull { it.priceUsd }
                            ComparisonRow(
                                title = "Starting Price",
                                cars = compareCars,
                                valueExtractor = { currency.formatPrice(it.priceUsd) },
                                isWinner = { it.priceUsd == bestPrice }
                            )

                            // Engine
                            ComparisonRow(
                                title = "Powertrain Engine",
                                cars = compareCars,
                                valueExtractor = { it.engine },
                                isWinner = { false }
                            )

                            // Transmission
                            ComparisonRow(
                                title = "Transmission",
                                cars = compareCars,
                                valueExtractor = { it.transmission },
                                isWinner = { false }
                            )

                            // Drivetrain
                            ComparisonRow(
                                title = "Drivetrain",
                                cars = compareCars,
                                valueExtractor = { it.drivetrain },
                                isWinner = { false }
                            )

                            // Fuel / Mileage
                            ComparisonRow(
                                title = "Efficiency / Range",
                                cars = compareCars,
                                valueExtractor = { it.mileage },
                                isWinner = { false }
                            )

                            // Curb Weight (lightest wins)
                            val bestWeight = compareCars.minOfOrNull { it.weightKg }
                            ComparisonRow(
                                title = "Curb Weight",
                                cars = compareCars,
                                valueExtractor = { "${it.weightKg} kg" },
                                isWinner = { it.weightKg == bestWeight }
                            )

                            // Seating
                            ComparisonRow(
                                title = "Seating Capacity",
                                cars = compareCars,
                                valueExtractor = { "${it.seating} Seats" },
                                isWinner = { false }
                            )

                            // Safety Rating
                            ComparisonRow(
                                title = "Safety Rating",
                                cars = compareCars,
                                valueExtractor = { "${it.safetyRating} ★" },
                                isWinner = { false }
                            )
                        }
                    }
                }
            }
        }

        // Car Picker Modal Dialog
        if (showCarPicker) {
            Dialog(onDismissRequest = { showCarPicker = false }) {
                GlassCard(
                    modifier = Modifier
                        .fillMaxWidth(0.96f)
                        .height(520.dp),
                    shape = RoundedCornerShape(26.dp)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Choose Car to Compare",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            GlassIconButton(
                                icon = Icons.Default.Close,
                                contentDescription = "Close",
                                onClick = { showCarPicker = false },
                                size = 32.dp
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(allCars.filter { !userProfile.compareCarIds.contains(it.id) }) { car ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(Color.White.copy(alpha = 0.06f))
                                        .clickable {
                                            viewModel.toggleCompare(car.id)
                                            showCarPicker = false
                                        }
                                        .padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    AsyncImage(
                                        model = car.images.firstOrNull(),
                                        contentDescription = car.fullName,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .size(54.dp)
                                            .clip(RoundedCornerShape(10.dp))
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = car.brand.uppercase(),
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontSize = 9.sp,
                                                color = ElectricBlueLight
                                            )
                                        )
                                        Text(
                                            text = car.model,
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                            )
                                        )
                                        Text(
                                            text = "${car.horsepower} HP • ${currency.formatPrice(car.priceUsd)}",
                                            style = MaterialTheme.typography.bodySmall.copy(color = GlassTextMuted)
                                        )
                                    }
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = "Select",
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
fun ComparisonRow(
    title: String,
    cars: List<Car>,
    valueExtractor: (Car) -> String,
    isWinner: (Car) -> Boolean
) {
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = title.uppercase(),
                style = MaterialTheme.typography.labelSmall.copy(
                    color = GlassTextMuted,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.8.sp
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                for (i in 0 until 3) {
                    val car = cars.getOrNull(i)
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .then(
                                if (car != null && isWinner(car)) {
                                    Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(WinnerGreen.copy(alpha = 0.16f))
                                        .border(1.dp, WinnerGreen.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                                        .padding(horizontal = 8.dp, vertical = 6.dp)
                                } else {
                                    Modifier.padding(horizontal = 4.dp, vertical = 6.dp)
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (car != null) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                if (isWinner(car)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.EmojiEvents,
                                            contentDescription = "Winner",
                                            tint = WinnerGreen,
                                            modifier = Modifier.size(12.dp)
                                        )
                                        Spacer(modifier = Modifier.width(3.dp))
                                        Text(
                                            text = "BEST",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontSize = 9.sp,
                                                color = WinnerGreen,
                                                fontWeight = FontWeight.Black
                                            )
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                }
                                Text(
                                    text = valueExtractor(car),
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontWeight = if (isWinner(car)) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isWinner(car)) Color.White else GlassTextSecondary,
                                        textAlign = TextAlign.Center,
                                        fontSize = 11.sp
                                    ),
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        } else {
                            Text(
                                text = "—",
                                color = GlassTextMuted,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }
        }
    }
}
