const mysql = require('mysql2/promise');

async function main() {
  const connection = await mysql.createConnection({
    host: 'localhost',
    user: 'root',
    password: '123520',
    database: 'practice_management'
  });

  console.log('数据库连接成功');

  // 插入用户数据
  const users = [
    {
      username: 'admin',
      password: '$2a$10$E1QywinleOjTMic1tm7pEu9.7SCxlJUfCTpZMjbpqTP4xYv1tmL2i', // 123456
      real_name: '系统管理员',
      role: 'ADMIN',
      status: 1
    },
    {
      username: 'teacher1',
      password: '$2a$10$E1QywinleOjTMic1tm7pEu9.7SCxlJUfCTpZMjbpqTP4xYv1tmL2i', // 123456
      real_name: '李老师',
      role: 'TEACHER',
      status: 1
    },
    {
      username: 'student1',
      password: '$2a$10$E1QywinleOjTMic1tm7pEu9.7SCxlJUfCTpZMjbpqTP4xYv1tmL2i', // 123456
      real_name: '张三',
      student_id: '2024001',
      role: 'STUDENT',
      status: 1
    },
    {
      username: 'student2',
      password: '$2a$10$E1QywinleOjTMic1tm7pEu9.7SCxlJUfCTpZMjbpqTP4xYv1tmL2i', // 123456
      real_name: '李四',
      student_id: '2024002',
      role: 'STUDENT',
      status: 1
    }
  ];

  for (const user of users) {
    try {
      const [result] = await connection.execute(
        `INSERT INTO sys_user (username, password, real_name, student_id, role, status, create_time, update_time) 
         VALUES (?, ?, ?, ?, ?, ?, NOW(), NOW())`,
        [user.username, user.password, user.real_name, user.student_id, user.role, user.status]
      );
      console.log(`插入用户 ${user.username} 成功，ID: ${result.insertId}`);
    } catch (error) {
      console.error(`插入用户 ${user.username} 失败:`, error.message);
    }
  }

  // 查看插入后的用户数据
  const [insertedUsers] = await connection.execute('SELECT * FROM sys_user');
  console.log('\n插入后的用户数据:');
  console.table(insertedUsers);

  await connection.end();
}

main().catch(console.error);
