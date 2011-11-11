package edu.sjsu.cs.davsync;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.view.View;
import android.view.View.OnClickListener;
import android.util.Log;

// webdav client & dependencies
import java.util.*;
import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.params.*;
import org.apache.commons.httpclient.auth.*;
import org.apache.jackrabbit.webdav.*;
import org.apache.jackrabbit.webdav.client.methods.*;
import org.apache.jackrabbit.webdav.header.*;
import org.apache.jackrabbit.webdav.io.*;
import org.apache.jackrabbit.webdav.lock.*;
import org.apache.jackrabbit.webdav.server.*;
import org.apache.jackrabbit.webdav.util.*;
import org.apache.jackrabbit.webdav.xml.*;
import org.apache.jackrabbit.webdav.property.*;

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
		// client init
		HostConfiguration hostConfig = new HostConfiguration();
		hostConfig.setHost("razor.temerity.net", 443, "https");
		HttpConnectionManager connectionManager = new MultiThreadedHttpConnectionManager();
		HttpConnectionManagerParams params = new HttpConnectionManagerParams();
		int maxHostConnections = 20;
		params.setMaxConnectionsPerHost(hostConfig, maxHostConnections);
		connectionManager.setParams(params);
		HttpClient client = new HttpClient(connectionManager);
		Credentials creds = new UsernamePasswordCredentials("cs266", "bie0Up7X");
		client.getState().setCredentials(AuthScope.ANY, creds);
		client.setHostConfiguration(hostConfig);

		//copy a file: source, dest, overwrite
		DavMethod copy;
		try {
			copy = new CopyMethod("https://razor.temerity.net/kp/random.kdb", "https://razor.temerity.net/kp/junk.kdb", true);
			client.executeMethod(copy);
			Log.d("TEST:", copy.getStatusCode() + " " + copy.getStatusText());
		} catch(Exception e) {
			Log.d("TEST:", "" + e);
		}

		// propfind
		DavMethod pFind;
		MultiStatus multiStatus;
		DavPropertySet props;
		try {
			pFind = new PropFindMethod("/kp/random.kdb", DavConstants.PROPFIND_ALL_PROP, DavConstants.DEPTH_INFINITY);
			client.executeMethod(pFind);
			multiStatus = pFind.getResponseBodyAsMultiStatus();
			props = multiStatus.getResponses()[0].getProperties(200);
			Collection propertyColl = props.getContent();
			for(Iterator iterator = propertyColl.iterator(); iterator.hasNext();){
				DefaultDavProperty tmpProp = (DefaultDavProperty)iterator.next();
				Log.d("TEST:", tmpProp.getName() + " " + tmpProp.getValue());
			}
		} catch(Exception e) {
			Log.d("TEST:", "" + e);
		}
    }

    // read the state of all fields from memory and return a Profile object
    private Profile getCurrentProfile() {
        Profile p;
        p = new Profile(
            field[0].getText().toString(),
            field[1].getText().toString(),
            field[2].getText().toString(),
            field[3].getText().toString());
        return p;
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
