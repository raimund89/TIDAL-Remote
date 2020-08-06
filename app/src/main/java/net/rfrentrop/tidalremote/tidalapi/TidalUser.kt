package net.rfrentrop.tidalremote.tidalapi

import androidx.compose.getValue
import androidx.compose.mutableStateOf
import androidx.compose.setValue

class TidalUser(
    username: String = "",
    userId: Int = 0,
    loggedIn: Boolean = false
) {
    var username by mutableStateOf(username)
    var userId by mutableStateOf(userId)
    var loggedIn by mutableStateOf(loggedIn)
}
