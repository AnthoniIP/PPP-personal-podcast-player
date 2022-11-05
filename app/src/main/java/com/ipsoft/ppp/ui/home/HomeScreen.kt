package com.ipsoft.ppp.ui.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.insets.statusBarsPadding
import com.ipsoft.ppp.R
import com.ipsoft.ppp.domain.model.Episode
import com.ipsoft.ppp.ui.common.PreviewContent
import com.ipsoft.ppp.ui.common.SearchBar
import com.ipsoft.ppp.ui.common.StaggeredVerticalGrid
import com.ipsoft.ppp.ui.common.ViewModelProvider
import com.ipsoft.ppp.ui.navigation.Destination
import com.ipsoft.ppp.ui.navigation.Navigator
import com.ipsoft.ppp.util.Resource

@Composable
fun HomeScreen() {
    val scrollState = rememberLazyListState()
    val navController = Navigator.current
    var lastSearch = remember { "" }
    val podcastSearchViewModel = ViewModelProvider.podcastSearch
    val podcastSearch = podcastSearchViewModel.podcastSearch

    Surface {
        LazyColumn(state = scrollState, modifier = Modifier.statusBarsPadding()) {
            item {
                SearchBar(searchText = lastSearch,
                    placeholderText = stringResource(id = R.string.search_podcasts),
                    onSearchTextChanged = {
                        lastSearch = it
                        podcastSearchViewModel.searchPodcasts(lastSearch)
                    }
                )
            }
            item {
                LargeTitle()
            }


            when (podcastSearch) {
                is Resource.Error -> {
                    item {
                        ErrorView(text = podcastSearch.failure.translate()) {
                            podcastSearchViewModel.searchPodcasts()
                        }
                    }
                }
                Resource.Loading -> {
                    item {
                        LoadingPlaceholder()
                    }
                }
                is Resource.Success -> {
                    item {
                        StaggeredVerticalGrid(
                            crossAxisCount = 2,
                            spacing = 16.dp,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        ) {
                            podcastSearch.data.results.forEach { podcast ->
                                PodcastView(
                                    podcast = podcast,
                                    modifier = Modifier.padding(bottom = 16.dp)
                                ) {
                                    openPodcastDetail(navController, podcast)
                                }
                            }
                        }
                    }
                }
            }

            item {
                Box(
                    modifier = Modifier
                        .navigationBarsPadding()
                        .padding(bottom = 32.dp)
                        .padding(bottom = if (ViewModelProvider.podcastPlayer.currentPlayingEpisode.value != null) 64.dp else 0.dp)
                )
            }
        }
    }
}

private fun openPodcastDetail(
    navController: NavHostController,
    podcast: Episode,
) {
    navController.navigate(Destination.podcast(podcast.id)) { }
}

@Composable
@Preview(name = "Home")
fun HomeScreenPreview() {
    PreviewContent {
        HomeScreen()
    }
}

@Composable
@Preview(name = "Home (Dark)")
fun HomeScreenDarkPreview() {
    PreviewContent(darkTheme = true) {
        HomeScreen()
    }
}