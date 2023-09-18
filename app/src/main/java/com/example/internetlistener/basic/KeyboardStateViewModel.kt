package com.example.internetlistener.basic

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class KeyboardStateViewModel: ViewModel() {
    private val _keyboardHeightChange = MutableStateFlow(0)
    val keyboardHeightChange:StateFlow<Int> = _keyboardHeightChange.asStateFlow()

    fun postKeyboardHeight(height: Int){
        _keyboardHeightChange.value = height
    }
}