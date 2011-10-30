package edu.sjsu.cs.davsync;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.view.View;
import android.view.View.OnClickListener;

public class davsync extends Activity
{

    private Button saveButton, clearButton;
    private class ButtonListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            finish();
        }
    }

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info);

        this.saveButton = (Button)this.findViewById(R.id.btnSave);
        this.saveButton.setOnClickListener(new ButtonListener());

        this.clearButton = (Button)this.findViewById(R.id.btnClear);
        this.clearButton.setOnClickListener(new ButtonListener());

        this.clearButton = (Button)this.findViewById(R.id.btnExit);
        this.clearButton.setOnClickListener(new ButtonListener());
    }
}
