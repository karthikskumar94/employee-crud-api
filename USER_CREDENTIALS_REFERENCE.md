# 🔐 **User Credentials Quick Reference**

## 👥 **Test User Accounts**

### **🔑 Login Credentials:**

| 👤 **Username** | 🔐 **Password** | 🏷️ **Role(s)** | 🎯 **Access Level** |
|----------------|-----------------|-----------------|---------------------|
| `admin` | `password` | ADMIN | 👑 **Full Access** - Everything |
| `hr_manager` | `password` | HR | 🏢 **HR Operations** - No Delete |
| `manager` | `password` | MANAGER | 👔 **Read Only** - No CRUD |
| `employee` | `password` | EMPLOYEE | 👤 **Limited** - Individual records only |
| `hr_admin` | `password` | HR + ADMIN | 🎖️ **Multi-Role** - Full Access |
| `team_lead` | `password` | MANAGER + EMPLOYEE | 🎗️ **Multi-Role** - Manager + Employee |

---

## 🎯 **Permission Matrix**

### **📊 Read Operations**
| Endpoint | ADMIN | HR | MANAGER | EMPLOYEE |
|----------|-------|----|---------|---------| 
| `GET /api/employees` | ✅ | ✅ | ✅ | ❌ |
| `GET /api/employees/{id}` | ✅ | ✅ | ✅ | ✅ |
| `GET /api/employees/email/{email}` | ✅ | ✅ | ✅ | ❌ |
| `GET /api/employees/search?name=X` | ✅ | ✅ | ✅ | ❌ |
| `GET /api/employees/department/X` | ✅ | ✅ | ✅ | ❌ |

### **📝 Write Operations**
| Endpoint | ADMIN | HR | MANAGER | EMPLOYEE |
|----------|-------|----|---------|---------| 
| `POST /api/employees` | ✅ | ✅ | ❌ | ❌ |
| `PUT /api/employees/{id}` | ✅ | ✅ | ❌ | ❌ |
| `DELETE /api/employees/{id}` | ✅ | ❌ | ❌ | ❌ |

### **📊 Statistics & Utility**
| Endpoint | ADMIN | HR | MANAGER | EMPLOYEE |
|----------|-------|----|---------|---------| 
| `GET /api/employees/count` | ✅ | ✅ | ❌ | ❌ |
| `GET /api/employees/health` | ✅ | ✅ | ✅ | ✅ |

---

## 🚀 **Quick Test Commands**

### **Login Examples:**
```bash
# Admin Login
POST /auth/login
{"username": "admin", "password": "password"}

# HR Login  
POST /auth/login
{"username": "hr_manager", "password": "password"}

# Manager Login
POST /auth/login
{"username": "manager", "password": "password"}

# Employee Login
POST /auth/login
{"username": "employee", "password": "password"}
```

### **Expected Login Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "username": "admin",
  "fullName": "System Administrator", 
  "roles": ["ADMIN"],
  "message": "Authentication successful"
}
```

---

## 🎪 **Demo Scenarios**

### **🔴 Scenario 1: Admin Power** 
- Login: `admin` / `password`
- Try: Create → Update → Delete → View Stats
- **Result**: ✅ All operations work

### **🟡 Scenario 2: HR Restrictions**
- Login: `hr_manager` / `password`  
- Try: Create ✅ → Update ✅ → Delete ❌
- **Result**: ⚠️ Delete fails with 403 Forbidden

### **🟢 Scenario 3: Manager Read-Only**
- Login: `manager` / `password`
- Try: View All ✅ → Create ❌ → Update ❌
- **Result**: 🔒 Only read operations work

### **🔵 Scenario 4: Employee Limited**
- Login: `employee` / `password`
- Try: View All ❌ → View Individual ✅
- **Result**: 🔐 Very limited access

### **🟣 Scenario 5: Multi-Role Power**
- Login: `hr_admin` / `password`
- Try: Any operation
- **Result**: ✅ Full access (has both HR + ADMIN)

---

## 🛠️ **Testing in Postman**

### **Step 1: Import Collection**
- File: `employee-crud-api-role-based.postman_collection.json`

### **Step 2: Login with Different Users**  
- Use login requests in `🔐 Authentication Tests` folder

### **Step 3: Test Permissions**
- Token is auto-saved after login
- Try different endpoints with different roles
- Watch console for detailed feedback

### **Step 4: Observe Results**
- ✅ **200/201** = Access granted
- ❌ **403** = Insufficient privileges  
- ❌ **401** = Not authenticated

---

## 🎯 **Key Points**

✅ **All passwords are**: `password`  
✅ **Tokens expire in**: 1 hour  
✅ **Multi-role users**: Get combined permissions  
✅ **AOP Security**: Method-level authorization  
✅ **JWT Claims**: Include role information  

**🔐 Ready to test your role-based security system!**

