# 🚀 Postman Guide for Employee CRUD API with JWT Authentication

## 📋 Prerequisites

1. **Application Running**: Make sure your Spring Boot application is running on `http://localhost:8080`
2. **Postman Installed**: Download from [https://www.postman.com/downloads/](https://www.postman.com/downloads/)

## 🔧 Step 1: Import the Collection

### Option A: Import the Updated Collection (Recommended)
1. Open Postman
2. Click **"Import"** button (top left)
3. Select **"Upload Files"** tab
4. Navigate to your project folder and select: `employee-crud-api-with-auth.postman_collection.json`
5. Click **"Import"**

### Option B: Create Collection Manually
If the import doesn't work, follow the manual setup steps below.

## 🎯 Step 2: Verify Collection Import

After importing, you should see a collection named **"employee-crud-api-with-auth"** with these folders/requests:

```
📁 employee-crud-api-with-auth
├── 📁 Authentication
│   └── Login
├── Get all employees
├── Get employee by ID
├── Get employee by Email
├── Create employee
├── Get employees by department
├── Search employee by name
├── Update employee
├── Delete employee
├── Get employee count
└── Application health check
```

## 🔐 Step 3: Authentication Flow

### 🔑 **IMPORTANT**: Always Login First!

Before testing any protected endpoints, you MUST authenticate:

1. **Expand** the "Authentication" folder
2. **Click** on "Login" request
3. **Verify** the request details:
   - **Method**: POST
   - **URL**: `http://localhost:8080/auth/login`
   - **Headers**: `Content-Type: application/json`
   - **Body** (raw JSON):
     ```json
     {
       "username": "admin",
       "password": "password"
     }
     ```

4. **Click "Send"** button
5. **Expected Response**:
   ```json
   {
     "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbiIsImlhdCI6MTY5NTkyNzQwMCwiZXhwIjoxNjk1OTMxMDAwfQ.xxx"
   }
   ```

6. **✅ Success Indicator**: The JWT token is automatically saved as `{{jwt_token}}` variable

## 🔍 Step 4: Testing Protected Endpoints

Now you can test all protected endpoints. Each request automatically includes the JWT token.

### 📊 **Example 1: Get All Employees**

1. Click on **"Get all employees"** request
2. **Verify** the request includes:
   - **Method**: GET
   - **URL**: `http://localhost:8080/api/employees`
   - **Headers**: `Authorization: Bearer {{jwt_token}}`
3. **Click "Send"**
4. **Expected Response**: List of employees
   ```json
   [
     {
       "id": 1,
       "name": "John Doe",
       "email": "john.doe@example.com",
       "department": "Engineering",
       "salary": 75000.00
     },
     // ... more employees
   ]
   ```

### 📝 **Example 2: Create New Employee**

1. Click on **"Create employee"** request
2. **Verify** the request details:
   - **Method**: POST
   - **URL**: `http://localhost:8080/api/employees`
   - **Headers**: 
     - `Content-Type: application/json`
     - `Authorization: Bearer {{jwt_token}}`
   - **Body** (raw JSON):
     ```json
     {
       "name": "Jane Smith",
       "email": "jane.smith@example.com", 
       "department": "Marketing",
       "salary": 65000.00
     }
     ```
3. **Click "Send"**
4. **Expected Response**: Created employee with assigned ID

## ⚙️ Step 5: Collection Variables

The collection uses these variables (automatically managed):

- **`{{base_url}}`**: `http://localhost:8080`
- **`{{jwt_token}}`**: Automatically set after login

### 🔧 To View/Edit Variables:
1. Right-click on the collection name
2. Select **"Edit"**
3. Go to **"Variables"** tab
4. See current values

## 🛠️ Step 6: Manual Configuration (If Auto-Auth Fails)

If the automatic token management doesn't work:

### Method 1: Collection-Level Authorization
1. Right-click collection → **"Edit"**
2. Go to **"Authorization"** tab
3. Type: **"Bearer Token"**
4. Token: `{{jwt_token}}`

### Method 2: Individual Request Authorization
For each protected request:
1. Select the request
2. Go to **"Authorization"** tab
3. Type: **"Bearer Token"**
4. Token: `{{jwt_token}}`

### Method 3: Manual Header Addition
For each protected request:
1. Go to **"Headers"** tab  
2. Add header:
   - **Key**: `Authorization`
   - **Value**: `Bearer {{jwt_token}}`

## 🧪 Step 7: Complete Testing Workflow

### 🎯 **Recommended Testing Order**:

1. **🔐 Login** (Authentication → Login)
2. **📊 Get all employees** (verify token works)
3. **🆔 Get employee by ID** (try ID: 1, 2, etc.)
4. **📧 Get employee by email** (try: john.doe@example.com)
5. **📝 Create employee** (add new employee)
6. **🔍 Search by name** (search for created employee)
7. **✏️ Update employee** (modify the created employee)
8. **🗑️ Delete employee** (remove the created employee)
9. **📊 Get employee count** (verify count changes)
10. **❤️ Health check** (confirm API is healthy)

## 🚨 Troubleshooting

### ❌ **"401 Unauthorized" Error**
- **Cause**: No token or expired token
- **Solution**: Run the Login request again

### ❌ **"403 Forbidden" Error**  
- **Cause**: Invalid token
- **Solution**: Check token format, ensure it starts with "Bearer "

### ❌ **Connection Refused**
- **Cause**: Application not running
- **Solution**: Start the Spring Boot app: `./gradlew bootRun`

### ❌ **Token Not Auto-Saved**
1. Check Login request has the **Test Script**:
   ```javascript
   if (pm.response.code === 200) {
       const responseJson = pm.response.json();
       if (responseJson.token) {
           pm.collectionVariables.set('jwt_token', responseJson.token);
           console.log('JWT token saved successfully!');
       }
   }
   ```

2. Enable Postman Console: **View → Show Postman Console**

## 🎯 Testing Tips

### ✅ **Best Practices**:
- Always login first before testing protected endpoints
- Check Postman Console for debug messages
- Use the **"Tests"** tab to write automated validations
- Save your requests for future use
- Use environment variables for different deployments (dev/prod)

### 🔧 **Advanced Features**:
- Set up **Pre-request Scripts** for automatic token refresh
- Create **Test Scripts** for response validation
- Use **Data Files** for bulk testing
- Set up **Monitors** for API health checking

## 📱 Quick Reference

| Endpoint | Method | Auth Required | Purpose |
|----------|--------|---------------|---------|
| `/auth/login` | POST | ❌ No | Get JWT token |
| `/api/employees` | GET | ✅ Yes | Get all employees |
| `/api/employees/{id}` | GET | ✅ Yes | Get employee by ID |
| `/api/employees/email/{email}` | GET | ✅ Yes | Get employee by email |
| `/api/employees` | POST | ✅ Yes | Create employee |
| `/api/employees/{id}` | PUT | ✅ Yes | Update employee |
| `/api/employees/{id}` | DELETE | ✅ Yes | Delete employee |
| `/api/employees/search?name={name}` | GET | ✅ Yes | Search by name |
| `/api/employees/department/{dept}` | GET | ✅ Yes | Get by department |
| `/api/employees/count` | GET | ✅ Yes | Get employee count |
| `/api/employees/health` | GET | ✅ Yes | Health check |

---

## 🎉 You're Ready!

Your Postman setup is complete! Start with the **Login** request and then explore all the CRUD operations. Happy testing! 🚀

