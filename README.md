# status4spring

[![Build Status](https://travis-ci.org/HerrSchwarz/status4spring.svg)](https://travis-ci.org/HerrSchwarz/status4spring)

This is in a quite early stage and to be considered unfinished. But it's already more or less usable.

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
- results of health checks

Status4spring comes with two health inspectors:

- check if a host is reachable on a certain port
- check if a mongodb is ok
- you can implement your own health inspectors

## Requirements

You will need to set up:

- Spring WebMVC
- Thymeleaf (needs access to 'messages/status_messages.properties')

## What you get

You will get three pages:

- Status page, with some information about your system
- Version page with only your version (can be used to check the version after a deployment, e.g. with ansible)
- Health page, representing the health of the server (need HealthInspectors to be configured)

You will find these pages:

- ${application-root}/internal/status
- ${application-root}/internal/version
- ${application-root}/internal/health

## ToDo:

- make urls configurable
- Documentation
- release to maven central
- create a good example project
- more health inspectors