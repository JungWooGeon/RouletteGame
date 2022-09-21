package com.selvashc.entertainment.roulettegame;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;

import com.jawon.han.key.HanBrailleKey;
import com.jawon.han.key.keyboard.usb.USB2Braille;
import com.jawon.han.util.HimsCommonFunc;
import com.jawon.han.widget.HanApplication;
import com.jawon.han.widget.HanButton;
import com.jawon.han.widget.HanDialog;
import com.jawon.han.widget.HanEditText;
import com.jawon.han.widget.HanSpinner;
import com.jawon.han.widget.adapter.HanStringArrayAdapter;

import java.util.ArrayList;

import static com.selvashc.entertainment.roulettegame.MainActivity.PREFERENCES_NAME;

/**
 * 룰렛 목록 개수와 목록 리스트를 설정(변경)하여 저장
 * 설정이 완료되면 call back listener 로 변경 알림
 */
public class SettingDialog extends HanDialog {

    private static final int FIRST_ITEM = 1;
    private static final int SECOND_ITEM = 2;
    private static final int THIRD_ITEM = 3;
    private static final int FOURTH_ITEM = 4;
    private static final int FIFTH_ITEM = 5;
    private static final int SIXTH_ITEM = 6;

    // 설정 저장, 처음 단계 true/false
    private String numberOption;
    private boolean isFirst = true;

    // xml component
    private HanSpinner numberSpinner;
    private HanButton saveButton;
    private LinearLayout numberLayout;

    private LinearLayout list1;
    private LinearLayout list2;
    private LinearLayout list3;
    private LinearLayout list4;
    private LinearLayout list5;
    private LinearLayout list6;

    private HanEditText item1;
    private HanEditText item2;
    private HanEditText item3;
    private HanEditText item4;
    private HanEditText item5;
    private HanEditText item6;

    private SharedPreferences sharedPreferences;

    // call back listener
    private MainActivity.Listener listener;

    SettingDialog(Context c, View view, MainActivity.Listener mainListener) {
        super(c);
        listener = mainListener;

        // init
        sharedPreferences = getContext().getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);

        list1 = view.findViewById(R.id.list_layout_1);
        list2 = view.findViewById(R.id.list_layout_2);
        list3 = view.findViewById(R.id.list_layout_3);
        list4 = view.findViewById(R.id.list_layout_4);
        list5 = view.findViewById(R.id.list_layout_5);
        list6 = view.findViewById(R.id.list_layout_6);

        item1 = view.findViewById(R.id.item_1);
        item2 = view.findViewById(R.id.item_2);
        item3 = view.findViewById(R.id.item_3);
        item4 = view.findViewById(R.id.item_4);
        item5 = view.findViewById(R.id.item_5);
        item6 = view.findViewById(R.id.item_6);
        numberLayout = view.findViewById(R.id.number_layout);

        saveButton = view.findViewById(R.id.save_button);
        saveButton.setOnClickListener(v -> {
            saveOption();
        });
        view.findViewById(R.id.cancel_button).setOnClickListener(v -> dismiss());

        numberSpinner = view.findViewById(R.id.number_spinner);

        final ArrayList<String> itemList = new ArrayList<>();
        itemList.add("2");
        itemList.add("3");
        itemList.add("4");
        itemList.add("5");
        itemList.add("6");

        numberSpinner.setAdapter(new HanStringArrayAdapter(getContext(), android.R.layout.simple_spinner_item, itemList));
        numberSpinner.setOnKeyListener((v, keyCode, event) -> {
            if ((event.getAction() == KeyEvent.ACTION_UP) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                saveOption();

                return true;
            } else
                return false;
        });
        numberSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                numberOption = String.format("%d", position + 2);

                list6.setVisibility(View.GONE);
                list5.setVisibility(View.GONE);
                list4.setVisibility(View.GONE);
                list3.setVisibility(View.GONE);
                list2.setVisibility(View.GONE);
                list1.setVisibility(View.GONE);

                // 5개라면 5~1 까지 추가해야 하므로 switch 문에 break 가 없음
                switch (Integer.parseInt(numberOption)) {
                    case SIXTH_ITEM:
                        list6.setVisibility(View.VISIBLE);
                        item6.setText(sharedPreferences.getString("ITEM6", "6"));
                    case FIFTH_ITEM:
                        list5.setVisibility(View.VISIBLE);
                        item5.setText(sharedPreferences.getString("ITEM5", "5"));
                    case FOURTH_ITEM:
                        list4.setVisibility(View.VISIBLE);
                        item4.setText(sharedPreferences.getString("ITEM4", "4"));
                    case THIRD_ITEM:
                        list3.setVisibility(View.VISIBLE);
                        item3.setText(sharedPreferences.getString("ITEM3", "3"));
                    case SECOND_ITEM:
                        list2.setVisibility(View.VISIBLE);
                        item2.setText(sharedPreferences.getString("ITEM2", "2"));
                    case FIRST_ITEM:
                        list1.setVisibility(View.VISIBLE);
                        item1.setText(sharedPreferences.getString("ITEM1", "1"));
                    default:
                        break;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {/* Nothing to do */}
        });

        // 이미 설정되어 있는 옵션 설정으로 Spinner 의 값을 셋팅해줌
        final int DEFAULT_DIFFERENCE_VALUE = 2;
        numberOption = sharedPreferences.getString("NUMBER", "3");
        numberSpinner.setSelection(Integer.parseInt(numberOption) - DEFAULT_DIFFERENCE_VALUE);
    }

    // 설정한 옵션을 SharedPreference 에 저장
    private void saveOption() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("NUMBER", String.format("%d", numberSpinner.getSelectedItemPosition() + 2));

        //HanApplication.getInstance(getContext()).getHanDevice().displayAndPlayTTS(getContext().getString(R.string.complete_save), true);

        ArrayList<String> saveItems = new ArrayList<>();

        // 5개라면 5~1 까지 추가해야 하므로 switch 문에 break 가 없음
        switch (Integer.parseInt(numberOption)) {
            case SIXTH_ITEM:
                editor.putString("ITEM6", item6.getText().toString());
                saveItems.add(item6.getText().toString());
            case FIFTH_ITEM:
                editor.putString("ITEM5", item5.getText().toString());
                saveItems.add(item5.getText().toString());
            case FOURTH_ITEM:
                editor.putString("ITEM4", item4.getText().toString());
                saveItems.add(item4.getText().toString());
            case THIRD_ITEM:
                editor.putString("ITEM3", item3.getText().toString());
                saveItems.add(item3.getText().toString());
            case SECOND_ITEM:
                editor.putString("ITEM2", item2.getText().toString());
                saveItems.add(item2.getText().toString());
            case FIRST_ITEM:
                editor.putString("ITEM1", item1.getText().toString());
                saveItems.add(item1.getText().toString());
            default:
                break;
        }
        listener.onCompletedSaveOption(saveItems);
        editor.apply();

        dismiss();
    }

    // 취소 버튼을 누르거나 종료 시 대화상자 종료
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        final int scanCode = USB2Braille.getInstance().convertUSBtoBraille(getContext(), event);
        if (event.getAction() == KeyEvent.ACTION_UP && HimsCommonFunc.isExitKey(event.getScanCode(), event.getKeyCode())) {
            this.dismiss();
            return true;
        }
        if (event.getAction() == KeyEvent.ACTION_UP && (scanCode == (HanBrailleKey.HK_ADVANCE4) ||
                (scanCode == (HanBrailleKey.HK_Z | HanBrailleKey.HK_SPACE)))) {
            this.dismiss();
            return true;
        }
        return super.dispatchKeyEvent(event);
    }
}