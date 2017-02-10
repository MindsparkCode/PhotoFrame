package example.vlado.photoframe.util;

import android.animation.Animator;
import android.view.View;

import io.codetail.animation.ViewAnimationUtils;

/**
 * Created by vlado on 09/02/2017.
 */

public class RevealAnimationUtil {


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
}
