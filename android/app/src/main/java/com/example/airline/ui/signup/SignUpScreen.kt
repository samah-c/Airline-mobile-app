package com.example.airline.ui.signup

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
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.airline.R
import com.example.airline.ui.theme.PrimaryBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(
    onNavigateBack: () -> Unit,
    onSignUpSuccess: () -> Unit,
    onNavigateToSignIn: () -> Unit,
    viewModel: SignUpViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current

    LaunchedEffect(uiState.isSignUpSuccessful) {
        if (uiState.isSignUpSuccessful) onSignUpSuccess()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp)
    ) {
        // Top bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 48.dp, bottom = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onNavigateBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.Black)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text("Sign up", fontSize = 20.sp, fontWeight = FontWeight.SemiBold, color = Color.Black)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Error message
        uiState.errorMessage?.let { error ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.Red.copy(alpha = 0.1f)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = error, color = Color.Red, fontSize = 14.sp, modifier = Modifier.weight(1f))
                    IconButton(onClick = { viewModel.clearError() }) {
                        Text("×", color = Color.Red, fontSize = 20.sp)
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // ── First Name ──────────────────────────────────────────
        Text("Your name", fontSize = 14.sp, color = Color.DarkGray, modifier = Modifier.padding(bottom = 8.dp))
        OutlinedTextField(
            value = uiState.firstName,
            onValueChange = { viewModel.onFirstNameChange(it) },
            placeholder = { Text("Input your first name", color = Color.Gray) },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(12.dp),
            isError = uiState.firstNameError != null,
            colors = outlinedColors(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Next)
        )
        uiState.firstNameError?.let { Text(it, color = Color.Red, fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp)) }

        Spacer(modifier = Modifier.height(20.dp))

        // ── Email ───────────────────────────────────────────────
        Text("Email address", fontSize = 14.sp, color = Color.DarkGray, modifier = Modifier.padding(bottom = 8.dp))
        OutlinedTextField(
            value = uiState.email,
            onValueChange = { viewModel.onEmailChange(it) },
            placeholder = { Text("Input email address", color = Color.Gray) },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(12.dp),
            isError = uiState.emailError != null,
            colors = outlinedColors(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next)
        )
        uiState.emailError?.let { Text(it, color = Color.Red, fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp)) }

        Spacer(modifier = Modifier.height(20.dp))

        // ── Phone Number ────────────────────────────────────────
        Text("Phone number", fontSize = 14.sp, color = Color.DarkGray, modifier = Modifier.padding(bottom = 8.dp))
        OutlinedTextField(
            value = uiState.phoneNumber,
            onValueChange = { viewModel.onPhoneNumberChange(it) },
            placeholder = { Text("Input your phone number", color = Color.Gray) },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(12.dp),
            isError = uiState.phoneNumberError != null,
            colors = outlinedColors(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone, imeAction = ImeAction.Next)
        )
        uiState.phoneNumberError?.let { Text(it, color = Color.Red, fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp)) }

        Spacer(modifier = Modifier.height(20.dp))

        // ── Password ────────────────────────────────────────────
        Text("Password", fontSize = 14.sp, color = Color.DarkGray, modifier = Modifier.padding(bottom = 8.dp))
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
                    Icon(icon, contentDescription = null, tint = Color.Gray)
                }
            },
            colors = outlinedColors(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Next)
        )
        uiState.passwordError?.let { Text(it, color = Color.Red, fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp)) }

        Spacer(modifier = Modifier.height(20.dp))

        // ── Confirm Password ────────────────────────────────────
        Text("Confirm Password", fontSize = 14.sp, color = Color.DarkGray, modifier = Modifier.padding(bottom = 8.dp))
        OutlinedTextField(
            value = uiState.confirmPassword,
            onValueChange = { viewModel.onConfirmPasswordChange(it) },
            placeholder = { Text("Input confirm password", color = Color.Gray) },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(12.dp),
            isError = uiState.confirmPasswordError != null,
            visualTransformation = if (uiState.confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val icon = if (uiState.confirmPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                IconButton(onClick = { viewModel.toggleConfirmPasswordVisibility() }) {
                    Icon(icon, contentDescription = null, tint = Color.Gray)
                }
            },
            colors = outlinedColors(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = {
                focusManager.clearFocus()
                if (uiState.isFormValid) viewModel.signUp(onSuccess = {}, onError = {})
            })
        )
        uiState.confirmPasswordError?.let { Text(it, color = Color.Red, fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp)) }

        Spacer(modifier = Modifier.height(32.dp))

        // ── Sign up button ──────────────────────────────────────
        Button(
            onClick = {
                focusManager.clearFocus()
                viewModel.signUp(onSuccess = {}, onError = {})
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
                Text("Sign up", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ── Divider ─────────────────────────────────────────────
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            HorizontalDivider(modifier = Modifier.weight(1f), color = Color.LightGray)
            Text("Or sign up with", modifier = Modifier.padding(horizontal = 16.dp), fontSize = 14.sp, color = Color.Gray)
            HorizontalDivider(modifier = Modifier.weight(1f), color = Color.LightGray)
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ── Google button ───────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFF2F2F2))
                .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(12.dp))
                .clickable(enabled = !uiState.isLoading, onClick = { /* TODO: Google Sign-In */ })
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                Image(
                    painter = painterResource(id = R.drawable.ic_google_logo_round),
                    contentDescription = "Google Logo",
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text("Continue with Google", fontSize = 16.sp, fontWeight = FontWeight.Medium, color = Color(0xFF1F1F1F))
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // ── Already have account ────────────────────────────────
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            Text("Already have an account? ", fontSize = 14.sp, color = Color.DarkGray)
            Text(
                text = "Sign in",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = PrimaryBlue,
                modifier = Modifier.clickable(
                    onClick = { onNavigateToSignIn() },
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                )
            )
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun outlinedColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = PrimaryBlue,
    unfocusedBorderColor = Color(0xFFE0E0E0),
    errorBorderColor = Color.Red,
    focusedTextColor = Color.Black,
    unfocusedTextColor = Color.Black,
    cursorColor = PrimaryBlue,
    focusedPlaceholderColor = Color(0xFF9E9E9E),
    unfocusedPlaceholderColor = Color(0xFF9E9E9E)
)