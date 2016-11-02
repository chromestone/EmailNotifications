package com.gmail.absolutevanillahelp.EMailNotification;

import java.util.*;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class EMailCommands implements CommandExecutor {

	private EMailNotification plugin;
	private final int coolDown;
	private final String emailSubject;
	private final String emailContent;
	private ArrayList<String> requestDelay;

	public EMailCommands(EMailNotification instance, String emailSubject, String emailContent, int coolDown) {
		plugin = instance;
		this.emailSubject = emailSubject;
		this.emailContent = emailContent;
		this.coolDown = coolDown;
		requestDelay = new ArrayList<String>();
	}

	public ArrayList<String> getRequestDelay() {
		return requestDelay;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if (sender instanceof Player) {
			Player player = (Player) sender;
			if (cmd.getName().equals("Request") && args.length > 0) {
				String playerName = args[0].toLowerCase();
				
				if (!player.hasPermission("EMailNotify.Request." + playerName)) {
					player.sendRawMessage(ChatColor.RED + "You do not have permission to request " + args[0]);
					return true;
				}
				
				if (plugin.getServer().getPlayer(args[0]) == null) {
					if (!requestDelay.contains(playerName)) {
						if (plugin.getNotifiablePlayers().containsKey(playerName)) {
							if (plugin.getNotifiablePlayers().get(playerName)) {
								if (plugin.getPlayerAddresses().containsKey(playerName)) {
									String message = "";
									if (args.length > 1 && player.hasPermission("EMailNotify.Email." + playerName)) {
										message = args[1];
									}
									
									boolean sent = false;
									try {
										sent = plugin.getMailSender().send(String.format(emailSubject, player.getName(), message), String.format(emailContent, player.getName(), message), plugin.getPlayerAddresses().get(playerName));
									}
									catch (IllegalFormatException e) {
										player.sendRawMessage(ChatColor.RED + "Could not send a request due to inproper formatting! (check the config)");
										return true;
									}
									catch (Exception e) {
										sent = false;
									}
									
									if (sent) {
										if (coolDown > 0) {
											requestDelay.add(playerName);
											plugin.getServer().getScheduler().runTaskLaterAsynchronously(plugin, new RequestDelay(this, player.getName()), coolDown*60*20);
										}
										player.sendRawMessage(ChatColor.GREEN + "Your request has been proccessed, " + args[0] + " will try to gain presence.");
									}
									else {
										player.sendRawMessage(ChatColor.RED + "Could not send a request! (invalid credentials or couldn't bypass gmail security checks?)");
									}
								}
								else {
									player.sendRawMessage(ChatColor.RED + "No address specified for " + args[0]);
								}
							}
							else {
								player.sendRawMessage(ChatColor.RED + args[0] + " is busy, cannot come on.");
							}
						}
						else {
							player.sendRawMessage(ChatColor.RED + args[0] + " has not been listed for notifications!");
						}
					}
					else {
						player.sendRawMessage(ChatColor.RED + "Must wait at least " + coolDown + " minutes since last request of " + args[0] +"!");
					}
				}
				else {
					player.sendRawMessage(ChatColor.RED + "That player is already online, is the player afk?");
				}
				return true;
			}
			else if (cmd.getName().equals("ToggleNotification")) {
				String playerName = player.getName().toLowerCase();
				if (plugin.getNotifiablePlayers().containsKey(playerName)) {
					if (plugin.getNotifiablePlayers().get(playerName)) {
						plugin.getNotifiablePlayers().put(playerName, false);
						player.sendRawMessage(ChatColor.GREEN + "Disabled notifications for you.");
					}
					else {
						plugin.getNotifiablePlayers().put(playerName, true);
						player.sendRawMessage(ChatColor.GREEN + "Enabled notifications for you.");
					}
				}
				else {
					player.sendRawMessage(ChatColor.RED + "You have not been listed for notifications!");
				}
				return true;
			}
		}
		return false;

	}

}
