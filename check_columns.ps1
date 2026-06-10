$password = "123520"
$query = "SHOW COLUMNS FROM activity_registration"
& mysql -u root -p$password -D practice_management -e $query 2>&1 | Out-String
