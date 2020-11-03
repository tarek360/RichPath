package com.richpathanimator.sample

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.richpath.RichPath
import kotlinx.android.synthetic.main.activity_compound_view_samples.*

class CompoundViewSamplesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_compound_view_samples)

        val bluePath = colorPickerRichPathView.findRichPathByName("bluePath")
        val redPath = colorPickerRichPathView.findRichPathByName("redPath")
        val greenPath = colorPickerRichPathView.findRichPathByName("greenPath")
        val purplePath = colorPickerRichPathView.findRichPathByName("purplePath")

        colorPickerRichPathView.onPathClickListener = object : RichPath.OnPathClickListener {
            override fun onClick(richPath: RichPath) {
                bluePath?.strokeAlpha = 0f
                redPath?.strokeAlpha = 0f
                greenPath?.strokeAlpha = 0f
                purplePath?.strokeAlpha = 0f

                if (richPath == bluePath
                        || richPath == redPath
                        || richPath == greenPath
                        || richPath == purplePath) {
                    richPath.strokeAlpha = 0.5f
                    richPath.name?.let { showToast(it) }
                }
            }
        }
    }

    private fun showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
}