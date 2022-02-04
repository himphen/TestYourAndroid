package hibernate.v2.testyourandroid.ui.info

import android.app.ActivityManager
import android.content.Context
import android.content.pm.ConfigurationInfo
import android.opengl.GLSurfaceView
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import hibernate.v2.testyourandroid.R
import hibernate.v2.testyourandroid.databinding.FragmentInfoOpenglListviewBinding
import hibernate.v2.testyourandroid.model.InfoItem
import hibernate.v2.testyourandroid.ui.base.BaseFragment
import hibernate.v2.testyourandroid.ui.base.InfoItemAdapter
import hibernate.v2.testyourandroid.util.Utils.logException
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class InfoOpenGLFragment : BaseFragment<FragmentInfoOpenglListviewBinding>() {

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): FragmentInfoOpenglListviewBinding =
        FragmentInfoOpenglListviewBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private lateinit var adapter: InfoItemAdapter
    private lateinit var list: List<InfoItem>

    private fun init() {
        viewBinding!!.mGLView.setEGLContextClientVersion(2)
        viewBinding!!.mGLView.setRenderer(object : GLSurfaceView.Renderer {
            override fun onSurfaceCreated(gl: GL10, config: EGLConfig) {
                list[3].contentText = gl.glGetString(GL10.GL_RENDERER)
                list[4].contentText = gl.glGetString(GL10.GL_VENDOR)
                list[5].contentText = gl.glGetString(GL10.GL_VERSION)
                list[6].contentText = gl.glGetString(GL10.GL_SUBPIXEL_BITS)
                list[7].contentText = gl.glGetString(GL10.GL_COLOR_BUFFER_BIT)
                list[8].contentText = gl.glGetString(GL10.GL_DEPTH_BITS)
                list[9].contentText = gl.glGetString(GL10.GL_MAX_TEXTURE_UNITS)
                list[10].contentText = gl.glGetString(GL10.GL_MAX_TEXTURE_SIZE)
                list[11].contentText = gl.glGetString(GL10.GL_MAX_TEXTURE_STACK_DEPTH)
                list[12].contentText = gl.glGetString(GL10.GL_EXTENSIONS).replace(" ", "\n")

                activity?.runOnUiThread {
                    adapter.submitList(list)
                    viewBinding?.root?.removeView(viewBinding?.mGLView)
                }
            }

            override fun onSurfaceChanged(gl: GL10, width: Int, height: Int) {
            }

            override fun onDrawFrame(gl: GL10) {
            }
        })

        val stringArray = resources.getStringArray(R.array.info_open_gl_string_array)
        list = stringArray.mapIndexed { index, s -> InfoItem(s, getData(index)) }
        adapter = InfoItemAdapter()
        viewBinding!!.rvlist.adapter = adapter
        adapter.submitList(list)
    }

    private fun getData(j: Int): String {
        return try {
            when (j) {
                0 -> if (getGlEsVersion() >= 0x00020000) "Supported" else "Not Supported"
                1 -> if (getGlEsVersion() >= 0x00030000) "Supported" else "Not Supported"
                2 -> if (getGlEsVersion() >= 0x00030001) "Supported" else "Not Supported"
                else -> "N/A"
            }
        } catch (e: Exception) {
            logException(e, false)
            "N/A"
        }
    }

    private fun getGlEsVersion(): Int {
        val activityManager =
            context?.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager ?: return 1
        val configInfo = activityManager.deviceConfigurationInfo
        return if (configInfo.reqGlEsVersion != ConfigurationInfo.GL_ES_VERSION_UNDEFINED) {
            configInfo.reqGlEsVersion
        } else {
            1 shl 16 // Lack of property means OpenGL ES version 1
        }
    }
}
