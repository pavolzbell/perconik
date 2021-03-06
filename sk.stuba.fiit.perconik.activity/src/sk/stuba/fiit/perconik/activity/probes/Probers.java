package sk.stuba.fiit.perconik.activity.probes;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;

import com.google.common.base.MoreObjects;
import com.google.common.base.MoreObjects.ToStringHelper;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableMap;

import sk.stuba.fiit.perconik.data.content.AnyContent;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.ImmutableMap.copyOf;
import static com.google.common.collect.Lists.newLinkedList;
import static com.google.common.collect.Maps.filterEntries;
import static com.google.common.util.concurrent.Uninterruptibles.awaitUninterruptibly;

import static sk.stuba.fiit.perconik.utilities.MoreThrowables.initializeSuppressor;

public final class Probers {
  private Probers() {}

  private static final class RegularProber<T extends AnyContent, P extends Probe<?>> extends AbstractProber<T, P> {
    final ImmutableMap<String, P> probes;

    RegularProber(final Map<String, P> probes) {
      this.probes = copyOf(probes);
    }

    public final Map<String, P> probes() {
      return this.probes;
    }
  }

  private static final class FilteringProber<T extends AnyContent, P extends Probe<?>> extends AbstractProber<T, P> {
    final ImmutableMap<String, P> probes;

    final Predicate<? super Entry<String, P>> predicate;

    FilteringProber(final Map<String, P> probes, final Predicate<? super Entry<String, P>> predicate) {
      this.probes = copyOf(probes);
      this.predicate = checkNotNull(predicate);
    }

    public final Map<String, P> probes() {
      return filterEntries(this.probes, this.predicate);
    }

    @Override
    protected ToStringHelper toStringHelper() {
      ToStringHelper helper = super.toStringHelper();

      helper.add("predicate", this.predicate);

      return helper;
    }
  }

  private static abstract class ProberProxy<T extends AnyContent, P extends Probe<?>> extends AbstractProber<T, P> {
    final Prober<T, P> prober;

    public ProberProxy(final Prober<T, P> prober) {
      this.prober = checkNotNull(prober);
    }

    public final Map<String, P> probes() {
      return this.prober.probes();
    }

    @Override
    protected ToStringHelper toStringHelper() {
      ToStringHelper helper = MoreObjects.toStringHelper(this);

      helper.add("proxy", this.prober);

      return helper;
    }
  }

  private static final class SameThreadProber<T extends AnyContent, P extends Probe<?>> extends ProberProxy<T, P> {
    SameThreadProber(final Prober<T, P> prober) {
      super(prober);
    }
  }

  private static final class ConcurrentProber<T extends AnyContent, P extends Probe<?>> extends ProberProxy<T, P> {
    final Executor executor;

    ConcurrentProber(final Prober<T, P> prober, final Executor executor) {
      super(prober);

      this.executor = checkNotNull(executor);
    }

    @Override
    public void inject(final T content) {
      final Map<String, P> probes = copyOf(this.prober.probes());

      final CountDownLatch latch = new CountDownLatch(probes.size());

      final List<RuntimeException> failures = newLinkedList();

      for (final Entry<String, P> entry: probes.entrySet()) {
        this.executor.execute(new Runnable() {
          public void run() {
            try {
              probeAndPut(entry.getValue(), entry.getKey(), content);
            } catch (ProbingException failure) {
              failures.add(failure);
            } finally {
              latch.countDown();
            }
          }
        });
      }

      awaitUninterruptibly(latch);

      if (!failures.isEmpty()) {
        throw initializeSuppressor(new ProbingException(), failures);
      }
    }

    @Override
    protected ToStringHelper toStringHelper() {
      ToStringHelper helper = super.toStringHelper();

      helper.add("executor", this.executor);

      return helper;
    }
  }

  public static <T extends AnyContent, P extends Probe<?>> Prober<T, P> create(final Map<String, P> probes) {
    return new SameThreadProber<>(new RegularProber<T, P>(probes));
  }

  public static <T extends AnyContent, P extends Probe<?>> Prober<T, P> create(final Map<String, P> probes, final Executor executor) {
    return new ConcurrentProber<>(new RegularProber<T, P>(probes), executor);
  }

  public static <T extends AnyContent, P extends Probe<?>> Prober<T, P> create(final Map<String, P> probes, final Predicate<? super Entry<String, P>> predicate) {
    return new SameThreadProber<>(new FilteringProber<T, P>(probes, predicate));
  }

  public static <T extends AnyContent, P extends Probe<?>> Prober<T, P> create(final Map<String, P> probes, final Predicate<? super Entry<String, P>> predicate, final Executor executor) {
    return new ConcurrentProber<>(new FilteringProber<T, P>(probes, predicate), executor);
  }
}
