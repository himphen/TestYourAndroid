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
import com.blankj.utilcode.util.DeviceUtils
import com.blankj.utilcode.util.ScreenUtils
import hibernate.v2.testyourandroid.R
import hibernate.v2.testyourandroid.helper.UtilHelper.notAppFound
import hibernate.v2.testyourandroid.model.AppItem
import hibernate.v2.testyourandroid.model.GridItem
import hibernate.v2.testyourandroid.ui.base.GridItemAdapter
import hibernate.v2.testyourandroid.ui.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_info_listview.*
import java.util.ArrayList

class AppInfoActionFragment : BaseFragment() {
    private val imageArray = arrayOf(
            R.drawable.app_open, R.drawable.app_uninstall,
            R.drawable.app_settings, R.drawable.app_play_store)
    private val typeArray = arrayOf("open", "uninstall", "settings", "play_store")

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_info_listview, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        arguments?.getParcelable<AppItem>("APP")?.let { appItem ->
            val stringArray = resources.getStringArray(R.array.app_action_string_array)
            val imageList = arrayListOf(*imageArray)
            val list: MutableList<GridItem> = ArrayList()
            for (i in imageList.indices) {
                list.add(GridItem(stringArray[i], imageList[i], typeArray[i]))
            }
            val spanCount = 1
            var columnCount = 3
            val man = GridLayoutManager(activity, spanCount)
            man.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    return 1
                }
            }
            if (DeviceUtils.isTablet() && ScreenUtils.isLandscape()) {
                columnCount = 4
            }
            val mListener: GridItemAdapter.ItemClickListener = object : GridItemAdapter.ItemClickListener {
                override fun onItemDetailClick(gridItem: GridItem) {
                    var intent: Intent?
                    when (gridItem.actionType) {
                        "uninstall" -> try {
                            intent = Intent(Intent.ACTION_DELETE, Uri.fromParts("package", appItem.packageName, null))
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(intent)
                        } catch (e: Exception) {
                            intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(intent)
                        }
                        "settings" -> try {
                            intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                    Uri.fromParts("package", appItem.packageName, null)
                            )
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(intent)
                        } catch (e: Exception) {
                            try {
                                val componentName = ComponentName(
                                        "com.android.settings",
                                        "com.android.settings.applications.InstalledAppDetails")
                                intent = Intent()
                                intent.action = "android.settings.APPLICATION_DETAILS_SETTINGS"
                                intent.data = Uri.fromParts("package", appItem.packageName, null)
                                intent.component = componentName
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                startActivity(intent)
                            } catch (e1: Exception) {
                                startActivity(Intent(Settings.ACTION_SETTINGS))
                            }
                        }
                        "play_store" -> {
                            intent = Intent(Intent.ACTION_VIEW)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            try {
                                intent.data = Uri.parse("market://details?id=" + appItem.packageName)
                                startActivity(intent)
                            } catch (e: ActivityNotFoundException) {
                                intent.data = Uri.parse("https://play.google.com/store/apps/details?id=" + appItem.packageName)
                                startActivity(intent)
                            }
                        }
                        "open" -> {
                            appItem.packageName?.let { packageName ->
                                intent = context?.packageManager?.getLaunchIntentForPackage(packageName)
                                intent?.let { intent ->
                                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                    startActivity(intent)
                                } ?: run {
                                    notAppFound(activity, false)
                                }
                            }
                        }
                    }
                }
            }
            rvlist.setHasFixedSize(true)
            rvlist.layoutManager = GridLayoutManager(context, columnCount)
            rvlist.adapter = GridItemAdapter(list, mListener)
        } ?: run {
            notAppFound(activity)
        }
    }

    companion object {
        fun newInstance(appItem: AppItem?): AppInfoActionFragment {
            val fragment = AppInfoActionFragment()
            val args = Bundle()
            args.putParcelable("APP", appItem)
            fragment.arguments = args
            return fragment
        }
    }
}