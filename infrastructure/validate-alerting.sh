#!/bin/bash

# Alerting Pipeline Validation Script
# This script validates the Prometheus Alertmanager and Mailhog setup

set -e

echo "================================================"
echo "Catbox Alerting Pipeline Validation"
echo "================================================"
echo ""

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Check if running from infrastructure directory
if [ ! -f "compose.yaml" ]; then
    echo -e "${RED}✗ Error: Please run this script from the infrastructure directory${NC}"
    exit 1
fi

echo "Step 1: Validating configuration files..."
echo "-------------------------------------------"

# Validate YAML files
echo -n "Checking prometheus.yml... "
if python3 -c "import yaml; yaml.safe_load(open('monitoring/prometheus/prometheus.yml'))" 2>/dev/null; then
    echo -e "${GREEN}✓${NC}"
else
    echo -e "${RED}✗ Invalid YAML${NC}"
    exit 1
fi

echo -n "Checking alertmanager.yml... "
if python3 -c "import yaml; yaml.safe_load(open('monitoring/alertmanager/alertmanager.yml'))" 2>/dev/null; then
    echo -e "${GREEN}✓${NC}"
else
    echo -e "${RED}✗ Invalid YAML${NC}"
    exit 1
fi

echo -n "Checking alert-rules.yml... "
if python3 -c "import yaml; yaml.safe_load(open('monitoring/alertmanager/alert-rules.yml'))" 2>/dev/null; then
    echo -e "${GREEN}✓${NC}"
else
    echo -e "${RED}✗ Invalid YAML${NC}"
    exit 1
fi

echo ""
echo "Step 2: Validating Docker Compose configuration..."
echo "---------------------------------------------------"

echo -n "Checking compose.yaml syntax... "
if docker compose config > /dev/null 2>&1; then
    echo -e "${GREEN}✓${NC}"
else
    echo -e "${RED}✗ Invalid docker-compose configuration${NC}"
    exit 1
fi

echo ""
echo "Step 3: Checking service definitions..."
echo "----------------------------------------"

for service in prometheus alertmanager mailhog; do
    echo -n "Checking $service service... "
    if docker compose config --services | grep -q "^${service}$"; then
        echo -e "${GREEN}✓${NC}"
    else
        echo -e "${RED}✗ Service not found${NC}"
        exit 1
    fi
done

echo ""
echo "Step 4: Verifying required volumes..."
echo "--------------------------------------"

for volume in prometheus-data alertmanager-data; do
    echo -n "Checking $volume... "
    if docker compose config --volumes | grep -q "^${volume}$"; then
        echo -e "${GREEN}✓${NC}"
    else
        echo -e "${RED}✗ Volume not found${NC}"
        exit 1
    fi
done

echo ""
echo "Step 5: Checking if services are running..."
echo "--------------------------------------------"

if docker compose ps | grep -q "catbox-prometheus"; then
    prometheus_status=$(docker compose ps prometheus | grep prometheus | awk '{print $NF}')
    echo -n "Prometheus status... "
    if [ "$prometheus_status" = "running" ] || [ "$prometheus_status" = "healthy" ]; then
        echo -e "${GREEN}✓ Running${NC}"
    else
        echo -e "${YELLOW}⚠ Not running (start with: docker compose up -d)${NC}"
    fi
else
    echo -e "${YELLOW}⚠ Prometheus not started (start with: docker compose up -d)${NC}"
fi

if docker compose ps | grep -q "catbox-alertmanager"; then
    alertmanager_status=$(docker compose ps alertmanager | grep alertmanager | awk '{print $NF}')
    echo -n "Alertmanager status... "
    if [ "$alertmanager_status" = "running" ] || [ "$alertmanager_status" = "healthy" ]; then
        echo -e "${GREEN}✓ Running${NC}"
    else
        echo -e "${YELLOW}⚠ Not running (start with: docker compose up -d)${NC}"
    fi
else
    echo -e "${YELLOW}⚠ Alertmanager not started (start with: docker compose up -d)${NC}"
fi

if docker compose ps | grep -q "catbox-mailhog"; then
    mailhog_status=$(docker compose ps mailhog | grep mailhog | awk '{print $NF}')
    echo -n "Mailhog status... "
    if [ "$mailhog_status" = "running" ] || [ "$mailhog_status" = "healthy" ]; then
        echo -e "${GREEN}✓ Running${NC}"
    else
        echo -e "${YELLOW}⚠ Not running (start with: docker compose up -d)${NC}"
    fi
else
    echo -e "${YELLOW}⚠ Mailhog not started (start with: docker compose up -d)${NC}"
fi

echo ""
echo "Step 6: Testing connectivity (if services are running)..."
echo "----------------------------------------------------------"

# Test Prometheus
if curl -s http://localhost:9090/-/healthy > /dev/null 2>&1; then
    echo -e "${GREEN}✓${NC} Prometheus is accessible at http://localhost:9090"
else
    echo -e "${YELLOW}⚠${NC} Prometheus is not accessible (may not be running)"
fi

# Test Alertmanager
if curl -s http://localhost:9093/-/healthy > /dev/null 2>&1; then
    echo -e "${GREEN}✓${NC} Alertmanager is accessible at http://localhost:9093"
else
    echo -e "${YELLOW}⚠${NC} Alertmanager is not accessible (may not be running)"
fi

# Test Mailhog
if curl -s http://localhost:8025 > /dev/null 2>&1; then
    echo -e "${GREEN}✓${NC} Mailhog is accessible at http://localhost:8025"
else
    echo -e "${YELLOW}⚠${NC} Mailhog is not accessible (may not be running)"
fi

echo ""
echo "Step 7: Validating alert rule structure..."
echo "-------------------------------------------"

# Count alert rules
rule_count=$(python3 -c "import yaml; data=yaml.safe_load(open('monitoring/alertmanager/alert-rules.yml')); print(sum(len(g['rules']) for g in data['groups']))")
group_count=$(python3 -c "import yaml; data=yaml.safe_load(open('monitoring/alertmanager/alert-rules.yml')); print(len(data['groups']))")

echo -e "${GREEN}✓${NC} Found $group_count alert groups with $rule_count total rules"

# List alert groups
python3 << 'EOF'
import yaml
data = yaml.safe_load(open('monitoring/alertmanager/alert-rules.yml'))
for group in data['groups']:
    print(f"  - {group['name']}: {len(group['rules'])} rules")
EOF

echo ""
echo "================================================"
echo -e "${GREEN}Validation Complete!${NC}"
echo "================================================"
echo ""
echo "Next steps:"
echo "  1. Start services: docker compose up -d"
echo "  2. Wait for healthy status: docker compose ps"
echo "  3. Access UIs:"
echo "     - Prometheus: http://localhost:9090"
echo "     - Alertmanager: http://localhost:9093"
echo "     - Mailhog: http://localhost:8025"
echo "  4. See infrastructure/monitoring/TESTING.md for detailed testing"
echo ""
