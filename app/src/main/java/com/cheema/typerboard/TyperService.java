package com.cheema.typerboard;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.inputmethodservice.InputMethodService;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

public class TyperService extends InputMethodService {
    private final Handler h = new Handler(Looper.getMainLooper());
    private boolean running = false;
    private int idx = 0;
    private TextView status;

    private final Runnable tick = new Runnable() {
        @Override
        public void run() {
            if (!running) return;
            SharedPreferences p = getSharedPreferences("cfg", MODE_PRIVATE);
            List<String> list = new ArrayList<>();
            for (String s : p.getString("items", "A\nB\nC").split("\n")) {
                if (!s.trim().isEmpty()) list.add(s.trim());
            }
            if (list.isEmpty()) { halt("Items khali hain"); return; }
            String it = list.get(idx % list.size());
            idx++;
            InputConnection ic = getCurrentInputConnection();
            if (ic == null) { halt("Text box nahi mila"); return; }
            ic.commitText(it, 1);
            if (p.getBoolean("send", true)) sendEnter(ic);
            if (status != null) status.setText("Sent: " + it);
            float d = Math.max(1f, p.getFloat("delay", 2f));
            h.postDelayed(this, (long) (d * 1000));
        }
    };

    private void sendEnter(InputConnection ic) {
        EditorInfo ei = getCurrentInputEditorInfo();
        int a = ei == null ? 0 : (ei.imeOptions & EditorInfo.IME_MASK_ACTION);
        if (a != EditorInfo.IME_ACTION_NONE && a != EditorInfo.IME_ACTION_UNSPECIFIED) {
            ic.performEditorAction(a);
        } else {
            ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER));
            ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_ENTER));
        }
    }

    private void halt(String msg) {
        running = false;
        h.removeCallbacks(tick);
        if (status != null) status.setText(msg);
    }

    private Button btn(String text, String color) {
        Button b = new Button(this);
        b.setText(text);
        b.setTextColor(Color.WHITE);
        b.setBackgroundColor(Color.parseColor(color));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        lp.setMargins(8, 8, 8, 8);
        b.setLayoutParams(lp);
        return b;
    }

    @Override
    public View onCreateInputView() {
        LinearLayout l = new LinearLayout(this);
        l.setOrientation(LinearLayout.VERTICAL);
        l.setBackgroundColor(Color.parseColor("#0d0d0d"));
        l.setPadding(24, 24, 24, 24);

        status = new TextView(this);
        status.setText("Ready");
        status.setTextColor(Color.WHITE);
        status.setTextSize(18);
        status.setGravity(Gravity.CENTER);
        l.addView(status);

        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        Button start = btn("START", "#1faa3b");
        Button stop = btn("STOP", "#d62828");
        Button sw = btn("KEYBOARD", "#444444");
        row.addView(start);
        row.addView(stop);
        row.addView(sw);
        l.addView(row);

        start.setOnClickListener(v -> {
            if (!running) {
                running = true;
                idx = 0;
                status.setText("Running...");
                h.post(tick);
            }
        });
        stop.setOnClickListener(v -> halt("Stopped"));
        sw.setOnClickListener(v -> ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).showInputMethodPicker());
        return l;
    }

    @Override
    public void onFinishInputView(boolean finishingInput) {
        halt("Stopped");
        super.onFinishInputView(finishingInput);
    }
}
