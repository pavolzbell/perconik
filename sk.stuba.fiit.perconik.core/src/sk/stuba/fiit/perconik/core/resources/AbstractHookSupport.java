package sk.stuba.fiit.perconik.core.resources;

import java.util.Map;

import sk.stuba.fiit.perconik.core.Listener;
import sk.stuba.fiit.perconik.core.Resource;

import static com.google.common.collect.Maps.newHashMap;

abstract class AbstractHookSupport<H extends Hook<T, L>, T, L extends Listener> implements HookFactory<T, L> {
  private final Map<L, H> hooks;

  AbstractHookSupport() {
    this.hooks = newHashMap();
  }

  @SuppressWarnings("unchecked")
  final void hook(final Resource<? super H> resource, final L listener) {
    H hook = this.hooks.get(listener);

    if (hook == null) {
      // safe cast as internal support always creates correct hooks
      hook = (H) this.create(listener);

      this.hooks.put(listener, hook);
    }

    DefaultResources.registerTo(resource, hook);
  }

  final void unhook(final Resource<? super H> resource, final L listener) {
    H hook = this.hooks.get(listener);

    if (hook != null) {
      DefaultResources.unregisterFrom(resource, hook);
    }
  }
}
