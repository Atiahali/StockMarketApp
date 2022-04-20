package com.plcoding.stockmarketapp.data.mapper

import com.plcoding.stockmarketapp.data.local.CompanyListingEntity
import com.plcoding.stockmarketapp.data.remote.dto.CompanyInfoDto
import com.plcoding.stockmarketapp.domain.model.CompanyInfo
import com.plcoding.stockmarketapp.domain.model.CompanyListing

class StockRepositoryMappersFacade(
    val mapCompanyListingEntity: (CompanyListingEntity) -> CompanyListing,
    val mapCompanyListing: (CompanyListing) -> CompanyListingEntity,
)

fun mapCompanyListing(input: CompanyListing): CompanyListingEntity {
    return CompanyListingEntity(
        input.name,
        input.symbol,
        input.exchange
    )
}

fun mapCompanyListingEntity(input: CompanyListingEntity): CompanyListing {
    return CompanyListing(
        input.name,
        input.symbol,
        input.exchange
    )
}

fun CompanyInfoDto.toCompanyInfo(): CompanyInfo {
    return CompanyInfo(
        symbol = symbol ?: "",
        description = description ?: "",
        name = name ?: "",
        country = country ?: "",
        industry = industry ?: ""
    )
}