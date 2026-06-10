@file:OptIn(ExperimentalMaterial3Api::class)

package com.shifthackz.aisdv1.presentation.screen.img2img

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp


/**
 * Renders the `ImagePickerTileButton` UI for the SDAI presentation layer.
 *
 * @param text text value consumed by the API.
 * @param icon icon value consumed by the API.
 * @param enabled enabled value consumed by the API.
 * @param onClick callback invoked when the user activates the control.
 * @param modifier Compose modifier applied to the rendered UI.
 * @author Dmitriy Moroz
 */
@Composable
internal fun ImagePickerTileButton(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedButton(
        modifier = modifier.height(112.dp),
        enabled = enabled,
        shape = RoundedCornerShape(size = 28.dp),
        contentPadding = PaddingValues(8.dp),
        onClick = onClick,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Icon(
                modifier = Modifier.size(30.dp),
                imageVector = icon,
                contentDescription = null,
            )
            Text(
                modifier = Modifier.padding(top = 10.dp),
                text = text,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
            )
        }
    }
}

/**
 * Renders the `ImagePickerButton` UI for the SDAI presentation layer.
 *
 * @param text text value consumed by the API.
 * @param icon icon value consumed by the API.
 * @param enabled enabled value consumed by the API.
 * @param onClick callback invoked when the user activates the control.
 * @param modifier Compose modifier applied to the rendered UI.
 * @author Dmitriy Moroz
 */
@Composable
internal fun ImagePickerButton(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedButton(
        modifier = modifier.height(48.dp),
        enabled = enabled,
        shape = RoundedCornerShape(percent = 50),
        contentPadding = PaddingValues(horizontal = 12.dp),
        onClick = onClick,
    ) {
        Icon(
            modifier = Modifier.size(ButtonDefaults.IconSize),
            imageVector = icon,
            contentDescription = null,
        )
        Text(
            modifier = Modifier.padding(start = 6.dp),
            text = text,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}
