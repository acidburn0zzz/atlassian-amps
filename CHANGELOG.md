# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [8.1.1] - 2020-03-10
### Fixed
- Upgraded commons-compress to 1.20 (CVE-2019-1240)

## [8.1.0] - 2020-03-10

### Added 
- Changelog
- All code and strategies for handling resource minification can be found in the `com.atlassian.maven.plugins.amps.minifier` package.
- Minifier: New interface for file minification strategies.
- Sources: New class that encapsulates multiple input files - e.g., raw source & source maps - for resource minification process. 
- AbstractProductHandler: Added possibility to override product context with properties define in product's pom
- AbstractWebappProductHandler: Added overrides for webapp container artifact and _containerId_ with properties defined in product's pom. Properties' names:
    - _amps.product.specific.cargo.container_ for cargo container id
    - _amps.product.specific.container_ for container artifact
- IntegratedTestMojo: Added testGroup.instanceIds property, a comma separated list of all product instances in the current testgroup.
  This can be used as an entrypoint to discover each instances complete configuration through the properties.
- System property "atlassian.allow.insecure.url.parameter.login" as default for products.

### Fixed

- [AMPS-1514] -Dno.webapp now correctly sets http port.

### Changed

- Google Closure Compiler is the default minifier for JS files.
- ResourcesMinifier: Rewritten to employ strategy pattern for selecting minification strategies.  
- IntegratedTestMojo: Fixed name of the product version property
- MavenGoals: Update maven-javadoc-plugin to fix rest doc generation for plain maven builds

### Deprecated

- All usage of YUI Compressor is deprecated and will be removed in AMPS v9.

## [8.0.0]
