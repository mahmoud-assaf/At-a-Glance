package mahmoud.com.atglance.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.List;

import mahmoud.com.atglance.Preferences;
import mahmoud.com.atglance.R;
import mahmoud.com.atglance.activity.SourcesRecyclerViewClickInterface;
import mahmoud.com.atglance.model.SubSource;

public class SourcesAdapter extends RecyclerView.Adapter<SourcesAdapter.SourceViewHolder> {
	private final Context context;
	SourcesRecyclerViewClickInterface mListner;
	String[] savedSources;
	Preferences preferences;
	private List<SubSource> items;

	public SourcesAdapter(Context context, List<SubSource> items) {
		this.items = items;
		this.context = context;
		preferences = Preferences.getInstance(context);
		savedSources = preferences.getKey("saved_sources").split(",");

	}

	@Override
	public SourceViewHolder onCreateViewHolder(ViewGroup parent,
	                                           int viewType) {
		View v = LayoutInflater.from(parent.getContext())
			.inflate(R.layout.source_item, parent, false);
		return new SourceViewHolder(v);
	}

	@Override
	public void onBindViewHolder(final SourceViewHolder holder, final int position) {
		SubSource item = items.get(position);
		//TODO Fill in your logic for binding the view.
		//holder.txtview.settext....
		holder.sourceName.setText(item.getName());
		holder.sourceDescription.setText(item.getDescription());
		holder.sourceCheckbox.setOnCheckedChangeListener(null);

		holder.sourceCheckbox.setChecked(items.get(position).isSelected());

		holder.sourceCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				holder.sourceCheckbox.setSelected(!isChecked);
				if (mListner != null) {
					if (isChecked) {

						items.get(position).setSelected(true);
						Log.e(" adapter: ", "checked " + position);
						mListner.onSourceChecked(holder.getAdapterPosition());
					} else {
						Log.e(" adapter: ", "un checked " + position);
						items.get(position).setSelected(false);
						mListner.onSourceUnChecked(position);

					}
				}
			}
		});
	}

	@Override
	public int getItemCount() {
		if (items == null) {
			return 0;
		}
		return items.size();
	}

	public void setListner(SourcesRecyclerViewClickInterface listner) {
		this.mListner = listner;
	}

	public static class SourceViewHolder extends RecyclerView.ViewHolder {

		public TextView sourceName, sourceDescription;
		CheckBox sourceCheckbox;

		public SourceViewHolder(View itemView) {
			super(itemView);

			sourceName = (TextView) itemView.findViewById(R.id.textView_source_name);
			sourceDescription = (TextView) itemView.findViewById(R.id.textView_src_description);
			sourceCheckbox = itemView.findViewById(R.id.checkBox_source);

		}
	}
}