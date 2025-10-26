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

