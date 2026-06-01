package com.example.airline.ui.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.airline.ui.theme.PrimaryBlue
import kotlinx.coroutines.flow.collectLatest
import com.example.airline.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onNavigateBack: () -> Unit,
    onLoginSuccess: () -> Unit,
    onNavigateToSignUp: () -> Unit,
    onNavigateToForgotPassword: () -> Unit,
    onNavigateToBaggage: () -> Unit,
    viewModel: LoginViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    LaunchedEffect(uiState.isLoggedIn) {
        if (uiState.isLoggedIn) {
            onLoginSuccess()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp)
    ) {
        // ── Header: Title centered with Back button ──
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 48.dp, bottom = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.Black
                )
            }
            Spacer(modifier = Modifier.width(8.dp))

            // Title centered within the row
            Text(
                text = "Login",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )

            // Spacer to balance the IconButton so title is perfectly centered
            Spacer(modifier = Modifier.width(48.dp))
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Email
        Text(
            text = "Email address",
            fontSize = 14.sp,
            color = Color(0xFF666666),
            modifier = Modifier.padding(bottom = 8.dp)
        )
        OutlinedTextField(
            value = uiState.email,
            onValueChange = { viewModel.onEmailChange(it) },
            placeholder = { Text("Input email address", color = Color.Gray) },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(12.dp),
            isError = uiState.emailError != null,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PrimaryBlue,
                unfocusedBorderColor = Color(0xFFE0E0E0),
                errorBorderColor = Color.Red,
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                cursorColor = PrimaryBlue,
                focusedPlaceholderColor = Color(0xFF9E9E9E),
                unfocusedPlaceholderColor = Color(0xFF9E9E9E)
            ),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next)
        )
        uiState.emailError?.let { Text(it, color = Color.Red, fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp)) }

        Spacer(modifier = Modifier.height(20.dp))

        // Password
        Text(
            text = "Password",
            fontSize = 14.sp,
            color = Color(0xFF666666),
            modifier = Modifier.padding(bottom = 8.dp)
        )
        OutlinedTextField(
            value = uiState.password,
            onValueChange = { viewModel.onPasswordChange(it) },
            placeholder = { Text("Input your password", color = Color.Gray) },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(12.dp),
            isError = uiState.passwordError != null,
            visualTransformation = if (uiState.passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val icon = if (uiState.passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                IconButton(onClick = { viewModel.togglePasswordVisibility() }) {
                    Icon(imageVector = icon, contentDescription = if (uiState.passwordVisible) "Hide" else "Show", tint = Color.Gray)
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PrimaryBlue,
                unfocusedBorderColor = Color(0xFFE0E0E0),
                errorBorderColor = Color.Red,
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                cursorColor = PrimaryBlue,
                focusedPlaceholderColor = Color(0xFF9E9E9E),
                unfocusedPlaceholderColor = Color(0xFF9E9E9E)
            ),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = {
                focusManager.clearFocus()
                if (uiState.isFormValid) viewModel.login(onSuccess = {}, onError = {})
            })
        )
        uiState.passwordError?.let { Text(it, color = Color.Red, fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp)) }

        Spacer(modifier = Modifier.height(32.dp))

        // Login Button
        Button(
            onClick = {
                focusManager.clearFocus()
                viewModel.login(onSuccess = {}, onError = {})
            },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp),
            enabled = !uiState.isLoading && uiState.isFormValid
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White, strokeWidth = 2.dp)
            } else {
                Text(text = "Login", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
            }
        }

        // Forgot Password (Centered below Login)
        Text(
            text = "Forgot Password?",
            fontSize = 14.sp,
            color = PrimaryBlue,
            fontWeight = FontWeight.Medium,
            modifier = Modifier
                .fillMaxWidth()
                .clickable(
                    onClick = { onNavigateToForgotPassword() },
                    indication = null,  // ← Supprime le ripple
                    interactionSource = remember { MutableInteractionSource() }
                )
                .padding(top = 16.dp, bottom = 32.dp),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onNavigateToBaggage,
            modifier = Modifier.fillMaxWidth().height(50.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)), // Vert
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
        ) {
            Text(
                text = "→ TEST: Aller à Baggage",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White
            )
        }

        // Google Button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp) // Même hauteur que le bouton "Mode offline"
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFF2F2F2)) // Fond gris clair comme le Figma
                .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(12.dp))
                .clickable(
                    enabled = !uiState.isLoading,
                    onClick = {
                        if (!uiState.isLoading) {
                            viewModel.loginWithGoogle(context)
                        }
                    }
                )
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_google_logo_round),
                    contentDescription = "Google Logo",
                    modifier = Modifier
                        .size(28.dp) // Ajuste la taille selon le rendu Figma
                )

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = "Continue with Google",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF1F1F1F)
                )
            }
        }

        uiState.errorMessage?.let { error ->
            Text(
                text = error,
                color = Color.Red,
                fontSize = 12.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Mode Offline Button (Exact match)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .border(1.5.dp, PrimaryBlue.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                .background(Color(0xFFFAFAFA), RoundedCornerShape(12.dp))
                .clickable { viewModel.toggleOfflineMode() }
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon: Wifi Off or Airplane (Using WifiOff as it fits the "offline" context)
            Icon(
                imageVector = Icons.Default.WifiOff,
                contentDescription = "Offline",
                tint = PrimaryBlue,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Mode offline",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Text(
                    text = "Accéder à ma carte d'embarquement sans réseau",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    maxLines = 1
                )
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Arrow",
                tint = Color.Gray,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // Sign up link
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(text = "You don't have an account? ", fontSize = 14.sp, color = Color(0xFF666666))
            Text(
                text = "Sign up",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = PrimaryBlue,
                modifier = Modifier.clickable { onNavigateToSignUp() }
            )
        }
    }
}