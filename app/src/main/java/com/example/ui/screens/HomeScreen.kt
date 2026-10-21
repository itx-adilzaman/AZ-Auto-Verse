package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CompareArrows
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.delay
import coil.compose.AsyncImage
import com.example.R
import com.example.model.AutomotiveAd
import com.example.model.Brand
import com.example.model.Car
import com.example.ui.components.GlassCard

import com.example.ui.components.GlassIconButton
import com.example.ui.components.NavTab
import com.example.ui.components.liquidGlass
import com.example.ui.theme.AmberGold
import com.example.ui.theme.DeepBlack
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.ElectricBlueLight
import com.example.ui.theme.GlassBorderColor
import com.example.ui.theme.GlassCardBg
import com.example.ui.theme.GlassTextMuted
import com.example.ui.theme.GlassTextPrimary
import com.example.ui.theme.GlassTextSecondary
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonRed
import com.example.ui.theme.WinnerGreen
import com.example.ui.viewmodel.AutoVerseViewModel

@Composable
fun HomeScreen(
    viewModel: AutoVerseViewModel,
    onOpenCar: (String) -> Unit,
    onOpenBrand: (String) -> Unit,
    onOpenSearch: () -> Unit,
    onOpenProfile: () -> Unit
) {
    val cars by viewModel.cars.collectAsState()
    val brands by viewModel.brands.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()
    val automotiveAds by viewModel.automotiveAds.collectAsState()
    val isNeonConnected by viewModel.isNeonConnected.collectAsState()

    var selectedCategory by remember { mutableStateOf("All") }
    val categories = listOf("All", "Supercars", "Electric", "Luxury", "SUVs", "Budget Friendly")

    val featuredCars = remember(cars) { cars.filter { it.isFeatured } }
    val trendingCars = remember(cars) { cars.filter { it.isTrending } }
    val electricCars = remember(cars) { cars.filter { it.isElectric } }
    val supercarCars = remember(cars) { cars.filter { it.isSupercar } }
    val suvCars = remember(cars) { cars.filter { it.isSUV } }

    val categoryFilteredCars = remember(selectedCategory, cars) {
        when (selectedCategory) {
            "Supercars" -> cars.filter { it.isSupercar }
            "Electric" -> cars.filter { it.isElectric }
            "Luxury" -> cars.filter { it.bodyType == "Luxury" || it.priceUsd > 200000.0 }
            "SUVs" -> cars.filter { it.isSUV }
            "Budget Friendly" -> cars.filter { it.priceUsd < 65000.0 }
            else -> cars
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepBlack)
    ) {
        // Ambient background glow
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            ElectricBlue.copy(alpha = 0.08f),
                            Color.Transparent
                        ),
                        radius = 800f
                    )
                )
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .testTag("home_screen_list"),
            contentPadding = PaddingValues(bottom = 110.dp)
        ) {
            // Header: User greeting, App badge & Profile avatar
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .windowInsetsPadding(WindowInsets.statusBars)
                        .padding(horizontal = 20.dp, vertical = 14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .border(1.dp, ElectricBlue.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                                    .background(Color(0xFF0C101A)),
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.ic_autoverse_logo),
                                    contentDescription = null,
                                    modifier = Modifier.size(34.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "Hello, ${userProfile.name.substringBefore(" ")}",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = GlassTextPrimary
                                        )
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    // Neon Live Indicator
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier
                                            .clip(CircleShape)
                                            .background(if (isNeonConnected) WinnerGreen.copy(alpha = 0.15f) else ElectricBlue.copy(alpha = 0.15f))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(5.dp)
                                                .clip(CircleShape)
                                                .background(if (isNeonConnected) WinnerGreen else NeonCyan)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = if (isNeonConnected) "Neon DB Live" else "Neon Connecting",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = if (isNeonConnected) WinnerGreen else NeonCyan,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        )
                                    }
                                }
                                Text(
                                    text = if (userProfile.id.startsWith("google")) "Google Verified • 27 Cars in Neon" else "Explore 30+ Global Marques",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = ElectricBlueLight,
                                        fontSize = 11.sp
                                    )
                                )
                            }
                        }

                        // Glass Profile Avatar
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .border(1.5.dp, ElectricBlue.copy(alpha = 0.6f), CircleShape)
                                .clickable { onOpenProfile() }
                                .testTag("home_avatar_btn")
                        ) {
                            val homeCtx = androidx.compose.ui.platform.LocalContext.current
                            AsyncImage(
                                model = coil.request.ImageRequest.Builder(homeCtx)
                                    .data(userProfile.avatarUrl)
                                    .crossfade(true)
                                    .setHeader("User-Agent", "Mozilla/5.0 (Linux; Android 14)")
                                    .build(),
                                contentDescription = "Profile",
                                contentScale = ContentScale.Crop,
                                alignment = Alignment.TopCenter,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Glass Search Bar
                    GlassCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("home_search_bar"),
                        shape = RoundedCornerShape(20.dp),
                        backgroundColor = Color.White.copy(alpha = 0.08f),
                        onClick = onOpenSearch
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "Search",
                                    tint = GlassTextSecondary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = "Search Ferrari, Porsche, Tesla...",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = GlassTextMuted
                                    )
                                )
                            }
                            Icon(
                                imageVector = Icons.Default.Tune,
                                contentDescription = "Filter",
                                tint = ElectricBlue,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }

            // Brands Horizontal Bar
            item {
                Column(modifier = Modifier.fillMaxWidth().padding(top = 10.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "TOP MARQUES",
                            style = MaterialTheme.typography.labelSmall.copy(
                                letterSpacing = 1.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = GlassTextMuted
                            )
                        )
                        Text(
                            text = "View All (${brands.size})",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = ElectricBlueLight,
                                fontWeight = FontWeight.SemiBold
                            ),
                            modifier = Modifier.clickable {
                                viewModel.selectTab(NavTab.EXPLORE)
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(brands) { brand ->
                            BrandGlassChip(
                                brand = brand,
                                onClick = { onOpenBrand(brand.id) }
                            )
                        }
                    }
                }
            }

            // Featured Cars Hero Carousel
            item {
                Column(modifier = Modifier.fillMaxWidth().padding(top = 26.dp)) {
                    Text(
                        text = "FEATURED HYPERCARS",
                        style = MaterialTheme.typography.labelSmall.copy(
                            letterSpacing = 1.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = GlassTextMuted
                        ),
                        modifier = Modifier.padding(horizontal = 20.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(featuredCars) { car ->
                            HeroFeaturedCard(
                                car = car,
                                currency = userProfile.preferredCurrency,
                                isFavorite = userProfile.favoriteCarIds.contains(car.id),
                                onFavoriteToggle = { viewModel.toggleFavorite(car.id) },
                                onCompareToggle = { viewModel.toggleCompare(car.id) },
                                onClick = { onOpenCar(car.id) }
                            )
                        }
                    }
                }
            }

            // Luxury Automotive Ads & Sponsor Carousel
            item {
                AutomotiveAdsCarousel(
                    ads = automotiveAds,
                    onAdClick = { ad ->
                        viewModel.showNotification("Exploring ${ad.sponsorName}: ${ad.headline}")
                    }
                )
            }

            // Category Filter Pills
            item {
                Column(modifier = Modifier.fillMaxWidth().padding(top = 28.dp)) {

                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(categories) { cat ->
                            val isSelected = cat == selectedCategory
                            Box(
                                modifier = Modifier
                                    .height(38.dp)
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
                                    .clickable { selectedCategory = cat }
                                    .padding(horizontal = 16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = cat,
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) Color.White else GlassTextSecondary,
                                        fontSize = 13.sp
                                    )
                                )
                            }
                        }
                    }

                    // Horizontal list of cars matching category
                    Spacer(modifier = Modifier.height(14.dp))

                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        items(categoryFilteredCars) { car ->
                            CompactCarCard(
                                car = car,
                                currency = userProfile.preferredCurrency,
                                isFavorite = userProfile.favoriteCarIds.contains(car.id),
                                onFavoriteToggle = { viewModel.toggleFavorite(car.id) },
                                onClick = { onOpenCar(car.id) }
                            )
                        }
                    }
                }
            }

            // Trending Now Section
            item {
                CarSectionRow(
                    title = "TRENDING RIGHT NOW",
                    cars = trendingCars,
                    currency = userProfile.preferredCurrency,
                    favoriteIds = userProfile.favoriteCarIds,
                    onFavoriteToggle = { viewModel.toggleFavorite(it) },
                    onCarClick = onOpenCar
                )
            }

            // Electric Revolution Section
            item {
                CarSectionRow(
                    title = "ELECTRIC REVOLUTION",
                    cars = electricCars,
                    currency = userProfile.preferredCurrency,
                    favoriteIds = userProfile.favoriteCarIds,
                    onFavoriteToggle = { viewModel.toggleFavorite(it) },
                    onCarClick = onOpenCar
                )
            }

            // Track Supercars Section
            item {
                CarSectionRow(
                    title = "TRACK MONSTERS & SUPERCARS",
                    cars = supercarCars,
                    currency = userProfile.preferredCurrency,
                    favoriteIds = userProfile.favoriteCarIds,
                    onFavoriteToggle = { viewModel.toggleFavorite(it) },
                    onCarClick = onOpenCar
                )
            }

            // Luxury SUVs Section
            item {
                CarSectionRow(
                    title = "PRESTIGE ALL-TERRAIN & SUVS",
                    cars = suvCars,
                    currency = userProfile.preferredCurrency,
                    favoriteIds = userProfile.favoriteCarIds,
                    onFavoriteToggle = { viewModel.toggleFavorite(it) },
                    onCarClick = onOpenCar
                )
            }
        }
    }
}

@Composable
fun BrandGlassChip(
    brand: Brand,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .height(44.dp)
            .liquidGlass(
                shape = CircleShape,
                backgroundColor = Color.White.copy(alpha = 0.08f)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .border(0.8.dp, GlassBorderColor, CircleShape)
            ) {
                AsyncImage(
                    model = brand.logoUrl,
                    contentDescription = brand.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = brand.name,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = GlassTextPrimary
                )
            )
        }
    }
}

@Composable
fun HeroFeaturedCard(
    car: Car,
    currency: com.example.model.AppCurrency,
    isFavorite: Boolean,
    onFavoriteToggle: () -> Unit,
    onCompareToggle: () -> Unit,
    onClick: () -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val heroImageUrl = car.images.firstOrNull()?.takeIf { it.isNotBlank() }
        ?: "https://images.unsplash.com/photo-1592198084033-aade902d1aae?auto=format&fit=crop&w=1400&q=80"

    Box(
        modifier = Modifier
            .width(320.dp)
            .height(340.dp)
            .clip(RoundedCornerShape(26.dp))
            .border(1.dp, GlassBorderColor, RoundedCornerShape(26.dp))
            .clickable(onClick = onClick)
    ) {
        // 1. Full-Card Car Exterior Image
        AsyncImage(
            model = coil.request.ImageRequest.Builder(context)
                .data(heroImageUrl)
                .crossfade(true)
                .setHeader("User-Agent", "Mozilla/5.0 (Linux; Android 14)")
                .build(),
            contentDescription = car.fullName,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // 2. Top Subtle Protective Vignette for Badge Readability
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(90.dp)
                .align(Alignment.TopCenter)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.70f),
                            Color.Transparent
                        )
                    )
                )
        )

        // 3. Top Badges & Actions (Brand pill + Compare/Favorite buttons)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
                .align(Alignment.TopCenter),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .liquidGlass(shape = CircleShape, backgroundColor = Color.Black.copy(alpha = 0.60f))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "${car.year} • ${car.brand}",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                GlassIconButton(
                    icon = Icons.Default.CompareArrows,
                    contentDescription = "Compare",
                    onClick = onCompareToggle,
                    size = 36.dp,
                    tint = ElectricBlueLight,
                    backgroundColor = Color.Black.copy(alpha = 0.65f)
                )

                GlassIconButton(
                    icon = if (isFavorite) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                    contentDescription = "Favorite",
                    onClick = onFavoriteToggle,
                    size = 36.dp,
                    tint = if (isFavorite) NeonRed else Color.White,
                    backgroundColor = Color.Black.copy(alpha = 0.65f)
                )
            }
        }

        // 4. Bottom Car Details Section with Frosted Blurred Car Background
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .clip(RoundedCornerShape(bottomStart = 26.dp, bottomEnd = 26.dp, topStart = 20.dp, topEnd = 20.dp))
        ) {
            // Blurred duplicate car image layer behind text details (halka sa blur)
            AsyncImage(
                model = coil.request.ImageRequest.Builder(context)
                    .data(heroImageUrl)
                    .crossfade(false)
                    .setHeader("User-Agent", "Mozilla/5.0 (Linux; Android 14)")
                    .build(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .matchParentSize()
                    .blur(12.dp)
            )

            // Ultra-light translucent glass tint with top edge highlight
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color(0x2E000000), // 18% soft tint - car image completely visible
                                Color(0x8C050812)  // 55% soft glass - keeps text super sharp
                            )
                        )
                    )
                    .border(
                        width = 1.dp,
                        brush = Brush.verticalGradient(
                            listOf(
                                Color.White.copy(alpha = 0.30f),
                                Color.White.copy(alpha = 0.05f)
                            )
                        ),
                        shape = RoundedCornerShape(bottomStart = 26.dp, bottomEnd = 26.dp, topStart = 20.dp, topEnd = 20.dp)
                    )
            )

            // High Contrast Details & Specs Content
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = car.model,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White,
                                shadow = Shadow(
                                    color = Color.Black.copy(alpha = 0.9f),
                                    offset = Offset(0f, 2f),
                                    blurRadius = 6f
                                )
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        if (car.variant.isNotBlank()) {
                            Text(
                                text = car.variant,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = NeonCyan,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 11.sp,
                                    shadow = Shadow(
                                        color = Color.Black.copy(alpha = 0.8f),
                                        offset = Offset(0f, 1f),
                                        blurRadius = 4f
                                    )
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    // Price Tag Badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(ElectricBlue.copy(alpha = 0.25f))
                            .border(1.dp, ElectricBlue.copy(alpha = 0.55f), RoundedCornerShape(12.dp))
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = currency.formatPrice(car.priceUsd),
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Specs pill row with dark glass backing
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.Black.copy(alpha = 0.45f))
                        .border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 12.dp, vertical = 7.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    SpecMicro(label = "0-100", value = "${car.acceleration0to100}s")
                    SpecMicro(label = "POWER", value = "${car.horsepower} HP")
                    SpecMicro(label = "TOP SPEED", value = "${car.topSpeedKmh} km/h")
                    SpecMicro(label = "DRIVE", value = car.drivetrain)
                }
            }
        }
    }
}

@Composable
fun SpecMicro(label: String, value: String) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 9.sp,
                color = GlassTextMuted,
                fontWeight = FontWeight.Bold
            )
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall.copy(
                fontSize = 11.sp,
                color = Color.White,
                fontWeight = FontWeight.SemiBold
            )
        )
    }
}

@Composable
fun CompactCarCard(
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
            .width(220.dp)
            .height(245.dp)
            .clip(RoundedCornerShape(22.dp))
            .border(1.dp, GlassBorderColor, RoundedCornerShape(22.dp))
            .clickable(onClick = onClick)
    ) {
        // 1. Full Card Car Image (Entire Card Background)
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

        // 2. Top Vignette & Favorite Action
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
                .clip(RoundedCornerShape(bottomStart = 22.dp, bottomEnd = 22.dp, topStart = 16.dp, topEnd = 16.dp))
        ) {
            // Blurred duplicate car image layer behind text details (halka sa blur)
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
                    .blur(12.dp)
            )

            // Ultra-light translucent glass tint with top edge highlight
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color(0x2E000000), // 18% soft tint - car image completely visible
                                Color(0x8C050812)  // 55% soft glass - keeps text super sharp
                            )
                        )
                    )
                    .border(
                        width = 1.dp,
                        brush = Brush.verticalGradient(
                            listOf(
                                Color.White.copy(alpha = 0.30f),
                                Color.White.copy(alpha = 0.05f)
                            )
                        ),
                        shape = RoundedCornerShape(bottomStart = 22.dp, bottomEnd = 22.dp, topStart = 16.dp, topEnd = 16.dp)
                    )
            )

            // High Contrast Details
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                Text(
                    text = car.brand.uppercase(),
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 10.sp,
                        color = NeonCyan,
                        fontWeight = FontWeight.Bold,
                        shadow = Shadow(
                            color = Color.Black.copy(alpha = 0.8f),
                            offset = Offset(0f, 1f),
                            blurRadius = 4f
                        )
                    )
                )
                Text(
                    text = car.model,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        shadow = Shadow(
                            color = Color.Black.copy(alpha = 0.9f),
                            offset = Offset(0f, 1f),
                            blurRadius = 4f
                        )
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${car.horsepower} HP • ${car.acceleration0to100}s",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color.White.copy(alpha = 0.85f),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            shadow = Shadow(
                                color = Color.Black.copy(alpha = 0.8f),
                                offset = Offset(0f, 1f),
                                blurRadius = 3f
                            )
                        )
                    )
                    Text(
                        text = currency.formatPrice(car.priceUsd),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun CarSectionRow(
    title: String,
    cars: List<Car>,
    currency: com.example.model.AppCurrency,
    favoriteIds: Set<String>,
    onFavoriteToggle: (String) -> Unit,
    onCarClick: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth().padding(top = 28.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall.copy(
                letterSpacing = 1.5.sp,
                fontWeight = FontWeight.Bold,
                color = GlassTextMuted
            ),
            modifier = Modifier.padding(horizontal = 20.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(cars) { car ->
                CompactCarCard(
                    car = car,
                    currency = currency,
                    isFavorite = favoriteIds.contains(car.id),
                    onFavoriteToggle = { onFavoriteToggle(car.id) },
                    onClick = { onCarClick(car.id) }
                )
            }
        }
    }
}

@Composable
fun AutomotiveAdsCarousel(
    ads: List<AutomotiveAd>,
    onAdClick: (AutomotiveAd) -> Unit
) {
    if (ads.isEmpty()) return
    val pagerState = rememberPagerState(pageCount = { ads.size })

    // Auto-advance banner every 4.5 seconds
    LaunchedEffect(pagerState) {
        while (true) {
            delay(4500)
            val next = (pagerState.currentPage + 1) % ads.size
            pagerState.animateScrollToPage(next)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 28.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(AmberGold)
                )
                Text(
                    text = "PARTNER SPOTLIGHT",
                    style = MaterialTheme.typography.labelSmall.copy(
                        letterSpacing = 1.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = GlassTextMuted
                    )
                )
            }
            Text(
                text = "Official Sponsor",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = Color.White.copy(alpha = 0.4f),
                    fontSize = 11.sp
                )
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        HorizontalPager(
            state = pagerState,
            contentPadding = PaddingValues(horizontal = 20.dp),
            pageSpacing = 14.dp,
            modifier = Modifier.fillMaxWidth()
        ) { page ->
            val ad = ads[page]
            val accentColor = Color(ad.accentColorHex)

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(178.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .clickable { onAdClick(ad) }
                    .border(
                        1.dp,
                        Brush.horizontalGradient(
                            listOf(
                                accentColor.copy(alpha = 0.65f),
                                Color.White.copy(alpha = 0.15f)
                            )
                        ),
                        RoundedCornerShape(24.dp)
                    )
            ) {
                // Background photography
                AsyncImage(
                    model = ad.imageUrl,
                    contentDescription = ad.headline,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Dark translucent gradient overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    Color(0xF5080A12),
                                    Color(0xD9080A12),
                                    Color(0x50080A12)
                                )
                            )
                        )
                )

                // Ambient halo glow
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    accentColor.copy(alpha = 0.25f),
                                    Color.Transparent
                                ),
                                radius = 450f
                            )
                        )
                )

                // Text and action layout
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(18.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        // Partner Badge
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(accentColor.copy(alpha = 0.20f))
                                .border(1.dp, accentColor.copy(alpha = 0.5f), CircleShape)
                                .padding(horizontal = 10.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "${ad.sponsorName} • ${ad.partnerBadge}",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Color.White,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.5.sp
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = ad.headline,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Black,
                                color = Color.White,
                                fontSize = 17.sp
                            )
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = ad.subline,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = GlassTextSecondary,
                                fontSize = 11.sp,
                                lineHeight = 15.sp
                            ),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.fillMaxWidth(0.85f)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(accentColor)
                                .padding(horizontal = 14.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = ad.ctaText,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black,
                                    fontSize = 11.sp
                                )
                            )
                        }

                        // Dot indicators
                        Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                            repeat(ads.size) { idx ->
                                val isSelected = pagerState.currentPage == idx
                                Box(
                                    modifier = Modifier
                                        .size(if (isSelected) 16.dp else 5.dp, 5.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (isSelected) accentColor
                                            else Color.White.copy(alpha = 0.35f)
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

