set VERSION=3.1.2


call mvn clean install || exit /b

docker build -t pierre-yves-monnet/c8-con-pdf:%VERSION% .
docker tag pierre-yves-monnet/c8-con-pdf:%VERSION% ghcr.io/camunda-community-hub/c8-con-pdf:%VERSION%
docker push ghcr.io/camunda-community-hub/c8-con-pdf:%VERSION%
docker tag pierre-yves-monnet/c8-con-pdf:%VERSION% ghcr.io/camunda-community-hub/c8-con-pdf:latest
docker push ghcr.io/camunda-community-hub/c8-con-pdf:latest
