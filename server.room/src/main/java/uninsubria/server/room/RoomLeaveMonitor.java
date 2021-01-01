package uninsubria.server.room;

import uninsubria.server.wrappers.PlayerWrapper;

import java.util.ArrayList;

/**
 * Monitor object for signaling when a player wants to leave the game while a match is ongoing
 *
 * @author Giulia Pais
 * @version 0.9.0
 */
public class RoomLeaveMonitor {
    /*---Fields---*/
    private ArrayList<PlayerWrapper> playersLeaving;

    /*---Constructors---*/
    public RoomLeaveMonitor() {
        playersLeaving = new ArrayList<>(6);
    }

    /*---Methods---*/
    public synchronized void signalPlayerisLeaving(PlayerWrapper player) {
        playersLeaving.add(player);
        notify();
    }

    public synchronized boolean isSomeoneLeaving(long waitTime) throws InterruptedException {
        while (playersLeaving.isEmpty()) {
            wait(waitTime);
        }
        return true;
    }

    public synchronized ArrayList<PlayerWrapper> getPlayersLeaving() {
        return this.playersLeaving;
    }

    public synchronized void clearQueue() {
        this.playersLeaving.clear();
    }
}
