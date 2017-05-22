/* Copyright (c) 2016 lib4j
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * You should have received a copy of The MIT License (MIT) along with this
 * program. If not, see <http://opensource.org/licenses/MIT/>.
 */

package org.safris.maven.plugin.version;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.maven.plugin.MojoFailureException;

public class Version implements Comparable<Version> {
  public static enum Part {
    MAJOR, MINOR, PATCH
  }

  private static final Pattern pattern = Pattern.compile("^(?<prefix>[^\\d]*)(?<major>\\d*)(\\.(?<minor>\\d*)(\\.(?<patch>\\d*))?)?(?<suffix>[^\\d].*)?$");

  private final String prefix;
  private final Integer major;
  private final Integer minor;
  private final Integer patch;
  private final String suffix;

  public Version(final String version) {
    if (version == null)
      throw new NullPointerException("version == null");

    final Matcher matcher = pattern.matcher(version);
    matcher.find();
    this.prefix = matcher.group("prefix");
    final String major = matcher.group("major");
    final String minor = matcher.group("minor");
    final String patch = matcher.group("patch");
    this.major = major.length() > 0 ? Integer.parseInt(major) : null;
    this.minor = minor != null && minor.length() > 0 ? Integer.parseInt(minor) : null;
    this.patch = patch != null && patch.length() > 0 ? Integer.parseInt(patch) : null;
    this.suffix = matcher.group("suffix");
  }

  private Version(final String prefix, final int major, final int minor, final int patch, final String suffix) {
    this.prefix = prefix;
    this.major = major;
    this.minor = minor;
    this.patch = patch;
    this.suffix = suffix;
  }

  public String prefix() {
    return prefix;
  }

  public Integer major() {
    return major;
  }

  public Integer minor() {
    return minor;
  }

  public Integer patch() {
    return patch;
  }

  public String suffix() {
    return suffix;
  }

  public Version increment(final Part part) throws MojoFailureException {
    if (part == null)
      throw new NullPointerException("part == null");

    if (part == Part.MAJOR) {
      if (major() == null)
        throw new MojoFailureException("Version " + this + " does not have a major version component to increment.");

      return new Version(prefix, major + 1, minor, patch, suffix);
    }

    if (part == Part.MINOR) {
      if (minor == null)
        throw new MojoFailureException("Version " + this + " does not have a minor version component to increment.");

      return new Version(prefix, major, minor + 1, patch, suffix);
    }

    if (part == Part.PATCH) {
      if (patch == null)
        throw new MojoFailureException("Version " + this + " does not have a patch version component to increment.");

      return new Version(prefix, major, minor, patch + 1, suffix);
    }

    throw new UnsupportedOperationException("Unknown Part: " + part);
  }

  @Override
  public int compareTo(final Version o) {
    if (o == null)
      return 1;

    final int majorComp = major == null ? (o.major == null ? 0 : 1) : (major < o.major ? -1 : major == o.major ? 0 : 1);
    if (majorComp != 0)
      return majorComp;

    final int minorComp = minor == null ? (o.minor == null ? 0 : 1) : (minor < o.minor ? -1 : minor == o.minor ? 0 : 1);
    if (minorComp != 0)
      return minorComp;

    final int patchComp = patch == null ? (o.patch == null ? 0 : 1) : (patch < o.patch ? -1 : patch == o.patch ? 0 : 1);
    return patchComp;
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj)
      return true;

    if (!(obj instanceof Version))
      return false;

    final Version that = (Version)obj;
    return (prefix != null ? prefix.equals(that.prefix) : that.prefix == null) && (major == that.major) && (minor == that.minor) && (patch == that.patch) && (suffix != null ? suffix.equals(that.suffix) : that.suffix == null);
  }

  @Override
  public int hashCode() {
    return (prefix != null ? prefix.hashCode() : -7) * (major != null ? major : -3) * (minor != null ? minor : -5) * (patch != null ? patch : -21) * (suffix != null ? suffix.hashCode() : -11);
  }

  @Override
  public String toString() {
    final String numericPart = "" + (major != null ? "." + major : "") + (minor != null ? "." + minor : "") + (patch != null ? "." + patch : "");
    return (prefix != null ? prefix : "") + (numericPart.length() > 0 ? numericPart.substring(1) : "") + (suffix != null ? suffix : "");
  }
}