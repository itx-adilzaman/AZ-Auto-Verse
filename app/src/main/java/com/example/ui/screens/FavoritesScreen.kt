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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Star
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
import com.example.ui.components.GlassTextField
import com.example.ui.components.NavTab
import com.example.ui.components.liquidGlass
import com.example.ui.theme.DeepBlack
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.ElectricBlueLight
import com.example.ui.theme.GlassBorderColor
import com.example.ui.theme.GlassTextMuted
import com.example.ui.theme.GlassTextPrimary
import com.example.ui.theme.GlassTextSecondary
import com.example.ui.theme.NeonRed
import com.example.ui.viewmodel.AutoVerseViewModel

enum class FavViewTab(val label: String) {
    SAVED_CARS("Saved Cars"),
    COLLECTIONS("Custom Collections")
}

@Composable
fun FavoritesScreen(
    viewModel: AutoVerseViewModel,
    onOpenCar: (String) -> Unit
) {
    val userProfile by viewModel.userProfile.collectAsState()
    val allCars by viewModel.cars.collectAsState()
    val collections by viewModel.collections.collectAsState()
    val currency = userProfile.preferredCurrency

    var activeTab by remember { mutableStateOf(FavViewTab.SAVED_CARS) }
    var showCreateCollectionDialog by remember { mutableStateOf(false) }

    val savedCars = remember(userProfile.favoriteCarIds, allCars) {
        userProfile.favoriteCarIds.mapNotNull { id -> allCars.find { it.id == id } }
    }

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
                        text = "Garage & Saved",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = GlassTextPrimary
                        )
                    )
                    Text(
                        text = "${savedCars.size} saved vehicles • ${collections.size} curated decks",
                        style = MaterialTheme.typography.bodySmall.copy(color = GlassTextMuted)
                    )
                }

                if (activeTab == FavViewTab.COLLECTIONS) {
                    GlassSecondaryButton(
                        text = "New Deck",
                        icon = Icons.Default.Add,
                        onClick = { showCreateCollectionDialog = true }
                    )
                }
            }

            // Tabs Switcher
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                FavViewTab.entries.forEach { tab ->
                    val isSelected = activeTab == tab
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(42.dp)
                            .clip(CircleShape)
                            .background(
                                if (isSelected) ElectricBlue.copy(alpha = 0.25f)
                                else Color.White.copy(alpha = 0.08f)
                            )
                            .border(
                                width = 1.dp,
                                color = if (isSelected) ElectricBlue else GlassBorderColor,
                                shape = CircleShape
                            )
                            .clickable { activeTab = tab },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = tab.label,
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else GlassTextSecondary
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            when (activeTab) {
                FavViewTab.SAVED_CARS -> {
                    if (savedCars.isEmpty()) {
                        // Empty State
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
                                        .size(76.dp)
                                        .clip(CircleShape)
                                        .background(ElectricBlue.copy(alpha = 0.12f))
                                        .border(1.dp, ElectricBlue.copy(alpha = 0.35f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.BookmarkBorder,
                                        contentDescription = null,
                                        tint = ElectricBlueLight,
                                        modifier = Modifier.size(38.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                Text(
                                    text = "Your Garage is Empty",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Bookmark your dream cars to quickly access full specs and track price changes.",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = GlassTextSecondary,
                                        textAlign = TextAlign.Center
                                    )
                                )

                                Spacer(modifier = Modifier.height(20.dp))

                                GlassButton(
                                    text = "Explore Showroom",
                                    onClick = { viewModel.selectTab(NavTab.EXPLORE) }
                                )
                            }
                        }
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 120.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxSize().testTag("saved_cars_list")
                        ) {
                            items(savedCars) { car ->
                                GlassCard(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(125.dp),
                                    shape = RoundedCornerShape(22.dp),
                                    onClick = { onOpenCar(car.id) }
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxSize(),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        AsyncImage(
                                            model = car.images.firstOrNull(),
                                            contentDescription = car.fullName,
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier
                                                .width(135.dp)
                                                .fillMaxSize()
                                                .clip(RoundedCornerShape(topStart = 22.dp, bottomStart = 22.dp))
                                        )

                                        Column(
                                            modifier = Modifier
                                                .weight(1f)
                                                .padding(horizontal = 12.dp, vertical = 8.dp)
                                        ) {
                                            Text(
                                                text = car.brand.uppercase(),
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    color = ElectricBlueLight,
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            )
                                            Text(
                                                text = car.model,
                                                style = MaterialTheme.typography.titleMedium.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color.White
                                                ),
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = currency.formatPrice(car.priceUsd),
                                                style = MaterialTheme.typography.titleSmall.copy(
                                                    fontWeight = FontWeight.Black,
                                                    color = Color.White
                                                )
                                            )
                                        }

                                        // Delete from favorites
                                        Box(
                                            modifier = Modifier
                                                .padding(end = 12.dp)
                                        ) {
                                            GlassIconButton(
                                                icon = Icons.Default.Delete,
                                                contentDescription = "Remove",
                                                onClick = { viewModel.toggleFavorite(car.id) },
                                                size = 36.dp,
                                                tint = NeonRed
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                FavViewTab.COLLECTIONS -> {
                    LazyColumn(
                        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 120.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxSize().testTag("collections_list")
                    ) {
                        items(collections) { collection ->
                            val colCars = allCars.filter { collection.carIds.contains(it.id) }
                            GlassCard(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(22.dp)
                            ) {
                                Column(modifier = Modifier.padding(18.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Box(
                                                modifier = Modifier
                                                    .size(36.dp)
                                                    .clip(CircleShape)
                                                    .background(ElectricBlue.copy(alpha = 0.2f)),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Folder,
                                                    contentDescription = null,
                                                    tint = ElectricBlueLight,
                                                    modifier = Modifier.size(18.dp)
                                                )
                                            }
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Column {
                                                Text(
                                                    text = collection.name,
                                                    style = MaterialTheme.typography.titleMedium.copy(
                                                        fontWeight = FontWeight.Bold,
                                                        color = Color.White
                                                    )
                                                )
                                                Text(
                                                    text = collection.description,
                                                    style = MaterialTheme.typography.bodySmall.copy(
                                                        color = GlassTextSecondary
                                                    )
                                                )
                                            }
                                        }

                                        Text(
                                            text = "${colCars.size} cars",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = ElectricBlueLight,
                                                fontWeight = FontWeight.Bold
                                            )
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(14.dp))

                                    // Thumbnail row
                                    LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                        items(colCars) { c ->
                                            Box(
                                                modifier = Modifier
                                                    .width(130.dp)
                                                    .height(85.dp)
                                                    .clip(RoundedCornerShape(14.dp))
                                                    .clickable { onOpenCar(c.id) }
                                            ) {
                                                AsyncImage(
                                                    model = c.images.firstOrNull(),
                                                    contentDescription = c.fullName,
                                                    contentScale = ContentScale.Crop,
                                                    modifier = Modifier.fillMaxSize()
                                                )
                                                Box(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .align(Alignment.BottomCenter)
                                                        .background(Color.Black.copy(alpha = 0.6f))
                                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                                ) {
                                                    Text(
                                                        text = c.model,
                                                        style = MaterialTheme.typography.labelSmall.copy(
                                                            fontSize = 9.sp,
                                                            color = Color.White
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
                    }
                }
            }
        }

        // Create Collection Modal
        if (showCreateCollectionDialog) {
            Dialog(onDismissRequest = { showCreateCollectionDialog = false }) {
                var colName by remember { mutableStateOf("") }
                var colDesc by remember { mutableStateOf("") }

                GlassCard(
                    modifier = Modifier.fillMaxWidth(0.95f),
                    shape = RoundedCornerShape(26.dp)
                ) {
                    Column(modifier = Modifier.padding(22.dp)) {
                        Text(
                            text = "New Custom Collection",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        GlassTextField(
                            value = colName,
                            onValueChange = { colName = it },
                            placeholder = "Collection Name (e.g. Daily Cruisers)"
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        GlassTextField(
                            value = colDesc,
                            onValueChange = { colDesc = it },
                            placeholder = "Short description..."
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            GlassSecondaryButton(
                                text = "Cancel",
                                onClick = { showCreateCollectionDialog = false },
                                modifier = Modifier.weight(1f)
                            )
                            GlassButton(
                                text = "Create",
                                onClick = {
                                    if (colName.isNotBlank()) {
                                        viewModel.createCollection(colName, colDesc, "Folder")
                                        showCreateCollectionDialog = false
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
}
