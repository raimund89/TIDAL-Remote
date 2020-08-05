package net.rfrentrop.tidalremote.screens

import androidx.compose.*
import androidx.ui.core.Alignment
import androidx.ui.core.Modifier
import androidx.ui.foundation.Icon
import androidx.ui.foundation.ScrollableColumn
import androidx.ui.foundation.Text
import androidx.ui.foundation.TextField
import androidx.ui.graphics.Color
import androidx.ui.input.TextFieldValue
import androidx.ui.layout.Row
import androidx.ui.layout.padding
import androidx.ui.material.MaterialTheme
import androidx.ui.material.Surface
import androidx.ui.res.vectorResource
import androidx.ui.savedinstancestate.savedInstanceState
import androidx.ui.unit.dp
import net.rfrentrop.tidalremote.R
import net.rfrentrop.tidalremote.tidalapi.TidalManager
import net.rfrentrop.tidalremote.ui.Screen
import org.json.JSONObject

@Composable
fun ScreenSearch(page: MutableState<Screen>, manager: TidalManager) {

    val searchResult = state { JSONObject() }
    var lastSearch = 0L

    ScrollableColumn(
        modifier = Modifier.padding(10.dp)
    ) {

        var searchval by savedInstanceState(saver = TextFieldValue.Saver) { TextFieldValue() }

        Surface(color = Color.White) {
            Row {
                Icon(
                    modifier = Modifier.gravity(Alignment.CenterVertically) + Modifier.padding(start=10.dp),
                    asset = vectorResource(id = R.drawable.ic_search),
                    tint = Color.Black
                )
                TextField(
                    modifier = Modifier.padding(15.dp) + Modifier.weight(1f, true),
                    value = searchval,
                    onValueChange = {
                        searchval = it
                        // TODO: The last entered character is not searched now. Make this a queued system
                        if(!it.text.isBlank() && System.currentTimeMillis() - lastSearch > 1000L) {
                            lastSearch = System.currentTimeMillis()
                            manager.search(it.text, searchResult)
                        }
                        else
                            manager.getExplore(searchResult)
                    },
                    textStyle = MaterialTheme.typography.h3,
                    textColor = Color.Gray,
                    cursorColor = Color.Gray
                )
                Icon(
                    modifier = Modifier.gravity(Alignment.CenterVertically) + Modifier.padding(end=10.dp),
                    asset = vectorResource(id = R.drawable.ic_clear),
                    tint = Color.Black
                )
            }
        }

        Text(
            text = searchResult.value.toString()
        )
    }
}
