package com.chrynan.android_guitar_tuner.ui.fragment;

import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.annotation.StringRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chrynan.android_guitar_tuner.GuitarTunerApplication;
import com.chrynan.android_guitar_tuner.R;
import com.chrynan.android_guitar_tuner.di.component.DaggerPitchViewComponent;
import com.chrynan.android_guitar_tuner.di.module.PitchViewModule;
import com.chrynan.android_guitar_tuner.presenter.PitchPresenter;
import com.chrynan.android_guitar_tuner.ui.view.PitchView;

import javax.inject.Inject;

import butterknife.BindView;

public class PitchPlayerFragment extends BaseFragment implements PitchView {

    public static final String TAG = "PitchPlayerFragment";
    public static final String TITLE = "Pitch Playback";

    private static final String KEY_NOTE = "Note";
    private static final String KEY_FREQUENCY = "Frequency";

    @BindView(R.id.noteTextView)
    TextView noteTextView;
    @BindView(R.id.volumeStateTextView)
    TextView volumeStateTextView;

    @Inject
    PitchPresenter presenter;

    private double frequency;

    public static PitchPlayerFragment newInstance(final String note, final double frequency) {
        Bundle bundle = new Bundle();
        bundle.putString(KEY_NOTE, note);
        bundle.putDouble(KEY_FREQUENCY, frequency);

        PitchPlayerFragment fragment = new PitchPlayerFragment();
        fragment.setArguments(bundle);

        return fragment;
    }

    public PitchPlayerFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflateAndBindView(inflater, R.layout.fragment_pitch_player, container, false);

        if (getArguments() != null) {
            String note = getArguments().getString(KEY_NOTE);
            frequency = getArguments().getDouble(KEY_FREQUENCY);

            noteTextView.setText(note);
        }

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();

        presenter.startPlayingNote(frequency);
        presenter.startListeningToVolumeChanges();
    }

    @Override
    public void onPause() {
        super.onPause();

        presenter.stopPlayingNote();
        presenter.stopListeningToVolumeChanges();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        presenter.detachView();

        unbinder.unbind();
    }

    @Override
    protected void setupDaggerComponent() {
        DaggerPitchViewComponent.builder()
                .applicationComponent(GuitarTunerApplication.getApplicationComponent())
                .pitchViewModule(new PitchViewModule(this))
                .build()
                .inject(this);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onUpdateVolumeState(@StringRes int volumeStateText, @ColorRes int textColor) {
        volumeStateTextView.setText(volumeStateText);
        volumeStateTextView.setTextColor(getContext().getResources().getColor(textColor));
    }
}
