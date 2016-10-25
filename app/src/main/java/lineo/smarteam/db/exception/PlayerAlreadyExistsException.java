package lineo.smarteam.db.exception;

import java.sql.SQLException;

/**
 * Created by marco on 25/10/2016.
 * PlayerAlreadyExistsException
 */
public class PlayerAlreadyExistsException extends SQLException {
    public PlayerAlreadyExistsException(String theReason) {
        super(theReason);
    }
    public PlayerAlreadyExistsException() {
        new PlayerAlreadyExistsException("Player already exists");
    }
}
