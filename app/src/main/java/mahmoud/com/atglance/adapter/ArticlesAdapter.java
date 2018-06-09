package mahmoud.com.atglance.adapter;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import mahmoud.com.atglance.R;
import mahmoud.com.atglance.activity.ArticleViewActivity;
import mahmoud.com.atglance.model.Article;

public class ArticlesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
	public final static int TYPE_HEADER = 1;
	public final static int TYPE_NORMAL = 2;
	private final Context context;
	private ArrayList<Article> items;
	
	public ArticlesAdapter(Context context, ArrayList<Article> items) {
		this.items = items;
		this.context = context;
		
	}
	
	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
		RecyclerView.ViewHolder viewHolder;
		LayoutInflater inflater = LayoutInflater.from(parent.getContext());
		switch (viewType) {
			case TYPE_NORMAL:
				
				View v1 = inflater.inflate(R.layout.headline_item, parent, false);
				viewHolder = new ArticleViewHolder(v1);
				
				break;
			case TYPE_HEADER:
				View v2 = inflater.inflate(R.layout.headline_item_header, parent, false);
				viewHolder = new ArticleViewHolderHeader(v2);
				break;
			default:
				View v3 = inflater.inflate(R.layout.headline_item, parent, false);
				viewHolder = new ArticleViewHolder(v3);
			
		}
		return viewHolder;
	}
	
	@Override
	public void onBindViewHolder(RecyclerView.ViewHolder holder1, final int position) {
		Article item = items.get(position);
		RecyclerView.ViewHolder holder;
		//TODO Fill in your logic for binding the view.
		switch (holder1.getItemViewType()) {
			case TYPE_NORMAL:
				ArticleViewHolder vh1 = (ArticleViewHolder) holder1;
				configureViewHolderNormal(vh1, position);
				break;
			case TYPE_HEADER:
				ArticleViewHolderHeader vh2 = (ArticleViewHolderHeader) holder1;
				configureViewHolderHeader(vh2, position);
				
				break;
			
		}
	}
	
	@Override
	public int getItemViewType(int position) {
		if (position == 0)
			return TYPE_HEADER;
		else
			return TYPE_NORMAL;
	}
	
	@Override
	public int getItemCount() {
		if (items == null) {
			return 0;
		}
		return items.size();
	}
	
	private void configureViewHolderHeader(ArticleViewHolderHeader holder, final int position) {
		Article item = items.get(position);
		
		holder.title.setText(escapeHtml(item.getTitle()));
		holder.source.setText(item.getSource().getName());
		
		holder.publishDate.setText(item.getPublishedAt().substring(0, 10));
		String url = item.getUrlToImage();
		
		if (url != null && !url.isEmpty()) {
			
			Picasso.with(context)
				
				.load(url)
				.placeholder(R.drawable.ic_loading)
				
				.resize(300, 200)         //avoid outofmemoryexception
				
				.into(holder.image);
			holder.image.setContentDescription(item.getTitle());
		}
		holder.cardView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.e("clicked", String.valueOf(position));
				
				if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
					ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation((Activity) context);
					Intent intent = new Intent(context, ArticleViewActivity.class);
					intent.putExtra("article", items.get(position));
					context.startActivity(intent, options.toBundle());
					
				} else {
					Intent intent = new Intent(context, ArticleViewActivity.class);
					intent.putExtra("article", items.get(position));
					context.startActivity(intent);
				}
				
			}
		});
		
	}
	
	private void configureViewHolderNormal(ArticleViewHolder holder, final int position) {
		Article item = items.get(position);
		
		holder.title.setText(escapeHtml(item.getTitle()));
		holder.source.setText(item.getSource().getName());
		holder.description.setText(escapeHtml(item.getDescription()));
		holder.publishDate.setText(item.getPublishedAt().substring(0, 10));
		String url = item.getUrlToImage();
		
		if (url != null && !url.isEmpty()) {
			
			Picasso.with(context)
				
				.load(url)
				.placeholder(R.drawable.ic_loading)
				
				.resize(120, 100)
				.centerCrop()
				.into(holder.image);
			holder.image.setContentDescription(item.getTitle());
		}
		holder.cardView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.e("clicked", String.valueOf(position));
				Intent intent = new Intent(context, ArticleViewActivity.class);
				intent.putExtra("article", items.get(position));
				context.startActivity(intent);
			}
		});
		
	}
	
	public String escapeHtml(String html) {
		if (html == null || html.equals(""))
			return "";
		html = html.replaceAll("<(.*?)\\>", " ");//Removes all items in brackets
		html = html.replaceAll("<(.*?)\\\n", " ");//Must be undeneath
		html = html.replaceFirst("(.*?)\\>", " ");//Removes any connected item to the last bracket
		html = html.replaceAll("&nbsp;", " ");
		html = html.replaceAll("&amp;", " ");
		return html;
		
	}
	
	public void setArticles(ArrayList<Article> items) {
		this.items = items;
		notifyDataSetChanged();
	}
	
	public static class ArticleViewHolder extends RecyclerView.ViewHolder {
		private ConstraintLayout cardView;
		private TextView title;
		private TextView source;
		private TextView description;
		private TextView publishDate;
		private ImageView image;
		
		public ArticleViewHolder(View itemView) {
			super(itemView);
			cardView = (ConstraintLayout) itemView.findViewById(R.id.itemview);
			title = (TextView) itemView.findViewById(R.id.title);
			source = (TextView) itemView.findViewById(R.id.source);
			description = (TextView) itemView.findViewById(R.id.description);
			publishDate = (TextView) itemView.findViewById(R.id.publishedat);
			image = (ImageView) itemView.findViewById(R.id.image);
		}
	}
	
	public static class ArticleViewHolderHeader extends RecyclerView.ViewHolder {
		private FrameLayout cardView;
		private TextView title;
		private TextView source;
		
		private TextView publishDate;
		private ImageView image;
		
		public ArticleViewHolderHeader(View itemView) {
			super(itemView);
			cardView = (FrameLayout) itemView.findViewById(R.id.itemview);
			title = (TextView) itemView.findViewById(R.id.title);
			source = (TextView) itemView.findViewById(R.id.source);
			
			publishDate = (TextView) itemView.findViewById(R.id.publishedat);
			image = (ImageView) itemView.findViewById(R.id.image);
		}
	}
}