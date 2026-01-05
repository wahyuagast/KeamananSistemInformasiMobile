package com.wahyuagast.keamanansisteminformasimobile.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wahyuagast.keamanansisteminformasimobile.data.model.DocumentListResponse
import com.wahyuagast.keamanansisteminformasimobile.data.model.DocumentStoreResponse
import com.wahyuagast.keamanansisteminformasimobile.data.model.DocumentTypeResponse
import com.wahyuagast.keamanansisteminformasimobile.data.repository.SuratRepository
import com.wahyuagast.keamanansisteminformasimobile.utils.Resource
import kotlinx.coroutines.launch

class SuratViewModel : ViewModel() {
    private val repository = SuratRepository()

    var documentListState by mutableStateOf<Resource<DocumentListResponse>>(Resource.Loading)
        private set

    var documentTypesState by mutableStateOf<Resource<DocumentTypeResponse>>(Resource.Loading)
        private set

    var submissionState by mutableStateOf<Resource<DocumentStoreResponse>?>(null)
        private set

    fun loadDocuments() {
        viewModelScope.launch {
            documentListState = Resource.Loading
            documentListState = repository.getDocuments()
        }
    }

    fun loadDocumentTypes() {
        viewModelScope.launch {
            documentTypesState = Resource.Loading
            documentTypesState = repository.getDocumentTypes()
        }
    }

    fun submitDocument(typeId: Int, description: String) {
        viewModelScope.launch {
            submissionState = Resource.Loading
            val result = repository.submitDocument(typeId, description)
            submissionState = result
            if (result is Resource.Success) {
                loadDocuments() // Refresh list on success
            }
        }
    }

    fun resetSubmissionState() {
        submissionState = null
    }
}
