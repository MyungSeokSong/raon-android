// TimeExtensions.kt 파일의 모든 내용을 아래 코드로 교체하세요.

package com.example.raon.core.common

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.time.temporal.ChronoUnit

// DB/API에서 오는 'yyyy-MM-dd HH:mm:ss' 또는 'yyyy-MM-ddTHH:mm:ss' 형식을 위한 포맷터
private val UTC_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd[ ]['T']HH:mm:ss")
private val KST_ZONE_ID = ZoneId.of("Asia/Seoul")

/**
 * 다양한 형식의 UTC 시간 문자열을 Instant 객체로 변환합니다.
 * 이 함수가 정렬의 핵심 기준이 됩니다.
 * 1. ISO 표준 형식 (Z 또는 오프셋 포함)
 * 2. DB/API 형식 (시간대 정보 없음 -> UTC로 간주)
 */
fun String.toInstant(): Instant? {
    if (this.isBlank()) return null
    return try {
        // 1. 표준 형식 (..., Z, +09:00 등) 먼저 시도
        Instant.parse(this)
    } catch (e: DateTimeParseException) {
        try {
            // 2. 표준 형식이 아니면, DB/API 형식(yyyy-MM-dd HH:mm:ss)을 UTC로 간주하여 파싱
            val localDateTime = LocalDateTime.parse(this, UTC_DATE_TIME_FORMATTER)
            localDateTime.toInstant(ZoneOffset.UTC) // "이 시간은 UTC 기준이다"라고 명시
        } catch (e2: DateTimeParseException) {
            null // 두 형식 모두 실패하면 null 반환
        }
    }
}

/**
 * Instant(UTC 기준) 객체를 한국 시간(KST)인 LocalDateTime으로 변환합니다.
 * 화면 표시에 사용됩니다.
 */
fun Instant.toKSTLocalDateTime(): LocalDateTime {
    return this.atZone(KST_ZONE_ID).toLocalDateTime()
}

/**
 * 한국 시간(KST) 기준의 LocalDateTime을 "방금 전"과 같은 상대 시간 문자열로 변환합니다.
 */
fun LocalDateTime.toRelativeTimeString(): String {
    val now = LocalDateTime.now(KST_ZONE_ID) // 현재 시간도 KST 기준으로 가져옵니다.

    val days = ChronoUnit.DAYS.between(this.toLocalDate(), now.toLocalDate())
    if (days > 1) return "${days}일 전"
    if (days == 1L) return "어제"

    val hours = ChronoUnit.HOURS.between(this, now)
    if (hours > 0) return "${hours}시간 전"

    val minutes = ChronoUnit.MINUTES.between(this, now)
    if (minutes > 0) return "${minutes}분 전"

    return "방금 전"
}