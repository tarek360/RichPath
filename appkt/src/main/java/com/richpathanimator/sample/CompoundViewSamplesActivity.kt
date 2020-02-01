package com.richpathanimator.sample

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_compound_view_samples.*

class CompoundViewSamplesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_compound_view_samples)

        val bluePath = colorPickerRichPathView.findRichPathByName("bluePath")
        val redPath = colorPickerRichPathView.findRichPathByName("redPath")
        val greenPath = colorPickerRichPathView.findRichPathByName("greenPath")
        val purplePath = colorPickerRichPathView.findRichPathByName("purplePath")

        colorPickerRichPathView.setOnPathClickListener { clickedRichPath ->

            bluePath?.strokeAlpha = 0f
            redPath?.strokeAlpha = 0f
            greenPath?.strokeAlpha = 0f
            purplePath?.strokeAlpha = 0f

            if (clickedRichPath == bluePath
                    || clickedRichPath == redPath
                    || clickedRichPath == greenPath
                    || clickedRichPath == purplePath) {
                clickedRichPath.strokeAlpha = 0.5f
                showToast(clickedRichPath.name)
            }
        }
    }

    private fun showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
}