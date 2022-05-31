package com.jacobao.fetchexercise

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.jacobao.fetchexercise.data.HiringItem
import com.jacobao.fetchexercise.ui.theme.Dimens.medium
import com.jacobao.fetchexercise.ui.theme.Dimens.small
import com.jacobao.fetchexercise.ui.theme.FetchExerciseTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel by viewModels<MainActivityViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FetchExerciseTheme {
                MainActivityScreen(viewModel)
            }
        }
    }
}

@Composable
private fun MainActivityScreen(viewModel: MainActivityViewModel) {
    val context = LocalContext.current
    val viewState by viewModel.viewState.collectAsState()

    // normally I would do main loading logic in ViewModel.init assuming I have the necessary
    // nav args or other data. I'm doing the main loading here just to access the context
    LaunchedEffect(Unit) {
        viewModel.loadHiringItems(context)
    }

    Scaffold(
        topBar = {
            TopAppBar { Text(stringResource(R.string.fetch_exercises)) }
        }
    ) { paddingValues ->
        Surface(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            color = MaterialTheme.colors.background
        ) {
            Column {
                if (viewState.loading) {
                    LinearProgressIndicator(Modifier.fillMaxWidth())
                }
                if (viewState.failure) {
                    Text(
                        stringResource(R.string.failed_to_load_hiring_items),
                        style = MaterialTheme.typography.h5,
                        modifier = Modifier.padding(medium)
                    )
                } else {
                    HiringItemList(viewState.hiringItemMap)
                }
            }
        }
    }
}

/**
 * Displays a scrollable list of hiring items with a header for each list
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun HiringItemList(hiringItemMap: Map<Int, List<HiringItem>>) {
    LazyColumn {
        hiringItemMap.keys.forEach { listId ->
            stickyHeader(listId) {
                ListHeader(listId)
            }

            hiringItemMap[listId]?.let { hiringItems ->
                itemsIndexed(hiringItems) { i, hiringItem ->
                    Column {
                        HiringItem(hiringItem)
                        if (i < hiringItems.size - 1) {
                            Box(
                                Modifier
                                    .background(color = MaterialTheme.colors.background)
                                    .fillMaxWidth()
                                    .height(small)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ListHeader(listId: Int) {
    Text(
        stringResource(R.string.list_x, listId),
        style = MaterialTheme.typography.h6,
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colors.background)
            .padding(medium)
    )
}

@Composable
private fun HiringItem(hiringItem: HiringItem) {
    Card(Modifier.fillMaxWidth()) {
        Row(Modifier.padding(medium)) {
            LabelData(
                stringResource(R.string.id),
                hiringItem.id.toString(),
                modifier = Modifier.weight(1f)
            )
            LabelData(
                stringResource(R.string.name),
                hiringItem.name.orEmpty(),
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun LabelData(label: String, data: String, modifier: Modifier = Modifier) {
    Column(modifier) {
        Text(
            label,
            style = MaterialTheme.typography.body1
        )
        Text(
            data,
            style = MaterialTheme.typography.body1.copy(fontWeight = FontWeight.Bold)
        )
    }
}