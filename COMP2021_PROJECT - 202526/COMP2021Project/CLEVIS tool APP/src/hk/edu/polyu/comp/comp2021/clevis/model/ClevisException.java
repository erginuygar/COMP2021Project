package hk.edu.polyu.comp.comp2021.clevis.model;

import java.io.Serial;

/**
 * Base checked exception and subclasses for Clevis system errors.
 * <p>
 * Contains all custom exceptions related to shape management and operations.
 */
public class ClevisException extends Exception {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new ClevisException with the specified message.
     *
     * @param message the detail message
     */
    public ClevisException(final String message) {
        super(message);
    }

    // ============================================================
    // Subclasses of ClevisException
    // ============================================================

    /**
     * Thrown when attempting to add a shape with a name that already exists.
     */
    public static final class DuplicateShapeException extends ClevisException {

        /**
         * Constructs a new DuplicateShapeException with the specified message.
         *
         * @param message the detail message
         */
        public DuplicateShapeException(final String message) {
            super(message);
        }
    }

    /**
     * Thrown when a shape with a given name cannot be found.
     */
    public static final class ShapeNotFoundException extends ClevisException {

        /**
         * Constructs a new ShapeNotFoundException with the specified message.
         *
         * @param message the detail message
         */
        public ShapeNotFoundException(final String message) {
            super(message);
        }
    }

    /**
     * Thrown when an error occurs during grouping or ungrouping of shapes.
     */
    public static final class GroupingException extends ClevisException {

        /**
         * Constructs a new GroupingException with the specified message.
         *
         * @param message the detail message
         */
        public GroupingException(final String message) {
            super(message);
        }
    }
}
