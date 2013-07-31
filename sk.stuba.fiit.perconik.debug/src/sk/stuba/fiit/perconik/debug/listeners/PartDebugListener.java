package sk.stuba.fiit.perconik.debug.listeners;

import org.eclipse.ui.IWorkbenchPart;
import sk.stuba.fiit.perconik.core.listeners.PartListener;
import sk.stuba.fiit.perconik.debug.Debug;
import sk.stuba.fiit.perconik.eclipse.core.runtime.PluginConsole;

public final class PartDebugListener extends AbstractDebugListener implements PartListener
{
	public PartDebugListener()
	{
	}
	
	public PartDebugListener(final PluginConsole console)
	{
		super(console);
	}
	
	public final void partOpened(final IWorkbenchPart part)
	{
		this.printHeader("Part opened");
		this.printPage(part);
	}

	public final void partClosed(final IWorkbenchPart part)
	{
		this.printHeader("Part closed");
		this.printPage(part);
	}

	public final void partActivated(final IWorkbenchPart part)
	{
		this.printHeader("Part activated");
		this.printPage(part);
	}

	public final void partDeactivated(final IWorkbenchPart part)
	{
		this.printHeader("Part deactivated");
		this.printPage(part);
	}

	public final void partBroughtToTop(final IWorkbenchPart part)
	{
		this.printHeader("Part brought to top");
		this.printPage(part);
	}
	
	private final void printPage(final IWorkbenchPart part)
	{
		this.put(Debug.dumpPart(part));
	}
}