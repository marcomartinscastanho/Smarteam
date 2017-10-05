package lineo.smarteam.exception;

import java.sql.SQLException;

/**
 * Created by marco on 25/10/2016.
 * IndividualResultNotFoundException
 */
class IndividualResultNotFoundException extends SQLException {
    private IndividualResultNotFoundException(String theReason) {
        super(theReason);
    }

    public IndividualResultNotFoundException() {
        new IndividualResultNotFoundException("Individual Result not found");
    }
}
