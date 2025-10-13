package com.example.raon.features.location.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * 서버의 위치 검색 API 응답 전체를 표현하는 최상위 DTO 클래스입니다.

 * 응답의 data 필드 내부를 표현하는 DTO 입니다.
 * 실제 위치 목록과 페이지 정보를 포함합니다.
 */


data class LocationResponseDto(
    @SerializedName("locations")
    val locations: List<LocationDto>,

    @SerializedName("currentPage")
    val currentPage: Int,

    @SerializedName("totalPages")
    val totalPages: Int,

    @SerializedName("totalElements")
    val totalElements: Long
)

/**
 * locations 배열 안의 개별 주소 객체를 표현하는 DTO 입니다.
 */
data class LocationDto(
    @SerializedName("locationId")
    val locationId: Int,

    @SerializedName("address")
    val address: String
)