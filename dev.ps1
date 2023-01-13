# starts all needed for development locally

$env_path=".\src\main\resources\env-dev\.env"
$db_path="docker_db_host_data\db-volume"

function Get-BuidArgs ($filepath) {
    $sub_cmd=""
    foreach($line in Get-Content $filepath) {
        if($line -match $regex){
            $sub_cmd=$sub_cmd + " --build-arg " + $line
        }
    }
    return $sub_cmd
}

docker compose -f docker-compose.dev.yaml down

Remove-Item -Recurse -Force "docker_db_host_data\db-volume"
New-Item -ItemType Directory -Force -Path "docker_db_host_data\db-volume"

$Env:JAVA_HOME="C:\Program Files\BellSoft\LibericaJDK-11"

cmd.exe /c "mvnw.cmd clean package -Dmaven.test.skip -P dev"

$sub_cmd=Get-BuidArgs -filepath $env_path
$docker_compose_cmd_string="docker compose -f docker-compose.dev.yaml build $sub_cmd --no-cache"
$ExecutionContext.InvokeCommand.ExpandString($docker_compose_cmd_string)
docker compose -f docker-compose.dev.yaml --env-file=$env_path up -d

./wait-for-it.ps1 "localhost" "5432" 3
Start-Sleep -Seconds 10

get-content $env_path | foreach {
    $name, $value = $_.split('=')
    set-content env:\$name $value
}

#do it once (look accurate at input for pasting)
#cmd.exe /c "echo """"%JAVA_HOME%\bin\keytool"" -noprompt -importkeystore -srckeystore ""cert/tst/localhost.pfx"" -srcstoretype %APP_KEYSTORE_TYPE% -srcstorepass %APP_KEYSTORE_PASSWORD% -destkeystore ""%APP_KEYSTORE%"" -deststoretype %APP_KEYSTORE_TYPE% -deststorepass %APP_KEYSTORE_PASSWORD%"""

Start-Process "cmd" -ArgumentList "/c java -jar -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:7342 -Dfile.encoding=UTF-8 backend/target/backend-0.0.1-SNAPSHOT.jar com.example.demo.DemoApplication --server.port=8091"

Start-Process "cmd" -ArgumentList "/c npm run --prefix frontend\vue-project serve"
