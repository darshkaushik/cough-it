package com.ibmhack2021.coughit.ui.pasttests

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ibmhack2021.coughit.repository.Repository

class PastTestsViewModelProviderFactory(val repository: Repository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return PastTestsViewModel(repository) as T
    }
}