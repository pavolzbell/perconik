package sk.stuba.fiit.perconik.core.services.resources;

import java.util.Set;
import sk.stuba.fiit.perconik.core.Listener;
import sk.stuba.fiit.perconik.core.Resource;
import sk.stuba.fiit.perconik.core.services.ForwardingProvider;

public abstract class ForwardingResourceProvider extends ForwardingProvider implements ResourceProvider
{
	protected ForwardingResourceProvider()
	{
	}

	@Override
	protected abstract ResourceProvider delegate();

	public Resource<?> forName(String name)
	{
		return this.delegate().forName(name);
	}

	public <L extends Listener> Set<Resource<? super L>> forClass(Class<L> type)
	{
		return this.delegate().forClass(type);
	}

	public Iterable<String> names()
	{
		return this.delegate().names();
	}

	public Iterable<Class<? extends Listener>> classes()
	{
		return this.delegate().classes();
	}

	public Iterable<Resource<?>> resources()
	{
		return this.delegate().resources();
	}
}
