/* Copyright (c) 2015 lib4j
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
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.maven.plugin.MojoFailureException;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.GitAPIException;

public class GitUtil {
  public static File findRepositoryDir(final File dir) {
    File repoDir = dir;
    while (repoDir != null && !new File(repoDir, ".git").isDirectory())
      repoDir = repoDir.getParentFile();

    return repoDir;
  }

  public static Set<String> lookupChangedFiles(final POMFile pomFile, final Git git) throws GitAPIException, IOException, MojoFailureException {
    final Status status = git.status().call();
    final Set<String> changes = new HashSet<String>();
    changes.addAll(status.getChanged());
    changes.addAll(status.getAdded());
    changes.addAll(status.getRemoved());

    final File repoDir = git.getRepository().getDirectory().getParentFile();
    final String include = pomFile.file().getParentFile().getAbsolutePath().substring(repoDir.getAbsolutePath().length() + 1);
    final Iterator<String> iterator = changes.iterator();
    while (iterator.hasNext()) {
      final String entry = iterator.next();
      if (!entry.startsWith(include)) {
        iterator.remove();
        continue;
      }

      for (final POMFile modulePomFile : pomFile.modules()) {
        final String exclude = modulePomFile.file().getParentFile().getAbsolutePath().substring(repoDir.getAbsolutePath().length() + 1);
        if (entry.startsWith(exclude)) {
          iterator.remove();
          break;
        }
      }
    }

    return changes;
  }

  private GitUtil() {
  }
}