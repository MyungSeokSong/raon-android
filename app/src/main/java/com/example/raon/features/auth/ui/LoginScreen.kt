package com.example.raon.features.auth.ui

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.raon.R
import com.example.raon.features.auth.ui.viewmodel.LoginResult
import com.example.raon.features.auth.ui.viewmodel.LoginViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    loginViewModel: LoginViewModel = hiltViewModel()
) {
    // UI 상태 관리를 위한 변수들
    val email = loginViewModel.email
    val password = loginViewModel.password
    var passwordVisible by remember { mutableStateOf(false) }
    val loginResult by loginViewModel.loginResult.collectAsState()

    // 화면 등장 애니메이션을 위한 상태
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(50) // SignUpScreen2와 동일한 속도
        visible = true
    }

    // 로그인 결과(State)가 변경되었을 때 탐지하여 화면 전환 처리
    LaunchedEffect(key1 = loginResult) {
        when (loginResult) {
            is LoginResult.Success -> {
                // 로그인 성공 시 화면 전환 콜백 실행
                onLoginSuccess()
            }

            is LoginResult.Error -> {
                // 로그인 실패 시 에러 로그 출력 (Toast나 Snackbar로 사용자에게 알림을 주도록 설계)
                val errorMessage = (loginResult as LoginResult.Error).message
                Log.e("LoginScreen", "Login failed: $errorMessage")
            }

            else -> {
                // Idle 또는 Loading 상태에서는 아무것도 하지 않음
            }
        }
    }




    Scaffold(
        containerColor = Color.White
    ) { paddingValues ->
        AnimatedVisibility(
            visible = visible,
            modifier = Modifier.fillMaxSize(),
            enter = fadeIn(tween(300)) + slideInHorizontally( // SignUpScreen2와 동일한 애니메이션
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
                    text = "로그인",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF333333)
                )
                Spacer(modifier = Modifier.height(32.dp))

                // 이메일 입력 필드
                LoginTextField(
                    value = email,
                    onValueChange = loginViewModel::onEmailChange,
                    label = "이메일 주소",
                    leadingIcon = Icons.Default.Email,
                    keyboardType = KeyboardType.Email
                )
                Spacer(modifier = Modifier.height(16.dp))

                // 비밀번호 입력 필드
                LoginPasswordTextField(
                    value = password,
                    onValueChange = loginViewModel::onPasswordChange,
                    label = "비밀번호",
                    isVisible = passwordVisible,
                    onVisibilityChange = { passwordVisible = !passwordVisible }
                )
                Spacer(modifier = Modifier.height(32.dp))

                // '로그인' 버튼
                Button(
                    onClick = {
                        Log.d("LoginTest", "로그인 버튼 클릭")
                        loginViewModel.login(email, password)
                        Log.d("LoginTest", "로그인 버튼 클릭2")

//                        when (loginResult) {
//                            is LoginResult.Success -> {
//                                onLoginSuccess()
//                            }
//
//                            is LoginResult.Error -> {}
//                            LoginResult.Idle -> {}
//                            LoginResult.Loading -> {}
//                        }

                    },
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
                    Text(text = "로그인", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                }
                Spacer(modifier = Modifier.height(16.dp))

                // '회원가입' 텍스트 버튼
                TextButton(onClick = { /*  회원가입 화면으로 이동 */ }) {
                    Text(
                        "계정이 없으신가요? 회원가입",
                        color = Color(0xFF666666),
                        fontWeight = FontWeight.Normal
                    )
                }
            }
        }
    }
}

/**
 * 일반 텍스트 입력을 위한 커스텀 OutlinedTextField
 */
@Composable
private fun LoginTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    leadingIcon: androidx.compose.ui.graphics.vector.ImageVector,
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

/**
 * 비밀번호 입력을 위한 커스텀 OutlinedTextField (보기/숨기기 기능 포함)
 */
@Composable
private fun LoginPasswordTextField(
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

