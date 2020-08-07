package net.rfrentrop.tidalremote.screens

import android.content.Context
import androidx.compose.Composable
import androidx.compose.getValue
import androidx.compose.setValue
import androidx.ui.core.Modifier
import androidx.ui.foundation.ScrollableColumn
import androidx.ui.foundation.Text
import androidx.ui.foundation.TextField
import androidx.ui.graphics.Color
import androidx.ui.input.PasswordVisualTransformation
import androidx.ui.input.TextFieldValue
import androidx.ui.layout.Column
import androidx.ui.layout.fillMaxWidth
import androidx.ui.layout.padding
import androidx.ui.material.Button
import androidx.ui.material.MaterialTheme
import androidx.ui.material.Surface
import androidx.ui.savedinstancestate.savedInstanceState
import androidx.ui.unit.dp
import net.rfrentrop.tidalremote.MainActivity
import net.rfrentrop.tidalremote.tidalapi.TidalManager
import net.rfrentrop.tidalremote.ui.Screen

@Composable
fun ScreenSettings(activity: MainActivity, manager: TidalManager) {
    ScrollableColumn(
        modifier = Modifier.padding(10.dp)
    ) {
        Text(
            modifier = Modifier.padding(bottom = 10.dp),
            text = "Settings",
            style = MaterialTheme.typography.h1,
        )

        val preferences = activity.getPreferences(Context.MODE_PRIVATE)

        var apiToken by savedInstanceState(saver = TextFieldValue.Saver) {
            TextFieldValue(
                text = preferences.getString("api_token", "") ?: ""
            )
        }

        var username by savedInstanceState(saver = TextFieldValue.Saver) {
            TextFieldValue(
                text = preferences.getString("username", "") ?: ""
            )
        }

        var password by savedInstanceState(saver = TextFieldValue.Saver) {
            TextFieldValue(
                text = preferences.getString("password", "") ?: ""
            )
        }

        Column(modifier = Modifier.padding(10.dp)) {
            Text(
                modifier = Modifier.padding(top=10.dp, bottom=10.dp),
                text = "API Token"
            )
            Surface(
                color = Color.White
            ) {
                TextField(
                    modifier = Modifier.padding(5.dp) + Modifier.fillMaxWidth(),
                    textColor = Color.Black,
                    cursorColor = Color.Black,
                    value = apiToken,
                    onValueChange = {
                        apiToken = it
                    }
                )
            }

            Text(
                modifier = Modifier.padding(top=10.dp, bottom=10.dp),
                text = "Username"
            )
            Surface(
                color = Color.White
            ) {
                TextField(
                    modifier = Modifier.padding(5.dp) + Modifier.fillMaxWidth(),
                    textColor = Color.Black,
                    cursorColor = Color.Black,
                    value = username,
                    onValueChange = {
                        username = it
                    }
                )
            }

            Text(
                modifier = Modifier.padding(top=10.dp, bottom=10.dp),
                text = "Password"
            )
            Surface(
                color = Color.White
            ) {
                TextField(
                    modifier = Modifier.padding(5.dp) + Modifier.fillMaxWidth(),
                    textColor = Color.Black,
                    cursorColor = Color.Black,
                    value = password,
                    onValueChange = {
                        password = it
                    },
                    visualTransformation = PasswordVisualTransformation()
                )
            }

            Button(
                modifier = Modifier.padding(top=20.dp) + Modifier.fillMaxWidth(),
                onClick = {
                    val oldtoken = preferences.getString("api_token", "") ?: ""
                    val olduser = preferences.getString("username", "") ?: ""
                    val oldpass = preferences.getString("password", "") ?: ""

                    if(oldtoken != apiToken.text || olduser != username.text || oldpass != password.text) {
                        with(preferences.edit()) {
                            putString("api_token", apiToken.text)
                            putString("username", username.text)
                            putString("password", password.text)
                            apply()
                        }

                        manager.relogin()
                    }

                    activity.navigate(Screen.Collection)
                }
            ) {
                Text(
                    text = "Save",
                    style = MaterialTheme.typography.h2,
                    color = Color.White
                )
            }
        }
    }
}
