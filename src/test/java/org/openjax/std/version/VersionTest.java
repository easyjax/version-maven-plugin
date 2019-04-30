/* Copyright (c) 2016 OpenJAX
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

package org.openjax.std.version;

import static org.junit.Assert.*;

import org.apache.maven.plugin.MojoFailureException;
import org.junit.Test;

public class VersionTest {
  @Test
  public void testVersion() throws MojoFailureException {
    assertEquals(0, new Version("1.7.1").compareTo(new Version("1.7.1")));

    assertEquals(new Version("0.1.1-SNAPSHOT"), new Version("0.1.0-SNAPSHOT").increment(Version.Part.PATCH));
    assertEquals(new Version("0.2.0-SNAPSHOT"), new Version("0.1.0-SNAPSHOT").increment(Version.Part.MINOR));
    assertEquals(new Version("1.1.0-SNAPSHOT"), new Version("0.1.0-SNAPSHOT").increment(Version.Part.MAJOR));

    assertEquals(0, new Version("1.1.0-SNAPSHOT").compareTo(new Version("1.1.0-SNAPSHOT")));
    assertEquals(-1, new Version("1.1.0-SNAPSHOT").compareTo(new Version("1.1.1-SNAPSHOT")));
    assertEquals(-1, new Version("1.1.0-SNAPSHOT").compareTo(new Version("1.2-SNAPSHOT")));
    assertEquals(0, new Version("1.1.1-SNAPSHOT").compareTo(new Version("1.1.1-SNAPSHOT")));

    assertEquals(1, new Version("RELEASE").compareTo(new Version("1.1.0-SNAPSHOT")));
  }
}