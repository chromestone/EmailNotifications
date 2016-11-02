package com.gmail.absolutevanillahelp.EMailNotification;

public class RequestDelay implements Runnable {

	private EMailCommands eMailCmds;
	private final String name;

	public RequestDelay(EMailCommands instance, String name) {
		eMailCmds = instance;
		this.name = name;
	}

	@Override
	public void run() {
		eMailCmds.getRequestDelay().remove(name);
	}
}
