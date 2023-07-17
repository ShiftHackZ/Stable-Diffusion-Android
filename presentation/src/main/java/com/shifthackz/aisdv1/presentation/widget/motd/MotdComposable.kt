package com.shifthackz.aisdv1.presentation.widget.motd

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.shifthackz.aisdv1.core.ui.EmptyEffect
import com.shifthackz.aisdv1.core.ui.MviScreen

class MotdComposable(
    private val viewModel: MotdViewModel,
) : MviScreen<MotdState, EmptyEffect>(viewModel) {

    @Composable
    override fun Content() {
        val state = viewModel.state.collectAsStateWithLifecycle().value
        if (state == MotdState.Hidden) return
        (state as? MotdState.Content)?.let { (title, subTitle) ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp)
                    .padding(horizontal = 16.dp)
                    .background(
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        shape = RoundedCornerShape(8.dp),
                    ),
            ) {
                val innerDimen = 8.dp
                val innerModifier = Modifier.padding(horizontal = innerDimen)
                Text(
                    modifier = innerModifier.padding(top = innerDimen),
                    text = title,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    fontWeight = FontWeight.Bold,
                    fontSize = MaterialTheme.typography.titleMedium.fontSize,
                )
                Text(
                    modifier = innerModifier.padding(vertical = innerDimen),
                    text = subTitle,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                )
            }
        }
    }
}
