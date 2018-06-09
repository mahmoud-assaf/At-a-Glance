package mahmoud.com.atglance.fragment;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import mahmoud.com.atglance.R;
import mahmoud.com.atglance.model.SourcesResponse;
import mahmoud.com.atglance.model.SubSource;
import mahmoud.com.atglance.rest.ApiService;
import mahmoud.com.atglance.rest.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SearchFragment.OnFragmentSearchListener} interface
 * to handle interaction events.
 */
public class SearchFragment extends Fragment implements View.OnClickListener {
	
	private static final int FROM_DATE = 1;
	private static final int TO_DATE = 2;
	
	Button btnSearch;
	EditText searchEdit;
	TextView sourcesTxtView, dateFromTxtView, DateToTxtView;
	Spinner languageSpinner, sortBySpinner;
	String language = "";
	String sources = "";
	String query = "";
	String fromDate = "", toDate = "";
	String sortBy = "publishedAt";
	CheckBox customeDate;
	boolean withCustomeDate = false;
	View customDateView;
	String[] languagesArray;
	String[] languagesCodesArray;
	String[] sortByArray, sortByArraykeys = {"publishedat", "relevancy", "popularity"};
	String[] sourcesNamesArray;
	String selectedSourcesText = "";
	
	List<String> sourcesNamesDisplayArray = new ArrayList<>();
	String selectedSourcesDisplay = "";
	List<SubSource> sourcesList;
	List<String> sourcesIDs = new ArrayList<>();
	int counter = 0;
	List<String> customeSources = new ArrayList<>();
	List<String> selectedSources = new ArrayList<>();
	HashMap<String, String> searchParams = new HashMap<>();
	ApiService apiService;
	ProgressBar bar;
	private int mYear, mMonth, mDay;
	private OnFragmentSearchListener mListener;
	
	public SearchFragment() {
		// Required empty public constructor
	}
	
	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		if (context instanceof OnFragmentSearchListener) {
			mListener = (OnFragmentSearchListener) context;
		} else {
			throw new RuntimeException(context.toString()
				+ " must implement OnFragmentInteractionListener");
		}
	}
	
	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
		
		apiService = RetrofitClient.getClient().create(ApiService.class);
		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View v = inflater.inflate(R.layout.fragment_search, container, false);
		getActivity().setTitle(R.string.advanced_search);
		btnSearch = v.findViewById(R.id.btn_search);
		btnSearch.setOnClickListener(this);
		searchEdit = v.findViewById(R.id.editText_search);
		customDateView = v.findViewById(R.id.custom_date_view);
		
		customeDate = v.findViewById(R.id.checkboxDate);
		customeDate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				customDateView.setVisibility(isChecked ? View.VISIBLE : View.GONE);
				withCustomeDate = isChecked;
			}
		});
		languagesArray = getActivity().getResources().getStringArray(R.array.language_names);
		languagesCodesArray = getActivity().getResources().getStringArray(R.array.languages_code);
		
		sortByArray = getActivity().getResources().getStringArray(R.array.sort_options);
		
		languageSpinner = v.findViewById(R.id.spinner_language);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
			R.array.language_names, android.R.layout.simple_spinner_item);
		// Specify the layout to use when the list of choices appears
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		languageSpinner.setAdapter(adapter);
		
		sortBySpinner = v.findViewById(R.id.spinner_sort);
		ArrayAdapter<CharSequence> adapterSort = ArrayAdapter.createFromResource(getActivity(),
			R.array.sort_options, android.R.layout.simple_spinner_item);
		// Specify the layout to use when the list of choices appears
		adapterSort.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		sortBySpinner.setAdapter(adapterSort);
		
		dateFromTxtView = v.findViewById(R.id.txtview_from);
		dateFromTxtView.setOnClickListener(this);
		DateToTxtView = v.findViewById(R.id.txtview_to);
		DateToTxtView.setOnClickListener(this);
		
		sourcesTxtView = v.findViewById(R.id.textView_sources);
		sourcesTxtView.setOnClickListener(this);
		
		bar = v.findViewById(R.id.progressBar);
		return v;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		if (!selectedSourcesDisplay.equals(""))
			sourcesTxtView.setText(selectedSourcesDisplay);
		
		if (!fromDate.equals(""))
			dateFromTxtView.setText(fromDate);
		
		if (!toDate.equals(""))
			DateToTxtView.setText(toDate);
	}
	
	@Override
	public void onDetach() {
		super.onDetach();
		mListener = null;
	}
	
	private void getAndShowSources() {
		if (sourcesList == null) {
			bar.setVisibility(View.VISIBLE);
			Call<SourcesResponse> call;
			
			call = apiService.getSources();
			
			call.enqueue(new Callback<SourcesResponse>() {
				@Override
				public void onResponse(Call<SourcesResponse> call, Response<SourcesResponse> response) {
					
					//Log.e("code",String.valueOf(response.code()));
					
					if (response.code() > 200) {
						Log.e("ERROR code", response.toString());
						
						return;
					}
					sourcesList = response.body().getSources();
					//prepare arrays and show dialog
					sourcesNamesArray = new String[sourcesList.size()];
					for (int i = 0; i < sourcesList.size(); i++) {
						sourcesNamesArray[i] = sourcesList.get(i).getName();
						sourcesIDs.add(i, sourcesList.get(i).getId());
					}
					bar.setVisibility(View.INVISIBLE);
					showSourcesDialog();
				}
				
				@Override
				public void onFailure(Call<SourcesResponse> call, Throwable t) {
					// Log error here since request failed
					Log.e("ERROR", t.toString());
					Toast.makeText(getActivity(), R.string.something_wrong_sources, Toast.LENGTH_SHORT).show();
					bar.setVisibility(View.INVISIBLE);
					
				}
			});
		} else {
			showSourcesDialog();
		}
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.btn_search:
				Log.e(" query =: ", searchEdit.getText().toString());
				if (searchEdit.getText().toString().isEmpty()) {
					searchEdit.setError(getString(R.string.search_edit_error));
					//Toast.makeText(getActivity(),"Please enter your search words/phrase" , Toast.LENGTH_SHORT).show();
					return;
				}
				try {
					query = URLEncoder.encode(searchEdit.getText().toString(), "utf-8");
				} catch (UnsupportedEncodingException e) {
					Toast.makeText(getActivity(), R.string.error_search_keywords, Toast.LENGTH_SHORT).show();
					
					e.printStackTrace();
					return;
				}
				//String searchQuery=
				//	mListener
				Log.e(" quuuery =: ", query);
				searchParams.put("q", query);
				searchParams.put("sources", selectedSourcesText);
				searchParams.put("from", (withCustomeDate) ? fromDate : "");
				searchParams.put("to", (withCustomeDate) ? toDate : "");
				
				int language_spinner_pos = languageSpinner.getSelectedItemPosition();
				searchParams.put("language", languagesCodesArray[language_spinner_pos]);
				
				int sortby_spinner_pos = sortBySpinner.getSelectedItemPosition();
				searchParams.put("sortBy", sortByArraykeys[sortby_spinner_pos]);
				if (mListener != null) {
					
					mListener.onFragmentSearch(searchParams);
				}
				break;
			
			case R.id.txtview_from:
				showDateDialog(FROM_DATE);
				break;
			
			case R.id.txtview_to:
				showDateDialog(TO_DATE);
				
				break;
			
			case R.id.textView_sources:
				getAndShowSources();
				break;
			
		}
		
	}
	
	public void showDateDialog(final int date) {
		
		final Calendar c = Calendar.getInstance();
		mYear = c.get(Calendar.YEAR);
		mMonth = c.get(Calendar.MONTH);
		mDay = c.get(Calendar.DAY_OF_MONTH);
		
		DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
			new DatePickerDialog.OnDateSetListener() {
				
				@Override
				public void onDateSet(DatePicker view, int year,
				                      int monthOfYear, int dayOfMonth) {
					
					if (date == FROM_DATE) {
						fromDate = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
						dateFromTxtView.setText(fromDate);
						
					} else {
						
						toDate = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
						DateToTxtView.setText(toDate);
						
					}
					
				}
			}, mYear, mMonth, mDay);
		datePickerDialog.show();
		
	}
	
	public void showSourcesDialog() {
		selectedSourcesText = "";
		selectedSources.clear();
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setMultiChoiceItems(sourcesNamesArray, null, new DialogInterface.OnMultiChoiceClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which, boolean isChecked) {
				
				if (isChecked) {
					// If the user checked the item, add it to the selected items
					if (counter > 20) {
						Toast.makeText(getActivity(), "Maximum 20 sources", Toast.LENGTH_SHORT).show();
						return;
					}
					selectedSources.add(sourcesIDs.get(which));
					sourcesNamesDisplayArray.add(sourcesNamesArray[which]);
					counter += 1;
				} else if (selectedSources.contains(sourcesIDs.get(which))) {
					// Else, if the item is already in the array, remove it
					selectedSources.remove(sourcesIDs.get(which));
					sourcesNamesDisplayArray.remove(sourcesNamesArray[which]);
					counter -= 1;
				}
				Log.e(" counter = : ", String.valueOf(counter));
			}
			
		});
		
		// Specify the dialog is not cancelable
		builder.setCancelable(true);
		
		// Set a title for alert dialog
		builder.setTitle("Choose specific sources");
		
		// Set the positive/yes button click listener
		builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// Do something when click positive button
				Log.e(" ok sources: ", sourcesNamesDisplayArray.toString());
				int i=0;
				for (String src : sourcesNamesDisplayArray) {
					selectedSourcesDisplay += "-" + src;
					selectedSourcesText+=","+selectedSources.get(i);
					i++;
					
				}
				sourcesTxtView.setText(selectedSourcesDisplay);
				dialog.dismiss();
				
			}
		});
		
		builder.setNegativeButton(R.string.clear_all, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// Do something when click the negative button
				selectedSourcesText = "";
				
				selectedSources.clear();
				sourcesTxtView.setText(selectedSourcesText);
				counter = 0;
				
			}
		});
		
		// Set the neutral/cancel button click listener
		builder.setNeutralButton(R.string.cancel, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// Do something when click the neutral button
				dialog.dismiss();
			}
		});
		
		AlertDialog dialog = builder.create();
		// Display the alert dialog on interface
		dialog.show();
	}
	
	/**
	 * This interface must be implemented by activities that contain this
	 * fragment to allow an interaction in this fragment to be communicated
	 * to the activity and potentially other fragments contained in that
	 * activity.
	 * <p>
	 * See the Android Training lesson <a href=
	 * "http://developer.android.com/training/basics/fragments/communicating.html"
	 * >Communicating with Other Fragments</a> for more information.
	 */
	public interface OnFragmentSearchListener {
		// TODO: Update argument type and name
		void onFragmentSearch(HashMap<String, String> searchParams);
	}
}
