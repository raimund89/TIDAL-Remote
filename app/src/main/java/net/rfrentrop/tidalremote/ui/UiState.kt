package net.rfrentrop.tidalremote.ui

import net.rfrentrop.tidalremote.tidalapi.PageType

enum class Screen {
    Home,
    Videos,
    Search,
    Collection,

    Settings,

    Page,
    Track,
}

data class BackstackItem (
        val page: Screen,
        val type: PageType = PageType.NONE,
        val id: String = ""
)

sealed class UiState<out T> {
    object Loading: UiState<Nothing>()
    data class Success<out T>(val data: T): UiState<T>()
    data class Error(val exception: Exception): UiState<Nothing>()
}

sealed class RefreshableUiState<out T> {
    data class Success<out T>(val data: T?, val loading: Boolean) : RefreshableUiState<T>()
    data class Error<out T>(val exception: Exception, val previousData: T?) :
            RefreshableUiState<T>()
}

data class RefreshableUiStateHandler<out T>(
        val state: RefreshableUiState<T>,
        val refreshAction: () -> Unit
)

val <T> RefreshableUiState<T>.loading: Boolean
    get() = this is RefreshableUiState.Success && this.loading && this.data == null

val <T> RefreshableUiState<T>.currentData: T?
    get() = when (this) {
        is RefreshableUiState.Success -> this.data
        is RefreshableUiState.Error -> this.previousData
    }
