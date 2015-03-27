package net.madmanmarkau.multihome.data;

import com.google.common.base.Supplier;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimaps;
import net.madmanmarkau.multihome.MultiHomePlugin;
import net.madmanmarkau.multihome.util.MessageUtil;
import net.madmanmarkau.multihome.util.MiscUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages a database of player home locations.
 *
 * @author MadManMarkAu
 */

public class HomeManagerFile extends HomeManager {
    private final File homesFile;
    private ListMultimap<String, HomeEntry> homeEntries = Multimaps.newListMultimap(new ConcurrentHashMap<String, Collection<HomeEntry>>(), new Supplier<List<HomeEntry>>() {
        @Override
        public List<HomeEntry> get() {
            return new ArrayList<>();
        }
    });

    public HomeManagerFile(MultiHomePlugin plugin) {
        super(plugin);
        this.homesFile = new File(plugin.getDataFolder(), "homes.txt");
        loadHomes();
    }

    @Override
    public void clearHomes() {
        this.homeEntries.clear();
        saveHomes();
    }

    @Override
    public HomeEntry getHomeById(String playerId, String name) {
        if (this.homeEntries.containsKey(playerId)) {
            for (HomeEntry thisLocation : homeEntries.get(playerId)) {
                if (thisLocation.getHomeName().equalsIgnoreCase(name)) {
                    return thisLocation;
                }
            }
        }

        return null;
    }

    @Override
    public HomeEntry getHomeByName(String playerName, String name) {
        OfflinePlayer potentialPlayer = Bukkit.getOfflinePlayer(playerName);
        if (potentialPlayer.isOnline()) {
            return getHomeById(potentialPlayer.getUniqueId().toString(), name);
        }

        for (HomeEntry thisHome : homeEntries.values()) {
            if (playerName.equals(thisHome.getOwnerName()) && thisHome.getHomeName().equals(name)) {
                return thisHome;
            }
        }

        return null;
    }

    @Override
    public void addHome(Player player, String name, Location location) {
        List<HomeEntry> homes;

        // Get the List of homes for this player
        if (this.homeEntries.containsKey(player.getUniqueId().toString())) {
            homes = this.homeEntries.get(player.getUniqueId().toString());
        } else {
            homes = new ArrayList<>();
        }

        boolean homeSet = false;

        HomeEntry home = new HomeEntry(player.getUniqueId().toString(), player.getName(), name.toLowerCase(), location);

        for (int index = 0; index < homes.size(); index++) {
            HomeEntry thisHome = homes.get(index);
            if (thisHome.getHomeName().compareToIgnoreCase(name) == 0) {
                // An existing home was found. Overwrite it.
                homes.set(index, home);
                homeSet = true;
            }
        }

        if (!homeSet) {
            // No existing location found. Create new entry.
            homes.add(home);
        }

        // Replace the List in the homes Map
        this.homeEntries.replaceValues(player.getUniqueId().toString(), homes);

        // Save
        this.saveHomes();
    }

    @Override
    public void removeHome(String player, String name) {
        if (this.homeEntries.containsKey(player.toLowerCase())) {
            List<HomeEntry> playerHomeList = this.homeEntries.get(player.toLowerCase());
            List<HomeEntry> removeList = new ArrayList<>();

            // Find all homes matching "name"
            for (HomeEntry thisHome : playerHomeList) {
                if (thisHome.getHomeName().compareToIgnoreCase(name) == 0) {
                    // Found match. Mark it for deletion.
                    removeList.add(thisHome);
                }
            }

            // Remove all matching homes.
            playerHomeList.removeAll(removeList);

            // Replace the List in the homes HashMap
            this.homeEntries.replaceValues(player, playerHomeList);

            // Save
            this.saveHomes();
        }
    }

    @Override
    public boolean getUserExists(String player) {
        return this.homeEntries.containsKey(player.toLowerCase());
    }

    @Override
    public int getUserHomeCount(String player) {
        if (this.homeEntries.containsKey(player.toLowerCase())) {
            return this.homeEntries.get(player.toLowerCase()).size();
        } else {
            return 0;
        }
    }

    @Override
    public List<HomeEntry> listUserHomes(String player) {
        if (this.homeEntries.containsKey(player.toLowerCase())) {
            return this.homeEntries.get(player.toLowerCase());
        } else {
            return new ArrayList<>();
        }
    }

    @Override
    public void importHomes(List<HomeEntry> homes, boolean overwrite) {
        List<HomeEntry> playerHomes;

        for (HomeEntry thisEntry : homes) {
            // Get the ArrayList of homes for this player
            if (this.homeEntries.containsKey(thisEntry.getOwnerName().toLowerCase())) {
                playerHomes = this.homeEntries.get(thisEntry.getOwnerName().toLowerCase());
            } else {
                playerHomes = new ArrayList<HomeEntry>();
            }

            boolean homeFound = false;

            for (int index = 0; index < playerHomes.size(); index++) {
                HomeEntry thisHome = playerHomes.get(index);
                if (thisHome.getHomeName().equals(thisEntry.getHomeName())) {
                    // An existing home was found.
                    if (overwrite) {
                        playerHomes.set(index, thisEntry);
                    }

                    homeFound = true;
                }
            }

            if (!homeFound) {
                // No existing location found. Create new entry.
                HomeEntry newHome = new HomeEntry(thisEntry.getOwnerId(), thisEntry.getOwnerName(), thisEntry.getHomeName(), thisEntry.getHomeLocation(plugin.getServer()));
                playerHomes.add(newHome);
            }

            // Replace the List in the homes HashMap
            this.homeEntries.replaceValues(thisEntry.getOwnerId(), playerHomes);
        }

        // Save
        this.saveHomes();
    }


    /**
     * Save homes list to file. Clears the saveRequired flag.
     */
    private void saveHomes() {
        try {
            FileWriter fstream = new FileWriter(this.homesFile);
            BufferedWriter writer = new BufferedWriter(fstream);

            writer.write("# Stores user home locations." + MiscUtil.newLine());
            writer.write("# <uuid>;<username>;<x>;<y>;<z>;<pitch>;<yaw>;<world>[;<name>]" + MiscUtil.newLine());
            writer.write(MiscUtil.newLine());

            for (HomeEntry thisHome : this.homeEntries.values()) {
                writer.write(thisHome.getOwnerId() + ";" + thisHome.getOwnerName() + ";" + thisHome.getX() + ";" + thisHome.getY() + ";" + thisHome.getZ() + ";"
                        + thisHome.getPitch() + ";" + thisHome.getYaw() + ";"
                        + thisHome.getWorld() + ";" + thisHome.getHomeName() + MiscUtil.newLine());
            }
            writer.close();
        } catch (Exception e) {
            MessageUtil.logSevere("Could not write the homes file.");
        }
    }

    /**
     * Load the homes list from file.
     */
    private void loadHomes() {
        if (this.homesFile.exists()) {
            try {
                FileReader fstream = new FileReader(this.homesFile);
                BufferedReader reader = new BufferedReader(fstream);

                String line = reader.readLine().trim();

                this.homeEntries.clear();

                while (line != null) {
                    if (!line.startsWith("#") && line.length() > 0) {
                        HomeEntry thisHome;

                        thisHome = parseHomeLine(line);

                        if (thisHome != null) {
                            List<HomeEntry> homeList;

                            // Find Map entry for player
                            if (!this.homeEntries.containsKey(thisHome.getOwnerId())) {
                                homeList = new ArrayList<>();
                            } else {
                                // Player not exist. Create dummy entry.
                                homeList = this.homeEntries.get(thisHome.getOwnerId());
                            }

                            // Don't save if this is a duplicate entry.
                            boolean save = true;
                            for (HomeEntry home : homeList) {
                                if (home.getHomeName().compareToIgnoreCase(thisHome.getHomeName()) == 0) {
                                    save = false;
                                }
                            }

                            if (save) {
                                homeList.add(thisHome);
                            }

                            this.homeEntries.replaceValues(thisHome.getOwnerId(), homeList);
                        }
                    }

                    line = reader.readLine();
                }

                reader.close();
            } catch (Exception e) {
                MessageUtil.logSevere("Could not read the homes file.");
                return;
            }
        }

        saveHomes();
    }


    private HomeEntry parseHomeLine(String line) {
        String[] values = line.split(";");
        double X = 0, Y = 0, Z = 0;
        float pitch = 0, yaw = 0;
        String world = "";
        String name = "";
        String playerId = "";
        String playerName = "";

        try {
            if (values.length == 8) {
                playerId = values[0];
                playerName = values[1];
                X = Double.parseDouble(values[2]);
                Y = Double.parseDouble(values[3]);
                Z = Double.parseDouble(values[4]);
                pitch = Float.parseFloat(values[5]);
                yaw = Float.parseFloat(values[6]);

                world = values[7];
                name = "";
            } else if (values.length == 9) {
                playerId = values[0];
                playerName = values[1];
                X = Double.parseDouble(values[2]);
                Y = Double.parseDouble(values[3]);
                Z = Double.parseDouble(values[4]);
                pitch = Float.parseFloat(values[5]);
                yaw = Float.parseFloat(values[6]);

                world = values[7];
                name = values[8];
            }
        } catch (Exception ignored) {
            // This entry failed. Ignore and continue.
            MessageUtil.logWarning("Failed to load home location! Line: " + line);
        }

        if (values.length == 7 || values.length == 8) {
            return new HomeEntry(playerId, playerName, name, world, X, Y, Z, pitch, yaw);
        }

        return null;
    }
}
