package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.model.Car
import com.example.ui.components.LiquidNavBar
import com.example.ui.components.NavTab
import com.example.ui.components.liquidGlass
import com.example.ui.screens.BrandScreen
import com.example.ui.screens.CarDetailScreen
import com.example.ui.screens.CompareScreen
import com.example.ui.screens.ExploreScreen
import com.example.ui.screens.FavoritesScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LoginScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.SearchScreen
import com.example.ui.screens.SignUpScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.theme.AutoVerseTheme
import com.example.ui.theme.DeepBlack
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.ElectricBlueLight
import com.example.ui.theme.GlassTextPrimary
import com.example.ui.viewmodel.AutoVerseViewModel
import com.example.ui.viewmodel.Screen
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AutoVerseTheme {
                AutoVerseApp()
            }
        }
    }
}

@Composable
fun AutoVerseApp() {
    val context = androidx.compose.ui.platform.LocalContext.current
    val db = remember { com.example.data.local.AutoVerseDatabase.getInstance(context) }
    val repository = remember { com.example.data.CarRepository(db.carDetailDao(), db.userDao()) }
    val viewModel: AutoVerseViewModel = viewModel(
        factory = object : androidx.lifecycle.ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                return AutoVerseViewModel(repository) as T
            }
        }
    )
    val currentScreen by viewModel.currentScreen.collectAsState()
    val currentTab by viewModel.currentTab.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()
    val snackBarMsg by viewModel.snackBarMessage.collectAsState()

    // Auto-dismiss snackbar after 2.8 seconds
    LaunchedEffect(snackBarMsg) {
        if (snackBarMsg != null) {
            delay(2800)
            viewModel.dismissNotification()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepBlack)
    ) {
        // Main Screen Switcher
        AnimatedContent(
            targetState = currentScreen,
            transitionSpec = {
                fadeIn() togetherWith fadeOut()
            },
            label = "screenTransition"
        ) { screen ->
            when (screen) {
                is Screen.Splash -> {
                    SplashScreen(
                        onSplashComplete = {
                            viewModel.navigateTo(Screen.Login)
                        }
                    )
                }

                is Screen.Login -> {
                    LoginScreen(
                        viewModel = viewModel,
                        onLoginSuccess = {
                            viewModel.navigateTo(Screen.Main(NavTab.HOME))
                        },
                        onNavigateToSignUp = {
                            viewModel.navigateTo(Screen.SignUp)
                        }
                    )
                }

                is Screen.SignUp -> {
                    SignUpScreen(
                        viewModel = viewModel,
                        onSignUpSuccess = {
                            viewModel.navigateTo(Screen.Main(NavTab.HOME))
                        },
                        onNavigateToLogin = {
                            viewModel.navigateTo(Screen.Login)
                        }
                    )
                }


                is Screen.Main -> {
                    Box(modifier = Modifier.fillMaxSize()) {
                        // Current tab content
                        when (currentTab) {
                            NavTab.HOME -> HomeScreen(
                                viewModel = viewModel,
                                onOpenCar = { viewModel.openCarDetail(it) },
                                onOpenBrand = { viewModel.openBrandDetail(it) },
                                onOpenSearch = { viewModel.openSearch() },
                                onOpenProfile = { viewModel.selectTab(NavTab.PROFILE) }
                            )

                            NavTab.EXPLORE -> ExploreScreen(
                                viewModel = viewModel,
                                onOpenCar = { viewModel.openCarDetail(it) }
                            )

                            NavTab.COMPARE -> CompareScreen(
                                viewModel = viewModel,
                                onOpenCar = { viewModel.openCarDetail(it) }
                            )

                            NavTab.FAVORITES -> FavoritesScreen(
                                viewModel = viewModel,
                                onOpenCar = { viewModel.openCarDetail(it) }
                            )

                            NavTab.PROFILE -> ProfileScreen(
                                viewModel = viewModel,
                                onOpenCar = { viewModel.openCarDetail(it) },
                                onLogout = { viewModel.navigateTo(Screen.Login) }
                            )
                        }

                        // Floating Liquid Glass Navigation Bar (iPhone Style)
                        LiquidNavBar(
                            currentTab = currentTab,
                            onTabSelected = { viewModel.selectTab(it) },
                            compareCount = userProfile.compareCarIds.size,
                            favoritesCount = userProfile.favoriteCarIds.size,
                            modifier = Modifier.align(Alignment.BottomCenter)
                        )
                    }
                }

                is Screen.CarDetail -> {
                    CarDetailScreen(
                        carId = screen.carId,
                        viewModel = viewModel,
                        onBack = { viewModel.navigateBack() },
                        onOpenCar = { viewModel.openCarDetail(it) }
                    )
                }

                is Screen.BrandDetail -> {
                    BrandScreen(
                        brandId = screen.brandId,
                        viewModel = viewModel,
                        onBack = { viewModel.navigateBack() },
                        onOpenCar = { viewModel.openCarDetail(it) }
                    )
                }

                is Screen.Search -> {
                    SearchScreen(
                        viewModel = viewModel,
                        onBack = { viewModel.navigateBack() },
                        onOpenCar = { viewModel.openCarDetail(it) }
                    )
                }
            }
        }

        // Global Glass Notification Pill Toast
        AnimatedVisibility(
            visible = snackBarMsg != null,
            enter = slideInVertically { -it } + fadeIn(),
            exit = slideOutVertically { -it } + fadeOut(),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .statusBarsPadding()
                .padding(top = 16.dp, start = 20.dp, end = 20.dp)
        ) {
            if (snackBarMsg != null) {
                Box(
                    modifier = Modifier
                        .liquidGlass(
                            shape = CircleShape,
                            backgroundColor = Color(0xF20F121E),
                            borderColor = ElectricBlue.copy(alpha = 0.5f)
                        )
                        .padding(horizontal = 18.dp, vertical = 10.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = ElectricBlueLight,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = snackBarMsg!!,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = GlassTextPrimary,
                                fontSize = 13.sp
                            )
                        )
                    }
                }
            }
        }
    }
}
