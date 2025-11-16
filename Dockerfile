# 第一阶段：构建阶段
FROM m.daocloud.io/docker.io/maven:3.9-eclipse-temurin-17 AS builder

WORKDIR /app
COPY pom.xml .
# 下载依赖
RUN mvn dependency:copy-dependencies -DoutputDirectory=target/lib

COPY src ./src
RUN mvn clean package -DskipTests

# 第二阶段：运行阶段
FROM m.daocloud.io/docker.io/eclipse-temurin:17-jre-alpine

RUN adduser -D appuser
WORKDIR /app

# 只复制必要的文件
COPY --from=builder /app/target/nsq-course-0.0.1-SNAPSHOT.jar app.jar

# 有效清理：删除JVM中不必要的大文件
RUN find /opt/java/openjdk -name "src.zip" -exec rm -f {} \; && \
    find /opt/java/openjdk -name "*.debuginfo" -exec rm -f {} \; && \
    find /opt/java/openjdk -name "*.diz" -exec rm -f {} \; && \
    # 清理文档和示例
    find /opt/java/openjdk -name "*.txt" -exec rm -f {} \; && \
    find /opt/java/openjdk -name "LICENSE" -exec rm -f {} \; && \
    # 清理缓存
    rm -rf /var/cache/apk/* /tmp/* /var/tmp/*

RUN chown appuser:appuser app.jar
USER appuser

EXPOSE 8080
CMD ["java", "-jar", "app.jar"]