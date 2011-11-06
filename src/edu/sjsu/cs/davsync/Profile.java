package edu.sjsu.cs.davsync;

public class Profile {
	private String username, password, hostname, resource;

	public Profile(String hostname, String resource, String username, String password) {
		this.username = new String(username);
		this.password = new String(password);
		this.hostname = new String(hostname);
		this.resource = new String(resource);
	}

	public String getHostname() {
		return new String(hostname);
	}

	public String getResource() {
		return new String(resource);
	}

	public String getUsername() {
		return new String(username);
	}

	public String getPassword() {
		return new String(password);
	}
}

