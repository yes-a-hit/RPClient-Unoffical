package me.kafuuneko.rpclient.feature.groupchat

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
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
        // 走系统文件/相册选择器而不是 Photo Picker：
        // Photo Picker 只给一条照片流，用户没法点进「相册」目录挑图，
        // 也没法选 GIF —— 只能一张张划。这里换成 OpenMultipleDocuments，
        // 系统会给出完整的相册/文件夹导航。
        ActivityResultContracts.OpenMultipleDocuments()
    ) { uris -> mViewModel.emit(GroupChatUiIntent.ImageAction(MessageImageAction.Picked(uris))) }
    private val mImageSaver = registerForActivityResult(
        CreateImageDocumentContract()
    ) { uri -> uri?.let { mViewModel.emit(GroupChatUiIntent.ImageAction(MessageImageAction.SaveResult(it))) } }
    private val mFilePicker = registerForActivityResult(
        ActivityResultContracts.OpenMultipleDocuments()
    ) { uris -> if (uris.isNotEmpty()) mViewModel.emit(GroupChatUiIntent.FilesPicked(uris)) }


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
            GroupChatViewEvent.PickImages -> mImagePicker.launch(arrayOf("image/*"))
            GroupChatViewEvent.OpenFilePicker -> mFilePicker.launch(arrayOf("*/*"))
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
    }
}
