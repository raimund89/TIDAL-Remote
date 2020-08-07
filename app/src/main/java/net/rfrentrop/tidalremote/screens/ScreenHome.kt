package net.rfrentrop.tidalremote.screens

import androidx.compose.Composable
import androidx.compose.MutableState
import androidx.compose.state
import androidx.ui.core.Modifier
import androidx.ui.foundation.ScrollableColumn
import androidx.ui.foundation.Text
import androidx.ui.layout.Column
import androidx.ui.layout.padding
import androidx.ui.material.MaterialTheme
import androidx.ui.unit.dp
import net.rfrentrop.tidalremote.tidalapi.TidalManager
import net.rfrentrop.tidalremote.ui.Screen
import org.json.JSONObject

@Composable
fun ScreenHome(page: MutableState<Screen>, manager: TidalManager) {

    val searchResult = state { JSONObject() }

    manager.getHome(searchResult)

    Column(
        modifier = Modifier.padding(10.dp)
    ) {
        Text(
            text = "Home",
            style = MaterialTheme.typography.h1,
        )

        ScrollableColumn(
            modifier = Modifier.padding(top=10.dp)
        ) {

        }
    }
}
