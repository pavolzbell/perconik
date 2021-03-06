package sk.stuba.fiit.perconik.eclipse.core.commands;

import javax.annotation.Nullable;

import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.handlers.IHandlerService;

import sk.stuba.fiit.perconik.eclipse.ui.Workbenches;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.util.concurrent.Uninterruptibles.sleepUninterruptibly;

import static sk.stuba.fiit.perconik.eclipse.ui.Workbenches.waitForWorkbench;

/**
 * Static utility methods pertaining to Eclipse command handlers.
 *
 * @author Pavol Zbell
 * @since 1.0
 */
public final class Handlers {
  private Handlers() {}

  /**
   * Gets the handler service.
   * @return the handler service or {@code null} if
   *         the workbench has not been created yet
   */
  public static IHandlerService getHandlerService() {
    return getHandlerService(Workbenches.getWorkbench());
  }

  /**
   * Gets the handler service.
   * @param workbench the workbench, may be {@code null}
   * @return the handler service or {@code null} if the workbench
   *         is {@code null} or if the workbench has no handler service
   */
  public static IHandlerService getHandlerService(@Nullable final IWorkbench workbench) {
    if (workbench == null) {
      return null;
    }

    return (IHandlerService) workbench.getService(IHandlerService.class);
  }

  /**
   * Waits for the handler service.
   * This method blocks until there is an available handler service.
   * @see #getHandlerService()
   */
  public static IHandlerService waitForHandlerService() {
    return waitForHandlerService(waitForWorkbench());
  }

  /**
   * Waits for the handler service.
   * This method blocks until there is an available handler service.
   * @param workbench the workbench, can not be {@code null}
   * @see #getHandlerService(IWorkbench)
   */
  public static IHandlerService waitForHandlerService(final IWorkbench workbench) {
    checkNotNull(workbench);

    IHandlerService service;

    while ((service = getHandlerService(workbench)) == null) {
      sleepUninterruptibly(20, MILLISECONDS);
    }

    return service;
  }
}
