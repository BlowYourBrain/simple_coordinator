package com.example.simplescroll

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.Button
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
import com.behaviorule.arturdumchev.library.BehaviorByRules
import com.behaviorule.arturdumchev.library.RuledView
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.CollapsingToolbarLayout

class MyCustomBehaviour(
    context: Context?,
    attrs: AttributeSet?
): BehaviorByRules(context, attrs) {

    override fun calcAppbarHeight(child: View): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun View.provideAppbar(): AppBarLayout {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun View.provideCollapsingToolbar(): CollapsingToolbarLayout {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun View.setUpViews(): List<RuledView> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}