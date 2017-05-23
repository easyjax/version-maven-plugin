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

package org.libx4j.maven.plugin.version;

public class ModuleId {
  protected static boolean equal(final ModuleId a, final ModuleId b) {
    return (a.groupId() != null ? a.groupId().equals(b.groupId()) : b.groupId() == null) && (a.artifactId() != null ? a.artifactId().equals(b.artifactId()) : b.artifactId() == null) && (a.version() != null ? a.version().equals(b.version()) : b.version() == null);
  }

  protected static String toString(final ModuleId moduleId) {
    return moduleId.groupId() + ":" + moduleId.artifactId() + ":" + moduleId.version();
  }

  private final String groupId;
  private final String artifactId;
  private final Version version;

  public ModuleId(final String xmlTag) {
    int start = xmlTag.indexOf("<groupId>");
    int end = xmlTag.indexOf("</groupId>");
    final String groupId = start != -1 && end != -1 ? xmlTag.substring(start + 9, end).trim() : null;

    start = xmlTag.indexOf("<artifactId>");
    end = xmlTag.indexOf("</artifactId>");
    final String artifactId = start != -1 && end != -1 ? xmlTag.substring(start + 12, end).trim() : null;

    start = xmlTag.indexOf("<version>");
    end = xmlTag.indexOf("</version>");
    final String version = start != -1 && end != -1 ? xmlTag.substring(start + 9, end).trim() : null;

    this.groupId = groupId;
    this.artifactId = artifactId;
    this.version = version != null ? new Version(version) : null;
  }

  public ModuleId(final String groupId, final String artifactId, final Version version) {
    this.groupId = groupId;
    this.artifactId = artifactId;
    this.version = version;
  }

  public ModuleId(final ModuleId copy) {
    this(copy.groupId(), copy.artifactId(), copy.version());
  }

  public String groupId() {
    return groupId;
  }

  public String artifactId() {
    return artifactId;
  }

  public Version version() {
    return version;
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj)
      return true;

    if (!(obj instanceof ModuleId))
      return false;

    return equal(this, (ModuleId)obj);
  }

  @Override
  public int hashCode() {
    int hashCode = 1;
    hashCode *= groupId != null ? groupId.hashCode() : -1;
    hashCode *= artifactId != null ? artifactId.hashCode() : -3;
    hashCode *= version != null ? version.hashCode() : -7;
    return hashCode;
  }

  @Override
  public String toString() {
    return toString(this);
  }
}