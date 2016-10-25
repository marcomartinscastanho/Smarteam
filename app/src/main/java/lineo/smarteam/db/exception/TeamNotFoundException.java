package lineo.smarteam.db.exception;

import java.sql.SQLException;

/**
 * Created by marco on 25/10/2016.
 * TeamNotFoundException
 */
public class TeamNotFoundException extends SQLException {
    public TeamNotFoundException(String theReason) {
        super(theReason);
    }

    public TeamNotFoundException() {
        new TeamNotFoundException("Team not found");
    }
}
