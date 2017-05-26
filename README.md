<img src="https://www.cohesionfirst.org/logo.png" align="right">

## version-maven-plugin<br>![mvn-plugin][mvn-plugin] <a href="https://www.cohesionfirst.org/"><img src="https://img.shields.io/badge/CohesionFirst%E2%84%A2--blue.svg"></a>
> Maven Plugin for POM version management goals

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
  <groupId>org.libx4j.maven.plugin</groupId>
  <artifactId>version-maven-plugin</artifactId>
  <version>0.1.1</version>
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

### License

This project is licensed under the MIT License - see the [LICENSE.txt](LICENSE.txt) file for details.

[mvn-plugin]: https://img.shields.io/badge/mvn-plugin-lightgrey.svg