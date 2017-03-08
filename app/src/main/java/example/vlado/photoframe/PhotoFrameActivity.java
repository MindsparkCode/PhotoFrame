package example.vlado.photoframe;

import android.Manifest;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.List;

import example.vlado.photoframe.dialog.SettingsDialogFragment;
import example.vlado.photoframe.util.ClickRecognizer;
import example.vlado.photoframe.util.CompatAnimationUtils;
import example.vlado.photoframe.util.FilesUtil;
import example.vlado.photoframe.util.SharedPrefHelper;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class PhotoFrameActivity extends AppCompatActivity {

    private static final String TAG = PhotoFrameActivity.class.getSimpleName();
    private static int REQUEST_CODE_READ_EXTERNAL_STORAGE = 0;

    private List<File> imageList;
    private ViewPager imageViewPager;
    private ImageView revealEffectImageView;
    private TextView releaseForSettingsTextView;
    private Drawable pauseIcon, playIcon;
    private Handler changePhotoHandler = new Handler();

    private boolean isSlideshowRunning;

    private Settings settings;

    private Runnable changePhotoRunnable = new Runnable() {
        @Override
        public void run() {
            if (imageList != null && imageList.size() > 1) {
                imageViewPager.setCurrentItem((imageViewPager.getCurrentItem() + 1) % imageList.size());
                changePhotoHandler.postDelayed(this, settings.getDelayInMs());
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
            setContentView(R.layout.activity_photo_frame);
        } else {
            setContentView(R.layout.activity_photo_frame_pre_ics);
        }

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        imageViewPager = (ViewPager) findViewById(R.id.image_view_pager);
        revealEffectImageView = (ImageView) findViewById(R.id.reveal_effect_image_view);
        releaseForSettingsTextView = (TextView) findViewById(R.id.release_for_settings_text_view);

        final Animation pulsingAnimation = AnimationUtils.loadAnimation(this, R.anim.pulsing_animation);

        imageViewPager.setOnTouchListener(new ClickRecognizer(new ClickRecognizer.OnClickListener() {

            int x = 0;
            int y = 0;
            private Handler startPulsingHandler = new Handler();

            @Override
            public void onClick(int x, int y) {
                if (settings.getDelayInMs() == 0 || revealEffectImageView.getVisibility() == View.VISIBLE) {
                    return;
                }
                if (isSlideshowRunning) {
                    pauseSlideshow();
                    revealEffectImageView.setImageDrawable(pauseIcon);
                } else {
                    startSlideshowIfDelaySet();
                    revealEffectImageView.setImageDrawable(playIcon);
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                    CompatAnimationUtils.getRevealAndReverseAnimator(revealEffectImageView, x, y, 500).start();
                } else {
                    // Animation won't start without setting the view's visibility to VISIBLE prior to starting the animation
                    revealEffectImageView.setVisibility(View.VISIBLE);
                    CompatAnimationUtils.getAlphaAndReverseAnimation(revealEffectImageView, 500).startNow();
                }
            }

            @Override
            public void onLongClick() {
                startPulsingHandler.removeCallbacksAndMessages(null);
                releaseForSettingsTextView.setVisibility(View.INVISIBLE);
                releaseForSettingsTextView.clearAnimation();
                showSettingsDialog();

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                    CompatAnimationUtils.getReverseRevealAnimation(revealEffectImageView, x, y, 500, true).start();
                } else {
                    revealEffectImageView.setVisibility(View.VISIBLE);
                    CompatAnimationUtils.getReverseAlphaAnimation(revealEffectImageView, 500, true).startNow();
                }
            }

            @Override
            public void onLongClickInterrupted() {
                revealEffectImageView.setVisibility(View.INVISIBLE);
                revealEffectImageView.clearAnimation();
                startPulsingHandler.removeCallbacksAndMessages(null);
                releaseForSettingsTextView.setVisibility(View.INVISIBLE);
                releaseForSettingsTextView.clearAnimation();
            }

            @Override
            public void onLongClickStart(int x, int y) {
                this.x = x;
                this.y = y;

                revealEffectImageView.setImageDrawable(null);
                startPulsingHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        pulsingAnimation.setStartTime(0);
                        releaseForSettingsTextView.setAnimation(pulsingAnimation);
                        releaseForSettingsTextView.setVisibility(View.VISIBLE);
                    }
                }, 600);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                    CompatAnimationUtils.getRevealAnimation(revealEffectImageView, x, y, 1000, false).start();
                } else {
                    revealEffectImageView.setVisibility(View.VISIBLE);
                    CompatAnimationUtils.getAlphaAnimation(revealEffectImageView, 1000, false).startNow();
                }
            }
        }));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            playIcon = getDrawable(R.drawable.ic_play_arrow_black_24dp);
            pauseIcon = getDrawable(R.drawable.ic_pause_black_24dp);
        } else {
            playIcon = getResources().getDrawable(R.drawable.ic_play_arrow_black_24dp);
            pauseIcon = getResources().getDrawable(R.drawable.ic_pause_black_24dp);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_READ_EXTERNAL_STORAGE);
        } else {
            loadSettings(SharedPrefHelper.getSettings(this));
            startSlideshowIfDelaySet();
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        hideSystemUI();
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        pauseSlideshow();
        EventBus.getDefault().unregister(this);
    }

    private void loadSettings(Settings newSettings) {
        pauseSlideshow();

        if (settings == null || !settings.equals(newSettings)) {
            imageList = FilesUtil.loadImageList(newSettings.getPhotosFolderPath(), newSettings.isIncludeSubdirectories());

            if (imageList.size() < 1) {
                showSettingsDialog();
                Toast.makeText(this, R.string.no_photos_to_load, Toast.LENGTH_LONG).show();
                return;
            }
            imageViewPager.setAdapter(new ImageViewPagerAdapter(this, imageList));
        }

        this.settings = newSettings;

        startSlideshowIfDelaySet();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSettingsChanged(Settings settings) {
        Log.d(TAG, "onSettingsChanged: " + "delay=" + settings.getDelay()
                + " folderPath=" + settings.getPhotosFolderPath());
        loadSettings(settings);
        SharedPrefHelper.saveSettings(settings, this);
    }

    private void startSlideshowIfDelaySet() {
        changePhotoHandler.removeCallbacksAndMessages(null);
        if (settings != null && settings.getDelayInMs() != 0) {
            isSlideshowRunning = true;
            changePhotoHandler.postDelayed(changePhotoRunnable, settings.getDelayInMs());
        }
    }

    private void pauseSlideshow() {
        isSlideshowRunning = false;
        changePhotoHandler.removeCallbacksAndMessages(null);
    }

    private void showSettingsDialog() {
        SettingsDialogFragment settingsDialogFragment = new SettingsDialogFragment();
        settingsDialogFragment.show(getSupportFragmentManager(), "settings");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_READ_EXTERNAL_STORAGE) {
            if (grantResults[0] == PERMISSION_GRANTED) {
                loadSettings(SharedPrefHelper.getSettings(this));
                startSlideshowIfDelaySet();
            } else {
                finish();
            }
        }
    }

    private void hideSystemUI() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                            | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }
}
