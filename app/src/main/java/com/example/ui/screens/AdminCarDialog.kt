package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.model.Car
import com.example.ui.components.GlassButton
import com.example.ui.components.GlassCard
import com.example.ui.components.GlassIconButton
import com.example.ui.components.GlassSecondaryButton
import com.example.ui.components.GlassTextField
import com.example.ui.theme.WinnerGreen
import java.util.UUID

@Composable
fun AdminCarDialog(
    onDismiss: () -> Unit,
    onSaveCar: (Car) -> Unit
) {
    var brand by remember { mutableStateOf("Aston Martin") }
    var model by remember { mutableStateOf("DB12") }
    var variant by remember { mutableStateOf("Super Tourer") }
    var yearText by remember { mutableStateOf("2025") }
    var priceText by remember { mutableStateOf("245000") }
    var horsepowerText by remember { mutableStateOf("671") }
    var accelerationText by remember { mutableStateOf("3.5") }
    var topSpeedText by remember { mutableStateOf("325") }
    var engine by remember { mutableStateOf("4.0L Twin-Turbo V8") }
    var fuelType by remember { mutableStateOf("Twin-Turbo V8") }
    var bodyType by remember { mutableStateOf("Sports") }
    var country by remember { mutableStateOf("United Kingdom") }
    var imageUrl by remember { mutableStateOf("https://images.unsplash.com/photo-1617814076367-b759c7d7e738?auto=format&fit=crop&w=1400&q=80") }
    var description by remember { mutableStateOf("The Aston Martin DB12 is the world's first Super Tourer, fusing immense twin-turbo V8 muscle with exquisite craftsmanship.") }

    Dialog(onDismissRequest = onDismiss) {
        GlassCard(
            modifier = Modifier
                .fillMaxWidth(0.96f)
                .height(600.dp)
                .imePadding(),
            shape = RoundedCornerShape(26.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Admin: Add New Model",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                    GlassIconButton(
                        icon = Icons.Default.Close,
                        contentDescription = "Close",
                        onClick = onDismiss,
                        size = 32.dp
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        GlassTextField(
                            value = brand,
                            onValueChange = { brand = it },
                            placeholder = "Brand (e.g. Porsche)",
                            modifier = Modifier.weight(1f)
                        )
                        GlassTextField(
                            value = model,
                            onValueChange = { model = it },
                            placeholder = "Model (e.g. 911 GT3)",
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        GlassTextField(
                            value = variant,
                            onValueChange = { variant = it },
                            placeholder = "Variant",
                            modifier = Modifier.weight(1f)
                        )
                        GlassTextField(
                            value = yearText,
                            onValueChange = { yearText = it },
                            placeholder = "Year",
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(0.6f)
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        GlassTextField(
                            value = priceText,
                            onValueChange = { priceText = it },
                            placeholder = "Price (USD)",
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                        GlassTextField(
                            value = horsepowerText,
                            onValueChange = { horsepowerText = it },
                            placeholder = "Horsepower (HP)",
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        GlassTextField(
                            value = accelerationText,
                            onValueChange = { accelerationText = it },
                            placeholder = "0-100 km/h (s)",
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f)
                        )
                        GlassTextField(
                            value = topSpeedText,
                            onValueChange = { topSpeedText = it },
                            placeholder = "Top Speed (km/h)",
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    GlassTextField(
                        value = engine,
                        onValueChange = { engine = it },
                        placeholder = "Engine specs"
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        GlassTextField(
                            value = fuelType,
                            onValueChange = { fuelType = it },
                            placeholder = "Fuel Type",
                            modifier = Modifier.weight(1f)
                        )
                        GlassTextField(
                            value = bodyType,
                            onValueChange = { bodyType = it },
                            placeholder = "Body Type",
                            modifier = Modifier.weight(1f)
                        )
                    }

                    GlassTextField(
                        value = imageUrl,
                        onValueChange = { imageUrl = it },
                        placeholder = "Image URL (Unsplash/web)"
                    )

                    GlassTextField(
                        value = description,
                        onValueChange = { description = it },
                        placeholder = "Detailed overview description"
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    GlassSecondaryButton(
                        text = "Cancel",
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    )
                    GlassButton(
                        text = "Publish Car",
                        onClick = {
                            val newCar = Car(
                                id = "custom-${UUID.randomUUID().toString().take(8)}",
                                brand = brand.trim(),
                                model = model.trim(),
                                variant = variant.trim(),
                                year = yearText.toIntOrNull() ?: 2025,
                                bodyType = bodyType.trim(),
                                priceUsd = priceText.toDoubleOrNull() ?: 100000.0,
                                fuelType = fuelType.trim(),
                                engine = engine.trim(),
                                horsepower = horsepowerText.toIntOrNull() ?: 500,
                                torqueNm = 650,
                                transmission = "8-Speed Automatic Dual Clutch",
                                drivetrain = "AWD",
                                topSpeedKmh = topSpeedText.toIntOrNull() ?: 300,
                                acceleration0to100 = accelerationText.toDoubleOrNull() ?: 3.5,
                                mileage = "11.5 L/100km",
                                dimensions = "4,725 × 1,980 × 1,290 mm",
                                wheelbase = "2,800 mm",
                                weightKg = 1680,
                                groundClearance = "120 mm",
                                bootSpace = "320 L",
                                seating = 2,
                                tyreSize = "Front: 275/35 R21, Rear: 315/30 R21",
                                brakes = "Carbon Ceramic Ventilated Discs",
                                suspension = "Active Adaptive Dampers",
                                safetyRating = 5.0,
                                features = listOf(
                                    "Active Aerodynamics",
                                    "Full Digital Glass Cockpit",
                                    "Bowers & Wilkins Audio System",
                                    "Launch Control"
                                ),
                                images = listOf(imageUrl),
                                description = description.trim(),
                                country = country.trim(),
                                rating = 4.9,
                                reviewsCount = 1,
                                isFeatured = true,
                                isTrending = true
                            )
                            onSaveCar(newCar)
                        },
                        gradient = listOf(WinnerGreen, Color(0xFF00802B)),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}
