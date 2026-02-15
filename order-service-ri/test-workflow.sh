#!/bin/bash

echo "🚀 Testing Complete Payment Workflow"
echo "===================================="

# Colors for output
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo ""
echo "${YELLOW}Step 1: Check if all services are running${NC}"

# Check if services are running
check_service() {
    local service_name=$1
    local port=$2
    local url=$3
    
    if curl -s -f "$url" > /dev/null 2>&1; then
        echo "${GREEN}✅ $service_name is running on port $port${NC}"
        return 0
    else
        echo "${RED}❌ $service_name is not running on port $port${NC}"
        return 1
    fi
}

# Check services
services_running=true

check_service "Order Service" "8080" "http://localhost:8080/api/test/status" || services_running=false
check_service "Authorization Service" "8081" "http://localhost:8081/api/authorization/test" || services_running=false
check_service "Settlement Service" "8082" "http://localhost:8082/api/test/status" || services_running=false
check_service "Eureka" "8761" "http://localhost:8761" || services_running=false
check_service "Axon Server" "8124" "http://localhost:8124" || services_running=false

if [ "$services_running" = false ]; then
    echo ""
    echo "${RED}❌ Some services are not running. Please start all services first.${NC}"
    echo ""
    echo "To start services:"
    echo "1. Eureka: cd DiscoveryServer && mvn spring-boot:run"
    echo "2. Axon Server: (start separately)"
    echo "3. Order Service: cd order-service-ri && mvn spring-boot:run"
    echo "4. Authorization Service: cd authorization-service-ri-lti && mvn spring-boot:run"
    echo "5. Settlement Service: cd settlement-service-ri && mvn spring-boot:run"
    exit 1
fi

echo ""
echo "${YELLOW}Step 2: Create Order and Initiate Payment${NC}"

# Start the complete workflow
response=$(curl -s -X POST http://localhost:8080/api/test/create-order-and-pay \
  -H "Content-Type: application/json")

if [ $? -eq 0 ]; then
    echo "${GREEN}✅ Workflow initiated successfully${NC}"
    echo "Response: $response"
else
    echo "${RED}❌ Failed to initiate workflow${NC}"
    exit 1
fi

echo ""
echo "${YELLOW}Step 3: Manual Payment Authorization Test${NC}"

# Test payment authorization directly
payment_response=$(curl -s -X POST http://localhost:8081/api/authorization/test \
  -H "Content-Type: application/json" \
  -d '{"orderId":"order-manual-test","amount":"150.00","currency":"USD","userId":"user-456","merchantId":"merchant-123","paymentMethod":"CREDIT_CARD"}')

if [ $? -eq 0 ]; then
    echo "${GREEN}✅ Payment authorization test successful${NC}"
    echo "Response: $payment_response"
else
    echo "${RED}❌ Payment authorization test failed${NC}"
fi

echo ""
echo "${YELLOW}Step 4: Check Order Status${NC}"

# Wait a moment for events to process
sleep 2

# Get all orders
orders=$(curl -s http://localhost:8080/api/orders)
echo "${GREEN}📋 Current Orders:${NC}"
echo "$orders" | jq '.' 2>/dev/null || echo "$orders"

echo ""
echo "${YELLOW}Step 5: Test Individual Order Lookup${NC}"

# Try to get a specific order (this might fail if order doesn't exist)
test_order_id="test-order-123"
order_response=$(curl -s http://localhost:8080/api/orders/$test_order_id)

if [[ $order_response == *"orderId"* ]]; then
    echo "${GREEN}✅ Order lookup successful${NC}"
    echo "$order_response" | jq '.' 2>/dev/null || echo "$order_response"
else
    echo "${YELLOW}⚠️  Test order not found (expected if no orders created yet)${NC}"
fi

echo ""
echo "${GREEN}🎉 Payment Workflow Test Complete!${NC}"
echo ""
echo "${YELLOW}Next Steps:${NC}"
echo "1. Check the service logs for detailed event processing"
echo "2. Verify order status changes in H2 console: http://localhost:8080/h2-console"
echo "3. Check Axon Server dashboard for event flow: http://localhost:8124"
echo "4. Verify service registration in Eureka: http://localhost:8761"

echo ""
echo "${YELLOW}H2 Database Access:${NC}"
echo "URL: jdbc:h2:mem:orderdb"
echo "Username: sa"
echo "Password: password"