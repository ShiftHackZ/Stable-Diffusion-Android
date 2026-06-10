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
import kotlinx.cinterop.ObjCAction
import kotlinx.cinterop.useContents
import platform.CoreGraphics.CGRectMake
import platform.Foundation.NSSelectorFromString
import platform.UIKit.NSTextAlignmentCenter
import platform.UIKit.UIColor
import platform.UIKit.UIControlEventEditingChanged
import platform.UIKit.UIEdgeInsetsMake
import platform.UIKit.UIFont
import platform.UIKit.UIKeyboardType
import platform.UIKit.UIKeyboardTypeASCIICapable
import platform.UIKit.UIKeyboardTypeDecimalPad
import platform.UIKit.UIKeyboardTypeDefault
import platform.UIKit.UIKeyboardTypeEmailAddress
import platform.UIKit.UIKeyboardTypeNumberPad
import platform.UIKit.UIKeyboardTypeURL
import platform.UIKit.UILabel
import platform.UIKit.UIReturnKeyType
import platform.UIKit.UITextAutocapitalizationType
import platform.UIKit.UITextAutocorrectionType
import platform.UIKit.UITextField
import platform.UIKit.UITextFieldDelegateProtocol
import platform.UIKit.UITextFieldViewMode
import platform.UIKit.UITextView
import platform.UIKit.UITextViewDelegateProtocol
import platform.UIKit.UIView


internal class PlatformUITextField : UITextField(
    frame = CGRectMake(0.0, 0.0, 0.0, 0.0),
),
    UITextFieldDelegateProtocol {

    var onTextChanged: (String) -> Unit = {}
    var onFocusChanged: (Boolean) -> Unit = {}

    init {
        delegate = this
        font = UIFont.systemFontOfSize(16.0)
        clearButtonMode = UITextFieldViewMode.UITextFieldViewModeWhileEditing
        returnKeyType = UIReturnKeyType.UIReturnKeyDone
        autocapitalizationType = UITextAutocapitalizationType.UITextAutocapitalizationTypeNone
        autocorrectionType = UITextAutocorrectionType.UITextAutocorrectionTypeNo
        clipsToBounds = true
        opaque = false
        backgroundColor = UIColor.clearColor
        addTarget(
            target = this,
            action = NSSelectorFromString("editingChanged:"),
            forControlEvents = UIControlEventEditingChanged,
        )
    }

    fun setFieldValue(value: String) {
        if (text != value) {
            text = value
        }
    }

    fun configure(
        enabled: Boolean,
        secureTextEntry: Boolean,
        keyboardType: UIKeyboardType,
        textColor: UIColor,
        tintColor: UIColor,
        hasTrailingIcon: Boolean,
    ) {
        this.enabled = enabled
        userInteractionEnabled = enabled
        this.secureTextEntry = secureTextEntry
        this.keyboardType = keyboardType
        this.textColor = textColor
        this.tintColor = tintColor
        backgroundColor = UIColor.clearColor
        leftView = UIView(frame = CGRectMake(0.0, 0.0, 14.0, 1.0))
        leftViewMode = UITextFieldViewMode.UITextFieldViewModeAlways
        rightView = if (hasTrailingIcon) {
            UIView(frame = CGRectMake(0.0, 0.0, 48.0, 1.0))
        } else {
            UIView(frame = CGRectMake(0.0, 0.0, 14.0, 1.0))
        }
        rightViewMode = UITextFieldViewMode.UITextFieldViewModeAlways
    }

    @ObjCAction
    fun editingChanged(sender: UITextField) {
        onTextChanged(sender.text.orEmpty())
    }

    override fun textFieldDidBeginEditing(textField: UITextField) {
        onFocusChanged(true)
    }

    override fun textFieldDidEndEditing(textField: UITextField) {
        onFocusChanged(false)
    }

    override fun textFieldShouldReturn(textField: UITextField): Boolean {
        textField.resignFirstResponder()
        return true
    }
}

internal class PlatformUITextView : UITextView(
    frame = CGRectMake(0.0, 0.0, 0.0, 0.0),
    textContainer = null,
),
    UITextViewDelegateProtocol {

    var onTextChanged: (String) -> Unit = {}
    var onFocusChanged: (Boolean) -> Unit = {}

    override fun debugDescription(): String? = "PlatformUITextView"

    init {
        delegate = this
        font = UIFont.systemFontOfSize(16.0)
        returnKeyType = UIReturnKeyType.UIReturnKeyDefault
        autocapitalizationType = UITextAutocapitalizationType.UITextAutocapitalizationTypeNone
        autocorrectionType = UITextAutocorrectionType.UITextAutocorrectionTypeNo
        opaque = false
        backgroundColor = UIColor.clearColor
        setTextContainerInset(
            UIEdgeInsetsMake(
                MULTILINE_TOP_INSET,
                MULTILINE_HORIZONTAL_INSET,
                MULTILINE_BOTTOM_INSET,
                MULTILINE_HORIZONTAL_INSET,
            ),
        )
        textContainer.setLineFragmentPadding(0.0)
    }

    fun setFieldValue(value: String) {
        if (text != value) {
            text = value
        }
    }

    fun configure(
        enabled: Boolean,
        keyboardType: UIKeyboardType,
        textColor: UIColor,
        tintColor: UIColor,
    ) {
        setEditable(enabled)
        setSelectable(enabled)
        userInteractionEnabled = enabled
        this.keyboardType = keyboardType
        this.textColor = textColor
        this.tintColor = tintColor
        backgroundColor = UIColor.clearColor
    }

    override fun textViewDidBeginEditing(textView: UITextView) {
        onFocusChanged(true)
    }

    override fun textViewDidEndEditing(textView: UITextView) {
        onFocusChanged(false)
    }

    override fun textViewDidChange(textView: UITextView) {
        onTextChanged(textView.text.orEmpty())
    }
}

internal fun KeyboardType.toUiKeyboardType(): UIKeyboardType = when (this) {
    KeyboardType.Ascii -> UIKeyboardTypeASCIICapable
    KeyboardType.Decimal -> UIKeyboardTypeDecimalPad
    KeyboardType.Email -> UIKeyboardTypeEmailAddress
    KeyboardType.Number -> UIKeyboardTypeNumberPad
    KeyboardType.Uri -> UIKeyboardTypeURL
    else -> UIKeyboardTypeDefault
}

internal fun Color.toUIColor(): UIColor = UIColor.colorWithRed(
    red = red.toDouble(),
    green = green.toDouble(),
    blue = blue.toDouble(),
    alpha = alpha.toDouble(),
)
