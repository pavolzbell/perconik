package sk.stuba.fiit.perconik.core.debug.services.resources;

import java.util.Set;

import com.google.common.collect.SetMultimap;

import sk.stuba.fiit.perconik.core.Listener;
import sk.stuba.fiit.perconik.core.Resource;
import sk.stuba.fiit.perconik.core.debug.Debug;
import sk.stuba.fiit.perconik.core.debug.DebugListeners;
import sk.stuba.fiit.perconik.core.debug.DebugNameableProxy;
import sk.stuba.fiit.perconik.core.debug.DebugResources;
import sk.stuba.fiit.perconik.core.debug.runtime.DebugConsole;
import sk.stuba.fiit.perconik.core.services.resources.ResourceManager;

import static com.google.common.base.Preconditions.checkNotNull;

public final class DebugResourceManagerProxy extends DebugNameableProxy implements DebugResourceManager {
  private final ResourceManager manager;

  private DebugResourceManagerProxy(final ResourceManager manager, final DebugConsole console) {
    super(console);

    this.manager = checkNotNull(manager);
  }

  public static DebugResourceManagerProxy wrap(final ResourceManager manager) {
    return wrap(manager, Debug.getDefaultConsole());
  }

  public static DebugResourceManagerProxy wrap(final ResourceManager manager, final DebugConsole console) {
    if (manager instanceof DebugResourceManagerProxy) {
      return (DebugResourceManagerProxy) manager;
    }

    return new DebugResourceManagerProxy(manager, console);
  }

  public static ResourceManager unwrap(final ResourceManager manager) {
    if (manager instanceof DebugResourceManagerProxy) {
      return ((DebugResourceManagerProxy) manager).delegate();
    }

    return manager;
  }

  @Override
  public ResourceManager delegate() {
    return this.manager;
  }

  public <L extends Listener> void register(final Class<L> type, final Resource<? super L> resource) {
    this.print("Registering resource %s to listener type %s", DebugResources.toString(resource), DebugListeners.toString(type));
    this.tab();

    this.delegate().register(type, resource);

    this.untab();
  }

  public <L extends Listener> void unregister(final Class<L> type, final Resource<? super L> resource) {
    this.print("Unregistering resource %s from listener type %s", DebugResources.toString(resource), DebugListeners.toString(type));
    this.tab();

    this.delegate().unregister(type, resource);

    this.untab();
  }

  public <L extends Listener> void unregisterAll(final Class<L> type) {
    this.print("Unregistering all resources assignable to listener type %s", DebugListeners.toString(type));
    this.tab();

    this.delegate().unregisterAll(type);

    this.untab();
  }

  public <L extends Listener> Set<Resource<? extends L>> assignables(final Class<L> type) {
    return this.delegate().assignables(type);
  }

  public <L extends Listener> Set<Resource<? super L>> registrables(final Class<L> type) {
    return this.delegate().registrables(type);
  }

  public SetMultimap<Class<? extends Listener>, Resource<?>> registrations() {
    return this.delegate().registrations();
  }

  public boolean registered(final Class<? extends Listener> type, final Resource<?> resource) {
    return this.delegate().registered(type, resource);
  }
}
