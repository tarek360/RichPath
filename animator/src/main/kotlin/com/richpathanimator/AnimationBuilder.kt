package com.richpathanimator

import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.util.Log
import android.view.animation.Interpolator
import com.richpath.RichPath
import com.richpath.pathparser.PathDataNode
import com.richpath.pathparser.PathParserCompat

class AnimationBuilder(private val richPathAnimator: RichPathAnimator,
                       private val paths: Array<out RichPath>) {

    companion object {
        private const val DEFAULT_DURATION = 300L
        private const val DEFAULT_START_DELAY = 0L
    }

    val animators = arrayListOf<ValueAnimator>()

    private var duration = DEFAULT_DURATION
    private var startDelay = DEFAULT_START_DELAY
    private var interpolator: Interpolator? = null
    private var repeatMode: RepeatMode = RepeatMode.Restart
    private var repeatCount = 0

    private fun property(propertyName: String, vararg values: Float) {
        for (path in paths) {
            val objectAnimator = ObjectAnimator.ofFloat(path, propertyName, *values)
            applyAnimatorProperties(objectAnimator, path)
        }
    }

    fun andAnimate(vararg paths: RichPath): AnimationBuilder {
        return richPathAnimator.addAnimationBuilder(paths)
    }

    fun thenAnimate(vararg paths: RichPath): AnimationBuilder {
        return richPathAnimator.thenAnimate(paths)
    }

    /**
     * Custom animation builder.
     *
     * @param listener the AnimationUpdateListener
     * @param values   A set of values that the animation will animate between over time.
     */
    fun custom(listener: AnimationUpdateListener?, vararg values: Float) = apply {
        for (path in paths) {
            ValueAnimator.ofFloat(*values).apply {
                addUpdateListener { animation ->
                    listener?.update(path, animation.animatedValue as Float)
                    path.onRichPathUpdatedListener?.onPathUpdated()
                }
                applyAnimatorProperties(this, path)
            }
        }
    }

    fun start(): RichPathAnimator {
        richPathAnimator.start()
        return richPathAnimator
    }

    @Deprecated("It doesn't make sense to cancel while you are still building")
    fun cancel() {
        richPathAnimator.cancel()
    }

    fun duration(duration: Long) = apply {
        this.duration = duration
        for (animator in animators) {
            animator.duration = duration
        }
    }

    fun durationSet(duration: Long) = apply {
        richPathAnimator.duration = duration
    }

    fun startDelay(startDelay: Long) = apply {
        this.startDelay = startDelay
        for (animator in animators) {
            animator.startDelay = startDelay
        }
    }

    fun startDelaySet(startDelay: Long) = apply {
        richPathAnimator.startDelay = startDelay
    }

    fun interpolator(interpolator: Interpolator) = apply {
        this.interpolator = interpolator
        for (animator in animators) {
            animator.interpolator = interpolator
        }
    }

    fun interpolatorSet(interpolator: Interpolator) = apply {
        richPathAnimator.interpolator = interpolator
    }

    fun repeatMode(repeatMode: RepeatMode) = apply {
        this.repeatMode = repeatMode
        for (animator in animators) {
            animator.repeatMode = repeatMode.value
        }
    }

    fun repeatModeSet(repeatMode: RepeatMode) = apply {
        richPathAnimator.repeatMode = repeatMode
    }

    fun repeatCount(repeatCount: Int) = apply {
        this.repeatCount = repeatCount
        for (animator in animators) {
            animator.repeatCount = repeatCount
        }
    }

    fun repeatCountSet(repeatCount: Int) = apply {
        richPathAnimator.repeatCount = repeatCount
    }

    fun fillColor(vararg colors: Int) = apply {
        color("fillColor", *colors)
    }

    fun strokeColor(vararg colors: Int) = apply {
        color("strokeColor", *colors)
    }

    private fun color(propertyName: String, vararg colors: Int) = apply {
        for (path in paths) {
            val objectAnimator = ObjectAnimator.ofInt(path, propertyName, *colors)
            objectAnimator.setEvaluator(ArgbEvaluator())
            applyAnimatorProperties(objectAnimator, path)
        }
    }

    fun pathData(vararg pathData: String) = apply {
        val pathDataNodes = arrayListOf<Array<PathDataNode>>()
        for (i in pathData.indices) {
            PathParserCompat.createNodesFromPathData(pathData[i])?.let {
                pathDataNodes.add(it)
            }
        }

        val pathDataNodesArray = pathDataNodes.toTypedArray()
        if (!PathParserCompat.canMorph(pathDataNodesArray)) {
            Log.w("RichPathAnimator", "the paths aren't compatible for morphing. The paths should have exact same length of commands, and exact same length of parameters for each command")
            return@apply
        }

        for (path in paths) {
            val objectAnimator = ObjectAnimator.ofObject(path,
                    "pathDataNodes", PathEvaluator(), *pathDataNodesArray)
            applyAnimatorProperties(objectAnimator, path)
        }
    }

    private fun applyAnimatorProperties(animator: ValueAnimator, path: RichPath?) {
        path ?: return
        animator.duration = duration
        animator.startDelay = startDelay
        animator.repeatMode = repeatMode.value
        animator.repeatCount = repeatCount
        interpolator?.let { animator.interpolator = it }
        //add animator to the animators list
        this.animators.add(animator)
    }

    fun strokeAlpha(vararg alpha: Float) = apply {
        property("strokeAlpha", *alpha)
    }

    fun fillAlpha(vararg alpha: Float) = apply {
        property("fillAlpha", *alpha)
    }

    fun size(width: Float, height: Float) = apply {
        property("width", width)
        property("height", height)
    }

    fun scaleX(vararg values: Float) = apply {
        property("scaleX", *values)
    }

    fun scaleY(vararg values: Float) = apply {
        property("scaleY", *values)
    }

    fun scale(vararg values: Float) = apply {
        scaleX(*values)
        scaleY(*values)
    }

    fun width(vararg values: Float) = apply {
        property("width", *values)
    }

    fun height(vararg values: Float) = apply {
        property("height", *values)
    }

    fun rotation(vararg values: Float) = apply {
        property("rotation", *values)
    }

    fun translationY(vararg values: Float) = apply {
        property("translationY", *values)
    }

    fun translationX(vararg values: Float) = apply {
        property("translationX", *values)
    }

    fun trimPathStart(vararg values: Float) = apply {
        property("trimPathStart", *values)
    }

    fun trimPathEnd(vararg values: Float) = apply {
        property("trimPathEnd", *values)
    }

    fun trimPathOffset(vararg values: Float) = apply {
        property("trimPathOffset", *values)
    }

    fun animationListener(listener: AnimationListener) = apply {
        richPathAnimator.animationListener = listener
    }
}
