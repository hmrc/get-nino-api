#!/usr/bin/env bash

sbt clean compile scalastyle coverage test it:test coverageOff coverageReport