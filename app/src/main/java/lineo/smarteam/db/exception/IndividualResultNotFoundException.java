package lineo.smarteam.db.exception;

import java.sql.SQLException;

/**
 * Created by marco on 25/10/2016.
 * IndividualResultNotFoundException
 */
public class IndividualResultNotFoundException extends SQLException {
    public IndividualResultNotFoundException(String theReason) {
        super(theReason);
    }
    public IndividualResultNotFoundException() {
        new IndividualResultNotFoundException("Individual Result not found");
    }
}
