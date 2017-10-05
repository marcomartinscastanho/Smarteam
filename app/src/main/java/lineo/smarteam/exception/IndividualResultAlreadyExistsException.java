package lineo.smarteam.exception;

import java.sql.SQLException;

/**
 * Created by marco on 25/10/2016.
 * IndividualResultAlreadyExistsException
 */
class IndividualResultAlreadyExistsException extends SQLException {
    private IndividualResultAlreadyExistsException(String theReason) {
        super(theReason);
    }

    public IndividualResultAlreadyExistsException() {
        new IndividualResultAlreadyExistsException("Individual Result already exists");
    }
}
