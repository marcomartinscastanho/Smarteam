package lineo.smarteam.db.exception;

import java.sql.SQLException;

/**
 * Created by marco on 25/10/2016.
 * TeamAlreadyExistsException
 */
public class TeamAlreadyExistsException extends SQLException {
    public TeamAlreadyExistsException() {
        new TeamAlreadyExistsException("Team already exists");
    }

    public TeamAlreadyExistsException(String theReason) {
        super(theReason);
    }
}
