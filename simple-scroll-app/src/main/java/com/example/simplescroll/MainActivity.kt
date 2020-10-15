package com.example.simplescroll

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.swipe_refresh_activity_layout.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.swipe_refresh_activity_layout)
        swipeContainer.run {
            swipeRefreshProgressCallback = {
                Log.d("fuck", "progress = $it")
                progress.text = "progress = ${it * 100}%"
            }
            onRefreshCallback = {
                Toast.makeText(this@MainActivity, "refresh", Toast.LENGTH_SHORT).show()
            }
        }

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
