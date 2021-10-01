package com.sary.task.store.ui

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.sary.task.store.data.model.CatalogSection
import com.sary.task.store.data.model.SectionItem

class GridSmartPreviewParameterProvider : PreviewParameterProvider<CatalogSection> {
    override val values = sequenceOf(
        CatalogSection(
            id = 3,
            title = "",
            data = List(4) { i -> SectionItem(name = "Offers", imageUrl = "https://$i") },
            showTitle = false,
            dataType = CatalogSection.DATA_TYPE_SMART,
            uiType = CatalogSection.UI_TYPE_GRID,
            rowItemsCount = 4
        )
    )
}

class LinearPreviewParameterProvider : PreviewParameterProvider<CatalogSection> {
    override val values = sequenceOf(
        CatalogSection(
            id = 171,
            title = "Partners",
            data = List(5) { i -> SectionItem(imageUrl = "https://$i") },
            showTitle = true,
            dataType = CatalogSection.DATA_TYPE_GROUP,
            uiType = CatalogSection.UI_TYPE_LINEAR,
            rowItemsCount = 4
        )
    )
}

class GridTwoColumnsPreviewParameterProvider : PreviewParameterProvider<CatalogSection> {
    override val values = sequenceOf(
        CatalogSection(
            id = 147,
            title = "Categories",
            data = List(8) { i -> SectionItem(imageUrl = "https://$i") },
            showTitle = true,
            dataType = CatalogSection.DATA_TYPE_BANNER,
            uiType = CatalogSection.UI_TYPE_GRID,
            rowItemsCount = 2
        )
    )
}

class GridFourColumnsPreviewParameterProvider : PreviewParameterProvider<CatalogSection> {
    override val values = sequenceOf(
        CatalogSection(
            id = 148,
            title = "",
            data = List(11) { i -> SectionItem(imageUrl = "https://$i") },
            showTitle = false,
            dataType = CatalogSection.DATA_TYPE_BANNER,
            uiType = CatalogSection.UI_TYPE_GRID,
            rowItemsCount = 4
        )
    )
}

class GridThreeColumnsPreviewParameterProvider : PreviewParameterProvider<CatalogSection> {
    override val values = sequenceOf(
        CatalogSection(
            id = 13,
            title = "By Business Type",
            data = List(5) { i -> SectionItem(imageUrl = "https://$i") },
            showTitle = true,
            dataType = CatalogSection.DATA_TYPE_GROUP,
            uiType = CatalogSection.UI_TYPE_GRID,
            rowItemsCount = 3
        )
    )
}

class SliderPreviewParameterProvider : PreviewParameterProvider<CatalogSection> {
    override val values = sequenceOf(
        CatalogSection(
            id = 1,
            title = "Horizontal Scrollable List",
            data = List(10) { i -> SectionItem(imageUrl = "https://$i") },
            showTitle = true,
            dataType = CatalogSection.DATA_TYPE_GROUP,
            uiType = CatalogSection.UI_TYPE_SLIDER,
            rowItemsCount = 2
        )
    )
}