#!/bin/bash

echo "📁 File-Based Notification Service Demo"
echo "===================================="

# Colors for output
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
CYAN='\033[0;36m'
NC='\033[0m' # No Color

echo ""
echo "${YELLOW}Step 1: Check if Notification Service is running${NC}"

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
echo "${YELLOW}Step 2: Test File-Based Payment Settled Notification${NC}"

settled_response=$(curl -s -X POST http://localhost:8083/api/notifications/test/payment-settled)
echo "${GREEN}✅ Payment Settled (File-Based):${NC}"
echo "$settled_response"

echo ""
echo "${YELLOW}Step 3: Test File-Based Payment Rejected Notification${NC}"

rejected_response=$(curl -s -X POST http://localhost:8083/api/notifications/test/payment-rejected)
echo "${GREEN}✅ Payment Rejected (File-Based):${NC}"
echo "$rejected_response"

echo ""
echo "${YELLOW}Step 4: Test All Channels (Different Log Files)${NC}"

all_channels_response=$(curl -s -X POST http://localhost:8083/api/notifications/test/all-channels)
echo "${GREEN}✅ All Channels (Different Log Files):${NC}"
echo "$all_channels_response"

echo ""
echo "${YELLOW}Step 5: Test Custom File-Based Notification${NC}"

custom_response=$(curl -s -X POST http://localhost:8083/api/notifications \
  -H "Content-Type: application/json" \
  -d '{
    "correlationId": "file-test-123",
    "eventType": "order.completed",
    "recipient": "customer@example.com",
    "channel": "EMAIL",
    "subject": "Order Completed - File Logged",
    "content": "Your order has been completed and logged to file system.",
    "metadata": "{\"orderId\":\"ORD-789\",\"amount\":\"$199.99\",\"loggedToFile\":true}"
  }')

echo "${GREEN}✅ Custom File-Based Notification:${NC}"
echo "$custom_response"

echo ""
echo "${YELLOW}Step 6: Get File-Based Statistics${NC}"

stats_response=$(curl -s http://localhost:8083/api/notifications/statistics)
echo "${GREEN}📊 File-Based Statistics:${NC}"
echo "$stats_response" | jq '. | {implementationType, logDirectoryInfo, fileStatistics}' 2>/dev/null || echo "$stats_response"

echo ""
echo "${YELLOW}Step 7: Check Generated Log Files${NC}"

if [ -d "notification-logs" ]; then
    echo "${GREEN}📁 Log Files Generated:${NC}"
    ls -la notification-logs/
    echo ""
    
    echo "${CYAN}📄 Recent Log Entries:${NC}"
    
    # Show last few lines from each log file
    for log_file in notification-logs/*.log; do
        if [ -f "$log_file" ]; then
            echo ""
            echo "${BLUE}📋 $(basename $log_file):${NC}"
            tail -3 "$log_file" 2>/dev/null | head -5
        fi
    done
else
    echo "${YELLOW}⚠️  Log directory not found yet. Notifications may still be processing.${NC}"
fi

echo ""
echo "${YELLOW}Step 8: Test Daily Summary Log${NC}"

summary_response=$(curl -s -X POST "http://localhost:8083/api/notifications/simple" \
  -d "correlationId=daily-summary-test&eventType=system.summary&recipient=admin@example.com&message=Daily summary test")

echo "${GREEN}✅ Daily Summary Test:${NC}"
echo "$summary_response"

echo ""
echo "${CYAN}📁 File-Based Notification System Features:${NC}"
echo "  📂 Organized by channel (email-YYYY-MM-DD.log, sms-YYYY-MM-DD.log, etc.)"
echo "  📝 Structured log entries with full notification details"
echo "  📊 Daily summary logs for quick overview"
echo "  🔍 Thread-safe file writing with locks"
echo "  📈 File statistics and monitoring"
echo "  🔄 Retry mechanism for failed notifications"

echo ""
echo "${CYAN}📋 Generated Log File Structure:${NC}"
echo "notification-logs/"
echo "├── email-$(date +%Y-%m-%d).log"
echo "├── sms-$(date +%Y-%m-%d).log"
echo "├── push-$(date +%Y-%m-%d).log"
echo "├── webhook-$(date +%Y-%m-%d).log"
echo "├── ui-$(date +%Y-%m-%d).log"
echo "├── console-$(date +%Y-%m-%d).log"
echo "└── daily-summary-$(date +%Y-%m-%d).log"

echo ""
echo "${CYAN}📄 Log Entry Format:${NC}"
echo "[2026-02-11 00:20:00.123] [EMAIL] [SENT] ID=notif-123"
echo "  CorrelationID: payment-456"
echo "  EventType: payment.settled"
echo "  Recipient: user@example.com"
echo "  Subject: Payment Successful"
echo "  Content: Your payment has been processed..."
echo "  CreatedAt: 2026-02-11 00:19:45"
echo "  SentAt: 2026-02-11 00:20:00"
echo "---"

echo ""
echo "${GREEN}🎉 File-Based Notification Demo Complete!${NC}"
echo ""
echo "${YELLOW}Benefits for Sample Assignment:${NC}"
echo "✅ No external dependencies (SMTP, SMS providers, etc.)"
echo "✅ Demonstrates core notification concepts and patterns"
echo "✅ Easy to verify and test (just read log files)"
echo "✅ Shows proper separation of concerns"
echo "✅ Includes retry logic and error handling"
echo "✅ Provides audit trail and statistics"

echo ""
echo "${YELLOW}Next Steps:${NC}"
echo "1. Review the generated log files in notification-logs/"
echo "2. Check the structured format and content"
echo "3. Verify different channels create separate log files"
echo "4. Test with real payment events from other services"

echo ""
echo "${BLUE}🔗 File-Based vs Real Notifications:${NC}"
echo "Real: Email → SMTP server → User inbox"
echo "File: Email → email-2026-02-11.log → File system"
echo ""
echo "Real: SMS → Twilio → User phone"
echo "File: SMS → sms-2026-02-11.log → File system"
echo ""
echo "Both demonstrate the same patterns and logic!"