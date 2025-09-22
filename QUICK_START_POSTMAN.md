# 🚀 Quick Start: Running Employee CRUD API in Postman

## 🎯 5-Minute Setup

### Step 1: Start Your Application
```powershell
# In your terminal, make sure the app is running:
./gradlew bootRun

# You should see: "Started EmployeeApiApplication in X.X seconds"
```

### Step 2: Import Collection in Postman

1. **Open Postman**
2. **Click "Import"** (top left corner)
3. **Drag & drop** or **browse** for: `employee-crud-api-with-auth.postman_collection.json`
4. **Click "Import"**

### Step 3: Login to Get JWT Token

1. **Find** the collection: `employee-crud-api-with-auth`
2. **Expand** "Authentication" folder
3. **Click** "Login" request
4. **Hit "Send"** button (blue button on right)

**✅ Expected Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbiIsImlhdCI6..."
}
```

### Step 4: Test Protected Endpoint

1. **Click** "Get all employees" request
2. **Hit "Send"**

**✅ Expected Response:**
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

## 🎪 Live Demo Sequence

### Test 1: 🔐 Authentication
```
POST http://localhost:8080/auth/login
Body: {"username":"admin","password":"password"}
Expected: JWT token returned
```

### Test 2: 📊 Read Data  
```
GET http://localhost:8080/api/employees
Header: Authorization: Bearer {your-token}
Expected: List of 5 sample employees
```

### Test 3: 📝 Create Employee
```
POST http://localhost:8080/api/employees
Header: Authorization: Bearer {your-token}
Body: {
  "name": "Test User",
  "email": "test@example.com",
  "department": "Testing",
  "salary": 50000.00
}
Expected: Created employee with ID
```

### Test 4: ✏️ Update Employee
```
PUT http://localhost:8080/api/employees/{id}
Header: Authorization: Bearer {your-token}
Body: {updated employee data}
Expected: Updated employee returned
```

### Test 5: 🗑️ Delete Employee
```
DELETE http://localhost:8080/api/employees/{id}
Header: Authorization: Bearer {your-token}
Expected: Success message
```

## 🚨 Common Issues & Solutions

| Problem | Solution |
|---------|----------|
| 401 Unauthorized | Run Login request first |
| Connection refused | Start the Spring Boot app |
| Token not working | Check it starts with "Bearer " |
| Import failed | Manually create requests |

## 📞 Need Help?

1. **Check application logs** in your terminal
2. **Enable Postman Console**: View → Show Postman Console
3. **Verify request format** matches the examples above

## 🎉 Success Indicators

- ✅ Login returns a JWT token
- ✅ Get employees returns list of data  
- ✅ Create/Update/Delete work without errors
- ✅ Postman shows 200/201 status codes

---

**🚀 You're ready to test your Employee CRUD API!**

