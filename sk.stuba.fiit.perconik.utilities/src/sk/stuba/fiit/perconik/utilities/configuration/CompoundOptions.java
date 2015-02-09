package sk.stuba.fiit.perconik.utilities.configuration;

import java.io.Serializable;
import java.util.Map;

import com.google.common.collect.ImmutableList;

import static com.google.common.collect.Maps.newHashMap;

class CompoundOptions extends AbstractOptions implements Serializable {
  private static final long serialVersionUID = 0L;

  ImmutableList<Options> options;

  CompoundOptions(final Options primary, final Options secondary) {
    this.options = ImmutableList.of(primary, secondary);
  }

  CompoundOptions(final Iterable<Options> options) {
    this.options = ImmutableList.copyOf(options);
  }

  public Map<String, Object> toMap() {
    Map<String, Object> map = newHashMap();

    for (Options options: this.options) {
      map.putAll(options.toMap());
    }

    return map;
  }

  @Override
  public Object get(final String key) {
    for (Options options: this.options) {
      Object result = options.get(key);

      if (result != null) {
        return result;
      }
    }

    return null;
  }
}