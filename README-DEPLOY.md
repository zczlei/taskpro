# 任务管理系统部署指南

## 部署文件说明

部署包 `taskpro-deploy.tar.gz` 包含以下文件：

1. `taskpro-frontend.tar.gz` - 前端部署文件
2. `taskpro-backend.jar` - 后端Spring Boot应用
3. `deploy-instructions.md` - 前端部署详细说明
4. `backend-deploy-instructions.md` - 后端部署详细说明

## 快速部署指南

### 1. 解压部署包

```bash
tar -zxvf taskpro-deploy.tar.gz
```

### 2. 部署后端服务

参考 `backend-deploy-instructions.md` 文件的详细说明部署后端服务。主要步骤包括：

1. 准备MySQL数据库
2. 配置环境变量
3. 部署并启动Spring Boot应用
4. 将应用配置为系统服务（可选）

### 3. 部署前端应用

参考 `deploy-instructions.md` 文件的详细说明部署前端应用。主要步骤包括：

1. 解压前端文件到Web服务器目录
2. 配置Nginx（或其他Web服务器）
3. 配置反向代理将API请求转发到后端服务

### 4. 验证部署

1. 访问前端应用地址（例如：http://your-domain.com）
2. 使用默认管理员账号登录：
   - 用户名：admin
   - 密码：admin123

## 系统要求

- 服务器：
  - Linux操作系统（推荐Ubuntu 20.04或CentOS 8）
  - 最低配置：1核CPU，2GB内存，20GB存储空间
- 软件要求：
  - Java 11或更高版本
  - MySQL 5.7或更高版本
  - Nginx 1.18或更高版本（或其他Web服务器）

## 注意事项

1. 生产环境部署时，请修改默认的管理员密码
2. 确保数据库和上传目录定期备份
3. 配置HTTPS以提高系统安全性

如有任何部署问题，请参考各部署说明文件中的常见问题排查部分。 