#!/bin/bash

# Reconciliation Service Startup Script
# Starts the reconciliation service with proper configuration

echo "🚀 Starting Payment Reconciliation Service"
echo "=========================================="

# Colors for output
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Check if Java is installed
if ! command -v java &> /dev/null; then
    echo "${RED}❌ Java is not installed or not in PATH${NC}"
    exit 1
fi

# Check Java version
JAVA_VERSION=$(java -version 2>&1 | head -n1 | cut -d'"' -f2)
echo "${BLUE}Java Version: $JAVA_VERSION${NC}"

# Check if Maven is installed
if ! command -v mvn &> /dev/null; then
    echo "${RED}❌ Maven is not installed or not in PATH${NC}"
    exit 1
fi

# Check if we're in the right directory
if [ ! -f "pom.xml" ]; then
    echo "${RED}❌ pom.xml not found. Please run this script from the reconciliation-service directory${NC}"
    exit 1
fi

echo ""
echo "${BLUE}Step 1: Checking Dependencies${NC}"

# Check if target directory exists
if [ ! -d "target" ]; then
    echo "${YELLOW}📦 Building project...${NC}"
    ./mvnw clean compile
    if [ $? -ne 0 ]; then
        echo "${RED}❌ Build failed${NC}"
        exit 1
    fi
else
    echo "${GREEN}✅ Build artifacts found${NC}"
fi

echo ""
echo "${BLUE}Step 2: Checking Axon Server${NC}"

# Check if Axon Server is running
if curl -s http://localhost:8124/v1/overview > /dev/null 2>&1; then
    echo "${GREEN}✅ Axon Server is running${NC}"
else
    echo "${YELLOW}⚠️  Axon Server is not running on localhost:8124${NC}"
    echo "${YELLOW}   Please start Axon Server before running the reconciliation service${NC}"
    echo "${YELLOW}   Example: cd ../AxonServer-4.6.7 && java -jar axonserver.jar${NC}"
    
    read -p "Do you want to continue anyway? (y/N): " -n 1 -r
    echo
    if [[ ! $REPLY =~ ^[Yy]$ ]]; then
        exit 1
    fi
fi

echo ""
echo "${BLUE}Step 3: Starting Reconciliation Service${NC}"

# Set JVM options
JVM_OPTS="-Xms512m -Xmx1g"
JVM_OPTS="$JVM_OPTS -XX:+UseG1GC"
JVM_OPTS="$JVM_OPTS -XX:MaxGCPauseMillis=200"
JVM_OPTS="$JVM_OPTS -Dspring.profiles.active=dev"

# Export environment variables
export RECONCILIATION_SCHEDULE_ENABLED=true
export RECONCILIATION_BATCH_SIZE=100

echo "${GREEN}🔄 Starting service with JVM options: $JVM_OPTS${NC}"
echo "${GREEN}📊 Service will be available at: http://localhost:8084${NC}"
echo "${GREEN}🔍 H2 Console: http://localhost:8084/h2-console${NC}"
echo "${GREEN}📈 Metrics: http://localhost:8084/actuator/metrics${NC}"
echo ""
echo "${YELLOW}Press Ctrl+C to stop the service${NC}"
echo ""

# Start the service
./mvnw spring-boot:run -Dspring-boot.run.jvmArguments="$JVM_OPTS"
