package org.federiconafria.transfer.logic.exceptions;

public class EntityCreationException extends Exception {
    public EntityCreationException(String message, Throwable cause) {
        super(message, cause);
    }

    public EntityCreationException(String message) {
        super(message);
    }
}
