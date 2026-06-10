$password = "123520"
$commands = @(
    "USE practice_management;",
    "ALTER TABLE activity_registration ADD COLUMN IF NOT EXISTS evaluation_status VARCHAR(20) DEFAULT 'PENDING' COMMENT '点评状态';",
    "ALTER TABLE activity_registration ADD COLUMN IF NOT EXISTS rating VARCHAR(20) COMMENT '点评等级';",
    "ALTER TABLE activity_registration ADD COLUMN IF NOT EXISTS final_credit_points DECIMAL(5,2) COMMENT '最终学分';"
)

foreach ($cmd in $commands) {
    $output = & mysql -u root -p$password -e $cmd 2>&1
    Write-Host "Command: $cmd"
    Write-Host "Output: $output"
    Write-Host "---"
}
