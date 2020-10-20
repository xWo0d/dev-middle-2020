package ru.skillbranch.skillarticles.viewmodels

import androidx.lifecycle.SavedStateHandle
import ru.skillbranch.skillarticles.viewmodels.base.BaseViewModel
import ru.skillbranch.skillarticles.viewmodels.base.IViewModelState

class RootViewModel(handle: SavedStateHandle) : BaseViewModel<RootState>(handle, RootState()) {

}

data class RootState(
    val isAuth: Boolean = false
) : IViewModelState