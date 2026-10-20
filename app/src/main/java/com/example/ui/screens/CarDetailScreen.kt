package com.example.ui.screens

import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
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
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CompareArrows
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.OfflinePin
import androidx.compose.material.icons.filled.Percent
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.model.AppCurrency
import com.example.model.Car
import com.example.model.Review
import com.example.model.UnitSystem
import com.example.ui.components.GlassButton
import com.example.ui.components.GlassCard
import com.example.ui.components.GlassDialog
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
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonRed
import com.example.ui.theme.WinnerGreen
import com.example.ui.viewmodel.AutoVerseViewModel

enum class DetailTab(val label: String) {
    OVERVIEW("Overview"),
    SPECS("Specs"),
    FEATURES("Features"),
    GALLERY("Gallery"),
    REVIEWS("Reviews")
}

@Composable
fun CarDetailScreen(
    carId: String,
    viewModel: AutoVerseViewModel,
    onBack: () -> Unit,
    onOpenCar: (String) -> Unit
) {
    BackHandler { onBack() }

    val context = LocalContext.current
    val car = viewModel.repository.getCarById(carId)
    val userProfile by viewModel.userProfile.collectAsState()
    val allCars by viewModel.cars.collectAsState()
    val cachedIds by viewModel.cachedCarIds.collectAsState()
    val reviews = viewModel.repository.getReviewsForCar(carId)

    var currentTab by remember { mutableStateOf(DetailTab.OVERVIEW) }
    var selectedCurrency by remember { mutableStateOf(userProfile.preferredCurrency) }
    var showCurrencyDialog by remember { mutableStateOf(false) }
    var fullScreenImage by remember { mutableStateOf<String?>(null) }
    var showReviewDialog by remember { mutableStateOf(false) }


    if (car == null) {
        Box(
            modifier = Modifier.fillMaxSize().background(DeepBlack),
            contentAlignment = Alignment.Center
        ) {
            Text("Car not found", color = Color.White)
        }
        return
    }

    val isFavorite = userProfile.favoriteCarIds.contains(car.id)
    val isCompared = userProfile.compareCarIds.contains(car.id)
    val isOfflineCached = cachedIds.contains(car.id)
    val unitSystem = userProfile.unitSystem

    val similarCars = remember(car, allCars) {
        allCars.filter { it.id != car.id && (it.brand == car.brand || it.bodyType == car.bodyType) }.take(6)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // DYNAMIC CINEMATIC BACKGROUND: Halki si blur (gentle 8-10dp blur) with liquid glass effect
        // The car's silhouette, grille, and headlights remain clearly recognizable behind the glass!
        AsyncImage(
            model = car.images.firstOrNull(),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .blur(9.dp)
        )

        // Frosted liquid glass translucent sheen & vignette overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0x9905060C), // 60% translucent at top
                            Color(0x66080B16), // 40% translucent in center so car pic shines through
                            Color(0xE605060A)  // 90% at bottom for comfortable reading
                        )
                    )
                )
        )

        // Subtle electric ambient glow reflection
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            ElectricBlue.copy(alpha = 0.14f),
                            Color.Transparent
                        ),
                        radius = 850f
                    )
                )
        )

        // Main Content Scroll
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .testTag("car_detail_scroll"),
            contentPadding = PaddingValues(bottom = 120.dp)
        ) {
            // Top Navigation & Hero Image Pager
            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    // Floating Top Header
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .windowInsetsPadding(WindowInsets.statusBars)
                            .padding(horizontal = 20.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        GlassIconButton(
                            icon = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            onClick = onBack,
                            size = 44.dp
                        )

                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            // Share Button
                            GlassIconButton(
                                icon = Icons.Default.Share,
                                contentDescription = "Share",
                                onClick = {
                                    val sendIntent = Intent().apply {
                                        action = Intent.ACTION_SEND
                                        putExtra(
                                            Intent.EXTRA_TEXT,
                                            "Check out the ${car.fullName} (${car.horsepower} HP, 0-100 in ${car.acceleration0to100}s) on AutoVerse!"
                                        )
                                        type = "text/plain"
                                    }
                                    context.startActivity(Intent.createChooser(sendIntent, "Share Car"))
                                },
                                size = 44.dp
                            )

                            // Favorite Button
                            GlassIconButton(
                                icon = if (isFavorite) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                contentDescription = "Favorite",
                                onClick = { viewModel.toggleFavorite(car.id) },
                                size = 44.dp,
                                tint = if (isFavorite) NeonRed else Color.White
                            )
                        }
                    }

                    // Multi-Angle Image Gallery Carousel with Smooth Cinematic Edges
                    val pagerState = rememberPagerState(pageCount = { car.images.size })
                    // Car Hero Image Pager Container - Smoothed top & bottom edges, no sharp cuts
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                            .height(310.dp)
                            .clip(RoundedCornerShape(28.dp))
                            .border(
                                width = 1.dp,
                                brush = Brush.verticalGradient(
                                    listOf(
                                        Color.White.copy(alpha = 0.25f),
                                        Color.Transparent,
                                        Color.White.copy(alpha = 0.15f)
                                    )
                                ),
                                shape = RoundedCornerShape(28.dp)
                            )
                    ) {
                        HorizontalPager(
                            state = pagerState,
                            modifier = Modifier.fillMaxSize()
                        ) { page ->
                            val imgUrl = car.images[page]
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clickable { fullScreenImage = imgUrl },
                                contentAlignment = Alignment.Center
                            ) {
                                AsyncImage(
                                    model = imgUrl,
                                    contentDescription = "${car.fullName} angle $page",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }

                        // Top smooth gradient fade - removes sharp top edge
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(80.dp)
                                .align(Alignment.TopCenter)
                                .background(
                                    brush = Brush.verticalGradient(
                                        colors = listOf(
                                            Color(0xD905060A),
                                            Color(0x8005060A),
                                            Color.Transparent
                                        )
                                    )
                                )
                        )

                        // Bottom smooth gradient fade - removes sharp bottom edge
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(105.dp)
                                .align(Alignment.BottomCenter)
                                .background(
                                    brush = Brush.verticalGradient(
                                        colors = listOf(
                                            Color.Transparent,
                                            Color(0x6005060A),
                                            Color(0xE605060A)
                                        )
                                    )
                                )
                        )

                        // Pager dots indicator
                        Row(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(bottom = 14.dp),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            repeat(car.images.size) { index ->
                                val isSelected = pagerState.currentPage == index
                                Box(
                                    modifier = Modifier
                                        .size(if (isSelected) 20.dp else 6.dp, 6.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (isSelected) ElectricBlue else Color.White.copy(alpha = 0.35f)
                                        )
                                )
                            }
                        }
                    }
                }

            }

            // Car Title, Badges (Brand, Rating & Room Offline Cache Badge)
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .liquidGlass(
                                        shape = CircleShape,
                                        backgroundColor = Color.White.copy(alpha = 0.12f)
                                    )
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = "${car.year} • ${car.brand.uppercase()}",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = ElectricBlueLight,
                                        letterSpacing = 1.sp
                                    )
                                )
                            }

                            // Room Offline Caching Pill
                            if (isOfflineCached) {
                                Box(
                                    modifier = Modifier
                                        .liquidGlass(
                                            shape = CircleShape,
                                            backgroundColor = WinnerGreen.copy(alpha = 0.18f),
                                            borderColor = WinnerGreen.copy(alpha = 0.5f)
                                        )
                                        .padding(horizontal = 10.dp, vertical = 5.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(6.dp)
                                                .clip(CircleShape)
                                                .background(WinnerGreen)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "Offline Cached",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = WinnerGreen
                                            )
                                        )
                                    }
                                }
                            }
                        }

                        // Rating pill
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .liquidGlass(
                                    shape = CircleShape,
                                    backgroundColor = AmberGold.copy(alpha = 0.15f),
                                    borderColor = AmberGold.copy(alpha = 0.4f)
                                )
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = AmberGold,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${car.rating} (${car.reviewsCount})",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = car.model,
                        style = MaterialTheme.typography.displayMedium.copy(
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                    )

                    if (car.variant.isNotBlank()) {
                        Text(
                            text = car.variant,
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = GlassTextSecondary,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Live Price with Multi-Currency Switcher Dropdown
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "ESTIMATED STARTING MSRP",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = GlassTextMuted,
                                    letterSpacing = 1.sp,
                                    fontSize = 10.sp
                                )
                            )
                            Text(
                                text = selectedCurrency.formatPrice(car.priceUsd),
                                style = MaterialTheme.typography.displaySmall.copy(
                                    fontWeight = FontWeight.Black,
                                    color = Color.White
                                )
                            )
                        }

                        // Currency Selector Pill with Liquid Glass Modal
                        Box(
                            modifier = Modifier
                                .liquidGlass(
                                    shape = CircleShape,
                                    backgroundColor = Color.White.copy(alpha = 0.14f),
                                    borderColor = ElectricBlueLight.copy(alpha = 0.5f)
                                )
                                .clickable { showCurrencyDialog = true }
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = "${selectedCurrency.symbol} ${selectedCurrency.code}",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                )
                                Icon(
                                    imageVector = Icons.Default.CompareArrows,
                                    contentDescription = "Change Currency",
                                    tint = ElectricBlueLight,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }


            // REDESIGNED TOP SPEED & HIGH-IMPACT PERFORMANCE SPEC BOXES
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "PERFORMANCE BENCHMARKS",
                        style = MaterialTheme.typography.labelSmall.copy(
                            letterSpacing = 1.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = GlassTextMuted
                        ),
                        modifier = Modifier.padding(bottom = 10.dp)
                    )

                    // Row 1: Top Speed (Electric Blue) & 0-100 Acceleration (Neon Red)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        val formattedTopSpeed = if (unitSystem == UnitSystem.METRIC) {
                            "${car.topSpeedKmh}"
                        } else {
                            "${(car.topSpeedKmh * 0.621371).toInt()}"
                        }
                        val speedUnit = if (unitSystem == UnitSystem.METRIC) "KM/H" else "MPH"

                        LuxurySpecCard(
                            label = "TOP SPEED",
                            metricValue = formattedTopSpeed,
                            unitBadge = speedUnit,
                            subtext = "⚡ Hyper-Velocity",
                            icon = Icons.Default.Speed,
                            accentColor = ElectricBlue,
                            glowBrush = listOf(ElectricBlue.copy(alpha = 0.28f), Color.Transparent),
                            progressFraction = (car.topSpeedKmh / 500f).coerceIn(0.2f, 1f),
                            modifier = Modifier.weight(1f)
                        )

                        LuxurySpecCard(
                            label = "0-100 KM/H",
                            metricValue = "${car.acceleration0to100}",
                            unitBadge = "SEC",
                            subtext = if (car.acceleration0to100 < 3.0) "🚀 Sub-3s Elite" else "⏱ Instant Launch",
                            icon = Icons.Default.Timer,
                            accentColor = NeonRed,
                            glowBrush = listOf(NeonRed.copy(alpha = 0.28f), Color.Transparent),
                            progressFraction = (4.5f / car.acceleration0to100.toFloat()).coerceIn(0.2f, 1f),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Row 2: Horsepower (Gold) & Torque (Cyan)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        LuxurySpecCard(
                            label = "HORSEPOWER",
                            metricValue = "${car.horsepower}",
                            unitBadge = "BHP",
                            subtext = if (car.horsepower >= 800) "🔥 Quad-Digit Tier" else "🐎 Pure Output",
                            icon = Icons.Default.ElectricBolt,
                            accentColor = AmberGold,
                            glowBrush = listOf(AmberGold.copy(alpha = 0.25f), Color.Transparent),
                            progressFraction = (car.horsepower / 1600f).coerceIn(0.15f, 1f),
                            modifier = Modifier.weight(1f)
                        )

                        LuxurySpecCard(
                            label = "MAX TORQUE",
                            metricValue = "${car.torqueNm}",
                            unitBadge = "NM",
                            subtext = "⚙ All-Wheel Pull",
                            icon = Icons.Default.Percent,
                            accentColor = NeonCyan,
                            glowBrush = listOf(NeonCyan.copy(alpha = 0.25f), Color.Transparent),
                            progressFraction = (car.torqueNm / 1600f).coerceIn(0.15f, 1f),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Row 3: Powertrain & Energy Profile
                    LuxuryWideSpecCard(
                        label = "POWERTRAIN & FUEL CONSUMPTION",
                        engineTitle = car.engine,
                        fuelStat = car.mileage,
                        drivetrain = "${car.drivetrain} • ${car.transmission}",
                        icon = Icons.Default.LocalGasStation,
                        accentColor = ElectricBlueLight
                    )
                }
            }

            // Glass Tab Switcher: Overview | Specs | Features | Gallery | Reviews
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 14.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .liquidGlass(
                                shape = CircleShape,
                                backgroundColor = Color.White.copy(alpha = 0.08f)
                            )
                            .padding(4.dp)
                    ) {
                        LazyRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            items(DetailTab.entries) { tab ->
                                val isSelected = currentTab == tab
                                Box(
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .background(
                                            if (isSelected) ElectricBlue else Color.Transparent
                                        )
                                        .clickable { currentTab = tab }
                                        .padding(horizontal = 14.dp, vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = tab.label,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            color = if (isSelected) Color.White else GlassTextSecondary,
                                            fontSize = 12.sp
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Tab Content
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                ) {
                    when (currentTab) {
                        DetailTab.OVERVIEW -> OverviewTabContent(car = car)
                        DetailTab.SPECS -> SpecsTabContent(car = car)
                        DetailTab.FEATURES -> FeaturesTabContent(car = car)
                        DetailTab.GALLERY -> GalleryTabContent(car = car, onImageClick = { fullScreenImage = it })
                        DetailTab.REVIEWS -> ReviewsTabContent(
                            reviews = reviews,
                            onAddReviewClick = { showReviewDialog = true },
                            onLikeClick = { viewModel.toggleReviewLike(it) }
                        )
                    }
                }
            }

            // Similar Cars Horizontal Slider
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 32.dp)
                ) {
                    Text(
                        text = "SIMILAR ALTERNATIVES",
                        style = MaterialTheme.typography.labelSmall.copy(
                            letterSpacing = 1.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = GlassTextMuted
                        ),
                        modifier = Modifier.padding(horizontal = 20.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        items(similarCars) { simCar ->
                            CompactCarCard(
                                car = simCar,
                                currency = selectedCurrency,
                                isFavorite = userProfile.favoriteCarIds.contains(simCar.id),
                                onFavoriteToggle = { viewModel.toggleFavorite(simCar.id) },
                                onClick = { onOpenCar(simCar.id) }
                            )
                        }
                    }
                }
            }
        }

        // Fixed Bottom Glass Action Bar: Compare and Add to Favorites
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .windowInsetsPadding(WindowInsets.navigationBars)
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .liquidGlass(
                        shape = CircleShape,
                        backgroundColor = Color(0xF20F121E)
                    )
                    .padding(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    GlassSecondaryButton(
                        text = if (isCompared) "In Comparison" else "Compare Car",
                        icon = Icons.Default.CompareArrows,
                        onClick = { viewModel.toggleCompare(car.id) },
                        accentColor = if (isCompared) ElectricBlueLight else Color.White,
                        modifier = Modifier.weight(1f)
                    )

                    GlassButton(
                        text = if (isFavorite) "Saved in Garage" else "Add to Saved",
                        icon = if (isFavorite) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                        onClick = { viewModel.toggleFavorite(car.id) },
                        gradient = if (isFavorite) listOf(NeonRed, Color(0xFFC70039)) else listOf(ElectricBlue, Color(0xFF0051C6)),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Full Screen Lightbox Viewer with ZERO padding (true edge-to-edge full screen)
        if (fullScreenImage != null) {
            Dialog(
                onDismissRequest = { fullScreenImage = null },
                properties = DialogProperties(
                    usePlatformDefaultWidth = false,
                    decorFitsSystemWindows = false
                )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black)
                        .clickable { fullScreenImage = null },
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = fullScreenImage,
                        contentDescription = "Full Screen Car View",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.fillMaxSize()
                    )

                    // Floating Glass Top Header with Title & Close
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.TopCenter)
                            .windowInsetsPadding(WindowInsets.statusBars)
                            .padding(horizontal = 20.dp, vertical = 14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .liquidGlass(
                                    shape = CircleShape,
                                    backgroundColor = Color.Black.copy(alpha = 0.5f),
                                    borderColor = Color.White.copy(alpha = 0.25f)
                                )
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = car.fullName,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                        }

                        GlassIconButton(
                            icon = Icons.Default.Close,
                            contentDescription = "Close",
                            onClick = { fullScreenImage = null },
                            size = 46.dp,
                            backgroundColor = Color.Black.copy(alpha = 0.6f),
                            borderColor = Color.White.copy(alpha = 0.3f)
                        )
                    }
                }
            }
        }

        // Glassmorphic Currency Exchange Popup
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
                                        text = "Currency Exchange",
                                        style = MaterialTheme.typography.titleLarge.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    )
                                    Text(
                                        text = "Live exchange rate prices for ${car.fullName}",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = GlassTextSecondary
                                        )
                                    )
                                }
                                GlassIconButton(
                                    icon = Icons.Default.Close,
                                    contentDescription = "Close",
                                    onClick = { showCurrencyDialog = false },
                                    size = 36.dp
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            AppCurrency.entries.forEach { curr ->
                                val isSelected = selectedCurrency == curr
                                val convertedPrice = curr.formatPrice(car.priceUsd)
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(
                                            if (isSelected) ElectricBlue.copy(alpha = 0.24f)
                                            else Color.White.copy(alpha = 0.06f)
                                        )
                                        .border(
                                            1.dp,
                                            if (isSelected) ElectricBlueLight else Color.White.copy(alpha = 0.12f),
                                            RoundedCornerShape(16.dp)
                                        )
                                        .clickable {
                                            viewModel.updateCurrency(curr)
                                            showCurrencyDialog = false
                                        }
                                        .padding(horizontal = 14.dp, vertical = 12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(34.dp)
                                                .clip(CircleShape)
                                                .background(
                                                    if (isSelected) ElectricBlue else Color.White.copy(alpha = 0.12f)
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = curr.symbol,
                                                style = MaterialTheme.typography.titleMedium.copy(
                                                    fontWeight = FontWeight.Black,
                                                    color = Color.White
                                                )
                                            )
                                        }
                                        Column {
                                            Text(
                                                text = curr.label,
                                                style = MaterialTheme.typography.bodyMedium.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color.White
                                                )
                                            )
                                            Text(
                                                text = curr.code,
                                                style = MaterialTheme.typography.bodySmall.copy(
                                                    color = GlassTextMuted,
                                                    fontSize = 11.sp
                                                )
                                            )
                                        }
                                    }

                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(
                                            text = convertedPrice,
                                            style = MaterialTheme.typography.titleSmall.copy(
                                                fontWeight = FontWeight.Black,
                                                color = if (isSelected) ElectricBlueLight else Color.White
                                            )
                                        )
                                        if (isSelected) {
                                            Text(
                                                text = "Selected",
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    color = WinnerGreen,
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
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

        // Write a Review Dialog
        if (showReviewDialog) {
            WriteReviewDialog(
                carName = car.fullName,
                onDismiss = { showReviewDialog = false },
                onSubmit = { title, comment, rating ->
                    viewModel.addReview(car.id, title, comment, rating)
                    showReviewDialog = false
                }
            )
        }
    }
}

/**
 * Luxury Hyper-Spec Card for Top Speed, 0-100, HP, and Torque.
 * Features glowing accent halo, bold display numbers, neon badge units, and micro gauge.
 */
@Composable
fun LuxurySpecCard(
    label: String,
    metricValue: String,
    unitBadge: String,
    subtext: String,
    icon: ImageVector,
    accentColor: Color,
    glowBrush: List<Color>,
    progressFraction: Float,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1f,
        label = "specCardScale"
    )

    Box(
        modifier = modifier
            .height(132.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clip(RoundedCornerShape(22.dp))

            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.16f),
                        Color.White.copy(alpha = 0.09f)
                    )
                )
            )
            .background(
                brush = Brush.radialGradient(
                    colors = glowBrush,
                    radius = 380f
                )
            )
            .border(
                width = 1.2.dp,
                brush = Brush.verticalGradient(
                    listOf(
                        accentColor.copy(alpha = 0.70f),
                        Color.White.copy(alpha = 0.22f)
                    )
                ),
                shape = RoundedCornerShape(22.dp)
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = {}
            )
            .padding(14.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Row: Category label & Icon badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = GlassTextMuted,
                        letterSpacing = 1.sp,
                        fontSize = 10.sp
                    )
                )

                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(accentColor.copy(alpha = 0.18f))
                        .border(1.dp, accentColor.copy(alpha = 0.45f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(15.dp)
                    )
                }
            }

            // Center: Big Bold Metric & Unit Badge
            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = metricValue,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        fontSize = 28.sp,
                        letterSpacing = (-0.5).sp
                    )
                )

                Box(
                    modifier = Modifier
                        .padding(bottom = 4.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(accentColor.copy(alpha = 0.25f))
                        .border(0.8.dp, accentColor.copy(alpha = 0.6f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = unitBadge,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Black,
                            color = Color.White,
                            fontSize = 9.sp
                        )
                    )
                }
            }

            // Bottom: Micro progress bar & Subtext
            Column {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(3.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.1f))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(progressFraction)
                            .height(3.dp)
                            .clip(CircleShape)
                            .background(accentColor)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = subtext,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = GlassTextSecondary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    maxLines = 1
                )
            }
        }
    }
}

/**
 * Wide banner card for Powertrain & Fuel/Energy profile.
 */
@Composable
fun LuxuryWideSpecCard(
    label: String,
    engineTitle: String,
    fuelStat: String,
    drivetrain: String,
    icon: ImageVector,
    accentColor: Color
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .liquidGlass(
                shape = RoundedCornerShape(22.dp),
                backgroundColor = Color.White.copy(alpha = 0.08f),
                borderColor = accentColor.copy(alpha = 0.35f)
            )
            .padding(16.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .clip(CircleShape)
                            .background(accentColor.copy(alpha = 0.2f))
                            .border(1.dp, accentColor.copy(alpha = 0.5f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = accentColor,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelSmall.copy(
                            letterSpacing = 1.sp,
                            fontWeight = FontWeight.Bold,
                            color = GlassTextMuted,
                            fontSize = 10.sp
                        )
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.White.copy(alpha = 0.1f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = fuelStat,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 11.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = engineTitle,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = drivetrain,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = GlassTextSecondary,
                    fontSize = 12.sp
                )
            )
        }
    }
}

@Composable
fun OverviewTabContent(car: Car) {
    Column {
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = "Engineering Overview",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = car.description,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = GlassTextSecondary,
                        lineHeight = 22.sp
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Visual Performance Gauges
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = "Performance Radar",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(16.dp))

                PerformanceBar(
                    label = "Acceleration (0-100 km/h)",
                    valueText = "${car.acceleration0to100}s",
                    fraction = (4.5f / car.acceleration0to100.toFloat()).coerceIn(0.2f, 1f),
                    color = ElectricBlue
                )

                Spacer(modifier = Modifier.height(12.dp))

                PerformanceBar(
                    label = "Horsepower Index",
                    valueText = "${car.horsepower} HP",
                    fraction = (car.horsepower / 1600f).coerceIn(0.1f, 1f),
                    color = NeonRed
                )

                Spacer(modifier = Modifier.height(12.dp))

                PerformanceBar(
                    label = "Top Velocity",
                    valueText = "${car.topSpeedKmh} km/h",
                    fraction = (car.topSpeedKmh / 500f).coerceIn(0.2f, 1f),
                    color = WinnerGreen
                )

                Spacer(modifier = Modifier.height(12.dp))

                PerformanceBar(
                    label = "Torque Muscle",
                    valueText = "${car.torqueNm} Nm",
                    fraction = (car.torqueNm / 1600f).coerceIn(0.15f, 1f),
                    color = AmberGold
                )
            }
        }
    }
}

@Composable
fun PerformanceBar(
    label: String,
    valueText: String,
    fraction: Float,
    color: Color
) {
    val animatedFraction by animateFloatAsState(
        targetValue = fraction,
        animationSpec = tween(800),
        label = "perfBar"
    )

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall.copy(color = GlassTextSecondary)
            )
            Text(
                text = valueText,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.1f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(animatedFraction)
                    .height(8.dp)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.horizontalGradient(
                            listOf(color.copy(alpha = 0.5f), color)
                        )
                    )
            )
        }
    }
}

@Composable
fun SpecsTabContent(car: Car) {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        SpecCategoryCard(
            categoryTitle = "Powertrain & Engine",
            specs = listOf(
                "Engine Type" to car.engine,
                "Horsepower" to "${car.horsepower} hp",
                "Torque" to "${car.torqueNm} Nm",
                "Fuel Type" to car.fuelType,
                "Transmission" to car.transmission,
                "Drivetrain" to car.drivetrain
            )
        )

        SpecCategoryCard(
            categoryTitle = "Performance Benchmarks",
            specs = listOf(
                "0 to 100 km/h" to "${car.acceleration0to100} seconds",
                "Top Speed" to "${car.topSpeedKmh} km/h",
                "Fuel / Electric Consumption" to car.mileage,
                "Safety Rating" to "${car.safetyRating} / 5.0 Stars"
            )
        )

        SpecCategoryCard(
            categoryTitle = "Chassis, Dimensions & Weight",
            specs = listOf(
                "Dimensions (L × W × H)" to car.dimensions,
                "Wheelbase" to car.wheelbase,
                "Curb Weight" to "${car.weightKg} kg",
                "Ground Clearance" to car.groundClearance,
                "Luggage Capacity" to car.bootSpace,
                "Seating Capacity" to "${car.seating} Seats"
            )
        )

        SpecCategoryCard(
            categoryTitle = "Suspension & Brakes",
            specs = listOf(
                "Suspension" to car.suspension,
                "Braking System" to car.brakes,
                "Tyres & Wheels" to car.tyreSize,
                "Country of Engineering" to car.country
            )
        )
    }
}

@Composable
fun SpecCategoryCard(
    categoryTitle: String,
    specs: List<Pair<String, String>>
) {
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = categoryTitle,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = ElectricBlueLight
                )
            )
            Spacer(modifier = Modifier.height(12.dp))
            specs.forEachIndexed { index, (label, value) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.bodyMedium.copy(color = GlassTextMuted),
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = value,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        ),
                        modifier = Modifier.weight(1.2f),
                        textAlign = androidx.compose.ui.text.style.TextAlign.End
                    )
                }
                if (index < specs.size - 1) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(0.6.dp)
                            .background(Color.White.copy(alpha = 0.08f))
                    )
                }
            }
        }
    }
}

@Composable
fun FeaturesTabContent(car: Car) {
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = "Key Equipment & Innovations",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(14.dp))
            car.features.forEach { feature ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 7.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(ElectricBlue.copy(alpha = 0.2f))
                            .border(1.dp, ElectricBlue.copy(alpha = 0.5f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = ElectricBlueLight,
                            modifier = Modifier.size(15.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = feature,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = GlassTextPrimary,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun GalleryTabContent(
    car: Car,
    onImageClick: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        car.images.forEachIndexed { index, imgUrl ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(210.dp)
                    .liquidGlass(
                        shape = RoundedCornerShape(20.dp),
                        backgroundColor = Color.White.copy(alpha = 0.08f)
                    )
                    .clickable { onImageClick(imgUrl) }
            ) {
                AsyncImage(
                    model = imgUrl,
                    contentDescription = "${car.fullName} view $index",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(12.dp)
                        .liquidGlass(
                            shape = CircleShape,
                            backgroundColor = Color.Black.copy(alpha = 0.5f)
                        )
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "Tap to view full screen",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color.White,
                            fontSize = 10.sp
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun ReviewsTabContent(
    reviews: List<Review>,
    onAddReviewClick: () -> Unit,
    onLikeClick: (String) -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Owner & Critic Reviews",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
            GlassSecondaryButton(
                text = "Write Review",
                icon = Icons.Default.Add,
                onClick = onAddReviewClick
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        if (reviews.isEmpty()) {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Be the first to review this car!",
                        style = MaterialTheme.typography.bodyMedium.copy(color = GlassTextSecondary)
                    )
                }
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                reviews.forEach { rev ->
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
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
                                            .border(1.dp, GlassBorderColor, CircleShape)
                                    ) {
                                        AsyncImage(
                                            model = rev.userAvatar,
                                            contentDescription = rev.userName,
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier.fillMaxSize()
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = rev.userName,
                                            style = MaterialTheme.typography.labelMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                            )
                                        )
                                        Text(
                                            text = rev.date,
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = GlassTextMuted,
                                                fontSize = 11.sp
                                            )
                                        )
                                    }
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = null,
                                        tint = AmberGold,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(
                                        text = "${rev.rating}",
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = rev.title,
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = rev.comment,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = GlassTextSecondary,
                                    lineHeight = 20.sp
                                )
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .background(Color.White.copy(alpha = 0.08f))
                                        .clickable { onLikeClick(rev.id) }
                                        .padding(horizontal = 10.dp, vertical = 5.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = if (rev.isLikedByUser) Icons.Filled.ThumbUp else Icons.Outlined.ThumbUp,
                                        contentDescription = "Like",
                                        tint = if (rev.isLikedByUser) ElectricBlueLight else GlassTextMuted,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "${rev.likes}",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = if (rev.isLikedByUser) ElectricBlueLight else GlassTextMuted,
                                            fontWeight = FontWeight.Bold
                                        )
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
fun WriteReviewDialog(
    carName: String,
    onDismiss: () -> Unit,
    onSubmit: (String, String, Float) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var comment by remember { mutableStateOf("") }
    var rating by remember { mutableStateOf(5.0f) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.70f))
                .clickable(onClick = onDismiss),
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
                Column(modifier = Modifier.padding(24.dp)) {
                    Text(
                        text = "Write Owner Review",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                    Text(
                        text = "Share verified telemetry & driving impressions for $carName",
                        style = MaterialTheme.typography.bodySmall.copy(color = GlassTextSecondary)
                    )

                Spacer(modifier = Modifier.height(16.dp))

                // Star Rating Picker
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    for (i in 1..5) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = if (i <= rating) AmberGold else Color.White.copy(alpha = 0.2f),
                            modifier = Modifier
                                .size(32.dp)
                                .clickable { rating = i.toFloat() }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                GlassTextField(
                    value = title,
                    onValueChange = { title = it },
                    placeholder = "Review Title (e.g. Unbelievable track weapon)"
                )

                Spacer(modifier = Modifier.height(12.dp))

                GlassTextField(
                    value = comment,
                    onValueChange = { comment = it },
                    placeholder = "Detailed impressions, dynamics, interior feel...",
                    modifier = Modifier.height(100.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    GlassSecondaryButton(
                        text = "Cancel",
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    )
                    GlassButton(
                        text = "Publish",
                        onClick = {
                            if (title.isNotBlank() && comment.isNotBlank()) {
                                onSubmit(title, comment, rating)
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

