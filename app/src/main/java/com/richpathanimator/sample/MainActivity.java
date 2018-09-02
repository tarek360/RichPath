package com.richpathanimator.sample;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.AccelerateInterpolator;

import com.richpath.RichPath;
import com.richpath.RichPathView;
import com.richpathanimator.AnimationListener;
import com.richpathanimator.RichPathAnimator;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onResume() {
        super.onResume();
        animateAndroid();
    }

    public void animateAndroid(View view) {
        animateAndroid();
    }

    private void animateAndroid() {

        RichPathView androidRichPathView = findViewById(R.id.ic_android);

        final RichPath[] allPaths = androidRichPathView.findAllRichPaths();
        final RichPath head = androidRichPathView.findRichPathByName("head");
        final RichPath body = androidRichPathView.findRichPathByName("body");
        final RichPath rHand = androidRichPathView.findRichPathByName("r_hand");
        final RichPath lHand = androidRichPathView.findRichPathByName("l_hand");

        RichPathAnimator.animate(allPaths)
                .trimPathEnd(0, 1)
                .duration(800)
                .animationListener(new AnimationListener() {
                    @Override
                    public void onStart() {
                        head.setFillColor(Color.TRANSPARENT);
                        body.setFillColor(Color.TRANSPARENT);
                        rHand.setFillColor(Color.TRANSPARENT);
                        lHand.setFillColor(Color.TRANSPARENT);
                        rHand.setRotation(0);
                    }

                    @Override
                    public void onStop() {
                    }
                })
                .thenAnimate(allPaths)
                .fillColor(Color.TRANSPARENT, 0xFFa4c639)
                .interpolator(new AccelerateInterpolator())
                .duration(900)
                .thenAnimate(rHand)
                .rotation(-150)
                .duration(700)
                .thenAnimate(rHand)
                .rotation(-150, -130, -150, -130, -150, -130, -150)
                .duration(2000)
                .thenAnimate(rHand)
                .rotation(0)
                .duration(500)
                .start();
    }

    public void openAnimationSamples(View view) {
        startActivity(new Intent(this, AnimationSamplesActivity.class));
    }

    public void openCompoundViewSamples(View view) {
        startActivity(new Intent(this, CompoundViewSamplesActivity.class));
    }
}