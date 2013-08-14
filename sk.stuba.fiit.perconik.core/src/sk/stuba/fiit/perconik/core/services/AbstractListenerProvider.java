package sk.stuba.fiit.perconik.core.services;

import sk.stuba.fiit.perconik.core.Listener;
import com.google.common.collect.BiMap;

public abstract class AbstractListenerProvider extends AbstractProvider implements ListenerProvider
{
	private final Object lock = new Object();
	
	protected AbstractListenerProvider()
	{
	}
	
	protected abstract BiMap<String, Class<? extends Listener>> map();

	protected abstract Class<? extends Listener> loadClassInternal(String name) throws ClassNotFoundException;
	
	protected abstract void validateClassInternal(Class<? extends Listener> type);
	
	public final <L extends Listener> L forClass(Class<L> type)
	{
		try
		{
			synchronized (this.lock)
			{
				if (!this.map().containsValue(type))
				{
					this.validateClassInternal(type);
					this.map().put(type.getName(), type);
				}
			}
			
			return type.newInstance();
		}
		catch (InstantiationException | IllegalAccessException e)
		{
			throw new UnsupportedOperationException(e);
		}
	}

	public final Class<? extends Listener> loadClass(final String name) throws ClassNotFoundException
	{
		synchronized (this.lock)
		{
			Class<? extends Listener> type = this.map().get(name);
			
			if (type == null)
			{
				type = this.loadClassInternal(name);
				
				this.validateClassInternal(type);
				this.map().put(name, type);
			}
			
			return type;
		}
	}

	public final Iterable<Class<? extends Listener>> loadedClasses()
	{
		synchronized (this.lock)
		{
			return map().values();
		}
	}
}
