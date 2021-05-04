package io.github.bedwarsrel;

import io.github.bedwarsrel.game.Game;
import io.github.bedwarsrel.game.GameState;
import io.github.bedwarsrel.statistics.PlayerStatistic;
import org.bukkit.*;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.*;

import java.util.ArrayList;

/**
 * This class will be registered through the register-method in the
 * plugins onEnable-method.
 */
public class BedwarsRelPlaceholders extends PlaceholderExpansion {

    private BedwarsRel plugin;

    /**
     * Since we register the expansion inside our own plugin, we
     * can simply use this method here to get an instance of our
     * plugin.
     *
     * @param plugin
     *        The instance of our plugin.
     */
    public BedwarsRelPlaceholders(BedwarsRel plugin){
        this.plugin = plugin;
    }

    /**
     * Because this is an internal class,
     * you must override this method to let PlaceholderAPI know to not unregister your expansion class when
     * PlaceholderAPI is reloaded
     *
     * @return true to persist through reloads
     */
    @Override
    public boolean persist(){
        return true;
    }

    /**
     * Because this is a internal class, this check is not needed
     * and we can simply return {@code true}
     *
     * @return Always true since it's an internal class.
     */
    @Override
    public boolean canRegister(){
        return true;
    }

    /**
     * The name of the person who created this expansion should go here.
     * <br>For convienience do we return the author from the plugin.yml
     *
     * @return The name of the author as a String.
     */
    @Override
    public String getAuthor(){
        return plugin.getDescription().getAuthors().toString();
    }

    /**
     * The placeholder identifier should go here.
     * <br>This is what tells PlaceholderAPI to call our onRequest
     * method to obtain a value if a placeholder starts with our
     * identifier.
     * <br>The identifier has to be lowercase and can't contain _ or %
     *
     * @return The identifier in {@code %<identifier>_<value>%} as String.
     */
    @Override
    public String getIdentifier(){
        return "bwrel";
    }

    /**
     * This is the version of the expansion.
     * <br>You don't have to use numbers, since it is set as a String.
     *
     * For convienience do we return the version from the plugin.yml
     *
     * @return The version as a String.
     */
    @Override
    public String getVersion(){
        return plugin.getDescription().getVersion();
    }

    /**
     * This is the method called when a placeholder with our identifier
     * is found and needs a value.
     * <br>We specify the value identifier in this method.
     * <br>Since version 2.9.1 can you use OfflinePlayers in your requests.
     *
     * @param  player
     *         A {@link org.bukkit.entity.Player Player}.
     * @param  identifier
     *         A String containing the identifier/value.
     *
     * @return possibly-null String of the requested identifier.
     */
    @Override
    public String onPlaceholderRequest(Player player, String identifier){

        if(player == null){
            return "";
        }

        if (identifier.startsWith("gamename")) {
            Game game = getGameFromPlaceholder(identifier, 2);
            return game != null ? game.getName() : null;

        } else if (identifier.startsWith("regionname")) {
            Game game = getGameFromPlaceholder(identifier, 2);
            return game != null ? game.getRegionName() : null;

        } else if (identifier.startsWith("currentplayers")) {
            Game game = getGameFromPlaceholder(identifier, 2);
            return game != null ? getGamePlayersString(game, "current") : null;

        } else if (identifier.startsWith("maxplayers")) {
            Game game = getGameFromPlaceholder(identifier, 2);
            return game != null ? getGamePlayersString(game, "max") : null;

        } else if (identifier.startsWith("status")) {
            Game game = getGameFromPlaceholder(identifier, 2);
            return game != null ? getStatus(game) : null;

        } else if (identifier.equals("allplayers")) {
            return getAllPlayersString("current");

        } else if (identifier.equals("allslots")) {
            return getAllPlayersString("max");

        } else if (identifier.equals("running")) {
            return getAllGamesString("run");

        } else if (identifier.equals("working")) {
            return getAllGamesString("work");

        } else if (identifier.equals("allgames")) {
            ArrayList<Game> games = plugin.getGameManager().getGames();
            return String.valueOf(games.size());
        }
        //##########################
        else if (identifier.equals("player_score")) {
            PlayerStatistic statistic = BedwarsRel.getInstance()
                    .getPlayerStatisticManager().getStatistic(player);
            return String.valueOf(statistic.getScore() + statistic.getCurrentScore());

        } else if (identifier.equals("player_wins")) {
            PlayerStatistic statistic = BedwarsRel.getInstance()
                    .getPlayerStatisticManager().getStatistic(player);
            return String.valueOf(statistic.getWins() + statistic.getCurrentWins());

        } else if (identifier.equals("player_loses")) {
            PlayerStatistic statistic = BedwarsRel.getInstance()
                    .getPlayerStatisticManager().getStatistic(player);
            return String.valueOf(statistic.getLoses() + statistic.getCurrentLoses());

        } else if (identifier.equals("player_kills")) {
            PlayerStatistic statistic = BedwarsRel.getInstance()
                    .getPlayerStatisticManager().getStatistic(player);
            return String.valueOf(statistic.getKills() + statistic.getCurrentKills());

        } else if (identifier.equals("player_deaths")) {
            PlayerStatistic statistic = BedwarsRel.getInstance()
                    .getPlayerStatisticManager().getStatistic(player);
            return String.valueOf(statistic.getDeaths() + statistic.getCurrentDeaths());

        } else if (identifier.equals("player_destroyedbeds")) {
            PlayerStatistic statistic = BedwarsRel.getInstance()
                    .getPlayerStatisticManager().getStatistic(player);
            return String.valueOf(statistic.getDestroyedBeds() + statistic.getCurrentDestroyedBeds());

        } else if (identifier.equals("player_games")) {
            PlayerStatistic statistic = BedwarsRel.getInstance()
                    .getPlayerStatisticManager().getStatistic(player);
            return String.valueOf(statistic.getGames() + statistic.getCurrentGames());
        }

        return null;
    }

    private Game getGameFromPlaceholder(String identifier, int length) {
        String[] temp = identifier.split("_");
        if (temp.length != length) {
            return null;
        }
        return plugin.getGameManager().getGame(temp[length-1]);
    }

    private String getGamePlayersString(Game game, String maxorcurrent) {
        int currentPlayers = 0;
        int maxPlayers = game.getMaxPlayers();

        if (game.getState() == GameState.RUNNING) {
            currentPlayers = game.getTeamPlayers().size();
        } else if (game.getState() == GameState.WAITING) {
            currentPlayers = game.getPlayers().size();
        }

        String current = String.valueOf(currentPlayers);
        String max = String.valueOf(maxPlayers);

        if (currentPlayers >= maxPlayers) {
            current = ChatColor.RED + current + ChatColor.WHITE;
            max = ChatColor.RED + max + ChatColor.WHITE;
        }

        return maxorcurrent.equals("max") ? max : current;
    }

    private String getStatus(Game game) {
        String status = null;
        if (game.getState() == GameState.WAITING && game.isFull()) {
            status = ChatColor.RED + BedwarsRel._l("sign.gamestate.full");
        } else {
            status = BedwarsRel._l("sign.gamestate." + game.getState().toString().toLowerCase());
        }
        return status;
    }

    private String getAllPlayersString(String maxorcurrent) {
        ArrayList<Game> games = plugin.getGameManager().getGames();
        int AllPlayers = 0;
        int AllSlots = 0;

        for (Game g : games) {
            if (g.getState() == GameState.RUNNING) {
                AllPlayers += g.getTeamPlayers().size();
                AllSlots += g.getMaxPlayers();
            } else if (g.getState() == GameState.WAITING) {
                AllPlayers += g.getPlayers().size();
                AllSlots += g.getMaxPlayers();
            }
        }

        String players = String.valueOf(AllPlayers);
        String slots = String.valueOf(AllSlots);

        if (AllPlayers >= AllSlots) {
            players = ChatColor.RED + players + ChatColor.WHITE;
            slots = ChatColor.RED + slots + ChatColor.WHITE;
        }

        return maxorcurrent.equals("max") ? slots : players;
    }

    private String getAllGamesString(String runorwork) {
        ArrayList<Game> games = plugin.getGameManager().getGames();
        int running = 0;
        int working = 0;

        for (Game g : games) {
            if (g.getState() == GameState.RUNNING) {
                running ++;
                working ++;
            } else if (g.getState() == GameState.WAITING) {
                working ++;
            }
        }

        String run = String.valueOf(running);
        String work = String.valueOf(working);

        if (running >= working) {
            run = ChatColor.RED + run + ChatColor.WHITE;
            work = ChatColor.RED + work + ChatColor.WHITE;
        }

        return runorwork.equals("run") ? run : work;
    }
}