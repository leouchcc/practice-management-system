const mysql = require('mysql2/promise');
const axios = require('axios');

async function main() {
  // 连接本地数据库
  const localDb = await mysql.createConnection({
    host: 'localhost',
    user: 'root',
    password: '123520',
    database: 'practice_management'
  });

  console.log('正在导出本地数据...');

  // 导出所有数据（使用本地表名）
  const [users] = await localDb.execute('SELECT * FROM sys_user');
  const [categories] = await localDb.execute('SELECT * FROM activity_category');
  const [activities] = await localDb.execute('SELECT * FROM activity');
  const [registrations] = await localDb.execute('SELECT * FROM activity_registration');
  const [announcements] = await localDb.execute('SELECT * FROM announcement');
  const [suggestions] = await localDb.execute('SELECT * FROM activity_suggestion');
  const [notifications] = await localDb.execute('SELECT * FROM system_notification');
  const [creditRecords] = await localDb.execute('SELECT * FROM credit_record');

  await localDb.end();

  console.log(`用户: ${users.length} 条`);
  console.log(`分类: ${categories.length} 条`);
  console.log(`活动: ${activities.length} 条`);
  console.log(`报名: ${registrations.length} 条`);
  console.log(`公告: ${announcements.length} 条`);
  console.log(`建议: ${suggestions.length} 条`);
  console.log(`通知: ${notifications.length} 条`);
  console.log(`学分记录: ${creditRecords.length} 条`);

  // 发送到线上后端
  const data = {
    users,
    categories,
    activities,
    registrations,
    announcements,
    suggestions,
    notifications,
    creditRecords
  };

  console.log('\n正在同步到线上数据库...');

  try {
    const response = await axios.post(
      'https://practice-management-system-production.up.railway.app/api/data/sync',
      data,
      { timeout: 60000 }
    );

    console.log('同步成功!');
    console.log(response.data);
  } catch (error) {
    console.error('同步失败:', error.response?.data || error.message);
  }
}

main().catch(console.error);