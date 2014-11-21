package sk.stuba.fiit.perconik.activity.listeners.git;

import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;

import sk.stuba.fiit.perconik.activity.events.Event;
import sk.stuba.fiit.perconik.activity.events.LocalEvent;
import sk.stuba.fiit.perconik.activity.listeners.CommonEventListener;
import sk.stuba.fiit.perconik.activity.serializers.git.CommitSerializer;
import sk.stuba.fiit.perconik.activity.serializers.git.RepositorySerializer;
import sk.stuba.fiit.perconik.core.annotations.Version;

import static sk.stuba.fiit.perconik.activity.listeners.git.CommitListener.Action.COMMIT;
import static sk.stuba.fiit.perconik.data.content.StructuredContents.key;
import static sk.stuba.fiit.perconik.eclipse.jgit.lib.GitRepositories.getFullBranch;
import static sk.stuba.fiit.perconik.eclipse.jgit.lib.GitRepositories.getMostRecentCommit;

/**
 * TODO
 *
 * @author Pavol Zbell
 * @since 1.0
 */
@Version("0.0.0.alpha")
public final class CommitListener extends AbstractReferenceListener {
  private final RepositoryMapCache<String, String> cache;

  public CommitListener() {
    this.cache = new RepositoryMapCache<>();
  }

  @Override
  void postRegisterRepository(final Repository repository) {
    this.cache.update(repository, getFullBranch(repository), getMostRecentCommit(repository).getName());
  }

  enum Action implements CommonEventListener.Action {
    COMMIT;

    private final String name;

    private final String path;

    private Action() {
      this.name = actionName("eclipse", "git", this);
      this.path = actionPath(this.name);
    }

    public String getName() {
      return this.name;
    }

    public String getPath() {
      return this.path;
    }
  }

  static Event build(final long time, final Action action, final Repository repository, final RevCommit commit) {
    Event data = LocalEvent.of(time, action.getName());

    data.put(key("repository"), new RepositorySerializer().serialize(repository));
    data.put(key("commit"), new CommitSerializer().serialize(commit));

    return data;
  }

  @Override
  void process(final long time, final Repository repository) {
    RevCommit commit = getMostRecentCommit(repository);

    if (this.cache.updated(repository, getFullBranch(repository), commit.getName())) {
      this.send(COMMIT.getPath(), build(time, COMMIT, repository, commit));
    }
  }
}