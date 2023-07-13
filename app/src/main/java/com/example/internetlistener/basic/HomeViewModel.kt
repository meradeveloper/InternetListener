package com.example.internetlistener.basic

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class HomeViewModel: ViewModel() {

    private var _connectionState = MutableLiveData(false)
    var connectionState: LiveData<Boolean> = _connectionState

    fun  setConnectionState(status: Boolean){
        _connectionState.postValue(status)
    }
}