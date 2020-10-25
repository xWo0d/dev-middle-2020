package ru.skillbranch.skillarticles.ui.auth

import androidx.lifecycle.SavedStateHandle
import ru.skillbranch.skillarticles.data.repositories.RootRepository
import ru.skillbranch.skillarticles.viewmodels.base.BaseViewModel
import ru.skillbranch.skillarticles.viewmodels.base.IViewModelState
import ru.skillbranch.skillarticles.viewmodels.base.NavigationCommand

class AuthViewModel(handle: SavedStateHandle) : BaseViewModel<AuthState>(handle, AuthState()) {
    private val repository = RootRepository

    init {
        subscribeOnDataSource(repository.isAuth()) { isAuth, state ->
            state.copy(isAuth = isAuth)
        }
    }

    fun handleLogin(login: String, password: String, dest: Int?) {
        repository.setAuth(true)
        navigate(NavigationCommand.FinishLogin(dest))
        //TODO do something with navigation
    }

    fun handleCloseLogin(dest: Int?) {
        navigate(NavigationCommand.FinishLogin(dest))
        //TODO do something with navigation
    }
}

data class AuthState(val isAuth: Boolean = false) : IViewModelState