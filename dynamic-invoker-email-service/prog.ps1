$output = mvn -X package
$start_time = Get-Date
$total_duration = New-TimeSpan

foreach ($line in $output.Split("`n")) {
    if ($line -match "^---") {
        $start_time = [datetime]::ParseExact($line.Split(" ")[0], "yyyy-MM-dd HH:mm:ss", $null)
    }
    elseif ($line -match "^<<<" -and $start_time) {
        $end_time = [datetime]::ParseExact($line.Split(" ")[0], "yyyy-MM-dd HH:mm:ss", $null)
        $duration = $end_time - $start_time
        $total_duration = $total_duration.Add($duration)
        Write-Host "Goal or Phase took $duration seconds"
        Write-Host "ETA is $($total_duration.TotalSeconds * (1 - ($total_duration.TotalSeconds / (New-TimeSpan (Get-Date) $start_time).TotalSeconds)))"
        $start_time = $null
    }
}