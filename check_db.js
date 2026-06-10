const mysql = require('mysql2/promise');

async function main() {
  const connection = await mysql.createConnection({
    host: 'localhost',
    user: 'root',
    password: '123520',
    database: 'practice_management'
  });

  console.log('数据库连接成功');

  // 查看用户表
  const [users] = await connection.execute('SELECT * FROM sys_user');
  console.log('用户表数据:');
  console.table(users);

  // 查看活动表
  const [activities] = await connection.execute('SELECT * FROM activity');
  console.log('\n活动表数据:');
  console.table(activities);

  // 查看公告表
  const [announcements] = await connection.execute('SELECT * FROM announcement');
  console.log('\n公告表数据:');
  console.table(announcements);

  await connection.end();
}

main().catch(console.error);
