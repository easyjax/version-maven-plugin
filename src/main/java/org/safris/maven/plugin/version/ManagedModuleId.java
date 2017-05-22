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

public class ManagedModuleId extends ManagedReference {
  private final ModuleId moduleId;

  public ManagedModuleId(final String groupId, final String artifactId, final Version version, final POMFile manager, final DependencyType dependencyType) {
    super(manager, dependencyType);
    this.moduleId = new ModuleId(groupId, artifactId, version);
  }

  public ManagedModuleId(final ModuleId moduleId) {
    this(moduleId.groupId(), moduleId.artifactId(), moduleId.version(), null, null);
  }

  public ModuleId moduleId() {
    return moduleId;
  }
}