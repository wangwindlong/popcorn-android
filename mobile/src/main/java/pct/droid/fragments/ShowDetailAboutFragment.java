package pct.droid.fragments;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Layout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import pct.droid.R;
import pct.droid.base.providers.media.models.Show;
import pct.droid.base.utils.VersionUtils;
import pct.droid.dialogfragments.SynopsisDialogFragment;

public class ShowDetailAboutFragment extends BaseDetailFragment {

    private Show mShow;

    @InjectView(R.id.title)
    TextView mTitle;
    @InjectView(R.id.meta)
    TextView mMeta;
    @InjectView(R.id.synopsis)
    TextView mSynopsis;
    @InjectView(R.id.rating)
    RatingBar mRating;
    @InjectView(R.id.read_more)
    Button mReadMore;
    @InjectView(R.id.info_buttons)
    LinearLayout mInfoButtons;

    public static ShowDetailAboutFragment newInstance(Show show) {
        Bundle b = new Bundle();
        b.putParcelable(DATA, show);
        ShowDetailAboutFragment showDetailFragment = new ShowDetailAboutFragment();
        showDetailFragment.setArguments(b);
        return showDetailFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mShow = getArguments().getParcelable(DATA);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRoot = inflater.inflate(R.layout.fragment_detail_about, container, false);
        ButterKnife.inject(this, mRoot);
        if(VersionUtils.isJellyBean() && container != null) {
            mRoot.setMinimumHeight(container.getMinimumHeight());
        }

        Double rating = Double.parseDouble(mShow.rating);
        mTitle.setText(mShow.title);
        mRating.setProgress(rating.intValue());

        String metaDataStr = mShow.year;

        if (mShow.status != null) {
            metaDataStr += " • ";
            if (mShow.status == Show.Status.CONTINUING) {
                metaDataStr += getString(R.string.continuing);
            } else {
                metaDataStr += getString(R.string.ended);
            }
        }

        if (!TextUtils.isEmpty(mShow.genre)) {
            metaDataStr += " • ";
            metaDataStr += mShow.genre;
        }

        mMeta.setText(metaDataStr);

        if (!TextUtils.isEmpty(mShow.synopsis)) {
            mSynopsis.setText(mShow.synopsis);
            mSynopsis.post(new Runnable() {
                @Override
                public void run() {
                    boolean ellipsized = false;
                    Layout layout = mSynopsis.getLayout();
                    if (layout == null) return;
                    int lines = layout.getLineCount();
                    if (lines > 0) {
                        int ellipsisCount = layout.getEllipsisCount(lines - 1);
                        if (ellipsisCount > 0) {
                            ellipsized = true;
                        }
                    }
                    mInfoButtons.setVisibility(ellipsized ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            mInfoButtons.setVisibility(View.GONE);
        }

        return mRoot;
    }

    @OnClick(R.id.read_more)
    public void openReadMore(View v) {
        if (getFragmentManager().findFragmentByTag("overlay_fragment") != null)
            return;
        SynopsisDialogFragment synopsisDialogFragment = new SynopsisDialogFragment();
        Bundle b = new Bundle();
        b.putString("text", mShow.synopsis);
        synopsisDialogFragment.setArguments(b);
        synopsisDialogFragment.show(getFragmentManager(), "overlay_fragment");
    }

}
