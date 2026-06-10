$password = "123520"
& mysql -u root -p$password -D practice_management -e "SHOW COLUMNS FROM activity_registration" | Out-File -FilePath "c:\Users\Liu17\OneDrive\Desktop\lhh35\table_structure.txt" -Encoding UTF8
Get-Content "c:\Users\Liu17\OneDrive\Desktop\lhh35\table_structure.txt"
