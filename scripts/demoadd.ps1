# ---------------------------------------------------------
# Bulk add products to the E-Commerce API (PowerShell)
# ---------------------------------------------------------

# Path to your JSON file (adjust path if needed)
$jsonPath = "..\json\products.json"

# Base API endpoint
$apiUrl = "http://localhost:8080/api/v1/products"

# Check file exists
if (-Not (Test-Path $jsonPath)) {
    Write-Host " File not found: $jsonPath"
    exit
}

Write-Host " Adding products from $jsonPath ..."
Write-Host "------------------------------------------"

# Read JSON array
$products = Get-Content $jsonPath | ConvertFrom-Json

foreach ($p in $products) {
    # Convert product object back to JSON string
    $body = $p | ConvertTo-Json -Depth 5

    # Send POST request
    try {
        $response = Invoke-RestMethod -Uri $apiUrl -Method Post -Body $body -ContentType "application/json"
        Write-Host "Added:" $p.name
    } catch {
        Write-Host "⚠Failed to add:" $p.name "→" $_.Exception.Message
    }
}

Write-Host "------------------------------------------"
Write-Host "🎉 Done adding products!"
