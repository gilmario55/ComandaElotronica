package com.example.comandaelotrnica.helper;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.comandaelotrnica.R;

public class ToastConfig {

    public static void showCustomAlert(Context context, LayoutInflater inflater, String texto)
    {
        // Call toast.xml file for toast layout
        View view = inflater.inflate(R.layout.taost, null);
        TextView text = view.findViewById(R.id.tostId);
        text.setText(texto);
        Toast toast = new Toast(context);

        // Set layout to toast
        toast.setView(view);
        toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM,
                10, 10);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.show();
    }
}
