package com.selvashc.entertainment.roulettegame;

import android.content.Context;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.RelativeLayout;

import com.jawon.han.HanActivity;
import com.jawon.han.widget.HanApplication;
import com.selvashc.entertainment.roulettegame.databinding.ActivityMainBinding;

import java.security.SecureRandom;
import java.util.ArrayList;

/**
 * 메인 화면
 * - 룰렛 돌리기 : 시작하기 버튼 으로 룰렛을 돌려 값을 얻어온 후 결과를 출력함
 * - 설정 대화 상자 열기 : 설정 대화 상자에서 룰렛 설정을 바꾸도록 할 수 있음 ( call back listener 로 통신 )
 * <p>
 * 룰렛 기능 참고 : https://github.com/thoon-kim/ThRoulette/
 */
public class MainActivity extends HanActivity {

    public static final String PREFERENCES_NAME = "Roulette_preference";

    private static final int NUMBER_OF_LIST_ONE = 1;
    private static final int NUMBER_OF_LIST_TWO = 2;
    private static final int NUMBER_OF_LIST_THREE = 3;
    private static final int NUMBER_OF_LIST_FOUR = 4;
    private static final int NUMBER_OF_LIST_FIVE = 5;
    private static final int NUMBER_OF_LIST_SIX = 6;

    private ActivityMainBinding binding;

    private int numRoulette;
    private ArrayList<String> rouletteList = new ArrayList<>();

    private CircleManager circleManager;

    private MediaPlayer roulettePlayer;
    private MediaPlayer shoutPlayer;

    // 룰렛에서 선택될 목록
    private String selectText = "";

    private SecureRandom random = new SecureRandom();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.setActivity(this);

        SharedPreferences sharedPreferences = getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        numRoulette = Integer.parseInt(sharedPreferences.getString("NUMBER", "3"));

        // 룰렛에 값 초기화 ( 5개라면 5~1 까지 추가해야 하므로 switch 문에 break 가 없음 )
        switch (numRoulette) {
            case NUMBER_OF_LIST_SIX:
                rouletteList.add(sharedPreferences.getString("ITEM6", "6"));
            case NUMBER_OF_LIST_FIVE:
                rouletteList.add(sharedPreferences.getString("ITEM5", "5"));
            case NUMBER_OF_LIST_FOUR:
                rouletteList.add(sharedPreferences.getString("ITEM4", "4"));
            case NUMBER_OF_LIST_THREE:
                rouletteList.add(sharedPreferences.getString("ITEM3", "3"));
            case NUMBER_OF_LIST_TWO:
                rouletteList.add(sharedPreferences.getString("ITEM2", "2"));
            case NUMBER_OF_LIST_ONE:
                rouletteList.add(sharedPreferences.getString("ITEM1", "1"));
            default:
                break;
        }

        circleManager = new CircleManager(this, numRoulette, binding.layoutRoulette, rouletteList);
        binding.layoutRoulette.addView(circleManager);

        // Media Player init
        roulettePlayer = MediaPlayer.create(this, R.raw.dice_mix);
        roulettePlayer.setOnCompletionListener(mp -> shoutPlayer.start());
        shoutPlayer = MediaPlayer.create(this, R.raw.dice_shout);
        shoutPlayer.setOnCompletionListener(mp -> {
            binding.btnStart.setEnabled(true);
            binding.btnSetting.setEnabled(true);

            HanApplication.getInstance(this).getHanDevice().displayAndPlayTTS(selectText + " " + getString(R.string.winning), true);
        });
    }

    @Override
    protected void onDestroy() {
        roulettePlayer.release();
        shoutPlayer.release();
        super.onDestroy();
    }

    public void onClickedStart() {
        roulettePlayer.start();
        rotateLayout(binding.layoutRoulette, numRoulette);
        binding.btnSetting.setEnabled(false);
        binding.btnStart.setEnabled(false);
    }

    public void onClickedSetting() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View v = inflater.inflate(R.layout.layout_setting_dlg, null);
        SettingDialog settingDialog = new SettingDialog(this, v, new Listener());

        settingDialog.setContentView(v);
        settingDialog.setTitle(getString(R.string.settings));
        settingDialog.show();
    }

    private void rotateLayout(final RelativeLayout layout, final int num) {
        final float INIT_ANGLE = 0.0f;
        final float FROM_ANGLE = (random.nextInt(360)) + 7200 + INIT_ANGLE;
        final int DURATION = 4500;
        final float pivotValue = 0.5f;

        RotateAnimation rotateAnimation = new RotateAnimation(INIT_ANGLE, FROM_ANGLE,
                Animation.RELATIVE_TO_SELF, pivotValue, Animation.RELATIVE_TO_SELF, pivotValue);

        rotateAnimation.setInterpolator(AnimationUtils.loadInterpolator(this, android.R.anim.accelerate_decelerate_interpolator));
        rotateAnimation.setDuration(DURATION);
        rotateAnimation.setFillEnabled(true);
        rotateAnimation.setFillAfter(true);
        rotateAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                // 사용 하지 않음
            }

            // 애니메이션 동작이 끝난 후에 결과 값 가져오기
            @Override
            public void onAnimationEnd(Animation animation) {
                getResult(FROM_ANGLE, num);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // 사용 하지 않음
            }
        });
        layout.startAnimation(rotateAnimation);
    }

    private void getResult(float angle, int numRoulette) {
        final int CIRCLE_ANGLE = 360;

        String text = "";
        angle = angle % CIRCLE_ANGLE;

        // 룰렛의 첫 번째 item 이 270 에서 부터 작아진다고 생각하고 계산하면 된다.
        if (numRoulette == NUMBER_OF_LIST_TWO) {
            if (angle > 270 || angle <= 90) {
                text = rouletteList.get(1);
            } else {
                text = rouletteList.get(0);
            }
        } else if (numRoulette == NUMBER_OF_LIST_THREE) {
            if (angle > 270 || angle <= 30) {
                text = rouletteList.get(2);
            } else if (angle <= 150) {
                text = rouletteList.get(1);
            } else if (angle <= 270) {
                text = rouletteList.get(0);
            }
        } else if (numRoulette == NUMBER_OF_LIST_FOUR) {
            if (angle > 270) {
                text = rouletteList.get(3);
            } else if (angle <= 90) {
                text = rouletteList.get(2);
            } else if (angle <= 180) {
                text = rouletteList.get(1);
            } else if (angle <= 270) {
                text = rouletteList.get(0);
            }
        } else if (numRoulette == NUMBER_OF_LIST_FIVE) {
            if (angle > 342 || angle <= 54) {
                text = rouletteList.get(3);
            } else if (angle <= 126) {
                text = rouletteList.get(2);
            } else if (angle <= 198) {
                text = rouletteList.get(1);
            } else if (angle <= 270) {
                text = rouletteList.get(0);
            } else if (angle <= 342) {
                text = rouletteList.get(4);
            }
        } else if (numRoulette == NUMBER_OF_LIST_SIX) {
            if (angle > 330 || angle <= 30) {
                text = rouletteList.get(4);
            } else if (angle <= 90) {
                text = rouletteList.get(3);
            } else if (angle <= 150) {
                text = rouletteList.get(2);
            } else if (angle <= 210) {
                text = rouletteList.get(1);
            } else if (angle <= 270) {
                text = rouletteList.get(0);
            } else if (angle <= 330) {
                text = rouletteList.get(5);
            }
        }
        selectText = text;
    }

    // 설정 대화상자에 보낼 Call Back Listener
    class Listener {
        void onCompletedSaveOption(ArrayList<String> list) {
            rouletteList = list;
            numRoulette = list.size();
            circleManager = new CircleManager(MainActivity.this, numRoulette, binding.layoutRoulette, rouletteList);
            binding.layoutRoulette.addView(circleManager);
        }
    }
}
