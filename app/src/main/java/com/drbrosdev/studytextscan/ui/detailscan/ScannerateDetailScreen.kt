package com.drbrosdev.studytextscan.ui.detailscan

import androidx.compose.animation.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.staggeredgrid.LazyHorizontalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.drbrosdev.studytextscan.R
import com.drbrosdev.studytextscan.persistence.entity.ExtractionModel
import com.drbrosdev.studytextscan.ui.detailscan.components.*
import com.drbrosdev.studytextscan.util.dateAsString

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ScannerateDetailScreen(
    modifier: Modifier = Modifier,
    state: DetailScanUiState,
    onTitleTextChanged: (String) -> Unit,
    onContentChanged: (String) -> Unit,
    onPinClicked: () -> Unit,
    onChipClicked: (ExtractionModel) -> Unit,
    onBackClick: () -> Unit,
    onPdfExport: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val topBarHeight = 72.dp
    val columnScrollState = rememberLazyListState()

    val topBarState by remember {
        derivedStateOf {
            if (columnScrollState.firstVisibleItemIndex > 0)
                ScanDetailTopBarState.NORMAL
            else ScanDetailTopBarState.EXPANDED
        }
    }

    if (state.scan == null) {
        ScannerateLoadingScreen()
    }

    state.scan?.let { scan ->
        //debounced, local state to represent truth
        var title by remember { mutableStateOf(scan.scanTitle) }
        //debounced, local state to represent truth
        var content by remember { mutableStateOf(scan.scanText) }

        Box(
            modifier = Modifier
                .then(modifier)
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .imePadding(),
                state = columnScrollState
            ) {
                item {
                    Spacer(modifier = Modifier.height(56.dp))
                }

                item { //header
                    Column {
                        ScannerateTextField(
                            modifier = Modifier.fillMaxWidth(),
                            text = title,
                            onTextChanged = {
                                title = it
                                onTitleTextChanged(it)
                            },
                            maxLines = 2
                        )
                        ScannerateDateText(
                            text = stringResource(
                                R.string.text_date_created,
                                dateAsString(scan.dateCreated)
                            )
                        )
                        ScannerateDateText(
                            text = stringResource(
                                R.string.text_date_modified,
                                dateAsString(scan.dateModified)
                            )
                        )
                    }
                }

                if (state.filteredTextModels.isNotEmpty()) {
                    item {
                        LazyHorizontalStaggeredGrid(
                            rows = StaggeredGridCells.Fixed(2),
                            modifier = Modifier
                                .padding(top = 10.dp)
                                .height(88.dp)
                                .fillMaxWidth(),
                            contentPadding = PaddingValues(horizontal = 12.dp),
                            verticalArrangement = Arrangement.spacedBy(
                                2.dp,
                                alignment = Alignment.CenterVertically
                            ),
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                        ) {
                            items(state.filteredTextModels) {
                                TextEntityChip(
                                    entity = it,
                                    onClick = { onChipClicked(it) }
                                )
                            }
                        }
                    }
                }

                item {
                    ScannerateTextField(
                        modifier = Modifier,
                        text = content,
                        onTextChanged = {
                            content = it
                            onContentChanged(it)
                        },
                        fontSize = 17.sp
                    )

                }

                item {
                    Spacer(modifier = Modifier.height(72.dp))
                }
            }

            ScanDetailTopBar(
                height = topBarHeight,
                topBarState = topBarState,
                modifier = Modifier
                    .align(Alignment.TopEnd),
                isPinned = scan.isPinned,
                onPinClicked = onPinClicked,
                onBackClicked = onBackClick,
                onPdfExportClicked = onPdfExport,
                onDeleteClicked = onDeleteClick,
                onSaveClicked = { } //can do nothing
            )

            AnimatedVisibility(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .navigationBarsPadding(),
                visible = columnScrollState.isScrollingUp(),
                enter = fadeIn() + expandIn(expandFrom = Alignment.Center),
                exit = fadeOut() + shrinkOut(shrinkTowards = Alignment.Center)
            ) {
                ScanDetailBottomBar(
                    modifier = Modifier
                )
            }
        }
    }
}
