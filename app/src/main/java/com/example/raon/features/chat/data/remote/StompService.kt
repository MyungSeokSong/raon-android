package com.example.raon.features.chat.data.remote


import android.util.Log
import com.example.raon.features.auth.data.local.TokenManager
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.launch
import org.hildan.krossbow.stomp.StompClient
import org.hildan.krossbow.stomp.StompSession
import org.hildan.krossbow.stomp.subscribeText
import org.hildan.krossbow.websocket.okhttp.OkHttpWebSocketClient
import javax.inject.Inject
import javax.inject.Singleton

data class ChatMessageDto(
    val senderId: Long,
    val content: String,
    val timestamp: String
)

@Singleton
class StompService @Inject constructor(
    private val tokenManager: TokenManager // 채팅에서 인증할 토큰을 가져올 TokenManeger
) {

    private val stompClient = StompClient(OkHttpWebSocketClient())
    private var session: StompSession? = null

    // 서버에서 Stomp 실시간 데이터를 Json 형식으로 받아서 dto로 저장해서 다룰 때 코드
//    private val _messages = MutableSharedFlow<ChatMessageDto>()
//    val messages: Flow<ChatMessageDto> get() = _messages.asSharedFlow()


    // ▼▼▼ 1. Flow가 String을 방출하도록 타입을 변경합니다. ▼▼▼
    private val _messages = MutableSharedFlow<String>()
    val messages: Flow<String> get() = _messages.asSharedFlow()

    private val gson = Gson()
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    suspend fun connectAndSubscribe(chatRoomId: Long) {
        try {
            if (session != null) return

            val authToken = tokenManager.getAccessToken() // 토큰을 내부에서 직접 가져옴
            val connectHeaders = mapOf("Authorization" to "Bearer $authToken")

            // 토큰이 있는지 반드시 확인합니다.
            if (authToken.isNullOrBlank()) {
                Log.e("StompService", "Auth token is null or blank. Connection aborted.")
                return
            }

            Log.d("StompService", "토큰 형식: $connectHeaders") // ⬅️ 여기서 원본 JSON 확인!


            val webSocketUrl = "ws://10.0.2.2:4000/ws" // -> 애뮬레이터 용
//            val webSocketUrl = "ws://localhost:4000/ws" // 실제 서버 엔드포인트로 교체


            // 헤더에 AccessToken 넣어서 만들기
            session = stompClient.connect(
                url = webSocketUrl,
                passcode = authToken
            )

            Log.d("StompService", "✅ STOMP connection successful! Session created.")


            // stomp subscribe destination
//            val destination = "/chat/${chatRoomId}"   // 수정전
            val destination = "/user/chat"     // 수정후



            Log.d("StompService", "destination : ${destination}")


            scope.launch {
                session!!.subscribeText(destination)
                    .mapNotNull { chatmessage ->
                        try {

                            // ▼▼▼ 바로 이 로그입니다! ▼▼▼
                            // 서버가 STOMP로 보낸 순수한 JSON 문자열이 그대로 출력됩니다.
                            Log.d("StompService", "수신 데이터: $chatmessage") // ⬅️ 여기서 원본 JSON 확인!

                            // 데이터 내보내기
                            _messages.emit(chatmessage)

//                            gson.fromJson(jsonString, ChatMessageDto::class.java)
                        } catch (e: Exception) {
                            Log.e("StompService", "JSON parsing failed", e)
                            null
                        }
                    }
                    .catch { e -> Log.e("StompService", "Error receiving messages", e) }
                    .collect { messageDto ->
                        Log.d("StompService", "실시간 메시지 파싱: $messageDto") // ⬅️ 파싱 완료 DTO 로그!
//                        _messages.emit(messageDto)
                    }
            }
        } catch (e: Exception) {
            Log.e("StompService", "STOMP connection failed", e)
        }
    }

//    suspend fun sendMessage(chatRoomId: Long, message: String) {
//        session?.let { currentSession ->
//            try {
//                val destinationPath = "/chat/${chatRoomId}" // send destination
//
//                Log.e("StompService", "destinationPath: ${destinationPath}")
//
//
//                val payload = ChatMessageDto(
//                    senderId = 1, // 로그인 유저 정보를 실제로 대입
//                    content = message,
//                    timestamp = ""   // 필요시 설정
//                )
//                val jsonString = gson.toJson(payload)
//                // 함수 시그니처대로 destination, body 순서로 전달!
//                currentSession.sendText(destinationPath, jsonString)
//
//            } catch (e: Exception) {
//                Log.e("StompService", "Failed to send message", e)
//            }
//        }
//    }

    suspend fun disconnect() {
        try {
            session?.disconnect()
            Log.d("StompService", "Stomp 연결종료 : Disconnecting STOMP...")

        } catch (e: Exception) {
            Log.e("StompService", "Failed to disconnect STOMP", e)
        } finally {
            session = null
        }
    }
}
