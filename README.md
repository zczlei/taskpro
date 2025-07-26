# 需求管理系统

这是一个基于Spring Boot + Vue.js的需求管理系统。

## 技术栈

### 后端
- Spring Boot 2.7.0
- Spring Data JPA
- MySQL 8.0
- Maven

### 前端
- Vue.js
- TypeScript
- Vite
- Element Plus

## 开发环境设置

1. 克隆项目
```bash
git clone [repository-url]
```

2. 配置数据库
- 复制 `src/main/resources/application.properties.example` 到 `src/main/resources/application.properties`
- 修改数据库配置信息

3. 启动后端
```bash
./mvnw spring-boot:run
```

4. 启动前端
```bash
cd frontend
npm install
npm run dev
```

## 项目结构
```
.
├── src/                    # 后端源代码
│   ├── main/
│   │   ├── java/          # Java源代码
│   │   └── resources/     # 配置文件
│   └── test/              # 测试代码
├── frontend/              # 前端项目
│   ├── src/              # 前端源代码
│   ├── public/           # 静态资源
│   └── package.json      # 前端依赖配置
└── pom.xml               # 后端依赖配置
```

## 部署说明

1. 后端部署
- 确保已安装JDK 8或以上版本
- 配置application.properties
- 执行 `./mvnw clean package` 打包
- 运行 `java -jar target/demo-0.0.1-SNAPSHOT.jar`

2. 前端部署
- 执行 `npm run build` 构建
- 将dist目录下的文件部署到Web服务器

## 注意事项
- 请勿将敏感信息（如数据库密码）提交到代码仓库

## [常用工具站-图片GIF](https://goodmorningmeme.com)

