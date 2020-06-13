package ru.skillbranch.skillarticles.extensions

fun String?.indexesOf(substr: String, ignoreCase: Boolean = true): List<Int> {
    this ?: return listOf()
    val res = mutableListOf<Int>()
    var si = 0
    while (true) {
        indexOf(substr, si, ignoreCase).takeIf { it != -1 }?.let {
            res += it
            si = it + substr.length
        } ?: break
    }
    return res
}