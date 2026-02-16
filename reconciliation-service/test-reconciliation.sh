#!/bin/bash

# Reconciliation Service Test Script
# Tests the reconciliation API endpoints and functionality

echo "🔍 Payment Reconciliation Service Test Script"
echo "=========================================="

# Colors for output
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

BASE_URL="http://localhost:8084"

echo ""
echo "${BLUE}Step 1: Health Check${NC}"
health_response=$(curl -s -w "\n%{http_code}" $BASE_URL/api/reconciliation/health)
http_code=$(echo "$health_response" | tail -n1)
body=$(echo "$health_response" | head -n -1)

if [ "$http_code" = "200" ]; then
    echo "${GREEN}✅ Health Check Passed:${NC} $body"
else
    echo "${RED}❌ Health Check Failed:${NC} HTTP $http_code"
    exit 1
fi

echo ""
echo "${BLUE}Step 2: Get Reconciliation Statistics${NC}"
stats_response=$(curl -s -w "\n%{http_code}" $BASE_URL/api/reconciliation/stats)
http_code=$(echo "$stats_response" | tail -n1)
body=$(echo "$stats_response" | head -n -1)

if [ "$http_code" = "200" ]; then
    echo "${GREEN}✅ Statistics Retrieved:${NC}"
    echo "$body" | python3 -m json.tool 2>/dev/null || echo "$body"
else
    echo "${RED}❌ Statistics Failed:${NC} HTTP $http_code"
fi

echo ""
echo "${BLUE}Step 3: Trigger Full Reconciliation${NC}"
recon_response=$(curl -s -w "\n%{http_code}" -X POST $BASE_URL/api/reconciliation/full)
http_code=$(echo "$recon_response" | tail -n1)
body=$(echo "$recon_response" | head -n -1)

if [ "$http_code" = "200" ]; then
    echo "${GREEN}✅ Full Reconciliation Triggered:${NC}"
    echo "$body" | python3 -m json.tool 2>/dev/null || echo "$body"
else
    echo "${RED}❌ Full Reconciliation Failed:${NC} HTTP $http_code"
fi

echo ""
echo "${BLUE}Step 4: Trigger Date Range Reconciliation${NC}"
current_time=$(date -u +"%Y-%m-%dT%H:%M:%S")
start_time=$(date -u -v-24H +"%Y-%m-%dT%H:%M:%S" 2>/dev/null || date -u -d "24 hours ago" +"%Y-%m-%dT%H:%M:%S")

date_range_response=$(curl -s -w "\n%{http_code}" -X POST $BASE_URL/api/reconciliation/date-range \
  -H "Content-Type: application/json" \
  -d "{
    \"startDate\": \"$start_time\",
    \"endDate\": \"$current_time\",
    \"reason\": \"MANUAL_TEST\"
  }")

http_code=$(echo "$date_range_response" | tail -n1)
body=$(echo "$date_range_response" | head -n -1)

if [ "$http_code" = "200" ]; then
    echo "${GREEN}✅ Date Range Reconciliation Triggered:${NC}"
    echo "$body" | python3 -m json.tool 2>/dev/null || echo "$body"
else
    echo "${RED}❌ Date Range Reconciliation Failed:${NC} HTTP $http_code"
fi

echo ""
echo "${BLUE}Step 5: Trigger Order-Specific Reconciliation${NC}"
order_response=$(curl -s -w "\n%{http_code}" -X POST $BASE_URL/api/reconciliation/order/ORDER-12345)
http_code=$(echo "$order_response" | tail -n1)
body=$(echo "$order_response" | head -n -1)

if [ "$http_code" = "200" ]; then
    echo "${GREEN}✅ Order Reconciliation Triggered:${NC}"
    echo "$body" | python3 -m json.tool 2>/dev/null || echo "$body"
else
    echo "${RED}❌ Order Reconciliation Failed:${NC} HTTP $http_code"
fi

echo ""
echo "${BLUE}Step 6: Check Service Metrics${NC}"
metrics_response=$(curl -s -w "\n%{http_code}" $BASE_URL/actuator/metrics)
http_code=$(echo "$metrics_response" | tail -n1)

if [ "$http_code" = "200" ]; then
    echo "${GREEN}✅ Metrics Available:${NC} HTTP $http_code"
else
    echo "${YELLOW}⚠️  Metrics Not Available:${NC} HTTP $http_code (may need actuator enabled)"
fi

echo ""
echo "${BLUE}Step 7: Check Service Info${NC}"
info_response=$(curl -s -w "\n%{http_code}" $BASE_URL/actuator/info)
http_code=$(echo "$info_response" | tail -n1)
body=$(echo "$info_response" | head -n -1)

if [ "$http_code" = "200" ]; then
    echo "${GREEN}✅ Service Info:${NC}"
    echo "$body" | python3 -m json.tool 2>/dev/null || echo "$body"
else
    echo "${YELLOW}⚠️  Service Info Not Available:${NC} HTTP $http_code"
fi

echo ""
echo "${GREEN}🎉 Reconciliation Service Test Complete!${NC}"
echo ""
echo "${YELLOW}Next Steps:${NC}"
echo "1. Start the reconciliation service: ./mvnw spring-boot:run"
echo "2. Ensure other services are running (Payment, Settlement, Authorization)"
echo "3. Generate some payment events to populate the ledger"
echo "4. Run this script again to see actual reconciliation results"
echo "5. Check logs for detailed reconciliation information"
echo ""
echo "${BLUE}API Endpoints:${NC}"
echo "- Health: GET $BASE_URL/api/reconciliation/health"
echo "- Stats: GET $BASE_URL/api/reconciliation/stats"
echo "- Full Reconciliation: POST $BASE_URL/api/reconciliation/full"
echo "- Date Range: POST $BASE_URL/api/reconciliation/date-range"
echo "- Order Reconciliation: POST $BASE_URL/api/reconciliation/order/{orderId}"
echo "- Metrics: GET $BASE_URL/actuator/metrics"
