package com.example.ivanschuetz.aacmlcodechallenge

/**
 * Minimal result holder
 *
 * Created by ivanschuetz on 18.12.17.
 */
sealed class Result<out T>
data class Success<out T>(val value: T): Result<T>()
data class Error<out T>(val msg: String): Result<T>()
