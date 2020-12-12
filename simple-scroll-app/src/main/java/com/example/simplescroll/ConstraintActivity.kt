package com.example.simplescroll

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.motion.widget.MotionLayout
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.constraint_activity_layout.*

class ConstraintActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.constraint_activity_layout)
        Glide.with(this)
            .load("https://www.google.es/images/srpr/logo11w.png")
            .into(topContainer)

//
//        (motionLayout as MotionLayout).addTransitionListener(
//            object: MotionLayout.TransitionListener{
//                override fun onTransitionTrigger(
//                    p0: MotionLayout,
//                    p1: Int,
//                    p2: Boolean,
//                    p3: Float
//                ) {
//
//                }
//
//                override fun onTransitionStarted(p0: MotionLayout, p1: Int, p2: Int) {
//
//                }
//
//                override fun onTransitionChange(p0: MotionLayout, p1: Int, p2: Int, p3: Float) {
//
//                }
//
//                override fun onTransitionCompleted(p0: MotionLayout, p1: Int) {
//
//                }
//            }
//        )
    }

}