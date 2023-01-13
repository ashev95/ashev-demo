REM имитируем работу сервера через отдельный контейнер

docker-compose down

rmdir "docker_db_host_data\db-volume" /S /Q
md "docker_db_host_data\db-volume"

set JAVA_HOME=C:\Program Files\BellSoft\LibericaJDK-11

call mvnw.cmd clean package -Dmaven.test.skip -P tst

call npm run --prefix frontend\vue-project build-tst

set "sub_cmd="
for /F "usebackq tokens=*" %%A in ("src/main/resources/env-tst/.env") do call set "sub_cmd=%%sub_cmd%% --build-arg %%A"

set docker_compose_cmd_string=docker compose -f docker-compose.tst.yaml build %sub_cmd% --no-cache
%docker_compose_cmd_string%

docker compose -f docker-compose.tst.yaml --env-file=src/main/resources/env-tst/.env up