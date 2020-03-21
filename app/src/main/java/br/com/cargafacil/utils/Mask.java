package br.com.cargafacil.utils;

import android.text.Editable;
import android.text.TextWatcher;

import androidx.appcompat.widget.AppCompatEditText;

public abstract class Mask {

    private static String unmask(String s) {
        return s.replaceAll("[^0-9]*", "");
    }

    public static String unmaskPlaca(String s) {
        return s.replaceAll("[^0-9]*", "");
    }

    public static TextWatcher insert(final AppCompatEditText editText, String maskFormat) {
        return new TextWatcher() {
            boolean isUpdating;
            String old = "";

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String str;
                if (!maskFormat.equals("###-####")) {
                    str = Mask.unmask(s.toString());
                } else {
                    str = s.toString().toUpperCase();
                    if (str.contains("-")) {
                        str = str.replace("-", "");
                    }
                }

                String mascara = "";
                if (isUpdating) {
                    old = str;
                    isUpdating = false;
                    return;
                }
                int i = 0;
                for (char m : maskFormat.toCharArray()) {
                    if ((m != '#' && str.length() > old.length()) || (m != '#' && str.length() < old.length() && str.length() != i)) {
                        mascara += m;
                        continue;
                    }

                    try {
                        mascara += str.charAt(i);
                    } catch (Exception e) {
                        e.printStackTrace();
                        break;
                    }
                    i++;
                }
                isUpdating = true;
                editText.setText(mascara);
                editText.setSelection(mascara.length());
            }

            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            public void afterTextChanged(Editable s) {
            }
        };
    }
}
