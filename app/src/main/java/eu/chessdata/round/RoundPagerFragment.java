package eu.chessdata.round;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import eu.chessdata.R;
import eu.chessdata.TournamentDetailsFragment;
import eu.chessdata.tools.MyGlobalTools;

/**
 * Created by Bogdan Oloeriu on 27/03/2016.
 */
public class RoundPagerFragment extends Fragment {
    private static final String TAG = "my-debug-tag";
    public static final String ROUND_NUMBER = "roundPagerFragment.round.count";

    private ViewPager mViewPager;
    private String mStringTournamentUri;
    private String mTournamentName;
    private int mRoundCount;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_round_pager, container, false);

        mStringTournamentUri = getArguments().getString(TournamentDetailsFragment.TOURNAMENT_URI);
        mTournamentName = getArguments().getString(TournamentDetailsFragment.TOURNAMENT_NAME);

        mRoundCount = MyGlobalTools.getTournamentTotalRounds(getContext().getContentResolver(), mStringTournamentUri);
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());
        mViewPager = (ViewPager) fragmentView.findViewById(R.id.container_round_pager);
        mViewPager.setAdapter(sectionsPagerAdapter);
        mViewPager.setPageTransformer(true,new DepthPageTransformer());

        Log.d(TAG,"mRoundCount = " + mRoundCount);
        return fragmentView;
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentStatePagerAdapter {
        private final FragmentManager mFragmentManager;
        private Fragment firstFragment;

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
            mFragmentManager = fm;
        }

        @Override
        public Fragment getItem(int position) {
            return RoundStateFragment.newInstance(mStringTournamentUri, position+1,mFragmentManager);
        }

        @Override
        public int getCount() {
            return mRoundCount;
        }
    }

    public class DepthPageTransformer implements ViewPager.PageTransformer {
        private static final float MIN_SCALE = 0.75f;

        public void transformPage(View view, float position) {
            int pageWidth = view.getWidth();

            if (position < -1) { // [-Infinity,-1)
                // This page is way off-screen to the left.
                view.setAlpha(0);

            } else if (position <= 0) { // [-1,0]
                // Use the default slide transition when moving to the left page
                view.setAlpha(1);
                view.setTranslationX(0);
                view.setScaleX(1);
                view.setScaleY(1);

            } else if (position <= 1) { // (0,1]
                // Fade the page out.
                view.setAlpha(1 - position);

                // Counteract the default slide transition
                view.setTranslationX(pageWidth * -position);

                // Scale the page down (between MIN_SCALE and 1)
                float scaleFactor = MIN_SCALE
                        + (1 - MIN_SCALE) * (1 - Math.abs(position));
                view.setScaleX(scaleFactor);
                view.setScaleY(scaleFactor);

            } else { // (1,+Infinity]
                // This page is way off-screen to the right.
                view.setAlpha(0);
            }
        }
    }
}
