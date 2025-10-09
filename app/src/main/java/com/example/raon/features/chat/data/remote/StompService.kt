//package com.example.raon.features.chat.data.remote
//
//import com.example.raon.features.chat.data.remote.dto.etc.ChatMessageDto
//import com.google.gson.Gson
//import kotlinx.coroutines.flow.Flow
//import kotlinx.coroutines.flow.map
//import org.hildan.krossbow.stomp.StompClient
//import org.hildan.krossbow.stomp.StompSession
//import org.hildan.krossbow.stomp.frame.StompFrame
//import org.hildan.krossbow.stomp.headers.StompSendHeaders
//import org.hildan.krossbow.stomp.headers.StompSubscribeHeaders
//import org.hildan.krossbow.websocket.okhttp.OkHttpWebSocketClient
//import javax.inject.Inject
//import javax.inject.Singleton
//
//
//@Singleton
//class StompService @Inject constructor(private val gson: Gson) {
//
//    // websocket-builtin 의존성을 사용하므로 BuiltInWebSocketClient()를 사용합니다.
//    private val client = StompClient(OkHttpWebSocketClient())
//    private var session: StompSession? = null
//
//    suspend fun connect(url: String) {
//        if (session?. == true) return
//        session = client.connect(url)
//    }
//
//    suspend fun subscribeToChatRoom(roomId: String): Flow<ChatMessageDto> {
//        val destination = "/topic/chat/room/$roomId"
//        val session = this.session ?: throw IllegalStateException("STOMP session not available")
//
//        val subscribeHeaders = StompSubscribeHeaders(destination = destination)
//        return session.subscribe(subscribeHeaders).map { frame ->
//            val body = (frame as StompFrame.Message).bodyAsText
//            gson.fromJson(body, ChatMessageDto::class.java)
//        }
//    }
//
//    suspend fun sendMessage(roomId: String, message: Any) {
//        val destination = "/app/chat/message/$roomId"
//        val session = this.session ?: throw IllegalStateException("STOMP session not available")
//
//        val body = gson.toJson(message)
//        val sendHeaders = StompSendHeaders(
//            destination = destination,
//            customHeaders = mapOf("content-type" to "application/json")
//        )
//        session.send(sendHeaders, body)
//    }
//
//    suspend fun disconnect() {
//        session?.disconnect()
//        session = null
//    }
//}