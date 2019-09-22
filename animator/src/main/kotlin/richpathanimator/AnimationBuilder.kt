package richpathanimator

import android.animation.ValueAnimator
import com.richpath.RichPath
import richpathanimator.RichPathAnimator.*

class AnimationBuilder(richPathAnimator: RichPathAnimator, vararg paths: RichPath) {
    companion object {
        private const val DEFAULT_DURATION = 300
        private const val DEFAULT_START_DELAY = 0
    }

    val animators = arrayListOf<ValueAnimator>()

    var repeatMode: RepeatMode = RepeatMode.Restart
    var repeatCount = 0
}