package sk.stuba.fiit.perconik.core.resources;

import org.eclipse.swt.widgets.Display;

import sk.stuba.fiit.perconik.core.listeners.CommandExecutionListener;
import sk.stuba.fiit.perconik.eclipse.core.commands.Commands;

enum CommandExecutionHandler implements Handler<CommandExecutionListener> {
  INSTANCE;

  public void register(final CommandExecutionListener listener) {
    final Runnable addListener = new Runnable() {
      public void run() {
        Commands.waitForCommandService().addExecutionListener(listener);
      }
    };

    Display.getDefault().asyncExec(addListener);
  }

  public void unregister(final CommandExecutionListener listener) {
    final Runnable removeListener = new Runnable() {
      public void run() {
        Commands.waitForCommandService().removeExecutionListener(listener);
      }
    };

    Display.getDefault().asyncExec(removeListener);
  }
}
