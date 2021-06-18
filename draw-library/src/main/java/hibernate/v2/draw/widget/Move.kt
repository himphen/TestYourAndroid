package hibernate.v2.draw.widget

import android.graphics.Path
import java.io.Writer

class Move(private val x: Float, private val y: Float) : Action {

    override fun perform(path: Path) {
        path.moveTo(x, y)
    }

    override fun perform(writer: Writer) {
        writer.write("M$x,$y")
    }
}