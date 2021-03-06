package sk.stuba.fiit.perconik.core.resources;

import sk.stuba.fiit.perconik.core.Listener;
import sk.stuba.fiit.perconik.core.Nameable;

import static com.google.common.base.Preconditions.checkNotNull;

abstract class InternalHook<T, L extends Listener> extends AbstractHook<T, L> implements Nameable {
  private final InternalHandler<T, L> handler;

  InternalHook(final InternalHandler<T, L> handler) {
    super(Pools.safe(Pools.getObjectPoolFactory().create(handler), handler.type));

    this.handler = checkNotNull(handler);
  }

  static abstract class InternalHandler<T, L extends Listener> implements Handler<T> {
    final Class<T> type;

    final L listener;

    InternalHandler(final Class<T> type, final L listener) {
      this.type = checkNotNull(type);
      this.listener = checkNotNull(listener);
    }
  }

  @Override
  public final void preRegister() {
    this.preRegisterInternal();

    Hooks.addAll(this, this.toCollection());
  }

  @Override
  public final void postRegister() {
    this.postRegisterInternal();
  }

  @Override
  public final void preUnregister() {
    this.preUnregisterInternal();
  }

  @Override
  public final void postUnregister() {
    Hooks.removeAll(this, this.toCollection());

    this.postUnregisterInternal();
  }

  void preRegisterInternal() {}

  void postRegisterInternal() {}

  void preUnregisterInternal() {}

  void postUnregisterInternal() {}

  public final L forListener() {
    return this.handler.listener;
  }

  @Override
  public final String toString() {
    return this.getName();
  }

  public final String getName() {
    String name = this.handler.getClass().getName();

    return name.replaceFirst("Handler\\z", "Hook") + " for " + this.handler.listener;
  }
}
