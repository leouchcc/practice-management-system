const http = require('http');

function makeRequest(options, data) {
  return new Promise((resolve, reject) => {
    const req = http.request(options, (res) => {
      let body = '';
      res.on('data', (chunk) => { body += chunk; });
      res.on('end', () => {
        try {
          resolve(JSON.parse(body));
        } catch (e) {
          resolve(body);
        }
      });
    });
    req.on('error', reject);
    if (data) req.write(data);
    req.end();
  });
}

async function testLogin() {
  console.log('=== 测试登录功能 ===');
  
  const loginData = JSON.stringify({ username: 'root', password: '123520' });
  const loginOptions = {
    hostname: 'localhost',
    port: 8080,
    path: '/api/auth/login',
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'Content-Length': loginData.length
    }
  };
  
  try {
    const loginResult = await makeRequest(loginOptions, loginData);
    console.log('登录测试结果:', JSON.stringify(loginResult, null, 2));
    
    if (loginResult.code === 200 && loginResult.data && loginResult.data.token) {
      console.log('✓ 登录成功');
      return loginResult.data.token;
    } else {
      console.log('✗ 登录失败');
      return null;
    }
  } catch (error) {
    console.error('登录测试错误:', error.message);
    return null;
  }
}

async function testActivityList(token) {
  console.log('\n=== 测试活动列表功能 ===');
  
  const activityOptions = {
    hostname: 'localhost',
    port: 8080,
    path: '/api/activity/list',
    method: 'GET',
    headers: {
      'Authorization': `Bearer ${token}`
    }
  };
  
  try {
    const activityResult = await makeRequest(activityOptions);
    console.log('活动列表测试结果:', JSON.stringify(activityResult, null, 2));
    console.log('✓ 活动列表获取成功');
  } catch (error) {
    console.error('活动列表测试错误:', error.message);
  }
}

async function testAnnouncementList(token) {
  console.log('\n=== 测试公告列表功能 ===');
  
  const announcementOptions = {
    hostname: 'localhost',
    port: 8080,
    path: '/api/announcement/list',
    method: 'GET',
    headers: {
      'Authorization': `Bearer ${token}`
    }
  };
  
  try {
    const announcementResult = await makeRequest(announcementOptions);
    console.log('公告列表测试结果:', JSON.stringify(announcementResult, null, 2));
    console.log('✓ 公告列表获取成功');
  } catch (error) {
    console.error('公告列表测试错误:', error.message);
  }
}

async function testCreditList(token) {
  console.log('\n=== 测试学分记录功能 ===');
  
  const creditOptions = {
    hostname: 'localhost',
    port: 8080,
    path: '/api/credit/list',
    method: 'GET',
    headers: {
      'Authorization': `Bearer ${token}`
    }
  };
  
  try {
    const creditResult = await makeRequest(creditOptions);
    console.log('学分记录测试结果:', JSON.stringify(creditResult, null, 2));
    console.log('✓ 学分记录获取成功');
  } catch (error) {
    console.error('学分记录测试错误:', error.message);
  }
}

async function main() {
  console.log('开始测试高校学生实践活动管理系统API...\n');
  
  const token = await testLogin();
  
  if (token) {
    await testActivityList(token);
    await testAnnouncementList(token);
    await testCreditList(token);
  }
  
  console.log('\n测试完成!');
}

main().catch(console.error);
