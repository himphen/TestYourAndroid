package hibernate.v2.testyourandroid.ui.hardware

import android.content.Context
import android.graphics.drawable.GradientDrawable
import androidx.core.content.ContextCompat
import hibernate.v2.testyourandroid.R

object MonitorHelper {
    fun getColorList(context: Context): ArrayList<Any> {
        val colorList = arrayListOf<Any>()
        val orientations = GradientDrawable.Orientation.values()
        colorList.add(MColor(R.color.monitorColorRed))
        colorList.add(MColor(R.color.monitorColorGreen))
        colorList.add(MColor(R.color.monitorColorBlue))
        colorList.add(MColor(R.color.monitorColorBlack))
        colorList.add(MColor(R.color.monitorColorGrey25))
        colorList.add(MColor(R.color.monitorColorGrey50))
        colorList.add(MColor(R.color.monitorColorGrey75))
        colorList.add(MColor(R.color.monitorColorWhite))

        orientations.forEach {
            colorList.add(
                getGradient(context, it, R.color.monitorColorRed)
            )
        }
        orientations.forEach {
            colorList.add(
                getGradient(context, it, R.color.monitorColorGreen)
            )
        }
        orientations.forEach {
            colorList.add(
                getGradient(context, it, R.color.monitorColorBlue)
            )
        }
        return colorList
    }

    private fun getGradient(
        context: Context,
        orientation: GradientDrawable.Orientation,
        to: Int
    ): GradientDrawable {
        val gd = GradientDrawable(
            orientation,
            intArrayOf(
                ContextCompat.getColor(
                    context,
                    R.color.monitorColorBlack
                ), ContextCompat.getColor(
                    context,
                    to
                )
            )
        )
        gd.cornerRadius = 0f
        return gd
    }
}


data class MColor(val colorId: Int)