package com.plcoding.stockmarketapp.di

import android.app.Application
import androidx.room.Room
import com.plcoding.stockmarketapp.data.local.StockDatabase
import com.plcoding.stockmarketapp.data.mapper.StockRepositoryMappersFacade
import com.plcoding.stockmarketapp.data.mapper.mapCompanyListing
import com.plcoding.stockmarketapp.data.mapper.mapCompanyListingEntity
import com.plcoding.stockmarketapp.data.remote.StockApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideStockApi(): StockApi {
        return Retrofit.Builder()
            .baseUrl(StockApi.BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create()
    }

    @Provides
    @Singleton
    fun provideStockDatabase(app: Application): StockDatabase {
        return Room.databaseBuilder(
            app,
            StockDatabase::class.java,
            "stockdb.db"
        ).build()
    }

//    @Provides
//    @Singleton
//    fun bindStockRepository(
//        api: StockApi,
//        db: StockDatabase,
//        companyListingsParser: CSVParser<CompanyListing>,
//        intradayInfoParser: CSVParser<IntradayInfo>
//    ): StockRepository {
//
//        return StockRepositoryImpl(
//            api,
//            db,
//            companyListingsParser,
//            intradayInfoParser,
//            mappersFacade
//        )
//    }

    @Provides
    fun provideStockRepositoryMappersFacade(): StockRepositoryMappersFacade =
        StockRepositoryMappersFacade(
            { entity -> mapCompanyListingEntity(entity) },
            { model -> mapCompanyListing(model) }
        )

}