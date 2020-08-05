package net.rfrentrop.tidalremote.tidalapi

data class TidalUser(
    var username: String = "",
    var userId: Int = 0,
    var loggedIn: Boolean = false
)
