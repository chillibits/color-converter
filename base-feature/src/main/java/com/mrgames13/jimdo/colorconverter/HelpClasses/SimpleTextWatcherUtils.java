package com.mrgames13.jimdo.colorconverter.HelpClasses;

import android.text.Editable;
import android.text.TextWatcher;

public class SimpleTextWatcherUtils implements TextWatcher{
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {}

    @Override
    public void afterTextChanged(Editable s) {}
}