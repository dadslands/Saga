package org.saga.messages;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.bukkit.ChatColor;
import org.saga.abilities.Ability;
import org.saga.abilities.AbilityDefinition;
import org.saga.chunks.Bundle;
import org.saga.chunks.BundleManager;
import org.saga.config.AbilityConfiguration;
import org.saga.config.AttributeConfiguration;
import org.saga.config.ExperienceConfiguration;
import org.saga.dependencies.EconomyDependency;
import org.saga.factions.Faction;
import org.saga.factions.FactionManager;
import org.saga.messages.PlayerMessages.ColourLoop;
import org.saga.player.GuardianRune;
import org.saga.player.SagaPlayer;
import org.saga.utility.text.RomanNumeral;
import org.saga.utility.text.StringBook;
import org.saga.utility.text.StringTable;
import org.saga.utility.text.TextUtil;

public class StatsMessages {

	
	public static ChatColor veryPositive = ChatColor.DARK_GREEN;
	
	public static ChatColor positive = ChatColor.GREEN;
	
	public static ChatColor negative = ChatColor.RED;
	
	public static ChatColor veryNegative = ChatColor.DARK_RED;
	
	public static ChatColor unavailable = ChatColor.DARK_GRAY;
	
	public static ChatColor announce = ChatColor.AQUA;
	
	public static ChatColor normal1 = ChatColor.GOLD;
	
	public static ChatColor normal2 = ChatColor.YELLOW;
	
	
	
	
	// Stats command:
	public static String stats(SagaPlayer sagaPlayer, Integer page) {

		
		StringBook book = new StringBook("stats", new ColourLoop().addColor(normal1).addColor(normal2));
		
		// Attributes and levels:
		book.addTable(attributesLevels(sagaPlayer));
		book.addLine("");
		book.addTable(factionSettlement(sagaPlayer));

		book.nextPage();
		
		// Abilities:
		book.addTable(abilities(sagaPlayer));

		book.nextPage();
		
		// Invites:
		book.addTable(invites(sagaPlayer));
		
		return book.framedPage(page);

		
	}
	
	
	private static StringTable attributesLevels(SagaPlayer sagaPlayer) {

		
		StringTable table = new StringTable(new ColourLoop().addColor(normal1).addColor(normal2));
		DecimalFormat format = new DecimalFormat("00");
		
		// Attributes:
		ArrayList<String> attrNames = AttributeConfiguration.config().getAttributeNames();
		for (String attrName : attrNames) {
			
			Integer attrBonus = sagaPlayer.getAttrScoreBonus(attrName);
			Integer attrScore = sagaPlayer.getRawAttributeScore(attrName);
			
			String scoreCurr = format.format(attrScore + attrBonus);
			String scoreMax = format.format(AttributeConfiguration.config().maxAttributeScore + attrBonus);
			
			String score = scoreCurr + "/" + scoreMax;
			
			// Colours:
			if(attrBonus > 0){
				score = positive + score;
			}
			else if(attrBonus < 0){
				score = negative + score;
			}
			
			table.addLine(attrName, score, 0);
			
		}
		
		// Health:
		table.addLine("Health", TextUtil.round((double)sagaPlayer.getHealth(), 0) + "/" + TextUtil.round((double)sagaPlayer.getTotalHealth(), 0), 2);
		
		String attrPoints = sagaPlayer.getUsedAttributePoints() + "/" + sagaPlayer.getAvailableAttributePoints();
		if(sagaPlayer.getRemainingAttributePoints() < 0){
			attrPoints = ChatColor.DARK_RED + attrPoints;
		}
		else if (sagaPlayer.getRemainingAttributePoints() > 0) {
			attrPoints = ChatColor.DARK_GREEN + attrPoints;
		}
		table.addLine("Attributes", attrPoints, 2);

		// Exp:
		table.addLine("Progress", (int)(100.0 - 100.0 * sagaPlayer.getRemainingExp() / ExperienceConfiguration.config().getAttributePointCost()) + "%", 2);

		// Wallet:
		table.addLine("Wallet", EconomyMessages.coins(EconomyDependency.getCoins(sagaPlayer)), 2);
		
		// Guard rune:
		GuardianRune guardRune = sagaPlayer.getGuardRune();
		String rune = "";
		if(!guardRune.isEnabled()){
			rune = "disabled";
		}else{
			if(guardRune.isCharged()){
				rune= "charged";
			}else{
				rune= "discharged";
			}
		}
		table.addLine("Guard rune", rune, 2);
		
		table.collapse();
		
		return table;
		

	}
	
	private static StringTable factionSettlement(SagaPlayer sagaPlayer) {

		
		StringTable table = new StringTable(new ColourLoop().addColor(normal1).addColor(normal2));

		
		// Faction and settlement:
		String faction = "none";
		if(sagaPlayer.getFaction() != null) faction = sagaPlayer.getFaction().getName();

		String settlement = "none";
		if(sagaPlayer.getBundle() != null) settlement = sagaPlayer.getBundle().getName();
		
		table.addLine("faction", faction, 0);
		table.addLine("settlement", settlement, 2);
		
		// Rank and role:
		String rank = "none";
		if(sagaPlayer.getRank() != null) rank = sagaPlayer.getRank().getName();

		String role = "none";
		if(sagaPlayer.getRole() != null) role = sagaPlayer.getRole().getName();

		table.addLine("rank", rank, 0);
		table.addLine("role", role, 2);
		
		// Style:
		table.collapse();
		
		return table;
		

	}
	
	private static StringTable abilities(SagaPlayer sagaPlayer) {

		
		StringTable table = new StringTable(new ColourLoop().addColor(normal1).addColor(normal2));
		HashSet<Ability> allAbilities = sagaPlayer.getAbilities();
		
    	// Add abilities:
    	if(allAbilities.size() > 0){
    		
    		for (Ability ability : allAbilities) {
    			
    			String name = ability.getName() + " " + RomanNumeral.binaryToRoman(ability.getScore());
    			String required = "";
    			String status = "";
    			
    			if(ability.getScore() == 0){
    				name = unavailable + name;
    				required = unavailable + required;
    				status = unavailable + status;
    			}
    			
    			if(ability.getScore() < AbilityConfiguration.config().maxAbilityScore){
    				
    				String requirements = requirements2(ability.getDefinition(), ability.getScore() + 1);
    				String restrictions = restrictions(ability.getDefinition());
    				
    				
    				if(restrictions.length() > 0){
    					if(requirements.length() > 0) requirements+= ", ";
    					requirements+= restrictions;
    				}
    				
    				required+= requirements;
    				
    			}else{
    				required = "-";
    			}
    			
    			if(ability.getCooldown() <= 0){
					status+= "ready";
				}else{
					status = ability.getCooldown() + "s";
				}
    			
    			table.addLine(new String[]{name, required, status});
    			
    		}
    		
    	}
    	
    	// No abilities:
    	else{
    		table.addLine("-");
    	}
    	
    	table.collapse();
    	
		return table;
    	
		
	}
	
	public static String requirements2(AbilityDefinition definition, Integer score) {

		
		StringBuffer result = new StringBuffer();
		
		// Attributes:
		ArrayList<String> attributeNames = AttributeConfiguration.config().getAttributeNames();
		
		for (String attribute : attributeNames) {
			
			Integer reqScore = definition.getAttrReq(attribute, score);
			if(reqScore <= 0) continue;
			
			if(result.length() > 0) result.append(", ");
			
			result.append(GeneralMessages.attrAbrev(attribute) + " " + reqScore);
			
		}

		// Buildings:
		List<String> buildings = definition.getBldgReq(score);
		if(buildings.size() > 0){
			if(result.length() > 0) result.append(", ");
			result.append(TextUtil.flatten(buildings));
		}
		
		return result.toString();
		
		
	}
	
	public static String restrictions(AbilityDefinition definition) {


		StringBuffer result = new StringBuffer();
		
		// Proficiencies:
		HashSet<String> proficiencies = definition.getProfRestr();
		if(proficiencies.size() > 0) result.append(TextUtil.flatten(proficiencies));
		
		return result.toString();
		
		
	}
	

	public static StringTable invites(SagaPlayer sagaPlayer) {

		
		StringTable table = new StringTable(new ColourLoop().addColor(normal1).addColor(normal2));
		
		// Table size:
    	ArrayList<Double> widths = new ArrayList<Double>();
    	widths.add(28.5);
    	widths.add(28.5);
    	table.setCustomWidths(widths);
		
    	// Factions:
    	table.addLine(GeneralMessages.columnTitle("faction invites"), 0);
    	
    	ArrayList<Faction> factions = getFactions(sagaPlayer.getFactionInvites());
    	
    	for (Faction faction : factions) {
			table.addLine(faction.getName(), 0);
		}
    	
    	if(factions.size() == 0){
    		table.addLine("-", 0);
    	}
    	
    	// Chunk groups:
    	table.addLine(GeneralMessages.columnTitle("settlement invites"), 1);
    	
    	ArrayList<Bundle> bundles = getSettlements(sagaPlayer.getBundleInvites());
    	
    	for (Bundle bundle : bundles) {
			table.addLine(bundle.getName(), 1);
		}
    	
    	if(bundles.size() == 0){
    		table.addLine("-", 1);
    	}
    	
		return table;
    	
		
	}
	
	private static ArrayList<Faction> getFactions(ArrayList<Integer> ids) {


		// Faction invites:
		ArrayList<Faction> factions = new ArrayList<Faction>();
		if(ids.size() > 0){
			
			for (int i = 0; i < ids.size(); i++) {
			
				Faction faction = FactionManager.manager().getFaction(ids.get(i));
				if( faction != null ){
					factions.add(faction);
				}else{
					ids.remove(i);
					i--;
				}
				
			}
		}
		
		return factions;
		
		
	}
	
	private static ArrayList<Bundle> getSettlements(ArrayList<Integer> ids) {


		// Faction invites:
		ArrayList<Bundle> bundles = new ArrayList<Bundle>();
		if(ids.size() > 0){
			
			for (int i = 0; i < ids.size(); i++) {
			
				Bundle faction = BundleManager.manager().getBundle(ids.get(i));
				if( faction != null ){
					bundles.add(faction);
				}else{
					ids.remove(i);
					i--;
				}
				
			}
		}
		
		return bundles;
		
		
	}
	

	
	// Attribute points:
	public static String gaineAttributePoints(Integer amount) {
		
		return veryPositive + "Gained " + amount + " attribute points.";
		
	}
	
	
	
	
}
