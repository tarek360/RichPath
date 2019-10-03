package richpathanimator

import android.animation.Animator
import android.animation.AnimatorSet
import android.view.animation.Interpolator
import com.richpath.RichPath

class RichPathAnimator {

    var duration = -1L
    var startDelay = -1L
    var interpolator: Interpolator? = null
    var repeatMode: RepeatMode = RepeatMode.None
    var repeatCount = -2
    var animationListener: AnimationListener? = null

    private val animationBuilders = arrayListOf<AnimationBuilder>()

    private lateinit var animatorSet: AnimatorSet

    private var prev: RichPathAnimator? = null
    private var next: RichPathAnimator? = null

    sealed class RepeatMode(val value: Int) {
        object None : RepeatMode(-2)
        object Restart : RepeatMode(1)
        object Reverse : RepeatMode(2)
    }

    companion object {
        const val INFINITE = -1
        @JvmStatic fun animate(vararg paths: RichPath): AnimationBuilder {
            val viewAnimator = RichPathAnimator()
            return viewAnimator.addAnimationBuilder(*paths)
        }
    }

    internal fun addAnimationBuilder(vararg paths: RichPath): AnimationBuilder {
        val animationBuilder = AnimationBuilder(this, *paths)
        animationBuilders.add(animationBuilder)
        return animationBuilder
    }

    internal fun thenAnimate(vararg paths: RichPath): AnimationBuilder {
        val nextRichPathAnimator = RichPathAnimator()
        this.next = nextRichPathAnimator
        nextRichPathAnimator.prev = this
        return nextRichPathAnimator.addAnimationBuilder(*paths)
    }

    private fun createAnimatorSet(): AnimatorSet {
        val animators = arrayListOf<Animator>()
        for (animationBuilder in animationBuilders) {
            val animatorList = animationBuilder.animators
            animators.addAll(animatorList)
            if (repeatMode != RepeatMode.None) {
                animationBuilder.repeatMode = repeatMode
            }
            if (repeatCount != -2) {
                animationBuilder.repeatCount = repeatCount
            }
        }

        val animatorSet = AnimatorSet()
        animatorSet.playTogether(animators)

        if (duration != -1L) {
            animatorSet.duration = duration
        }
        if (startDelay != -1L) {
            animatorSet.startDelay = startDelay
        }
        interpolator?.let { animatorSet.interpolator = it }

        animatorSet.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator?) {
                animationListener?.onStart()
            }

            override fun onAnimationEnd(animation: Animator?) {
                animationListener?.onStop()
                next?.let {
                    it.prev = null
                    it.start()
                }
            }

            override fun onAnimationCancel(animation: Animator?) {
            }

            override fun onAnimationRepeat(animation: Animator?) {
            }
        })

        return animatorSet
    }

    fun start(): RichPathAnimator {
        prev?.start() ?: run {
            animatorSet = createAnimatorSet()
            animatorSet.start()
        }
        return this
    }

    fun cancel() {
        if (animatorSet.isRunning) {
            animatorSet.cancel()
        }
        next?.let {
            it.cancel()
            next = null
        }
    }
}