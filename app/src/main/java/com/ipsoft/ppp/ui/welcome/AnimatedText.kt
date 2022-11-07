package com.ipsoft.ppp.ui.welcome

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.ipsoft.ppp.R

@Composable
fun AnimatedText(visible: Boolean) {
    val titleEnterTransition = fadeIn(
        animationSpec = tween(1000, 1600)
    ) + slideInVertically(
        initialOffsetY = { -100 },
        animationSpec = tween(1000, 1600)
    )

    AnimatedVisibility(
        visible = visible,
        enter = titleEnterTransition,
    ) {
        Text(
            text = stringResource(R.string.welcome_subtitle),
            style = MaterialTheme.typography.h2,
            textAlign = TextAlign.Center
        )
    }
}
