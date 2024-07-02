#!/bin/bash

cd "$(dirname "$0")" && cd ..

./gradlew publishAllPublicationsToLocalRepository || exit 1
rsync -avHSx ./build/mavenLocal/ maven:~/m2/
