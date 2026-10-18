package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.TrendingUp
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.GlassCard
import com.example.ui.components.GlassIconButton
import com.example.ui.components.GlassTextField
import com.example.ui.components.liquidGlass
import com.example.ui.theme.DeepBlack
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.ElectricBlueLight
import com.example.ui.theme.GlassTextMuted
import com.example.ui.theme.GlassTextPrimary
import com.example.ui.theme.GlassTextSecondary
import com.example.ui.theme.NeonRed
import com.example.ui.viewmodel.AutoVerseViewModel

@Composable
fun SearchScreen(
    viewModel: AutoVerseViewModel,
    onBack: () -> Unit,
    onOpenCar: (String) -> Unit
) {
    BackHandler { onBack() }

    val recentSearches by viewModel.recentSearches.collectAsState()
    val trendingSearches = viewModel.trendingSearches
    val userProfile by viewModel.userProfile.collectAsState()
    val allCars by viewModel.cars.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var isListeningVoice by remember { mutableStateOf(false) }

    val searchResults = remember(searchQuery, allCars) {
        if (searchQuery.isBlank()) emptyList()
        else {
            allCars.filter { car ->
                car.brand.contains(searchQuery, ignoreCase = true) ||
                    car.model.contains(searchQuery, ignoreCase = true) ||
                    car.variant.contains(searchQuery, ignoreCase = true) ||
                    car.engine.contains(searchQuery, ignoreCase = true) ||
                    car.country.contains(searchQuery, ignoreCase = true) ||
                    car.bodyType.contains(searchQuery, ignoreCase = true)
            }
        }
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
            // Search Input Header with Back Button and Mic
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                GlassIconButton(
                    icon = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    onClick = onBack,
                    size = 40.dp
                )

                Spacer(modifier = Modifier.width(10.dp))

                GlassTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = "Search makes, models, V8, AWD...",
                    leadingIcon = Icons.Default.Search,
                    trailingIcon = if (searchQuery.isNotEmpty()) Icons.Default.Close else Icons.Default.Mic,
                    onTrailingIconClick = {
                        if (searchQuery.isNotEmpty()) {
                            searchQuery = ""
                        } else {
                            // Voice search simulation
                            isListeningVoice = !isListeningVoice
                            if (isListeningVoice) {
                                searchQuery = "Porsche 911 GT3 RS"
                            }
                        }
                    },
                    modifier = Modifier.weight(1f)
                )
            }

            if (searchQuery.isBlank()) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp, vertical = 10.dp)
                ) {
                    // Recent Searches
                    if (recentSearches.isNotEmpty()) {
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "RECENT SEARCHES",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = GlassTextMuted,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.sp
                                    )
                                )
                                Text(
                                    text = "Clear History",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = NeonRed,
                                        fontWeight = FontWeight.SemiBold
                                    ),
                                    modifier = Modifier.clickable { viewModel.clearRecentSearches() }
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                recentSearches.forEach { term ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(14.dp))
                                            .background(Color.White.copy(alpha = 0.06f))
                                            .clickable { searchQuery = term }
                                            .padding(horizontal = 14.dp, vertical = 10.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.History,
                                            contentDescription = null,
                                            tint = GlassTextMuted,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Text(
                                            text = term,
                                            style = MaterialTheme.typography.bodyMedium.copy(color = Color.White)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(24.dp))
                        }
                    }

                    // Trending Searches
                    item {
                        Text(
                            text = "TRENDING ENCYCLOPEDIA SEARCHES",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = GlassTextMuted,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            trendingSearches.forEach { trending ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(Color.White.copy(alpha = 0.06f))
                                        .clickable { searchQuery = trending }
                                        .padding(horizontal = 14.dp, vertical = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.TrendingUp,
                                            contentDescription = null,
                                            tint = ElectricBlueLight,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Text(
                                            text = trending,
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                color = Color.White,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        )
                                    }
                                    Text(
                                        text = "Explore",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = ElectricBlueLight,
                                            fontSize = 11.sp
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            } else {
                // Live Search Results
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("search_results_list"),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Text(
                            text = "SEARCH RESULTS (${searchResults.size})",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = GlassTextMuted,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }

                    if (searchResults.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 40.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "No models match '$searchQuery'",
                                    style = MaterialTheme.typography.bodyMedium.copy(color = GlassTextSecondary)
                                )
                            }
                        }
                    } else {
                        items(searchResults) { car ->
                            ListCarCard(
                                car = car,
                                currency = userProfile.preferredCurrency,
                                isFavorite = userProfile.favoriteCarIds.contains(car.id),
                                isCompared = userProfile.compareCarIds.contains(car.id),
                                onFavoriteToggle = { viewModel.toggleFavorite(car.id) },
                                onCompareToggle = { viewModel.toggleCompare(car.id) },
                                onClick = {
                                    viewModel.addRecentSearch(searchQuery)
                                    onOpenCar(car.id)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
