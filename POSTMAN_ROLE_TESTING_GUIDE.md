# 🚀 **Postman Guide: Testing Role-Based Authentication**

## 📋 **Quick Setup**

### **Step 1: Start Your Application**
```powershell
# In your project directory
./gradlew bootRun

# Wait for: "Started EmployeeApiApplication in X.X seconds"
```

### **Step 2: Import the Role-Based Collection**
1. **Open Postman**
2. **Click "Import"** (top-left)
3. **Select file**: `employee-crud-api-role-based.postman_collection.json`
4. **Click "Import"**

## 🎯 **Testing Workflow**

### **🔐 Phase 1: Test Different User Roles**

#### **Test as ADMIN (Full Access)**
1. **Run**: `🔐 Authentication Tests` → `1️⃣ Login as ADMIN`
2. **Expected Response**:
   ```json
   {
     "token": "eyJhbGciOiJIUzI1NiJ9...",
     "username": "admin",
     "fullName": "System Administrator", 
     "roles": ["ADMIN"],
     "message": "Authentication successful"
   }
   ```
3. **Test Full Access**:
   - ✅ `📊 Read Operations` → `Get All Employees` (should work)
   - ✅ `📝 Create Operations` → `Create Employee` (should work)
   - ✅ `✏️ Update Operations` → `Update Employee` (should work)
   - ✅ `🗑️ Delete Operations` → `Delete Employee` (should work)
   - ✅ `📊 Statistics & Utility` → `Get Employee Count` (should work)

#### **Test as HR Manager (Create/Read/Update Only)**
1. **Run**: `🔐 Authentication Tests` → `2️⃣ Login as HR Manager`
2. **Test HR Permissions**:
   - ✅ `📊 Read Operations` → `Get All Employees` (should work)
   - ✅ `📝 Create Operations` → `Create Employee` (should work)
   - ✅ `✏️ Update Operations` → `Update Employee` (should work)
   - ❌ `🗑️ Delete Operations` → `Delete Employee` (should fail with 403)
   - ✅ `📊 Statistics & Utility` → `Get Employee Count` (should work)

#### **Test as Manager (Read Only)**
1. **Run**: `🔐 Authentication Tests` → `3️⃣ Login as Manager`
2. **Test Manager Permissions**:
   - ✅ `📊 Read Operations` → `Get All Employees` (should work)
   - ✅ `📊 Read Operations` → `Search Employees` (should work)
   - ❌ `📝 Create Operations` → `Create Employee` (should fail with 403)
   - ❌ `✏️ Update Operations` → `Update Employee` (should fail with 403)
   - ❌ `🗑️ Delete Operations` → `Delete Employee` (should fail with 403)
   - ❌ `📊 Statistics & Utility` → `Get Employee Count` (should fail with 403)

#### **Test as Employee (Limited Access)**
1. **Run**: `🔐 Authentication Tests` → `4️⃣ Login as Employee`
2. **Test Employee Permissions**:
   - ✅ `📊 Read Operations` → `Get Employee by ID` (should work)
   - ✅ `📊 Statistics & Utility` → `Health Check` (should work)
   - ❌ `📊 Read Operations` → `Get All Employees` (should fail with 403)
   - ❌ `📊 Read Operations` → `Search Employees` (should fail with 403)
   - ❌ All Create/Update/Delete operations (should fail with 403)

#### **Test Multi-Role User**
1. **Run**: `🔐 Authentication Tests` → `5️⃣ Login as HR Admin (Multi-Role)`
2. **Expected**: Should have both HR and ADMIN privileges (full access)

### **🧪 Phase 2: Security Testing**

#### **Test Without Authentication**
1. **Run**: `🧪 Permission Testing Scenarios` → `Test Without Token (Should Fail)`
2. **Expected**: 401 Unauthorized

#### **Test Invalid Token**
1. **Run**: `🧪 Permission Testing Scenarios` → `Test with Invalid Token (Should Fail)`
2. **Expected**: 401 Unauthorized

## 📊 **Expected Results Matrix**

| Operation | ADMIN | HR | MANAGER | EMPLOYEE | Expected Status |
|-----------|-------|----|---------|---------|--------------| 
| **Login** | ✅ | ✅ | ✅ | ✅ | 200 OK |
| **Get All Employees** | ✅ | ✅ | ✅ | ❌ | 200 OK / 403 Forbidden |
| **Get Employee by ID** | ✅ | ✅ | ✅ | ✅ | 200 OK |
| **Create Employee** | ✅ | ✅ | ❌ | ❌ | 201 Created / 403 Forbidden |
| **Update Employee** | ✅ | ✅ | ❌ | ❌ | 200 OK / 403 Forbidden |
| **Delete Employee** | ✅ | ❌ | ❌ | ❌ | 200 OK / 403 Forbidden |
| **Search Employees** | ✅ | ✅ | ✅ | ❌ | 200 OK / 403 Forbidden |
| **Get Employee Count** | ✅ | ✅ | ❌ | ❌ | 200 OK / 403 Forbidden |
| **Health Check** | ✅ | ✅ | ✅ | ✅ | 200 OK |
| **No Token** | ❌ | ❌ | ❌ | ❌ | 401 Unauthorized |

## 🎪 **Live Demo Sequence**

### **Recommended Testing Order:**

#### **1. Full Admin Demo** (5 minutes)
- Login as `admin`
- Create new employee
- Read all employees 
- Update employee
- Delete employee
- View statistics
- **Result**: Everything works ✅

#### **2. HR Restrictions Demo** (3 minutes)
- Login as `hr_manager`
- Try to create employee (✅ works)
- Try to delete employee (❌ fails with 403)
- **Result**: Shows HR cannot delete ⚠️

#### **3. Employee Limitations Demo** (3 minutes)
- Login as `employee`
- Try to view all employees (❌ fails with 403)
- Try to view single employee (✅ works)
- **Result**: Shows limited access 🔒

#### **4. Security Demo** (2 minutes)
- Try request without token (❌ 401)
- Try with invalid token (❌ 401)
- **Result**: Shows proper security 🛡️

## 🎯 **Success Indicators**

### **✅ What You Should See:**

**ADMIN Login Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbiIsImlhdCI6MTYzNDU2Nzg5MCwicm9sZXMiOlsiQURNSU4iXSwiZXhwIjoxNjM0NTcxNDkwfQ.xyz",
  "username": "admin", 
  "fullName": "System Administrator",
  "roles": ["ADMIN"],
  "message": "Authentication successful"
}
```

**403 Forbidden (Role Restriction):**
```json
{
  "timestamp": "2023-10-18T10:30:00.000+00:00",
  "status": 403,
  "error": "Forbidden", 
  "message": "Access denied: insufficient privileges"
}
```

**401 Unauthorized (No Token):**
```json
{
  "timestamp": "2023-10-18T10:30:00.000+00:00",
  "status": 401,
  "error": "Unauthorized",
  "message": "Authentication required"
}
```

### **📝 Postman Console Output:**
- `✅ Admin login successful!`
- `🎯 Full access granted - can do everything`
- `🚫 Access denied - insufficient privileges`
- `👥 Current roles: ["HR"]`
- `🎯 Only ADMIN can delete employees`

## 🔧 **Advanced Testing**

### **Collection Variables Tracking:**
The collection automatically tracks:
- `jwt_token`: Current authentication token
- `current_user`: Currently logged in user
- `current_roles`: User's roles as JSON array

### **Automatic Role Validation:**
Each request includes scripts that:
- ✅ Display current user and roles
- ✅ Explain why access was granted/denied  
- ✅ Show expected behavior for each role
- ✅ Provide troubleshooting hints

### **Real-time Feedback:**
Watch the **Postman Console** (View → Show Postman Console) for:
- 🔐 Authentication status
- 👥 Current user roles
- ✅ Access granted messages
- 🚫 Access denied explanations
- 🎯 Permission requirement hints

## 🚨 **Troubleshooting**

### **Issue: "No JWT token found"**
- **Solution**: Run a login request first
- **Check**: Collection variables show `jwt_token`

### **Issue: "403 Forbidden" for valid user**
- **Check**: User has required role for that endpoint
- **Verify**: Token contains correct role claims
- **Debug**: Enable Postman Console for detailed output

### **Issue: All requests fail with 401**
- **Check**: Application is running (`./gradlew bootRun`)
- **Verify**: Base URL is correct (`http://localhost:8080`)
- **Confirm**: Token is not expired (1 hour timeout)

### **Issue: Unexpected permissions**
- **Check**: Sample users were created on startup
- **Verify**: Database contains user role mappings
- **Debug**: Check application logs for AOP security messages

## 🎉 **Ready to Test!**

Your Postman collection is now ready for comprehensive role-based authentication testing!

### **Quick Start:**
1. ✅ Start application
2. ✅ Import collection  
3. ✅ Login as different users
4. ✅ Test various endpoints
5. ✅ Watch console output
6. ✅ Verify role-based access control

**🚀 Start testing your enterprise-grade security system!**

