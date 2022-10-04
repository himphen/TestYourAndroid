package hibernate.v2.testyourandroid.ui.appinfo

import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import hibernate.v2.testyourandroid.R
import hibernate.v2.testyourandroid.databinding.FragmentInfoListviewBinding
import hibernate.v2.testyourandroid.model.AppItem
import hibernate.v2.testyourandroid.model.GridItem
import hibernate.v2.testyourandroid.ui.appinfo.AppInfoFragment.Companion.ARG_APP
import hibernate.v2.testyourandroid.ui.base.BaseFragment
import hibernate.v2.testyourandroid.ui.base.GridItemAdapter
import hibernate.v2.testyourandroid.util.Utils
import hibernate.v2.testyourandroid.util.Utils.notAppFound

class AppInfoActionFragment : BaseFragment<FragmentInfoListviewBinding>() {

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): FragmentInfoListviewBinding =
        FragmentInfoListviewBinding.inflate(inflater, container, false)

    private val imageArray = arrayListOf(
        R.drawable.app_open,
        R.drawable.app_uninstall,
        R.drawable.app_settings,
        R.drawable.app_play_store
    )
    private val actionArray = arrayListOf(
        GridItem.Action.APP_INFO_OPEN,
        GridItem.Action.APP_INFO_UNINSTALL,
        GridItem.Action.APP_INFO_SETTINGS,
        GridItem.Action.APP_INFO_PLAY_STORE
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.getParcelable<AppItem>(ARG_APP)?.let { appItem ->
            val stringArray = resources.getStringArray(R.array.app_action_string_array)
            val list: MutableList<GridItem> = ArrayList()
            for (i in imageArray.indices) {
                list.add(
                    GridItem(
                        text = stringArray[i],
                        image = imageArray[i],
                        action = actionArray[i]
                    )
                )
            }
            val spanCount = 1
            var columnCount = 3
            val man = GridLayoutManager(activity, spanCount)
            man.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    return 1
                }
            }
            if (Utils.isTablet() && Utils.isLandscape(context)) {
                columnCount = 4
            }
            val mListener = object : GridItemAdapter.ItemClickListener {
                override fun onItemDetailClick(gridItem: GridItem) {
                    var intent: Intent?
                    when (gridItem.action) {
                        GridItem.Action.APP_INFO_UNINSTALL -> try {
                            intent = Intent(
                                Intent.ACTION_DELETE,
                                Uri.fromParts("package", appItem.packageName, null)
                            )
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(intent)
                        } catch (e: Exception) {
                            try {
                                intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                startActivity(intent)
                            } catch (e1: Exception) {
                                startActivity(Intent(Settings.ACTION_SETTINGS))
                            }
                        }
                        GridItem.Action.APP_INFO_SETTINGS -> try {
                            intent = Intent(
                                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                Uri.fromParts("package", appItem.packageName, null)
                            )
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(intent)
                        } catch (e: Exception) {
                            try {
                                val componentName = ComponentName(
                                    "com.android.settings",
                                    "com.android.settings.applications.InstalledAppDetails"
                                )
                                intent = Intent()
                                intent.action = "android.settings.APPLICATION_DETAILS_SETTINGS"
                                intent.data =
                                    Uri.fromParts("package", appItem.packageName, null)
                                intent.component = componentName
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                startActivity(intent)
                            } catch (e1: Exception) {
                                startActivity(Intent(Settings.ACTION_SETTINGS))
                            }
                        }
                        GridItem.Action.APP_INFO_PLAY_STORE -> {
                            intent = Intent(Intent.ACTION_VIEW)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            try {
                                intent.data =
                                    Uri.parse("market://details?id=" + appItem.packageName)
                                startActivity(intent)
                            } catch (e: ActivityNotFoundException) {
                                intent.data =
                                    Uri.parse("https://play.google.com/store/apps/details?id=" + appItem.packageName)
                                startActivity(intent)
                            }
                        }
                        GridItem.Action.APP_INFO_OPEN -> {
                            intent = context?.packageManager?.getLaunchIntentForPackage(
                                appItem.packageName
                            )
                            intent?.let {
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                startActivity(intent)
                            } ?: run {
                                notAppFound(activity, false)
                            }
                        }
                        else -> {
                        }
                    }
                }
            }
            viewBinding!!.rvlist.setHasFixedSize(true)
            viewBinding!!.rvlist.layoutManager = GridLayoutManager(context, columnCount)
            viewBinding!!.rvlist.adapter = GridItemAdapter(mListener).apply {
                submitList(list)
            }
        } ?: run {
            notAppFound(activity)
        }
    }

    companion object {
        fun newInstance(appItem: AppItem?) = AppInfoActionFragment().apply {
            arguments = Bundle().apply {
                putParcelable(ARG_APP, appItem)
            }
        }
    }
}
