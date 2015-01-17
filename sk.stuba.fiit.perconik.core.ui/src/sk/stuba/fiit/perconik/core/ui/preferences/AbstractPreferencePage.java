package sk.stuba.fiit.perconik.core.ui.preferences;

import java.lang.annotation.Annotation;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Table;

import org.osgi.service.prefs.BackingStoreException;

import sk.stuba.fiit.perconik.core.annotations.Version;
import sk.stuba.fiit.perconik.core.persistence.AnnotableRegistration;
import sk.stuba.fiit.perconik.core.persistence.MarkableRegistration;
import sk.stuba.fiit.perconik.core.persistence.RegistrationMarker;
import sk.stuba.fiit.perconik.core.ui.plugin.Activator;
import sk.stuba.fiit.perconik.eclipse.swt.SortDirection;
import sk.stuba.fiit.perconik.eclipse.swt.widgets.WidgetListener;
import sk.stuba.fiit.perconik.ui.preferences.AbstractWorkbenchPreferencePage;
import sk.stuba.fiit.perconik.ui.utilities.Buttons;
import sk.stuba.fiit.perconik.ui.utilities.Tables;
import sk.stuba.fiit.perconik.ui.utilities.Widgets;
import sk.stuba.fiit.perconik.utilities.reflect.annotation.Annotations;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newLinkedHashSet;

import static org.eclipse.jface.dialogs.MessageDialog.openError;
import static org.eclipse.jface.dialogs.MessageDialog.openInformation;

import static sk.stuba.fiit.perconik.utilities.MoreStrings.toUpperCaseFirst;

/**
 * TODO
 *
 * @author Pavol Zbell
 * @since 1.0
 */
abstract class AbstractPreferencePage<P, R extends AnnotableRegistration & MarkableRegistration & RegistrationMarker<R>> extends AbstractWorkbenchPreferencePage {
  private P preferences;

  Set<R> registrations;

  CheckboxTableViewer tableViewer;

  AbstractOptionsDialog<P, R> optionsDialog;

  Button addButton;

  Button removeButton;

  Button registerButton;

  Button unregisterButton;

  Button importButton;

  Button exportButton;

  Button refreshButton;

  Button optionsButton;

  Button notesButton;

  AbstractPreferencePage() {}

  abstract String name();

  private static String pluralize(final String s) {
    return s + "s";
  }

  abstract Class<R> type();

  final R cast(final Object o) {
    return this.type().cast(o);
  }

  @Override
  protected final Control createContents(final Composite parent) {
    Composite composite = new Composite(parent, SWT.NONE);

    GridLayout parentLayout = new GridLayout();
    parentLayout.numColumns = 2;
    parentLayout.marginHeight = 0;
    parentLayout.marginWidth = 0;
    composite.setLayout(parentLayout);
    composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

    Composite innerParent = new Composite(composite, SWT.NONE);

    GridLayout innerLayout = new GridLayout();
    innerLayout.numColumns = 2;
    innerLayout.marginHeight = 0;
    innerLayout.marginWidth = 0;
    innerParent.setLayout(innerLayout);

    GridData innerGrid = new GridData(GridData.FILL_BOTH);
    innerGrid.horizontalSpan = 2;
    innerParent.setLayoutData(innerGrid);

    Composite tableComposite = new Composite(innerParent, SWT.NONE);
    TableColumnLayout tableLayout = new TableColumnLayout();

    GridData tableGrid = new GridData(GridData.FILL_BOTH);
    tableGrid.widthHint = 360;
    tableGrid.heightHint = this.convertHeightInCharsToPixels(10);
    tableComposite.setLayout(tableLayout);
    tableComposite.setLayoutData(tableGrid);

    Table table = Tables.create(tableComposite, SWT.CHECK | SWT.BORDER | SWT.MULTI | SWT.FULL_SELECTION | SWT.H_SCROLL | SWT.V_SCROLL);

    GC gc = new GC(this.getShell());
    gc.setFont(JFaceResources.getDialogFont());

    this.createTableColumns(table, tableLayout, gc);

    gc.dispose();

    this.tableViewer = new CheckboxTableViewer(table);

    this.tableViewer.setContentProvider(new CollectionContentProvider());
    this.tableViewer.setLabelProvider(this.createContentProvider());

    this.tableViewer.addSelectionChangedListener(new ISelectionChangedListener() {
      public void selectionChanged(final SelectionChangedEvent e) {
        updateButtons();
      }
    });

    this.tableViewer.addCheckStateListener(new ICheckStateListener() {
      public void checkStateChanged(final CheckStateChangedEvent e) {
        @SuppressWarnings("unchecked")
        R data = (R) e.getElement();

        if (data.isProvided()) {
          updateChosenData(data, e.getChecked());
          updateButtons();
        } else {
          e.getCheckable().setChecked(data, data.hasRegistredMark());
        }
      }
    });

    Composite buttons = new Composite(innerParent, SWT.NONE);

    buttons.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));
    parentLayout = new GridLayout();
    parentLayout.marginHeight = 0;
    parentLayout.marginWidth = 0;
    buttons.setLayout(parentLayout);

    this.addButton = Buttons.create(buttons, "Add", new WidgetListener() {
      public void handleEvent(final Event e) {
        performAdd();
      }
    });

    this.removeButton = Buttons.create(buttons, "Remove", new WidgetListener() {
      public void handleEvent(final Event e) {
        performRemove();
      }
    });

    Widgets.createButtonSeparator(buttons);

    this.registerButton = Buttons.create(buttons, "Register", new WidgetListener() {
      public void handleEvent(final Event e) {
        performRegister();
      }
    });

    this.unregisterButton = Buttons.create(buttons, "Unregister", new WidgetListener() {
      public void handleEvent(final Event e) {
        performUnregister();
      }
    });

    Widgets.createButtonSeparator(buttons);

    this.importButton = Buttons.create(buttons, "Import", new WidgetListener() {
      public void handleEvent(final Event e) {
        performImport();
      }
    });

    this.exportButton = Buttons.create(buttons, "Export", new WidgetListener() {
      public void handleEvent(final Event e) {
        performExport();
      }
    });

    Widgets.createButtonSeparator(buttons);

    this.refreshButton = Buttons.create(buttons, "Refresh", new WidgetListener() {
      public void handleEvent(final Event e) {
        performRefresh();
      }
    });

    this.optionsButton = Buttons.create(buttons, "Options", new WidgetListener() {
      public void handleEvent(final Event e) {
        performOptions();
      }
    });

    this.notesButton = Buttons.create(buttons, "Notes", new WidgetListener() {
      public void handleEvent(final Event e) {
        performNotes();
      }
    });

    this.optionsDialog = this.createOptionsDialog();

    this.loadInternal(this.sharedPreferences());
    this.performRefresh();

    Dialog.applyDialogFont(composite);

    innerParent.layout();

    TableSorter.attachedSort(table.getColumn(0), SortDirection.UP);
    table.setSortColumn(null);

    return composite;
  }

  protected abstract AbstractLabelProvider<R> createContentProvider();

  protected abstract AbstractOptionsDialog<P, R> createOptionsDialog();

  protected abstract SortingViewerComparator createViewerComparator();

  protected abstract void createTableColumns(final Table table, final TableColumnLayout layout, final GC gc);

  final Set<R> checkedData() {
    return Sets.filter(this.registrations, new Predicate<R>() {
      public boolean apply(@Nonnull final R registration) {
        return registration.hasRegistredMark();
      }
    });
  }

  final Set<R> unknownData() {
    return Sets.filter(this.registrations, new Predicate<R>() {
      public boolean apply(@Nonnull final R registration) {
        return !registration.isProvided();
      }
    });
  }

  final void updateChosenData(final R registration, final boolean status) {
    List<R> registrations = newArrayList(this.registrations);

    this.updateData(registrations, registration, status);

    this.registrations = newLinkedHashSet(registrations);

    this.tableViewer.refresh();
  }

  final void updateSelectedData(final boolean status) {
    IStructuredSelection selection = (IStructuredSelection) this.tableViewer.getSelection();

    List<R> registrations = newArrayList(this.registrations);

    for (Object item: selection.toList()) {
      R registration = this.cast(item);

      if (registration.isProvided()) {
        this.updateData(registrations, registration, status);
      }
    }

    this.registrations = newLinkedHashSet(registrations);

    this.tableViewer.refresh();
  }

  private void updateData(final List<R> registrations, final R registration, final boolean status) {
    registrations.set(registrations.indexOf(registration), registration.markRegistered(status));

    this.tableViewer.setChecked(registration, status);
  }

  final void updateTable() {
    this.tableViewer.setInput(this.registrations);
    this.tableViewer.refresh();
    this.tableViewer.setAllChecked(false);
    this.tableViewer.setCheckedElements(this.checkedData().toArray());
    this.tableViewer.setGrayedElements(this.unknownData().toArray());
  }

  final void updateButtons() {
    IStructuredSelection selection = (IStructuredSelection) this.tableViewer.getSelection();

    int selectionCount = selection.size();
    int itemCount = this.tableViewer.getTable().getItemCount();

    boolean registrable = false;
    boolean unregistrable = false;

    if (selectionCount > 0) {
      for (Object item: selection.toList()) {
        R registration = this.cast(item);

        if (registration.isProvided()) {
          boolean registred = registration.hasRegistredMark();

          registrable |= !registred;
          unregistrable |= registred;
        }
      }
    }

    this.removeButton.setEnabled(selectionCount > 0 && selectionCount <= itemCount);

    this.registerButton.setEnabled(registrable);
    this.unregisterButton.setEnabled(unregistrable);

    this.exportButton.setEnabled(selectionCount > 0);

    this.optionsButton.setEnabled(selectionCount == 1);
    this.notesButton.setEnabled(selectionCount == 1);
  }

  class LocalSetTableSorter extends SetTableSorter<R> {
    LocalSetTableSorter(final Table table, @Nullable final Comparator<? super R> comparator) {
      super(table, comparator);
    }

    @Override
    final Set<R> loadSet() {
      return AbstractPreferencePage.this.registrations;
    }

    @Override
    final void updateSet(final Set<R> set) {
      AbstractPreferencePage.this.registrations = set;

      updateTable();
    }
  }

  static abstract class AbstractLabelProvider<R extends AnnotableRegistration & MarkableRegistration & RegistrationMarker<R>> extends LabelProvider implements ITableLabelProvider {
    AbstractLabelProvider() {}

    public final String getNotes(final R registration) {
      if (!registration.isProvided()) {
        return "?";
      }

      return Annotations.toString(NoteFilter.apply(registration.getAnnotations()));
    }

    public final String getVersion(final R registration) {
      Version version = registration.getAnnotation(Version.class);

      return version != null ? version.value() : "?";
    }

    public Image getColumnImage(final Object element, final int column) {
      return null;
    }
  }

  private enum NoteFilter implements Predicate<Annotation> {
    INSTANCE;

    public static Iterable<Annotation> apply(final Iterable<Annotation> annotations) {
      return Iterables.filter(annotations, INSTANCE);
    }

    public boolean apply(@Nonnull final Annotation annotation) {
      return annotation.annotationType() != Version.class;
    }
  }

  void performAdd() {
    openInformation(this.getShell(), "Add " + toUpperCaseFirst(this.name()), "Operation not yet supported.");
  }

  void performRemove() {
    openInformation(this.getShell(), "Remove" + toUpperCaseFirst(this.name()), "Operation not yet supported.");
  }

  void performRegister() {
    this.updateSelectedData(true);
    this.updateButtons();
  }

  void performUnregister() {
    this.updateSelectedData(false);
    this.updateButtons();
  }

  void performImport() {
    openInformation(this.getShell(), "Import " + toUpperCaseFirst(this.name()), "Operation not yet supported.");
  }

  void performExport() {
    openInformation(this.getShell(), "Export " + toUpperCaseFirst(this.name()), "Operation not yet supported.");
  }

  void performRefresh() {
    for (R registration: newLinkedHashSet(this.registrations)) {
      this.updateChosenData(registration, registration.isRegistered());
    }
  }

  void performOptions() {
    IStructuredSelection selection = (IStructuredSelection) this.tableViewer.getSelection();

    R registration = this.cast(selection.toList().get(0));

    AbstractOptionsDialog<P, R> dialog = this.optionsDialog;

    dialog.setTitle("Options for " + ((ITableLabelProvider) this.tableViewer.getLabelProvider()).getColumnText(registration, 0));
    dialog.setPreferences(this.getPreferences());
    dialog.setRegistration(registration);

    dialog.open();

    if (dialog.getReturnCode() == Window.OK) {
      dialog.configure();
    }
  }

  void performNotes() {
    IStructuredSelection selection = (IStructuredSelection) this.tableViewer.getSelection();

    R registration = this.cast(selection.toList().get(0));

    String name = ((ITableLabelProvider) this.tableViewer.getLabelProvider()).getColumnText(registration, 0);
    String message = Annotations.toString(NoteFilter.apply(registration.getAnnotations()));

    openInformation(this.getShell(), "Notes for " + name, !message.isEmpty() ? message : "No notes available.");
  }

  abstract Set<R> defaultRegistrations();

  abstract P sharedPreferences();

  @Override
  public final boolean performOk() {
    this.applyInternal();
    this.saveInternal();

    return super.performOk();
  }

  @Override
  public final boolean performCancel() {
    this.loadInternal(this.sharedPreferences());

    return super.performCancel();
  }

  @Override
  protected final void performDefaults() {
    String name = pluralize(this.name());
    String title = format("Restore %s Defaults", toUpperCaseFirst(name));
    String message = format("PerConIK Core is about to restore default state of %s registrations. Configured options are not restored, see options dialog to restore effective options.", this.name(), name);

    if (new MessageDialog(this.getShell(), title, null, message, WARNING, new String[] { "Continue", "Cancel" }, 1).open() == 1) {
      return;
    }

    this.registrations = this.defaultRegistrations();

    this.updateTable();
    this.updateButtons();

    super.performDefaults();
  }

  @Override
  protected final void performApply() {
    this.performOk();
  }

  abstract void apply();

  abstract void load(P preferences);

  abstract void save() throws BackingStoreException;

  private void applyInternal() {
    this.apply();

    this.updateTable();
    this.updateButtons();
  }

  private void loadInternal(final P preferences) {
    this.load(preferences);

    this.updateTable();
    this.updateButtons();
  }

  private void saveInternal() {
    try {
      this.save();
    } catch (BackingStoreException failure) {
      String title = "Preferences";
      String message = "Failed to save preferences.";

      openError(this.getShell(), title, message + " See error log for more details.");

      Activator.defaultInstance().getConsole().error(failure, message);
    }
  }

  final void setPreferences(final P preferences) {
    this.preferences = requireNonNull(preferences);
  }

  final P getPreferences() {
    return this.preferences;
  }
}
