package org.doff.meizubbs.adapters;

import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;

public class PagerAdapter extends FragmentPagerAdapter {
	List<View> mListViews;

	public PagerAdapter(FragmentManager fm) {
		super(fm);
	}

	public PagerAdapter(FragmentManager fm, List<View> mListViews) {
		super(fm);
		this.mListViews = mListViews;
	}

	@Override
	public Fragment getItem(int position) {
		// getItem is called to instantiate the fragment for the given page.
		// Return a DummySectionFragment (defined as a static inner class
		// below) with the page number as its lone argument.
		Fragment fragment = new DummySectionFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_SECTION_NUMBER, position);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public int getCount() {
		// Show 3 total pages.
		return mListViews.size();
	}

	final String ARG_SECTION_NUMBER = "section_number";

	/**
	 * A dummy fragment representing a section of the app, but that simply
	 * displays dummy text.
	 */
	public class DummySectionFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */

		ListAdapter la;

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			Bundle bundle = getArguments();

			int page = bundle.getInt(ARG_SECTION_NUMBER);
			//System.out.println(page + "ARG_SECTION_NUMBER");
			return mListViews.get(page);

		}

	}

}
