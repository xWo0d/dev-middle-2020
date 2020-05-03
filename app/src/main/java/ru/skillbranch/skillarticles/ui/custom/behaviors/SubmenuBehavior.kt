package ru.skillbranch.skillarticles.ui.custom.behaviors

import android.util.Log
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.marginRight
import ru.skillbranch.skillarticles.ui.custom.ArticleSubmenu
import ru.skillbranch.skillarticles.ui.custom.Bottombar

class SubmenuBehavior: CoordinatorLayout.Behavior<ArticleSubmenu>() {

    // set view as dependent on bottombar
    override fun layoutDependsOn(
        parent: CoordinatorLayout,
        child: ArticleSubmenu,
        dependency: View
    ): Boolean = dependency is Bottombar

    // will be called if dependency view has been changed
    override fun onDependentViewChanged(
        parent: CoordinatorLayout,
        child: ArticleSubmenu,
        dependency: View
    ): Boolean {
        if (!child.isOpen) return false
        if (dependency.translationY < 0) return false
        if (dependency !is Bottombar) return false

        animate(child, dependency)
        return true
    }

    private fun animate(child: ArticleSubmenu, dependency: Bottombar) {
        val fraction = dependency.translationY / dependency.height
        child.translationX = (child.width + child.marginRight) * fraction
        Log.e("SubmenuBehavior", "fraction: $fraction translationX: ${child.translationX}")
    }
}