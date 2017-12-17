package io.github.alessandrojean.mangachecklists.util;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.view.View;

/**
 * Created by Desktop on 16/12/2017.
 */

public class LoadingUtils {

    public static void showContent(View toShow, final View toHide, int animationDuration) {
        toShow.setAlpha(0f);
        toShow.setVisibility(View.VISIBLE);
        toShow.animate()
                .alpha(1f)
                .setDuration(animationDuration)
                .setListener(null);

        toHide.animate()
                .alpha(0f)
                .setDuration(animationDuration)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        toHide.setVisibility(View.GONE);
                    }
                });
    }

    public static void showContent(View toShow1, View toShow2, final View toHide, int animationDuration) {
        toShow1.setAlpha(0f);
        toShow2.setAlpha(0f);

        toShow1.setVisibility(View.VISIBLE);
        toShow2.setVisibility(View.VISIBLE);

        toShow1.animate()
                .alpha(1f)
                .setDuration(animationDuration)
                .setListener(null);
        toShow2.animate()
                .alpha(1f)
                .setDuration(animationDuration)
                .setListener(null);

        toHide.animate()
                .alpha(0f)
                .setDuration(animationDuration)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        toHide.setVisibility(View.GONE);
                    }
                });
    }

    public static void showLoading(final View toHide1, final View toHide2, View toShow, int animationDuration) {
        toShow.setAlpha(0f);
        toShow.setVisibility(View.VISIBLE);

        toShow.animate()
                .alpha(1f)
                .setDuration(animationDuration)
                .setListener(null);

        toHide1.animate()
                .alpha(0f)
                .setDuration(animationDuration)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        toHide1.setVisibility(View.GONE);
                    }
                });

        toHide2.animate()
                .alpha(0f)
                .setDuration(animationDuration)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        toHide2.setVisibility(View.GONE);
                    }
                });
    }
}
