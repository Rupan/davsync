package edu.sjsu.cs.davsync;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.view.View;
import android.view.View.OnClickListener;
import android.util.Log;

// webdav code
import java.io.IOException;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpConnectionManager;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.apache.jackrabbit.webdav.client.methods.PropFindMethod;

public class davsync extends Activity {

    private DSDatabase db;
    private EditText[] field = new EditText[4]; // hostname, resource, username, password
    private Button[] button = new Button[3]; // save, clear, test

    private enum ButtonType {
        SAVE, CLEAR, TEST
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
                db.addProfile(getCurrentProfile());
                break;
            case CLEAR:
                db.delProfile(getCurrentProfile());
                clearTextFields();
                break;
            case TEST:
                test();
                break;
            }
        }
    }

    private void test() {
        int ret = -1;
        Log.d("davsync/test", "PROPFIND starting...");

        Profile p = getCurrentProfile();

        HostConfiguration hostConfig = new HostConfiguration();
        hostConfig.setHost(p.getHostname(), 443, "https");

        HttpConnectionManagerParams params = new HttpConnectionManagerParams();
        params.setMaxConnectionsPerHost(hostConfig, 20);

        HttpConnectionManager connectionManager = new MultiThreadedHttpConnectionManager();
        connectionManager.setParams(params);

        HttpClient client = new HttpClient(connectionManager);
        Credentials creds = new UsernamePasswordCredentials(p.getUsername(), p.getPassword());
        client.getState().setCredentials(AuthScope.ANY, creds);
        PropFindMethod pfm;

        try {
            pfm = new PropFindMethod("https://" + p.getHostname() + p.getResource());
            ret = client.executeMethod(pfm);
        } catch( HttpException he ) {
            Log.w("davsync/test", "Caught HttpException: " + he.getMessage());
        } catch ( IOException ioe ) {
            Log.w("davsync/test", "Caught IOException: " + ioe.getMessage() + ioe);
        }
        // ret should be 207 if the resource exists
        Log.d("davsync/test", "PROPFIND returned " + ret);
    }

    // read the state of all fields from memory and return a Profile object
    // TODO: throw an exception if any field is incorrect
    private Profile getCurrentProfile() {
        String host = field[0].getText().toString();
        String rsrc = field[1].getText().toString();
        String user = field[2].getText().toString();
        String pass = field[3].getText().toString();
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
	db = new DSDatabase(this);

        // handle button events
        button[0] = (Button)this.findViewById(R.id.btnSave);
        button[1] = (Button)this.findViewById(R.id.btnClear);
        button[2] = (Button)this.findViewById(R.id.btnTest);
        button[0].setOnClickListener(new ButtonListener(ButtonType.SAVE));
        button[1].setOnClickListener(new ButtonListener(ButtonType.CLEAR));
        button[2].setOnClickListener(new ButtonListener(ButtonType.TEST));

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
