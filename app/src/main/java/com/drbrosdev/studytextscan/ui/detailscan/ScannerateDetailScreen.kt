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
    onDeleteClick: () -> Unit,
    onCopyClick: () -> Unit,
    onShareClick: () -> Unit,
    onTtsClick: () -> Unit,
    onTranslateClick: () -> Unit
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

    if (state.isLoading) {
        ScannerateLoadingScreen()
    }

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
            item(key = "top_spacer") {
                Spacer(modifier = Modifier.height(56.dp))
            }

            item(key = "scan_header") {
                state.scan?.let { scan ->
                    ScanDetailHeader(
                        title = scan.scanTitle,
                        onTitleChanged = { onTitleTextChanged(it) },
                        dateCreated = stringResource(
                            R.string.text_date_created,
                            dateAsString(scan.dateCreated)
                        ),
                        dateModified = stringResource(
                            R.string.text_date_modified,
                            dateAsString(scan.dateModified)
                        ),
                        isCreated = state.isCreated
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
                        items(items = state.filteredTextModels, key = { it.id }) {
                            TextEntityChip(
                                entity = it,
                                onClick = { onChipClicked(it) }
                            )
                        }
                    }
                }
            }

            state.scan?.let { scan ->
                item(key = "content_field") {
                    var content by remember { mutableStateOf(scan.scanText) }
                    ScannerateTextField(
                        modifier = Modifier.fillMaxWidth(),
                        text = content,
                        onTextChanged = {
                            content = it
                            onContentChanged(it)
                        },
                        fontSize = 17.sp
                    )
                }
            }

            item(key = "bottom_spacer") {
                Spacer(modifier = Modifier.height(82.dp))
            }
        }

        ScanDetailTopBar(
            height = topBarHeight,
            topBarState = topBarState,
            modifier = Modifier
                .align(Alignment.TopEnd),
            isPinned = state.scan?.isPinned ?: false,
            onPinClicked = onPinClicked,
            onBackClicked = onBackClick,
            onPdfExportClicked = onPdfExport,
            onDeleteClicked = onDeleteClick,
            onSaveClicked = { } //can do nothing
        )

        ScanDetailBottomBar(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding(),
            onCopyClick = onCopyClick,
            onShareClick = onShareClick,
            onTosClick = onTtsClick,
            onTranslateClick = onTranslateClick
        )
    }
}
