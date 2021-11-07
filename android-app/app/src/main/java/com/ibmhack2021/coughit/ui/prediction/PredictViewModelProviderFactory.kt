package com.ibmhack2021.coughit.ui.prediction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ibmhack2021.coughit.repository.Repository

class PredictViewModelProviderFactory(val repository: Repository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return PredictViewModel(repository = repository) as T
    }
}