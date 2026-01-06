package com.wahyuagast.keamanansisteminformasimobile.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wahyuagast.keamanansisteminformasimobile.data.model.ImplementationResponse
import com.wahyuagast.keamanansisteminformasimobile.data.repository.ImplementationRepository
import com.wahyuagast.keamanansisteminformasimobile.utils.Resource
import kotlinx.coroutines.launch

class ImplementationViewModel : ViewModel() {
    private val repository = ImplementationRepository()

    var implementationState by mutableStateOf<Resource<ImplementationResponse>>(Resource.Loading)
        private set

    fun loadImplementation() {
        viewModelScope.launch {
            implementationState = Resource.Loading
            implementationState = repository.getImplementation()
        }
    }
}
