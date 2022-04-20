package com.plcoding.stockmarketapp.data.repository

import com.plcoding.stockmarketapp.data.csv.CSVParser
import com.plcoding.stockmarketapp.data.local.StockDatabase
import com.plcoding.stockmarketapp.data.mapper.StockRepositoryMappersFacade
import com.plcoding.stockmarketapp.data.mapper.toCompanyInfo
import com.plcoding.stockmarketapp.data.remote.StockApi
import com.plcoding.stockmarketapp.domain.model.CompanyInfo
import com.plcoding.stockmarketapp.domain.model.CompanyListing
import com.plcoding.stockmarketapp.domain.model.IntradayInfo
import com.plcoding.stockmarketapp.domain.repository.StockRepository
import com.plcoding.stockmarketapp.util.Resource
import com.plcoding.stockmarketapp.util.Resource.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StockRepositoryImpl @Inject constructor(
    private val api: StockApi,
    db: StockDatabase,
    private val companyListingsParser: CSVParser<CompanyListing>,
    private val intradayInfoParser: CSVParser<IntradayInfo>,
    private val mappersFacade : StockRepositoryMappersFacade
) : StockRepository {

    private val dao = db.dao

    override suspend fun getCompanyListings(
        fetchFromRemote: Boolean,
        query: String
    ): Flow<Resource<List<CompanyListing>>> {
        return flow {
            emit(Loading())
            val localListings = dao.searchCompanyListing(query)
            emit(Success(
                data = localListings.map { mappersFacade.mapCompanyListingEntity(it) }
            ))

            val isDbEmpty = localListings.isEmpty() && query.isBlank()
            val shouldJustLoadFromCache = !isDbEmpty && !fetchFromRemote

            if (shouldJustLoadFromCache) {
                emit(Loading(false))
                return@flow
            }
            val remoteListings = try {
                val response = api.getListings()
                companyListingsParser.parse(response.byteStream())
            } catch (e: IOException) {
                e.printStackTrace()
                emit(Error("Couldn't load data"))
                null
            } catch (e: HttpException) {
                e.printStackTrace()
                emit(Error("Couldn't load data"))
                null
            }

            remoteListings?.let { listings ->
                dao.clearCompanyListings()
                dao.insertCompanyListings(
                    listings.map { mappersFacade.mapCompanyListing(it) }
                )
                emit(Success(
                    dao
                        .searchCompanyListing("")
                        .map { mappersFacade.mapCompanyListingEntity(it) }
                ))
                emit(Loading(false))
            }
        }
    }

    override suspend fun getIntradayInfo(symbol: String): Resource<List<IntradayInfo>> {
        return try {
            val response = api.getIntradayInfo(symbol)
            val results = intradayInfoParser.parse(response.byteStream())
            Success(results)
        } catch (e: IOException) {
            e.printStackTrace()
            Error(
                message = "Couldn't load intraday info IO-ex"
            )
        } catch (e: HttpException) {
            e.printStackTrace()
            Error(
                message = "Couldn't load intraday info HTTP-ex"
            )
        }
    }

    override suspend fun getCompanyInfo(symbol: String): Resource<CompanyInfo> {
        return try {
            val dto = api.getCompanyInfo(symbol)
            Success(dto.toCompanyInfo())
        } catch (e: IOException) {
            e.printStackTrace()
            Error(
                message = "Couldn't load company info IO-ex"
            )
        } catch (e: HttpException) {
            e.printStackTrace()
            Error(
                message = "Couldn't load company info HTTP-ex"
            )
        }
    }


}