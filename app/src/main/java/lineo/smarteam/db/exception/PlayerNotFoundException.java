package lineo.smarteam.db.exception;

import java.sql.SQLException;

/**
 * Created by marco on 25/10/2016.
 * PlayerNotFoundException
 */
public class PlayerNotFoundException extends SQLException {
    public PlayerNotFoundException(String theReason) {
        super(theReason);
    }
    public PlayerNotFoundException() {
        new PlayerNotFoundException("Player not found");
    }
}
