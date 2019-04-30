# OpenJAX Std Version Maven Plugin

> Maven Plugin for POM version management goals

[![Build Status](https://travis-ci.org/openjax/version-maven-plugin.png)](https://travis-ci.org/openjax/version-maven-plugin)
[![Coverage Status](https://coveralls.io/repos/github/openjax/version-maven-plugin/badge.svg)](https://coveralls.io/github/openjax/version-maven-plugin)
[![Javadocs](https://www.javadoc.io/badge/org.openjax.std/version-maven-plugin.svg)](https://www.javadoc.io/doc/org.openjax.std/version-maven-plugin)
[![Released Version](https://img.shields.io/maven-central/v/org.openjax.std/version-maven-plugin.svg)](https://mvnrepository.com/artifact/org.openjax.std/version-maven-plugin)

### Introduction

The `version-maven-plugin` plugin is used for POM version management goals.

### Goals Overview

* [`version:update`](#versionupdate) update POM artifact and plugin versions.

### Usage

#### `version:update`

The `version:update` goal is bound to the `validate` phase, and is used to analyze and update versions of artifacts and plugins (and their dependents) that have been detected to change due to the GIT status.

##### Example 1

```xml
<plugin>
  <groupId>org.openjax.std</groupId>
  <artifactId>version-maven-plugin</artifactId>
  <version>0.1.3-SNAPSHOT</version>
  <configuration>
    <incrementPart>MINOR</incrementPart>
    <incrementSnapshot>true</incrementSnapshot>
  </configuration>
</plugin>
```

##### Configuration Parameters

| Name                 | Type    | Use      | Description                                                                |
|:---------------------|:--------|:---------|:---------------------------------------------------------------------------|
| `/incrementPart`     | String  | Optional | Part of version to increment: `[MAJOR|MINOR|PATCH]`. **Default:** `PATCH`. |
| `/incrementSnapshot` | Boolean | Optional | Increment version of `-SNAPSHOT` artifacts. **Default:** `false`.          |

### Known Issues

**THIS PLUGIN IS IN ALPHA**

## Contributing

Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.

Please make sure to update tests as appropriate.

### License

This project is licensed under the MIT License - see the [LICENSE.txt](LICENSE.txt) file for details.

[mvn-plugin]: https://img.shields.io/badge/mvn-plugin-lightgrey.svg