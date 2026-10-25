package me.kafuuneko.rpclient.feature.gallery.presentation

import me.kafuuneko.rpclient.libs.gallery.GalleryImage

/** 图库页状态。 */
sealed class GalleryUiState {
    /** 加载中。 */
    data object Loading : GalleryUiState()

    /** 正常态。 */
    data class Ready(
        /** 全部图片，按加入时间倒序。 */
        val images: List<GalleryImage> = emptyList(),
        /** 沙箱外的公开图数量，用于顶部统计。 */
        val publicCount: Int = 0,
        /** 正在编辑哪张图的标签；null 表示未展开编辑器。 */
        val editingId: String? = null,
        /** 标签编辑草稿。 */
        val labelDraft: String = "",
        /** 角色绑定草稿。 */
        val characterDraft: String = "",
        /** 是否设为公开。 */
        val publicDraft: Boolean = true,
        /** 一句话说明草稿。 */
        val descriptionDraft: String = "",
        /** 相册名草稿。 */
        val albumDraft: String = "",
        /** 已存在的相册名，按图片数量排序。 */
        val albums: List<String> = emptyList(),
        /**
         * 当前筛选的相册。
         *
         * null 表示全部；空串表示只看未分类——这两者必须分开，
         * 否则「未分类」这个真实的分组永远点不到。
         */
        val selectedAlbum: String? = null,
        /** 按当前相册筛选后用于展示的图片。 */
        val visibleImages: List<GalleryImage> = images
    ) : GalleryUiState() {
        /** 空图库。 */
        val isEmpty: Boolean get() = images.isEmpty()

        /** 筛选后为空但图库里确实有图。 */
        val isFilteredEmpty: Boolean get() = !isEmpty && visibleImages.isEmpty()
    }
}

/** 图库页意图。 */
sealed class GalleryUiIntent {
    /** 初始化。 */
    data object Init : GalleryUiIntent()

    /** 选择图片导入。 */
    data object ImportClick : GalleryUiIntent()

    /** 已选中的图片 URI。 */
    data class ImagesPicked(val uris: List<android.net.Uri>) : GalleryUiIntent()

    /** 打开某张图的标签编辑器。 */
    data class EditLabels(val id: String) : GalleryUiIntent()

    /** 关闭编辑器。 */
    data object CloseEditor : GalleryUiIntent()

    /** 标签草稿变化，逗号或顿号分隔。 */
    data class LabelDraftChanged(val value: String) : GalleryUiIntent()

    /** 角色绑定变化。 */
    data class CharacterDraftChanged(val value: String) : GalleryUiIntent()

    /** 说明变化。 */
    data class DescriptionDraftChanged(val value: String) : GalleryUiIntent()

    /** 公开开关。 */
    data class PublicDraftChanged(val value: Boolean) : GalleryUiIntent()

    /** 保存当前编辑。 */
    data object SaveEdit : GalleryUiIntent()

    /** 切换相册筛选；null 为全部，空串为未分类。 */
    data class SelectAlbum(val album: String?) : GalleryUiIntent()

    /** 相册名草稿变化。 */
    data class AlbumDraftChanged(val value: String) : GalleryUiIntent()

    /** 删除一张图。 */
    data class Delete(val id: String) : GalleryUiIntent()

    /** 返回。 */
    data object Back : GalleryUiIntent()
}

/** 图库页事件。 */
sealed class GalleryViewEvent : me.kafuuneko.rpclient.libs.core.IViewEvent {
    /** 轻提示。 */
    data class Toast(val message: String) : GalleryViewEvent()

    /** 打开系统文件选择器。 */
    data object PickImages : GalleryViewEvent()

    /** 退出页面。 */
    data object Finish : GalleryViewEvent()
}
