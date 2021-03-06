# status4spring
 [![Build Status](https://travis-ci.org/HerrSchwarz/status4spring.svg)](https://travis-ci.org/HerrSchwarz/status4spring) [![codecov](https://codecov.io/gh/HerrSchwarz/status4spring/branch/develop/graph/badge.svg)](https://codecov.io/gh/HerrSchwarz/status4spring)
 [![Dependency Status](https://www.versioneye.com/user/projects/573c9c5fce8d0e004505e902/badge.svg?style=flat)](https://www.versioneye.com/user/projects/573c9c5fce8d0e004505e902)
 [![Codacy Badge](https://api.codacy.com/project/badge/Grade/6b33af614b2f4497bf6c50a1b6ddc3c7)](https://www.codacy.com/app/felix_3/status4spring?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=HerrSchwarz/status4spring&amp;utm_campaign=Badge_Grade)
 [![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.herrschwarz/status4spring/badge.svg)](https://maven-badges.herokuapp.com/maven-central//com.github.herrschwarz/status4spring) [![Licence](https://img.shields.io/github/license/mashape/apistatus.svg)](https://github.com/HerrSchwarz/status4spring/blob/master/LICENSE)

There is a [Spring Boot example](https://github.com/herrschwarz/status4springExampleSpringBoot) available. Clone the repo, run 'mvn spring-boot:run' and have a look at [http://localhost:8080/internal/status](http://localhost:8080/internal/status)

The status controller is in an early stage and should be considered unfinished.

Status controller for spring applications. Can provide various information like:

- version
- server time
- uptime
- load
- memory
- number of threads
- number of cpu cores
- OS information (arch, name, version)
- system properties
- environment variables
- cache statistics
- results of health checks

Status4spring comes with two health inspectors:

- check if a host is reachable on a certain port
- check if a mongodb is ok
- you can implement your own health inspectors

There is CacheStatsProvider for caches using ConcurrentHashMaps. This is the standard cache used in Spring Boot. You can implement your own CacheStatsProvider, if you are using something else.

## Requirements

If you use Spring boot with default template configuration (prefix=classpath:/templates and suffix=.html) you only need to set up the controller in your Spring config. Otherwise you will need to set up:

- Spring WebMVC
- Thymeleaf (needs access to 'messages/status_messages.properties', prefix should be set up similar to Spring Boot projects)

## What you get

You will get pages with different information:

- Status page
    -  with some information about your system incl. version of your project
    - System properties
    - Environment variables
- Version page, delivers version only (can be used to check the version after a deployment, e.g. with ansible)
- Health page, representing the health of the server (need HealthInspectors to be configured). delivers json

You will find these pages:

- ${application-root}/internal/status
- ${application-root}/internal/version
- ${application-root}/internal/health

It is possible to integrate more pages, e.g. for testing purposes.

## Maven dependency

Status4spring is released to maven central:

```xml
<dependency>
    <groupId>com.github.herrschwarz</groupId>
    <artifactId>status4spring</artifactId>
    <version>0.1.1</version>
</dependency>
```

## ToDo:

- make urls configurable
- Documentation
- more health inspectors
- See Issues

## License

This software is distributed under the [MIT License](LICENSE.md).
