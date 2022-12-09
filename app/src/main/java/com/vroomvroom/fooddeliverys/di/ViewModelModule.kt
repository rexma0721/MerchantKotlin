package com.vroomvroom.fooddeliverys.di

import android.content.BroadcastReceiver
import com.vroomvroom.fooddeliverys.repository.auth.AuthRepository
import com.vroomvroom.fooddeliverys.repository.auth.AuthRepositoryImpl
import com.vroomvroom.fooddeliverys.repository.cart.CartRepository
import com.vroomvroom.fooddeliverys.repository.cart.CartRepositoryImpl
import com.vroomvroom.fooddeliverys.repository.local.RoomRepository
import com.vroomvroom.fooddeliverys.repository.local.RoomRepositoryImpl
import com.vroomvroom.fooddeliverys.repository.merchant.MerchantRepository
import com.vroomvroom.fooddeliverys.repository.merchant.MerchantRepositoryImpl
import com.vroomvroom.fooddeliverys.repository.order.OrderRepository
import com.vroomvroom.fooddeliverys.repository.order.OrderRepositoryImpl
import com.vroomvroom.fooddeliverys.repository.services.FirebaseAuthRepository
import com.vroomvroom.fooddeliverys.repository.services.FirebaseAuthRepositoryImpl
import com.vroomvroom.fooddeliverys.repository.services.LocationRepository
import com.vroomvroom.fooddeliverys.repository.services.LocationRepositoryImpl
import com.vroomvroom.fooddeliverys.repository.user.UserRepository
import com.vroomvroom.fooddeliverys.repository.user.UserRepositoryImpl
import com.vroomvroom.fooddeliverys.utils.SmsBroadcastReceiver
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
abstract class ViewModelModule {

    @Binds
    @ViewModelScoped
    abstract fun bindMerchantRepository(repo: MerchantRepositoryImpl): MerchantRepository

    @Binds
    @ViewModelScoped
    abstract fun bindOrderRepository(repo: OrderRepositoryImpl): OrderRepository

    @Binds
    @ViewModelScoped
    abstract fun bindAuthRepository(repo: AuthRepositoryImpl): AuthRepository

    @Binds
    @ViewModelScoped
    abstract fun bindUserRepository(repo: UserRepositoryImpl): UserRepository

    @Binds
    @ViewModelScoped
    abstract fun cartRepository(repo: CartRepositoryImpl) : CartRepository

    @Binds
    @ViewModelScoped
    abstract fun roomRepository(repo: RoomRepositoryImpl) : RoomRepository

    @Binds
    @ViewModelScoped
    abstract fun firebaseAuthRepository(repo: FirebaseAuthRepositoryImpl) : FirebaseAuthRepository

    @Binds
    @ViewModelScoped
    abstract fun smsBroadcastReceiver(broadcastReceiver: SmsBroadcastReceiver) : BroadcastReceiver

    @Binds
    @ViewModelScoped
    abstract fun locationRepository(repo: LocationRepositoryImpl) : LocationRepository
}