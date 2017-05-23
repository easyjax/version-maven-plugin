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

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VersionUtil {
  private static final Pattern tagPattern = Pattern.compile("((?m)<\\/?[a-z][\\w0-9-]*(([ >])|($)))|(<!--)|(-->)");

  public static int[][] indexOfTag(final String text, final String ... xpaths) {
    final Matcher matcher = tagPattern.matcher(text);

    int i = 0;
    int d = -1;
    boolean inComment = false;
    final List<int[]> results = new ArrayList<int[]>();
    for (final String xpath : xpaths) {
      matcher.reset();
      final String[] parts = xpath.split("\\/");
      while (matcher.find()) {
        final int start = matcher.start();
        final int end = matcher.end();
        final String tag = text.substring(start, end);
        if (tag.equals("<!--")) {
          inComment = true;
        }
        else if (tag.endsWith("-->")) {
          inComment = false;
        }
        else if (!inComment) {
          d += tag.startsWith("</") ? -1 : 1;
          if (d == i) {
            if (i > 0 && (tag.equals("</" + parts[i - 1] + ">") || tag.trim().equals("</" + parts[i - 1]))) {
              --i;
            }
            else if ((tag.equals("<" + parts[i] + ">") || tag.trim().equals("<" + parts[i])) && ++i == parts.length) {
              final int index = text.indexOf('>', start + 1) + 1;
              results.add(new int[] {index, text.indexOf("</" + parts[i-- - 1] + ">", index)});
            }
          }
        }
      }
    }

    return results.size() > 0 ? results.toArray(new int[results.size()][2]) : null;
  }

  private static final Pattern p = Pattern.compile("^(?<major>\\d+)\\.(?<minor>\\d+)\\.(?<patch>\\d+)(?<suffix>[^\\d].*)$");

  public static String increaseVersion(final String version) {
//    if (version.endsWith("-SNAPSHOT"))
//      return version;

    final Matcher matcher = p.matcher(version);
    matcher.find();
    final int major = Integer.parseInt(matcher.group("major"));
    final int minor = Integer.parseInt(matcher.group("minor"));
    final int patch = Integer.parseInt(matcher.group("patch"));
    final String suffix = matcher.group("suffix");
    return major + "." + minor + "." + (patch + 1) + suffix;
  }

  private VersionUtil() {
  }
}