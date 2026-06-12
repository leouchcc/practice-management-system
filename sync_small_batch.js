const https = require('https');
const http = require('http');

// Railway 后端地址
const RAILWAY_HOST = 'practice-management-system-production.up.railway.app';

// 本地后端地址
const LOCAL_HOST = 'localhost';
const LOCAL_PORT = 8080;

// 分批同步，每批只同步 1 条记录
async function exportData() {
  return new Promise((resolve, reject) => {
    http.get(`http://${LOCAL_HOST}:${LOCAL_PORT}/api/data/export`, (res) => {
      let data = '';
      res.on('data', (chunk) => { data += chunk; });
      res.on('end', () => {
        try {
          const result = JSON.parse(data);
          resolve(result.data);
        } catch (e) {
          reject(new Error('JSON解析失败: ' + e.message));
        }
      });
    }).on('error', reject);
  });
}

async function syncSingleRecord(tableName, record) {
  return new Promise((resolve, reject) => {
    const jsonData = JSON.stringify({ [tableName]: [record] });
    const options = {
      hostname: RAILWAY_HOST,
      path: '/api/data/sync-table',
      method: 'POST',
      headers: { 
        'Content-Type': 'application/json', 
        'Content-Length': Buffer.byteLength(jsonData)
      }
    };
    
    const req = https.request(options, (res) => {
      let responseData = '';
      res.on('data', (chunk) => { responseData += chunk; });
      res.on('end', () => {
        try {
          const result = JSON.parse(responseData);
          if (result.code === 200) {
            resolve(result.message);
          } else {
            reject(new Error(result.message));
          }
        } catch (e) {
          reject(new Error('响应解析失败: ' + responseData.substring(0, 200)));
        }
      });
    });
    
    req.on('error', reject);
    req.write(jsonData);
    req.end();
  });
}

async function clearDatabase() {
  return new Promise((resolve, reject) => {
    const options = {
      hostname: RAILWAY_HOST,
      path: '/api/data/clear',
      method: 'POST'
    };
    
    const req = https.request(options, (res) => {
      let responseData = '';
      res.on('data', (chunk) => { responseData += chunk; });
      res.on('end', () => {
        try {
          const result = JSON.parse(responseData);
          if (result.code === 200) {
            resolve(result.message);
          } else {
            reject(new Error(result.message));
          }
        } catch (e) {
          reject(new Error('响应解析失败: ' + responseData));
        }
      });
    });
    
    req.on('error', reject);
    req.end();
  });
}

async function main() {
  try {
    console.log('正在导出本地数据...');
    const data = await exportData();
    
    console.log('\n正在清空 Railway 数据库...');
    const clearResult = await clearDatabase();
    console.log('清空完成:', clearResult);
    
    console.log('\n正在同步数据到 Railway（每条记录单独同步）...');
    
    // 同步用户数据
    if (data.users && data.users.length > 0) {
      console.log('\n同步 users:');
      for (let i = 0; i < data.users.length; i++) {
        const user = data.users[i];
        console.log(`  [${i + 1}/${data.users.length}] 同步用户 ${user.username}...`);
        try {
          await syncSingleRecord('users', user);
          console.log('    ✓ 成功');
        } catch (e) {
          console.log('    ✗ 失败:', e.message);
        }
        await new Promise(r => setTimeout(r, 300));
      }
    }
    
    // 同步分类数据
    if (data.categories && data.categories.length > 0) {
      console.log('\n同步 categories:');
      for (let i = 0; i < data.categories.length; i++) {
        const category = data.categories[i];
        console.log(`  [${i + 1}/${data.categories.length}] 同步分类 ${category.name}...`);
        try {
          await syncSingleRecord('categories', category);
          console.log('    ✓ 成功');
        } catch (e) {
          console.log('    ✗ 失败:', e.message);
        }
        await new Promise(r => setTimeout(r, 300));
      }
    }
    
    // 同步活动数据
    if (data.activities && data.activities.length > 0) {
      console.log('\n同步 activities:');
      for (let i = 0; i < data.activities.length; i++) {
        const activity = data.activities[i];
        console.log(`  [${i + 1}/${data.activities.length}] 同步活动 ${activity.title}...`);
        try {
          await syncSingleRecord('activities', activity);
          console.log('    ✓ 成功');
        } catch (e) {
          console.log('    ✗ 失败:', e.message);
        }
        await new Promise(r => setTimeout(r, 300));
      }
    }
    
    // 同步报名数据
    if (data.registrations && data.registrations.length > 0) {
      console.log('\n同步 registrations:');
      for (let i = 0; i < data.registrations.length; i++) {
        const registration = data.registrations[i];
        console.log(`  [${i + 1}/${data.registrations.length}] 同步报名记录...`);
        try {
          await syncSingleRecord('registrations', registration);
          console.log('    ✓ 成功');
        } catch (e) {
          console.log('    ✗ 失败:', e.message);
        }
        await new Promise(r => setTimeout(r, 300));
      }
    }
    
    // 同步公告数据
    if (data.announcements && data.announcements.length > 0) {
      console.log('\n同步 announcements:');
      for (let i = 0; i < data.announcements.length; i++) {
        const announcement = data.announcements[i];
        console.log(`  [${i + 1}/${data.announcements.length}] 同步公告 ${announcement.title}...`);
        try {
          await syncSingleRecord('announcements', announcement);
          console.log('    ✓ 成功');
        } catch (e) {
          console.log('    ✗ 失败:', e.message);
        }
        await new Promise(r => setTimeout(r, 300));
      }
    }
    
    // 同步学分记录
    if (data.creditRecords && data.creditRecords.length > 0) {
      console.log('\n同步 creditRecords:');
      for (let i = 0; i < data.creditRecords.length; i++) {
        const record = data.creditRecords[i];
        console.log(`  [${i + 1}/${data.creditRecords.length}] 同步学分记录...`);
        try {
          await syncSingleRecord('creditRecords', record);
          console.log('    ✓ 成功');
        } catch (e) {
          console.log('    ✗ 失败:', e.message);
        }
        await new Promise(r => setTimeout(r, 300));
      }
    }
    
    // 同步通知数据
    if (data.notifications && data.notifications.length > 0) {
      console.log('\n同步 notifications:');
      for (let i = 0; i < data.notifications.length; i++) {
        const notification = data.notifications[i];
        console.log(`  [${i + 1}/${data.notifications.length}] 同步通知...`);
        try {
          await syncSingleRecord('notifications', notification);
          console.log('    ✓ 成功');
        } catch (e) {
          console.log('    ✗ 失败:', e.message);
        }
        await new Promise(r => setTimeout(r, 300));
      }
    }
    
    // 同步建议数据
    if (data.suggestions && data.suggestions.length > 0) {
      console.log('\n同步 suggestions:');
      for (let i = 0; i < data.suggestions.length; i++) {
        const suggestion = data.suggestions[i];
        console.log(`  [${i + 1}/${data.suggestions.length}] 同步建议...`);
        try {
          await syncSingleRecord('suggestions', suggestion);
          console.log('    ✓ 成功');
        } catch (e) {
          console.log('    ✗ 失败:', e.message);
        }
        await new Promise(r => setTimeout(r, 300));
      }
    }
    
    console.log('\n数据同步完成！');
  } catch (e) {
    console.error('失败:', e.message);
  }
}

main();