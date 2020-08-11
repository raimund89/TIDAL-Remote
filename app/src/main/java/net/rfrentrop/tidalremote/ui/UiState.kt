package net.rfrentrop.tidalremote.ui

enum class Screen {
    Home,
    Videos,
    Search,
    Collection,

    Settings,

    Album,
    Artist,
    Playlist,
    Track,
    Mix,
}

sealed class UiState<out T> {
    object Loading: UiState<Nothing>()
    data class Success<out T>(val data: T): UiState<T>()
    data class Error(val exception: Exception): UiState<Nothing>()
}
