package mahmoud.com.atglance.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class ViewPageAdapter extends FragmentPagerAdapter {
	private final List<Fragment> mFragmentList = new ArrayList<Fragment>();
	private final List<String> mFragmentTitleList = new ArrayList<String>();
	
	public ViewPageAdapter(FragmentManager manager) {
		super(manager);
	}
	
	@Override
	public Fragment getItem(int position) {
		return mFragmentList.get(position);
	}
	
	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
	
	}
	
	@Override
	public int getCount() {
		return mFragmentList.size();
	}
	
	@Override
	public CharSequence getPageTitle(int position) {
		return mFragmentTitleList.get(position);
	}
	
	public void addFrag(Fragment fragment, String title) {
		mFragmentList.add(fragment);
		mFragmentTitleList.add(title);
	}
}
