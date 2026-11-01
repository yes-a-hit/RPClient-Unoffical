package me.kafuuneko.rpclient.feature.chat

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
        // 与群聊保持一致：用系统文件/相册选择器，能进相册目录、能选 GIF。
        // Photo Picker 只给一条照片流，选图只能一张张划。
        ActivityResultContracts.OpenMultipleDocuments()
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
            ChatViewEvent.PickImages -> mImagePicker.launch(arrayOf("image/*"))

            ChatViewEvent.OpenFilePicker -> mFilePicker.launch(arrayOf("*/*"))

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
    }
}
