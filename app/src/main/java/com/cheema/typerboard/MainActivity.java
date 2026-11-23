package com.cheema.typerboard;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.Settings;
import android.text.InputType;
import android.view.Gravity;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        final SharedPreferences p = getSharedPreferences("cfg", MODE_PRIVATE);
        StringBuilder d = new StringBuilder();
        for (char c = 'A'; c <= 'Z'; c++) d.append(c).append("\n");
        for (char c = '0'; c <= '9'; c++) d.append(c).append("\n");

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(40, 60, 40, 40);
        root.setBackgroundColor(Color.parseColor("#0d0d0d"));

        TextView t = new TextView(this);
        t.setText("ALPHABET / NUMBER DROP");
        t.setTextColor(Color.parseColor("#ff2b2b"));
        t.setTextSize(22);
        root.addView(t);

        TextView l1 = new TextView(this);
        l1.setText("Items (ek line mein ek)");
        l1.setTextColor(Color.WHITE);
        l1.setPadding(0, 30, 0, 0);
        root.addView(l1);

        final EditText items = new EditText(this);
        items.setText(p.getString("items", d.toString().trim()));
        items.setTextColor(Color.WHITE);
        items.setMinLines(6);
        items.setGravity(Gravity.TOP);
        items.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        root.addView(items);

        TextView l2 = new TextView(this);
        l2.setText("Delay (seconds, kam se kam 1)");
        l2.setTextColor(Color.WHITE);
        l2.setPadding(0, 30, 0, 0);
        root.addView(l2);

        final EditText delay = new EditText(this);
        delay.setText(String.valueOf(p.getFloat("delay", 2f)));
        delay.setTextColor(Color.WHITE);
        delay.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        root.addView(delay);

        final CheckBox send = new CheckBox(this);
        send.setText("Auto send (type ke baad Enter)");
        send.setTextColor(Color.WHITE);
        send.setChecked(p.getBoolean("send", true));
        root.addView(send);

        Button save = new Button(this);
        save.setText("SAVE");
        save.setOnClickListener(v -> {
            float dl = 2f;
            try { dl = Float.parseFloat(delay.getText().toString()); } catch (Exception e) { }
            p.edit().putString("items", items.getText().toString())
                    .putFloat("delay", Math.max(1f, dl))
                    .putBoolean("send", send.isChecked()).apply();
            Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();
        });
        root.addView(save);

        Button enable = new Button(this);
        enable.setText("1) KEYBOARD ENABLE KARO");
        enable.setOnClickListener(v -> startActivity(new Intent(Settings.ACTION_INPUT_METHOD_SETTINGS)));
        root.addView(enable);

        Button choose = new Button(this);
        choose.setText("2) KEYBOARD CHUNO");
        choose.setOnClickListener(v -> ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).showInputMethodPicker());
        root.addView(choose);

        ScrollView sv = new ScrollView(this);
        sv.addView(root);
        setContentView(sv);
    }
}
