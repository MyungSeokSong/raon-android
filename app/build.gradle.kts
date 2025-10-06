import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)

    // hilt 사용 코드
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
}

// local.properties에서 값 읽기
val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localProperties.load(FileInputStream(localPropertiesFile))
}

android {
    namespace = "com.example.raon"
    compileSdk = 35

    // 공통 설정
    defaultConfig {
        applicationId = "com.example.raon"
        minSdk = 28
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // BuildConfig에 값 등록
        buildConfigField(
            "String",
            "Kakao_native_App_Key",
            localProperties.getProperty("kakao_native_app_key", "NO_KEY")
        )
        // 메니페스트에 넣을 키 등록
        manifestPlaceholders["NATIVE_APP_KEY"] =
            localProperties.getProperty("kakao_native_app_key", "NO_KEY")
    }

    buildTypes {
        debug {
            // 개발 모드 (Debug)에서만 사용할 BuildConfig 필드
//            buildConfigField("Stirng", "BASE_URL", localProperties.getProperty("BASE_URL_DEV"))

        }

        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
        buildConfig = true  // buildConfig 활성화
    }
}


dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    //
    // 추가한 라이브러리들
    //

    // Compose integration for Jatpack Navigation
    implementation("androidx.navigation:navigation-compose:2.8.9")


    // Preferences DataStore -> 유저 정보 저장
    implementation("androidx.datastore:datastore-preferences:1.0.0")

    // 파일 및 SharedPreferences 암호화를 위한 Jetpack 보안 라이브_러리
    implementation("androidx.security:security-crypto:1.0.0")


    // Retrofit 라이브러리
//    implementation("com.squareup.retrofit2:retrofit:3.0.0")
    implementation("com.squareup.retrofit2:retrofit:2.11.0") // 안정적인 최신 버전
    // Gson 라이브러리 -> Retrofit의 컨버터로 사용됨
    implementation("com.google.code.gson:gson:2.13.1")

    // CookieJar 사용을 위해
    implementation(platform("com.squareup.okhttp3:okhttp-bom:4.12.0"))
    implementation("com.squareup.okhttp3:okhttp")
    implementation("com.squareup.okhttp3:logging-interceptor") // 네트워크 로그 확인용
    implementation("com.squareup.okhttp3:okhttp-urlconnection")   // JavaNetCookieJar 사용을 위해 필요


    // Paging 라이브러리
    implementation("androidx.paging:paging-runtime:3.3.6")
    implementation("androidx.paging:paging-compose:3.3.6")    // optional - Jetpack Compose integration

    // Hilt 라이브러리
    implementation("com.google.dagger:hilt-android:2.56.2")
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")

//    ksp("com.google.dagger:hilt-android-compiler:2.56.2") // -> 이게 에러라고함
    ksp("com.google.dagger:hilt-compiler:2.56.2") // -> 위 코드 수정본

//    // Coil 라이브러리
    implementation("io.coil-kt.coil3:coil-compose:3.2.0")
    implementation("io.coil-kt.coil3:coil-network-okhttp:3.2.0")


    // 카카오 로그인 API 모듈
    implementation("com.kakao.sdk:v2-user:2.21.0")


    // 디자인 아이콘용 라이브러리
    implementation("androidx.compose.material:material-icons-extended") // 더 많은 아이콘 사용을 위해


}