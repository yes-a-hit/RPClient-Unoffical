package me.kafuuneko.rpclient.feature.chat

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
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
import me.kafuuneko.rpclient.feature.chat.presentation.ChatUiIntent
import me.kafuuneko.rpclient.feature.chat.presentation.ChatUiState
import me.kafuuneko.rpclient.feature.chat.presentation.ChatViewEvent
import me.kafuuneko.rpclient.feature.chat.ui.ChatLayout
import me.kafuuneko.rpclient.libs.media.CreateImageDocumentContract
import me.kafuuneko.rpclient.libs.media.MessageImageAction
import me.kafuuneko.rpclient.libs.core.CoreActivityWithEvent
import me.kafuuneko.rpclient.libs.core.IViewEvent

/** 单角色聊天页面宿主，绑定会话 ID、状态流和一次性事件。 */
class ChatActivity : CoreActivityWithEvent() {
    private val mViewModel by viewModels<ChatViewModel>()
    private val mImagePicker = registerForActivityResult(
        // 走系统 Photo Picker：一进来就是照片流，顶部能切相册。之前为拿到完整目录树
        // 换成了 OpenMultipleDocuments，代价是丢了照片流的快速预览；按目录挑文件走「附加文件」。
        ActivityResultContracts.PickMultipleVisualMedia(MaxPickedImages)
    ) { uris ->
        mViewModel.emit(ChatUiIntent.ImageAction(MessageImageAction.Picked(uris)))
    }
    private val mImageSaver = registerForActivityResult(
        CreateImageDocumentContract()
    ) { uri ->
        uri?.let { mViewModel.emit(ChatUiIntent.ImageAction(MessageImageAction.SaveResult(it))) }
    }


    /** 导出目标由系统文档选择器创建，Activity 只回传 URI。 */
    private val mChatExporterLauncher = registerForActivityResult(
        ActivityResultContracts.CreateDocument("application/x-ndjson")
    ) { uri ->
        uri?.let { mViewModel.emit(ChatUiIntent.ExportChatResult(it)) }
    }

    /** 发送文件入口：系统文档选择器，任意类型，ViewModel 读取文本内容。 */
    private val mFilePicker = registerForActivityResult(
        ActivityResultContracts.OpenMultipleDocuments()
    ) { uris ->
        if (uris.isNotEmpty()) mViewModel.emit(ChatUiIntent.FilesPicked(uris))
    }

    /** 会话级背景只挑一张，走单选取图。 */
    private val mBackgroundPicker = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { mViewModel.emit(ChatUiIntent.SessionBackgroundPicked(it)) }
    }

    override fun getViewEventFlow() = mViewModel.viewEventFlow

    @Composable
    override fun ViewContent() {
        val uiState by mViewModel.uiStateFlow.collectAsState()

        LaunchedEffect(uiState) {
            if (uiState is ChatUiState.Finished) finish()
        }

        ChatLayout(
            uiState = uiState,
            emit = { mViewModel.emit(this) }
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewModel.emit(
            ChatUiIntent.Init(
                sessionId = intent.getStringExtra(EXTRA_SESSION_ID)
            )
        )
    }

    override fun onResume() {
        super.onResume()
        mViewModel.emit(ChatUiIntent.Resume)
    }

    /**
     * 分发图片选择、保存及其他页面宿主事件。
     *
     * @param viewEvent ViewModel 已准备好参数的一次性系统操作。
     */
    override suspend fun onReceivedViewEvent(viewEvent: IViewEvent) {
        // 图片保存使用本次原图的格式元数据，其他导出沿用各自的文档合约。
        when (viewEvent) {
            ChatViewEvent.PickImages ->
                mImagePicker.launch(PickVisualMediaRequest(PickVisualMedia.ImageOnly))

            ChatViewEvent.OpenFilePicker -> mFilePicker.launch(arrayOf("*/*"))

            ChatViewEvent.PickBackgroundImage -> mBackgroundPicker.launch("image/*")

            is ChatViewEvent.SaveImage -> mImageSaver.launch(viewEvent.metadata)
            is ChatViewEvent.CopyText -> copyText(viewEvent.text)
            is ChatViewEvent.OpenSession -> openSession(viewEvent.sessionId)
            is ChatViewEvent.OpenChatExporter -> {
                mChatExporterLauncher.launch(viewEvent.fileName)
            }

            else -> super.onReceivedViewEvent(viewEvent)
        }
    }

    private fun copyText(text: String) {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.setPrimaryClip(ClipData.newPlainText(getString(R.string.message), text))
        Toast.makeText(this, R.string.copied_to_clipboard, Toast.LENGTH_SHORT).show()
    }

    private fun openSession(sessionId: String) {
        startActivity(
            Intent(this, ChatActivity::class.java).apply {
                putExtra(EXTRA_SESSION_ID, sessionId)
            }
        )
        finish()
    }

    companion object {
        const val EXTRA_SESSION_ID = "extra_session_id"

        /** 一次最多挑几张；与 MessageImagePolicy.MAX_IMAGES_PER_MESSAGE 对齐。 */
        const val MaxPickedImages = 4
    }
}
