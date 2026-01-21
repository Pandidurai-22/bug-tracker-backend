# 🚀 Backend Startup Guide

## Prerequisites

### 1. Java 17 or Higher
Check your Java version:
```bash
java -version
```
If you don't have Java 17+, download from:
- [Oracle JDK 17](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)
- [OpenJDK 17](https://adoptium.net/)

### 2. Maven 3.8+
Check Maven version:
```bash
mvn -version
```
If you don't have Maven, download from: [Maven Download](https://maven.apache.org/download.cgi)

### 3. PostgreSQL Database
✅ **Already Configured!** Your backend is connected to Render PostgreSQL.
- Database URL is already set in `application.properties`
- No local database setup needed

### 4. (Optional) AI Service
If you want AI features enabled:
- Start the AI service first (see `../bug-tracker-ai/`)
- Or disable AI by setting `ai.enabled=false` in `application.properties`

---

## 🎯 Quick Start (3 Steps)

### Step 1: Navigate to Backend Directory
```bash
cd bug-tracker-backend
```

### Step 2: Build the Project
```bash
mvn clean install
```
Or if you're on Windows and Maven wrapper is available:
```bash
.\mvnw.cmd clean install
```

### Step 3: Run the Application
**Option A: Using Maven**
```bash
mvn spring-boot:run
```

**Option B: Using Maven Wrapper (Windows)**
```bash
.\mvnw.cmd spring-boot:run
```

**Option C: Using Maven Wrapper (Linux/Mac)**
```bash
./mvnw spring-boot:run
```

**Option D: Run JAR directly (after building)**
```bash
java -jar target/backend-0.0.1-SNAPSHOT.jar
```

---

## ✅ Verify Backend is Running

### 1. Check Console Output
You should see:
```
Started BugTrackerBackendApplication in X.XXX seconds
```

### 2. Test Health Endpoint
Open browser or use curl:
```bash
# Browser
http://localhost:8080/api/test/all

# Or curl
curl http://localhost:8080/api/test/all
```

### 3. Check API Endpoints
- **Health Check**: `http://localhost:8080/api/test/all`
- **Auth Endpoints**: `http://localhost:8080/api/auth/signup` or `/signin`
- **Bugs API**: `http://localhost:8080/api/bugs`

---

## 🔧 Common Issues & Solutions

### Issue 1: Port 8080 Already in Use
**Error**: `Port 8080 is already in use`

**Solution**:
```bash
# Option 1: Change port in application.properties
server.port=8081

# Option 2: Kill process using port 8080
# Windows
netstat -ano | findstr :8080
taskkill /PID <PID> /F

# Linux/Mac
lsof -ti:8080 | xargs kill -9
```

### Issue 2: Database Connection Failed
**Error**: `Connection to database failed`

**Solution**:
1. Check if Render database is accessible
2. Verify database credentials in `application.properties`
3. Check internet connection (Render requires internet)

### Issue 3: Java Version Mismatch
**Error**: `Unsupported class file major version`

**Solution**:
- Ensure Java 17+ is installed
- Check JAVA_HOME environment variable
- Use: `java -version` to verify

### Issue 4: Maven Not Found
**Error**: `'mvn' is not recognized`

**Solution**:
- Install Maven and add to PATH
- Or use Maven wrapper: `.\mvnw.cmd` (Windows) or `./mvnw` (Linux/Mac)

### Issue 5: AI Service Connection Failed
**Error**: `AI service health check failed`

**Solution**:
- Start AI service first: `cd ../bug-tracker-ai && python main.py`
- Or disable AI: Set `ai.enabled=false` in `application.properties`

---

## 📋 Configuration Options

### Change Server Port
Edit `application.properties`:
```properties
server.port=8081
```

### Enable/Disable AI
Edit `application.properties`:
```properties
ai.enabled=true  # or false
ai.service.url=http://localhost:8000
```

### Change Database (if needed)
Edit `application.properties`:
```properties
spring.datasource.url=jdbc:postgresql://your-db-url
spring.datasource.username=your-username
spring.datasource.password=your-password
```

---

## 🎯 Development Mode

### Hot Reload (Auto-restart on code changes)
Spring Boot DevTools is already included! Just:
1. Make code changes
2. Save file
3. Application auto-restarts

### Debug Mode
Run with debug port:
```bash
mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005"
```

---

## 📊 Logs

### View Logs
Logs appear in console. For file logs, check:
- `logs/` directory (if configured)
- Console output

### Log Levels
Configure in `application.properties`:
```properties
logging.level.com.bugtracker=DEBUG
logging.level.org.springframework.web=INFO
```

---

## 🚀 Production Deployment

### Build JAR for Production
```bash
mvn clean package -DskipTests
```

### Run Production JAR
```bash
java -jar target/backend-0.0.1-SNAPSHOT.jar
```

### Environment Variables (for production)
Set these instead of hardcoding in `application.properties`:
- `SPRING_DATASOURCE_URL`
- `SPRING_DATASOURCE_USERNAME`
- `SPRING_DATASOURCE_PASSWORD`
- `BUGTRACKER_APP_JWTSECRET`
- `AI_SERVICE_URL`

---

## 📝 Quick Reference

| Command | Description |
|---------|-------------|
| `mvn clean install` | Build project |
| `mvn spring-boot:run` | Run application |
| `mvn test` | Run tests |
| `mvn clean package` | Build JAR file |
| `java -jar target/backend-0.0.1-SNAPSHOT.jar` | Run JAR |

---

## 🎉 Success Checklist

- [ ] Java 17+ installed
- [ ] Maven installed
- [ ] Database connection working
- [ ] Backend starts without errors
- [ ] Can access `http://localhost:8080/api/test/all`
- [ ] (Optional) AI service running if enabled

---

## 💡 Pro Tips

1. **Use Maven Wrapper**: Always use `mvnw` instead of `mvn` for consistency
2. **Check Logs**: First place to look when something goes wrong
3. **Test Database**: Verify database connection before starting
4. **Port Conflicts**: Always check if port 8080 is available
5. **Environment Variables**: Use them for sensitive data in production

---

**Need Help?** Check the main README.md or open an issue!

