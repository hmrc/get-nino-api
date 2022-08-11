#!/usr/bin/env bash

sbt scalafmtAll clean compile scalastyle coverage test it:test coverageOff dependencyUpdates coverageReport
