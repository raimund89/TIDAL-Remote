package net.rfrentrop.tidalremote.tidalapi

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.compose.Composable
import androidx.compose.getValue
import androidx.compose.setValue
import androidx.compose.state
import androidx.ui.core.ContextAmbient
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import net.rfrentrop.tidalremote.ui.UiState

// Square image sizes: 160, 320, 480, 640, 750, 1080
// Wide images: 160x107, 480x320, 750x500, 1080x720

@Composable
fun loadPicture(url: String): UiState<Bitmap> {
    var bitmapState: UiState<Bitmap> by state { UiState.Loading }

    Glide.with(ContextAmbient.current)
            .asBitmap()
            .load(url)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    bitmapState = UiState.Success(resource)
                }

                override fun onLoadCleared(placeholder: Drawable?) { }
            })

    return bitmapState
}
