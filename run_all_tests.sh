#!/usr/bin/env bash

sbt scalafmtAll clean compile coverage test it/test coverageOff dependencyUpdates coverageReport
