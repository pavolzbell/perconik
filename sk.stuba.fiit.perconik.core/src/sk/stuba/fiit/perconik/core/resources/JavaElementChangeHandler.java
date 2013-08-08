package sk.stuba.fiit.perconik.core.resources;

import org.eclipse.jdt.core.JavaCore;
import sk.stuba.fiit.perconik.core.listeners.JavaElementChangeListener;

enum JavaElementChangeHandler implements Handler<JavaElementChangeListener>
{
	INSTANCE;
	
	public final void register(final JavaElementChangeListener listener)
	{
		JavaCore.addElementChangedListener(listener, Handlers.mask(listener));
	}

	public final void unregister(final JavaElementChangeListener listener)
	{
		JavaCore.removeElementChangedListener(listener);
	}
}
