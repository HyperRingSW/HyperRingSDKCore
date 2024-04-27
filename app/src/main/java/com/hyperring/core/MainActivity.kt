package com.hyperring.core
import android.app.Activity
import android.content.Context
import android.nfc.Tag
import android.os.Bundle
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
import com.hyperring.sdk.core.nfc.HyperRingNFC
import com.hyperring.sdk.core.nfc.NFCStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private lateinit var mainViewModel : MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainViewModel = ViewModelProvider(this)[MainViewModel::class.java]
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                mainViewModel.initNFCStatus(applicationContext)
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
            .height((170.dp))) {
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
                                startPolling(context, viewModel)
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
//                    onClick()
                            }) {
                            Text("[writeHyperRing]\n" + "isPolling: false", textAlign = TextAlign.Center)
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

fun startPolling(context: Context, viewModel: MainViewModel) {
    viewModel.startPolling(context)
}

fun checkAvailable(context: Context, viewModel: MainViewModel) {
    viewModel.initNFCStatus(context)
}

fun onDiscovered(context: Context, tag: Tag) : Tag {
    Log.d("MainActivity", "onDiscovered: ${tag.id}")
    Toast.makeText(context, "onDiscovered: ${tag.id}", Toast.LENGTH_SHORT).show()
    return tag
}

data class MainUiState(
    val nfcStatus: NFCStatus = NFCStatus.NFC_UNSUPPORTED,
    val isPolling: Boolean = false
)

class MainViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    fun initNFCStatus(context: Context) {
        HyperRingNFC.initializeHyperRingNFC(context).let {
            _uiState.update { currentState ->
                currentState.copy(
                    nfcStatus = HyperRingNFC.getNFCStatus(),
                )
            }
        }
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
}