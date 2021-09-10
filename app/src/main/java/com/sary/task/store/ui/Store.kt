package com.sary.task.store.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.insets.navigationBarsHeight
import com.google.accompanist.insets.statusBarsHeight
import com.sary.task.R
import com.sary.task.banner.BannerView
import com.sary.task.banner.ImageLoadingListener
import com.sary.task.banner.Slide
import com.sary.task.banner.SlideView
import com.sary.task.databinding.BannerSlideBinding
import com.sary.task.loadImage
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
    viewModel: StoreViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        viewModel.getBannerItems()
        viewModel.getCatalog()
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
            Catalog(viewModel)
            Spacer(modifier = Modifier.navigationBarsHeight(additional = 56.dp))
        }
    }
}

@Composable
private fun Banner(viewModel: StoreViewModel) {
    val context = LocalContext.current
    val state = viewModel.bannerItems.observeAsState()
    val bannerView = rememberBannerView()

    when (val response = state.value) {
        is Response.Success -> {
            AndroidView({ bannerView }) { banner ->
                val items = response.data.result
                val slides = List(items.size) { i ->
                    Slide(
                        imageUrl = items[i].imageUrl,
                        onClick = {
                            Toast.makeText(context, items[i].link, Toast.LENGTH_SHORT).show()
                        }
                    )
                }
                banner.setSlides(
                    slides = slides,
                    onSlideView = { inflater, viewGroup ->
                        BannerItemView(context, inflater, viewGroup)
                    }
                )
            }
        }
        is Response.Error -> {
            AndroidView({ bannerView }) { banner ->
                banner.setError(
                    message = response.message,
                    onRetryClick = { viewModel.getBannerItems() }
                )
            }
        }
        is Response.Empty -> {
            AndroidView({ bannerView }) { banner ->
                // We could create an empty state view that has an image and a tagline,
                // but for now we show nothing.
                val emptyStateView: View? = null
                banner.showEmptyState(view = emptyStateView)
            }
        }
    }
}

@Composable
private fun Catalog(viewModel: StoreViewModel) {
    val snackBarHostState = rememberSnackBarHostState()
    val scope = rememberCoroutineScope()

    viewModel.catalog.observeAsState().value?.let {
        when (it) {
            is Response.Success -> {
                val sections = it.data.result
                sections.forEach { section -> CatalogSection(section) }
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

    Box(modifier = Modifier.fillMaxSize()) {
        ErrorSnackBar(
            hostState = snackBarHostState,
            onAction = {
                viewModel.getCatalog()
                snackBarHostState.currentSnackbarData?.dismiss()
            },
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
private fun CatalogSection(section: CatalogSection) {
    val background = if (section.dataType == CatalogSection.DATA_TYPE_SMART) {
        AppTheme.colors.cardBackground
    } else {
        AppTheme.colors.background
    }
    Surface(
        modifier = Modifier.fillMaxWidth(),
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
                CatalogSection.UI_TYPE_GRID -> GridCatalogSection(section)
                CatalogSection.UI_TYPE_LINEAR -> LinearCatalogSection(section)
                CatalogSection.UI_TYPE_SLIDER -> SliderCatalogSection(section)
            }
        }
    }
    Spacer(modifier = Modifier.height(16.dp))
}

@Composable
private fun GridCatalogSection(section: CatalogSection) {
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
                             dataType = section.dataType
                         )
                     }
                 }
             }
         }
     }
}

@Composable
private fun LinearCatalogSection(section: CatalogSection) {
    BoxWithConstraints {
        val itemWidth = (constraints.maxWidth / 2).dp
        Column(
            modifier = Modifier.padding(horizontal = EDGE_PADDING / 2),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            section.data.forEach { item ->
                SectionItem(
                    modifier = Modifier
                        .size(width = itemWidth, height = Dp.Unspecified)
                        .padding(4.dp),
                    item = item,
                    dataType = section.dataType
                )
            }
        }
    }
}

@Composable
private fun SliderCatalogSection(section: CatalogSection) {
    BoxWithConstraints {
        val edgePadding = with(LocalDensity.current) { EDGE_PADDING.toPx() }
        val itemWidth = ((constraints.maxWidth - edgePadding) / 3).dp
        LazyRow(
            contentPadding = PaddingValues(horizontal = EDGE_PADDING / 2)
        ) {
            items(section.data) { item ->
                SectionItem(
                    modifier = Modifier
                        .size(width = itemWidth, height = Dp.Unspecified)
                        .padding(4.dp),
                    item = item,
                    dataType = section.dataType
                )
            }
        }
    }
}

@Composable
private fun SectionItem(
    modifier: Modifier = Modifier,
    item: SectionItem,
    dataType: String
) {
    when (dataType) {
        CatalogSection.DATA_TYPE_SMART -> SmartSectionItem(modifier, item)
        CatalogSection.DATA_TYPE_GROUP -> GroupSectionItem(modifier, item)
        CatalogSection.DATA_TYPE_BANNER -> BannerSectionItem(modifier, item)
    }
}

@Composable
private fun SmartSectionItem(
    modifier: Modifier = Modifier,
    item: SectionItem
) {
    Column(modifier = modifier) {
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
                    .size(75.dp)
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
    }
}

@Composable
private fun GroupSectionItem(
    modifier: Modifier = Modifier,
    item: SectionItem
) {
    NetworkImage(
        url = item.imageUrl,
        contentDescription = null,
        modifier = modifier
    )
}

@Composable
private fun BannerSectionItem(
    modifier: Modifier = Modifier,
    item: SectionItem
) {
    NetworkImage(
        url = item.imageUrl,
        contentDescription = null,
        modifier = modifier
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