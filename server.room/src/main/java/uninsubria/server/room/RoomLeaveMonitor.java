package uninsubria.server.room;

import uninsubria.server.wrappers.PlayerWrapper;

import java.util.ArrayList;

/**
 * Monitor object for signaling when a player wants to leave the game while a match is ongoing
 *
 * @author Giulia Pais
 * @version 0.9.1
 */
public class RoomLeaveMonitor {
    /*---Fields---*/
    private ArrayList<PlayerWrapper> playersLeaving;

    /*---Constructors---*/

    /**
     * Instantiates a new Room leave monitor.
     */
    public RoomLeaveMonitor() {
        playersLeaving = new ArrayList<>(6);
    }

    /*---Methods---*/
    /**
     * Signals a player is leaving the room and notifies all threads waiting.
     *
     * @param player the player
     */
    public synchronized void signalPlayerisLeaving(PlayerWrapper player) {
        playersLeaving.add(player);
        notify();
    }

    /**
     * Waits either for a notification that a player is leaving or for the amount of time specified.
     *
     * @param waitTime the wait time
     * @return false if, when waked, no players were leaving, true otherwise
     * @throws InterruptedException the interrupted exception
     */
    public synchronized boolean isSomeoneLeaving(long waitTime) throws InterruptedException {
        while (playersLeaving.isEmpty()) {
            wait(waitTime);
            if (playersLeaving.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns a list of the leaving players.
     *
     * @return the players leaving
     */
    public synchronized ArrayList<PlayerWrapper> getPlayersLeaving() {
        return this.playersLeaving;
    }

    /**
     * Clear queue.
     */
    public synchronized void clearQueue() {
        this.playersLeaving.clear();
    }
}
