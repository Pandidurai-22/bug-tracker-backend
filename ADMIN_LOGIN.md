# 🔐 Admin Login Guide

## Default Admin Credentials

After starting the backend server, a default admin user is automatically created:

- **Username**: `admin`
- **Password**: `admin123`
- **Email**: `admin@bugtracker.com`
- **Role**: `ROLE_ADMIN`

---

## How to Login as Admin

### Option 1: Using the Login Page

1. Navigate to `/login` in your browser
2. Enter credentials:
   - Username: `admin`
   - Password: `admin123`
3. Click "Sign in"
4. You'll be redirected to the dashboard
5. Click "Admin" in the navbar to access the admin dashboard

### Option 2: Quick Test Login Button

1. Go to `/login` page
2. Click "Quick Test Login (Demo)" button
   - This logs in as `testing/testing123` (regular user)
3. For admin, manually enter:
   - Username: `admin`
   - Password: `admin123`

---

## Creating Additional Admin Users

### Method 1: Register via API

```bash
curl -X POST http://localhost:8080/api/auth/signup \
  -H "Content-Type: application/json" \
  -d '{
    "username": "newadmin",
    "email": "newadmin@bugtracker.com",
    "password": "yourpassword",
    "role": ["admin"]
  }'
```

### Method 2: Register via Frontend

1. Go to `/register` page
2. Fill in the form
3. **Note**: Frontend currently doesn't allow selecting roles during registration
4. After registration, you'll need to manually update the user role in the database

### Method 3: Update Existing User to Admin (Database)

```sql
-- Connect to your PostgreSQL database
-- Find the user ID
SELECT id, username FROM users WHERE username = 'yourusername';

-- Add admin role (replace USER_ID with actual ID)
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u, roles r
WHERE u.username = 'yourusername' 
AND r.name = 'ROLE_ADMIN'
AND NOT EXISTS (
  SELECT 1 FROM user_roles ur 
  WHERE ur.user_id = u.id AND ur.role_id = r.id
);
```

---

## Verify Admin Access

After logging in as admin:

1. Check navbar - you should see "Admin" link
2. Navigate to `/admin` - should see admin dashboard
3. Check browser console - user object should have `ROLE_ADMIN` in roles

---

## Troubleshooting

### Admin link not showing?

1. **Check user roles**: Open browser console and check:
   ```javascript
   JSON.parse(localStorage.getItem('user'))
   ```
   Look for `roles` array containing `"ROLE_ADMIN"`

2. **Check backend logs**: When you login, backend should show roles in JWT response

3. **Verify user exists**: Check database:
   ```sql
   SELECT u.username, r.name as role
   FROM users u
   JOIN user_roles ur ON u.id = ur.user_id
   JOIN roles r ON ur.role_id = r.id
   WHERE u.username = 'admin';
   ```

### Can't login?

1. **Check backend is running**: `http://localhost:8080/api/auth/signin`
2. **Check credentials**: Username is case-sensitive
3. **Check database**: User should exist with correct password hash

---

## Security Note

⚠️ **Important**: Change the default admin password in production!

1. Login as admin
2. Go to admin dashboard → Settings
3. Change password (if password change feature is implemented)
4. Or update directly in database (hash the new password first)

---

## Quick Reference

| Username | Password | Role | Access |
|----------|----------|------|--------|
| `admin` | `admin123` | ADMIN | Full admin access |
| `testing` | `testing123` | USER | Regular user access |

---

**Default admin user is created automatically when backend starts!** 🚀

