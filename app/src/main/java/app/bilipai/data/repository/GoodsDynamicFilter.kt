package app.bilipai.data.repository

import app.bilipai.data.model.response.DynamicItem

internal fun isGoodsDynamic(item: DynamicItem): Boolean =
    item.modules.module_dynamic?.additional?.type == "ADDITIONAL_TYPE_GOODS" ||
        item.orig?.let(::isGoodsDynamic) == true

internal fun isGoodsSpaceDynamic(item: app.bilipai.data.model.response.SpaceDynamicItem): Boolean =
    item.modules.module_dynamic?.additional?.type == "ADDITIONAL_TYPE_GOODS" ||
        item.orig?.let(::isGoodsSpaceDynamic) == true

internal suspend fun goodsDynamicFilterEnabled(): Boolean =
    app.bilipai.core.network.NetworkModule.appContext?.let {
        app.bilipai.core.store.ContentDisplaySettings.enabled(
            it, app.bilipai.core.store.ContentDisplaySettings.BLOCK_GOODS_DYNAMIC
        )
    } ?: false

internal suspend fun filterGoodsSpaceDynamics(
    items: List<app.bilipai.data.model.response.SpaceDynamicItem>,
): List<app.bilipai.data.model.response.SpaceDynamicItem> =
    if (goodsDynamicFilterEnabled()) items.filterNot(::isGoodsSpaceDynamic) else items
