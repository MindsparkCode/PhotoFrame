package example.vlado.photoframe.util;

import android.animation.Animator;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

import io.codetail.animation.ViewAnimationUtils;

/**
 * Created by vlado on 09/02/2017.
 */

public class CompatAnimationUtils {


    @RequiresApi(api = Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
    public static Animator getRevealAnimation(final View view, final int x, final int y, final int duration, final boolean dissappear) {
        final float finalRadius = (float) Math.hypot(view.getWidth(), view.getHeight());
        Animator revealAnimator = ViewAnimationUtils.createCircularReveal(view, x, y, 0, finalRadius);
        revealAnimator.setDuration(duration);

        revealAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                view.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                if (dissappear) {
                    view.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onAnimationCancel(Animator animator) {
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
            }
        });

        return revealAnimator;
    }

    @RequiresApi(api = Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
    public static Animator getReverseRevealAnimation(final View view, final int x, final int y, final int duration, final boolean dissappear) {
        final float finalRadius = (float) Math.hypot(view.getWidth(), view.getHeight());
        Animator revealAnimator = ViewAnimationUtils.createCircularReveal(view, x, y, finalRadius, 0);
        revealAnimator.setDuration(duration);

        revealAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                view.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                if (dissappear) {
                    view.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onAnimationCancel(Animator animator) {
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
            }
        });

        return revealAnimator;
    }

    @RequiresApi(api = Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
    public static Animator getRevealAndReverseAnimator(final View view, final int x, final int y, final int duration) {
        Animator revealAndReverse = getRevealAnimation(view, x, y, duration, false);
        revealAndReverse.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                getReverseRevealAnimation(view, x, y, duration, true).start();
            }

            @Override
            public void onAnimationCancel(Animator animator) {
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
            }
        });

        return revealAndReverse;
    }

    public static Animation getAlphaAnimation(final View view, final int duration, final boolean dissappear) {
        AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
        view.setAnimation(alphaAnimation);
        alphaAnimation.setDuration(duration);

        alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (dissappear) {
                    view.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        return alphaAnimation;
    }

    public static Animation getReverseAlphaAnimation(final View view, final int duration, final boolean dissappear) {
        AlphaAnimation alphaAnimation = new AlphaAnimation(1, 0);
        view.setAnimation(alphaAnimation);
        alphaAnimation.setDuration(duration);

        alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (dissappear) {
                    view.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        return alphaAnimation;
    }

    public static Animation getAlphaAndReverseAnimation(final View view, final int duration) {
        Animation alphaAnimation = getAlphaAnimation(view, duration, false);
        view.setAnimation(alphaAnimation);

        final Animation reverseAlphaAnimation = getReverseAlphaAnimation(view, duration, true);
        alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                reverseAlphaAnimation.startNow();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        return alphaAnimation;
    }
}
