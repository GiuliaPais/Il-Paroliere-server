package uninsubria.server.services.playerServicesImpl;

import uninsubria.server.db.api.TransactionManager;
import uninsubria.server.services.api.Service;
import uninsubria.utils.business.Player;
import uninsubria.utils.serviceResults.ServiceResultInterface;

/**
 * Implementation of Service responsible for updating player info.
 *
 * @author Giulia Pais
 * @version 0.9.0
 */
public class UpdatePlayerInfoService implements Service {
    /*---Fields---*/
    private final Player player;

    /*---Constructors---*/
    /**
     * Instantiates a new Update player info service.
     *
     * @param player the player
     */
    public UpdatePlayerInfoService(Player player) {
        this.player = player;
    }

    /*---Methods---*/
    @Override
    public ServiceResultInterface execute() {
        TransactionManager tm = new TransactionManager();
        tm.updatePlayerInfo(player);
        return null;
    }
}
