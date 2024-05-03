package com.hyperring.core
import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.CalendarContract.Colors
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.hyperring.core.ui.theme.HyperRingCoreTheme
import com.hyperring.sdk.core.nfc.HyperRingData
import com.hyperring.sdk.core.nfc.HyperRingTag
import com.hyperring.sdk.core.nfc.HyperRingNFC
import com.hyperring.sdk.core.nfc.NFCStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Demo Application
 */
class MainActivity : ComponentActivity() {
    private lateinit var mainViewModel : MainViewModel

    companion object {
        var mainActivity: ComponentActivity? = null
    }

    override fun onResume() {
        mainActivity = this
        super.onResume()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainActivity = this
        mainViewModel = ViewModelProvider(this)[MainViewModel::class.java]
        lifecycleScope.launch {
            // If application is started, init nfc status
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                mainViewModel.initNFCStatus(this@MainActivity)
            }
        }

        setContent {
            HyperRingCoreTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column {
                        NFCBox(context = LocalContext.current, viewModel = mainViewModel)
                        MFABox()
                    }
                }
            }
        }
    }
}

@Composable
fun MFABox(modifier: Modifier = Modifier) {
    Column(modifier = modifier.padding(10.dp)) {
        Box(modifier = modifier
            .background(Color.LightGray)
            .padding(10.dp)
            .fillMaxWidth()
            .height((180.dp))) {
            Column(
                modifier = modifier
                    .align(Alignment.TopCenter)
            ) {
                Box(modifier = modifier
                    .fillMaxWidth()
                    .height(40.dp)
                ) {
                    Text(
                        text = "MFA",
                        modifier = modifier.fillMaxWidth(),
                        style = TextStyle(fontSize = 22.sp),
                        textAlign = TextAlign.Center,
                    )
                }
                Box(modifier = modifier
                    .background(Color.LightGray)
                    .padding(10.dp)
                    .fillMaxWidth()
                    .height(80.dp)
                ) {
                    Text(
                        text = "[Init] Registered Service publicKey: [dfssafj111f13fa1]",
                        modifier = modifier.fillMaxWidth(),
                        style = TextStyle(fontSize = 22.sp),
                        textAlign = TextAlign.Center,
                    )
                }
                FilledTonalButton(
                    modifier = modifier.fillMaxWidth(),
                    onClick = {
//                    onClick()
                    }) {
                    Text("Open requestAuthPage()", textAlign = TextAlign.Center)
                }
            }
        }
    }
}

@Composable
fun NFCBox(context: Context, modifier: Modifier = Modifier, viewModel: MainViewModel) {
    Column(modifier = modifier.padding(10.dp)) {
        Box(modifier = modifier
            .background(Color.LightGray)
            .padding(10.dp)
            .fillMaxWidth()
            .height((220.dp))) {
            Column(
                modifier = modifier
                    .align(Alignment.TopCenter)
            ) {
                Box(modifier = modifier
                    .fillMaxWidth()
                    .height(40.dp)
                ) {
                    Text(
                        text = "NFC",
                        modifier = modifier.fillMaxWidth(),
                        style = TextStyle(fontSize = 22.sp),
                        textAlign = TextAlign.Center,
                    )
                }
                FilledTonalButton(
                    modifier = modifier.fillMaxWidth(),
                    onClick = {
                        checkAvailable(context, viewModel)
                    }) {
                    Text("isAvailable(): ${viewModel.uiState.collectAsState().value.nfcStatus.name}", textAlign = TextAlign.Center)
                }
                Box(
                    modifier = modifier
                        .background(Color.LightGray)
                        .height(10.dp)
                        .fillMaxWidth())
                Row() {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .wrapContentWidth(Alignment.Start)) {
                        FilledTonalButton(
                            modifier = modifier.fillMaxWidth(),
                            onClick = {
                                togglePolling(context, viewModel, viewModel.uiState.value.isPolling)
//                                HyperRingNFC.startNFCTagPolling(
//                                    context as Activity,
//                                    onDiscovered = :: onDiscovered
//                                )
                            }) {
                            Text("[readHyperRing]\n" + "isPolling: ${viewModel.uiState.collectAsState().value.isPolling}", textAlign = TextAlign.Center)
                        }
                    }
                    Box(modifier = modifier
                        .background(Color.LightGray)
                        .width(10.dp))
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .wrapContentWidth(Alignment.Start)) {
                        FilledTonalButton(
                            modifier = modifier.fillMaxWidth(),
                            onClick = {
                                toggleNFCMode(viewModel)
                            }
                        ) {
                            Text("[HyperRing] ${if(viewModel.uiState.collectAsState().value.isWriteMode)"Writing Mode" else "Reading Mode"}", textAlign = TextAlign.Center)
                        }
                    }
                }
                Box(
                    modifier = modifier
                        .background(Color.LightGray)
                        .height(10.dp)
                        .fillMaxWidth())
                if(!viewModel.uiState.collectAsState().value.isWriteMode) Row() {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .wrapContentWidth(Alignment.Start)) {
                        FilledTonalButton(
                            modifier = modifier.fillMaxWidth(),
                            colors = if(viewModel.uiState.collectAsState().value.targetReadId == 10L)
                                ButtonDefaults.filledTonalButtonColors(containerColor = Color.Red)
                            else ButtonDefaults.outlinedButtonColors(),
                            onClick = {
                                setReadTargetId(viewModel, 10)
                            }) {
                            Text("[Read] Read to only ID-10 TAG", textAlign = TextAlign.Center)
                        }
                    }
                    Box(modifier = modifier
                        .background(Color.LightGray)
                        .width(10.dp))
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .wrapContentWidth(Alignment.Start)) {
                        FilledTonalButton(
                            modifier = modifier.fillMaxWidth(),
                            colors = if(viewModel.uiState.collectAsState().value.targetReadId == null)
                                ButtonDefaults.filledTonalButtonColors(containerColor = Color.Red)
                            else ButtonDefaults.outlinedButtonColors(),
                            onClick = {
                                setReadTargetId(viewModel, null)
                            }
                        ) {
                            Text("[Read] Read to Any HyperRing TAG", textAlign = TextAlign.Center)
                        }
                    }
                }
                if(viewModel.uiState.collectAsState().value.isWriteMode) Row() {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .wrapContentWidth(Alignment.Start)) {
                        FilledTonalButton(
                            modifier = modifier.fillMaxWidth(),
                            colors = if(viewModel.uiState.collectAsState().value.targetWriteId == 10L)
                                ButtonDefaults.filledTonalButtonColors(containerColor = Color.Red)
                            else ButtonDefaults.outlinedButtonColors(),
                            onClick = {
                                setWriteTargetId(viewModel, 10)
                            }) {
                            Text("[Write] Write to ID-10 TAG", textAlign = TextAlign.Center)
                        }
                    }
                    Box(modifier = modifier
                        .background(Color.LightGray)
                        .width(10.dp))
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .wrapContentWidth(Alignment.Start)) {
                        FilledTonalButton(
                            modifier = modifier.fillMaxWidth(),
                            colors = if(viewModel.uiState.collectAsState().value.targetWriteId == null)
                                ButtonDefaults.filledTonalButtonColors(containerColor = Color.Red)
                            else ButtonDefaults.outlinedButtonColors(),
                            onClick = {
                                setWriteTargetId(viewModel, null)
                            }
                        ) {
                            Text("[Write] Write to Any TAG", textAlign = TextAlign.Center)
                        }
                    }
                }
                Box(
                    modifier = modifier
                        .background(Color.LightGray)
                        .height(10.dp)
                        .fillMaxWidth())
            }
        }
    }
}

fun setReadTargetId(viewModel: MainViewModel, id: Long?) {
    viewModel.setReadTargetId(id)
}

fun setWriteTargetId(viewModel: MainViewModel, id: Long?) {
    viewModel.setWriteTargetId(id)
}

fun toggleNFCMode(viewModel: MainViewModel) {
    viewModel.toggleNFCMode()
}

fun togglePolling(context: Context, viewModel: MainViewModel, isPolling: Boolean) {
    if(isPolling) {
        stopPolling(context, viewModel)
    } else {
        startPolling(context, viewModel)
    }
}

fun startPolling(context: Context, viewModel: MainViewModel) {
    viewModel.startPolling(context)
}

fun stopPolling(context: Context, viewModel: MainViewModel) {
    viewModel.stopPolling(context)
}

fun checkAvailable(context: Context, viewModel: MainViewModel) {
    viewModel.initNFCStatus(context)
}

private fun showToast(context: Context, text: String) {
    Log.d("MainActivity", "text: $text")
    val handler = Handler(Looper.getMainLooper())
    handler.postDelayed(Runnable {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show() }, 0)
}

data class MainUiState(
    // UI state flags
    val nfcStatus: NFCStatus = NFCStatus.NFC_UNSUPPORTED,
    val isPolling: Boolean = false,
    var isWriteMode : Boolean = false,
    var targetWriteId: Long? = null,
    var targetReadId: Long? = null
)

class MainViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    fun initNFCStatus(context: Context) {
        // init HyperRingNFC
        HyperRingNFC.initializeHyperRingNFC(context).let {
            _uiState.update { currentState ->
                currentState.copy(
                    nfcStatus = HyperRingNFC.getNFCStatus(),
                )
            }
        }
    }

    private fun onDiscovered(hyperRingTag: HyperRingTag) : HyperRingTag {
        if(_uiState.value.isWriteMode) {
            /// Writing Data to Any HyperRing NFC TAG
            val isWrite = HyperRingNFC.write(uiState.value.targetWriteId, hyperRingTag,
                // Default HyperRingData
//                HyperRingData.createData(10, mutableMapOf("age" to 20, "name" to "홍길동")))
                // Demo custom Data
                DemoData.createData(10, "John Doe"))
            if(isWrite && MainActivity.mainActivity != null)
                showToast(MainActivity.mainActivity!!, "[write]${hyperRingTag.id}")
        } else {
            if(hyperRingTag.isHyperRingTag()) {
                val readTag: HyperRingTag? = HyperRingNFC.read(uiState.value.targetReadId, hyperRingTag)
                if(readTag != null) {
                    if(MainActivity.mainActivity != null)
                        showToast(MainActivity.mainActivity!!, "[read]${hyperRingTag.id}")
                }
            }
        }
        return hyperRingTag
    }

    fun startPolling(context: Context) {
        HyperRingNFC.startNFCTagPolling(
            context as Activity, onDiscovered = :: onDiscovered).let {
            _uiState.update { currentState ->
                currentState.copy(
                    isPolling = HyperRingNFC.isPolling
                )
            }
        }
    }

    fun stopPolling(context: Context) {
        HyperRingNFC.stopNFCTagPolling(context as Activity).let {
            _uiState.update { currentState ->
                currentState.copy(
                    isPolling = HyperRingNFC.isPolling
                )
            }
        }
    }

    fun toggleNFCMode() {
        _uiState.update { currentState ->
            currentState.copy(
                isWriteMode = !_uiState.value.isWriteMode
            )
        }
    }

    fun setReadTargetId(id: Long?) {
        _uiState.update { currentState ->
            currentState.copy(
                targetReadId = id
            )
        }
    }

    fun setWriteTargetId(id: Long?) {
        _uiState.update { currentState ->
            currentState.copy(
                targetWriteId = id
            )
        }
    }
}