package sk.stuba.fiit.perconik.core;

import java.util.Collection;
import java.util.Map;

public final class Listeners
{
	private Listeners()
	{
		throw new AssertionError();
	}

	public static final <L extends Listener> void register(final L listener)
	{
		Services.getListenerService().getListenerManager().register(listener);
	}

	public static final <L extends Listener> void unregister(final L listener)
	{
		Services.getListenerService().getListenerManager().unregister(listener);
	}

	public static final void unregisterAll()
	{
		unregisterAll(Listener.class);
	}

	public static final void unregisterAll(final Class<? extends Listener> type)
	{
		Services.getListenerService().getListenerManager().unregisterAll(type);
	}

	public static final Collection<Listener> registered()
	{
		return registered(Listener.class);
	}
	
	public static final <L extends Listener> Collection<L> registered(final Class<L> type)
	{
		return Services.getListenerService().getListenerManager().registered(type);
	}

	public static final Map<Resource<?>, Collection<Listener>> registrations()
	{
		return Services.getListenerService().getListenerManager().registrations();
	}

}
