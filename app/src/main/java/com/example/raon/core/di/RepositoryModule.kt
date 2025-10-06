package com.example.raon.core.di


import com.example.raon.features.item.data.repository.ItemRepository
import com.example.raon.features.item.data.repository.ItemRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module // Hilt에게 이것이 모듈 파일임을 알립니다.
@InstallIn(SingletonComponent::class) // 이 모듈의 의존성은 앱 전체에서 싱글톤으로 관리됩니다.
abstract class RepositoryModule {

    @Binds // 인터페이스와 구현체를 연결해주는 역할을 합니다.
    @Singleton
    abstract fun bindItemRepository(
        // Hilt가 ItemRepositoryImpl을 어떻게 만드는지 알고 있으므로(생성자에 @Inject가 있음),
        // 이 구현체를 가져와서 ItemRepository에 바인딩(연결)해줍니다.
        itemRepositoryImpl: ItemRepositoryImpl
    ): ItemRepository // 누군가 ItemRepository를 요청하면 -> ItemRepositoryImpl을 주입해줘!
}