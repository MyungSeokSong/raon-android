package com.example.raon.features.chat.data.remote.dto

// ▼▼▼ 수정된 공용 도구들을 import 합니다. ▼▼▼
import com.example.raon.core.common.toInstant
import com.example.raon.core.common.toKSTLocalDateTime
import com.example.raon.core.common.toRelativeTimeString
import com.example.raon.features.chat.domain.model.ChatMessage
import com.google.gson.annotations.SerializedName

// ▲▲▲ 여기까지 ▲▲▲


// 이 부분은 변경 없음
data class MessageListDto(
    @SerializedName("messages") val messages: List<MessageDto>,
    @SerializedName("currentPage") val currentPage: Int,
    @SerializedName("totalPages") val totalPages: Int,
    @SerializedName("totalElements") val totalElements: Long
)

// 이 부분은 변경 없음
data class MessageDto(
    @SerializedName("messageId") val messageId: Long,
    @SerializedName("chatId") val chatId: Long,
    @SerializedName("sender") val sender: SenderData2,
    @SerializedName("content") val content: String?,
    @SerializedName("imageUrl") val imageUrl: String?,
    @SerializedName("isRead") val isRead: Boolean,
    @SerializedName("isDeleted") val isDeleted: Boolean,
    @SerializedName("sentAt") val sentAt: String
)

// 이 부분은 변경 없음
data class SenderData2(
    @SerializedName("userId") val userId: Int,
    @SerializedName("nickname") val nickname: String,
    @SerializedName("profileImage") val profileImage: String?,
    @SerializedName("isDeleted") val isDeleted: Boolean
)


/**
 * MessageDto(과거 메시지 데이터)를 ChatMessage(앱 UI용 데이터)로 변환합니다.
 * 이 함수 역시 시간 변환의 최종 관문 역할을 합니다.
 */
fun MessageDto.toDomainModel(myUserId: Int?): ChatMessage {
    // 1. 서버가 준 원본 UTC 문자열을 Instant 객체로 변환합니다.
    val instant = this.sentAt.toInstant()

    // 2. 화면에 표시할 상대 시간 문자열("방금 전")을 생성합니다.
    //    (UTC Instant -> KST LocalDateTime -> 상대 시간 문자열)
    val displayTime = instant?.toKSTLocalDateTime()
        ?.toRelativeTimeString()
        ?: "" // 변환 실패 시 빈 문자열

    return ChatMessage(
        messageId = this.messageId,
        chatRoomId = this.chatId,
        senderId = this.sender.userId,
        senderNickname = this.sender.nickname,
        senderProfileUrl = this.sender.profileImage,
        content = this.content ?: "",
        imageUrl = this.imageUrl,
        isFromMe = this.sender.userId == myUserId,

        // 화면 표시용: 한국 시간 기준으로 변환된 상대 시간 ("방금 전", "어제" 등)
        timestamp = displayTime,

        // 정렬용: 서버가 준 원본 UTC 문자열을 '그대로' 저장 (가장 중요!)
        originalTimestamp = this.sentAt
    )
}