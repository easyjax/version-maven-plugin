<img src="http://safris.org/logo.png" align="right" />
# version-maven-plugin [![CohesionFirst](http://safris.org/cf2.svg)](https://cohesionfirst.com/)
> Maven Plugin for POM version management goals

## Introduction

The `version-maven-plugin` plugin is used for POM version management goals.

## Goals Overview

* [`version:update`](https://github.com/SevaSafris/java/new/master/maven/plugin/version-maven-plugin#versionupdate) update POM artifact and plugin versions.

## Usage

### `version:update`

The `version:update` goal is bound to the `validate` phase, and is used to analyze and update versions of artifacts and plugins (and their dependents) that have been detected to change due to the GIT status.

#### Example 1

```xml
<plugin>
  <groupId>org.safris.maven.plugin</groupId>
  <artifactId>version-maven-plugin</artifactId>
  <version>0.1.1</version>
  <configuration>
    <incrementPart>MINOR</incrementPart>
    <incrementSnapshot>true</incrementSnapshot>
  </configuration>
</plugin>
```

#### Configuration Parameters

| Name                  | Type    | Use      | Description                                                                |
|:----------------------|:--------|:---------|:---------------------------------------------------------------------------|
| /`incrementPart    `  | String  | Optional | Part of version to increment: `[MAJOR|MINOR|PATCH]`. **Default:** `PATCH`. |
| /`incrementSnapshot`  | Boolean | Optional | Increment version of `-SNAPSHOT` artifacts. **Default:** `false`.          |

## Known Issues

**THIS PLUGIN IS IN ALPHA**

## License

This project is licensed under the MIT License - see the [LICENSE.txt](LICENSE.txt) file for details.
