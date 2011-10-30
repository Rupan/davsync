package edu.sjsu.cs.davsync;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.view.View;
import android.view.View.OnClickListener;
import android.util.Log;

public class davsync extends Activity
{

    private Button saveButton, clearButton, exitButton;

    private enum ButtonType {
        SAVE, CLEAR, TEST, EXIT
    }

    private class ButtonListener implements OnClickListener {
        ButtonType type;

        public ButtonListener(ButtonType type) {
            this.type = type;
        }
        @Override
        public void onClick(View v) {
            switch( type ) {
            case SAVE:
                save();
                break;
            case CLEAR:
                clear();
                break;
            case TEST:
                test();
                break;
            case EXIT:
                finish();
            }
        }
    }

    private final String TAG = "davsync";
    // save the server info to local storage
    private void save() {
        Log.d(TAG, "saving data...");
    }
    // clear the fields and delete local storage
    private void clear() {
        Log.d(TAG, "wiping data...");
    }
    // connect to the server & test credentials
    private void test() {
        Log.d(TAG, "testing connection...");
    }

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info);

        saveButton = (Button)this.findViewById(R.id.btnSave);
        saveButton.setOnClickListener(new ButtonListener(ButtonType.SAVE));

        clearButton = (Button)this.findViewById(R.id.btnClear);
        clearButton.setOnClickListener(new ButtonListener(ButtonType.CLEAR));

        exitButton = (Button)this.findViewById(R.id.btnExit);
        exitButton.setOnClickListener(new ButtonListener(ButtonType.EXIT));
    }
}
