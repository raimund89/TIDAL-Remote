package net.rfrentrop.tidalremote.screens

import androidx.compose.Composable
import androidx.compose.MutableState
import androidx.ui.core.Modifier
import androidx.ui.foundation.Icon
import androidx.ui.foundation.ScrollableColumn
import androidx.ui.foundation.Text
import androidx.ui.layout.Row
import androidx.ui.layout.Spacer
import androidx.ui.layout.padding
import androidx.ui.material.IconButton
import androidx.ui.material.MaterialTheme
import androidx.ui.res.vectorResource
import androidx.ui.unit.dp
import net.rfrentrop.tidalremote.R
import net.rfrentrop.tidalremote.tidalapi.TidalManager
import net.rfrentrop.tidalremote.ui.Screen

@Composable
fun ScreenCollection(page: MutableState<Screen>, manager: TidalManager) {
    ScrollableColumn(
        modifier = Modifier.padding(10.dp)
    ) {
        Row {
            Text(
                text = "Collection",
                style = MaterialTheme.typography.h1,
            )
            Spacer(modifier = Modifier.weight(1f, true))
            IconButton(onClick = {
                page.value = Screen.Settings
            }) {
                Icon(vectorResource(id = R.drawable.ic_settings))
            }
        }
    }
}
