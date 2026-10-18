package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.components.GlassButton
import com.example.ui.components.GlassCard
import com.example.ui.components.GlassSecondaryButton
import com.example.ui.components.GlassTextField
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

@Composable
fun LoginScreen(
    viewModel: AutoVerseViewModel,
    onLoginSuccess: () -> Unit,
    onNavigateToSignUp: () -> Unit
) {
    var email by remember { mutableStateOf("alex.vance@autoverse.io") }
    var password by remember { mutableStateOf("Autoverse2026!") }
    var passwordVisible by remember { mutableStateOf(false) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var isLoggingIn by remember { mutableStateOf(false) }
    var showForgotDialog by remember { mutableStateOf(false) }

    // Subtle Ken Burns slow zoom effect on background car
    val infiniteTransition = rememberInfiniteTransition(label = "kenBurns")
    val zoomScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(12000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "zoomScale"
    )


    Box(modifier = Modifier.fillMaxSize()) {
        // Dramatic luxury sports car background with dark gradients
        Image(
            painter = painterResource(id = R.drawable.luxury_hypercar_bg),
            contentDescription = "Luxury Hypercar Background",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    scaleX = zoomScale
                    scaleY = zoomScale
                }
        )

        // Dark gradient overlay for extreme readability and glass pop
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xCC05060A),
                            Color(0x990A0E18),
                            Color(0xF505060A)
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // Logo & Title
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .border(1.dp, ElectricBlue.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                        .background(Color(0xFF0C101A)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_autoverse_logo),
                        contentDescription = null,
                        modifier = Modifier.size(38.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "AUTOVERSE",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Black,
                            letterSpacing = 2.sp,
                            color = GlassTextPrimary
                        )
                    )
                    Text(
                        text = "Car Encyclopedia",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = ElectricBlueLight,
                            fontSize = 11.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            // Main Liquid Glass Login Card
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("login_card"),
                shape = RoundedCornerShape(28.dp),
                backgroundColor = Color.White.copy(alpha = 0.12f)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                ) {
                    Text(
                        text = "Welcome Back",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = GlassTextPrimary
                        )
                    )
                    Text(
                        text = "Access full specifications and comparison tools",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = GlassTextSecondary
                        )
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Email input
                    GlassTextField(
                        value = email,
                        onValueChange = {
                            email = it
                            emailError = null
                        },
                        placeholder = "Email Address",
                        leadingIcon = Icons.Default.Email,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        errorText = emailError,
                        modifier = Modifier.testTag("login_email_input")
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Password input
                    GlassTextField(
                        value = password,
                        onValueChange = {
                            password = it
                            passwordError = null
                        },
                        placeholder = "Password",
                        leadingIcon = Icons.Default.Lock,
                        isPassword = true,
                        passwordVisible = passwordVisible,
                        onPasswordToggle = { passwordVisible = !passwordVisible },
                        errorText = passwordError,
                        modifier = Modifier.testTag("login_password_input")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Forgot Password
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.CenterEnd
                    ) {
                        Text(
                            text = "Forgot Password?",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = ElectricBlueLight,
                                fontWeight = FontWeight.SemiBold
                            ),
                            modifier = Modifier
                                .clickable { showForgotDialog = true }
                                .padding(4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Login Button
                    GlassButton(
                        text = if (isLoggingIn) "Authenticating..." else "Login to AutoVerse",
                        isLoading = isLoggingIn,
                        onClick = {
                            var valid = true
                            if (email.isBlank() || !email.contains("@")) {
                                emailError = "Please enter a valid email address"
                                valid = false
                            }
                            if (password.length < 6) {
                                passwordError = "Password must be at least 6 characters"
                                valid = false
                            }
                            if (valid) {
                                isLoggingIn = true
                                viewModel.login(email, password) { success, errorMsg ->
                                    isLoggingIn = false
                                    if (success) {
                                        onLoginSuccess()
                                    } else {
                                        emailError = errorMsg ?: "Invalid email or password"
                                    }
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("login_submit_btn")
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Demo Credentials One-Tap Pill
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.06f))
                            .border(1.dp, Color.White.copy(alpha = 0.14f), CircleShape)
                            .clickable {
                                email = "alex.vance@autoverse.io"
                                password = "Autoverse2026!"
                                emailError = null
                                passwordError = null
                            }
                            .padding(vertical = 8.dp, horizontal = 14.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "💡 Tap to fill demo account (alex.vance@autoverse.io)",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = ElectricBlueLight,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))


                    // Divider
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        HorizontalDivider(
                            modifier = Modifier.weight(1f),
                            color = GlassBorderColor
                        )
                        Text(
                            text = "or continue with",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = GlassTextMuted,
                                fontSize = 11.sp
                            ),
                            modifier = Modifier.padding(horizontal = 12.dp)
                        )
                        HorizontalDivider(
                            modifier = Modifier.weight(1f),
                            color = GlassBorderColor
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Social login buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        GlassSecondaryButton(
                            text = "Google",
                            onClick = onLoginSuccess,
                            modifier = Modifier.weight(1f)
                        )
                        GlassSecondaryButton(
                            text = "Apple",
                            onClick = onLoginSuccess,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Guest mode
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Continue as Guest Explorer",
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = GlassTextSecondary,
                                textDecoration = androidx.compose.ui.text.style.TextDecoration.Underline
                            ),
                            modifier = Modifier
                                .clickable { onLoginSuccess() }
                                .padding(6.dp)
                                .testTag("continue_guest_btn")
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Switch to Sign Up
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Don't have an account?",
                    style = MaterialTheme.typography.bodyMedium.copy(color = GlassTextSecondary)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Sign Up",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = ElectricBlueLight,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier
                        .clickable { onNavigateToSignUp() }
                        .padding(4.dp)
                        .testTag("to_signup_btn")
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        // Forgot password dialog
        if (showForgotDialog) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.75f))
                    .clickable { showForgotDialog = false },
                contentAlignment = Alignment.Center
            ) {
                GlassCard(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .clickable(enabled = false) {},
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Text(
                            text = "Reset Password",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "A password recovery email will be sent to $email",
                            style = MaterialTheme.typography.bodyMedium.copy(color = GlassTextSecondary)
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        GlassButton(
                            text = "Send Reset Link",
                            onClick = { showForgotDialog = false },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SignUpScreen(
    viewModel: AutoVerseViewModel,
    onSignUpSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    var fullName by remember { mutableStateOf("Alex Vance") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("+1 415 555 2671") }
    var dreamCar by remember { mutableStateOf("Porsche 911 GT3 RS") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var agreedTerms by remember { mutableStateOf(true) }
    var isSigningUp by remember { mutableStateOf(false) }

    var fullNameError by remember { mutableStateOf<String?>(null) }

    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var confirmError by remember { mutableStateOf<String?>(null) }
    var termsError by remember { mutableStateOf<String?>(null) }

    // Password strength calculation
    val passwordStrength = remember(password) {
        when {
            password.isEmpty() -> 0
            password.length < 6 -> 1
            password.length < 10 && password.any { it.isDigit() } -> 2
            password.any { it.isDigit() } && password.any { it.isUpperCase() } && password.any { !it.isLetterOrDigit() } -> 4
            else -> 3
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.luxury_hypercar_bg),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xCC05060A),
                            Color(0xAA0A0E18),
                            Color(0xF505060A)
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // App branding
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
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
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "AUTOVERSE",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Black,
                        letterSpacing = 2.sp
                    )
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("signup_card"),
                shape = RoundedCornerShape(28.dp),
                backgroundColor = Color.White.copy(alpha = 0.12f)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(22.dp)
                ) {
                    Text(
                        text = "Create Account",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = "Join global enthusiasts & save dream cars",
                        style = MaterialTheme.typography.bodyMedium.copy(color = GlassTextSecondary)
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    // Full Name
                    GlassTextField(
                        value = fullName,
                        onValueChange = {
                            fullName = it
                            fullNameError = null
                        },
                        placeholder = "Full Name",
                        leadingIcon = Icons.Default.Person,
                        errorText = fullNameError
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Email
                    GlassTextField(
                        value = email,
                        onValueChange = {
                            email = it
                            emailError = null
                        },
                        placeholder = "Email Address",
                        leadingIcon = Icons.Default.Email,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        errorText = emailError
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Phone Number
                    GlassTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        placeholder = "Phone Number",
                        leadingIcon = Icons.Default.Phone,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Dream Car / Primary Passion
                    GlassTextField(
                        value = dreamCar,
                        onValueChange = { dreamCar = it },
                        placeholder = "Dream Car (e.g. Ferrari SF90 / 911 GT3 RS)",
                        leadingIcon = Icons.Default.Star
                    )

                    Spacer(modifier = Modifier.height(12.dp))


                    // Password
                    GlassTextField(
                        value = password,
                        onValueChange = {
                            password = it
                            passwordError = null
                        },
                        placeholder = "Password (min 6 chars)",
                        leadingIcon = Icons.Default.Lock,
                        isPassword = true,
                        passwordVisible = passwordVisible,
                        onPasswordToggle = { passwordVisible = !passwordVisible },
                        errorText = passwordError
                    )

                    // Password Strength Indicator
                    if (password.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            for (i in 1..4) {
                                val barColor = when {
                                    passwordStrength >= i -> when (passwordStrength) {
                                        1 -> NeonRed
                                        2 -> Color(0xFFFF9500)
                                        3 -> ElectricBlueLight
                                        else -> WinnerGreen
                                    }
                                    else -> Color.White.copy(alpha = 0.15f)
                                }
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(4.dp)
                                        .clip(CircleShape)
                                        .background(barColor)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Confirm Password
                    GlassTextField(
                        value = confirmPassword,
                        onValueChange = {
                            confirmPassword = it
                            confirmError = null
                        },
                        placeholder = "Confirm Password",
                        leadingIcon = Icons.Default.Lock,
                        isPassword = true,
                        passwordVisible = passwordVisible,
                        onPasswordToggle = { passwordVisible = !passwordVisible },
                        errorText = confirmError
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Terms Checkbox
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Checkbox(
                            checked = agreedTerms,
                            onCheckedChange = {
                                agreedTerms = it
                                termsError = null
                            },
                            colors = CheckboxDefaults.colors(
                                checkedColor = ElectricBlue,
                                uncheckedColor = GlassBorderColor,
                                checkmarkColor = Color.White
                            )
                        )
                        Text(
                            text = "I accept the Terms of Service & Privacy Policy",
                            style = MaterialTheme.typography.bodySmall.copy(color = GlassTextSecondary),
                            modifier = Modifier.clickable { agreedTerms = !agreedTerms }
                        )
                    }

                    if (termsError != null) {
                        Text(
                            text = termsError!!,
                            style = MaterialTheme.typography.bodySmall.copy(color = NeonRed),
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    GlassButton(
                        text = if (isSigningUp) "Creating Account..." else "Create AutoVerse Account",
                        isLoading = isSigningUp,
                        onClick = {
                            var valid = true
                            if (fullName.isBlank()) {
                                fullNameError = "Full name is required"
                                valid = false
                            }
                            if (email.isBlank() || !email.contains("@")) {
                                emailError = "Valid email is required"
                                valid = false
                            }
                            if (password.length < 6) {
                                passwordError = "Password must be at least 6 characters"
                                valid = false
                            }
                            if (password != confirmPassword) {
                                confirmError = "Passwords do not match"
                                valid = false
                            }
                            if (!agreedTerms) {
                                termsError = "Please accept the terms to proceed"
                                valid = false
                            }
                            if (valid) {
                                isSigningUp = true
                                viewModel.register(
                                    name = fullName,
                                    email = email,
                                    password = password,
                                    phone = phone,
                                    dreamCar = dreamCar
                                ) { success, errorMsg ->
                                    isSigningUp = false
                                    if (success) {
                                        onSignUpSuccess()
                                    } else {
                                        emailError = errorMsg ?: "Failed to create account"
                                    }
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Switch to Login
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Already have an account?",
                    style = MaterialTheme.typography.bodyMedium.copy(color = GlassTextSecondary)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Login",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = ElectricBlueLight,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier
                        .clickable { onNavigateToLogin() }
                        .padding(4.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
