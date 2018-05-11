package hibernate.v2.testyourandroid.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import hibernate.v2.testyourandroid.R;
import hibernate.v2.testyourandroid.model.GridItem;

/**
 * Created by himphen on 24/5/16.
 */
public class GridItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

	private List<GridItem> mDataList;
	private ItemClickListener mListener;

	private Context mContext;

	public interface ItemClickListener {
		void onItemDetailClick(GridItem gridItem);
	}

	public GridItemAdapter(List<GridItem> mDataList, ItemClickListener mListener) {
		this.mDataList = mDataList;
		this.mListener = mListener;
	}

	@NonNull
	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		mContext = parent.getContext();

		View itemView;
		itemView = LayoutInflater.from(mContext).inflate(R.layout.grid_item_test, parent, false);
		return new ItemViewHolder(itemView);
	}

	@Override
	public void onBindViewHolder(@NonNull RecyclerView.ViewHolder rawHolder, int position) {
		GridItem item = mDataList.get(position);
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
				mListener.onItemDetailClick((GridItem) v.getTag());
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
		@BindView(R.id.mainIv)
		ImageView mainIv;
		@BindView(R.id.mainTv)
		TextView mainTv;

		public ItemViewHolder(View itemView) {
			super(itemView);
			ButterKnife.bind(this, itemView);
		}
	}

}
