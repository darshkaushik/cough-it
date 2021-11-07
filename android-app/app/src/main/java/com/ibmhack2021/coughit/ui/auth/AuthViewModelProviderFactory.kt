package com.ibmhack2021.coughit.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ibmhack2021.coughit.repository.Repository

class AuthViewModelProviderFactory(val repository: Repository) : ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AuthViewModel(repository = repository) as T
    }


}