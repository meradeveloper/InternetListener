package com.example.internetlistener.basic

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsAnimationCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.internetlistener.ui.theme.InternetListenerTheme

class KeyboardStateListenActivity : ComponentActivity() {
    private var isKeyboardShowing = false
    private val mViewModel: KeyboardStateViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            InternetListenerTheme(
                darkTheme = true
            ) {
                Surface {
                    Box(Modifier.safeDrawingPadding()) {
                        showKeyboardScreen()
                    }
                }
            }
        }
        setInsetListener()
    }

    private fun isAboveAndroid10(): Boolean {
        return Build.VERSION.SDK_INT > Build.VERSION_CODES.Q
    }

    private fun setInsetListener() {
        if (isAboveAndroid10()) {
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)
            // synchronize animation with keyboard for smooth view shifting
            ViewCompat.setWindowInsetsAnimationCallback(
                window.decorView.rootView,
                object : WindowInsetsAnimationCompat.Callback(DISPATCH_MODE_STOP) {
                    override fun onProgress(
                        insets: WindowInsetsCompat,
                        runningAnimations: MutableList<WindowInsetsAnimationCompat>
                    ): WindowInsetsCompat {
                        val imeHeight = insets.getInsets(WindowInsetsCompat.Type.ime()).bottom
                        val sysBarInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())

                        val updatedHeight =
                            if (imeHeight - sysBarInsets.bottom < 0) 0 else imeHeight - sysBarInsets.bottom
                        mViewModel.postKeyboardHeight(
                            pxToDp(
                                updatedHeight,
                                this@KeyboardStateListenActivity
                            )
                        )
                        return insets
                    }
                }
            )
        } else {
            @Suppress("DEPRECATION")
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        }

        ViewCompat.setOnApplyWindowInsetsListener(window.decorView.rootView) { view, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemGestures())
            val insetsSystemBar = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            val imeHeight = windowInsets.getInsets(WindowInsetsCompat.Type.ime()).bottom
            val isImeVisible = windowInsets.isVisible(WindowInsetsCompat.Type.ime())

            if (isKeyboardShowing == !isImeVisible) {
                if (isImeVisible) {
                    if (!isAboveAndroid10()) {
                        // shift view to top
                        mViewModel.postKeyboardHeight(
                            pxToDp(
                                imeHeight - insetsSystemBar.bottom,
                                this@KeyboardStateListenActivity
                            )
                        )
                    }
                } else {
                    if (!isAboveAndroid10()) {
                        // shift view to bottom
                        mViewModel.postKeyboardHeight(pxToDp(0, this@KeyboardStateListenActivity))
                    }
                }
                Toast.makeText(
                    this,
                    "keyboard ${if (isImeVisible) "open" else "close"}, height = $imeHeight",
                    Toast.LENGTH_SHORT
                ).show()
            }

            isKeyboardShowing = isImeVisible
            if (isAboveAndroid10())
                ViewCompat.onApplyWindowInsets(view, windowInsets)
            else {
                view.updatePadding(
                    insets.left,
                    insetsSystemBar.top,
                    insets.right,
                    insetsSystemBar.bottom
                )
                WindowInsetsCompat.CONSUMED
            }
        }
    }
}

@Composable
private fun showKeyboardScreen() {
    keyboardScreen()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun keyboardScreen(keyboardViewModel: KeyboardStateViewModel = viewModel()) {

    val height by keyboardViewModel.keyboardHeightChange.collectAsState()

    var inputState: String by remember {
        mutableStateOf("")
    }

    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .background(color = Color.Gray)
            .fillMaxSize(1f)
    ) {
        Column(
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize(1f)
        ) {
            TextField(modifier = Modifier.background(color = Color.Blue),
                value = inputState,
                onValueChange = {
                    inputState = it
                },
                label = { Text("write something...") }
            )
            Spacer(modifier = Modifier.size(width = 0.dp, height = height.dp))
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun previewKeyboardScreen() {
    keyboardScreen()
}

fun pxToDp(px: Int, context: Context): Int {
    val resources = context.resources
    val metrics = resources.displayMetrics
    return (px / (metrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)).toInt()
}
