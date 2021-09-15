package com.sary.task.store.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.insets.navigationBarsHeight
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.insets.statusBarsHeight
import com.sary.task.R
import com.sary.task.banner.*
import com.sary.task.databinding.BannerSlideBinding
import com.sary.task.store.data.model.CatalogSection
import com.sary.task.store.data.model.SectionItem
import com.sary.task.theme.AppTheme
import com.sary.task.theme.cardBackground
import com.sary.task.theme.snackBarAction
import com.sary.task.util.NetworkImage
import com.sary.task.util.Response
import kotlinx.coroutines.launch

@Composable
fun Store(
    modifier: Modifier = Modifier,
    viewModel: StoreViewModel = hiltViewModel(),
    onCatalogSectionItemSelected: (SectionItem) -> Unit
) {
    LaunchedEffect(Unit) {
        viewModel.getBannerItems()
        viewModel.getCatalog()
    }

    val snackBarHostState = rememberSnackBarHostState()
    val catalogErrorRetryAction = {
        viewModel.getCatalog()
        snackBarHostState.currentSnackbarData?.dismiss()
        Unit
    }

    LazyColumn(modifier) {
        item {
            Spacer(Modifier.statusBarsHeight())
        }
        item {
            Card(
                modifier = Modifier
                    .padding(16.dp)
                    .height(172.dp),
                shape = AppTheme.shapes.medium.copy(all = CornerSize(38.dp)),
                backgroundColor = AppTheme.colors.cardBackground,
                elevation = AppTheme.elevations.card
            ) {
                Banner(viewModel)
            }
        }
        item {
            Catalog(snackBarHostState, viewModel, onCatalogSectionItemSelected)
            Spacer(modifier = Modifier.navigationBarsHeight(additional = 56.dp))
        }
    }

    Box(modifier = Modifier
        .fillMaxSize()
        .navigationBarsPadding()
        .padding(bottom = 56.dp)
    ) {
        ErrorSnackBar(
            hostState = snackBarHostState,
            onAction = catalogErrorRetryAction,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
private fun Banner(viewModel: StoreViewModel) {
    val context = LocalContext.current
    val state = viewModel.bannerItems.observeAsState()
    val bannerView = rememberBannerView()

    AndroidView({ bannerView }) { banner ->
        when (val response = state.value) {
            is Response.Loading -> {
                banner.showProgress()
            }
            is Response.Success -> {
                val items = response.data.result
                val slides = List(items.size) { i ->
                    Slide(
                        imageUrl = items[i].imageUrl,
                        onClick = {
                            Toast.makeText(context, items[i].link, Toast.LENGTH_SHORT).show()
                        }
                    )
                }

                banner.hideProgress()
                banner.setSlides(
                    slides = slides,
                    onSlideView = { inflater, viewGroup ->
                        BannerItemView(context, inflater, viewGroup)
                    }
                )
            }
            is Response.Error -> {
                banner.hideProgress()
                banner.setError(
                    message = response.message,
                    onRetryClick = { viewModel.getBannerItems() }
                )
            }
            is Response.Empty -> {
                banner.hideProgress()
                // We could create an empty state view that has an image and a tagline,
                // but for now we show nothing.
                val emptyStateView: View? = null
                banner.showEmptyState(view = emptyStateView)
            }
        }
    }
}

@Composable
private fun Catalog(
    snackBarHostState: SnackbarHostState,
    viewModel: StoreViewModel,
    onSectionItemSelected: (SectionItem) -> Unit
) {
    val scope = rememberCoroutineScope()

    viewModel.catalog.observeAsState().value?.let {
        when (it) {
            is Response.Loading -> {
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = AppTheme.colors.secondary
                    )
                }
            }
            is Response.Success -> {
                val sections = it.data.result
                sections.forEach { section ->
                    CatalogSection(section = section, onSectionItemSelected = onSectionItemSelected)
                }
            }
            is Response.Error -> {
                val snackBarActionLabel = stringResource(id = R.string.retry)

                it.message?.let { message ->
                    scope.launch {
                        snackBarHostState.showSnackbar(
                            message = "Error Showing Catalog: $message",
                            actionLabel = snackBarActionLabel,
                            duration = SnackbarDuration.Indefinite
                        )
                    }
                }
            }
            is Response.Empty -> {
                // Show empty state for the catalog,
                // but for now we do nothing.
            }
        }
    }
}

@Composable
private fun CatalogSection(
    modifier: Modifier = Modifier,
    section: CatalogSection,
    onSectionItemSelected: (SectionItem) -> Unit = { }
) {
    val background = if (section.dataType == CatalogSection.DATA_TYPE_SMART) {
        AppTheme.colors.cardBackground
    } else {
        AppTheme.colors.background
    }
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = background
    ) {
        Column {
            if (section.showTitle) {
                CompositionLocalProvider(
                    LocalContentAlpha provides ContentAlpha.medium
                ) {
                    Text(
                        text = section.title,
                        style = AppTheme.typography.h6,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            when (section.uiType) {
                CatalogSection.UI_TYPE_GRID -> GridCatalogSection(section, onSectionItemSelected)
                CatalogSection.UI_TYPE_LINEAR -> LinearCatalogSection(section, onSectionItemSelected)
                CatalogSection.UI_TYPE_SLIDER -> SliderCatalogSection(section, onSectionItemSelected)
            }
        }
    }
    Spacer(modifier = Modifier.height(16.dp))
}

@Composable
private fun GridCatalogSection(
    section: CatalogSection,
    onSectionItemSelected: (SectionItem) -> Unit
) {
    val rowItemsCount = section.rowItemsCount

    Column(modifier = Modifier.padding(horizontal = EDGE_PADDING / 2)) {
        val rowsCount = (section.data.size + rowItemsCount - 1) / rowItemsCount
        for (i in 0 until rowsCount) {
            Row {
                for (j in 0 until rowItemsCount) {
                    val index = i * rowItemsCount + j
                    if (index >= section.data.size) {
                        Spacer(modifier = Modifier
                            .weight(1f)
                            .padding(4.dp))
                    } else {
                        SectionItem(
                            modifier = Modifier
                                .weight(1f)
                                .padding(4.dp),
                            item = section.data[index],
                            dataType = section.dataType,
                            onSectionItemSelected = onSectionItemSelected
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LinearCatalogSection(
    section: CatalogSection,
    onSectionItemSelected: (SectionItem) -> Unit
) {
    Column(
        modifier = Modifier
            .padding(horizontal = EDGE_PADDING / 2)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        section.data.forEach { item ->
            SectionItem(
                modifier = Modifier
                    .size(width = 170.dp, height = 170.dp)
                    .padding(4.dp),
                item = item,
                dataType = section.dataType,
                onSectionItemSelected = onSectionItemSelected
            )
        }
    }
}

@Composable
private fun SliderCatalogSection(
    section: CatalogSection,
    onSectionItemSelected: (SectionItem) -> Unit
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = EDGE_PADDING / 2)
    ) {
        items(section.data) { item ->
            SectionItem(
                modifier = Modifier
                    .size(width = 150.dp, height = 150.dp)
                    .padding(4.dp),
                item = item,
                dataType = section.dataType,
                onSectionItemSelected = onSectionItemSelected
            )
        }
    }
}

@Composable
private fun SectionItem(
    modifier: Modifier = Modifier,
    item: SectionItem,
    dataType: String,
    onSectionItemSelected: (SectionItem) -> Unit
) {
    when (dataType) {
        CatalogSection.DATA_TYPE_SMART -> SmartSectionItem(modifier, item, onSectionItemSelected)
        CatalogSection.DATA_TYPE_GROUP -> GroupSectionItem(modifier, item, onSectionItemSelected)
        CatalogSection.DATA_TYPE_BANNER -> BannerSectionItem(modifier, item, onSectionItemSelected)
    }
}

@Composable
private fun SmartSectionItem(
    modifier: Modifier = Modifier,
    item: SectionItem,
    onSectionItemSelected: (SectionItem) -> Unit = { }
) {
    Column(modifier = modifier.clickable(onClick = { onSectionItemSelected(item) })) {
        Box(
            modifier = Modifier
                .background(
                    color = AppTheme.colors.surface,
                    shape = CircleShape
                )
                .border(
                    width = 1.dp,
                    color = Color.DarkGray.copy(alpha = 0.4f),
                    shape = CircleShape
                )
                .align(alignment = Alignment.CenterHorizontally)
        ) {
            NetworkImage(
                url = item.imageUrl,
                contentDescription = null,
                modifier = modifier
                    .size(64.dp)
                    .padding(16.dp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
            Text(
                text = item.name ?: "",
                style = AppTheme.typography.subtitle2,
                textAlign = TextAlign.Center,
                modifier = Modifier.align(alignment = Alignment.CenterHorizontally)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
private fun GroupSectionItem(
    modifier: Modifier = Modifier,
    item: SectionItem,
    onSectionItemSelected: (SectionItem) -> Unit = { }
) {
    NetworkImage(
        url = item.imageUrl,
        contentDescription = null,
        modifier = modifier.clickable(onClick = { onSectionItemSelected(item) })
    )
}

@Composable
private fun BannerSectionItem(
    modifier: Modifier = Modifier,
    item: SectionItem,
    onSectionItemSelected: (SectionItem) -> Unit = { }
) {
    NetworkImage(
        url = item.imageUrl,
        contentDescription = null,
        modifier = modifier.clickable(onClick = { onSectionItemSelected(item) })
    )
}

@Composable
private fun ErrorSnackBar(
    hostState: SnackbarHostState,
    modifier: Modifier = Modifier,
    onAction: () -> Unit = { }
) {
    SnackbarHost(
        hostState = hostState,
        snackbar = { data ->
            Snackbar(
                modifier = Modifier.padding(16.dp),
                content = {
                    Text(
                        text = data.message,
                        style = AppTheme.typography.body2
                    )
                },
                action = {
                    data.actionLabel?.let { actionLabel ->
                        TextButton(onClick = onAction) {
                            Text(
                                text = actionLabel,
                                color = AppTheme.colors.snackBarAction
                            )
                        }
                    }
                }
            )
        },
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight(Alignment.Bottom)
    )
}

@Composable
private fun rememberSnackBarHostState(): SnackbarHostState = remember { SnackbarHostState() }

@Composable
private fun rememberBannerView(): BannerView {
    val context = LocalContext.current
    return remember {
        BannerView(context).apply {
            id = R.id.banner
        }
    }
}

private class BannerItemView(
    private val context: Context,
    layoutInflater: LayoutInflater,
    container: ViewGroup?
) : SlideView() {
    private val binding = BannerSlideBinding.inflate(layoutInflater, container, false)

    override val view: View get() = binding.root

    override fun bind(position: Int, slide: Slide) = with(binding) {
        root.setOnClickListener { slide.onClick() }
        slideImage.loadImage(
            context = context, imageUrl = slide.imageUrl, listener = ImageLoadingListener(progress)
        )
    }
}

private val EDGE_PADDING = 24.dp

@Preview(name = "Grid - Smart Catalog Section", widthDp = 480, heightDp = 150)
@Composable
private fun GridSmartCatalogSectionPreview() {
    AppTheme {
        val section = CatalogSection(
            id = 3,
            title = "",
            data = List(4) { i -> SectionItem(name = "Offers", imageUrl = "https://$i") },
            showTitle = false,
            dataType = CatalogSection.DATA_TYPE_SMART,
            uiType = CatalogSection.UI_TYPE_GRID,
            rowItemsCount = 4
        )
        CatalogSection(section = section)
    }
}

@Preview(name = "Grid - Smart Catalog Section - Dark Theme", widthDp = 480, heightDp = 150)
@Composable
private fun GridSmartCatalogSectionPreviewDark() {
    AppTheme(darkTheme = true) {
        val section = CatalogSection(
            id = 3,
            title = "",
            data = List(4) { i -> SectionItem(name = "Offers", imageUrl = "https://$i") },
            showTitle = false,
            dataType = CatalogSection.DATA_TYPE_SMART,
            uiType = CatalogSection.UI_TYPE_GRID,
            rowItemsCount = 4
        )
        CatalogSection(section = section)
    }
}

@Preview(name = "Linear - Catalog Section", widthDp = 480, heightDp = 600)
@Composable
private fun LinearCatalogSectionPreview() {
    AppTheme {
        val section = CatalogSection(
            id = 171,
            title = "Partners",
            data = List(5) { i -> SectionItem(imageUrl = "https://$i") },
            showTitle = true,
            dataType = CatalogSection.DATA_TYPE_GROUP,
            uiType = CatalogSection.UI_TYPE_LINEAR,
            rowItemsCount = 4
        )
        LazyColumn {
            item { CatalogSection(section = section) }
        }
    }
}

@Preview(name = "Linear - Catalog Section - Dark Theme", widthDp = 480, heightDp = 600)
@Composable
private fun LinearCatalogSectionPreviewDark() {
    AppTheme(darkTheme = true) {
        val section = CatalogSection(
            id = 171,
            title = "Partners",
            data = List(5) { i -> SectionItem(imageUrl = "https://$i") },
            showTitle = true,
            dataType = CatalogSection.DATA_TYPE_GROUP,
            uiType = CatalogSection.UI_TYPE_LINEAR,
            rowItemsCount = 4
        )
        LazyColumn {
            item { CatalogSection(section = section) }
        }
    }
}

@Preview(name = "Grid - 2 Columns - Catalog Section", widthDp = 480, heightDp = 1120)
@Composable
private fun GridTwoColumnsCatalogSectionPreview() {
    AppTheme {
        val section = CatalogSection(
            id = 147,
            title = "Categories",
            data = List(8) { i -> SectionItem(imageUrl = "https://$i") },
            showTitle = true,
            dataType = CatalogSection.DATA_TYPE_BANNER,
            uiType = CatalogSection.UI_TYPE_GRID,
            rowItemsCount = 2
        )
        LazyColumn {
            item { CatalogSection(section = section) }
        }
    }
}

@Preview(name = "Grid - 2 Columns - Catalog Section - Dark Theme", widthDp = 480, heightDp = 1120)
@Composable
private fun GridTwoColumnsCatalogSectionPreviewDark() {
    AppTheme(darkTheme = true) {
        val section = CatalogSection(
            id = 147,
            title = "Categories",
            data = List(8) { i -> SectionItem(imageUrl = "https://$i") },
            showTitle = true,
            dataType = CatalogSection.DATA_TYPE_BANNER,
            uiType = CatalogSection.UI_TYPE_GRID,
            rowItemsCount = 2
        )
        LazyColumn {
            item { CatalogSection(section = section) }
        }
    }
}

@Preview(name = "Grid - 4 Columns - Catalog Section", widthDp = 480, heightDp = 420)
@Composable
private fun GridFourColumnsCatalogSectionPreview() {
    AppTheme {
        val section = CatalogSection(
            id = 148,
            title = "",
            data = List(11) { i -> SectionItem(imageUrl = "https://$i") },
            showTitle = false,
            dataType = CatalogSection.DATA_TYPE_BANNER,
            uiType = CatalogSection.UI_TYPE_GRID,
            rowItemsCount = 4
        )
        LazyColumn {
            item { CatalogSection(section = section) }
        }
    }
}

@Preview(name = "Grid - 4 Columns - Catalog Section - Dark Theme", widthDp = 480, heightDp = 420)
@Composable
private fun GridFourColumnsCatalogSectionPreviewDark() {
    AppTheme(darkTheme = true) {
        val section = CatalogSection(
            id = 148,
            title = "",
            data = List(11) { i -> SectionItem(imageUrl = "https://$i") },
            showTitle = false,
            dataType = CatalogSection.DATA_TYPE_BANNER,
            uiType = CatalogSection.UI_TYPE_GRID,
            rowItemsCount = 4
        )
        LazyColumn {
            item { CatalogSection(section = section) }
        }
    }
}

@Preview(name = "Grid - 3 Columns - Catalog Section", widthDp = 480, heightDp = 400)
@Composable
private fun GridThreeColumnsCatalogSectionPreview() {
    AppTheme {
        val section = CatalogSection(
            id = 13,
            title = "By Business Type",
            data = List(5) { i -> SectionItem(imageUrl = "https://$i") },
            showTitle = true,
            dataType = CatalogSection.DATA_TYPE_GROUP,
            uiType = CatalogSection.UI_TYPE_GRID,
            rowItemsCount = 3
        )
        LazyColumn {
            item { CatalogSection(section = section) }
        }
    }
}

@Preview(name = "Grid - 3 Columns - Catalog Section - Dark Theme", widthDp = 480, heightDp = 400)
@Composable
private fun GridThreeColumnsCatalogSectionPreviewDark() {
    AppTheme(darkTheme = true) {
        val section = CatalogSection(
            id = 13,
            title = "By Business Type",
            data = List(5) { i -> SectionItem(imageUrl = "https://$i") },
            showTitle = true,
            dataType = CatalogSection.DATA_TYPE_GROUP,
            uiType = CatalogSection.UI_TYPE_GRID,
            rowItemsCount = 3
        )
        LazyColumn {
            item { CatalogSection(section = section) }
        }
    }
}

@Preview(name = "Slider - Catalog Section", widthDp = 520, heightDp = 220)
@Composable
private fun SliderCatalogSectionPreview() {
    AppTheme {
        val section = CatalogSection(
            id = 1,
            title = "Categories",
            data = List(10) { i -> SectionItem(imageUrl = "https://$i") },
            showTitle = true,
            dataType = CatalogSection.DATA_TYPE_GROUP,
            uiType = CatalogSection.UI_TYPE_SLIDER,
            rowItemsCount = 2
        )
        CatalogSection(section = section)
    }
}

@Preview(name = "Slider - Catalog Section - Dark Theme", widthDp = 520, heightDp = 220)
@Composable
private fun SliderCatalogSectionPreviewDark() {
    AppTheme(darkTheme = true) {
        val section = CatalogSection(
            id = 1,
            title = "Categories",
            data = List(10) { i -> SectionItem(imageUrl = "https://$i") },
            showTitle = true,
            dataType = CatalogSection.DATA_TYPE_GROUP,
            uiType = CatalogSection.UI_TYPE_SLIDER,
            rowItemsCount = 2
        )
        CatalogSection(section = section)
    }
}

@Preview(name = "Smart - Section Item")
@Composable
private fun SmartSectionItemPreview() {
    AppTheme {
        SmartSectionItem(
            item = SectionItem(name = "Favorites", imageUrl = "https://...")
        )
    }
}

@Preview(name = "Smart - Section Item - Dark Theme")
@Composable
private fun SmartSectionItemPreviewDark() {
    AppTheme(darkTheme = true) {
        SmartSectionItem(
            item = SectionItem(name = "Favorites", imageUrl = "https://...")
        )
    }
}

@Preview(name = "Banner - Section Item")
@Composable
private fun BannerSectionItemPreview() {
    AppTheme {
        BannerSectionItem(
            modifier = Modifier.size(100.dp),
            item = SectionItem(imageUrl = "https://...")
        )
    }
}

@Preview(name = "Group - Section Item")
@Composable
private fun GroupSectionItemPreview() {
    AppTheme {
        GroupSectionItem(
            modifier = Modifier.size(width = 120.dp, height = 60.dp),
            item = SectionItem(imageUrl = "https://...")
        )
    }
}