package com.example.raon.features.auth.ui

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.raon.R
import com.example.raon.features.auth.ui.state.SignUpResult
import com.example.raon.features.auth.ui.viewmodel.SignUpViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(
    onSignupScussess: () -> Unit,
    onNavigateToLocationSetting: () -> Unit = {},
    signUpViewModel: SignUpViewModel = hiltViewModel()
) {
    val uiState by signUpViewModel.uiState.collectAsState()
    val context = LocalContext.current

    // 모든 입력 필드가 채워졌는지 확인하는 변수
    val isFormValid = uiState.nickname.isNotBlank() &&
            uiState.email.isNotBlank() &&
            uiState.password.isNotBlank() &&
            uiState.passwordCheck.isNotBlank()

    LaunchedEffect(key1 = uiState.signUpResult) {
        when (val result = uiState.signUpResult) {
            is SignUpResult.Success -> {
                Toast.makeText(context, "회원가입 성공!", Toast.LENGTH_SHORT).show()
                onSignupScussess()
                signUpViewModel.resultConsumed()
            }

            is SignUpResult.Failure -> {
                Toast.makeText(context, result.message, Toast.LENGTH_LONG).show()
                signUpViewModel.resultConsumed()
            }

            else -> {}
        }
    }

    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(50)
        visible = true
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Scaffold(
            containerColor = Color.White
        ) { paddingValues ->
            AnimatedVisibility(
                visible = visible,
                modifier = Modifier.fillMaxSize(),
                enter = fadeIn(tween(300)) + slideInHorizontally(
                    initialOffsetX = { it },
                    animationSpec = tween(300)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(horizontal = 24.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "회원가입",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF333333)
                    )
                    Spacer(modifier = Modifier.height(32.dp))

                    SignUpTextField(
                        value = uiState.nickname,
                        onValueChange = signUpViewModel::onNicknameChange,
                        label = "닉네임",
                        leadingIcon = Icons.Default.Person
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    SignUpTextField(
                        value = uiState.email,
                        onValueChange = signUpViewModel::onEmailChange,
                        label = "이메일 주소",
                        leadingIcon = Icons.Default.Email,
                        keyboardType = KeyboardType.Email
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    PasswordTextField(
                        value = uiState.password,
                        onValueChange = signUpViewModel::onPasswordChange,
                        label = "비밀번호",
                        isVisible = uiState.isPasswordVisible,
                        onVisibilityChange = signUpViewModel::togglePasswordVisibility
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    PasswordTextField(
                        value = uiState.passwordCheck,
                        onValueChange = signUpViewModel::onPasswordCheckChange,
                        label = "비밀번호 확인",
                        isVisible = uiState.isPasswordCheckVisible,
                        onVisibilityChange = signUpViewModel::togglePasswordCheckVisibility
                    )
                    Spacer(modifier = Modifier.height(32.dp))

//                    Button(
//                        onClick = { onNavigateToLocationSetting() },
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .height(56.dp),
//                        shape = RoundedCornerShape(12.dp),
//                        colors = ButtonDefaults.buttonColors(
//                            containerColor = Color(0xFFFDCC31),
//                            contentColor = Color(0xFF3C3C3C)
//                        ),
//                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
//                    ) {
//                        Text(text = "위치 데이터", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
//                    }


                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { signUpViewModel.signUp() },
                        // 버튼 활성화 조건
                        enabled = isFormValid && uiState.signUpResult != SignUpResult.Loading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFDCC31),
                            contentColor = Color(0xFF3C3C3C)
                        ),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                    ) {
                        Text(text = "가입하기", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    TextButton(onClick = { /* TODO: 로그인 화면으로 이동 */ }) {
                        Text(
                            "이미 계정이 있으신가요? 로그인",
                            color = Color(0xFF666666),
                            fontWeight = FontWeight.Normal
                        )
                    }
                }
            }
        }

        if (uiState.signUpResult == SignUpResult.Loading) {
            CircularProgressIndicator()
        }
    }
}

@Composable
private fun SignUpTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    leadingIcon: ImageVector,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, color = Color(0xFF666666)) },
        leadingIcon = {
            Icon(
                imageVector = leadingIcon,
                contentDescription = null,
                tint = Color(0xFFFDCC31)
            )
        },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color(0xFFFDCC31),
            unfocusedBorderColor = Color(0xFFCCCCCC),
            focusedLabelColor = Color(0xFFFDCC31),
            unfocusedLabelColor = Color(0xFF9E9E9E)
        )
    )
}

@Composable
private fun PasswordTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    isVisible: Boolean,
    onVisibilityChange: () -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, color = Color(0xFF666666)) },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = null,
                tint = Color(0xFFFDCC31)
            )
        },
        trailingIcon = {
            IconButton(onClick = onVisibilityChange) {
                Icon(
                    painter = if (isVisible) painterResource(id = R.drawable.eyes) else painterResource(
                        id = R.drawable.eyes_hidden
                    ),
                    contentDescription = if (isVisible) "비밀번호 숨기기" else "비밀번호 보기",
                    modifier = Modifier.size(24.dp),
                    tint = Color.Black
                )
            }
        },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        singleLine = true,
        visualTransformation = if (isVisible) VisualTransformation.None else PasswordVisualTransformation(),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color(0xFFFDCC31),
            unfocusedBorderColor = Color(0xFFCCCCCC),
            focusedLabelColor = Color(0xFFFDCC31),
            unfocusedLabelColor = Color(0xFF9E9E9E)
        )
    )
}