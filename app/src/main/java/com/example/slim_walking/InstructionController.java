package com.example.slim_walking;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.PersistableBundle;

import androidx.annotation.Nullable;

public class InstructionController{
    private Context context;
    String dialogVersion;
    String dialog;


    public InstructionController(Context current) {
        context = current;

        dialogVersion = context.getResources().getString(R.string.instruction_ver);
        dialog = context.getResources().getString(R.string.instruction_dialog);
    }

    public void checkForUpdate() {
        // TODO : retrieve from server most recent version
        String recentVersion = "1.0";
        if(!recentVersion.equals(dialogVersion)) {
            // update version and dialog
            // dialog = new dialog
        }
    }

    public String getDialog() {
        return dialog;
    }

}
