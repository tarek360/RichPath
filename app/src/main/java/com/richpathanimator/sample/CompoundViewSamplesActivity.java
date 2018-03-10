package com.richpathanimator.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.richpath.RichPath;
import com.richpath.RichPathView;

public class CompoundViewSamplesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compound_view_samples);

        RichPathView colorPickerRichPathView = findViewById(R.id.color_picker);
        final RichPath bluePath = colorPickerRichPathView.findRichPathByName("bluePath");
        final RichPath redPath = colorPickerRichPathView.findRichPathByName("redPath");
        final RichPath greenPath = colorPickerRichPathView.findRichPathByName("greenPath");
        final RichPath purplePath = colorPickerRichPathView.findRichPathByName("purplePath");

        colorPickerRichPathView.setOnPathClickListener(new RichPath.OnPathClickListener() {
            @Override
            public void onClick(RichPath richPath) {
                bluePath.setStrokeAlpha(0f);
                redPath.setStrokeAlpha(0f);
                greenPath.setStrokeAlpha(0f);
                purplePath.setStrokeAlpha(0f);

                richPath.setStrokeAlpha(0.5f);

                showToast(richPath.getName());
            }
        });
    }

    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}