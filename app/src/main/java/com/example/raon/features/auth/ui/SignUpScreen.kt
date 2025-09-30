package com.example.raon.features.auth.ui

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
import androidx.compose.material.icons.filled.Person
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.raon.R
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(
    onNavigateToLocationSetting: () -> Unit = {}
) {
    // UI 상태 관리를 위한 변수들
    var nickname by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordCheck by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var passwordCheckVisible by remember { mutableStateOf(false) }

    // 화면 등장 애니메이션을 위한 상태
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(50) // 딜레이를 0.05초로 최소화
        visible = true
    }

    Scaffold(
        containerColor = Color.White
    ) { paddingValues ->
        AnimatedVisibility(
            visible = visible,
            modifier = Modifier.fillMaxSize(),
            // 애니메이션을 0.3초로 빠르게 변경
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
                    value = nickname,
                    onValueChange = { nickname = it },
                    label = "닉네임",
                    leadingIcon = Icons.Default.Person
                )
                Spacer(modifier = Modifier.height(16.dp))

                SignUpTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = "이메일 주소",
                    leadingIcon = Icons.Default.Email,
                    keyboardType = KeyboardType.Email
                )
                Spacer(modifier = Modifier.height(16.dp))

                PasswordTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = "비밀번호",
                    isVisible = passwordVisible,
                    onVisibilityChange = { passwordVisible = !passwordVisible }
                )
                Spacer(modifier = Modifier.height(16.dp))

                PasswordTextField(
                    value = passwordCheck,
                    onValueChange = { passwordCheck = it },
                    label = "비밀번호 확인",
                    isVisible = passwordCheckVisible,
                    onVisibilityChange = { passwordCheckVisible = !passwordCheckVisible }
                )
                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = { /* TODO: 위치 데이터 로직 구현 */
                        onNavigateToLocationSetting()
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
                    Text(text = "위치 데이터", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                }

                Button(
                    onClick = { /* TODO: 가입하기 로직 구현 */ },
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
}

@Composable
private fun SignUpTextField(
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

@Preview(showBackground = true)
@Composable
fun SignUpScreenPreview() {
    SignUpScreen()
}