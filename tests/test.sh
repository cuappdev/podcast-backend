#!/bin/bash
if [ $# -eq 0 ]
then
  python ../venv/bin/nosetests --nocapture
else
  python ../venv/bin/nosetests $1 --nocapture
fi
