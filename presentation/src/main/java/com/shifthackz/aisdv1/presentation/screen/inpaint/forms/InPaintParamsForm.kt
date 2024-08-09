package com.shifthackz.aisdv1.presentation.screen.inpaint.forms

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.shifthackz.aisdv1.presentation.model.InPaintModel
import com.shifthackz.aisdv1.presentation.screen.inpaint.InPaintIntent
import com.shifthackz.aisdv1.presentation.theme.sliderColors
import com.shifthackz.aisdv1.presentation.utils.Constants
import kotlin.math.abs
import kotlin.math.roundToInt
import com.shifthackz.aisdv1.core.localization.R as LocalizationR

@Composable
@Preview
fun InPaintParamsForm(
    modifier: Modifier = Modifier,
    model: InPaintModel = InPaintModel(),
    processIntent: (InPaintIntent) -> Unit = {},
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            modifier = Modifier.padding(top = 8.dp),
            text = stringResource(
                id = LocalizationR.string.hint_mask_blur,
                "${model.maskBlur}",
            ),
        )
        Slider(
            value = model.maskBlur * 1f,
            valueRange = (Constants.MASK_BLUR_MIN * 1f)..(Constants.MASK_BLUR_MAX * 1f),
            steps = abs(Constants.MASK_BLUR_MIN - Constants.MASK_BLUR_MAX) - 1,
            colors = sliderColors,
            onValueChange = { processIntent(InPaintIntent.Update.MaskBlur(it.roundToInt())) },
        )

        Text(
            modifier = Modifier.padding(top = 8.dp),
            text = stringResource(id = LocalizationR.string.hint_mask_mode),
        )
        InPaintModel.MaskMode.entries.forEach { maskMode ->
            val click: () -> Unit = {
                processIntent(InPaintIntent.Update.MaskMode(maskMode))
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { click() },
                verticalAlignment = Alignment.CenterVertically,
            ) {
                RadioButton(
                    selected = model.maskMode == maskMode,
                    onClick = { click() },
                )
                Text(
                    text = stringResource(
                        id = when (maskMode) {
                            InPaintModel.MaskMode.InPaintMasked -> LocalizationR.string.in_paint_mode_masked
                            InPaintModel.MaskMode.InPaintNotMasked -> LocalizationR.string.in_paint_mode_not_masked
                        }
                    ),
                )
            }
        }

        Text(
            modifier = Modifier.padding(top = 8.dp),
            text = stringResource(id = LocalizationR.string.hint_mask_content),
        )
        InPaintModel.MaskContent.entries.forEach { maskContent ->
            val click: () -> Unit = {
                processIntent(InPaintIntent.Update.MaskContent(maskContent))
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { click() },
                verticalAlignment = Alignment.CenterVertically,
            ) {
                RadioButton(
                    selected = model.maskContent == maskContent,
                    onClick = { click() },
                )
                Text(
                    text = stringResource(
                        id = when (maskContent) {
                            InPaintModel.MaskContent.Fill -> LocalizationR.string.in_paint_mask_content_fill
                            InPaintModel.MaskContent.Original -> LocalizationR.string.in_paint_mask_content_original
                            InPaintModel.MaskContent.LatentNoise -> LocalizationR.string.in_paint_mask_content_latent_noise
                            InPaintModel.MaskContent.LatentNothing -> LocalizationR.string.in_paint_mask_content_latent_nothing
                        }
                    ),
                )
            }
        }

        Text(
            modifier = Modifier.padding(top = 8.dp),
            text = stringResource(id = LocalizationR.string.hint_in_paint_area),
        )
        InPaintModel.Area.entries.forEach { area ->
            val click: () -> Unit = {
                processIntent(InPaintIntent.Update.Area(area))
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { click() },
                verticalAlignment = Alignment.CenterVertically,
            ) {
                RadioButton(
                    selected = model.inPaintArea == area,
                    onClick = { click() },
                )
                Text(
                    text = stringResource(
                        id = when (area) {
                            InPaintModel.Area.WholePicture -> LocalizationR.string.in_paint_area_whole
                            InPaintModel.Area.OnlyMasked -> LocalizationR.string.in_paint_area_only_masked
                        }
                    ),
                )
            }
        }


        Text(
            modifier = Modifier.padding(top = 8.dp),
            text = stringResource(
                id = LocalizationR.string.hint_only_masked_padding,
                "${model.onlyMaskedPaddingPx}",
            ),
        )
        Slider(
            value = model.onlyMaskedPaddingPx * 1f,
            valueRange = (Constants.ONLY_MASKED_PADDING_MIN * 1f)..(Constants.ONLY_MASKED_PADDING_MAX * 1f),
            steps = abs(Constants.ONLY_MASKED_PADDING_MIN - Constants.ONLY_MASKED_PADDING_MAX) - 1,
            colors = sliderColors,
            onValueChange = { processIntent(InPaintIntent.Update.OnlyMaskedPadding(it.roundToInt())) },
        )
    }
}
