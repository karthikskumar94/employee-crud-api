# Test JWT Authentication
Write-Host "Testing JWT Authentication..." -ForegroundColor Green

# Test 1: Login to get JWT token
Write-Host "`n1. Testing Login..." -ForegroundColor Yellow
try {
    $loginResponse = Invoke-RestMethod -Uri "http://localhost:8080/auth/login" -Method POST -ContentType "application/json" -Body '{"username":"admin","password":"password"}'
    $token = $loginResponse.token
    Write-Host "✅ Login successful! Token received: $($token.Substring(0,50))..." -ForegroundColor Green
} catch {
    Write-Host "❌ Login failed: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}

# Test 2: Access protected endpoint without token
Write-Host "`n2. Testing protected endpoint WITHOUT token..." -ForegroundColor Yellow
try {
    $unauthorizedResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/employees" -Method GET
    Write-Host "❌ Unexpected: Access granted without token" -ForegroundColor Red
} catch {
    Write-Host "✅ Correctly blocked access without token" -ForegroundColor Green
}

# Test 3: Access protected endpoint WITH token
Write-Host "`n3. Testing protected endpoint WITH token..." -ForegroundColor Yellow
try {
    $headers = @{
        Authorization = "Bearer $token"
    }
    $employeesResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/employees" -Method GET -Headers $headers
    Write-Host "✅ Successfully accessed protected endpoint with token!" -ForegroundColor Green
    Write-Host "   Found $($employeesResponse.Count) employees" -ForegroundColor Gray
} catch {
    Write-Host "❌ Failed to access protected endpoint with token: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "`n🎉 JWT Authentication test completed!" -ForegroundColor Green
