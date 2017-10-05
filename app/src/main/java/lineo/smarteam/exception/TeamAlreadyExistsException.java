package lineo.smarteam.exception;

import java.sql.SQLException;

/**
 * Created by marco on 25/10/2016.
 * TeamAlreadyExistsException
 */
public class TeamAlreadyExistsException extends SQLException {
    public TeamAlreadyExistsException() {
        new TeamAlreadyExistsException("Team already exists");
    }

    private TeamAlreadyExistsException(String theReason) {
        super(theReason);
    }
}
