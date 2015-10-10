package com.github.thstock.streamseq;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import javax.annotation.Nonnull;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

public class Seq<E> {
  private final Stream<E> stream;

  public Seq(Stream<E> stream) {
    this.stream = Preconditions.checkNotNull(stream);
  }

  public static <E> Seq<E> fluent(Iterable<E> iterable) {
    return fluent(StreamSupport.stream(checked(iterable, "iterable").spliterator(), false));
  }

  private static <E> Seq<E> fluent(Stream<E> in) {
    return new Seq<>(in.parallel());
  }

  @Nonnull
  static <E> E checked(E in, Object msg) {
    return Preconditions.checkNotNull(in, "\"" + msg + "\" must not be null");
  }

  @SafeVarargs
  public static <T> Stream<T> stream(T... o) {
    return Stream.of(o).map(seqIn -> checked(seqIn, "seqIn"));
  }

  private <T> NonnullPr<T> nonNull(Predicate<T> predicate) {
    checked(predicate, "predicate");
    if (predicate instanceof NonnullPr) {
      return (NonnullPr<T>) predicate;
    } else {
      return predicate::test;
    }
  }

  private <T> NonnnullCmp<T> nonNull(Comparator<T> comparator) {
    checked(comparator, "comparator");
    if (comparator instanceof NonnnullCmp) {
      return (NonnnullCmp<T>) comparator;
    } else {
      return (left, right) -> {
        T nonNullLeft = checked(left, "left");
        T nonNullRight = checked(right, "right");
        return comparator.compare(nonNullLeft, nonNullRight);
      };
    }
  }

  private <T> NonnnullFn<E, T> nonNull(Function<E, T> function) {
    checked(function, "function");
    if (function instanceof NonnnullFn) {
      return (NonnnullFn<E, T>) function;
    } else {
      return in -> {
        E nonNullIn = checked(in, "in");
        T result = function.apply(nonNullIn);
        return checked(result, "result");
      };
    }
  }

  public final <T> Seq<T> map(Function<E, T> function) {
    return fluent(stream().map(nonNull(function)));
  }

  public <T> Seq<T> flatMap(Function<E, Stream<T>> function) {
    return fluent(stream().flatMap(nonNull(function)));
  }

  public final Seq<E> filter(Predicate<? super E> predicate) {
    return fluent(stream().filter(nonNull(predicate)));
  }

  public ImmutableSet<E> toSet() {
    Set<E> collect = stream().collect(Collectors.toSet());
    return ImmutableSet.copyOf(collect);
  }

  public ImmutableList<E> toList() {
    List<E> collect = stream().collect(Collectors.toList());
    return ImmutableList.copyOf(collect);
  }

  public Seq<E> distict() {
    return fluent(stream().distinct());
  }

  public Seq<E> sort(Comparator<E> ordering) {
    return fluent(stream().sorted(nonNull(ordering)));
  }

  public Stream<E> stream() {
    return stream;
  }

  public interface NonnnullCmp<T> extends Comparator<T> {

    @Override
    int compare(@Nonnull T o1, @Nonnull T o2);
  }

  @FunctionalInterface
  public interface NonnnullFn<F, T> extends Function<F, T> {

    @Override
    @Nonnull
    T apply(@Nonnull F f);
  }

  public interface NonnullPr<T> extends Predicate<T> {

    @Override
    boolean test(@Nonnull T t);
  }
}
