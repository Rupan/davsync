package edu.sjsu.cs.davsync;

import android.util.Log;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.File;
import java.io.InputStream;
import java.lang.IllegalArgumentException;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.FileRequestEntity;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.jackrabbit.webdav.MultiStatus;
import org.apache.jackrabbit.webdav.client.methods.PropFindMethod;
import org.apache.jackrabbit.webdav.DavException;

// TODO:
// 1. what happens if the directory on the sd card doesn't exist?
// 2. is the sd card always as /sdcard?

public class DAVNetwork {
	private String url, path;
	private Credentials creds;
	HttpClient client;
	
	public DAVNetwork(Profile profile) {
		url = "https://" + profile.getHostname() + profile.getResource();
		creds = new UsernamePasswordCredentials(profile.getUsername(), profile.getPassword());
		path = "/sdcard" + profile.getResource();
		client = new HttpClient();
		client.getState().setCredentials(AuthScope.ANY, creds);
		client.getHttpConnectionManager().getParams().setConnectionTimeout(5000);
	}
	
	public MultiStatus propfind() throws IOException {
		final String TAG = "DAVNetwork::propfind";
        
        try {
        	PropFindMethod pfm = new PropFindMethod(url);
            int ret = client.executeMethod(pfm);
            if( ret != 207 ) { throw new IOException(); }
            MultiStatus ms = pfm.getResponseBodyAsMultiStatus();
            return ms;
        } catch( HttpException he ) {
            Log.w(TAG, "Caught HttpException: " + he.getMessage());
        } catch ( IOException ioe ) {
            Log.w(TAG, "Caught IOException: " + ioe.getMessage());
        } catch( IllegalArgumentException iae ) {
            // if e.g. one of the Profile fields contains a space
            Log.w(TAG, "Caught IllegalArgumentException: " + iae.getMessage());
        } catch ( DavException de ) {
        	// MultiStatus
        	Log.w(TAG, "Caught DavException: " + de.getMessage());
        }
        // we'll only get here in case of e.g. a network error or an invalid Profile
        throw new IOException();
	}
	
	public void upload() {
		final String TAG = "DAVNetwork::upload";
        
        PutMethod pm = new PutMethod(url);
		pm.setRequestEntity(new FileRequestEntity(new File(path), "binary/octet-stream"));
		try {
			int ret = client.executeMethod(pm);
			if ( ret != HttpStatus.SC_NO_CONTENT ) {
				Log.d(TAG, "Failed to execute Put method: " + ret);
			} else {
				Log.d(TAG, "Put method successfully completed");
			}
		} catch (HttpException he) {
			Log.w(TAG, "Caught HttpException: " + he.getMessage());
		} catch (IOException ioe) {
			Log.w(TAG, "Caught IOException: " + ioe.getMessage());
		} finally {
			pm.releaseConnection();
		}
	}
	
	public void download() {
		final String TAG = "DAVNetwork::upload";
		
		GetMethod gm = new GetMethod(url);
		gm.setFollowRedirects(true);
		try {
			int ret = client.executeMethod(gm);
			if ( ret != HttpStatus.SC_OK ) {
				Log.d(TAG, "Failed to execute Get method: " + ret);
			} else {
				Log.d(TAG, "Get method successfully completed");
			}
			// http://www.eboga.org/java/open-source/httpclient-demo.html
			InputStream input = gm.getResponseBodyAsStream();
			FileOutputStream output = new FileOutputStream(path, false);
			int count = -1;
			byte[] buffer = new byte[8192];
			while( (count = input.read(buffer)) != -1 ) {
				output.write(buffer, 0, count);
			}
			output.flush();
			output.close();
		}catch (HttpException he) {
			Log.w(TAG, "Caught HttpException: " + he.getMessage());
		} catch (IOException ioe) {
			Log.w(TAG, "Caught IOException: " + ioe.getMessage());
		} finally {
			gm.releaseConnection();
		}
	}
}
