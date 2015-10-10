package com.github.thstock.streamseq;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Test;

import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;

public class SeqTest {

  @Test
  public void testGuava() {
    // GIVEN
    ImmutableList<String> in = ImmutableList.of("a");

    // WHEN
    ImmutableList<String> result = FluentIterable.from(in).transform(m -> m + "a").toList();

    // THEN
    ImmutableList<String> expected = ImmutableList.of("aa");
    assertEquals(expected, result);
  }

  @Test
  public void testJava() {
    // GIVEN
    List<String> in = new ArrayList<>();
    in.add("a");
    List<String> unmodifiableList = Collections.unmodifiableList(new ArrayList<>(in));
    in.add("b");

    // WHEN
    List<String> collect = Collections.unmodifiableList(unmodifiableList.stream()
        .map(m -> m + "a").collect(Collectors.toList()));

    // THEN
    List<String> exptected = new ArrayList<>();
    exptected.add("aa");
    assertEquals(exptected, collect);
  }

  @Test
  public void testMapString() {
    // GIVEN
    ImmutableList<String> in = ImmutableList.of("a");

    // WHEN
    ImmutableList<String> result = Seq.fluent(in).map(m -> m + "a").toList();

    // THEN
    ImmutableList<String> expected = ImmutableList.of("aa");
    assertEquals(expected, result);
  }

  @Test
  public void testToSet() {
    // GIVEN
    ImmutableList<String> in = ImmutableList.of("a", "a");

    // WHEN
    ImmutableSet<String> result = Seq.fluent(in).toSet();

    // THEN
    ImmutableSet<String> expected = ImmutableSet.of("a");
    assertEquals(expected, result);
  }

  @Test
  public void testMapBoolean() {
    // GIVEN
    ImmutableList<Boolean> in = ImmutableList.of(true, false);

    // WHEN
    ImmutableList<String> result = Seq.fluent(in)
        .filter(b -> b)
        .map(m -> m + " A").toList();

    // THEN
    ImmutableList<String> expected = ImmutableList.of("true A");
    assertEquals(expected, result);
  }

  @Test
  public void testDistinct() {
    // GIVEN
    Object o = new Object();
    ImmutableList<Object> in = ImmutableList.of(o, o, o);

    // WHEN
    ImmutableList<Object> original = Seq.fluent(in).toList();
    assertEquals(in, original);
    ImmutableList<Object> result = Seq.fluent(in).distict().toList();

    // THEN
    ImmutableList<Object> expected = ImmutableList.of(o);
    assertEquals(expected, result);
  }

  @Test
  public void testSorted() {
    // GIVEN
    ImmutableList<String> in = ImmutableList.of("b", "0", "a");

    // WHEN
    ImmutableList<String> result = Seq.fluent(in).sort(Ordering.natural()).toList();

    // THEN
    ImmutableList<Object> expected = ImmutableList.of("0", "a", "b");
    assertEquals(expected, result);
  }

  @Test
  public void testFlatMapString() {
    // GIVEN
    ImmutableList<String> in = ImmutableList.of("x", "y");

    // WHEN
    ImmutableList<String> result = Seq.fluent(in)
        .flatMap(m -> Stream.of(m.toUpperCase(), "a"))
        .toList();

    // THEN
    ImmutableList<String> expected = ImmutableList.of("X", "a", "Y", "a");
    assertEquals(expected, result);
  }

  @Test
  public void testSortedNull_left() {
    // GIVEN
    List<String> in = Lists.newArrayList("b", null);

    try {
      // WHEN
      Seq.fluent(in).sort(String::compareTo).toList();
      fail();
    } catch (NullPointerException e) {
      // THEN
      assertEquals("\"left\" must not be null", e.getMessage());
    }
  }

  @Test
  public void testSortedNull_right() {
    // GIVEN
    List<String> in = Lists.newArrayList(null, "right");

    try {
      // WHEN
      Seq.fluent(in).sort(String::compareTo).toList();
      fail();
    } catch (NullPointerException e) {
      // THEN
      assertEquals("\"right\" must not be null", e.getMessage());
    }
  }

  @Test
  public void testFlatMapNullIn() {
    // GIVEN
    List<String> in = Lists.newArrayList("b", null);

    try {
      // WHEN
      Seq.fluent(in).flatMap(m -> Stream.empty()).toList();
      fail();
    } catch (NullPointerException e) {
      // THEN
      assertEquals("\"in\" must not be null", e.getMessage());
    }
  }

  @Test
  public void testFlatMapNullStream() {
    // GIVEN
    List<String> in = Lists.newArrayList("b");

    try {
      // WHEN
      Seq.fluent(in).flatMap(m -> Seq.stream("", null)).toList();
      fail();
    } catch (NullPointerException e) {
      // THEN
      assertEquals("\"seqIn\" must not be null", e.getMessage());
    }
  }

  @Test
  public void testFlatMapNull() {
    // GIVEN
    ImmutableList<String> in = ImmutableList.of("b", "f");

    try {
      // WHEN
      Seq.fluent(in).flatMap(m -> null).toList();
      fail();
    } catch (NullPointerException e) {
      // THEN
      assertEquals("\"result\" must not be null", e.getMessage());
    }
  }

  @Test
  public void testMapNullIn() {
    // GIVEN
    List<String> in = Lists.newArrayList("b", null);

    try {
      // WHEN
      Seq.fluent(in).map(m -> m).toList();
      fail();
    } catch (NullPointerException e) {
      // THEN
      assertEquals("\"in\" must not be null", e.getMessage());
    }
  }

  @Test
  public void testMapNull() {
    // GIVEN
    ImmutableList<String> in = ImmutableList.of("b", "f");

    try {
      // WHEN
      Seq.fluent(in).map(m -> null).toList();
      fail();
    } catch (NullPointerException e) {
      // THEN
      assertEquals("\"result\" must not be null", e.getMessage());
    }
  }

  @Test
  public void testFilterNull() {
    // GIVEN
    List<String> in = Lists.newArrayList("a", null);

    // WHEN
    ImmutableList<String> result = Seq.fluent(in).filter(m -> m != null).toList();

    // THEN
    assertEquals(ImmutableList.of("a"), result);
  }

}
