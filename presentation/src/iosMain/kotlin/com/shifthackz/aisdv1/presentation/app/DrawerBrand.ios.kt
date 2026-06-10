package com.shifthackz.aisdv1.presentation.app

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.shifthackz.aisdv1.presentation.generated.resources.Res
import com.shifthackz.aisdv1.presentation.generated.resources.sdai_logo
import com.shifthackz.aisdv1.presentation.widget.icon.BrandIcons
import org.jetbrains.compose.resources.painterResource

@Composable
internal actual fun DrawerBrandLogo(modifier: Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        color = Color.White,
        tonalElevation = 2.dp,
    ) {
        Box(contentAlignment = Alignment.Center) {
            Image(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(14.dp)),
                painter = painterResource(Res.drawable.sdai_logo),
                contentDescription = "SDAI",
            )
        }
    }
}

@Composable
internal actual fun DrawerPlatformIcon(modifier: Modifier) {
    Icon(
        modifier = modifier,
        imageVector = BrandIcons.Apple,
        contentDescription = "iOS",
    )
}
