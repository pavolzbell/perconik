package sk.stuba.fiit.perconik.activity.listeners.ui.text;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.NotEnabledException;
import org.eclipse.core.commands.NotHandledException;
import org.eclipse.core.commands.common.NotDefinedException;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.IEditorPart;

import sk.stuba.fiit.perconik.activity.listeners.CommonEventListener;
import sk.stuba.fiit.perconik.core.annotations.Version;
import sk.stuba.fiit.perconik.core.listeners.CommandExecutionListener;
import sk.stuba.fiit.perconik.core.listeners.DocumentListener;
import sk.stuba.fiit.perconik.eclipse.core.commands.CommandExecutionStateHandler;
import sk.stuba.fiit.perconik.eclipse.jface.text.LineRegion;
import sk.stuba.fiit.perconik.eclipse.ui.Editors;

import static java.util.Objects.requireNonNull;

import static sk.stuba.fiit.perconik.activity.listeners.ui.text.TextPasteListener.Action.PASTE;
import static sk.stuba.fiit.perconik.eclipse.core.commands.CommandExecutionState.DISABLED;
import static sk.stuba.fiit.perconik.eclipse.core.commands.CommandExecutionState.EXECUTING;
import static sk.stuba.fiit.perconik.eclipse.core.commands.CommandExecutionState.FAILED;
import static sk.stuba.fiit.perconik.eclipse.core.commands.CommandExecutionState.SUCCEEDED;
import static sk.stuba.fiit.perconik.eclipse.core.commands.CommandExecutionState.UNDEFINED;
import static sk.stuba.fiit.perconik.eclipse.core.commands.CommandExecutionState.UNHANDLED;

/**
 * TODO
 *
 * @author Pavol Zbell
 * @since 1.0
 */
@Version("0.0.0.alpha")
public final class TextPasteListener extends AbstractTextOperationListener implements CommandExecutionListener, DocumentListener {
  private final CommandExecutionStateHandler paste;

  public TextPasteListener() {
    this.paste = CommandExecutionStateHandler.of(PASTE.getIdentifier());
  }

  enum Action implements CommonEventListener.Action {
    PASTE("org.eclipse.ui.edit.paste");

    private final String identifier;

    private final String name;

    private final String path;

    private Action(final String identifier) {
      this.identifier = requireNonNull(identifier);

      this.name = actionName("eclipse", "text", this);
      this.path = actionPath(this.name);
    }

    public String getIdentifier() {
      return this.identifier;
    }

    public String getName() {
      return this.name;
    }

    public String getPath() {
      return this.path;
    }
  }

  void process(final long time, final Action action, final DocumentEvent event) {
    IDocument document = event.getDocument();
    IEditorPart editor = Editors.forDocument(document);

    LineRegion region = LineRegion.of(document, event.getOffset(), event.getLength(), event.getText());

    if (editor == null) {
      if (Log.isEnabled()) {
        Log.message("paste: editor not found / documents not equal%n").appendTo(this.log);
      }

      return;
    }

    this.process(time, action, editor, document, region);
  }

  public void documentAboutToBeChanged(final DocumentEvent event) {
    // ignore
  }

  public void documentChanged(final DocumentEvent event) {
    final long time = currentTime();

    if (this.paste.getState() != EXECUTING) {
      if (Log.isEnabled()) {
        Log.message("paste: not EXECUTING but %s%n", this.paste.getState()).appendTo(this.log);
      }

      return;
    }

    this.execute(new Runnable() {
      public void run() {
        process(time, PASTE, event);
      }
    });
  }

  public void preExecute(final String identifier, final ExecutionEvent event) {
    this.paste.transitOnMatch(identifier, EXECUTING);
  }

  public void postExecuteSuccess(final String identifier, final Object result) {
    this.paste.transitOnMatch(identifier, SUCCEEDED);
  }

  public void postExecuteFailure(final String identifier, final ExecutionException exception) {
    this.paste.transitOnMatch(identifier, FAILED);
  }

  public void notDefined(final String identifier, final NotDefinedException exception) {
    this.paste.transitOnMatch(identifier, UNDEFINED);
  }

  public void notEnabled(final String identifier, final NotEnabledException exception) {
    this.paste.transitOnMatch(identifier, DISABLED);
  }

  public void notHandled(final String identifier, final NotHandledException exception) {
    this.paste.transitOnMatch(identifier, UNHANDLED);
  }
}