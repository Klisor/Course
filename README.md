# 课程选课系统

## 项目说明

这是一个基于 Spring Boot 开发的课程选课系统，提供完整的课程管理、学生管理和选课管理功能。系统采用 RESTful API 设计，支持学生选课、课程容量控制、重复选课检查等业务功能。

### 主要功能
- **课程管理**：创建、查询、更新、删除课程
- **学生管理**：创建、查询、更新、删除学生
- **选课管理**：学生选课、取消选课、查询选课记录
- **业务规则**：课程容量限制、重复选课检查、数据验证

### 技术栈
- **后端框架**：Spring Boot
- **数据存储**：内存存储（ConcurrentHashMap）
- **API 风格**：RESTful
- **构建工具**：Maven

## 如何运行项目

### 环境要求
- Java 17 或更高版本
- Maven 3.6 或更高版本

### 运行步骤

1. **克隆项目**
   ```bash
   git clone <项目地址>
   cd course-enrollment-system
   ```

2. **构建项目**
   ```bash
   mvn clean package
   ```

3. **运行项目**
   ```bash
   mvn spring-boot:run
   ```

4. **访问应用**
   ```
   应用将在 http://localhost:8080 启动
   API 基础路径：http://localhost:8080/api
   ```

### 项目结构
```
src/main/java/com/zjsu/nsq/course/
├── controller/     # 控制器层
├── model/          # 数据模型
├── repository/     # 数据访问层
├── service/        # 业务逻辑层
└── CourseApplication.java  # 应用启动类
```

## API 接口列表

### 课程管理接口

#### 获取所有课程
- **URL**: `GET /api/courses`
- **响应**: 
  ```json
  {
    "code": 200,
    "message": "Success",
    "data": [
      {
        "id": "课程ID",
        "code": "课程代码",
        "title": "课程名称",
        "instructor": { ... },
        "schedule": { ... },
        "capacity": 容量,
        "enrolled": 已选人数
      }
    ]
  }
  ```

#### 根据ID获取课程
- **URL**: `GET /api/courses/{id}`
- **响应**: 返回指定课程信息

#### 创建课程
- **URL**: `POST /api/courses`
- **请求体**:
  ```json
  {
    "code": "CS101",
    "title": "计算机科学导论",
    "instructor": {
      "id": "T001",
      "name": "张教授",
      "email": "zhang@example.edu.cn"
    },
    "schedule": {
      "dayOfWeek": "MONDAY",
      "startTime": "08:00",
      "endTime": "10:00",
      "expectedAttendance": 50
    },
    "capacity": 60
  }
  ```
- **响应**: 201 Created

#### 更新课程
- **URL**: `PUT /api/courses/{id}`
- **响应**: 200 OK

#### 删除课程
- **URL**: `DELETE /api/courses/{id}`
- **响应**: 200 OK

### 学生管理接口

#### 获取所有学生
- **URL**: `GET /api/students`
- **响应**: 返回学生列表

#### 根据ID获取学生
- **URL**: `GET /api/students/{id}`
- **响应**: 返回指定学生信息

#### 创建学生
- **URL**: `POST /api/students`
- **请求体**:
  ```json
  {
    "studentId": "S2024001",
    "name": "张三",
    "major": "计算机科学",
    "grade": 1,
    "email": "zhangsan@example.com"
  }
  ```
- **响应**: 201 Created

#### 更新学生
- **URL**: `PUT /api/students/{id}`
- **响应**: 200 OK

#### 删除学生
- **URL**: `DELETE /api/students/{id}`
- **响应**: 200 OK（如果学生有选课记录返回409）

### 选课管理接口

#### 获取所有选课记录
- **URL**: `GET /api/enrollments`
- **响应**: 返回选课记录列表

#### 根据课程获取选课记录
- **URL**: `GET /api/enrollments/course/{courseId}`
- **响应**: 返回指定课程的选课记录

#### 根据学生获取选课记录
- **URL**: `GET /api/enrollments/student/{studentId}`
- **响应**: 返回指定学生的选课记录

#### 学生选课
- **URL**: `POST /api/enrollments`
- **请求体**:
  ```json
  {
    "studentId": "S001",
    "courseId": "课程ID"
  }
  ```
- **响应**: 201 Created

#### 取消选课
- **URL**: `DELETE /api/enrollments/{id}`
- **响应**: 200 OK

## 测试说明

### 测试工具
- **API 测试**: Apifox
- **测试文件**: 包含在项目中的测试用例集合

### 测试场景

#### 场景1：完整的课程管理流程
1. 创建3门不同的课程
2. 查询所有课程，验证返回3条记录
3. 根据ID查询某门课程
4. 更新该课程的信息
5. 删除该课程
6. 再次查询，验证返回404

#### 场景2：选课业务流程
1. 创建一门容量为2的课程
2. 学生S001选课，验证成功
3. 学生S002选课，验证成功
4. 学生S003选课，验证失败（容量已满）
5. 学生S001再次选课，验证失败（重复选课）
6. 查询课程，验证enrolled字段为2

#### 场景3：学生管理流程
1. 创建3个不同学号的学生
2. 查询所有学生，验证返回3条记录
3. 根据ID查询某个学生，验证返回正确信息
4. 更新该学生的专业和邮箱信息，验证更新成功
5. 尝试让一个不存在的学生选课，验证返回404错误
6. 让学生S2024001选课，然后尝试删除该学生，验证返回错误（存在选课记录）
7. 删除没有选课记录的学生S2024003，验证删除成功

#### 场景4：错误处理
1. 查询不存在的课程ID，验证返回404
2. 创建课程时缺少必填字段，验证返回400
3. 选课时提供不存在的课程ID，验证返回404
4. 创建学生时使用重复的studentId，验证返回错误
5. 创建学生时使用无效的邮箱格式，验证返回错误

### 运行测试

1. **导入测试集合**
   - 在Apifox中导入提供的测试用例JSON文件
   - 设置环境变量（基础URL等）

2. **执行测试**
   - 选择相应的测试场景
   - 运行测试并查看结果
   - 检查断言和响应状态码

### 测试数据说明
- 系统使用内存存储，重启后数据会丢失
- 测试前确保相关数据已创建
- 使用变量提取确保测试流程的连贯性

### 业务规则验证
- ✅ 课程容量限制
- ✅ 重复选课检查
- ✅ 课程存在性检查
- ✅ 学生存在性验证
- ✅ 级联更新（选课后自动更新课程enrolled计数）
- ✅ 邮箱格式验证
- ✅ 数据完整性约束

## 注意事项

1. **数据持久化**: 当前使用内存存储，重启应用后所有数据将丢失

2. **并发处理**: 系统使用ConcurrentHashMap支持基本并发访问

3. **错误处理**: 统一的错误响应格式和HTTP状态码

4. **API文档**: 所有API遵循RESTful设计原则

   

## Docker 部署

### 环境要求
- Docker 20.10+
- Docker Compose 2.0+
- 至少 2GB 可用内存

### 一键部署
```bash
# 克隆项目
git clone <项目地址>
cd course

# 构建并启动所有服务
docker-compose up -d --build

# 查看服务状态
docker-compose ps
```

## 详细部署步骤

### 1. 构建镜像
```bash
# 构建应用镜像
docker build -t course-app:latest .

# 或者使用 Docker Compose 构建
docker-compose build
```

### 2. 启动服务
```bash
# 启动所有服务（后台运行）
docker-compose up -d

# 启动特定服务
docker-compose up -d app
docker-compose up -d mysql

# 查看运行状态
docker-compose ps
```

### 3. 服务验证
```bash
# 检查应用健康状态
curl http://localhost:8080/actuator/health

# 测试课程API
curl http://localhost:8080/api/courses

# 测试学生API  
curl http://localhost:8080/api/students
```

## 服务配置

### 服务架构
```
coursehub-app (Spring Boot) :8080
          ↓
coursehub-mysql (MySQL) :3306
```

### 端口映射
- **应用服务**: 8080 → 8080
- **数据库服务**: 3306 → 3306

### 环境变量
```yaml
# 应用环境变量
SPRING_PROFILES_ACTIVE: docker
SPRING_DATASOURCE_URL: jdbc:mysql://mysql:3306/course_db
SPRING_DATASOURCE_USERNAME: Klisor
SPRING_DATASOURCE_PASSWORD: 123456

# 数据库环境变量
MYSQL_ROOT_PASSWORD: 123456
MYSQL_DATABASE: course_db
MYSQL_USER: Klisor
MYSQL_PASSWORD: 123456
```

## 运维管理

### 查看日志
```bash
# 查看所有服务日志
docker-compose logs

# 查看特定服务日志
docker-compose logs app
docker-compose logs mysql

# 实时查看日志
docker-compose logs -f app

# 查看最近日志
docker-compose logs --tail=100 app
```

### 服务管理
```bash
# 停止服务
docker-compose down

# 重启服务
docker-compose restart

# 重启特定服务
docker-compose restart app

# 查看服务状态
docker-compose ps

# 查看资源使用
docker-compose top
```

### 数据管理
```bash
# 备份数据库
docker exec coursehub-mysql mysqldump -u root -p123456 course_db > backup.sql

# 恢复数据库
docker exec -i coursehub-mysql mysql -u root -p123456 course_db < backup.sql

# 进入数据库控制台
docker exec -it coursehub-mysql mysql -u Klisor -p123456 course_db
```

## 故障排查

### 常见问题解决

**1. 端口冲突**
```bash
# 检查端口占用
netstat -an | grep 8080
netstat -an | grep 3306

# 修改 docker-compose.yml 中的端口映射
ports:
  - "8081:8080"  # 主机端口:容器端口
```

**2. 容器启动失败**
```bash
# 查看详细日志
docker-compose logs --tail=50 app

# 检查容器状态
docker-compose ps -a

# 重新构建镜像
docker-compose build --no-cache app
```

**3. 数据库连接问题**
```bash
# 测试数据库连接
docker exec coursehub-mysql mysql -u Klisor -p123456 -e "SHOW DATABASES;"

# 检查网络连通性
docker exec coursehub-app ping mysql
docker exec coursehub-app nc -z mysql 3306
```

**4. 内存不足**
```bash
# 查看系统资源
docker system df
docker stats

# 清理无用资源
docker system prune
```

### 性能监控
```bash
# 查看容器资源使用
docker stats

# 查看镜像大小
docker images course-app

# 分析镜像层次
docker history course-app:latest
```

## 开发调试

### 开发模式
```bash
# 使用开发配置启动
docker-compose -f docker-compose.yml -f docker-compose.dev.yml up -d

# 查看实时日志
docker-compose logs -f app
```

### 进入容器调试
```bash
# 进入应用容器
docker exec -it coursehub-app sh

# 进入数据库容器
docker exec -it coursehub-mysql bash

# 在容器内执行命令
docker exec coursehub-app java -version
```

## 生产部署建议

### 安全配置
```yaml
# 使用强密码
environment:
  - MYSQL_ROOT_PASSWORD=${DB_ROOT_PASSWORD}
  - MYSQL_PASSWORD=${DB_PASSWORD}

# 限制资源
deploy:
  resources:
    limits:
      memory: 512M
      cpus: '1.0'
```

### 备份策略
```bash
# 定时备份脚本
#!/bin/bash
docker exec coursehub-mysql mysqldump -u root -p$DB_PASSWORD course_db > /backups/backup_$(date +%Y%m%d_%H%M%S).sql
```

## 扩展配置

### 自定义配置
创建 `.env` 文件覆盖默认配置：
```env
DB_PASSWORD=your_secure_password
APP_PORT=8080
```

### 网络配置
```bash
# 查看网络详情
docker network inspect course_coursehub-network

# 创建自定义网络
docker network create course-network
```

---

## 技术支持

遇到问题时请按以下步骤排查：
1. 查看服务日志：`docker-compose logs`
2. 检查容器状态：`docker-compose ps`  
3. 验证网络连通性：`docker exec coursehub-app nc -z mysql 3306`
4. 检查资源使用：`docker stats`
