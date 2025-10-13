package com.example.raon.core.di


import com.example.raon.features.category.data.repository.CategoryRepositoryImpl
import com.example.raon.features.category.domain.repository.CategoryRepository
import com.example.raon.features.chat.data.repository.ChatRepositoryImpl
import com.example.raon.features.chat.domain.repository.ChatRepository
import com.example.raon.features.item.data.repository.ItemRepository
import com.example.raon.features.item.data.repository.ItemRepositoryImpl
import com.example.raon.features.location.data.repository.LocationRepositoryImpl
import com.example.raon.features.location.domain.repository.LocationRepository
import com.example.raon.features.user.data.repository.UserRepositoryImpl
import com.example.raon.features.user.domain.repository.UserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module // Hilt에게 이것이 모듈 파일임을 알림
@InstallIn(SingletonComponent::class) // 이 모듈의 의존성은 앱 전체에서 싱글톤으로 관리
abstract class RepositoryModule {


    // 👇 여기에 이 코드를 추가하세요!
    @Binds
    @Singleton
    abstract fun bindCategoryRepository(
        categoryRepositoryImpl: CategoryRepositoryImpl
    ): CategoryRepository // CategoryRepository를 요청하면 -> CategoryRepositoryImpl을 주입


    @Binds // 인터페이스와 구현체를 연결해주는 역할을 함
    @Singleton
    abstract fun bindItemRepository(
        // Hilt가 ItemRepositoryImpl을 어떻게 만드는지 알고 있으므로(생성자에 @Inject가 있음),
        // 이 구현체를 가져와서 ItemRepository에 바인딩(연결)
        itemRepositoryImpl: ItemRepositoryImpl
    ): ItemRepository // 누군가 ItemRepository를 요청하면 -> ItemRepositoryImpl을 주입


    //
    @Binds
    @Singleton
    abstract fun bindUserRepository(
        userRepositoryImpl: UserRepositoryImpl
    ): UserRepository // UserRepository를 요청하면 -> UserRepositoryImpl을 주입


    // 👇 이 부분을 추가하시면 됩니다.
    @Binds
    @Singleton
    abstract fun bindChatRepository(
        chatRepositoryImpl: ChatRepositoryImpl
    ): ChatRepository // ChatRepository를 요청하면 -> ChatRepositoryImpl을 주입


    // @Binds 어노테이션을 사용하여 Hilt에게 지시합니다.
    @Binds
    @Singleton // 앱 전체에서 Repository 인스턴스를 하나만 사용하도록 설정
    abstract fun bindLocationRepository(
        // 'LocationRepositoryImpl' 객체를
        locationRepositoryImpl: LocationRepositoryImpl
        // 'LocationRepository' 인터페이스 타입으로 주입해달라는 의미
    ): LocationRepository
}