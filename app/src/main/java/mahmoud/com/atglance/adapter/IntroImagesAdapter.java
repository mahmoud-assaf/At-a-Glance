package mahmoud.com.atglance.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import mahmoud.com.atglance.R;

public class IntroImagesAdapter extends PagerAdapter {
	private Context context;
	private LayoutInflater layoutInflater;
	private Integer[] images = {R.drawable.intro1, R.drawable.intro2, R.drawable.intro3, R.drawable.intro4};
	
	public IntroImagesAdapter(Context context) {
		this.context = context;
	}
	
	@Override
	public int getCount() {
		return images.length;
	}
	
	@Override
	public Object instantiateItem(ViewGroup container, final int position) {
		
		layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = layoutInflater.inflate(R.layout.custom_intro, null);
		ImageView imageView = (ImageView) view.findViewById(R.id.imageView);
		imageView.setImageResource(images[position]);
		
		ViewPager vp = (ViewPager) container;
		vp.addView(view, 0);
		return view;
		
	}
	
	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		
		ViewPager vp = (ViewPager) container;
		View view = (View) object;
		vp.removeView(view);
		
	}
	
	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == object;
	}
}
