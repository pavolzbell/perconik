package sk.stuba.fiit.perconik.core;

import javax.annotation.Nullable;

/**
 * Thrown when one of the core service classes tries to instantiate
 * a resource through its class but the instantiation process fails.
 *
 * @author Pavol Zbell
 * @since 1.0
 */
public class ResourceInstantiationException extends RuntimeException {
  private static final long serialVersionUID = 0L;

  /**
   * Creates a new instance with no detail message.
   */
  public ResourceInstantiationException() {
    super();
  }

  /**
   * Creates a new instance with the given detail message.
   */
  public ResourceInstantiationException(@Nullable final String message) {
    super(message);
  }

  /**
   * Creates a new instance with the given detail message and cause.
   */
  public ResourceInstantiationException(@Nullable final String message, @Nullable final Throwable cause) {
    super(message, cause);
  }

  /**
   * Creates a new instance with the given cause.
   */
  public ResourceInstantiationException(@Nullable final Throwable cause) {
    super(cause);
  }
}
