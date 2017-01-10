package hibernate.v2.testyourandroid.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import hibernate.v2.testyourandroid.R;
import hibernate.v2.testyourandroid.model.MainInfoItem;

/**
 * Created by himphen on 25/5/16.
 */
public class MainInfoItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

	private List<MainInfoItem> mDataList;
	private ItemClickListener mListener;

	public interface ItemClickListener {
		void onItemDetailClick(MainInfoItem catChoice);
	}

	public MainInfoItemAdapter(List<MainInfoItem> mDataList, ItemClickListener mListener) {
		this.mDataList = mDataList;
		this.mListener = mListener;
	}

	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		Context context = parent.getContext();

		View itemView = LayoutInflater.from(context).inflate(R.layout.list_item_simple, parent, false);
		return new ItemViewHolder(itemView);
	}

	@Override
	public void onBindViewHolder(RecyclerView.ViewHolder rawHolder, int position) {
		MainInfoItem item = mDataList.get(position);
		ItemViewHolder holder = (ItemViewHolder) rawHolder;

		holder.titleTv.setText(item.getMainText());

		holder.rootView.setTag(item);
		holder.rootView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mListener.onItemDetailClick((MainInfoItem) v.getTag());
			}
		});
	}

	@Override
	public int getItemCount() {
		if (mDataList == null)
			return 0;
		return this.mDataList.size();
	}

	@SuppressWarnings("WeakerAccess")
	class ItemViewHolder extends RecyclerView.ViewHolder {

		@BindView(R.id.root_view)
		LinearLayout rootView;
		@BindView(R.id.text1)
		TextView titleTv;

		ItemViewHolder(View itemView) {
			super(itemView);
			ButterKnife.bind(this, itemView);
		}
	}
}