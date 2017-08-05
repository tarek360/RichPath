package com.richpathanimator;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.util.Log;
import android.view.animation.Interpolator;

import com.richpath.RichPath;
import com.richpath.pathparser.PathDataNode;
import com.richpath.pathparser.PathParserCompat;

import java.util.ArrayList;
import java.util.List;

import static com.richpathanimator.RichPathAnimator.RESTART;

/**
 * Created by tarek on 6/29/17.
 */


public class AnimationBuilder {


    private static final long DEFAULT_DURATION = 300;
    private static final long DEFAULT_START_DELAY = 0;

    private final RichPathAnimator richPathAnimator;
    private final RichPath[] paths;
    private final List<ValueAnimator> animators = new ArrayList<>();

    private long duration = DEFAULT_DURATION;
    private long startDelay = DEFAULT_START_DELAY;
    private Interpolator interpolator;
    private int repeatMode = RESTART;
    private int repeatCount = 0;

    public AnimationBuilder(RichPathAnimator richPathAnimator, RichPath... paths) {
        this.richPathAnimator = richPathAnimator;
        this.paths = paths;
    }

    private void property(String propertyName, float... values) {
        for (final RichPath path : paths) {
            ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(path, propertyName, values);
            applyAnimatorProperties(objectAnimator, path);
        }
    }

    public AnimationBuilder andAnimate(RichPath... paths) {
        return richPathAnimator.addAnimationBuilder(paths);
    }

    public AnimationBuilder thenAnimate(RichPath... paths) {
        return richPathAnimator.thenAnimate(paths);
    }

    /**
     * Custom animation builder.
     *
     * @param listener the AnimationUpdateListener
     * @param values   A set of values that the animation will animate between over time.
     */
    public AnimationBuilder custom(final AnimationUpdateListener listener, float... values) {
        for (final RichPath path : paths) {
            ValueAnimator valueAnimator = ValueAnimator.ofFloat(values);
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    if (path != null) {
                        if (listener != null) {
                            listener.update(path, (Float) animation.getAnimatedValue());
                        }
                        if (path.getOnRichPathUpdatedListener() != null) {
                            path.getOnRichPathUpdatedListener().onPathUpdated();
                        }
                    }
                }
            });

            applyAnimatorProperties(valueAnimator, path);
        }
        return this;
    }

    public RichPathAnimator start() {
        richPathAnimator.start();
        return richPathAnimator;
    }

    List<ValueAnimator> getAnimators() {
        return animators;
    }

    public AnimationBuilder duration(long duration) {
        this.duration = duration;
        for (ValueAnimator animator : animators) {
            animator.setDuration(duration);
        }
        return this;
    }

    public AnimationBuilder durationSet(long duration) {
        richPathAnimator.duration(duration);
        return this;
    }

    public AnimationBuilder startDelay(long startDelay) {
        this.startDelay = startDelay;
        for (ValueAnimator animator : animators) {
            animator.setStartDelay(startDelay);
        }
        return this;
    }

    public AnimationBuilder startDelaySet(long startDelay) {
        richPathAnimator.startDelay(startDelay);
        return this;
    }

    public AnimationBuilder interpolator(Interpolator interpolator) {
        this.interpolator = interpolator;
        for (ValueAnimator animator : animators) {
            animator.setInterpolator(interpolator);
        }
        return this;
    }

    public AnimationBuilder interpolatorSet(Interpolator interpolator) {
        richPathAnimator.interpolator(interpolator);
        return this;
    }

    public AnimationBuilder repeatMode(@RichPathAnimator.RepeatMode int repeatMode) {
        this.repeatMode = repeatMode;
        for (ValueAnimator animator : animators) {
            animator.setRepeatMode(repeatMode);
        }
        return this;
    }

    public AnimationBuilder repeatModeSet(@RichPathAnimator.RepeatMode int repeatMode) {
        richPathAnimator.repeatMode(repeatMode);
        return this;
    }

    public AnimationBuilder repeatCount(int repeatCount) {
        this.repeatCount = repeatCount;
        for (ValueAnimator animator : animators) {
            animator.setRepeatCount(repeatCount);
        }
        return this;
    }

    public AnimationBuilder repeatCountSet(int repeatCount) {
        richPathAnimator.repeatCount(repeatCount);
        return this;
    }

    public AnimationBuilder fillColor(int... colors) {
        color("fillColor", colors);
        return this;
    }

    public AnimationBuilder strokeColor(int... colors) {
        color("strokeColor", colors);
        return this;
    }

    private AnimationBuilder color(String propertyName, int... colors) {
        for (final RichPath path : paths) {
            ObjectAnimator objectAnimator = ObjectAnimator.ofInt(path, propertyName, colors);
            objectAnimator.setEvaluator(new ArgbEvaluator());
            applyAnimatorProperties(objectAnimator, path);
        }
        return this;
    }

    public AnimationBuilder pathData(String... pathData) {

        PathDataNode[][] pathDataNodes = new PathDataNode[pathData.length][];
        for (int i = 0; i < pathData.length; i++) {
            pathDataNodes[i] = PathParserCompat.createNodesFromPathData(pathData[i]);
        }

        if (!PathParserCompat.canMorph(pathDataNodes)) {
            Log.w("RichPathAnimator", "the paths aren't compatible for morphing. The paths should have exact same length of commands, and exact same length of parameters for each command");
            return this;
        }

        for (final RichPath path : paths) {
            ObjectAnimator objectAnimator =
                    ObjectAnimator.ofObject(path, "pathDataNodes", new PathEvaluator(), pathDataNodes);
            applyAnimatorProperties(objectAnimator, path);
        }
        return this;
    }

    private void applyAnimatorProperties(ValueAnimator animator, final RichPath path) {

        if (path == null) {
            return;
        }
        animator.setDuration(duration);
        animator.setStartDelay(startDelay);
        animator.setRepeatMode(repeatMode);
        animator.setRepeatCount(repeatCount);
        if (interpolator != null) {
            animator.setInterpolator(interpolator);
        }
        //add animator to the animators list
        this.animators.add(animator);
    }

    public AnimationBuilder strokeAlpha(float... alpha) {
        property("strokeAlpha", alpha);
        return this;
    }

    public AnimationBuilder fillAlpha(float... alpha) {
        property("fillAlpha", alpha);
        return this;
    }

    public AnimationBuilder size(float width, float height) {
        property("width", width);
        property("height", height);
        return this;
    }

    public AnimationBuilder scaleX(float... values) {
        property("scaleX", values);
        return this;
    }

    public AnimationBuilder scaleY(float... values) {
        property("scaleY", values);
        return this;
    }

    public AnimationBuilder scale(float... values) {
        scaleX(values);
        scaleY(values);
        return this;
    }

    public AnimationBuilder width(float... values) {
        property("width", values);
        return this;
    }

    public AnimationBuilder height(float... values) {
        property("height", values);
        return this;
    }

    public AnimationBuilder rotation(float... values) {
        property("rotation", values);
        return this;
    }

    public AnimationBuilder translationY(float... values) {
        property("translationY", values);
        return this;
    }

    public AnimationBuilder translationX(float... values) {
        property("translationX", values);
        return this;
    }

    public AnimationBuilder trimPathStart(float... values) {
        property("trimPathStart", values);
        return this;

    }

    public AnimationBuilder trimPathEnd(float... values) {
        property("trimPathEnd", values);
        return this;
    }

    public AnimationBuilder trimPathOffset(float... values) {
        property("trimPathOffset", values);
        return this;
    }

    public AnimationBuilder animationListener(AnimationListener listener) {
        richPathAnimator.setAnimationListener(listener);
        return this;
    }

}
