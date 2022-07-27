[![Build Status](https://qa.nuxeo.org/jenkins/buildStatus/icon?job=Misc/tools_nuxeo-studio-components-common-master)](https://qa.nuxeo.org/jenkins/job/Misc/job/tools_nuxeo-studio-components-common-master/)

# About

Library that extracts Nuxeo Components to Nuxeo Studio's registries format

# Code

## Build

    mvn clean install
    
## Release

This project uses jgitver for versioning, see https://github.com/jgitver/jgitver.

To release this project just push an annotated Git tag following the 'vX.Y.Z' pattern on any branch.
```
git tag -a vX.Y.Z -m "Release X.Y.Z"
git push origin vX.Y.Z
```

# Contributing / Reporting issues

- https://jira.nuxeo.com/browse/NXBT/component/16055/
- https://jira.nuxeo.com/secure/CreateIssue!default.jspa?project=NXBT

# License

[Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.html)

# About Nuxeo

The [Nuxeo Platform](http://www.nuxeo.com/products/content-management-platform/) is an open source customizable and extensible content management platform for building business applications. It provides the foundation for developing [document management](http://www.nuxeo.com/solutions/document-management/), [digital asset management](http://www.nuxeo.com/solutions/digital-asset-management/), [case management application](http://www.nuxeo.com/solutions/case-management/) and [knowledge management](http://www.nuxeo.com/solutions/advanced-knowledge-base/). You can easily add features using ready-to-use addons or by extending the platform using its extension point system.

The Nuxeo Platform is developed and supported by Nuxeo, with contributions from the community.

Nuxeo dramatically improves how content-based applications are built, managed and deployed, making customers more agile, innovative and successful. Nuxeo provides a next generation, enterprise ready platform for building traditional and cutting-edge content oriented applications. Combining a powerful application development environment with
SaaS-based tools and a modular architecture, the Nuxeo Platform and Products provide clear business value to some of the most recognizable brands including Verizon, Electronic Arts, Sharp, FICO, the U.S. Navy, and Boeing. Nuxeo is headquartered in New York and Paris.
More information is available at [www.nuxeo.com](http://www.nuxeo.com).