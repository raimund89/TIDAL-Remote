package net.rfrentrop.tidalremote.screens

import androidx.compose.Composable
import androidx.ui.core.Modifier
import androidx.ui.foundation.ScrollableColumn
import androidx.ui.foundation.Text
import androidx.ui.layout.padding
import androidx.ui.material.MaterialTheme
import androidx.ui.unit.dp

@Composable
fun ScreenCollection() {
    ScrollableColumn(
        modifier = Modifier.padding(10.dp)
    ) {
        Text(
            text = "Collection",
            style = MaterialTheme.typography.h1,
        )
    }
}
