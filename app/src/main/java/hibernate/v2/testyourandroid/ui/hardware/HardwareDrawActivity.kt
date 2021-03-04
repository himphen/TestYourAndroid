package hibernate.v2.testyourandroid.ui.hardware

import android.content.Intent
import android.content.res.Resources
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.SeekBar
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import com.afollestad.materialdialogs.MaterialDialog
import hibernate.v2.draw.databinding.ColorPaletteViewBinding
import hibernate.v2.testyourandroid.R
import hibernate.v2.testyourandroid.databinding.ActivityDrawBinding
import hibernate.v2.testyourandroid.ui.base.BaseFragmentActivity
import hibernate.v2.testyourandroid.util.Utils
import java.io.ByteArrayOutputStream
import java.io.IOException

class HardwareDrawActivity : BaseFragmentActivity<ActivityDrawBinding>() {

    private val saveImageResult =
        registerForActivityResult(StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode != RESULT_OK) return@registerForActivityResult

            result.data?.data?.let { data ->
                try {
                    val bStream = ByteArrayOutputStream()
                    val bitmap = viewBinding.drawView.getBitmap()
                    bitmap.compress(Bitmap.CompressFormat.PNG, 90, bStream)
                    val byteArray = bStream.toByteArray()

                    contentResolver.openOutputStream(data)?.let { os ->
                        os.write(byteArray)
                        os.close()
                        Utils.snackbar(viewBinding.root, stringRid = R.string.ui_success)?.show()
                        showLeaveWarning = false
                    } ?: run {
                        Utils.snackbar(viewBinding.root, stringRid = R.string.ui_error)?.show()
                    }
                } catch (e: IOException) {
                    Utils.snackbar(viewBinding.root, stringRid = R.string.ui_error)?.show()
                }
            }
        }

    override fun getActivityViewBinding() = ActivityDrawBinding.inflate(layoutInflater)

    private var showLeaveWarning = false
    private lateinit var colorPaletteViewBinding: ColorPaletteViewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Utils.setFullScreen(window)

        colorPaletteViewBinding = viewBinding.drawColorPalette

        viewBinding.imageCloseDrawing.setOnClickListener {
            MaterialDialog(this)
                .message(R.string.dialog_drawing_cancel_message)
                .cancelable(false)
                .positiveButton(R.string.ui_okay) { finish() }
                .negativeButton(R.string.ui_cancel)
                .show()
        }
        viewBinding.fabSendDrawing.setOnClickListener {
            val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "image/png"
                putExtra(Intent.EXTRA_TITLE, "image.png")
            }

            saveImageResult.launch(intent)
        }

        setUpDrawTools()
        colorSelector()
        setPaintAlpha()
        setPaintWidth()
    }

    override fun onAttachedToWindow() {
        // set top padding if device has cutout
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
            window.decorView.rootWindowInsets.displayCutout?.let {
                viewBinding.viewStatusBarSpace.layoutParams =
                    ConstraintLayout.LayoutParams(
                        ConstraintLayout.LayoutParams.MATCH_PARENT,
                        it.safeInsetTop
                    )
            }
        }
    }

    private fun setUpDrawTools() {
        viewBinding.drawView.drawingCallback = { showLeaveWarning = true }
        viewBinding.imageDrawEraser.setOnClickListener {
            viewBinding.let { viewBinding ->
                viewBinding.drawView.clearCanvas()
                toggleDrawTools(viewBinding.drawTools, false)
                showLeaveWarning = true
            }
        }
        viewBinding.imageDrawWidth.setOnClickListener {
            if (viewBinding.drawTools.translationY == (56).toPx) {
                toggleDrawTools(viewBinding.drawTools, true)
            } else if (viewBinding.drawTools.translationY == (0).toPx &&
                viewBinding.seekBarWidth.visibility == View.VISIBLE
            ) {
                toggleDrawTools(viewBinding.drawTools, false)
            }
            viewBinding.seekBarWidth.visibility = View.VISIBLE
            viewBinding.seekBarOpacity.visibility = View.GONE
            viewBinding.drawColorPalette.root.visibility = View.GONE
        }
        viewBinding.imageDrawOpacity.setOnClickListener {
            if (viewBinding.drawTools.translationY == (56).toPx) {
                toggleDrawTools(viewBinding.drawTools, true)
            } else if (viewBinding.drawTools.translationY == (0).toPx &&
                viewBinding.seekBarOpacity.visibility == View.VISIBLE
            ) {
                toggleDrawTools(viewBinding.drawTools, false)
            }
            viewBinding.seekBarWidth.visibility = View.GONE
            viewBinding.seekBarOpacity.visibility = View.VISIBLE
            viewBinding.drawColorPalette.root.visibility = View.GONE
        }
        viewBinding.imageDrawColor.setOnClickListener {
            if (viewBinding.drawTools.translationY == (56).toPx) {
                toggleDrawTools(viewBinding.drawTools, true)
            } else if (viewBinding.drawTools.translationY == (0).toPx &&
                viewBinding.drawColorPalette.root.visibility == View.VISIBLE
            ) {
                toggleDrawTools(viewBinding.drawTools, false)
            }
            viewBinding.seekBarWidth.visibility = View.GONE
            viewBinding.seekBarOpacity.visibility = View.GONE
            viewBinding.drawColorPalette.root.visibility = View.VISIBLE
        }
        viewBinding.imageDrawUndo.setOnClickListener {
            viewBinding.drawView.undo()
            toggleDrawTools(viewBinding.drawTools, false)
            showLeaveWarning = true
        }
        viewBinding.imageDrawRedo.setOnClickListener {
            viewBinding.drawView.redo()
            toggleDrawTools(viewBinding.drawTools, false)
            showLeaveWarning = true
        }
    }

    private fun toggleDrawTools(view: View, showView: Boolean = true) {
        if (showView) {
            view.animate().translationY((0).toPx)
        } else {
            view.animate().translationY((56).toPx)
        }
    }

    private fun colorSelector() {
        colorPaletteViewBinding.imageColorBlack.setOnClickListener {
            viewBinding.drawView.setColor(
                ResourcesCompat.getColor(
                    resources,
                    R.color.color_black,
                    null
                )
            )
            scaleColorView(colorPaletteViewBinding.imageColorBlack)

        }
        colorPaletteViewBinding.imageColorRed.setOnClickListener {
            viewBinding.drawView.setColor(
                ResourcesCompat.getColor(
                    resources,
                    R.color.color_red,
                    null
                )
            )
            scaleColorView(colorPaletteViewBinding.imageColorRed)
        }
        colorPaletteViewBinding.imageColorYellow.setOnClickListener {
            viewBinding.drawView.setColor(
                ResourcesCompat.getColor(
                    resources,
                    R.color.color_yellow,
                    null
                )
            )
            scaleColorView(colorPaletteViewBinding.imageColorYellow)
        }
        colorPaletteViewBinding.imageColorGreen.setOnClickListener {
            viewBinding.drawView.setColor(
                ResourcesCompat.getColor(
                    resources,
                    R.color.color_green,
                    null
                )
            )
            scaleColorView(colorPaletteViewBinding.imageColorGreen)
        }
        colorPaletteViewBinding.imageColorBlue.setOnClickListener {
            viewBinding.drawView.setColor(
                ResourcesCompat.getColor(
                    resources,
                    R.color.color_blue,
                    null
                )
            )
            scaleColorView(colorPaletteViewBinding.imageColorBlue)
        }
        colorPaletteViewBinding.imageColorPink.setOnClickListener {
            viewBinding.drawView.setColor(
                ResourcesCompat.getColor(
                    resources,
                    R.color.color_pink,
                    null
                )
            )
            scaleColorView(colorPaletteViewBinding.imageColorPink)
        }
        colorPaletteViewBinding.imageColorBrown.setOnClickListener {
            viewBinding.drawView.setColor(
                ResourcesCompat.getColor(
                    resources,
                    R.color.color_brown,
                    null
                )
            )
            scaleColorView(colorPaletteViewBinding.imageColorBrown)
        }
    }

    private fun scaleColorView(view: View) {
        //reset scale of all views
        colorPaletteViewBinding.imageColorBlack.scaleX = 1f
        colorPaletteViewBinding.imageColorBlack.scaleY = 1f

        colorPaletteViewBinding.imageColorRed.scaleX = 1f
        colorPaletteViewBinding.imageColorRed.scaleY = 1f

        colorPaletteViewBinding.imageColorYellow.scaleX = 1f
        colorPaletteViewBinding.imageColorYellow.scaleY = 1f

        colorPaletteViewBinding.imageColorGreen.scaleX = 1f
        colorPaletteViewBinding.imageColorGreen.scaleY = 1f

        colorPaletteViewBinding.imageColorBlue.scaleX = 1f
        colorPaletteViewBinding.imageColorBlue.scaleY = 1f

        colorPaletteViewBinding.imageColorPink.scaleX = 1f
        colorPaletteViewBinding.imageColorPink.scaleY = 1f

        colorPaletteViewBinding.imageColorBrown.scaleX = 1f
        colorPaletteViewBinding.imageColorBrown.scaleY = 1f

        //set scale of selected view
        view.scaleX = 1.5f
        view.scaleY = 1.5f
    }

    private fun setPaintWidth() {
        viewBinding.seekBarWidth.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                viewBinding.drawView.setStrokeWidth(progress.toFloat())
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    private fun setPaintAlpha() {
        viewBinding.seekBarOpacity.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                viewBinding.drawView.setAlpha(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    private val Int.toPx: Float
        get() = (this * Resources.getSystem().displayMetrics.density)

    override fun onPause() {
        window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }
}