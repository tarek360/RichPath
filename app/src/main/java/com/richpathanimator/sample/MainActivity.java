package com.richpathanimator.sample;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;

import com.richpath.RichPath;
import com.richpath.RichPathView;
import com.richpathanimator.AnimationListener;
import com.richpathanimator.RichPathAnimator;

public class MainActivity extends AppCompatActivity {

    private RichPathView commandRichPathView;
    private RichPathView androidRichPathView;
    private RichPathView arrowSearchRichPathView;
    private RichPathView notificationsRichPathView;
    private RichPathView playlistAddCheckRichPathView;
    private RichPathView loveFaceRichPathView;
    private RichPathView animalRichPathView;
    private boolean reverse = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        commandRichPathView = (RichPathView) findViewById(R.id.ic_command);
        androidRichPathView = (RichPathView) findViewById(R.id.ic_android);
        arrowSearchRichPathView = (RichPathView) findViewById(R.id.ic_arrow_search);
        notificationsRichPathView = (RichPathView) findViewById(R.id.ic_notifications);
        playlistAddCheckRichPathView = (RichPathView) findViewById(R.id.ic_playlist_add_check);
        loveFaceRichPathView = (RichPathView) findViewById(R.id.love_face);
        animalRichPathView = (RichPathView) findViewById(R.id.animal);

        animateCommand();
    }

    public void animateCommand() {

        final RichPath part1 = commandRichPathView.findRichPathByName("part1");
        final RichPath part2 = commandRichPathView.findRichPathByName("part2");
        final RichPath part3 = commandRichPathView.findRichPathByName("part3");
        final RichPath part4 = commandRichPathView.findRichPathByName("part4");
        final RichPath part5 = commandRichPathView.findRichPathByName("part5");
        final RichPath part6 = commandRichPathView.findRichPathByName("part6");
        final RichPath part7 = commandRichPathView.findRichPathByName("part7");
        final RichPath part8 = commandRichPathView.findRichPathByName("part8");

        RichPathAnimator
                .animate(part1)
                .trimPathOffset(0, 1.0f)

                .andAnimate(part2)
                .trimPathOffset(0.125f, 1.125f)

                .andAnimate(part3)
                .trimPathOffset(0.250f, 1.250f)

                .andAnimate(part4)
                .trimPathOffset(0.375f, 1.375f)

                .andAnimate(part5)
                .trimPathOffset(0.500f, 1.500f)

                .andAnimate(part6)
                .trimPathOffset(0.625f, 1.625f)

                .andAnimate(part7)
                .trimPathOffset(0.750f, 1.750f)

                .andAnimate(part8)
                .trimPathOffset(0.875f, 1.875f)

                .durationSet(2000)
                .repeatModeSet(RichPathAnimator.RESTART)
                .repeatCountSet(RichPathAnimator.INFINITE)
                .interpolatorSet(new LinearInterpolator())
                .start();
    }

    public void animateAndroid(View view) {

        final RichPath head = androidRichPathView.findRichPathByName("head");
        final RichPath body = androidRichPathView.findRichPathByName("body");
        final RichPath rEye = androidRichPathView.findRichPathByName("r_eye");
        final RichPath lEye = androidRichPathView.findRichPathByName("l_eye");
        final RichPath rHand = androidRichPathView.findRichPathByName("r_hand");
        final RichPath lHand = androidRichPathView.findRichPathByName("l_hand");

        RichPathAnimator.animate(head, rEye, lEye, body, rHand, lHand)
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
                .thenAnimate(head, rEye, lEye, body, rHand, lHand)
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

    public void animateAnimal(View view) {

        String hippoPathData = getString(R.string.hippo_path);
        String elephantPathData = getString(R.string.elephant_path);
        String bullPathData = getString(R.string.bull_path);

        final RichPath richPath = animalRichPathView.findFirstRichPath();

        RichPathAnimator
                .animate(richPath)
                .pathData(elephantPathData)
                .duration(600)

                .thenAnimate(richPath)
                .pathData(bullPathData)
                .duration(600)

                .thenAnimate(richPath)
                .pathData(hippoPathData)
                .duration(600)

                .start();
    }

    public void animateArrowToSearch(View view) {

        RichPath searchCircle = arrowSearchRichPathView.findRichPathByName("search_circle");
        RichPath stem = arrowSearchRichPathView.findRichPathByName("stem");
        RichPath arrowTop = arrowSearchRichPathView.findRichPathByName("arrow_head_top");
        RichPath arrowBottom = arrowSearchRichPathView.findRichPathByName("arrow_head_bottom");

        if (reverse) {
            RichPathAnimator.animate(stem)
                    .trimPathStart(0f, 0.75f)
                    .trimPathEnd(0.185f, 1f)
                    .andAnimate(searchCircle)
                    .trimPathEnd(1, 0)
                    .andAnimate(arrowTop, arrowBottom)
                    .trimPathEnd(0, 1)
                    .start();
        } else {
            RichPathAnimator.animate(stem)
                    .trimPathStart(0.75f, 0f)
                    .trimPathEnd(1f, 0.185f)
                    .andAnimate(searchCircle)
                    .trimPathEnd(0, 1)
                    .andAnimate(arrowTop, arrowBottom)
                    .trimPathEnd(1, 0)
                    .start();
        }
        reverse = !reverse;
    }

    public void animateNotification(View view) {

        final RichPath top = notificationsRichPathView.findRichPathByIndex(0);
        final RichPath bottom = notificationsRichPathView.findRichPathByIndex(1);

        RichPathAnimator.animate(top)
                .interpolator(new DecelerateInterpolator())
                .rotation(0, 20, -20, 10, -10, 5, -5, 2, -2, 0)
                .duration(4000)
                .andAnimate(bottom)
                .interpolator(new DecelerateInterpolator())
                .rotation(0, 10, -10, 5, -5, 2, -2, 0)
                .startDelay(50)
                .duration(4000)
                .start();
    }

    public void animatePlaylistAddCheck(View view) {

        final RichPath line1 = playlistAddCheckRichPathView.findRichPathByName("line1");
        final RichPath line2 = playlistAddCheckRichPathView.findRichPathByName("line2");
        final RichPath line3 = playlistAddCheckRichPathView.findRichPathByName("line3");
        final RichPath tick = playlistAddCheckRichPathView.findRichPathByName("tick");
        final RichPath line3AndTick = playlistAddCheckRichPathView.findRichPathByName("line3_tick");

        line1.setTrimPathEnd(0);
        line2.setTrimPathEnd(0);
        line3.setTrimPathEnd(0);
        tick.setTrimPathEnd(0);
        line3AndTick.setTrimPathEnd(0);
        line3AndTick.setTrimPathStart(0);

        int duration = 400;

        RichPathAnimator.animate(line1)
                .trimPathEnd(0, 1)
                .interpolator(new DecelerateInterpolator())
                .duration(duration)

                .andAnimate(line2)
                .trimPathEnd(0, 1)
                .interpolator(new DecelerateInterpolator())
                .startDelay(140)
                .duration(duration)

                .andAnimate(line3)
                .trimPathEnd(0, 1)
                .interpolator(new LinearInterpolator())
                .startDelay(180)
                .duration(duration)

                .andAnimate(line3AndTick)
                .trimPathEnd(0.33f, 0.428f)
                .interpolator(new LinearInterpolator())
                .startDelay(140 + duration)
                .duration(duration / 3)

                .thenAnimate(line3AndTick)
                .trimPathStart(0.33f, 0.428f)
                .interpolator(new LinearInterpolator())
                .duration(duration / 3)

                .andAnimate(tick)
                .trimPathEnd(0, 1)
                .interpolator(new LinearInterpolator())
                .duration(duration)

                .start();
    }

    public void animateLoveFace(View view) {

        final RichPath rEye = loveFaceRichPathView.findRichPathByName("r_eye");
        final RichPath lEye = loveFaceRichPathView.findRichPathByName("l_eye");

        rEye.setPivotToCenter(true);
        lEye.setPivotToCenter(true);

        RichPathAnimator
                .animate(rEye, lEye)
                .interpolator(new LinearInterpolator())
                .duration(800)
                .repeatMode(RichPathAnimator.RESTART)
                .repeatCount(RichPathAnimator.INFINITE)
                .scale(1, 0.9f, 1.07f, 1)
                .fillColor(0XFFF52C5B, 0xFFF24976, 0XFFD61A4C, 0XFFF52C5B)
                .start();
    }

}