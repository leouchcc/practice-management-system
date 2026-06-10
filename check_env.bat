@echo off

REM 检查Java版本
echo 检查Java版本...
java -version
if %errorlevel% neq 0 (
    echo Java未正确安装或环境变量未配置
) else (
    echo Java安装正常
)

echo.

REM 检查MySQL服务状态
echo 检查MySQL服务状态...
sc query MySQL80
if %errorlevel% neq 0 (
    echo MySQL服务不存在或未安装
) else (
    echo MySQL服务状态检查完成
)

echo.

REM 检查8080端口占用情况
echo 检查8080端口占用情况...
netstat -ano | findstr :8080
if %errorlevel% neq 0 (
    echo 8080端口未被占用
) else (
    echo 8080端口已被占用
)

echo.

echo 环境检查完成，请按任意键退出...
pause > nul
