# 任务管理系统后端部署说明

## 部署准备

1. 确保服务器已安装 Java 11 或更高版本
2. 确保服务器已安装 MySQL 数据库
3. 将 `target/prj-0.0.1-SNAPSHOT.jar` 文件上传到服务器

## 部署步骤

### 1. 准备数据库

```sql
CREATE DATABASE taskpro CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER 'taskpro'@'localhost' IDENTIFIED BY 'your_secure_password';
GRANT ALL PRIVILEGES ON taskpro.* TO 'taskpro'@'localhost';
FLUSH PRIVILEGES;
```

### 2. 配置环境变量

创建一个环境变量配置文件 `application-env.properties`：

```properties
# 数据库连接配置
DB_HOST=localhost
DB_PORT=3306
DB_NAME=taskpro
DB_USERNAME=taskpro
DB_PASSWORD=your_secure_password

# JWT配置
JWT_SECRET=your_jwt_secret_key
JWT_EXPIRATION=86400000

# 文件上传配置
UPLOAD_DIR=/path/to/uploads
```

### 3. 启动应用

```bash
# 创建上传目录
mkdir -p /path/to/uploads

# 使用环境变量启动应用
java -jar prj-0.0.1-SNAPSHOT.jar --spring.config.location=file:/path/to/application-env.properties
```

### 4. 配置为系统服务（可选）

创建系统服务文件 `/etc/systemd/system/taskpro.service`：

```ini
[Unit]
Description=TaskPro Management System
After=network.target mysql.service

[Service]
User=your_service_user
WorkingDirectory=/path/to/application
ExecStart=/usr/bin/java -jar prj-0.0.1-SNAPSHOT.jar --spring.config.location=file:/path/to/application-env.properties
SuccessExitStatus=143
TimeoutStopSec=10
Restart=on-failure
RestartSec=5

[Install]
WantedBy=multi-user.target
```

启用并启动服务：

```bash
systemctl daemon-reload
systemctl enable taskpro.service
systemctl start taskpro.service
```

## 常见问题排查

1. **数据库连接失败**：检查数据库服务是否运行，以及连接配置是否正确
2. **应用无法启动**：检查日志文件 `/path/to/application/logs/application.log`
3. **文件上传失败**：确保上传目录存在且有正确的读写权限

## 更新部署

更新后端时，只需替换 JAR 文件并重启服务：

```bash
# 停止服务
systemctl stop taskpro.service

# 替换 JAR 文件
cp new-prj-0.0.1-SNAPSHOT.jar /path/to/application/prj-0.0.1-SNAPSHOT.jar

# 启动服务
systemctl start taskpro.service
``` 