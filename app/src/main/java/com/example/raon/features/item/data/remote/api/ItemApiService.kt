package com.example.raon.features.item.data.remote.api

import com.example.raon.features.chat.data.remote.dto.CreateChatRoomResponseDto
import com.example.raon.features.chat.data.remote.dto.GetChatRoomResponseDto
import com.example.raon.features.item.data.remote.dto.add.ItemAddRequest
import com.example.raon.features.item.data.remote.dto.add.ItemResponse
import com.example.raon.features.item.data.remote.dto.detail.FavoriteRequest
import com.example.raon.features.item.data.remote.dto.detail.ItemDetailResponse
import com.example.raon.features.item.data.remote.dto.list.ItemListResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query


// Item과 관련된 모든 API 통신을 정의하는 인터페이스
interface ItemApiService {

    // --- 게시글 등록 API  ---
    @POST("api/v1/products")
    suspend fun postItem(
        @Body itemData: ItemAddRequest
    ): ItemResponse

    // 아이템 목록을 가져오는 API
    // @param page 페이지 번호 (0부터 시작)
    // @param size 한 페이지에 보여줄 아이템 개수
    @GET("api/v1/products") // 경로도 items로 변경하는 것이 일반적입니다. (서버와 협의 필요)
    suspend fun getItems(
        // 이름 변경
        @Query("page") page: Int,
//        @Query("size") size: Int = 20
    ): Response<ItemListResponse>

    // itemDetail을 가져오는ㄴ API
    @GET("api/v1/products{itemId}") // itemId는 상품 id
    suspend fun getItemDetail()

    // 특정 ID의 상품 상세 정보를 가져오는 GET 요청
    @GET("api/v1/products/{id}") // URL 경로의 {id} 부분은 변수임을 알림
    suspend fun getItemDetail(
        @Path("id") itemId: Int // @Path("id")는 {id} 자리에 파라미터 itemId 값을 넣으라는 의미
    ): ItemDetailResponse // 서버 응답을 받을 데이터 클래스 (DTO)


    // ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ 채팅 관련 ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ


    // 상품(아이템)에 대한 기존 채팅방 조회 API
    @GET("/api/v1/products/{itemId}/chats") // ◀◀ 경로를 items로 변경
    suspend fun getChatRoomForItem(
        @Path("itemId") itemId: Long // ◀◀ 파라미터 이름을 itemId로 변경
    ): Response<GetChatRoomResponseDto>

    // 상품(아이템)에 대한 채팅방 생성 API
    @POST("/api/v1/products/{itemId}/chats") // ◀◀ 경로를 items로 변경
    suspend fun createChatForItem(
        @Path("itemId") itemId: Long // ◀◀ 파라미터 이름을 itemId로 변경
    ): Response<CreateChatRoomResponseDto>

    // ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ 기타  ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ

    // 조회수 증가 API
    @PATCH("api/v1/products/{productId}/views")
    suspend fun increaseViewCount(@Path("productId") productId: Int): Response<Unit>


    // 찜(관심상품) 상태 변경 API
    @PUT("api/v1/products/{productId}/favorites")
    suspend fun updateFavoriteStatus(
        @Path("productId") productId: Int,
        @Body favoriteRequest: FavoriteRequest
    ): Response<Unit>
}


