package mahmoud.com.atglance.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import java.util.ArrayList;

import mahmoud.com.atglance.R;
import mahmoud.com.atglance.adapter.ArticlesAdapter;
import mahmoud.com.atglance.model.Article;
import mahmoud.com.atglance.provider.DBContract;

public class SavedArticlesFragment extends Fragment implements
	LoaderManager.LoaderCallbacks<Cursor> {

	// TODO: Rename and change types of parameters

	private static final int LOADER_ID = 0x01;
	public RecyclerView recyclerView;
	public ArrayList<Article> articles;
	ProgressBar bar;
	ArticlesAdapter adapter;
	private OnFragmentInteractionListener mListener;

	public SavedArticlesFragment() {
		// Required empty public constructor
	}

	// TODO: Rename and change types and number of parameters
	public static SavedArticlesFragment newInstance() {
		SavedArticlesFragment fragment = new SavedArticlesFragment();

		return fragment;
	}

	// TODO: Rename method, update argument and hook method into UI event
	public void onArticleClicked(Article article) {
		if (mListener != null) {
			mListener.onSavedArticelClick(article);
		}
	}

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
              /*  if (context instanceof OnFragmentInteractionListener) {
                        mListener = (OnFragmentInteractionListener) context;
                } else {
                        throw new RuntimeException(context.toString()
                                + " must implement OnFragmentInteractionListener");
                }*/
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View v = inflater.inflate(R.layout.fragment_saved_articles, container, false);
		recyclerView = v.findViewById(R.id.articles_rv);
		LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
		recyclerView.setLayoutManager(layoutManager);
		DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
			layoutManager.getOrientation());
		recyclerView.addItemDecoration(dividerItemDecoration);
		adapter = new ArticlesAdapter(getActivity(), new ArrayList<Article>());
		recyclerView.setAdapter(adapter);
		bar = v.findViewById(R.id.progressBar);

		ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
			@Override
			public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
				return false;
			}

			@Override
			public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction) {
				final int position = viewHolder.getAdapterPosition(); //get position which is swipe

				if (direction == ItemTouchHelper.LEFT || direction == ItemTouchHelper.RIGHT) {    //if swipe left

					AlertDialog.Builder builder = new AlertDialog.Builder(getActivity()); //alert for confirm to delete
					builder.setMessage(R.string.remove_article);    //set message

					builder.setPositiveButton(R.string.remove, new DialogInterface.OnClickListener() { //when click on DELETE
						@Override
						public void onClick(DialogInterface dialog, int which) {
							getActivity().getContentResolver().delete(DBContract.CONTENT_URI.buildUpon().appendPath(String.valueOf(articles.get(position).id)).build(),
								null, null);
							adapter.notifyItemRemoved(position);    //item removed from recylcerview
							//    sqldatabase.execSQL("delete from " + TABLE_NAME + " where _id='" + (position + 1) + "'"); //query for delete
							articles.remove(position);  //then remove item

							return;
						}
					}).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {  //not removing items if cancel is done
						@Override
						public void onClick(DialogInterface dialog, int which) {
							adapter.notifyItemRemoved(position + 1);    //notifies the RecyclerView Adapter that data in adapter has been removed at a particular position.
							adapter.notifyItemRangeChanged(position, adapter.getItemCount());   //notifies the RecyclerView Adapter that positions of element in adapter has been changed from position(removed element index to end of list), please update it.
							return;
						}
					})
						.setOnCancelListener(new DialogInterface.OnCancelListener() {
							@Override
							public void onCancel(DialogInterface dialog) {
								// stuff to put the item back
								adapter.notifyItemRemoved(position + 1);    //notifies the RecyclerView Adapter that data in adapter has been removed at a particular position.
								adapter.notifyItemRangeChanged(position, adapter.getItemCount());   //notifies the RecyclerView Adapter that positions of element in adapter has been changed from position(removed element index to end of list), please update it.
								return;

							}
						})

						.show();  //show alert dialog
				}
			}
		};
		ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
		itemTouchHelper.attachToRecyclerView(recyclerView); //set swipe to recylcerview
		return v;
	}

	@Override
	public void onResume() {
		super.onResume();
		getActivity().getSupportLoaderManager().initLoader(LOADER_ID, null, this);
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mListener = null;
	}

	public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

		return new CursorLoader(getActivity(),
			DBContract.CONTENT_URI
			, null, null, null, null);
	}

	public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
		articles = new ArrayList<Article>();
		cursor.moveToFirst();
		Article article;
		while (!cursor.isAfterLast()) {
			article = new Article(cursor.getString(cursor.getColumnIndex(DBContract.ARTICLE.ID)),
				cursor.getString(cursor.getColumnIndex(DBContract.ARTICLE.TITLE)),
				cursor.getString(cursor.getColumnIndex(DBContract.ARTICLE.DESCRIPTION)),
				cursor.getString(cursor.getColumnIndex(DBContract.ARTICLE.SOURCE)),
				cursor.getString(cursor.getColumnIndex(DBContract.ARTICLE.IMAGEURL)),
				cursor.getString(cursor.getColumnIndex(DBContract.ARTICLE.URL)),
				cursor.getString(cursor.getColumnIndex(DBContract.ARTICLE.PUBLISHEDAT)));

			articles.add(article);
			cursor.moveToNext();
		}
		adapter = new ArticlesAdapter(getActivity(), articles);
		adapter.notifyDataSetChanged();
		recyclerView.setAdapter(adapter);
		bar.setVisibility(View.INVISIBLE);

	}

	public void onLoaderReset(Loader<Cursor> cursorLoader) {

	}

	public void sayHi() {
		Log.e("Saved", "hiiiiiiiiii");
	}

	public ArrayList<Article> getArticles() {
		return articles;
	}

	public ArticlesAdapter getAdapter() {
		return adapter;
	}

	public interface OnFragmentInteractionListener {
		// TODO: Update argument type and name
		void onSavedArticelClick(Article article);
	}
}
