package net.rfrentrop.tidalremote.ui

sealed class UiState<out T> {
    object Loading: UiState<Nothing>()
    data class Success<out T>(val data: T): UiState<T>()
    data class Error(val exception: Exception): UiState<Nothing>()
}