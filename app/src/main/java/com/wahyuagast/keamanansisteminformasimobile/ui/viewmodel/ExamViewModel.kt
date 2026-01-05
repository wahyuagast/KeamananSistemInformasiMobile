package com.wahyuagast.keamanansisteminformasimobile.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wahyuagast.keamanansisteminformasimobile.data.model.ExamDraftResponse
import com.wahyuagast.keamanansisteminformasimobile.data.model.ExamFinalResponse
import com.wahyuagast.keamanansisteminformasimobile.data.repository.ExamRepository
import com.wahyuagast.keamanansisteminformasimobile.utils.Resource
import kotlinx.coroutines.launch

class ExamViewModel : ViewModel() {
    private val repository = ExamRepository()

    var draftState by mutableStateOf<Resource<ExamDraftResponse>>(Resource.Loading)
        private set

    var finalState by mutableStateOf<Resource<ExamFinalResponse>>(Resource.Loading)
        private set

    fun loadDraft() {
        viewModelScope.launch {
            draftState = Resource.Loading
            draftState = repository.getExamDraft()
        }
    }

    fun loadFinal() {
        viewModelScope.launch {
            finalState = Resource.Loading
            finalState = repository.getExamFinal()
        }
    }
}
