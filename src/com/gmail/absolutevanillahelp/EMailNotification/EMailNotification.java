package com.gmail.absolutevanillahelp.EMailNotification;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.regex.*;

import org.bukkit.plugin.java.JavaPlugin;

public class EMailNotification extends JavaPlugin {
	
	private HashMap<String, Boolean> notifiablePlayers;
	private HashMap<String, String> playerAddresses;
	private SendMailTLS mailSender;
	
	public SendMailTLS getMailSender() {
		return mailSender;
	}
	
	public HashMap<String, Boolean> getNotifiablePlayers() {
		return notifiablePlayers;
	}
	
	public HashMap<String, String> getPlayerAddresses() {
		return playerAddresses;
	}

	@Override
	public void onEnable() {
		
		notifiablePlayers = new HashMap<String, Boolean>();
		playerAddresses = new HashMap<String, String>();
		
		getLogger().info("Made by chromestone(bukkit alias: chromeaton), for tech support PM me on dev.bukkit.org");
		
		isNewestVersion();
		
		saveDefaultConfig();

		if (getConfig().isSet("sender.address") && getConfig().isSet("sender.password")) {
			mailSender = new SendMailTLS(getConfig().getString("sender.address"), getConfig().getString("sender.password"));

			if (getConfig().isSet("request")) {
				for (String name : getConfig().getConfigurationSection("request").getKeys(false)) {
					notifiablePlayers.put(name.toLowerCase(), true);
					playerAddresses.put(name.toLowerCase(), getConfig().getString("request." + name));
				}
			}

			String emailSubject, emailContent;
			if (getConfig().isSet("email.subject") && getConfig().isSet("email.content")) {
				emailSubject = getConfig().getString("email.subject").replace("[player]", "%1$s").replace("[msg]", "%2$s");
				emailContent = getConfig().getString("email.content").replace("[player]", "%1$s").replace("[msg]", "%2$s");
				
				
//				emailSubject = getConfig().getString("email.subject");
//				if (emailSubject.contains("[") || emailSubject.contains("]")) {
//					System.out.println(emailSubject);
//					emailSubject = emailSubject.substring(3, emailSubject.length()-3);
//					System.out.println(emailSubject);
//					emailSubject = emailSubject.replace("[player]", "%1$s").replace("[msg]", "%2$s");
//				}
//				
//				emailContent = getConfig().getString("email.content");
//				if (emailContent.contains("[") || emailContent.contains("]")) {
//					emailContent = emailContent.substring(3, emailContent.length()-3);
//					emailContent = emailContent.replace("[player]", "%1$s").replace("[msg]", "%2$s");
//				}
			}
			else {
				emailSubject = "[Server] INFO";
				emailContent = "%1$s has requested your presence! %2$s";
			}
			
			int coolDown = 0;
			if (getConfig().isSet("cool-down")) {
				coolDown = getConfig().getInt("cool-down");
			}

			EMailCommands eMailCmds = new EMailCommands(this, emailSubject, emailContent, coolDown);

			getCommand("Request").setExecutor(eMailCmds);
			getCommand("ToggleNotification").setExecutor(eMailCmds);
		}
		else {
			getLogger().warning("Could not load EMailNotification, no credentials set.(if first time using, stop server, and check config.)");
		}
	}
	
	private void isNewestVersion() {
		
		try {
			URL url = new URL("http://chromestone.wix.com/emailnotifyversion?_escaped_fragment_=");
			URLConnection con = url.openConnection();
			InputStream is = con.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			
			String line = null;
			String xmlString = "";
			while ((line = br.readLine()) != null) {
				xmlString += line;
			}
			
			Pattern pattern = Pattern.compile("<p class=\"font_8\">" + ".+?" + "</p>");
			Matcher match = pattern.matcher(xmlString);
			if(match.find()) {
				String version = match.group();
				version = version.substring(18, version.length()-4);
				if (!"1.3".equals(version)) {
					getLogger().info("A new update is available. Please check dev.bukkit.org/bukkit-plugins/e-mail-notifications");
				}
				else {
					getLogger().info("This plugin (Email Notifications) is up to date.");
				}
				
			}
		}
		catch (Exception e) {}
	}
	
	@Override
	public void onDisable() {
		
		getCommand("Request").setExecutor(null);
		getCommand("ToggleNotification").setExecutor(null);
		mailSender = null;
	}
}
