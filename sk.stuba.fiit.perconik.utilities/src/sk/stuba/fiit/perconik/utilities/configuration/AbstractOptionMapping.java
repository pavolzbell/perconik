package sk.stuba.fiit.perconik.utilities.configuration;

import javax.annotation.Nullable;

import com.google.common.reflect.TypeToken;

import static com.google.common.base.Preconditions.checkNotNull;

import static sk.stuba.fiit.perconik.utilities.MorePreconditions.checkNotNullAsState;

public abstract class AbstractOptionMapping<T> implements OptionMapping<T> {
  /**
   * Option type, exact runtime type.
   */
  protected final TypeToken<T> type;

  /**
   * Option key, non-empty string.
   */
  protected final String key;

  /**
   * Default value, may be {@code null}.
   */
  @Nullable
  protected final T defaultValue;

  /**
   * Constructor for use by subclasses.
   */
  protected AbstractOptionMapping(final TypeToken<T> type, final String key, @Nullable final T defaultValue) {
    this.type = checkNotNull(type);
    this.key = checkNotNullAsState(key);
    this.defaultValue = defaultValue;
  }

  public final TypeToken<T> getType() {
    return this.type;
  }

  public final String getKey() {
    return this.key;
  }

  public final T getDefaultValue() {
    return this.defaultValue;
  }
}
