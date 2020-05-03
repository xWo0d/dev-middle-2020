package ru.skillbranch.skillarticles.ui.custom

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.shape.MaterialShapeDrawable
import ru.skillbranch.skillarticles.R
import ru.skillbranch.skillarticles.ui.custom.behaviors.BottomBarBehavior

class Bottombar @JvmOverloads constructor(
    context: Context,
    attrRes: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrRes, defStyleAttr), CoordinatorLayout.AttachedBehavior {

    init {
        View.inflate(context, R.layout.layout_bottombar, this)
        val materialBg = MaterialShapeDrawable.createWithElevationOverlay(context)
        materialBg.elevation = elevation
        background = materialBg
    }

    override fun getBehavior(): CoordinatorLayout.Behavior<*> = BottomBarBehavior()
}