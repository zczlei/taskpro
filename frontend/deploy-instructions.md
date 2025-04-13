# 任务管理系统前端部署说明

## 部署准备

1. 确保服务器已安装 Nginx 或其他 Web 服务器
2. 将 `taskpro-frontend.tar.gz` 文件上传到服务器

## 部署步骤

### 解压文件

```bash
# 创建部署目录
mkdir -p /var/www/taskpro

# 解压前端文件
tar -zxvf taskpro-frontend.tar.gz -C /var/www/taskpro
```

### 配置 Nginx

创建 Nginx 配置文件 `/etc/nginx/sites-available/taskpro.conf`：

```nginx
server {
    listen 80;
    server_name your-domain.com;  # 替换为您的域名

    root /var/www/taskpro/dist;
    index index.html;

    location / {
        try_files $uri $uri/ /index.html;
    }

    # 代理后端 API 请求
    location /api {
        proxy_pass http://localhost:8080;  # 替换为后端服务的地址和端口
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
}
```

### 启用站点并重启 Nginx

```bash
# 创建符号链接到 sites-enabled 目录
ln -s /etc/nginx/sites-available/taskpro.conf /etc/nginx/sites-enabled/

# 检查 Nginx 配置是否有效
nginx -t

# 重启 Nginx
systemctl restart nginx
```

## 常见问题排查

1. **页面无法访问**：检查 Nginx 是否正常运行 `systemctl status nginx`
2. **API 请求失败**：确认后端服务是否运行，检查代理配置是否正确
3. **页面显示但功能不正常**：检查浏览器控制台错误，可能是 API 访问路径问题

## 更新部署

更新前端时，只需替换 `/var/www/taskpro/dist` 目录中的文件即可：

```bash
# 解压新版本
tar -zxvf new-taskpro-frontend.tar.gz -C /tmp

# 替换文件
rm -rf /var/www/taskpro/dist
mv /tmp/dist /var/www/taskpro/
``` 