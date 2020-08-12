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
