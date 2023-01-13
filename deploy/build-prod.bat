rmdir "deploy\release" /S /Q
md "deploy\release"
md "deploy\release\docker"
md "deploy\release\docker\dbapp"
md "deploy\release\docker\dbapp\db-volume"
md "deploy\release\docker\dbapp\db-script"
md "deploy\release\docker\backapp"

set JAVA_HOME=C:\Program Files\BellSoft\LibericaJDK-11

call mvnw.cmd clean package -Dmaven.test.skip -P prod

xcopy "backend\target\prod\backend-0.0.1-SNAPSHOT.jar" "deploy\release\docker\backapp"

call npm run --prefix frontend\vue-project build-prod

xcopy "frontend\vue-project\dist\prod\" "deploy\release\dist\" /e

xcopy "deploy\nginx_ashev_demo.conf" "deploy\release\"

xcopy "deploy\nginx_ashev_demo_update.conf" "deploy\release\"

xcopy "src\main\resources\dbapp\db-script\init.sql" "deploy\release\docker\dbapp\db-script"

xcopy "deploy\docker-compose.prod.yaml" "deploy\release\docker"

xcopy "src\main\resources\backapp\Dockerfile_prod" "deploy\release\docker\backapp"

xcopy "src\main\resources\wait-for-it.sh" "deploy\release\docker"

xcopy "deploy\run.sh" "deploy\release\docker"

xcopy "deploy\index_update.html" "deploy\release\dist"

xcopy "deploy\.env" "deploy\release\docker"

xcopy "deploy\build-args.py" "deploy\release\docker"