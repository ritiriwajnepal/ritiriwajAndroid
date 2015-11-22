package co.ritiriwaj.android.helper;

import java.util.ArrayList;



import co.ritiriwaj.android.R;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

public class SimplePagerAdapterNoSide extends PagerAdapter {

	private final int[] ICONS = { R.drawable.ic_action_restore,
			R.drawable.ic_action_grade, R.drawable.ic_action_find_in_page,
			R.drawable.ic_social_school };

	ArrayList<View> views;

	public SimplePagerAdapterNoSide(ArrayList<View> views) {
		this.views = views;
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		((ViewPager) container).addView(views.get(position));
		return views.get(position);
	}

	@Override
	public int getCount() {
		return views.size();
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {

		return view == object;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		((ViewPager) container).removeView((View) object);
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return position + "";
	}

}