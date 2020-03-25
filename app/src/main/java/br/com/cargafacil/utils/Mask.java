package br.com.cargafacil.utils;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;

import androidx.appcompat.widget.AppCompatEditText;

import java.text.DecimalFormat;
import java.util.Objects;

import static br.com.cargafacil.utils.Utils.MY_LOG_TAG;

public abstract class Mask {

    public static String unmask(String s) {
        return s.replaceAll("[^0-9]*", "");
    }

    public static String unmaskPlaca(String s) {
        return s.replaceAll("[^0-9]*", "");
    }

    public static TextWatcher insert(final AppCompatEditText editText, String maskFormat) {
        return new TextWatcher() {
//
            String old = "";
//            String teste = ""

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                editText.removeTextChangedListener(this);

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
                editText.setText(mascara);
                editText.setSelection(mascara.length());
                editText.addTextChangedListener(this);
            }

            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            public void afterTextChanged(Editable s) {
            }
        };
    }

    public static TextWatcher insert(final AppCompatEditText campo) {
        String pattern = "###,###";
        DecimalFormat myFormatter = new DecimalFormat(pattern);
        final String[] str = new String[1];

        return new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int after) {
                campo.removeTextChangedListener(this);
                try {
                    str[0] = s.toString();
                    boolean hasMask = (str[0].contains(".") || str[0].contains(",") || str[0].contains("-"));
                    if (hasMask) {
                        str[0] = unmask(str[0]);
                    }
                    Log.d(MY_LOG_TAG, "STR -> " + str[0]);
                    campo.setText((myFormatter.format(Integer.parseInt(str[0]))).replace(".", ","));
                    campo.setSelection(Objects.requireNonNull(campo.getText()).length()); //leva ponteiro para o final
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                campo.addTextChangedListener(this);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
    }
}
