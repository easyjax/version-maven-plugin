/* Copyright (c) 2016 Seva Safris
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

public class ManagedPOMFile extends ManagedReference {
  private final POMFile pomFile;

  public ManagedPOMFile(final POMFile manager, final DependencyType dependencyType, final POMFile pomFile) {
    super(manager, dependencyType);
    this.pomFile = pomFile;
  }

  public POMFile pomFile() {
    return pomFile;
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj)
      return true;

    if (!(obj instanceof ManagedPOMFile) || !super.equals(obj))
      return false;

    final ManagedPOMFile that = (ManagedPOMFile)obj;
    return pomFile != null ? pomFile.equals(that.pomFile) : that.pomFile == null;
  }

  @Override
  public int hashCode() {
    return super.hashCode() * (pomFile != null ? pomFile.hashCode() : -5);
  }
}