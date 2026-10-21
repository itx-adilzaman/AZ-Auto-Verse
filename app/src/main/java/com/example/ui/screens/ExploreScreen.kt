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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CompareArrows
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.ViewList
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.model.Car
import com.example.model.CarSort
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
import com.example.ui.viewmodel.AutoVerseViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreScreen(
    viewModel: AutoVerseViewModel,
    onOpenCar: (String) -> Unit
) {
    val filteredCars by viewModel.filteredCars.collectAsState()
    val filter by viewModel.carFilter.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()
    val brands by viewModel.brands.collectAsState()

    var isGridView by remember { mutableStateOf(true) }
    var showFilterSheet by remember { mutableStateOf(false) }

    val bodyTypes = listOf("All", "Supercar", "Hypercar", "Sports", "SUV", "Sedan", "Hatchback", "Truck", "Luxury")
    val fuelTypes = listOf("All", "Petrol", "Electric", "Hybrid", "Plug-in Hybrid")
    val sortOptions = CarSort.entries

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
            // Header: Search and Filter Bar
            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Global Showroom",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = GlassTextPrimary
                        )
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        // Grid/List toggle
                        GlassIconButton(
                            icon = if (isGridView) Icons.Default.ViewList else Icons.Default.GridView,
                            contentDescription = "Toggle view",
                            onClick = { isGridView = !isGridView },
                            size = 40.dp
                        )

                        // Filter button with active indicator
                        Box {
                            GlassIconButton(
                                icon = Icons.Default.Tune,
                                contentDescription = "Open Filters",
                                onClick = { showFilterSheet = true },
                                size = 40.dp,
                                tint = if (filter.selectedBrand != null || filter.selectedBodyType != null || filter.selectedFuelType != null) ElectricBlueLight else GlassTextPrimary
                            )
                            if (filter.selectedBrand != null || filter.selectedBodyType != null || filter.selectedFuelType != null) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(ElectricBlue)
                                        .align(Alignment.TopEnd)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Search Input Field
                GlassTextField(
                    value = filter.query,
                    onValueChange = { viewModel.setSearchQuery(it) },
                    placeholder = "Filter by model, brand, engine or body...",
                    leadingIcon = Icons.Default.Search,
                    trailingIcon = if (filter.query.isNotEmpty()) Icons.Default.Close else null,
                    onTrailingIconClick = { viewModel.setSearchQuery("") },
                    modifier = Modifier.fillMaxWidth()
                )

                // Quick Body Type Pills
                Spacer(modifier = Modifier.height(12.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(bodyTypes) { type ->
                        val isSelected = (type == "All" && filter.selectedBodyType == null) || filter.selectedBodyType == type
                        Box(
                            modifier = Modifier
                                .height(34.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isSelected) ElectricBlue.copy(alpha = 0.25f)
                                    else Color.White.copy(alpha = 0.06f)
                                )
                                .border(
                                    width = 1.dp,
                                    color = if (isSelected) ElectricBlue else GlassBorderColor,
                                    shape = CircleShape
                                )
                                .clickable {
                                    viewModel.setFilterBodyType(if (type == "All") null else type)
                                }
                                .padding(horizontal = 14.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = type,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) Color.White else GlassTextSecondary,
                                    fontSize = 12.sp
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Status info & active filter chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${filteredCars.size} Models Found",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = GlassTextMuted,
                            fontWeight = FontWeight.SemiBold
                        )
                    )

                    if (filter.selectedBrand != null || filter.selectedBodyType != null || filter.selectedFuelType != null || filter.query.isNotEmpty()) {
                        Text(
                            text = "Reset All",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = NeonRed,
                                fontWeight = FontWeight.Bold
                            ),
                            modifier = Modifier
                                .clickable { viewModel.resetFilters() }
                                .padding(4.dp)
                        )
                    }
                }
            }

            // Results List / Grid
            if (filteredCars.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 110.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "No cars match your filters",
                            style = MaterialTheme.typography.titleMedium.copy(color = GlassTextSecondary)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        GlassButton(
                            text = "Clear Filters",
                            onClick = { viewModel.resetFilters() }
                        )
                    }
                }
            } else if (isGridView) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("explore_grid"),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 120.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredCars) { car ->
                        GridCarCard(
                            car = car,
                            currency = userProfile.preferredCurrency,
                            isFavorite = userProfile.favoriteCarIds.contains(car.id),
                            onFavoriteToggle = { viewModel.toggleFavorite(car.id) },
                            onClick = { onOpenCar(car.id) }
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("explore_list"),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 120.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredCars) { car ->
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

        // Advanced Filter Bottom Sheet Modal
        if (showFilterSheet) {
            ModalBottomSheet(
                onDismissRequest = { showFilterSheet = false },
                containerColor = Color(0xF50C0E18),
                scrimColor = Color.Black.copy(alpha = 0.7f),
                sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 24.dp, vertical = 16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Filter & Sort Cars",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "Reset",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = NeonRed,
                                fontWeight = FontWeight.SemiBold
                            ),
                            modifier = Modifier.clickable { viewModel.resetFilters() }
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Sort By
                    Text(
                        text = "SORT BY",
                        style = MaterialTheme.typography.labelSmall.copy(color = GlassTextMuted, fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(sortOptions) { sort ->
                            val isSelected = filter.sort == sort
                            Box(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(if (isSelected) ElectricBlue else Color.White.copy(alpha = 0.08f))
                                    .clickable { viewModel.setSort(sort) }
                                    .padding(horizontal = 14.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = sort.label,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = if (isSelected) Color.White else GlassTextSecondary,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Filter By Brand
                    Text(
                        text = "BRAND / MARQUE",
                        style = MaterialTheme.typography.labelSmall.copy(color = GlassTextMuted, fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        item {
                            val isAll = filter.selectedBrand == null
                            Box(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(if (isAll) ElectricBlue else Color.White.copy(alpha = 0.08f))
                                    .clickable { viewModel.setFilterBrand(null) }
                                    .padding(horizontal = 14.dp, vertical = 8.dp)
                            ) {
                                Text("All Brands", color = if (isAll) Color.White else GlassTextSecondary)
                            }
                        }
                        items(brands) { brand ->
                            val isSelected = filter.selectedBrand == brand.name
                            Box(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(if (isSelected) ElectricBlue else Color.White.copy(alpha = 0.08f))
                                    .clickable { viewModel.setFilterBrand(brand.name) }
                                    .padding(horizontal = 14.dp, vertical = 8.dp)
                            ) {
                                Text(brand.name, color = if (isSelected) Color.White else GlassTextSecondary)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Fuel Type
                    Text(
                        text = "POWERTRAIN & FUEL",
                        style = MaterialTheme.typography.labelSmall.copy(color = GlassTextMuted, fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(fuelTypes) { fuel ->
                            val isSelected = (fuel == "All" && filter.selectedFuelType == null) || filter.selectedFuelType == fuel
                            Box(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(if (isSelected) ElectricBlue else Color.White.copy(alpha = 0.08f))
                                    .clickable {
                                        viewModel.setFilterFuelType(if (fuel == "All") null else fuel)
                                    }
                                    .padding(horizontal = 14.dp, vertical = 8.dp)
                            ) {
                                Text(fuel, color = if (isSelected) Color.White else GlassTextSecondary)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(28.dp))

                    GlassButton(
                        text = "Show ${filteredCars.size} Results",
                        onClick = { showFilterSheet = false },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }
}

@Composable
fun GridCarCard(
    car: Car,
    currency: com.example.model.AppCurrency,
    isFavorite: Boolean,
    onFavoriteToggle: () -> Unit,
    onClick: () -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val cardImageUrl = car.images.firstOrNull()?.takeIf { it.isNotBlank() }
        ?: "https://images.unsplash.com/photo-1503376780353-7e6692767b70?auto=format&fit=crop&w=1000&q=80"

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp)
            .clip(RoundedCornerShape(20.dp))
            .border(1.dp, GlassBorderColor, RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
    ) {
        // 1. Full-Card Car Exterior Image (Entire Card Background)
        AsyncImage(
            model = coil.request.ImageRequest.Builder(context)
                .data(cardImageUrl)
                .crossfade(true)
                .setHeader("User-Agent", "Mozilla/5.0 (Linux; Android 14)")
                .build(),
            contentDescription = car.fullName,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // 2. Top Subtle Protective Vignette & Favorite Action
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(65.dp)
                .align(Alignment.TopCenter)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.65f),
                            Color.Transparent
                        )
                    )
                )
        )

        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(8.dp)
        ) {
            GlassIconButton(
                icon = if (isFavorite) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                contentDescription = null,
                onClick = onFavoriteToggle,
                size = 32.dp,
                tint = if (isFavorite) NeonRed else Color.White,
                backgroundColor = Color.Black.copy(alpha = 0.65f)
            )
        }

        // 3. Bottom Details Section with Blurred Car Image Background & Frosted Scrim
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .clip(RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp, topStart = 16.dp, topEnd = 16.dp))
        ) {
            // Blurred duplicate car image layer behind text details
            AsyncImage(
                model = coil.request.ImageRequest.Builder(context)
                    .data(cardImageUrl)
                    .crossfade(false)
                    .setHeader("User-Agent", "Mozilla/5.0 (Linux; Android 14)")
                    .build(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .matchParentSize()
                    .blur(20.dp)
            )

            // Frosted dark glass gradient backing with upper glass highlight
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color(0xCC080C18),
                                Color(0xF5050812)
                            )
                        )
                    )
                    .border(
                        width = 1.dp,
                        brush = Brush.verticalGradient(
                            listOf(
                                Color.White.copy(alpha = 0.35f),
                                Color.White.copy(alpha = 0.05f)
                            )
                        ),
                        shape = RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp, topStart = 16.dp, topEnd = 16.dp)
                    )
            )

            // High Contrast Text & Spec Content
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                Text(
                    text = car.brand.uppercase(),
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 10.sp,
                        color = ElectricBlueLight,
                        fontWeight = FontWeight.Bold,
                        shadow = Shadow(
                            color = Color.Black.copy(alpha = 0.85f),
                            offset = Offset(0f, 1f),
                            blurRadius = 4f
                        )
                    )
                )
                Text(
                    text = car.model,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        shadow = Shadow(
                            color = Color.Black.copy(alpha = 0.95f),
                            offset = Offset(0f, 1f),
                            blurRadius = 5f
                        )
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "${car.horsepower} HP • ${car.topSpeedKmh} km/h",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color.White.copy(alpha = 0.90f),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        shadow = Shadow(
                            color = Color.Black.copy(alpha = 0.85f),
                            offset = Offset(0f, 1f),
                            blurRadius = 3f
                        )
                    )
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = currency.formatPrice(car.priceUsd),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                    )

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(ElectricBlue.copy(alpha = 0.20f))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = car.drivetrain,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 9.sp,
                                color = ElectricBlueLight,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ListCarCard(
    car: Car,
    currency: com.example.model.AppCurrency,
    isFavorite: Boolean,
    isCompared: Boolean,
    onFavoriteToggle: () -> Unit,
    onCompareToggle: () -> Unit,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .liquidGlass(
                shape = RoundedCornerShape(20.dp),
                backgroundColor = Color.White.copy(alpha = 0.08f)
            )
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Thumbnail
            AsyncImage(
                model = car.images.firstOrNull(),
                contentDescription = car.fullName,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .width(130.dp)
                    .height(120.dp)
                    .clip(RoundedCornerShape(topStart = 20.dp, bottomStart = 20.dp))
            )

            // Details
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "${car.year} • ${car.brand.uppercase()}",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = ElectricBlueLight,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp
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
                Text(
                    text = "${car.horsepower} HP • 0-100 ${car.acceleration0to100}s • ${car.fuelType}",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = GlassTextMuted,
                        fontSize = 11.sp
                    ),
                    maxLines = 1
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

            // Quick actions
            Column(
                modifier = Modifier.padding(end = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                GlassIconButton(
                    icon = if (isFavorite) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                    contentDescription = null,
                    onClick = onFavoriteToggle,
                    size = 36.dp,
                    tint = if (isFavorite) NeonRed else Color.White
                )

                GlassIconButton(
                    icon = Icons.Default.CompareArrows,
                    contentDescription = null,
                    onClick = onCompareToggle,
                    size = 36.dp,
                    tint = if (isCompared) ElectricBlueLight else GlassTextMuted
                )
            }
        }
    }
}
