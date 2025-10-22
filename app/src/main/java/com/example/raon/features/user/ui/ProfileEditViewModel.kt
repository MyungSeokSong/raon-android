package com.example.raon.features.user.ui

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.raon.core.common.AppConstants
import com.example.raon.core.network.ApiResult
import com.example.raon.core.network.repository.ImageStorageRepository
import com.example.raon.features.user.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject

/**
 * 프로필 수정 화면의 UI 상태를 나타내는 데이터 클래스
 */
data class ProfileEditUiState(
    val isLoading: Boolean = false,
    val initialNickname: String = "",       // 처음 화면에 진입했을 때의 닉네임 (변경 여부 확인용)
    val nickname: String = "",               // 현재 사용자가 입력 중인 닉네임
    val currentImageUrl: String? = null,     // 현재 서버에 저장된 프로필 이미지 URL
    val viewableCurrentImageUrl: String? = null, // 화면 표시용 Presigned URL
    val newImageUri: Uri? = null,            // 사용자가 갤러리에서 새로 선택한 이미지의 Uri
    val isImageRemoved: Boolean = false      // 사용자가 '프로필 사진 삭제'를 선택했는지 여부
)

@HiltViewModel
class ProfileEditViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val imageStorageRepository: ImageStorageRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileEditUiState())
    val uiState = _uiState.asStateFlow()

    // ... (init, loadCurrentUserProfile, onNicknameChanged, onImageSelected, onImageRemoved 함수는 동일) ...
    init {
        loadCurrentUserProfile()
    }

    private fun loadCurrentUserProfile() {
        viewModelScope.launch {
            val user = userRepository.getUserProfile().first()
            if (user != null) {

                var presignedUrl: String? = null
                val s3ImageKey = user.profileImage?.removePrefix(AppConstants.S3_BASE_URL)

                if (s3ImageKey != null && user.profileImage != AppConstants.DEFAULT_PROFILE_URL) {
                    try {
                        presignedUrl =
                            imageStorageRepository.getPresignedImageUrl(s3ImageKey).getOrNull()
                        Log.d(
                            "ProfileEditViewModel",
                            "✅ Initial Presigned URL loaded: $presignedUrl"
                        )
                    } catch (e: Exception) {
                        Log.e("ProfileEditViewModel", "❌ Failed to load initial Presigned URL", e)
                    }
                }

                _uiState.update {
                    it.copy(
                        initialNickname = user.nickname,
                        nickname = user.nickname,
                        currentImageUrl = user.profileImage,
                        viewableCurrentImageUrl = presignedUrl // Presigned URL 저장

                    )
                }

                Log.d("ProfileEditViewModel", "Current user profile loaded: $user")
            }
        }
    }

    fun onNicknameChanged(newNickname: String) {
        _uiState.update { it.copy(nickname = newNickname) }
    }

    fun onImageSelected(uri: Uri) {
        Log.d("ProfileImageLog", "1. Image selected from gallery. New URI: $uri")
        _uiState.update {
            it.copy(
                newImageUri = uri,
                viewableCurrentImageUrl = null, // 새 이미지를 보여줘야 하므로 기존 Presigned URL 무효화
                isImageRemoved = false
            )
        }
    }

    fun onImageRemoved() {
        Log.d("ProfileImageLog", "Image removal selected. Clearing local URI/URL.")
        _uiState.update {
            it.copy(
                currentImageUrl = null,
                viewableCurrentImageUrl = null,
                newImageUri = null,
                isImageRemoved = true
            )
        }
    }


    /**
     * '완료' 버튼을 눌렀을 때 호출됩니다.
     */
    fun onSaveChanges(onSuccess: () -> Unit) {
        viewModelScope.launch {
            // 1. 로딩 상태 시작
            _uiState.update { it.copy(isLoading = true) }

            val currentState = _uiState.value
            var nicknameSuccess = true
            var imageSuccess = true

            try {
                // 2. 이미지 변경 작업 (async로 비동기 실행)
                val imageUpdateJob = async {
                    if (currentState.isImageRemoved) {
                        // 2-1. 프로필 이미지 삭제 (기본 이미지 URL로 설정)
                        Log.d("ProfileImageLog", "Requesting: Set default profile image.")
                        userRepository.updateProfileImage("http://default.profile")

                    } else if (currentState.newImageUri != null) {
                        Log.d(
                            "ProfileImageLog",
                            "3. 'Save' clicked. Uploading URI: ${currentState.newImageUri}"
                        )

                        try {
                            // 1단계: Presigned URL 요청

                            val mimeType = context.contentResolver.getType(currentState.newImageUri)
                            val extension = mimeType?.split("/")?.lastOrNull() ?: "jpg"

                            val baseFileName = currentState.newImageUri.lastPathSegment ?: "file"
                            val fileName =
                                "profile-image-${System.currentTimeMillis()}-${baseFileName}.$extension"

//                            // 👇👇👇 [수정된 부분] Lambda 형식에 맞게 key를 조합 👇👇👇
//                            val folder = "profiles" // Lambda의 'profile' 타입
//                            val key = "$folder/$fileName"

                            Log.d("ProfileImageLog", "Generated MimeType: $mimeType, Key: ")

                            //  [수정된 부분] 2개 -> 1개의 파라미터로 수정
                            val presignedUrlResult =
                                imageStorageRepository.getPresignedUrl("profile", fileName)



                            Log.d("ProfileImageLog", "presignedUrlResult: $presignedUrlResult")

                            val presignedUrl = presignedUrlResult.getOrNull()

                            Log.d("ProfileImageLog", "presignedUrl: $presignedUrl")


                            if (presignedUrl == null) {
                                Log.e(
                                    "ProfileImageLog",
                                    "S3 Upload failed: Could not get presigned URL (Key='')"
                                )
                                throw Exception("Presigned URL 받기 실패")
                            }

                            Log.d(
                                "ProfileImageLog",
                                "3.5 'Save' clicked. presignedUrl: $presignedUrl"
                            )

                            // 2단계: Uri -> RequestBody 변환
                            val requestBody =
                                context.contentResolver.openInputStream(currentState.newImageUri)
                                    ?.use {
                                        it.readBytes().toRequestBody(
                                            mimeType?.toMediaTypeOrNull()
                                        )
                                    } ?: throw Exception("이미지 파일 읽기 실패")


                            Log.d("ProfileImageLog", " requestBody: $requestBody")

                            // 3단계: S3로 실제 파일 업로드
                            val uploadFileResult =
                                imageStorageRepository.uploadFile(presignedUrl, requestBody)

                            if (uploadFileResult.isSuccess) {
                                // 4단계: (성공) 최종 URL을 메인 서버에 전송
                                val finalUrl = presignedUrl.substringBefore("?")
                                Log.d("ProfileImageLog", "4. S3 Upload OK. Final URL: $finalUrl")
                                Log.d(
                                    "ProfileImageLog",
                                    "5. Requesting: Update profile image URL to $finalUrl"
                                )
                                userRepository.updateProfileImage(finalUrl) // 'UserRepository' 호출
                            } else {
                                Log.e(
                                    "ProfileImageLog",
                                    "S3 Upload failed: File upload step failed."
                                )
                                throw Exception("S3 파일 업로드 실패")
                            }

                        } catch (e: Exception) {
                            Log.e("ProfileImageLog", "S3 Upload failed", e)
//                            ApiResult.Error(e.message) // 👈 [수정된 부분] 이 코드는 ApiResult<Nothing>을 반환 (정상)
//                            ApiResult.Error(exception = e)
                        }

                    } else {
                        // 이미지 변경 사항 없음
                        ApiResult.Success(Unit) // 성공으로 간주
                    }
                }

                // 3. 닉네임 변경 작업 (async로 비동기 실행)
                val nicknameUpdateJob = async {
                    if (currentState.nickname != currentState.initialNickname) {
                        Log.d(
                            "ProfileEditViewModel",
                            "Requesting: Update nickname to ${currentState.nickname}"
                        )
                        userRepository.updateNickname(currentState.nickname)
                    } else {
                        // 닉네임 변경 사항 없음
                        ApiResult.Success(Unit) // 성공으로 간주
                    }
                }

                // 4. 두 작업의 결과가 모두 반환될 때까지 대기
                imageSuccess = imageUpdateJob.await() is ApiResult.Success<*>
                nicknameSuccess = nicknameUpdateJob.await() is ApiResult.Success

            } catch (e: Exception) {
                Log.e("ProfileEditViewModel", "Error saving changes", e)
                imageSuccess = false // 예외 발생 시 실패 처리
                nicknameSuccess = false
            } finally {
                // 5. 로딩 상태 종료 및 성공 시 콜백 실행
                _uiState.update { it.copy(isLoading = false) }

                if (imageSuccess && nicknameSuccess) {
                    // 모든 작업이 성공하면 DataStore의 로컬 프로필 정보도 새로고침
                    userRepository.fetchAndSaveUserProfile()
                    onSuccess()
                } else {
                    // TODO: 사용자에게 부분적/전체적 실패 메시지를 보여주는 로직 (예: Toast, Snackbar)
                    Log.w(
                        "ProfileEditViewModel",
                        "Save failed: ImageSuccess=$imageSuccess, NicknameSuccess=$nicknameSuccess"
                    )
                }
            }
        }
    }
}