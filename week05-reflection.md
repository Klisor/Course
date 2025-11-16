## 镜像优化思考

### 多阶段构建相比单阶段构建有什么优势？镜像大小减少了多少？

**多阶段构建优势：**
1. **分离构建和运行环境**：构建阶段包含完整的开发工具（Maven、JDK等），运行阶段只包含运行应用所需的最小环境（JRE）
2. **减小最终镜像体积**：避免将构建工具和中间文件打包到生产镜像中
3. **提高安全性**：生产环境不包含编译工具，减少攻击面
4. **优化层缓存**：依赖变更时只重新构建相关层，提高构建效率



### 如何进一步优化镜像大小？

**1. 使用 Alpine Linux 基础镜像**
```dockerfile
FROM eclipse-temurin:17-jre-alpine  # 约 180MB
# 对比标准镜像约减少 40-50%
```

**2. 分层缓存优化**
- 将不经常变化的层放在 Dockerfile 前面（如依赖安装）
- 频繁变化的层（如应用代码）放在后面
- 利用 Docker 构建缓存机制

**3. 优化 .dockerignore 文件**
```dockerignore
# 排除不必要的文件
.git
.gitignore
*.log
target/*.original
src/test/
.mvn/
```

**4. 应用层优化**
- 移除开发工具依赖（如 spring-boot-devtools）
- 排除不必要的依赖（如 H2 数据库移至测试范围）
- 使用 ProGuard 进行代码混淆和优化

**5. JVM 优化**
```dockerfile
ENV JAVA_OPTS="-Xmx128m -Xms64m -XX:+UseG1GC -Djava.security.egd=file:/dev/./urandom"
```

### 为什么要使用命名卷而不是绑定挂载来持久化数据库数据？

**命名卷优势：**
1. **更好的性能**：Docker 管理的卷通常比绑定挂载有更好的 I/O 性能
2. **可移植性**：不依赖主机文件系统路径，便于在不同环境部署
3. **备份和迁移**：Docker 提供标准的卷管理命令，便于备份和恢复
4. **安全性**：避免直接暴露主机文件系统给容器
5. **生命周期管理**：卷的生命周期与容器解耦，删除容器不会自动删除卷

**配置示例：**
```yaml
services:
  mysql:
    volumes:
      - mysql-data:/var/lib/mysql  # 命名卷
      # 对比绑定挂载: - ./data:/var/lib/mysql

volumes:
  mysql-data:
    driver: local
```

### 生产环境中应该注意哪些 Docker 安全问题？

**1. 最小权限原则**
```dockerfile
# 使用非 root 用户
RUN adduser -D appuser
USER appuser
```

**2. 镜像安全**
- 使用官方或可信的基础镜像
- 定期更新基础镜像和安全补丁
- 扫描镜像中的安全漏洞

**3. 网络安全**
```yaml
# 限制网络暴露
services:
  app:
    ports:
      - "8080:8080"  # 只暴露必要端口
    networks:
      - backend  # 使用内部网络
```

**4. 资源限制**
```yaml
services:
  app:
    deploy:
      resources:
        limits:
          memory: 512M
          cpus: '1.0'
```

**5. 安全配置**
- 设置容器为只读文件系统（如可能）
- 禁用不必要的内核功能
- 使用安全配置选项

### 如果需要初始化数据库数据，应该如何在容器启动时自动执行？

**方案一：使用 Docker Entrypoint 脚本**
```sql
-- init.sql
CREATE TABLE IF NOT EXISTS courses (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(50) NOT NULL UNIQUE,
    title VARCHAR(255) NOT NULL
);
```

```dockerfile
# Dockerfile
COPY init.sql /docker-entrypoint-initdb.d/
```

**方案二：Spring Boot 自动初始化**
```yaml
# application.yml
spring:
  sql:
    init:
      mode: always
      schema-locations: classpath:schema.sql
      data-locations: classpath:data.sql
```

**方案三：自定义启动脚本**
```dockerfile
# 在 Dockerfile 中添加初始化逻辑
COPY init-db.sh /usr/local/bin/
RUN chmod +x /usr/local/bin/init-db.sh
CMD ["/usr/local/bin/init-db.sh"]
```

**方案四：使用数据库迁移工具**
```xml
<!-- pom.xml -->
<dependency>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-core</artifactId>
</dependency>
```

**最佳实践：**
1. **幂等性**：初始化脚本应该可以重复执行而不产生副作用
2. **健康检查**：确保数据库完全启动后再执行初始化
3. **版本控制**：使用 Flyway 或 Liquibase 管理数据库版本
4. **环境区分**：开发环境可自动初始化，生产环境需谨慎

