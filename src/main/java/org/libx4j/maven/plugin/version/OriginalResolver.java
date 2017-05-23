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

package org.libx4j.maven.plugin.version;

import java.io.IOException;

import org.eclipse.jgit.errors.CorruptObjectException;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.TreeWalk;

public class OriginalResolver implements AutoCloseable {
  private final ObjectReader reader;
  private final RevWalk walk;
  private final RevCommit commit;

  public OriginalResolver(final Repository repository) throws IncorrectObjectTypeException, IOException, MissingObjectException {
    // Resolve the revision specification
    final ObjectId id = repository.resolve(Constants.HEAD);

    this.reader = repository.newObjectReader();
    this.walk = new RevWalk(reader);
    this.commit = walk.parseCommit(id);
  }

  public String fetchBlob(final String path) throws CorruptObjectException, IncorrectObjectTypeException, IOException, MissingObjectException {
    // .. and narrow it down to the single file's path
    final TreeWalk treewalk = TreeWalk.forPath(reader, path, commit.getTree());

    if (treewalk == null)
      return null;

    // use the blob id to read the file's data
    final byte[] data = reader.open(treewalk.getObjectId(0)).getBytes();
    return new String(data, "utf-8");
  }

  @Override
  public void close() {
    walk.close();
    reader.close();
  }
}