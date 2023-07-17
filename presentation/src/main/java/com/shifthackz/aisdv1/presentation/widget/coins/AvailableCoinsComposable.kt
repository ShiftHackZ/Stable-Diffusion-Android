package com.shifthackz.aisdv1.presentation.widget.coins

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Toll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.shifthackz.aisdv1.core.ui.EmptyEffect
import com.shifthackz.aisdv1.core.ui.MviScreen

class AvailableCoinsComposable(
    private val modifier: Modifier = Modifier,
    private val viewModel: AvailableCoinsViewModel,
) : MviScreen<AvailableCoinsState, EmptyEffect>(viewModel) {

    @Composable
    override fun Content() {
        when (val state = viewModel.state.collectAsStateWithLifecycle().value) {
            is AvailableCoinsState.Content -> {
                Row(
                    modifier = modifier.padding(vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                ) {
                    Icon(
                        imageVector = Icons.Default.Toll,
                        contentDescription = "SDAI Coins",
                        tint = MaterialTheme.colorScheme.primary,
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "SDAI Coins: ${state.value}",
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            else -> Unit
        }
    }
}
