package net.rfrentrop.tidalremote.screens

import androidx.compose.Composable
import androidx.compose.MutableState
import androidx.ui.core.Modifier
import androidx.ui.foundation.ScrollableColumn
import androidx.ui.foundation.Text
import androidx.ui.layout.padding
import androidx.ui.material.MaterialTheme
import androidx.ui.unit.dp
import net.rfrentrop.tidalremote.tidalapi.TidalManager
import net.rfrentrop.tidalremote.ui.Screen

@Composable
fun ScreenSearch(page: MutableState<Screen>, manager: TidalManager) {
    ScrollableColumn(
        modifier = Modifier.padding(10.dp)
    ) {
        Text(
            text = "Search",
            style = MaterialTheme.typography.h1,
        )
    }
}
