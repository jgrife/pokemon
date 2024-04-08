package com.example.pokemon.ui

sealed class UIResult<out T> {
    data object Idle : UIResult<Nothing>()
    data object Loading : UIResult<Nothing>()
    data class Success<T>(val data: T) : UIResult<T>()
    data class Error(val e: Throwable) : UIResult<Nothing>()
}

fun <T> Result<T>.toUIResult(): UIResult<T> {
    if (this.isFailure) {
        return UIResult.Error(exceptionOrNull() ?: Exception())
    }
    val data = getOrElse { return UIResult.Error(exceptionOrNull() ?: Exception()) }
    return UIResult.Success(data)
}