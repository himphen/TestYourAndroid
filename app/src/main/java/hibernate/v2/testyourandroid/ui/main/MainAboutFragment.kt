package hibernate.v2.testyourandroid.ui.main

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.blankj.utilcode.util.AppUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import de.psdev.licensesdialog.LicensesDialog
import hibernate.v2.testyourandroid.BuildConfig
import hibernate.v2.testyourandroid.R
import hibernate.v2.testyourandroid.helper.UtilHelper
import hibernate.v2.testyourandroid.ui.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_main_about.*

class MainAboutFragment : BaseFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_main_about, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        versionTv.text = String.format("%s %s", getString(R.string.ui_version), AppUtils.getAppVersionName())
        context?.let { context ->
            Glide.with(context)
                    .load(R.drawable.android_resources)
                    .apply(RequestOptions()
                            .diskCacheStrategy(DiskCacheStrategy.ALL))
                    .into(logoIv)
        }

        moreButton.setOnClickListener {
            try {
                val uri = Uri.parse("market://search?q=pub:\"Hibernate\"")
                val intent = Intent(Intent.ACTION_VIEW, uri)
                startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                UtilHelper.notAppFound(activity)
            }
        }

        shareButton.setOnClickListener {
            val text = getString(R.string.share_message) + "\n\nhttps://play.google.com/store/apps/details?id=hibernate.v2.testyourandroid"
            val intent = Intent(Intent.ACTION_SEND)
            intent.putExtra(Intent.EXTRA_TEXT, text)
            intent.type = "text/plain"
            startActivity(Intent.createChooser(intent, getString(R.string.share_button)))
        }

        feedbackButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND)
            var text = "Android Version: " + Build.VERSION.RELEASE + "\n"
            text += "SDK Level: " + Build.VERSION.SDK_INT + "\n"
            text += "Version: " + AppUtils.getAppVersionName() + "\n"
            text += "Brand: " + Build.BRAND + "\n"
            text += "Model: " + Build.MODEL + "\n\n\n"
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(BuildConfig.CONTACT_EMAIL))
            intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.feedback_title))
            intent.putExtra(Intent.EXTRA_TEXT, text)
            startActivity(intent)
        }
        creditButton.setOnClickListener {
            LicensesDialog.Builder(context)
                    .setNotices(R.raw.notices)
                    .setThemeResourceId(R.style.AppTheme_Dialog_License)
                    .build()
                    .show()
        }
    }
}