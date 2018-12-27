# EasyJAX Version Maven Plugin

**Maven Plugin for POM version management goals**

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
  <groupId>org.easyjax.version</groupId>
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

### JavaDocs

JavaDocs are available [here](https://www.easyjax.org/version-maven-archetype/javadocs/).

## Contributing

Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.

Please make sure to update tests as appropriate.

### License

This project is licensed under the MIT License - see the [LICENSE.txt](LICENSE.txt) file for details.

<a href="http://cooltext.com" target="_top"><img src="https://cooltext.com/images/ct_pixel.gif" width="80" height="15" alt="Cool Text: Logo and Graphics Generator" border="0" /></a>

[mvn-plugin]: https://img.shields.io/badge/mvn-plugin-lightgrey.svg