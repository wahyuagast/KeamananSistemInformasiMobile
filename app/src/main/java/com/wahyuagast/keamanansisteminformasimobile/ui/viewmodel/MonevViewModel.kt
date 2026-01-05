package com.wahyuagast.keamanansisteminformasimobile.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wahyuagast.keamanansisteminformasimobile.data.model.MonevResponse
import com.wahyuagast.keamanansisteminformasimobile.data.repository.MonevRepository
import com.wahyuagast.keamanansisteminformasimobile.utils.Resource
import kotlinx.coroutines.launch

class MonevViewModel : ViewModel() {
    private val repository = MonevRepository()

    var monevState by mutableStateOf<Resource<MonevResponse>>(Resource.Loading)
        private set

    fun loadMonev() {
        viewModelScope.launch {
            monevState = Resource.Loading
            monevState = repository.getMonev()
        }
    }
}
