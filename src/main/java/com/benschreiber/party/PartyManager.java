package com.benschreiber.party;

import org.bukkit.entity.Player;

import java.util.*;

public class PartyManager {

    //Members map to their party id
    private static final HashMap<UUID, UUID> partyMembers = new HashMap<>();

    //Party id to party class
    private static final HashMap<UUID, Party> allParties = new HashMap<>();

    /**
     * Create a new party and add the leader to the partyMembers map and the allParties map
     * @param leader the player to be made the leader of the party
     */
    public static void newParty(Player leader) {

        Party party = new Party(leader);

        //Add the new party to the party list
        allParties.put(party.getId(), party);

        // Put the leader into the list of party members
        partyMembers.put(leader.getUniqueId(), party.getId());

    }

    /**
     * Completely remove the party and all members
     * @param leader party leader
     */
    public static void disbandParty(Player leader) {

        //Grab the party obj from the id
        Party party = allParties.get(partyMembers.get(leader.getUniqueId()));

        //Remove all Players of the party from the partyMembers map
        for (Player p: party.getPlayers()) {
            partyMembers.remove(p.getUniqueId());
        }

        //Remove the party
        allParties.remove(partyMembers.get(leader.getUniqueId()));
    }

    public static void disbandParty(Party party) {

        //Remove all Players of the party from the partyMembers map
        for (Player p: party.getPlayers()) {
            partyMembers.remove(p.getUniqueId());
        }

        //Remove the party
        allParties.remove(party.getId());
    }

    /**
     * Add a player into a party and party list
     * @param party party ID
     * @param player the player to be added
     */
    public static void addPartyMember(UUID party, Player player) {

        // Put the player into the list of members
        partyMembers.put(player.getUniqueId(), party);

        // Put player into party object
        allParties.get(party).getPlayers().add(player);

    }

    /**
     * Remove a player from their party.
     * @param player the player to be removed from both the party and the members list
     */
    public static void removePartyMember(Player player) {

        //Remove the player from the members list and grab the party ID
        UUID uuid = partyMembers.remove(player.getUniqueId());

        //Remove from the party
        allParties.get(uuid).getPlayers().remove(player);

    }

    public static Party getParty(UUID partyID) {
        return allParties.get(partyID);
    }

    public static Party getParty(Player player) {
        return allParties.get(
                partyMembers.get(player.getUniqueId())
        );
    }

    public static List<Player> getPartyMembers(Player player) {
        return allParties.get(
                partyMembers.get(player.getUniqueId())
        ).getPlayers();
    }

    public static boolean inAParty(Player player) {
        return partyMembers.containsKey(player.getUniqueId());
    }

    public static boolean inAMiniGame(Player player) {
        if (!inAParty(player)) return false;

        Party party = getParty(player);
        return party.inMiniGame();
    }

    public static boolean isAPartyLeader(Player player) {
        UUID p = partyMembers.get(player.getUniqueId());
        if (p == null) return false;
        return getParty(p).getLeader().equals(player);
    }

    public static boolean gameIsRunning() {
        for (Party p: allParties.values()) {
            if (p.inMiniGame()) return true;
        }
        return false;
    }
}
