package com.ibmhack2021.coughit.ui.record

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ibmhack2021.coughit.repository.Repository

class RecordViewModelProviderFactory(val repository: Repository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return RecordViewModel(repository) as T
    }
}