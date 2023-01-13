param(
[string]$srv,
[string]$port,
[Int32]$seconds
)
while((Test-NetConnection $srv -Port $port).TcpTestSucceeded -ne 'True')
{
    Write-Host "Sleep $seconds seconds"
    Start-Sleep -Seconds $seconds
}
Write-Host "OK"