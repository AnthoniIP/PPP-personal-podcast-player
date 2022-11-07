package com.ipsoft.ppp.ui.common

import androidx.compose.runtime.Composable
import com.google.accompanist.insets.ProvideWindowInsets
import com.ipsoft.ppp.ui.navigation.ProvideNavHostController
import com.ipsoft.ppp.ui.theme.PodcastAppTheme

@Composable
fun PreviewContent(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    PodcastAppTheme(darkTheme = darkTheme) {
        ProvideWindowInsets {
            ProvideNavHostController {
                content()
            }
        }
    }
}
