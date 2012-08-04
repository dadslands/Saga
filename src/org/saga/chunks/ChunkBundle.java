package org.saga.chunks;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.entity.Enderman;
import org.bukkit.event.Event.Result;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.block.EntityBlockFormEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.saga.Saga;
import org.saga.SagaLogger;
import org.saga.buildings.Building;
import org.saga.config.BalanceConfiguration;
import org.saga.config.SettlementConfiguration;
import org.saga.exceptions.InvalidBuildingException;
import org.saga.exceptions.NonExistantSagaPlayerException;
import org.saga.factions.Faction;
import org.saga.listeners.events.SagaBuildEvent;
import org.saga.listeners.events.SagaBuildEvent.BuildOverride;
import org.saga.listeners.events.SagaEntityDamageEvent;
import org.saga.listeners.events.SagaEntityDamageEvent.PvPOverride;
import org.saga.messages.GeneralMessages;
import org.saga.messages.SettlementMessages;
import org.saga.player.SagaPlayer;
import org.saga.saveload.Directory;
import org.saga.saveload.SagaCustomSerialization;
import org.saga.saveload.WriterReader;
import org.saga.settlements.Settlement.SettlementPermission;

import com.google.gson.JsonParseException;

public class ChunkBundle extends SagaCustomSerialization{

	
	/**
	 * Group name ID. -1 if none.
	 */
	private Integer id;
	
	/**
	 * Group name.
	 */
	private String name;
	
	/**
	 * Players associated with the group.
	 */
	private ArrayList<String> players;

	/**
	 * Factions associated with the group.
	 */
	private ArrayList<Integer> factions;
	
	/**
	 * Group chunks.
	 */
	private ArrayList<SagaChunk> groupChunks;

	/**
	 * Building scores.
	 */
	private Hashtable<String, Integer> buildingScores;
	
	/**
	 * Registered players.
	 */
	transient private ArrayList<SagaPlayer> registeredPlayers = new ArrayList<SagaPlayer>();
	
	/**
	 * Registered factions.
	 */
	transient private ArrayList<Faction> registeredFactions = new ArrayList<Faction>();
	
	/**
	 * Chunk group owners.
	 */
	private String owner;

	
	// Control:
	/**
	 * If true then saving is enabled.
	 */
	transient private Boolean isSavingEnabled;

	
	
	// Options:
	/**
	 * Toggle options.
	 */
	private HashSet<ChunkBundleToggleable> toggleOptions;
	
	/**
	 * Forced pvp protection.
	 */
	private Boolean pvpProtectionBonus;
	
	/**
	 * Unlimited claims.
	 */
	private Boolean unlimitedClaimBonus;
	
	

	// Properties:
	/**
	 * True if fire spread is enabled.
	 */
	private Boolean fireSpread;
	
	/**
	 * True if lava spread is enabled.
	 */
	private Boolean lavaSpread;
	
	
	
	// Initialisation:
	/**
	 * Sets name and ID.
	 * 
	 * @param id ID
	 * @param name name
	 */
	public ChunkBundle(String name){
		
		
		this.name = name;
		this.id = ChunkBundleManager.manager().getUnusedId();
		this.players = new ArrayList<String>();
		this.factions = new ArrayList<Integer>();
		this.groupChunks = new ArrayList<SagaChunk>();
		this.buildingScores = new Hashtable<String, Integer>();
		this.isSavingEnabled = true;
		this.owner = "";
		this.pvpProtectionBonus = false;
		this.unlimitedClaimBonus = false;
		this.fireSpread = false;
		this.lavaSpread = false;
		this.toggleOptions = new HashSet<ChunkBundleToggleable>();
		
		
	}
	
	/**
	 * Fixes problematic fields.
	 * 
	 */
	public void complete() {

		
		if(name == null){
			SagaLogger.nullField(this, "name");
			name = "unnamed";
		}
		if(id == null){
			SagaLogger.nullField(this, "id");
			id = -1;
		}
		if(players == null){
			SagaLogger.nullField(this, "players");
			players = new ArrayList<String>();
		}
		for (int i = 0; i < players.size(); i++) {
			if(players.get(i) == null){
				SagaLogger.nullField(this, "players element");
				players.remove(i);
				i--;
			}
		}
		if(factions == null){
			SagaLogger.nullField(this, "factions");
			factions = new ArrayList<Integer>();
		}
		for (int i = 0; i < factions.size(); i++) {
			if(factions.get(i) == null){
				SagaLogger.nullField(this, "factions element");
				factions.remove(i);
				i--;
			}
		}

		if(buildingScores == null){
			SagaLogger.nullField(this, "buildingScores");
			buildingScores = new Hashtable<String, Integer>();
		}
		ArrayList<Building> builings = getBuildings();
		for (Building building : builings) {
			if(buildingScores.containsValue(building.getName())) continue;
			buildingScores.put(building.getName(), 1);
		}
		
		if(owner == null){
			SagaLogger.nullField(this, "owners");
			owner = "";
		}
		
		// Transient:
		registeredPlayers = new ArrayList<SagaPlayer>();
		registeredFactions = new ArrayList<Faction>();
		isSavingEnabled = true;
	
		if(groupChunks == null){
			SagaLogger.nullField(this, "groupChunks");
			groupChunks = new ArrayList<SagaChunk>();
		}
		for (int i = 0; i < groupChunks.size(); i++) {
			SagaChunk coords= groupChunks.get(i);
			if(coords == null){
			
				SagaLogger.nullField(this, "groupChunks element");
				groupChunks.remove(i);
				i--;
				continue;
				
			}
			coords.complete(this);
			// Buildings:
			if(coords.getBuilding() != null){
				
				try {
					coords.getBuilding().complete();
				} catch (InvalidBuildingException e) {
					SagaLogger.severe(this,"failed to complete " + coords.getBuilding().getName() + " building: "+ e.getClass().getSimpleName() + ":" + e.getMessage());
					disableSaving();
					coords.clearBuilding();
				}
			}
			
		}
		
		// Properties:
		if(fireSpread == null){
			SagaLogger.nullField(this, "fireSpread");
			fireSpread = false;
		}
		if(lavaSpread == null){
			SagaLogger.nullField(this, "lavaSpread");
			lavaSpread = false;
		}
		
		if(toggleOptions == null){
//			SagaLogger.nullField(this, "toggleOptions");
			toggleOptions = new HashSet<ChunkBundleToggleable>();
			
			// TODO: Remove options migration
			if(pvpProtectionBonus != null && pvpProtectionBonus){
				toggleOptions.add(ChunkBundleToggleable.PVP_PROTECTION);
			}
			if(unlimitedClaimBonus != null && unlimitedClaimBonus){
				toggleOptions.add(ChunkBundleToggleable.UNLIMITED_CLAIMS);
			}
			
		}
		if(toggleOptions.remove(null)){
			SagaLogger.nullField(this, "toggleOptions element");
		}
		
		
	}

	/**
	 * Enables the building
	 * 
	 */
	public void enable() {
		
		
		// Enable all buildings:
		ArrayList<Building> buildings = getBuildings();
		for (Building building : buildings) {
			
			building.enable();
			
		}
		
	}
	
	/**
	 * Disables the building.
	 * 
	 */
	public void disable() {
		

		// Enable all buildings:
		ArrayList<Building> buildings = getBuildings();
		for (Building building : buildings) {
			
			building.disable();
			
		}
		
	}
	
	
	
	// Chunk group management:
	/**
	 * Adds a new chunk group.
	 * 
	 * @param chunkBundle chunk group.
	 */
	public final static void create(ChunkBundle chunkBundle){

		
		// Log:
		SagaLogger.info("Creating " + chunkBundle + " chunk group.");

		// Update chunk group manager:
		ChunkBundleManager.manager().addChunkBundle(chunkBundle);
		
		// Do the first save:
		chunkBundle.save();
		
		// Refresh:
		ArrayList<SagaChunk> sagaChunks = chunkBundle.getGroupChunks();
		for (SagaChunk sagaChunk : sagaChunks) {
			sagaChunk.refresh();
		}
		
		// Enable:
		chunkBundle.enable();
		
		
	}
	
	/**
	 * Adds a new chunk group.
	 * 
	 * @param chunkBundle chunk group
	 * @param owner owner
	 */
	public static void create(ChunkBundle chunkBundle, SagaPlayer owner){
		

		// Add player:
		chunkBundle.addMember(owner);

		// Set owner:
		chunkBundle.setOwner(owner.getName());
		
		// Forward:
		create(chunkBundle);

		
	}

	
	/**
	 * Deletes a chunk group
	 * 
	 * @param groupName group name
	 */
	public void delete() {


		// Log:
		SagaLogger.info("Deleting " + this + " chunk group.");
		
		// Disable:
		disable();
		
		// Remove all player:
		ArrayList<String> players = getMembers();
		for (String player : players) {
			
			try {
				SagaPlayer sagaPlayer = Saga.plugin().forceSagaPlayer(player);
				removeMember(sagaPlayer);
				Saga.plugin().unforceSagaPlayer(player);
			} catch (NonExistantSagaPlayerException e) {
				SagaLogger.severe(this, "failed to remove " + player + " player");
				removePlayer(player);
			}
			
		}
		
		// Remove all saga chunks:
		ArrayList<SagaChunk> groupChunks = getGroupChunks();
		for (SagaChunk sagaChunk : groupChunks) {
			removeChunk(sagaChunk);
		}
		
		// Save one last time:
		save();
		
		// Remove from disc:
		WriterReader.delete(Directory.SETTLEMENT_DATA, getId().toString());
		
		// Update chunk group manager:
		ChunkBundleManager.manager().removeChunkBundle(this);
		
		
	}
	
	/**
	 * Adds a chunk.
	 * Needs to be done by chunk group manager, to update chunk shortcuts.
	 * 
	 * @param sagaChunk saga chunk
	 */
	public void addChunk(SagaChunk sagaChunk) {

		
		// Check if already on the list:
		if(groupChunks.contains(sagaChunk)){
			SagaLogger.severe(this, "tried to add an already existing " + sagaChunk + "chunk");
			return;
		}
		
		// Set chunk chunk group:
		sagaChunk.complete(this);
		
		// Add:
		groupChunks.add(sagaChunk);
		
		// Update chunk group manager:
		ChunkBundleManager.manager().addChunk(sagaChunk);
		
		// Refresh:
		sagaChunk.refresh();
		
		
	}
	
	/**
	 * Removes a chunk.
	 * Needs to be done by chunk group manager, to update chunk shortcuts.
	 * 
	 * @param sagaChunk saga chunk
	 */
	public void removeChunk(SagaChunk sagaChunk) {

		
		// Check if not in this group:
		if(!groupChunks.contains(sagaChunk)){
			SagaLogger.severe(this, "tried to remove a non-existing " + sagaChunk + "chunk");
			return;
		}
		
		// Remove member:
		groupChunks.remove(sagaChunk);

		// Update chunk group manager:
		ChunkBundleManager.manager().removeChunk(sagaChunk);

		// Refresh:
		sagaChunk.refresh();
		
		
	}

	/**
	 * Checks if the ID is on the list.
	 * 
	 * @param id ID
	 * @return true if on the list
	 */
	public boolean hasFaction(Integer id){
		return factions.contains(id);
	}

	/**
	 * Checks if the given bukkit chunk is adjacent to the chunk group.
	 * 
	 * @param bukkitChunk bukkit chunk
	 * @return true if adjacent
	 */
	public boolean isAdjacent(Chunk bukkitChunk) {

		String bWorld = bukkitChunk.getWorld().getName();
		int bX = bukkitChunk.getX();
		int bZ = bukkitChunk.getZ();
		
		for (int i = 0; i < groupChunks.size(); i++) {
			SagaChunk sChunk = groupChunks.get(i);
			// World:
			if(!sChunk.getWorldName().equals(bWorld)){
				continue;
			}
			// North from saga chunk:
			if( (sChunk.getX() == bX + 1) && (sChunk.getZ() == bZ) ){
				return true;
			}
			// East from saga chunk:
			if( (sChunk.getX() == bX) && (sChunk.getZ() == bZ + 1) ){
				return true;
			}
			// South from saga chunk:
			if( (sChunk.getX() == bX - 1) && (sChunk.getZ() == bZ) ){
				return true;
			}
			// West from saga chunk:
			if( (sChunk.getX() == bX) && (sChunk.getZ() == bZ - 1) ){
				return true;
			}
		}
		return false;
		
		
	}
	
	
	
	// Naming:
	/**
	 * Sets the name
	 * 
	 * @param name name
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	
	
	// Territory:
	/**
	 * Gets all chunks from the group.
	 * 
	 * @return group chunks
	 */
	public ArrayList<SagaChunk> getGroupChunks() {
		return new ArrayList<SagaChunk>(groupChunks);
	}
	
	/**
	 * Returns settlement size in chunks.
	 * 
	 * @return settlement size
	 */
	public int getSize() {
		return groupChunks.size();
	}

	
	
	// Buildings:
	/**
	 * Gets all settlement buildings.
	 * 
	 * @return all settlement buildings
	 */
	public ArrayList<Building> getBuildings() {

		ArrayList<Building> buildings = new ArrayList<Building>();
		for (int i = 0; i < groupChunks.size(); i++) {
			Building building = groupChunks.get(i).getBuilding();
			if(building != null) buildings.add(building);
		}
		return buildings;
		
	}
	
	/**
	 * Gets  buildings count.
	 * 
	 * @return building count
	 */
	public Integer getBuildingCount() {

		return getBuildings().size();
		
	}
	
	
	/**
	 * Gets the total amount of buildings with the given name. 
	 * 
	 * @param buildingName building name
	 * @return total amount
	 */
	public Integer getTotalBuildings(String buildingName) {


		// Total buildings:
		Integer total = 0;
		ArrayList<SagaChunk> groupChunks = getGroupChunks();
		for (SagaChunk sagaChunk : groupChunks) {
			
			Building building = sagaChunk.getBuilding();
			if(building == null) continue;
			if(!building.getName().equals(buildingName)) continue;
			
			total ++;
			
		}
		
		return total;
		
		
	}

	/**
	 * Gets the amount of available buildings. 
	 * 
	 * @param buildingName building name
	 * @return amount available
	 */
	public Integer getAvailableBuildings(String buildingName) {
		
		return 0;
		
	}
	
	/**
	 * Gets the amount of remaining buildings. 
	 * 
	 * @param buildingName building name
	 * @return amount remaining
	 */
	public Integer getRemainingBuildings(String buildingName) {

		return getAvailableBuildings(buildingName) - getTotalBuildings(buildingName);
		
	}
	
	/**
	 * Checks if the a building is available.
	 * 
	 * @param buildingName building name
	 * @return true if available
	 */
	public boolean isBuildingAvailable(String buildingName) {
		
		return getRemainingBuildings(buildingName) > 0;
		
	}
	

	/**
	 * Gets the first building with the given name.
	 * 
	 * @param buildingName building name
	 * @return buildings with the given name
	 */
	public ArrayList<Building> getBuildings(String buildingName) {

		
		ArrayList<Building> buildings = new ArrayList<Building>();
		
		for (SagaChunk sagaChunk : groupChunks) {
			
			Building building = sagaChunk.getBuilding();
			if(building == null) continue;
			
			if(building.getName().equals(buildingName)) buildings.add(building);
			
		}
		
		return buildings;
		
	}
	
	/**
	 * Gets all buildings instance of the given class.
	 * 
	 * @param buildingClass class
	 * @return buildings that are instances of the given class
	 */
	public <T extends Building> ArrayList<T> getBuildings(Class<T> buildingClass){
		
		
		ArrayList<Building> buildings = getBuildings();
		ArrayList<T> filteredBuildings = new ArrayList<T>();
		for (Building building : buildings) {
			
			if(buildingClass.isInstance(building)){
				try {
					filteredBuildings.add(buildingClass.cast(building));
				} catch (Exception e) {
				}
			}
			
		}
		
		return filteredBuildings;
		
		
	}
	
	/**
	 * Gets the first building with the given name.
	 * 
	 * @param buildingName building name
	 * @return first building with the given name
	 */
	public Building getFirstBuilding(String buildingName) {

		for (SagaChunk sagaChunk : groupChunks) {
			
			Building building = sagaChunk.getBuilding();
			if(building == null) continue;
			
			if(building.getName().equals(buildingName)) return building;
			
		}
		
		return null;
		
	}
	
	
	/**
	 * Gets the building level.
	 * 
	 * @param buildingName building name
	 * @return building level
	 */
	public Integer getBuildingLevel(String buildingName) {

		for (SagaChunk sagaChunk : groupChunks) {
			
			Building building = sagaChunk.getBuilding();
			if(building == null) continue;
			
			if(building.getName().equals(buildingName)) return building.getScore();
			
		}
		
		return 0;
		
	}
	
	
	
	// Building scores:
	/**
	 * Gets the building score.
	 * 
	 * @param bldgName building name
	 * @return building score
	 */
	public Integer getBuildingScore(String bldgName) {
		
		Integer score = buildingScores.get(bldgName);
		if(score == null) return 0;
		
		return score;
		
	}
	
	/**
	 * Sets building score.
	 * 
	 * @param bldgName building name
	 * @param score building score
	 */
	public void setBuildingScore(String bldgName, Integer score) {
		
		buildingScores.put(bldgName, score);
		
		// Update players:
		ArrayList<SagaPlayer> members = getRegisteredMembers();
		
		for (SagaPlayer sagaPlayer : members) {
			sagaPlayer.update();
		}
		
	}
	
	/**
	 * Removes building score.
	 * 
	 * @param bldgName building name
	 */
	public void removeBuildingScore(String bldgName) {
		
		buildingScores.remove(bldgName);

		// Update players:
		ArrayList<SagaPlayer> members = getRegisteredMembers();

		for (SagaPlayer sagaPlayer : members) {
			sagaPlayer.update();
		}
		
	}
	
	
	

	// Building points:
	/**
	 * Gets the amount of building points used.
	 * 
	 * @return amount of building points used
	 */
	public Integer getUsedBuildPoints() {
		
		
		Integer total = 0;
		ArrayList<Building> buildings = getBuildings();
		
		for (Building building : buildings) {
			total+= building.getDefinition().getBuildPoints();
		}
 		
		return total;
		
		
	}

	/**
	 * Gets the amount of building points available.
	 * 
	 * @return amount building points available
	 */
	public Integer getAvailableBuildPoints() {
		
		return 0;
		
	}
	
	/**
	 * Gets the amount of building points remaining.
	 * 
	 * @return amount of building points remaining
	 */
	public Integer getRemainingBuildPoints() {
		
		return getAvailableBuildPoints() - getUsedBuildPoints();
		
	}


	
	// Todo methods:
	/**
	 * Registers a faction.
	 * 
	 * @param faction saga faction
	 */
	void registerFaction(Faction faction) {

		
		// Check list:
		if(registeredFactions.contains(faction)){
			SagaLogger.severe(this, "tried to register an already registered faction");
			return;
		}
		
		// Register faction:
		registeredFactions.add(faction);

		
	}
	
	/**
	 * Unregisters a faction.
	 * Will not add player permanently to the faction list.
	 * Used by SagaPlayer to create a connection with the faction.
	 * Should not be used.
	 * 
	 * @param faction saga faction
	 */
	void unregisterFaction(Faction faction) {

		
		// Check list:
		if(!registeredFactions.contains(faction)){
			SagaLogger.severe(this, "tried to unregister a non-registered faction");
			return;
		}

		// Unregister faction:
		registeredFactions.remove(faction);
		
		
	}
	

	
	// Player and faction management:
	/**
	 * Adds a player.
	 * 
	 * @param playerName player name
	 */
	private void addPlayer(String playerName) {

		
		// Check if already on the list:
		if(players.contains(playerName)){
			SagaLogger.severe(this, "tried to add an already registered " + playerName + " player");
			return;
		}
		
		// Add player:
		players.add(playerName);

		
	}

	/**
	 * Removes a player.
	 * 
	 * @param playerName player name
	 */
	private void removePlayer(String playerName) {
		
		
		// Check if not in this settlement:
		if(!players.contains(playerName)){
			SagaLogger.severe(this, "tried to remove a non-member " + playerName + " player");
			return;
		}

		// Remove player:
		players.remove(playerName);

		// Remove ownership:
		if(isOwner(playerName)){
			removeOwner();
		}
		
		
	}

	/**
	 * Adds and registers a player.
	 * 
	 * @param sagaPlayer saga player
	 */
	public void addMember(SagaPlayer sagaPlayer) {


		// Add player:
		addPlayer(sagaPlayer.getName());
		
		// Set chunk group ID:
		sagaPlayer.setChunkBundleId(getId());
		
		// Register:
		registerPlayer(sagaPlayer);

		
	}

	/**
	 * Removes and unregisters a player.
	 * 
	 * @param playerName saga player
	 */
	public void removeMember(SagaPlayer sagaPlayer) {
		
		
		// Remove player:
		removePlayer(sagaPlayer.getName());

		// Remove chunk group ID:
		sagaPlayer.removeChunkBundleId(getId());
		
		// Unregister:
		unregisterPlayer(sagaPlayer);

		
	}
	
	/**
	 * Registers a player.
	 * 
	 * @param sagaPlayer saga player
	 */
	public void registerPlayer(SagaPlayer sagaPlayer) {

		
		// Check list:
		if(registeredPlayers.contains(sagaPlayer)){
			SagaLogger.severe(this, "tried to register an already registered " + sagaPlayer + " player");
			return;
		}
		
		// Register player:
		registeredPlayers.add(sagaPlayer);

		// Register chunk group:
		sagaPlayer.registerChunkBundle(this);
		
		// Saving disabled:
		if(!isSavingEnabled){
			sagaPlayer.error("saving disabled for " + getName() + " settlement");
		}
		
		
	}
	
	/**
	 * Unregisters a player.
	 * 
	 * @param sagaPlayer saga player
	 */
	public void unregisterPlayer(SagaPlayer sagaPlayer) {

		
		// Check list:
		if(!registeredPlayers.contains(sagaPlayer)){
			SagaLogger.severe(this, "tried to unregister a non-registered " + sagaPlayer + " player");
			return;
		}

		// Register player:
		registeredPlayers.remove(sagaPlayer);
		
		// Unregister chunk group:
		sagaPlayer.unregisterChunkBundle(this);
		
		
	}

	
	
	// Members:
	/**
	 * Gets members associated.
	 * 
	 * @return member names
	 */
	public ArrayList<String> getMembers() {
		return new ArrayList<String>(players);
	}
	
	/**
	 * Gets the member count
	 * 
	 * @return member count
	 */
	public int getMemberCount() {
		return players.size();
	}
	
	/**
	 * Gets the registered members.
	 * 
	 * @return registered members
	 */
	public ArrayList<SagaPlayer> getRegisteredMembers() {
		return new ArrayList<SagaPlayer>(registeredPlayers);
	}
	
	/**
	 * Gets the registered members count.
	 * 
	 * @return registered members count
	 */
	public int getRegisteredMemberCount() {
		return registeredPlayers.size();
	}
	
	/**
	 * Checks if the member is on the chunk groups list.
	 * 
	 * @param playerName player name
	 * @return true if member is on the list
	 */
	public boolean hasMember(String playerName) {
		

		boolean registered = players.contains(playerName);
		if(registered){
			return true;
		}
		for (int i = 0; i < registeredFactions.size(); i++) {
			if(registeredFactions.get(i).isMember(playerName)) return true;
		}
		return false;
		

	}
	
	/**
	 * Checks if the member is registered.
	 * 
	 * @param sagaPlayer saga player
	 * @return true if member is registered
	 */
	public boolean hasRegisteredMember(SagaPlayer sagaPlayer) {
		
		return registeredPlayers.contains(sagaPlayer);

	}
	
	/**
	 * Checks if the member is registered.
	 * 
	 * @param playerName player name
	 * @return true if member is registered
	 */
	public boolean hasRegisteredMember(String playerName) {
		
		for (int i = 0; i < registeredPlayers.size(); i++) {
			if(registeredPlayers.get(i).getName().equals(playerName)) return true;
		}
		
		return false;

	}
	
	/**
	 * Check if the saga player is a member.
	 * 
	 * @param sagaPlayer saga player
	 * @return true if member
	 */
	public boolean isMember(SagaPlayer sagaPlayer) {

		
		boolean iMember = hasMember(sagaPlayer.getName());
		if(iMember) return true;
		
		ArrayList<Faction> factions = getRegisteredFactions();
		for (Faction faction : factions) {
			if(faction.isMember(sagaPlayer.getName())) return true;
		}
		return false;
		
		
	}
	
	/**
	 * Matches a name to a members name.
	 * 
	 * @param name name
	 * @return matched name, same as given if not found
	 */
	public String matchName(String name) {
		
		ArrayList<String> members = getMembers();
		for (String memberName : members) {
			
			if(memberName.equalsIgnoreCase(name)) return memberName;
			
		}
		return name;

	}
	
	
	
	// Owners:
	/**
	 * Checks if the player counts as the owner of the settlement.
	 * 
	 * @param playerName player name
	 * @return true if owner
	 */
	public boolean isOwner(String playerName) {

		return owner.equalsIgnoreCase(playerName);
		
	}
	
	/**
	 * Sets an owner.
	 * 
	 * @param playerName player name
	 */
	public void setOwner(String playerName) {
		owner = playerName;
	}

	/**
	 * Removes an owner.
	 * 
	 * @param playerName player name
	 */
	public void removeOwner() {
		owner = "";
	}
	
	/**
	 * Gets the owner.
	 * 
	 * @return the owner
	 */
	public String getOwner() {
		return owner;
	}

	/**
	 * Returns the owner count.
	 * 
	 * @return owner count
	 */
	public boolean hasOwner() {
		return !owner.equals("");
	}
	
	
	
	// Factions:
	/**
	 * Gets factions associated.
	 * 
	 * @return faction IDs
	 */
	public ArrayList<Integer> getFactions() {
		return factions;
	}

	/**
	 * Gets the registered factions.
	 * 
	 * @return the registered factions
	 */
	public ArrayList<Faction> getRegisteredFactions() {
		return new ArrayList<Faction>(registeredFactions);
	}

	/**
	 * Checks if the faction is registered.
	 * 
	 * @param faction saga faction
	 * @return true if registered
	 */
	public boolean isFactionRegistered(Faction faction) {
		return registeredFactions.contains(faction);
	}

	/**
	 * Gets the factions count.
	 * 
	 * @return factions count
	 */
	public int getFactionCount() {
		return factions.size();
	}

	
	
	// Permissions:
	/**
	 * Checks if the player has permission.
	 * 
	 * @param sagaPlayer saga player
	 * @param permission permission
	 * @return true if has permission
	 */
	public boolean hasPermission(SagaPlayer sagaPlayer, SettlementPermission permission) {

		return false;
	}

	
	
	// Bonuses:
	/**
	 * Checks if the option is enabled.
	 * 
	 * @param option toggle option
	 * @return true if enabled
	 */
	public boolean isOptionEnabled(ChunkBundleToggleable option) {

		return toggleOptions.contains(option);

	}
	
	/**
	 * Enables option.
	 * 
	 * @param option toggle option
	 */
	public void enableOption(ChunkBundleToggleable option) {

		toggleOptions.add(option);

	}
	
	/**
	 * Disables option.
	 * 
	 * @param option toggle option
	 */
	public void disableOption(ChunkBundleToggleable option) {

		toggleOptions.remove(option);

	}
	
	
	/**
	 * Gets the forcedPvpProtection.
	 * 
	 * @return the forcedPvpProtection
	 */
	public Boolean hasPvpProtectionBonus2() {
		return pvpProtectionBonus;
	}

	/**
	 * Toggles the forcedPvpProtection.
	 */
	public void togglePvpProtectionBonus2() {
		this.pvpProtectionBonus = !pvpProtectionBonus;
	}

	/**
	 * Gets the enabledUnlimitedClaim.
	 * 
	 * @return the enabledUnlimitedClaim
	 */
	public Boolean hasUnlimitedClaimBonus2() {
		return unlimitedClaimBonus;
	}

	/**
	 * Toggles the enabledUnlimitedClaim.
	 */
	public void toggleUnlimitedClaim2() {
		this.unlimitedClaimBonus = !unlimitedClaimBonus;
	}
	
	
	
	// Messages:
	/**
	 * Broadcast a message to all members.
	 * 
	 * @param message message
	 */
	public void broadcast(String message){
		
		
		for (int i = 0; i < registeredPlayers.size(); i++) {
			registeredPlayers.get(i).message(message);
		}
		for (int i = 0; i < registeredFactions.size(); i++) {
			registeredFactions.get(i).broadcast(message);
		}
		
		
	}
	
	

	// Identification:
	/**
	 * Gets chunk group ID.
	 * 
	 * @return ID
	 */
	public Integer getId() {
		return id;
	}
	
	/**
	 * Sets the ID.
	 * 
	 * @param id the id to set
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * Gets the name.
	 * 
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	
	
	// Events:
	/**
	 * Called when an entity explodes on the chunk
	 * 
	 * @param event event
	 */
	void onEntityExplode(EntityExplodeEvent event, SagaChunk locationChunk) {

		
		// Cancel entity explosions:
		event.blockList().clear();

		
	}

	/**
	 * Called when a creature spawns.
	 * 
	 * @param event event
	 * @param locationChunk origin chunk.
	 */
	void onCreatureSpawn(CreatureSpawnEvent event, SagaChunk locationChunk) {
		

		if(event.isCancelled()){
			return;
		}
		
		// Forward to all buildings:
		for (int i = 0; i < groupChunks.size() && !event.isCancelled(); i++) {
			Building building = groupChunks.get(i).getBuilding();
			if(building != null) building.onCreatureSpawn(event, locationChunk);
		}
		
		
	}
	

	
	// Block events:
	/**
	 * Called when an entity forms blocks.
	 * 
	 * @param event event
	 * @param sagaChunk saga chunk
	 */
	public void onEntityBlockForm(EntityBlockFormEvent event, SagaChunk sagaChunk) {
		
		
		if(event.getEntity() instanceof Enderman){
			event.setCancelled(true);
		}
		
		
	}
	
	/**
	 * Called when a block spreads.
	 * 
	 * @param event event
	 * @param sagaChunk saga chunk
	 */
	public void onBlockSpread(BlockSpreadEvent event, SagaChunk sagaChunk) {
		
		
		// Cancel fire spread:
		if(!fireSpread){
			
			if(event.getNewState().getType().equals(Material.FIRE)){
				event.setCancelled(true);
				return;
			}
			
		}
		
		
		
	}
	
	/**
	 * Called when a block forms.
	 * 
	 * @param event event
	 * @param sagaChunk saga chunk
	 */
	public void onBlockFromTo(BlockFromToEvent event, SagaChunk sagaChunk) {
		
		
		// Cancel lava spread:
		if(!lavaSpread){
			
			if(event.getToBlock().getType().equals(Material.STATIONARY_LAVA) && event.getBlock().getLocation().getY() > 10){
				event.setCancelled(true);
				return;
			}
			if(event.getToBlock().getType().equals(Material.LAVA) && event.getBlock().getLocation().getY() > 10){
				event.setCancelled(true);
				return;
			}
			
		}
		
		
	}

	/**
	 * Called when a block is broken in the chunk.
	 * 
	 * @param event event
	 * @param sagaPlayer saga player
	 */
	public void onBuild(SagaBuildEvent event) {
		
		// Deny building:
		event.addBuildOverride(BuildOverride.CHUNK_GROUP_DENY);
		
	}

	
	
	// Command events:
    /**
     * Called when a player performs a command.
     * 
     * @param sagaPlayer saga player
     * @param event event
     * @param sagaChunk location chunk
     */
    public void onPlayerCommandPreprocess(SagaPlayer sagaPlayer, PlayerCommandPreprocessEvent event, SagaChunk sagaChunk) {

    	String command = event.getMessage().split(" ")[0].replace("/", "");
    	
    	// Permission:
    	if(SettlementConfiguration.config().checkMemberOnlyCommand(command) && 
    			!hasPermission(sagaPlayer, SettlementPermission.MEMBER_COMMAND)
    	){
    		sagaPlayer.message(GeneralMessages.noCommandPermission(this, command));
    		event.setCancelled(true);
    		return;
    	}

    }
    
	
	
	// Interact events:
	/**
	 * Called when a player interacts with something on the chunk.
	 * 
	 * @param event event
	 * @param sagaPlayer saga player
	 * @param sagaChunk saga chunk
	 */
    @SuppressWarnings("deprecation")
	public void onPlayerInteract(PlayerInteractEvent event, SagaPlayer sagaPlayer, SagaChunk sagaChunk) {
    	
		
		ItemStack item = event.getPlayer().getItemInHand();
		
		// Harmful potions:
		if(item != null && item.getType() == Material.POTION){

			Short durability = item.getDurability();
			
			if(BalanceConfiguration.config().getHarmfulSplashPotions().contains(durability)){
				event.setUseItemInHand(Result.DENY);
				sagaPlayer.message(GeneralMessages.noPermission(this));
				event.getPlayer().updateInventory();
				return;
			}
			
		}
		
		
    }

    
    
    // Damage events:
	/**
	 * Called when a a entity takes damage.
	 * 
	 * @param event event
	 */
	public void onEntityDamage(SagaEntityDamageEvent event, SagaChunk locationChunk){

		// Deny pvp:
		if(isOptionEnabled(ChunkBundleToggleable.PVP_PROTECTION)) event.addPvpOverride(PvPOverride.SAFE_AREA_DENY);
		
	}
	
	/**
	 * Called when a player is killed by another player.
	 * 
	 * @param event event
	 * @param damager damager saga player
	 * @param damaged damaged saga player
	 * @param locationChunk chunk where the pvp occurred
	 */
	public void onPvpKill(SagaPlayer attacker, SagaPlayer defender, SagaChunk locationChunk){
		
	}

	
	
	// Move events:
	/**
	 * Called when a player enters the chunk group.
	 * 
	 * @param sagaPlayer saga player
	 * @param last last chunk group, null if none
	 */
	public void onPlayerEnter(SagaPlayer sagaPlayer, ChunkBundle last) {

		sagaPlayer.message(SettlementMessages.entered(this));

	}
	
	/**
	 * Called when a player enters the chunk group.
	 * 
	 * @param sagaPlayer saga player
	 * @param next next chunk group, null if none
	 */
	public void onPlayerLeave(SagaPlayer sagaPlayer, ChunkBundle next) {

		if(next == null) sagaPlayer.message(SettlementMessages.left(this));


	}
	
	
	// Member events:
	/**
	 * Called when a member joins the game.
	 * 
	 * @param event event
	 * @param sagaPlayer saga player
	 */
	public void onMemberJoin(PlayerJoinEvent event, SagaPlayer sagaPlayer) {



	}
	
	/**
	 * Called when a member quits the game.
	 * 
	 * @param event event
	 * @param sagaPlayer saga player
	 */
	public void onMemberQuit(PlayerQuitEvent event, SagaPlayer sagaPlayer) {



	}

	/**
	 * Called when a member respawns.
	 * 
	 * @param sagaPlayer saga player
	 * @param event event
	 */
	public void onMemberRespawn(SagaPlayer sagaPlayer, PlayerRespawnEvent event) {

		
		// Forward to all buildings:
		for (int i = 0; i < groupChunks.size(); i++) {
			Building building = groupChunks.get(i).getBuilding();
			if(building != null) building.onMemberRespawn(sagaPlayer, event);
		}
		
		
	}
	
	
	
    // Other:
	/* 
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return getId() + "(" + getName() + ")";
	}
	

	
	// Control:
	/**
	 * Disables saving.
	 * 
	 */
	private void disableSaving() {

		SagaLogger.warning(this, "disabling saving");
		isSavingEnabled = false;
		
	}
	
	/**
	 * Checks if saving is enabled.
	 * 
	 * @return true if enabled
	 */
	public boolean isSavingEnabled() {
		return isSavingEnabled;
	}

	
	
	// Load save:
	/**
	 * Loads and a faction from disc.
	 * 
	 * @param chunkGroupId faction ID in String form
	 * @return saga faction
	 */
	public static ChunkBundle load(String id) {

		
		// Load:
		ChunkBundle config;
		try {
			
			config = WriterReader.read(Directory.SETTLEMENT_DATA, id, ChunkBundle.class);
			
		} catch (FileNotFoundException e) {
			
			SagaLogger.info(ChunkBundle.class, "missing data for " + id + " ID");
			config = new ChunkBundle("invalid");
			
		} catch (IOException e) {
			
			SagaLogger.severe(ChunkBundle.class, "failed to read data for " + id + " ID");
			config = new ChunkBundle("invalid");
			config.disableSaving();
			
		} catch (JsonParseException e) {
			
			SagaLogger.severe(ChunkBundle.class, "failed to parse data for " + id + " ID: " + e.getClass().getSimpleName() + "");
			SagaLogger.info("Parse message: " + e.getMessage());
			config = new ChunkBundle("invalid");
			config.disableSaving();
			
		}
		
		// Complete:
		config.complete();
		
		// Add to manager:
		ChunkBundleManager.manager().addChunkBundle(config);
		ArrayList<SagaChunk> groupChunks = config.getGroupChunks();
		for (SagaChunk sagaChunk : groupChunks) {
			ChunkBundleManager.manager().addChunk(sagaChunk);
		}
		
		// Enable:
		config.enable();
		
		return config;
		
		
	}

	/**
	 * Saves faction to disc.
	 * 
	 */
	public void save() {

		
		if(!isSavingEnabled){
			SagaLogger.warning(this, "saving disabled");
			return;
		}
		
		try {
			WriterReader.write(Directory.SETTLEMENT_DATA, id.toString(), this);
		} catch (IOException e) {
			SagaLogger.severe(this, "write failed: " + e.getClass().getSimpleName() + ":" + e.getMessage());
		}
		
		
	}



}
