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

        Log.d(TAG,"mRoundCount = " + mRoundCount);
        return fragmentView;
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentStatePagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return RoundStateFragment.newInstance(mStringTournamentUri, position);
        }

        @Override
        public int getCount() {
            return mRoundCount;
        }
    }
}
