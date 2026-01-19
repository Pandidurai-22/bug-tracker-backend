# Render Deployment Checklist

## ✅ Pre-Deployment Verification

### 1. Code Configuration ✅
- [x] Database connection configured in `application.properties`
- [x] CORS configured for frontend domain
- [x] AI service configuration added (optional)
- [x] Dockerfile updated (removed hardcoded credentials)

### 2. Frontend Configuration ✅
- [x] API URLs point to: `https://bug-tracker-backend-83mn.onrender.com/api`
- [x] Auth service URLs configured correctly
- [x] CORS credentials enabled

### 3. Database Configuration ✅
- [x] PostgreSQL database on Render: `bug_tracker_backend_b4uu`
- [x] SSL enabled
- [x] Connection pool configured

---

## 🔧 Render Environment Variables Setup

### Required Environment Variables

Set these in your Render dashboard under your backend service → Environment:

```
# Server Port (Render sets this automatically, but you can override)
PORT=8080

# CORS Configuration
SPRING_MVC_CORS_ALLOWED_ORIGINS=http://localhost:3000,https://bugtrackerclient-mu.vercel.app
SPRING_MVC_CORS_ALLOWED_METHODS=GET,POST,PUT,DELETE,OPTIONS
```

### Optional Environment Variables (if you want to use env vars instead of hardcoded values)

```
# Database (currently hardcoded in application.properties, optional to override)
SPRING_DATASOURCE_URL=jdbc:postgresql://dpg-d4nb4ekhg0os73cj3pqg-a.singapore-postgres.render.com:5432/bug_tracker_backend_b4uu
SPRING_DATASOURCE_USERNAME=bug_tracker_backend_b4uu_user
SPRING_DATASOURCE_PASSWORD=w2d6FauEowPky7o1PGArzH2XL4yJDJfJ
```

### AI Service Configuration (if enabling AI features)

```
# Enable AI Service
AI_ENABLED=true
AI_SERVICE_URL=https://bug-tracker-ai.onrender.com
AI_SERVICE_TIMEOUT=10000
```

**Note:** Currently AI is disabled (`ai.enabled=false`). To enable:
1. Set `AI_ENABLED=true` in Render environment variables
2. Set `AI_SERVICE_URL` to your AI service Render URL
3. Deploy your AI service first and get its URL

---

## 📋 Deployment Steps

### Step 1: Push Code to Repository
```bash
cd bug-tracker-backend
git add .
git commit -m "Prepare for Render deployment"
git push origin main
```

### Step 2: Connect Repository to Render
1. Go to Render Dashboard
2. Click "New" → "Web Service"
3. Connect your repository
4. Select the `bug-tracker-backend` directory

### Step 3: Configure Build Settings
- **Build Command:** `mvn clean package -DskipTests`
- **Start Command:** `java -jar target/*.jar` (or use Dockerfile)
- **Dockerfile Path:** `Dockerfile` (if using Docker)

### Step 4: Set Environment Variables
Add the environment variables listed above in the Render dashboard.

### Step 5: Deploy
1. Click "Create Web Service"
2. Wait for build to complete
3. Check logs for any errors

---

## 🧪 Post-Deployment Testing

### Test Backend Health
```bash
# Check if backend is running
curl https://bug-tracker-backend-83mn.onrender.com/api/bugs

# Should return JSON array (may be empty if no bugs)
```

### Test Authentication
```bash
# Test registration
curl -X POST https://bug-tracker-backend-83mn.onrender.com/api/auth/signup \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","email":"test@example.com","password":"testpass123","role":["user"]}'

# Test login
curl -X POST https://bug-tracker-backend-83mn.onrender.com/api/auth/signin \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"testpass123"}'
```

### Test Frontend Connection
1. Open frontend: `https://bugtrackerclient-mu.vercel.app`
2. Try to login/register
3. Create a bug
4. Check if bugs are fetched correctly

---

## 🐛 Troubleshooting

### Issue: Backend won't start
**Check:**
- Database connection string is correct
- Database is accessible from Render
- Environment variables are set correctly
- Check Render logs for errors

### Issue: CORS errors in browser
**Fix:**
- Verify `SPRING_MVC_CORS_ALLOWED_ORIGINS` includes your frontend URL
- Check that frontend is using correct backend URL
- Ensure `withCredentials: true` is set in axios requests

### Issue: Database connection fails
**Fix:**
- Verify database credentials in `application.properties`
- Check database is running and accessible
- Verify SSL settings are correct
- Check Render database connection string format

### Issue: AI endpoints return 404
**Fix:**
- AI is currently disabled by default
- Set `AI_ENABLED=true` in environment variables
- Verify AI service URL is correct
- Check AI service is deployed and running

---

## 📊 Current Configuration Summary

### Backend URL
- **Production:** `https://bug-tracker-backend-83mn.onrender.com`
- **API Base:** `https://bug-tracker-backend-83mn.onrender.com/api`

### Frontend URL
- **Production:** `https://bugtrackerclient-mu.vercel.app`

### Database
- **Host:** `dpg-d4nb4ekhg0os73cj3pqg-a.singapore-postgres.render.com`
- **Database:** `bug_tracker_backend_b4uu`
- **SSL:** Enabled

### AI Service
- **Status:** Disabled (`ai.enabled=false`)
- **URL:** Not configured (defaults to localhost)

---

## ✅ Ready to Deploy!

All configurations are verified and ready. The backend should deploy successfully to Render.

**Last Updated:** January 19, 2026

