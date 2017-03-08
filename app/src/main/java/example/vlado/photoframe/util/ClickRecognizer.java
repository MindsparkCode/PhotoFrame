package example.vlado.photoframe.util;

import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by vlado on 30/01/2017.
 */

public class ClickRecognizer implements View.OnTouchListener {

    public static int TAP_TIMEOUT = 350;
    public static int LONG_CLICK_TIMEOUT = 1000;

    private Handler handler = new Handler();
    private boolean shouldRegisterAsClick = false;
    private boolean longClickStarted = false;
    private boolean longClickFinished = false;
    private float firstX;
    private OnClickListener listener;

    public interface OnClickListener {
        void onClick(int x, int y);

        void onLongClick();

        void onLongClickInterrupted();

        void onLongClickStart(int x, int y);
    }

    public ClickRecognizer(OnClickListener onTouchListener) {
        this.listener = onTouchListener;
    }

    @Override
    public boolean onTouch(View view, final MotionEvent motionEvent) {

        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                firstX = motionEvent.getX();
                shouldRegisterAsClick = true;
                longClickStarted = false;
                longClickFinished = false;

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        shouldRegisterAsClick = false;
                        longClickStarted = true;

                        listener.onLongClickStart((int) motionEvent.getX(), (int) motionEvent.getY());
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                longClickFinished = true;
                            }
                        }, LONG_CLICK_TIMEOUT);
                    }
                }, TAP_TIMEOUT);
                return true;

            case MotionEvent.ACTION_UP:
                handler.removeCallbacksAndMessages(null);
                if (shouldRegisterAsClick) {
                    listener.onClick((int) motionEvent.getX(), (int) motionEvent.getY());
                } else if (longClickFinished) {
                    listener.onLongClick();
                } else if (longClickStarted) {
                    listener.onLongClickInterrupted();
                }
                break;

            case MotionEvent.ACTION_MOVE:
                if (Math.abs(motionEvent.getX() - firstX) > 10) {
                    if (shouldRegisterAsClick) {
                        shouldRegisterAsClick = false;
                        handler.removeCallbacksAndMessages(null);
                    }
                }
                break;
        }
        return false;
    }
}
