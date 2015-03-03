package com.cylinder.www.ui;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cylinder.www.env.Mode;
import com.cylinder.www.env.Signal;
import com.cylinder.www.env.font.InterfaceTypeface;
import com.cylinder.www.env.font.InterfaceTypefaceCreator;
import com.cylinder.www.env.font.XKTypefaceCreator;
import com.cylinder.www.env.person.businessobject.Donor;
import com.cylinder.www.thread.ObservableLaunchActivityListenerThread;

import java.lang.ref.WeakReference;
import java.util.Locale;
import java.util.Observable;


public class LaunchActivity extends Activity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v13.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;
    static InterfaceTypeface XKface;
    InterfaceTypefaceCreator typefaceCreator;
    Donor donor=Donor.getInstance();
    ObservableLaunchActivityListenerThread observableLaunchActivityListenerThread = null;
    ObserverLaunchHandler observerLaunchHandler = new ObserverLaunchHandler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Mode.isDebug) {
            Log.e("ERROR", "MainActivity=onCreate");
        }

        setContentView(R.layout.activity_launch);

        // Given the typeface, we should construct a factory pattern for these type face.
        typefaceCreator = new XKTypefaceCreator();
        XKface = typefaceCreator.createTypeface(this);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.vp_fragment_container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // Start launch listener thread.
        observableLaunchActivityListenerThread = new ObservableLaunchActivityListenerThread(donor);
        observableLaunchActivityListenerThread.addObserver(observerLaunchHandler);
        observableLaunchActivityListenerThread.start();

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (Mode.isDebug) {
            Log.e("ERROR", "MainActivity=onStart");
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (Mode.isDebug) {
            Log.e("ERROR", "MainActivity=onRestart");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Mode.isDebug) {
            Log.e("ERROR", "MainActivity=onResume");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (Mode.isDebug) {
            Log.e("ERROR", "MainActivity=onPause");
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (Mode.isDebug) {
            Log.e("ERROR", "MainActivity=onStop");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (Mode.isDebug) {
            Log.e("ERROR", "MainActivity=onDestroy");
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            return  true;
        }
        return  super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int itemId = item.getItemId();
        switch (itemId) {
            case R.id.making_pairs:
                return true;
            case R.id.network_settings:
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Log.e("vpvp","position="+position);
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.

            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_section1).toUpperCase(l);
                case 1:
                    return getString(R.string.title_section2).toUpperCase(l);
                case 2:
                    return getString(R.string.title_section3).toUpperCase(l);
            }
            return null;
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {


        private TextView textViewWelcome;
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            Log.e("vpvp","sectionNumber="+sectionNumber);

            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_launch, container, false);

            // Set the typeface of the textViewWelcome.
            textViewWelcome = (TextView) rootView.findViewById(R.id.tv_slogan);
            textViewWelcome.setTypeface(XKface.getTypeface());

            return rootView;
        }
    }

    private class ObserverLaunchHandler implements java.util.Observer{

        // Avoiding memory leaks, we use reference Activity weakly.
        WeakReference<Activity> wa = new WeakReference<Activity>(LaunchActivity.this);

        @Override
        public void update(Observable observable, Object data) {

            switch ((Signal)data){
            case CONFIRM:
                wa.get().finish();
                Intent intent = new Intent(wa.get(), MainActivity.class);
                wa.get().startActivity(intent);
                break;
            default:
            }
        }
    }

}
