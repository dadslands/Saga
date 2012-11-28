package org.saga.messages;

import java.util.ArrayList;
import java.util.Hashtable;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.saga.buildings.TownSquare;
import org.saga.chunks.Bundle;
import org.saga.config.FactionConfiguration;
import org.saga.config.GeneralConfiguration;
import org.saga.config.ProficiencyConfiguration;
import org.saga.factions.Faction;
import org.saga.factions.FactionClaimManager;
import org.saga.factions.FactionManager;
import org.saga.messages.PlayerMessages.ColourLoop;
import org.saga.player.Proficiency;
import org.saga.player.Proficiency.ProficiencyType;
import org.saga.player.ProficiencyDefinition;
import org.saga.player.SagaPlayer;
import org.saga.settlements.Settlement;
import org.saga.utility.text.StringBook;
import org.saga.utility.text.StringFramer;
import org.saga.utility.text.StringTable;
import org.saga.utility.text.TextUtil;


public class FactionMessages {

public static ChatColor positiveHighlightColor = ChatColor.GREEN;
	


	// Colors:
	public static ChatColor veryPositive = ChatColor.DARK_GREEN; // DO NOT OVERUSE.

	public static ChatColor positive = ChatColor.GREEN;

	public static ChatColor negative = ChatColor.RED;

	public static ChatColor veryNegative = ChatColor.DARK_RED; // DO NOT OVERUSE.

	public static ChatColor unavailable = ChatColor.DARK_GRAY;

	public static ChatColor announce = ChatColor.AQUA;

	public static ChatColor normal1 = ChatColor.GOLD;

	public static ChatColor normal2 = ChatColor.YELLOW;

	public static ChatColor frame = ChatColor.DARK_GREEN;

	

	// General:
	public static String notMember() {
		return negative + "You aren't a faction member.";
	}
	
	public static String notMember(Faction faction, String name){
		return negative + "Player " + name + " isn't a member of the faction.";
	}
	
	public static String noFaction(String factionName) {
		return negative + "Faction " + factionName + " doesn't exist.";
	}
	
	public static String notFormed(Faction faction) {

		return negative + "The faction isn't formed yet.";
		
	}
	
	public static String notFormedInfo(Faction faction) {

		return normal1 + "The faction requires " + (FactionConfiguration.config().formationAmount-faction.getMemberCount()) + " more members.";
		
	}

	
	
	// Retrieval:
	public static String invalidFaction(String factionName) {
		return negative + "Faction " + factionName + " doesn't exist.";
	}
	
	public static String invalidFaction() {
		return negative + "Faction doesn't exist.";
	}
	
	public static String alreadyInFaction() {
		return negative + "You are already in a faction.";
	}
	
	public static String alreadyInFaction(SagaPlayer sagaPlayer) {
		return negative + "Player " + sagaPlayer.getName() + " is already in a faction.";
	}
	
	
	
	// Customisation:
	public static String colour1Set(Faction faction) {

		return faction.getColour2() + "Factions colour I set to " + faction.getColour1() + TextUtil.colour(faction.getColour1()) + faction.getColour2() + ".";
		
	}
	
	public static String colour2Set(Faction faction) {

		return faction.getColour2() + "Factions colour II set to " + faction.getColour2() + TextUtil.colour(faction.getColour2()) + faction.getColour2() + ".";
		
	}
	

	
	// Create delete:
	public static String created(Faction faction) {

		return faction.getColour2() + "Created " + faction(faction, faction.getColour2()) + " faction.";
		
	}
	
	public static String deleted2(Faction faction) {

		return faction.getColour2() + "Deleted " + faction(faction, faction.getColour2()) + " faction.";
		
	}
	
	public static String formed(Faction faction) {

		return faction.getColour2() + "The faction is now fully formed.";
		
	}

	public static String unformed(Faction faction) {

		return faction.getColour2() + "The faction is no longer formed.";
		
	}

	public static String disbanded(Faction faction) {

		return faction.getColour2() + "The faction was disbanded.";
		
	}
	
	public static String disbandedOther(Faction faction) {

		return faction.getColour2() + "Disbanded " + faction(faction, faction.getColour2()) + " faction.";
		
	}
	

	
	// Invite join leave broadcasts:
	public static String wasInvited(SagaPlayer sagaPlayer, Faction faction) {
		return faction.getColour2() + "You were invited to " + faction.getColour1() + faction.getName() + faction.getColour2() + " faction.";
	}
	
	public static String invited(SagaPlayer sagaPlayer, Faction faction) {
		return faction.getColour2() + "" + sagaPlayer.getName() + " was invited to the faction.";
	}
	
	
	public static String haveJoined(SagaPlayer sagaPlayer, Faction faction) {
		return faction.getColour2() + "Joined " + faction.getColour1() + faction.getName() + faction.getColour2() + " faction.";
	}
	
	public static String joined(SagaPlayer sagaPlayer, Faction faction) {
		return faction.getColour2() + "" + sagaPlayer.getName() + " joined the faction.";
	}
	
	
	public static String haveQuit(SagaPlayer sagaPlayer, Faction faction) {
		return faction.getColour2() + "Quit " + faction(faction, faction.getColour2()) + " faction.";
	}
	
	public static String quit(SagaPlayer sagaPlayer, Faction faction) {
		return faction.getColour2() + "" + sagaPlayer.getName() + " quit the faction.";
	}

	
	public static String wasKicked(SagaPlayer sagaPlayer, Faction faction) {
		return faction.getColour2() + "You were kicked from the faction.";
	}
	
	public static String playerKicked(SagaPlayer sagaPlayer, Faction faction) {
		return faction.getColour2() + "" + sagaPlayer.getName() + " was kicked from the faction.";
	}
	
	
	public static String cantKickYourself(SagaPlayer sagaPlayer, Faction faction) {
		return negative + "Can't kick yourself.";
	}
	
	public static String cantKickOwner(Faction faction) {
		return negative + "Can't kick the owner.";
	}
	
	
	public static String notFactionMember(SagaPlayer sagaPlayer, Faction faction) {
		return negative + "Player " + sagaPlayer.getName() + " isn't a member of the faction.";
	}
	
	
	
	// Invite join leave:
	public static String noInvites() {
		return negative + "You don't have any faction invitations.";
	}
	
	public static String noInvites(String factionName) {
		return negative + "You don't have an invitation to " + factionName + " faction.";
	}
	
	public static String declinedInvite(Faction faction) {
		return normal1 + "Declined a join invitation from " + faction(faction, normal1) + " faction.";
	}
	
	public static String declinedInvites() {
		return normal1 + "Declined all faction join invitations.";
	}

	public static String informAccept() {
		return normal1 + "Use /faccept to accept the faction join invitation.";
	}
	
	
	
	// Owner:
	public static String newOwner(Faction faction, String name) {
		return faction.getColour2() + name + " is the new owner of the faction.";
	}

	public static String ownerCantQuit() {
		return negative + "Faction owner can't quit the faction.";
	}
	
	public static String ownerCantQuitInfo() {
		return normal1 + "Use /fresign to declare someone else as the owner.";
	}
	
	
	
	// Other:
	public static String invalidName() {
		
		return negative + "Name must be " + FactionConfiguration.config().getMinNameLength() + "-" + FactionConfiguration.config().getMaxNameLength() + " characters long and only contain letters and numbers.";
		
	}

	public static String inUse(String name) {
		return negative + "Faction name " + name + " is already in use.";
	}
	
	public static String invalidColor(String colourName) {
		
		return negative + "Colour " + colourName +" isn't a valid colour.";
		
	}
	
	public static String possibleColors(ChatColor[] colours, Faction faction) {
		
		
		StringBuffer rString = new StringBuffer();
		
		if(colours.length == 0){
			rString.append("Possible colours: none");
		}else if(colours.length == 1){
			rString.append("Possible colour: ");
		}else{
			rString.append("Possible colours: ");
		}
		
		for (int i = 0; i < colours.length; i++) {
			
			if( i!= 0) rString.append(", ");
			
			rString.append(colours[i].name().toLowerCase().replace("_", " "));
			
		}
		
		
		rString.insert(0, faction.getColour2());
		
		return rString.toString();
		
		
	}
	

	
	// Stats:
	public static String stats(Faction faction, Integer page) {

		
		StringBook book = new StringBook(faction.getName() + " stats", new ColourLoop().addColor(faction.getColour2()));
		
		// Levels, claims and allies:
		book.addTable(main(faction));
		book.addLine("");
		book.addTable(claims(faction));
		book.addLine("");
		book.addLine(allies(faction));
		
		book.nextPage();
		
		// Members:
		book.addLine(listMembers(faction));
		
		book.nextPage();
		
		// Claimed:
		book.addTable(claimed(faction));
		
		return book.framedPage(page);

		
	}
	
	private static StringTable main(Faction faction){
		
		
		ColourLoop colours = new ColourLoop().addColor(faction.getColour2());
		StringTable table = new StringTable(colours);
		
		// Colours:
		table.addLine("colour I", faction.getColour1() + TextUtil.colour(faction.getColour1()), 0);
		
		// Building points:
		table.addLine("colour II", faction.getColour2() + TextUtil.colour(faction.getColour2()), 0);
		
		// Owner:
		if(faction.hasOwner()){
			table.addLine("owner", faction.getOwner(), 0);
		}else{
			table.addLine("owner", veryNegative + "none", 0);
		}
		
		
		int claimed = FactionClaimManager.manager().findSettlementsIds(faction.getId()).length;
		int availClaims = faction.getAvailableClaims();
		double progress = faction.getClaimProgress();
		
		// Claimed:
		table.addLine("claimed", claimed + "/" + availClaims, 2);

		// Next exp:
		table.addLine("next claim", (int)(progress*100) + "%", 2);

		table.collapse();
		
		return table;
		
		
	}
	
	private static StringTable claims(Faction faction){
		
		
		ColourLoop colours = new ColourLoop().addColor(faction.getColour2());
		StringTable table = new StringTable(colours);

		// Wages:
		table.addLine(GeneralMessages.tableTitle("wages"), "", 0);
		
		Hashtable<Integer, Double> wages = faction.calcWages();
		int min = FactionConfiguration.config().getDefinition().getHierarchyMin();
		int max = FactionConfiguration.config().getDefinition().getHierarchyMax();
		
		if(min != max){
		
			for (int hiera = max; hiera >= min; hiera--) {
				
				String name = faction.getDefinition().getHierarchyName(hiera);
				Double wage = wages.get(hiera);
				if(wage == null) wage = 0.0;
				
				table.addLine(name, EconomyMessages.coins(wage), 0);
				
			}
			
		}else{
			
			table.addLine("-", "-", 0);
			
		}
		
		table.collapse();
		
		return table;
		
		
	}		
	
	private static String allies(Faction faction){
		
		
		StringBuffer result = new StringBuffer();

		ArrayList<String> allies = FactionManager.manager().getFactionNames(faction.getAllies());
		ArrayList<String> allyInvites = FactionManager.manager().getFactionNames(faction.getAllyInvites());
		
		// Allies:
		result.append("allies: ");
		if(allies.size() > 0){
			result.append(TextUtil.flatten(allies));
		}else{
			result.append("none");
		}

		// Ally invites:
		if(allyInvites.size() > 0){
			
			result.append("\n");
			result.append("ally invites: " + TextUtil.flatten(allyInvites));
			
		}
		
		return result.toString();
		
		
	}
	
	private static String listMembers(Faction faction){
		
		
		StringBuffer result = new StringBuffer();
		
		ChatColor general = faction.getColour1();
		ChatColor normal = faction.getColour2();
		
		int hMin = faction.getDefinition().getHierarchyMin();
		int hMax = faction.getDefinition().getHierarchyMax();
		
		// Hierarchy levels:
		for (int hierarchy = hMax; hierarchy >= hMin; hierarchy--) {
			
			if(result.length() > 0){
				result.append("\n");
				result.append("\n");
			}
			
			// Group name:
			String groupName = faction.getDefinition().getHierarchyName(hierarchy);
			if(groupName.length() == 0) groupName = "-";
			result.append(GeneralMessages.tableTitle(general + groupName));
			
			// Rank amounts:
			if(hierarchy != faction.getDefinition().getHierarchyMin()){
				
				String amounts = faction.getUsedRanks(hierarchy) + "/" + faction.getAvailableRanks(hierarchy);
				
				if(faction.isRankAvailable(hierarchy)){
					amounts = positive + amounts;
				}else{
					amounts = negative + amounts;
				}
				
				result.append(" " + amounts);
				
			}else{
				
				String amounts = faction.getUsedRanks(hierarchy) + "/-";
				result.append(" " + amounts);
				
			}
			
			// All ranks:
			StringBuffer resultRanks = new StringBuffer();
			
			ArrayList<ProficiencyDefinition> ranks = ProficiencyConfiguration.config().getDefinitions(ProficiencyType.RANK, hierarchy);
			
			for (ProficiencyDefinition definition : ranks) {
				
				// Members:
				if(resultRanks.length() > 0) resultRanks.append("\n");
				
				String roleName = definition.getName();
				ArrayList<String> members = faction.getMembersForRanks(roleName);
				
				// Colour members:
				colourMembers(members, faction);
				
				// Add members:
				resultRanks.append(normal);
				
				resultRanks.append(roleName + ": ");
				
				if(members.size() != 0){
					resultRanks.append(TextUtil.flatten(members));
				}else{
					resultRanks.append("none");
				}
				
			}
			
			result.append("\n");
			
			// Add roles:
			result.append(resultRanks);
			
		}
		
		return result.toString();
		
		
	}

	private static void colourMembers(ArrayList<String> members, Faction faction){
		
		for (int i = 0; i < members.size(); i++) {
			members.set(i, member(members.get(i), faction));
		}
		
	}
	
	private static String member(String name, Faction faction){
		
		
		// Active:
		if(!faction.isMemberActive(name)){
			return unavailable + "" + ChatColor.STRIKETHROUGH + name + normal1;
		}
		
		// Offline:
		else if(!faction.isMemberOnline(name)){
			return unavailable + name + faction.getColour2();
		}
		
		// Normal:
		else{
			return faction.getColour2() + name;
		}
		
		
	}
	
	private static StringTable claimed(Faction faction){
		
		
		Settlement[] settlements = FactionClaimManager.manager().findSettlements(faction.getId());
		
		ColourLoop colours = new ColourLoop().addColor(faction.getColour2());
		StringTable table = new StringTable(colours);

		
		// Titles:
		table.addLine(new String[]{GeneralMessages.columnTitle("settlement"), GeneralMessages.columnTitle("location"), GeneralMessages.columnTitle("closest"), GeneralMessages.columnTitle("distance")});
		
		if(settlements.length > 0){
			
			for (int i = 0; i < settlements.length; i++) {
				
				String name = settlements[i].getName();
				Location location = retTownSquareLoc(settlements[i]);
				
				String locationStr = "no " + HelpMessages.townSquare();
				if(location != null) locationStr = location(location);
				
				String closestName = "none";
				Location closestLocation = null;

				Settlement closestSettle = closest(settlements[i], settlements);
				if(closestSettle != null){
					closestLocation = retTownSquareLoc(closestSettle);
					closestName = closestSettle.getName();
				}
				
				String distance = "-";
				if(location != null && closestLocation != null) distance = (int)location.distance(closestLocation) + "";
				
				table.addLine(new String[]{name, locationStr, closestName, distance});
			
			}
			
		}else{
			
			table.addLine(new String[]{"-", "-", "-", "-"});
			
		}
		
		table.collapse();
		
		return table;
		
		
	}
	
	private static Settlement closest(Settlement settlement, Settlement[] otherSettles){
		
		
		Double minDistSuared = Double.MAX_VALUE;
		Settlement closestSettlement = null;
				
		// Location:
		Location location = retTownSquareLoc(settlement);
		if(location == null) return null;
				
		for (int i = 0; i < otherSettles.length; i++) {
			
			// Same settlement:
			if(settlement == otherSettles[i]) continue;
			
			// Town square:
			Location otherLocation = retTownSquareLoc(otherSettles[i]);
			if(otherLocation == null) continue;
			
			// Other world:
			if(!otherLocation.getWorld().getName().equalsIgnoreCase(location.getWorld().getName())) continue;
			
			Double distSqared = otherLocation.distanceSquared(location);
			if(distSqared < minDistSuared){
				minDistSuared = distSqared;
				closestSettlement = otherSettles[i];
			}
			
		}
		
		return closestSettlement;
		
		
	}


	private static String location(Location location){
		
		
		StringBuffer result = new StringBuffer();
		
		if(location != null){
			result.append(location.getBlockX() + ", " + location.getBlockY() + ", " + location.getBlockZ()); 

			if(!location.getWorld().getName().equals(GeneralConfiguration.config().getDefaultWorld())) result.insert(0, location.getWorld().getName() + " ");
			
		}
		
		return result.toString();
		
		
	}

	
	private static Location retTownSquareLoc(Settlement settlement) {

		ArrayList<TownSquare> townSquares = settlement.getBuildings(TownSquare.class);
		if(townSquares.size() == 0) return null;
		return townSquares.get(0).getSagaChunk().getCenterLocation();
		
	}
	
	
	// Rename:
	public static String renamed(Faction faction) {

		return faction.getColour2() + "Faction was renamed to " + faction(faction, faction.getColour2()) + ".";
		
	}
	
	
	
	// Spawn:
	public static String noSpawn(Faction faction) {
		
		return negative + "Faction spawn point isn't set.";
		
	}
	
	public static String newSpawn(Faction faction) {
		
		return faction.getColour2() + "New faction spawn point was set.";
		
	}

	
	
	// Ally:
	public static String sentAlliance(Faction faction, Faction target) {
		
		return faction.getColour2() + "An alliance request was sent to " + faction(target, faction.getColour2()) + " faction.";
		
	}
	
	public static String recievedAlliance(Faction faction, Faction source) {
		
		return faction.getColour2() + "Recieved an alliance request from " + faction(source, faction.getColour2()) + " faction.";
		
	}
	
	public static String recievedAllianceInfo(Faction faction, Faction target) {
		
		return normal1 + "Use /fallyaccept to accept and /fallydecline to decline the alliance request.";
		
	}

	public static String declinedAllianceRequest(Faction faction, Faction target) {
		
		return faction.getColour2() + "Alliance request from " + faction(target, faction.getColour2()) + " faction was declined.";
		
	}
	
	public static String formedAlliance(Faction faction, Faction target) {
		
		return faction.getColour2() + "An alliance was formed with " + faction(target, faction.getColour2()) + " faction.";
		
	}
	
	public static String brokeAlliance(Faction faction, Faction target) {
		
		return faction.getColour2() + "The alliance with " + faction(target, faction.getColour2()) + " faction was broken.";
		
	}

	public static String selfAlliance(Faction faction) {
		
		return negative + "Can't request an alliance from your own faction.";
		
	}
	
	public static String alreadyAlliance(Faction faction, Faction targetFaction) {
		
		return negative + "An alliance with " + faction(targetFaction, negative) + " is already formed.";
		
	}

	public static String noAllianceRequest(Faction faction, String souceFaction) {
		
		return negative + "The faction doesn't have an alliance request from " + souceFaction + " faction.";
		
	}
	
	public static String noAlliance(Faction faction, Faction targetFaction) {
		
		return negative + "No alliance formed with " + faction(targetFaction, negative) + " faction.";
		
	}
	
	public static String noAllianceRequest(Faction faction) {
		
		return negative + "The faction doesn't have alliance requests.";
		
	}

	
	
	// List:
	public static String list(Faction faction) {

		
		StringBuffer result = new StringBuffer();
		
		result.append(listMembers(faction));
		
		return StringFramer.frame(faction.getName() + " members", result.toString(), normal1);
		
		
	}
	
	
	
	// Info:
	public static String wrongQuit() {
		
		return negative + "Because /squit and /fquit are similar, this command isn't used. Please use /factionquit instead.";
		
	}
	
	
	
	// Rank:
	public static String newRank(Faction faction, String rankName, SagaPlayer targetPlayer) {
		
		return faction.getColour2() + "Rank " + rankName + " was assigned to " + targetPlayer.getName() + ".";
		
	}
	
	public static String invalidRank(String rankName) {
		
		return negative + "Rank " + rankName + " is invalid.";
		
	}
	
	public static String rankUnavailable(Faction faction, String rankName) {
		
		return negative + "Rank " + rankName + " isn't available.";
		
	}
	
	
	
	// Experience:
	public static String levelUp(Faction faction) {
		
		return faction.getColour2() + "The faction is now level " + faction.getLevel() + ".";
		
	}
	
	
	
	// Claiming:
	public static String notClaimed(Faction faction, Bundle bundle) {
		return negative + "Settlement " + bundle.getName() + " is not claimed by the faction.";
	}
	
	public static String unclaimed(Faction faction, Bundle bundle) {
		return faction.getColour2() + "Settlement " + bundle.getName() + " was unclaimed.";
	}
	
	
	// Utility:
	public static String faction(Faction faction, ChatColor colour) {
		
		return faction.getColour1() + faction.getName() + colour;
		
	}
	
	public static String faction(ChatColor style, Faction faction, ChatColor colour) {
		
		return faction.getColour1().toString() + style + faction.getName() + colour + style;
		
	}

	public static String rankedPlayer(Faction faction, SagaPlayer sagaPlayer) {

		Proficiency rank = faction.getRank(sagaPlayer.getName());
		
		if(rank == null){
			return sagaPlayer.getName();
		}else{
			return rank.getName() + " " + sagaPlayer.getName();
		}
		
	}
	
	
}
