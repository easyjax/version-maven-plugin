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

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;

import org.safris.commons.io.Files;
import org.safris.commons.lang.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Transaction {
  private static final Logger logger = LoggerFactory.getLogger(Transaction.class);
  private static final File tempDir = new File(new File(System.getProperty("java.io.tmpdir")), Transaction.class.getPackage().getName());

  static {
    tempDir.mkdir();
  }

  private final Map<File,File> realToTemp = new HashMap<File,File>();

  public void addFile(final File file, final byte[] contents) throws IOException {
    final File tempFile = new File(tempDir, file.getName() + "-" + Strings.getRandomAlphaNumericString(6));
    try (final RandomAccessFile raf = new RandomAccessFile(tempFile, "rw")) {
      raf.write(contents);
      raf.setLength(raf.getFilePointer());
    }

    realToTemp.put(file, tempFile);
  }

  public void commit() {
    try {
      for (final Map.Entry<File,File> entry : realToTemp.entrySet())
        java.nio.file.Files.move(entry.getValue().toPath(), entry.getKey().toPath(), StandardCopyOption.ATOMIC_MOVE);
    }
    catch (final IOException e) {
      throw new IllegalStateException("Error encountered mid-commit", e);
    }

    try {
      Files.deleteAll(tempDir.toPath());
    }
    catch (final IOException e) {
      logger.warn("Failed to delete temp dir: " + tempDir.getAbsolutePath());
    }
  }
}