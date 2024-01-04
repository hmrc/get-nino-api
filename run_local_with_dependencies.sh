#!/bin/bash

sm2 --start GET_NINO_ALL

sm2 --stop GET_NINO_API

sbt run
