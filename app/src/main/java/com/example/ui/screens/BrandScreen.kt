package com.example.ui.screens

import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ui.components.GlassCard
import com.example.ui.components.GlassIconButton
import com.example.ui.components.liquidGlass
import com.example.ui.theme.DeepBlack
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.ElectricBlueLight
import com.example.ui.theme.GlassBorderColor
import com.example.ui.theme.GlassTextMuted
import com.example.ui.theme.GlassTextPrimary
import com.example.ui.theme.GlassTextSecondary
import com.example.ui.viewmodel.AutoVerseViewModel

@Composable
fun BrandScreen(
    brandId: String,
    viewModel: AutoVerseViewModel,
    onBack: () -> Unit,
    onOpenCar: (String) -> Unit
) {
    BackHandler { onBack() }

    val brand = viewModel.repository.getBrandById(brandId)
    val allCars by viewModel.cars.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()
    val brandCars = allCars.filter { it.brand.equals(brand?.name, ignoreCase = true) }

    if (brand == null) {
        Box(modifier = Modifier.fillMaxSize().background(DeepBlack), contentAlignment = Alignment.Center) {
            Text("Brand not found", color = Color.White)
        }
        return
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepBlack)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .testTag("brand_screen_scroll"),
            contentPadding = PaddingValues(bottom = 120.dp)
        ) {
            // Header & Brand Hero
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp)
                ) {
                    // Ambient Marque backdrop
                    AsyncImage(
                        model = brand.logoUrl,
                        contentDescription = brand.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        Color(0x9905060A),
                                        Color(0xD905060A),
                                        DeepBlack
                                    )
                                )
                            )
                    )

                    // Back button
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .windowInsetsPadding(WindowInsets.statusBars)
                            .padding(horizontal = 20.dp, vertical = 12.dp)
                    ) {
                        GlassIconButton(
                            icon = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            onClick = onBack
                        )
                    }

                    // Brand Badge & Name
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(20.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(CircleShape)
                                    .border(2.dp, ElectricBlue, CircleShape)
                            ) {
                                AsyncImage(
                                    model = brand.logoUrl,
                                    contentDescription = brand.name,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Column {
                                Text(
                                    text = brand.name,
                                    style = MaterialTheme.typography.displaySmall.copy(
                                        fontWeight = FontWeight.Black,
                                        color = Color.White
                                    )
                                )
                                Text(
                                    text = "Origin: ${brand.country} • Est. ${brand.foundedYear}",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = ElectricBlueLight
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // Quick Stats
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .liquidGlass(shape = RoundedCornerShape(16.dp))
                            .padding(12.dp)
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Public, null, tint = ElectricBlueLight, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("HEADQUARTERS", style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, color = GlassTextMuted))
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(brand.headquarters.substringBefore(","), style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = Color.White), maxLines = 1)
                        }
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .liquidGlass(shape = RoundedCornerShape(16.dp))
                            .padding(12.dp)
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.DirectionsCar, null, tint = ElectricBlueLight, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("CATALOG", style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, color = GlassTextMuted))
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("${brandCars.size} Models", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = Color.White))
                        }
                    }
                }
            }

            // Brand History & Description
            item {
                GlassCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 10.dp)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = "Heritage & Philosophy",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = brand.description,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = GlassTextSecondary,
                                lineHeight = 22.sp
                            )
                        )
                    }
                }
            }

            // Brand's Models List
            item {
                Text(
                    text = "${brand.name.uppercase()} SHOWROOM LINEUP",
                    style = MaterialTheme.typography.labelSmall.copy(
                        letterSpacing = 1.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = GlassTextMuted
                    ),
                    modifier = Modifier.padding(start = 20.dp, top = 20.dp, bottom = 12.dp)
                )
            }

            items(brandCars) { car ->
                Box(modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)) {
                    ListCarCard(
                        car = car,
                        currency = userProfile.preferredCurrency,
                        isFavorite = userProfile.favoriteCarIds.contains(car.id),
                        isCompared = userProfile.compareCarIds.contains(car.id),
                        onFavoriteToggle = { viewModel.toggleFavorite(car.id) },
                        onCompareToggle = { viewModel.toggleCompare(car.id) },
                        onClick = { onOpenCar(car.id) }
                    )
                }
            }
        }
    }
}
