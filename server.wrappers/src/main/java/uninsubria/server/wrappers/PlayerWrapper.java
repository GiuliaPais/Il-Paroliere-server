package uninsubria.server.wrappers;

import uninsubria.utils.business.Player;

import java.net.InetAddress;

/**
 * Wrapper for a player that also contains its associated ip address.
 *
 * @author Giulia Pais
 * @version 0.9.0
 */
public class PlayerWrapper {
    /*---Fields---*/
    private Player player;
    private InetAddress ipAddress;

    /*---Constructors---*/

    /**
     * Instantiates a new Player wrapper.
     *
     * @param player    the player
     * @param ipAddress the ip address
     */
    public PlayerWrapper(Player player, InetAddress ipAddress) {
        this.player = player;
        this.ipAddress = ipAddress;
    }

    /*---Methods---*/
    /**
     * Gets player.
     *
     * @return the player
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Sets player.
     *
     * @param player the player
     */
    public void setPlayer(Player player) {
        this.player = player;
    }

    /**
     * Gets ip address.
     *
     * @return the ip address
     */
    public InetAddress getIpAddress() {
        return ipAddress;
    }

    /**
     * Sets ip address.
     *
     * @param ipAddress the ip address
     */
    public void setIpAddress(InetAddress ipAddress) {
        this.ipAddress = ipAddress;
    }
}
