package com.plcoding.stockmarketapp.data.local

import androidx.room.*

@Dao
interface BaseDao<T> {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg entity: T)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertList(entities: List<T>)
    @Update
    suspend fun update(entity: T)
    @Delete
    suspend fun delete(entity: T)
}