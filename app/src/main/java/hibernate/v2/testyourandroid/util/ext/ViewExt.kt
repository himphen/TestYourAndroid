package hibernate.v2.testyourandroid.util.ext

import android.animation.ObjectAnimator
import android.content.res.Resources
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator

fun View.slideDown() {
    visibility = View.VISIBLE
    val layoutParams = this.layoutParams
    layoutParams.height = 1
    this.layoutParams = layoutParams
    measure(
        View.MeasureSpec.makeMeasureSpec(
            Resources.getSystem().displayMetrics.widthPixels,
            View.MeasureSpec.EXACTLY
        ),
        View.MeasureSpec.makeMeasureSpec(
            0,
            View.MeasureSpec.UNSPECIFIED
        )
    )
    val height = measuredHeight
    val valueAnimator = ObjectAnimator.ofInt(1, height)
    valueAnimator.addUpdateListener { animation ->
        val value = animation.animatedValue as Int
        if (height > value) {
            val layoutParams1 = this.layoutParams
            layoutParams1.height = value
            this.layoutParams = layoutParams1
        } else {
            val layoutParams1 = this.layoutParams
            layoutParams1.height = ViewGroup.LayoutParams.WRAP_CONTENT
            this.layoutParams = layoutParams1
        }
    }
    valueAnimator.start()
}

fun View.slideUp() {
    post {
        val height = height
        val valueAnimator = ObjectAnimator.ofInt(height, 0)
        valueAnimator.addUpdateListener { animation ->
            val value = animation.animatedValue as Int
            if (value > 0) {
                val layoutParams1 = this.layoutParams
                layoutParams1.height = value
                this.layoutParams = layoutParams1
            } else {
                visibility = View.GONE
            }
        }
        valueAnimator.start()
    }
}

fun RecyclerView?.disableChangeAnimation() {
    if (this == null) return
    (itemAnimator as? SimpleItemAnimator)?.supportsChangeAnimations = false
}
