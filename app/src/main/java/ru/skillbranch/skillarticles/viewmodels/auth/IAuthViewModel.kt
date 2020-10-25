package ru.skillbranch.skillarticles.viewmodels.auth

interface IAuthViewModel {
    fun handleLogin(login: String, password: String, dest: Int?)
    fun handleCloseLogin(dest: Int?)
}