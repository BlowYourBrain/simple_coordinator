package com.example.simplescroll

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.swipe_refresh_activity_layout.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.swipe_refresh_activity_layout)
        swipeContainer.swipeRefreshProgressCallback = {
            Log.d("fuck", "progress = $it")
        }
//
//        mainContainer.clipChildren = false
//        mainContainer.clipToPadding = false
//
//        mainContainer.viewTreeObserver.addOnGlobalLayoutListener {
//            calculateButtonPosition()
//        }
//
//        coordinator.onScrollListener = { scrollView, scrollViewChild ->
//
//            val diff =
//                scrollViewChild.bottom - scrollView.height - scrollView.scrollY
//
//            if (diff <= primaryButton.measuredHeight) {
//                primaryButton.y = (mainContainer.bottom - primaryButton.measuredHeight + diff).toFloat()
//            } else if (primaryButton.y < mainContainer.bottom) {
//                primaryButton.y = mainContainer.bottom.toFloat()
//            }
//
//        }

    }

    private fun calculateButtonPosition() {
        primaryButton.y = when {
            hasSpaceForButton(scrollView, viewsHolder) -> {
                (mainContainer.bottom - primaryButton.measuredHeight).toFloat()
            }

            hasSpaceForPartOfButton(scrollView, viewsHolder) -> {
                (viewsHolder.measuredHeight - viewsHolder.paddingBottom).toFloat()
            }

            else -> {
                mainContainer.bottom.toFloat()
            }
        }
    }


    private fun hasSpaceForButton(scrollview: View, scrollViewChild: View): Boolean {
        val parentScrollHeightDifference =
            scrollview.measuredHeight - (scrollViewChild.measuredHeight - scrollViewChild.paddingBottom)

        return parentScrollHeightDifference > primaryButton.measuredHeight
    }

    private fun hasSpaceForPartOfButton(scrollview: View, scrollViewChild: View): Boolean {
        val parentScrollHeightDifference =
            scrollview.measuredHeight - (scrollViewChild.measuredHeight - scrollViewChild.paddingBottom)

        return parentScrollHeightDifference > 0
    }


}
