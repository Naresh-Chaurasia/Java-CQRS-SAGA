#!/bin/bash

echo "🔔 Notification Service - Unified Output Demo"
echo "=========================================="

# Colors for output
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
PURPLE='\033[0;35m'
CYAN='\033[0;36m'
NC='\033[0m' # No Color

echo ""
echo "${YELLOW}Step 1: Check if Notification Service is running${NC}"

# Check if notification service is running
if curl -s -f http://localhost:8083/api/notifications/test > /dev/null 2>&1; then
    echo "${GREEN}✅ Notification Service is running on port 8083${NC}"
else
    echo "${RED}❌ Notification Service is not running on port 8083${NC}"
    echo ""
    echo "To start the service:"
    echo "cd notification-service && mvn spring-boot:run"
    exit 1
fi

echo ""
echo "${YELLOW}Step 2: Test Service Health${NC}"

health_response=$(curl -s http://localhost:8083/api/notifications/test)
echo "${GREEN}📋 Service Response:${NC}"
echo "$health_response"

echo ""
echo "${YELLOW}Step 3: Test Payment Settled Notification${NC}"

settled_response=$(curl -s -X POST http://localhost:8083/api/notifications/test/payment-settled)
echo "${GREEN}✅ Payment Settled Test:${NC}"
echo "$settled_response"

echo ""
echo "${YELLOW}Step 4: Test Payment Rejected Notification${NC}"

rejected_response=$(curl -s -X POST http://localhost:8083/api/notifications/test/payment-rejected)
echo "${GREEN}✅ Payment Rejected Test:${NC}"
echo "$rejected_response"

echo ""
echo "${YELLOW}Step 5: Test All Notification Channels${NC}"

all_channels_response=$(curl -s -X POST http://localhost:8083/api/notifications/test/all-channels)
echo "${GREEN}✅ All Channels Test:${NC}"
echo "$all_channels_response"

echo ""
echo "${YELLOW}Step 6: Test Custom Notification${NC}"

custom_response=$(curl -s -X POST http://localhost:8083/api/notifications \
  -H "Content-Type: application/json" \
  -d '{
    "correlationId": "custom-test-123",
    "eventType": "custom.order.completed",
    "recipient": "customer@example.com",
    "channel": "EMAIL",
    "subject": "Order Completed Successfully",
    "content": "Your order has been completed and will be shipped soon. Thank you for your purchase!",
    "metadata": "{\"orderId\":\"ORD-456789\",\"amount\":\"$299.99\",\"items\":3}"
  }')

echo "${GREEN}✅ Custom Notification Test:${NC}"
echo "$custom_response"

echo ""
echo "${YELLOW}Step 7: Test Simple Notification${NC}"

simple_response=$(curl -s -X POST "http://localhost:8083/api/notifications/simple" \
  -d "correlationId=simple-456&eventType=test.alert&recipient=admin@example.com&message=System maintenance scheduled for tonight")

echo "${GREEN}✅ Simple Notification Test:${NC}"
echo "$simple_response"

echo ""
echo "${YELLOW}Step 8: Get Notification Statistics${NC}"

stats_response=$(curl -s http://localhost:8083/api/notifications/statistics)
echo "${GREEN}📊 Notification Statistics:${NC}"
echo "$stats_response" | jq '.' 2>/dev/null || echo "$stats_response"

echo ""
echo "${YELLOW}Step 9: Get Notifications by Status${NC}"

# Get failed notifications (if any)
failed_notifications=$(curl -s http://localhost:8083/api/notifications/status/FAILED)
echo "${GREEN}🔍 Failed Notifications:${NC}"
echo "$failed_notifications" | jq '.' 2>/dev/null || echo "$failed_notifications"

echo ""
echo "${YELLOW}Step 10: Test UI Notifications${NC}"

ui_notifications=$(curl -s http://localhost:8083/api/notifications/ui/test@example.com)
echo "${GREEN}🖥️  UI Notifications for test@example.com:${NC}"
echo "$ui_notifications" | jq '.' 2>/dev/null || echo "$ui_notifications"

echo ""
echo "${YELLOW}Step 11: Test Retry Failed Notifications${NC}"

retry_response=$(curl -s -X POST http://localhost:8083/api/notifications/retry)
echo "${GREEN}🔄 Retry Response:${NC}"
echo "$retry_response"

echo ""
echo "${CYAN}🎯 Notification Channels Demonstrated:${NC}"
echo "  📧 EMAIL - Email notifications with HTML templates"
echo "  📱 SMS - SMS notifications (simulated)"
echo "  🔔 PUSH - Push notifications (simulated)"
echo "  🔗 WEBHOOK - HTTP webhooks (simulated)"
echo "  💻 CONSOLE - Formatted console output"
echo "  🖥️  UI - Real-time UI notifications"

echo ""
echo "${CYAN}📋 Event Types Handled:${NC}"
echo "  ✅ payment.settled - Payment successfully processed"
echo "  ❌ payment.rejected - Payment declined"
echo "  🔄 payment.authorized - Payment approved"
echo "  🚀 payment.initiated - Payment started"
echo "  📦 order.completed - Order finished"
echo "  ⚠️  order.payment_failed - Order payment issue"

echo ""
echo "${GREEN}🎉 Notification Service Demo Complete!${NC}"
echo ""
echo "${YELLOW}Next Steps:${NC}"
echo "1. Check the service logs for detailed notification processing"
echo "2. Verify notification persistence in H2 console: http://localhost:8083/h2-console"
echo "3. Check event processing in Axon Server: http://localhost:8124"
echo "4. Test with real payment events from other services"

echo ""
echo "${YELLOW}H2 Database Access:${NC}"
echo "URL: jdbc:h2:mem:notificationdb"
echo "Username: sa"
echo "Password: password"

echo ""
echo "${YELLOW}Available Templates:${NC}"
echo "- payment-settled.html - Success notification template"
echo "- payment-rejected.html - Rejection notification template"

echo ""
echo "${BLUE}🔗 API Endpoints Summary:${NC}"
echo "POST /api/notifications - Send custom notification"
echo "POST /api/notifications/simple - Send simple notification"
echo "GET  /api/notifications/{id} - Get specific notification"
echo "GET  /api/notifications/statistics - Get statistics"
echo "POST /api/notifications/retry - Retry failed notifications"
echo "GET  /api/notifications/ui/{recipient} - Get UI notifications"