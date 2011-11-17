package edu.sjsu.cs.davsync;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.view.View;
import android.view.View.OnClickListener;
import android.content.Context;

public class davsync extends Activity {

    private Context context;
    private DSDatabase db;
    private EditText[] field = new EditText[4]; // hostname, resource, username, password
    private Button[] button = new Button[4]; // save, clear, test, sync

    private enum ButtonType {
        SAVE, CLEAR, TEST, SYNC
    }

    private class ButtonListener implements OnClickListener {
        ButtonType type;

        public ButtonListener(ButtonType type) {
            this.type = type;
        }
        public void onClick(View v) {
            switch( type ) {
            case SAVE:
                try {
                        db.addProfile(getCurrentProfile());
                } catch( ConfigurationException ce ) {
                        Toast toast = Toast.makeText(context, ce.toString(), Toast.LENGTH_SHORT);
                        toast.show();
                }
                break;
            case CLEAR:
                try {
                        db.delProfile(getCurrentProfile());
                        clearTextFields();
                } catch( ConfigurationException ce ) {
                        Toast toast = Toast.makeText(context, ce.toString(), Toast.LENGTH_SHORT);
                        toast.show();
                }
                break;
            case TEST:
                test();
                break;
            case SYNC:
            	sync();
            	break;
            }
        }
    }

    // TODO: add a dialog during the test
    // http://developer.android.com/guide/topics/ui/dialogs.html
    private void test() {
    	Toast toast = Toast.makeText(context, "Unspecified failure", Toast.LENGTH_SHORT);
    	try {
			DAVNetwork net = new DAVNetwork(getCurrentProfile());
			if( net.testRemote() ) {
				toast = Toast.makeText(context, "Test succeeded", Toast.LENGTH_SHORT);
			} else {
				toast = Toast.makeText(context, "Test failed", Toast.LENGTH_SHORT);
			}
		} catch (ConfigurationException ce) {
			toast = Toast.makeText(context, ce.toString(), Toast.LENGTH_SHORT);
		} finally {
			toast.show();
		}
    }
    
    private void sync() {
    	// TODO
    }

    // read the state of all fields from memory and return a Profile object
    private Profile getCurrentProfile() throws ConfigurationException {
        String host = field[0].getText().toString();
        if( ! host.matches("[a-zA-Z0-9.]+") ) {
        	throw new ConfigurationException("please input a valid hostname");
        }
        String rsrc = field[1].getText().toString();
        if( ! rsrc.matches("[a-zA-Z0-9./]+") ) {
        	throw new ConfigurationException("please input a valid resource");
        }
        String user = field[2].getText().toString();
        if( user.length() == 0 ) {
        	throw new ConfigurationException("please input a valid username");
        }
        String pass = field[3].getText().toString();
        if( pass.length() == 0 ) {
        	throw new ConfigurationException("please input a valid password");
        }
        return new Profile(host, rsrc, user, pass);
    }

    // removes any text from all fields
    private void clearTextFields() {
        for(int i = 0; i < 4; i++)
            field[i].setText("");
    }

    /** Called when the activity is first created. */
    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info);
        context = getApplicationContext();
        db = new DSDatabase(context);

        // handle button events
        button[0] = (Button)this.findViewById(R.id.btnSave);
        button[1] = (Button)this.findViewById(R.id.btnClear);
        button[2] = (Button)this.findViewById(R.id.btnTest);
        button[3] = (Button)this.findViewById(R.id.btnSync);
        button[0].setOnClickListener(new ButtonListener(ButtonType.SAVE));
        button[1].setOnClickListener(new ButtonListener(ButtonType.CLEAR));
        button[2].setOnClickListener(new ButtonListener(ButtonType.TEST));
        button[3].setOnClickListener(new ButtonListener(ButtonType.SYNC));

        // access to text fields
        field[0] = (EditText)findViewById(R.id.hostname);
        field[1] = (EditText)findViewById(R.id.resource);
        field[2] = (EditText)findViewById(R.id.username);
        field[3] = (EditText)findViewById(R.id.password);

        Profile p = db.getProfile();
        field[0].setText(p.getHostname());
        field[1].setText(p.getResource());
        field[2].setText(p.getUsername());
        field[3].setText(p.getPassword());
    }
}
