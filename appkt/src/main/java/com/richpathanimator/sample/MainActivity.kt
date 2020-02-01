package com.richpathanimator.sample

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.animation.AccelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import com.richpathanimator.AnimationListener
import com.richpathanimator.RichPathAnimator
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        icAndroidRichPathView.setOnClickListener { animateAndroid() }
        animationSamplesButton.setOnClickListener { openAnimationSamples() }
        compoundViewSamplesButton.setOnClickListener { openCompoundViewSamples() }
    }

    override fun onResume() {
        super.onResume()
        animateAndroid()
    }

    private fun animateAndroid() {

        val allPaths = icAndroidRichPathView.findAllRichPaths()
        val head = icAndroidRichPathView.findRichPathByName("head")
        val body = icAndroidRichPathView.findRichPathByName("body")
        val rHand = icAndroidRichPathView.findRichPathByName("r_hand")
        val lHand = icAndroidRichPathView.findRichPathByName("l_hand")

        RichPathAnimator.animate(*allPaths)
                .trimPathEnd(0f, 1f)
                .duration(800)
                .animationListener(object : AnimationListener {
                    override fun onStart() {
                        head?.fillColor = Color.TRANSPARENT
                        body?.fillColor = Color.TRANSPARENT
                        rHand?.fillColor = Color.TRANSPARENT
                        lHand?.fillColor = Color.TRANSPARENT
                        rHand?.rotation = 0f
                    }

                    override fun onStop() {}
                })
                .thenAnimate(*allPaths)
                .fillColor(Color.TRANSPARENT, -0x5b39c7)
                .interpolator(AccelerateInterpolator())
                .duration(900)
                .thenAnimate(rHand)
                .rotation(-150f)
                .duration(700)
                .thenAnimate(rHand)
                .rotation(-150f, -130f, -150f, -130f, -150f, -130f, -150f)
                .duration(2000)
                .thenAnimate(rHand)
                .rotation(0f)
                .duration(500)
                .start()
    }

    private fun openAnimationSamples() {
        startActivity(Intent(this, AnimationSamplesActivity::class.java))
    }

    private fun openCompoundViewSamples() {
        startActivity(Intent(this, CompoundViewSamplesActivity::class.java))
    }
}