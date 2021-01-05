package uninsubria.server.services.roomServiceType;

import uninsubria.server.services.api.ServiceType;
import uninsubria.server.services.roomServicesImpl.RegisterGameStatsService;

/**
 * All available types of services that can be requested by a RoomManager.
 *
 * @author Giulia Pais
 * @version 0.9.2
 */
public enum RoomServiceType implements ServiceType {
    /*---Enum constants---*/
    GAME_STATS(RegisterGameStatsService.class);

    /*---Fields---*/
    private final Class<?> value;

    /*---Constructors---*/
    RoomServiceType(Class<?> value) {
        this.value = value;
    }

    /*---Methods---*/
    @Override
    public Class<?> getValue() {
        return value;
    }
}
