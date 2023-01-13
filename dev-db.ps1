# starts DB container (for backapp tests)

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

function ReCreate-Container-And-Db ($db_path, $env_path) {
    Remove-Item -Recurse -Force $db_path
    New-Item -Path $db_path -ItemType "directory"
    $sub_cmd=Get-BuidArgs -filepath $env_path
    $docker_compose_cmd_string="docker compose -f docker-compose.dev.yaml build $sub_cmd --no-cache"
    $ExecutionContext.InvokeCommand.ExpandString($docker_compose_cmd_string)
    docker compose -f docker-compose.dev.yaml --env-file=$env_path up
}

$docker_state=docker container inspect -f='{{.State.Status}}' ashev-demo-dbhost-1 | Out-String
$docker_state = $docker_state.Substring(0, $docker_state.Length - 2) # remove from tail ASCII: 10 13
if ( $docker_state.Length -eq 0 ) # error
{
    Write-Output 'Need to create'
    ReCreate-Container-And-Db -db_path $db_path -env_path $env_path
}
else
{
    if ( $docker_state -eq 'running' )
    {
        Write-Output 'It is already running'
    }
    else
    {
        Write-Output 'Need to destroy and create'
        docker compose -f docker-compose.dev.yaml down
        ReCreate-Container-And-Db -db_path $db_path -env_path $env_path
    }
}
Write-Host -NoNewLine 'Press any key to continue...';
$null = $Host.UI.RawUI.ReadKey('NoEcho,IncludeKeyDown');