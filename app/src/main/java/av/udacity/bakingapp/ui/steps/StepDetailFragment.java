package av.udacity.bakingapp.ui.steps;

import android.content.res.Configuration;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.gson.Gson;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import av.udacity.bakingapp.R;
import av.udacity.bakingapp.databinding.RecipeDetailBinding;
import av.udacity.bakingapp.message.StepChangeEvent;
import av.udacity.bakingapp.net.NetworkUtils;
import av.udacity.bakingapp.net.jsonstubs.Step;
import av.udacity.bakingapp.ui.databinding.StepDetailModel;
import av.udacity.bakingapp.ui.recipe.RecipeDetailActivity;

import static av.udacity.bakingapp.ui.Constants.DATA_ARGUMENTS_KEY;
import static av.udacity.bakingapp.ui.Constants.MEDIA_SESSION_TAG;
import static av.udacity.bakingapp.ui.Constants.PLAYBACK_POSITION_KEY;
import static av.udacity.bakingapp.ui.Constants.STEPS_COUNT;
import static av.udacity.bakingapp.ui.Constants.STEP_INDEX_KEY;
import static av.udacity.bakingapp.ui.Constants.TWO_PANE_KEY;

public class StepDetailFragment extends Fragment implements Player.EventListener {
    private Step step;
    private SimpleExoPlayer exoPlayer;

    private String videoUrl;
    private Uri videoUri;

    private long playbackPosition;

    private boolean mTwoPane = false;
    private int mStepIndex;
    private int mStepsCount;

    private MediaSessionCompat mMediaSession;
    private PlaybackStateCompat.Builder mStateBuilder;

    private RecipeDetailBinding mBinding;
    private StepDetailModel mStepDetailModel;


    public StepDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Set Data Binding, two valid way:
        //mBinding = RecipeDetailBinding.inflate(inflater, container, false);
        // or
        mBinding = DataBindingUtil.inflate(inflater, R.layout.recipe_detail, container, false);
        mStepDetailModel = new StepDetailModel();
        mBinding.setDetailModel(mStepDetailModel);

        if (savedInstanceState != null) {
            playbackPosition = savedInstanceState.getLong(PLAYBACK_POSITION_KEY);
        }

        Bundle arguments = getArguments();
        if (arguments != null && arguments.containsKey(DATA_ARGUMENTS_KEY)) {
            step = new Gson().fromJson(arguments.getString(DATA_ARGUMENTS_KEY), Step.class);
            videoUrl = step.getVideoURL();
            videoUri = Uri.parse(videoUrl);
            mStepIndex = arguments.getInt(STEP_INDEX_KEY);
            mStepsCount = arguments.getInt(STEPS_COUNT);
            mTwoPane = arguments.getBoolean(TWO_PANE_KEY);
        }

        LinearLayout playerContainer = mBinding.playerContainer;
        ImageView stepImageView = mBinding.ivStepImage;
        if (step != null) {
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE && !mTwoPane) {
                fullScreen();
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                playerContainer.setLayoutParams(layoutParams);
                mStepDetailModel.setDetailVisible(false);
            } else {
                if (mTwoPane) {
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT, 700);
                    playerContainer.setLayoutParams(layoutParams);
                    stepImageView.setLayoutParams(layoutParams);
                }
                mStepDetailModel.setDetailVisible(true);
                mStepDetailModel.setRecipeDetail(step.getDescription());
            }
        }

        if (!TextUtils.isEmpty(videoUrl)) {
            initMediaSession();
            initPlayer(videoUri);
        }

        Button btnNext = mBinding.btnNext;
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mStepIndex++;
                String data = RecipeDetailActivity.getStep(mStepIndex);
                step = new Gson().fromJson(data, Step.class);
                startStep();
            }
        });

        Button btnPrev = mBinding.btnPrev;
        btnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mStepIndex--;
                String data = RecipeDetailActivity.getStep(mStepIndex);
                step = new Gson().fromJson(data, Step.class);
                startStep();
            }
        });

        return mBinding.getRoot();
    }

    private void startStep() {
        initPlayer(Uri.parse(step.getVideoURL()));
        mStepDetailModel.setRecipeDetail(step.getDescription());
    }

    private void checkNav() {
        boolean btnPrevEnabled = true;
        boolean btnNextEnabled = true;
        if (mStepIndex == 0) { //first step
            btnPrevEnabled = false;
            btnNextEnabled = true;
        } else if (mStepIndex == mStepsCount - 1) { //last step
            btnPrevEnabled = true;
            btnNextEnabled = false;
        }
        mStepDetailModel.setPrevEnabled(btnPrevEnabled);
        mStepDetailModel.setNextEnabled(btnNextEnabled);
    }

    private void initPlayer(Uri uri) {
        checkNav();
        videoUrl = step.getVideoURL();
        final SimpleExoPlayerView playerView = mBinding.videoView;
        final ImageView stepImageView = mBinding.ivStepImage;

        // no video url present in json
        if (TextUtils.isEmpty(videoUrl)) {
            releasePlayer();
            mStepDetailModel.setPlayerVisible(false);
            showToast(getString(R.string.no_video_available));
            if (!TextUtils.isEmpty(step.getThumbnailURL())) {
                Picasso.get()
                        .load(step.getThumbnailURL())
                        .fit()
                        .centerInside()
                        .error(R.drawable.no_video_placeholder)
                        .placeholder(R.drawable.cake_placeholder)
                        .into(stepImageView, new Callback() {
                            @Override
                            public void onSuccess() {
                            }

                            @Override
                            public void onError(Exception exc) {
                                NetworkUtils.checkConnection(getContext());
                            }
                        });

            }
        } else {
            mStepDetailModel.setPlayerVisible(true);
            if (exoPlayer == null) {
                TrackSelector trackSelector = new DefaultTrackSelector();
                LoadControl loadControl = new DefaultLoadControl();
                DefaultRenderersFactory renderersFactory = new DefaultRenderersFactory(getContext());
                exoPlayer = ExoPlayerFactory.newSimpleInstance(renderersFactory, trackSelector, loadControl);
                playerView.setPlayer(exoPlayer);
                exoPlayer.addListener(this);

                MediaSource mediaSource = buildMediaSource(uri);
                exoPlayer.prepare(mediaSource, true, false);
                exoPlayer.setPlayWhenReady(true);
                exoPlayer.seekTo(playbackPosition);

            } else {
                MediaSource mediaSource = buildMediaSource(uri);
                exoPlayer.prepare(mediaSource, true, false);
                exoPlayer.setPlayWhenReady(true);
                exoPlayer.seekTo(playbackPosition);
            }
        }
    }

    private void showToast(String msg) {
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }


    private void hideSystemUI() {
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        //Use Google's "LeanBack" mode to get fullscreen in landscape
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            getActivity().getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN

                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE);
        }
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    private MediaSource buildMediaSource(Uri uri) {
        return new ExtractorMediaSource.Factory(new DefaultHttpDataSourceFactory("ua")).createMediaSource(uri);
    }

    private void releasePlayer() {
        if (exoPlayer != null) {
            exoPlayer.stop();
            exoPlayer.release();
            exoPlayer = null;
        }

        if (mMediaSession != null) {
            mMediaSession.setActive(false);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (exoPlayer != null) {
            outState.putLong(PLAYBACK_POSITION_KEY, exoPlayer.getCurrentPosition());
        }
        super.onSaveInstanceState(outState);
    }


    private void fullScreen() {
        hideSystemUI();
        PlayerView playerView = mBinding.videoView;
        playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FILL);
    }

    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest, int reason) {

    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

    }

    @Override
    public void onLoadingChanged(boolean isLoading) {

    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        if (mMediaSession == null) {
            return;
        }
        if ((playbackState == Player.STATE_READY) && playWhenReady) {
            mStateBuilder.setState(PlaybackStateCompat.STATE_PLAYING,
                    exoPlayer.getCurrentPosition(), 1f);
        } else if ((playbackState == Player.STATE_READY)) {
            mStateBuilder.setState(PlaybackStateCompat.STATE_PAUSED, exoPlayer.getCurrentPosition(), 1f);
        }
        mMediaSession.setPlaybackState(mStateBuilder.build());
    }

    @Override
    public void onRepeatModeChanged(int repeatMode) {

    }

    @Override
    public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {

    }

    @Override
    public void onPositionDiscontinuity(int reason) {

    }

    @Override
    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

    }

    @Override
    public void onSeekProcessed() {

    }

    private void initMediaSession() {
        mMediaSession = new MediaSessionCompat(getContext(), MEDIA_SESSION_TAG);
        mMediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

        mMediaSession.setMediaButtonReceiver(null);
        mStateBuilder = new PlaybackStateCompat.Builder()
                .setActions(PlaybackStateCompat.ACTION_PLAY |
                        PlaybackStateCompat.ACTION_PAUSE |
                        PlaybackStateCompat.ACTION_PLAY_PAUSE |
                        PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS);

        mMediaSession.setPlaybackState(mStateBuilder.build());
        mMediaSession.setActive(true);
        mMediaSession.setCallback(new MediaSessionCallback());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        releasePlayer();
        if (mMediaSession != null) {
            mMediaSession.setActive(false);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (videoUri != null) {
            if (exoPlayer != null) {
                exoPlayer.seekTo(playbackPosition);
            } else {
                initPlayer(videoUri);
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
            releasePlayer();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            releasePlayer();
        }
    }


    private class MediaSessionCallback extends MediaSessionCompat.Callback {
        @Override
        public void onPlay() {
            exoPlayer.setPlayWhenReady(true);
        }

        @Override
        public void onPause() {
            exoPlayer.setPlayWhenReady(false);
        }

        @Override
        public void onSkipToPrevious() {
            exoPlayer.seekTo(0);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(StepChangeEvent event) {
        step = new Gson().fromJson(event.getStepData(), Step.class);
        mStepDetailModel.setDetailVisible(true);
        mStepDetailModel.setRecipeDetail(step.getDescription());
        videoUrl = step.getVideoURL();
        videoUri = Uri.parse(videoUrl);
        mStepIndex = event.getCurrentStep();
        mStepsCount = event.getStepsCount();
        mTwoPane = event.isTwoPane();
        startStep();
    }

}
