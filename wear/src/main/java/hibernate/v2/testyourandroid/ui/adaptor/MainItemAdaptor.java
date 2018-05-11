package hibernate.v2.testyourandroid.ui.adaptor;

import android.content.Context;
import android.support.wearable.view.WearableListView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import hibernate.v2.testyourandroid.R;
import hibernate.v2.testyourandroid.model.MainItem;

/**
 * Created by himphen on 25/5/16.
 */
public class MainItemAdaptor extends WearableListView.Adapter {

	private List<MainItem> mDataList;
	private ItemClickListener mListener;

	public interface ItemClickListener {
		void onItemDetailClick(MainItem item);
	}

	public MainItemAdaptor(List<MainItem> mDataList, ItemClickListener mListener) {
		this.mDataList = mDataList;
		this.mListener = mListener;
	}

	@Override
	public WearableListView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		Context context = parent.getContext();

		View itemView = LayoutInflater.from(context).inflate(R.layout.list_item_main, parent, false);
		return new ItemViewHolder(itemView);
	}

	@Override
	public void onBindViewHolder(WearableListView.ViewHolder rawHolder, int position) {
		MainItem item = mDataList.get(position);
		ItemViewHolder holder = (ItemViewHolder) rawHolder;
		Glide.with(holder.mainIv.getContext())
				.load(item.getMainImageId())
				.apply(new RequestOptions()
						.fitCenter()
						.diskCacheStrategy(DiskCacheStrategy.ALL))
				.into(holder.mainIv);

		holder.mainTv.setText(item.getMainText());

		holder.rootView.setTag(item);
		holder.rootView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mListener.onItemDetailClick((MainItem) v.getTag());
			}
		});
	}

	@Override
	public int getItemCount() {
		return mDataList.size();
	}

	static class ItemViewHolder extends WearableListView.ViewHolder {
		LinearLayout rootView;
		ImageView mainIv;
		TextView mainTv;

		ItemViewHolder(View itemView) {
			super(itemView);
			rootView = itemView.findViewById(R.id.root_view);
			mainIv = itemView.findViewById(R.id.mainIv);
			mainTv = itemView.findViewById(R.id.mainTv);
		}
	}
}