$mysqld = "C:\Program Files\MySQL\MySQL Server 8.4\bin\mysqld.exe"
$iniFile = "C:\ProgramData\MySQL\MySQL Server 8.4\my.ini"

$running = Get-Process mysqld -ErrorAction SilentlyContinue
if ($running) {
    Write-Output "mysqld is already running (PID: $($running.Id))"
} else {
    Start-Process -FilePath $mysqld -ArgumentList "--defaults-file=`"$iniFile`"" -WindowStyle Hidden
    Start-Sleep -Seconds 3
    Write-Output "mysqld started"
}
