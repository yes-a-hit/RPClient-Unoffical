package me.kafuuneko.rpclient.feature.groupchat

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import me.kafuuneko.rpclient.R
import me.kafuuneko.rpclient.feature.groupchat.presentation.GroupChatUiIntent
import me.kafuuneko.rpclient.feature.groupchat.presentation.GroupChatUiState
import me.kafuuneko.rpclient.feature.groupchat.presentation.GroupChatViewEvent
import me.kafuuneko.rpclient.feature.groupchat.ui.GroupChatLayout
import me.kafuuneko.rpclient.libs.media.CreateImageDocumentContract
import me.kafuuneko.rpclient.libs.media.MessageImageAction
import me.kafuuneko.rpclient.libs.core.CoreActivityWithEvent
import me.kafuuneko.rpclient.libs.core.IViewEvent

/** 群聊页面宿主，绑定群聊会话 ID 与 MVI 事件流。 */
class GroupChatActivity : CoreActivityWithEvent() {
    private val mViewModel by viewModels<GroupChatViewModel>()
    private val mImagePicker = registerForActivityResult(
        // 走系统 Photo Picker：一进来就是照片流，顶部能切相册。之前为拿到完整目录树
        // 换成了 OpenMultipleDocuments，代价是丢了照片流的快速预览；按目录挑文件走「附加文件」。
        ActivityResultContracts.PickMultipleVisualMedia(MaxPickedImages)
    ) { uris -> mViewModel.emit(GroupChatUiIntent.ImageAction(MessageImageAction.Picked(uris))) }
    private val mImageSaver = registerForActivityResult(
        CreateImageDocumentContract()
    ) { uri -> uri?.let { mViewModel.emit(GroupChatUiIntent.ImageAction(MessageImageAction.SaveResult(it))) } }
    private val mFilePicker = registerForActivityResult(
        ActivityResultContracts.OpenMultipleDocuments()
    ) { uris -> if (uris.isNotEmpty()) mViewModel.emit(GroupChatUiIntent.FilesPicked(uris)) }

    /** 会话级背景只挑一张，走单选取图。 */
    private val mBackgroundPicker = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri -> uri?.let { mViewModel.emit(GroupChatUiIntent.SessionBackgroundPicked(it)) } }

    /** 场景背景同样是单张，但回填给正在编辑的场景草稿。 */
    private val mSceneBackgroundPicker = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri -> uri?.let { mViewModel.emit(GroupChatUiIntent.SceneBackgroundPicked(it)) } }


    override fun getViewEventFlow() = mViewModel.viewEventFlow

    @Composable
    override fun ViewContent() {
        val uiState by mViewModel.uiStateFlow.collectAsState()
        LaunchedEffect(uiState) {
            if (uiState is GroupChatUiState.Finished) finish()
        }
        GroupChatLayout(
            uiState = uiState,
            emitIntent = mViewModel::emit
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewModel.emit(
            GroupChatUiIntent.Init(
                sessionId = intent.getStringExtra(EXTRA_SESSION_ID)
            )
        )
    }

    override fun onResume() {
        super.onResume()
        mViewModel.emit(GroupChatUiIntent.Resume)
    }

    /**
     * 分发图片选择、保存及其他页面宿主事件。
     *
     * @param viewEvent ViewModel 已准备好参数的一次性系统操作。
     */
    override suspend fun onReceivedViewEvent(viewEvent: IViewEvent) {
        when (viewEvent) {
            GroupChatViewEvent.PickImages ->
                mImagePicker.launch(PickVisualMediaRequest(PickVisualMedia.ImageOnly))
            GroupChatViewEvent.OpenFilePicker -> mFilePicker.launch(arrayOf("*/*"))
            GroupChatViewEvent.PickBackgroundImage -> mBackgroundPicker.launch("image/*")
            GroupChatViewEvent.PickSceneBackgroundImage -> mSceneBackgroundPicker.launch("image/*")
            is GroupChatViewEvent.SaveImage -> mImageSaver.launch(viewEvent.metadata)
            is GroupChatViewEvent.CopyText -> copyText(viewEvent.text)
            else -> super.onReceivedViewEvent(viewEvent)
        }
    }

    private fun copyText(text: String) {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.setPrimaryClip(ClipData.newPlainText(getString(R.string.message), text))
        Toast.makeText(this, R.string.copied_to_clipboard, Toast.LENGTH_SHORT).show()
    }

    companion object {
        const val EXTRA_SESSION_ID = "extra_group_chat_session_id"

        /** 一次最多挑几张；与 MessageImagePolicy.MAX_IMAGES_PER_MESSAGE 对齐。 */
        const val MaxPickedImages = 4
    }
}
