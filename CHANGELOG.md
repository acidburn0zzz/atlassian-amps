# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [8.1.0] - unreleased

### Added 
- Changelog
- AbstractProductHandler: Added possibility to override product context with properties define in product's pom
- AbstractWebappProductHandler: Added overrides for webapp container artifact and _containerId_ with properties defined in product's pom. Properties' names:
    - _amps.product.specific.cargo.container_ for cargo container id
    - _amps.product.specific.container_ for container artifact
- IntegratedTestMojo: Added testGroup.instanceIds property, a comma separated list of all product instances in the current testgroup.
  This can be used as an entrypoint to discover each instances complete configuration through the properties.

### Changed

- IntegratedTestMojo: Fixed name of the product version property
- MavenGoals: Update maven-javadoc-plugin to fix rest doc generation for plain maven builds

### Fixed
- [AMPS-1514] -Dno.webapp now correctly sets http port.

##8.0.0
