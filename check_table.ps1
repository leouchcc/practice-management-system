$password = "123520"
$command = "USE practice_management; SHOW COLUMNS FROM activity_registration;"
& mysql -u root -p$password -e $command
