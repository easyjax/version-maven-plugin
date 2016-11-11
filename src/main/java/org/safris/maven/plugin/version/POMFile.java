/* Copyright (c) 2015 Seva Safris
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

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.maven.plugin.MojoFailureException;
import org.safris.commons.lang.Pair;
import org.safris.commons.lang.Paths;

public class POMFile extends ModuleId {
  private static final Map<POMFile,POMFile> pomFiles = new HashMap<POMFile,POMFile>();

  public static Set<POMFile> getPendingUpdates() {
    final Set<POMFile> updates = new LinkedHashSet<POMFile>();
    for (final POMFile pomFile : pomFiles.values())
      if (pomFile.newVersion() != null)
        updates.add(pomFile);

    return updates;
  }

  public static POMFile entry(final File file) throws IOException, MojoFailureException {
    final String text = new String(Files.readAllBytes(file.toPath()));
    final POMFile pomFile = parse(file, text, "project");

    // Drill down to the root parent, and then resolve the dependency tree from there
    POMFile parentPomFile;
    for (parentPomFile = pomFile; parentPomFile.parent() != null; parentPomFile = parentPomFile.parent());

    // Resolve all modules of all sub-modules
    POMFile.resolveModules(parentPomFile.modules());

    // Resolve all relations of all sub-modules
    parentPomFile.resolveRelations();
    POMFile.resolveRelations(parentPomFile.modules());

    return pomFile;
  }

  protected static POMFile parse(final File file, final String text) throws IOException, MojoFailureException {
    return parse(file, text, "project");
  }

  public static POMFile lookup(final ModuleId moduleId) {
    return pomFiles.get(moduleId);
  }

  private static POMFile makeFromParent(final File dir, final String text) throws IOException, MojoFailureException {
    final ModuleId parentId = parseModuleId(text, "project/parent");
    if (parentId == null)
      return null;

    final int[][] relativePathIndex = VersionUtil.indexOfTag(text, "project/parent/relativePath");
    final File file = new File(dir, relativePathIndex != null ? text.substring(relativePathIndex[0][0], relativePathIndex[0][1]).trim() : "../pom.xml");
    final POMFile pomFile = parse(file, new String(Files.readAllBytes(file.toPath())), "project");
    if (!ModuleId.equal(parentId, pomFile))
      throw new MojoFailureException("Version of parent pom is expected to be " + parentId + ", but was found to be " + ModuleId.toString(pomFile));

    return pomFile;
  }

  private static ModuleId parseModuleId(final String text, final String scope) {
    final int[][] artifactIdIndex = VersionUtil.indexOfTag(text, scope + "/artifactId");
    if (artifactIdIndex == null)
      return null;

    final String artifactId = text.substring(artifactIdIndex[0][0], artifactIdIndex[0][1]).trim();

    final int[][] groupIdIndex = VersionUtil.indexOfTag(text, scope + "/groupId");
    final String groupId = groupIdIndex == null ? null : text.substring(groupIdIndex[0][0], groupIdIndex[0][1]).trim();

    final int[][] versionIndex = VersionUtil.indexOfTag(text, scope + "/version");
    final Version version = versionIndex == null ? null : new Version(text.substring(versionIndex[0][0], versionIndex[0][1]).trim());
    return new ModuleId(groupId, artifactId, version);
  }

  private static POMFile parse(final File file, final String text, final String scope) throws IOException, MojoFailureException {
    final POMFile prototype = new POMFile(file, text, scope);
    POMFile instance = pomFiles.get(prototype);
    if (instance != null)
      return instance;

    synchronized (pomFiles) {
      instance = pomFiles.get(prototype);
      if (instance != null)
        return instance;

      pomFiles.put(prototype, prototype);
      return prototype;
    }
  }

  private static POMFile getParent(final File file, final String text) throws IOException, MojoFailureException {
    return POMFile.makeFromParent(file.getParentFile(), text);
  }

  private final File file;
  private final String text;
  private String newText;
  private boolean parentPOMFileInited = false;
  private POMFile parentPOMFile;

  private final POMFile groupDeclarator;
  private final POMFile versionDeclarator;

  private POMFile(final File file, final String text, final String scope) throws IOException, MojoFailureException {
    super(parseModuleId(text, scope));
    this.file = file;
    this.text = text;
    this.newText = text;

    groupDeclarator = groupId() != null ? this : parent() != null ? parent().groupDeclarator : null;
    versionDeclarator = version() != null ? this : parent() != null ? parent().versionDeclarator : null;
  }

  @Override
  public String groupId() {
    return groupDeclarator != this && groupDeclarator != null ? groupDeclarator.groupId() : super.groupId();
  }

  @Override
  public Version version() {
    return versionDeclarator != this && versionDeclarator != null ? versionDeclarator.version() : super.version();
  }

  public File file() {
    return file;
  }

  private void parent(final POMFile parentPOMFile) {
    this.parentPOMFile = parentPOMFile;
    parentPOMFileInited = true;
  }

  public POMFile parent() throws IOException, MojoFailureException {
    if (parentPOMFileInited)
      return parentPOMFile;

    parentPOMFileInited = true;
    return parentPOMFile = POMFile.getParent(file, text);
  }

  private POMFile[] modules = null;

  public POMFile[] modules() throws IOException, MojoFailureException {
    if (modules != null)
      return modules;

    synchronized (file) {
      if (modules != null)
        return modules;

      final int[][] moduleIndices = VersionUtil.indexOfTag(newText, "project/modules/module", "project/profiles/profile/modules/module");
      if (moduleIndices == null)
        return this.modules = new POMFile[0];

      final POMFile[] modules = new POMFile[moduleIndices.length];
      for (int i = 0; i < moduleIndices.length; i++) {
        final int[] moduleIndex = moduleIndices[i];
        final String moduleName = newText.substring(moduleIndex[0], moduleIndex[1]).trim();
        final File pomFile = new File(file.getParent(), moduleName + "/pom.xml");
        modules[i] = POMFile.parse(pomFile, new String(Files.readAllBytes(pomFile.toPath())));
        modules[i].parent(this);
      }

      return this.modules = modules;
    }
  }

  private static void resolveModules(final POMFile[] modules) throws IOException, MojoFailureException {
    for (final POMFile module : modules)
      resolveModules(module.modules());
  }

  private static void resolveRelations(final POMFile[] modules) throws IOException, MojoFailureException {
    for (final POMFile module : modules) {
      module.resolveRelations();
      resolveRelations(module.modules());
    }
  }

  private ManagedModuleId getManagedVersion(final ModuleId moduleId, final boolean dependency) throws IOException, MojoFailureException {
    if (moduleId.version() != null)
      return new ManagedModuleId(moduleId);

    final POMFile pomFile = (dependency ? managedDependencies : managedPlugins).get(moduleId);
    if (pomFile != null)
      return new ManagedModuleId(pomFile.groupId(), pomFile.artifactId(), pomFile.version(), pomFile, dependency ? DependencyType.MANAGED_DEPENDENCY : DependencyType.MANAGED_PLUGIN);

    if (parent() == null)
      return null;

    return parent().getManagedVersion(moduleId, dependency);
  }

  private volatile Boolean relationsResolved = false;
  private final Set<ManagedPOMFile> dependents = new LinkedHashSet<ManagedPOMFile>();
  private final Set<ManagedPOMFile> dependencies = new LinkedHashSet<ManagedPOMFile>();;
  private final Map<ModuleId,POMFile> managedDependencies = new LinkedHashMap<ModuleId,POMFile>();;
  private final Set<ManagedPOMFile> plugins = new LinkedHashSet<ManagedPOMFile>();;
  private final Map<ModuleId,POMFile> managedPlugins = new LinkedHashMap<ModuleId,POMFile>();;
  private final Set<Pair<ManagedPOMFile,DependencyType>> allDependencies = new LinkedHashSet<Pair<ManagedPOMFile,DependencyType>>();;

  public Set<ManagedPOMFile> dependents() {
    return dependents;
  }

  private List<ManagedPOMFile> parseModuleIds(final DependencyType dependencyType) throws IOException, MojoFailureException {
    final int[][] indices = VersionUtil.indexOfTag(text, dependencyType.scope());
    final List<ManagedPOMFile> moduleIds = new ArrayList<ManagedPOMFile>();
    if (indices != null) {
      for (final int[] index : indices) {
        final ManagedModuleId managedModuleId = getManagedVersion(new ModuleId(text.substring(index[0], index[1]).trim()), dependencyType == DependencyType.DEPENDENCY || dependencyType == DependencyType.MANAGED_DEPENDENCY);
        if (managedModuleId != null) {
          final POMFile pomFile = pomFiles.get(managedModuleId.moduleId());
          if (pomFile != null) {
            moduleIds.add(new ManagedPOMFile(managedModuleId.manager(), dependencyType, pomFile));
            pomFile.dependents.add(new ManagedPOMFile(managedModuleId.manager(), dependencyType, this));
          }
        }
      }
    }

    return moduleIds;
  }

  private void resolveRelations() throws IOException, MojoFailureException {
    if (relationsResolved)
      return;

    synchronized (relationsResolved) {
      if (relationsResolved)
        return;

      final POMFile parentPOMFile = parent();
      if (parentPOMFile != null)
        dependents.add(new ManagedPOMFile(parentPOMFile.versionDeclarator, DependencyType.POM, parentPOMFile));

      for (final ManagedPOMFile managedDependency : parseModuleIds(DependencyType.MANAGED_DEPENDENCY)) {
        managedDependencies.put(new ModuleId(managedDependency.pomFile().groupId(), managedDependency.pomFile().artifactId(), null), managedDependency.pomFile());
        allDependencies.add(new Pair<ManagedPOMFile,DependencyType>(managedDependency, DependencyType.MANAGED_DEPENDENCY));
      }

      dependencies.addAll(parseModuleIds(DependencyType.DEPENDENCY));
      for (final ManagedPOMFile dependency : dependencies)
        allDependencies.add(new Pair<ManagedPOMFile,DependencyType>(dependency, DependencyType.DEPENDENCY));

      for (final ManagedPOMFile managedPlugin : parseModuleIds(DependencyType.MANAGED_PLUGIN)) {
        managedPlugins.put(new ModuleId(managedPlugin.pomFile().groupId(), managedPlugin.pomFile().artifactId(), null), managedPlugin.pomFile());
        allDependencies.add(new Pair<ManagedPOMFile,DependencyType>(managedPlugin, DependencyType.MANAGED_PLUGIN));
      }

      plugins.addAll(parseModuleIds(DependencyType.PLUGIN));
      for (final ManagedPOMFile plugin : plugins)
        allDependencies.add(new Pair<ManagedPOMFile,DependencyType>(plugin, DependencyType.PLUGIN));

      relationsResolved = true;
    }
  }

  private Version newVersion = null;

  public Version newVersion() {
    return newVersion;
  }

  public void checkIncreaseVersion(final Version.Part incrementPart) throws MojoFailureException {
    newVersion = this.version().increment(incrementPart);
    for (final ManagedPOMFile dependent : dependents) {
//      (dependent.manager() != null ? dependent.manager() : dependent.pomFile()).addUpdate(new UpdateCommand(dependent.dependencyType().scope, this, VersionUtil.increaseVersion(version())));
      if (dependent.manager() != null)
        dependent.manager().checkIncreaseVersion(incrementPart);

      dependent.pomFile().checkIncreaseVersion(incrementPart);
    }
  }

  public void addToTransaction(final Transaction transaction) throws IOException {
    final int[][] versionIndex = VersionUtil.indexOfTag(newText, "project/version");
    newText = newText.substring(0, versionIndex[0][0]) + newVersion + newText.substring(versionIndex[0][1]);
    transaction.addFile(file, newText.getBytes());
  }

  public void rollback() throws IOException {
    if (newText != null) {
      try (final RandomAccessFile raf = new RandomAccessFile(file, "rw")) {
        raf.write(text.getBytes());
        raf.setLength(raf.getFilePointer());
      }
    }
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj)
      return true;

    if (!(obj instanceof POMFile) || !super.equals(obj))
      return false;

    final POMFile that = (POMFile)obj;
    return file != null ? Paths.equal(file, that.file) : that.file == null;
  }

  @Override
  public int hashCode() {
    return super.hashCode() * (file != null ? 13 * file.hashCode() : -73);
  }

  @Override
  public String toString() {
    try {
      return super.toString() + "[" + file.getCanonicalFile().getAbsolutePath() + "]";
    }
    catch (final IOException e) {
      return super.toString() + "[" + e.getMessage() + "]";
    }
  }
}