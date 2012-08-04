package org.saga.commands;

import org.bukkit.Location;
import org.saga.Saga;
import org.saga.config.EconomyConfiguration;
import org.saga.exceptions.NonExistantSagaPlayerException;
import org.saga.messages.EconomyMessages;
import org.saga.messages.InfoMessages;
import org.saga.messages.PlayerMessages;
import org.saga.messages.SettlementMessages;
import org.saga.player.SagaPlayer;
import org.sk89q.Command;
import org.sk89q.CommandContext;
import org.sk89q.CommandPermissions;


public class EconomyCommands {


	// Coins:
	@Command(
			aliases = {"spay", "pay"},
			usage = "<name> <amount>",
			flags = "",
			desc = "Gives money to someone.",
			min = 2,
			max = 2
	)
	@CommandPermissions({"saga.user.pay"})
	public static void pay(CommandContext args, Saga plugin, SagaPlayer sagaPlayer) {
		
		
		// Arguments:
		SagaPlayer targetPlayer = Saga.plugin().getSagaPlayer(args.getString(0));
		if(targetPlayer == null){
			sagaPlayer.message(EconomyMessages.notOnline(args.getString(0)));
			return;
		}
		
		Double amount = null;
		try {
			amount = Double.parseDouble(args.getString(1));
		} catch (NumberFormatException e) {
			sagaPlayer.message(EconomyMessages.notNumber(args.getString(1)));
			return;
		}
		
		if(amount < 0){
			amount *= -1;
		}
		
		// Enough currency:
		if(sagaPlayer.getCoins() < amount){
			sagaPlayer.message(EconomyMessages.notEnoughCoins());
			return;
		}
		
		// Not online:
		Location playerLocation = sagaPlayer.getLocation();
		Location targetLocation = targetPlayer.getLocation();
		if(playerLocation == null || targetLocation == null){
			sagaPlayer.message(EconomyMessages.tooFarPay());
			return;
		}
		
		double distance = playerLocation.distance(targetLocation);
		double maxDistance = EconomyConfiguration.config().exchangeDistance;
		if(distance > maxDistance){
			sagaPlayer.message(EconomyMessages.tooFarPay(maxDistance));
			return;
		}
		
		// Pay:
		sagaPlayer.removeCoins(amount);
		targetPlayer.addCoins(amount);
		
		// Inform:
		sagaPlayer.message(EconomyMessages.paid(targetPlayer, amount));
		targetPlayer.message(EconomyMessages.gotPaid(sagaPlayer, amount));
		
		
	}
	
	@Command(
			aliases = {"asetwallet"},
			usage = "<name> <amount>",
			flags = "",
			desc = "Gives money to someone.",
			min = 2,
			max = 2
	)
	@CommandPermissions({"saga.admin.setwallet"})
	public static void setWallet(CommandContext args, Saga plugin, SagaPlayer sagaPlayer) {
		
		
		// Arguments:
		String targetName = args.getString(0);
		
		Double amount = null;
		try {
			amount = Double.parseDouble(args.getString(1));
		} catch (NumberFormatException e) {
			sagaPlayer.message(EconomyMessages.notNumber(args.getString(1)));
			return;
		}
		
		// Force player:
		SagaPlayer targetPlayer;
		try {
			targetPlayer = Saga.plugin().forceSagaPlayer(targetName);
		} catch (NonExistantSagaPlayerException e) {
			sagaPlayer.message(PlayerMessages.invalidPlayer(targetName));
			return;
		}

		// Set wallet:
		targetPlayer.setCoins(amount);
		
		// Inform:
		sagaPlayer.message(EconomyMessages.setWallet(sagaPlayer, amount));
		if(targetPlayer != sagaPlayer){
			targetPlayer.message(EconomyMessages.walletWasSet(amount));
		}
		
		// Unforce:
		Saga.plugin().unforceSagaPlayer(targetName);
		return;
		
	}
	
	@Command(
			aliases = {"balance","wallet","bal","emoney"},
			usage = "",
			flags = "",
			desc = "See how much currency you have.",
			min = 0,
			max = 0
	)
	@CommandPermissions({"saga.user.balance"})
	public static void balance(CommandContext args, Saga plugin, SagaPlayer sagaPlayer) {
		
		sagaPlayer.message(EconomyMessages.wallet(sagaPlayer));
		
	}
	
	
	// Other:
	@Command(
			aliases = {"ehelp"},
			usage = "[page number]",
			flags = "",
			desc = "Display economy help.",
			min = 0,
			max = 1
	)
	@CommandPermissions({"saga.user.settlement.help"})
	public static void help(CommandContext args, Saga plugin, SagaPlayer sagaPlayer) {

		
		Integer page = null;
		
		// Arguments:
		if(args.argsLength() == 1){
			try {
				page = Integer.parseInt(args.getString(0));
			} catch (NumberFormatException e) {
				sagaPlayer.message(SettlementMessages.invalidPage(args.getString(0)));
				return;
			}
		}else{
			page = 0;
		}
		
		// Inform:
		sagaPlayer.message(InfoMessages.ehelp(page - 1));

		
	}

	
	
}
