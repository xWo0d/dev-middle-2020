package ru.skillbranch.skillarticles.viewmodels.base

import androidx.lifecycle.SavedStateHandle

interface IViewModelState {

    /**
     * override it if you need save state in Bundle
     */
    fun save(outState: SavedStateHandle) {
        // default empty implementation
    }

    /**
     * override it if you need save state in Bundle
     */
    fun restore(savedState: SavedStateHandle): IViewModelState {
        // default empty implementation
        return this
    }
}