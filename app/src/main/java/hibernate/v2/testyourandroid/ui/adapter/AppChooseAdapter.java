package hibernate.v2.testyourandroid.ui.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import hibernate.v2.testyourandroid.R;
import hibernate.v2.testyourandroid.model.AppChooseItem;

/**
 * Created by himphen on 25/5/16.
 */
public class AppChooseAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

	private List<AppChooseItem> mDataList;
	private ItemClickListener mListener;

	private Context mContext;

	public interface ItemClickListener {
		void onItemDetailClick(AppChooseItem catChoice);
	}

	public AppChooseAdapter(List<AppChooseItem> mDataList, ItemClickListener mListener) {
		this.mDataList = mDataList;
		this.mListener = mListener;
	}

	@NonNull
	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		mContext = parent.getContext();

		View itemView;
		itemView = LayoutInflater.from(mContext).inflate(R.layout.list_item_info, parent, false);
		return new ItemViewHolder(itemView);
	}

	@Override
	public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
		AppChooseItem item = mDataList.get(position);
		ItemViewHolder viewHolder = (ItemViewHolder) holder;

		viewHolder.titleTv.setText(item.getTitleText());
		viewHolder.contentTv.setText(item.getContentText());

		viewHolder.rootView.setTag(item);
		viewHolder.rootView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mListener.onItemDetailClick((AppChooseItem) v.getTag());
			}
		});
	}

	@Override
	public int getItemCount() {
		return mDataList.size();
	}

	static class ItemViewHolder extends RecyclerView.ViewHolder {
		@BindView(R.id.root_view)
		LinearLayout rootView;
		@BindView(R.id.text1)
		TextView titleTv;
		@BindView(R.id.text2)
		TextView contentTv;

		ItemViewHolder(View itemView) {
			super(itemView);
			ButterKnife.bind(this, itemView);
		}
	}
}