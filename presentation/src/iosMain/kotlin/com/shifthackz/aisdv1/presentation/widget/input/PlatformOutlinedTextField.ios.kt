@file:OptIn(BetaInteropApi::class, ExperimentalForeignApi::class)

package com.shifthackz.aisdv1.presentation.widget.input

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.UIKitInteropInteractionMode
import androidx.compose.ui.viewinterop.UIKitInteropProperties
import androidx.compose.ui.viewinterop.UIKitView
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import platform.CoreGraphics.CGRectMake
import platform.UIKit.NSTextAlignmentCenter
import platform.UIKit.UIColor
import platform.UIKit.UIFont
import platform.UIKit.UIKeyboardType
import platform.UIKit.UILabel
import platform.UIKit.UIView

/**
 * Exposes the `TEXT_FIELD_CORNER_RADIUS` value used by the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
internal const val TEXT_FIELD_CORNER_RADIUS = 4.0
/**
 * Exposes the `FIELD_TOP` value used by the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
internal const val FIELD_TOP = 8.0
/**
 * Exposes the `MIN_FIELD_HEIGHT` value used by the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
internal const val MIN_FIELD_HEIGHT = 56.0
/**
 * Exposes the `LABEL_HEIGHT` value used by the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
internal const val LABEL_HEIGHT = 20.0
/**
 * Exposes the `MULTILINE_TOP_INSET` value used by the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
internal const val MULTILINE_TOP_INSET = 16.0
/**
 * Exposes the `MULTILINE_HORIZONTAL_INSET` value used by the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
internal const val MULTILINE_HORIZONTAL_INSET = 14.0
/**
 * Exposes the `MULTILINE_BOTTOM_INSET` value used by the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
internal const val MULTILINE_BOTTOM_INSET = 10.0

/**
 * Renders the `PlatformOutlinedTextField` UI for the SDAI presentation layer.
 *
 * @param value value value consumed by the API.
 * @param onValueChange callback invoked by the component.
 * @param label label value consumed by the API.
 * @param containerColor container color value consumed by the API.
 * @param textColor text color value consumed by the API.
 * @param hintColor hint color value consumed by the API.
 * @param modifier Compose modifier applied to the rendered UI.
 * @param enabled enabled value consumed by the API.
 * @param error error value consumed by the API.
 * @param keyboardType keyboard type value consumed by the API.
 * @param visualTransformation visual transformation value consumed by the API.
 * @param trailingIcon trailing icon value consumed by the API.
 * @param singleLine single line value consumed by the API.
 * @author Dmitriy Moroz
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
internal actual fun PlatformOutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    containerColor: Color,
    textColor: Color,
    hintColor: Color,
    modifier: Modifier,
    enabled: Boolean,
    error: String?,
    keyboardType: KeyboardType,
    visualTransformation: VisualTransformation,
    trailingIcon: @Composable (() -> Unit)?,
    singleLine: Boolean,
) {
    var focused by remember { mutableStateOf(false) }
    val colors = MaterialTheme.colorScheme
    val borderColor = when {
        error != null -> colors.error
        focused -> colors.primary
        else -> colors.outline
    }
    val labelColor = when {
        error != null -> colors.error
        focused -> colors.primary
        else -> hintColor
    }
    val floatingLabel = focused || value.isNotEmpty()
    val textLines = if (singleLine) {
        1
    } else {
        value
            .lineSequence()
            .sumOf { line -> maxOf(1, line.length / 32 + 1) }
            .coerceIn(1, 4)
    }
    val height = if (singleLine) {
        64.dp
    } else {
        (64 + (textLines - 1) * 24).dp
    }

    Column(
        modifier = modifier.fillMaxWidth(),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(height),
        ) {
            UIKitView(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(height),
                factory = {
                    PlatformTextInputView(singleLine = singleLine).apply {
                        setFieldValue(value)
                        onTextChanged = onValueChange
                        onFocusChanged = { focused = it }
                        configure(
                            label = label,
                            floatingLabel = floatingLabel,
                            enabled = enabled,
                            secureTextEntry = visualTransformation != VisualTransformation.None,
                            keyboardType = keyboardType.toUiKeyboardType(),
                            containerColor = containerColor.toUIColor(),
                            textColor = textColor.toUIColor(),
                            placeholderColor = hintColor.toUIColor(),
                            labelColor = labelColor.toUIColor(),
                            tintColor = colors.primary.toUIColor(),
                            borderColor = borderColor.toUIColor(),
                            hasTrailingIcon = trailingIcon != null,
                        )
                    }
                },
                update = { inputView ->
                    inputView.onTextChanged = onValueChange
                    inputView.onFocusChanged = { focused = it }
                    inputView.setFieldValue(value)
                    inputView.configure(
                        label = label,
                        floatingLabel = floatingLabel,
                        enabled = enabled,
                        secureTextEntry = visualTransformation != VisualTransformation.None,
                        keyboardType = keyboardType.toUiKeyboardType(),
                        containerColor = containerColor.toUIColor(),
                        textColor = textColor.toUIColor(),
                        placeholderColor = hintColor.toUIColor(),
                        labelColor = labelColor.toUIColor(),
                        tintColor = colors.primary.toUIColor(),
                        borderColor = borderColor.toUIColor(),
                        hasTrailingIcon = trailingIcon != null,
                    )
                },
                properties = UIKitInteropProperties(
                    interactionMode = UIKitInteropInteractionMode.NonCooperative,
                    isNativeAccessibilityEnabled = true,
                    placedAsOverlay = false,
                ),
            )
            if (trailingIcon != null) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(56.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    trailingIcon()
                }
            }
        }
        if (error != null) {
            Text(
                modifier = Modifier.padding(start = 16.dp, top = 4.dp),
                text = error,
                color = colors.error,
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}

/**
 * Coordinates `PlatformTextInputView` behavior in the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
private class PlatformTextInputView(
    /**
     * Exposes the `singleLine` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    private val singleLine: Boolean,
) : UIView(
    frame = CGRectMake(0.0, 0.0, 0.0, 0.0),
) {

    private val outlineView = UIView(frame = CGRectMake(0.0, 0.0, 0.0, 0.0))
    private val textField = PlatformUITextField()
    private val textView = PlatformUITextView()
    private val labelView = UILabel(frame = CGRectMake(0.0, 0.0, 0.0, 0.0))

    private var floatingLabel: Boolean = false
    private var hasTrailingIcon: Boolean = false
    private var labelText: String = ""
    private var containerColor: UIColor = UIColor.clearColor
    private var placeholderColor: UIColor = UIColor.grayColor
    private var labelColor: UIColor = UIColor.grayColor

    var onTextChanged: (String) -> Unit
        get() = if (singleLine) textField.onTextChanged else textView.onTextChanged
        set(value) {
            if (singleLine) {
                textField.onTextChanged = value
            } else {
                textView.onTextChanged = value
            }
        }

    var onFocusChanged: (Boolean) -> Unit
        get() = if (singleLine) textField.onFocusChanged else textView.onFocusChanged
        set(value) {
            if (singleLine) {
                textField.onFocusChanged = value
            } else {
                textView.onFocusChanged = value
            }
        }

    private val activeInputView: UIView
        get() = if (singleLine) textField else textView

    init {
        opaque = false
        clipsToBounds = false
        backgroundColor = containerColor

        outlineView.opaque = false
        outlineView.userInteractionEnabled = false
        outlineView.backgroundColor = containerColor

        labelView.opaque = false
        labelView.textAlignment = NSTextAlignmentCenter
        labelView.numberOfLines = 1
        labelView.userInteractionEnabled = false

        addSubview(outlineView)
        addSubview(activeInputView)
        addSubview(labelView)
    }

    fun setFieldValue(value: String) {
        if (singleLine) {
            textField.setFieldValue(value)
        } else {
            textView.setFieldValue(value)
        }
    }

    fun configure(
        label: String,
        floatingLabel: Boolean,
        enabled: Boolean,
        secureTextEntry: Boolean,
        keyboardType: UIKeyboardType,
        containerColor: UIColor,
        textColor: UIColor,
        placeholderColor: UIColor,
        labelColor: UIColor,
        tintColor: UIColor,
        borderColor: UIColor,
        hasTrailingIcon: Boolean,
    ) {
        this.labelText = label
        this.floatingLabel = floatingLabel
        this.containerColor = containerColor
        this.placeholderColor = placeholderColor
        this.labelColor = labelColor
        this.hasTrailingIcon = hasTrailingIcon

        backgroundColor = containerColor
        outlineView.backgroundColor = containerColor
        outlineView.layer.cornerRadius = TEXT_FIELD_CORNER_RADIUS
        outlineView.layer.borderWidth = 1.0
        outlineView.layer.borderColor = borderColor.CGColor

        labelView.text = label
        labelView.font = UIFont.systemFontOfSize(if (floatingLabel) 12.0 else 16.0)
        labelView.textColor = if (floatingLabel) labelColor else placeholderColor
        labelView.backgroundColor = if (floatingLabel) containerColor else UIColor.clearColor

        if (singleLine) {
            textField.configure(
                enabled = enabled,
                secureTextEntry = secureTextEntry,
                keyboardType = keyboardType,
                textColor = textColor,
                tintColor = tintColor,
                hasTrailingIcon = hasTrailingIcon,
            )
        } else {
            textView.configure(
                enabled = enabled,
                keyboardType = keyboardType,
                textColor = textColor,
                tintColor = tintColor,
            )
        }
        setNeedsLayout()
    }

    override fun layoutSubviews() {
        super.layoutSubviews()

        val width = bounds.useContents { size.width }
        val height = bounds.useContents { size.height }
        val fieldHeight = maxOf(MIN_FIELD_HEIGHT, height - FIELD_TOP)
        outlineView.setFrame(CGRectMake(0.0, FIELD_TOP, width, fieldHeight))
        activeInputView.setFrame(CGRectMake(0.0, FIELD_TOP, width, fieldHeight))

        val labelWidth = if (labelText.isEmpty()) {
            0.0
        } else {
            val textWidth = labelText.length * if (floatingLabel) 7.0 else 9.5
            minOf(width - 28.0, textWidth + if (floatingLabel) 16.0 else 8.0)
        }
        val labelX = if (floatingLabel) 12.0 else 14.0
        val labelY = if (floatingLabel) 0.0 else FIELD_TOP + (fieldHeight - LABEL_HEIGHT) / 2.0
        labelView.setFrame(CGRectMake(labelX, labelY, labelWidth, LABEL_HEIGHT))
    }
}

