# Test Duration Report

Generated: 2025-11-04T03:07:58.954361176Z

This report aggregates test durations across all modules in the build.

---

## Module: order-processor

### Summary Statistics

- **Test Classes:** 3
- **Test Methods:** 16
- **Passed:** 16
- **Failed:** 0
- **Total Test Duration:** 7.83s
- **Module Execution Time:** 51.25s
- **Average Test Duration:** 489ms

### Test Details

### OrderEventListenerTest

**Class Total Duration:** 2.41s

| Test Method | Duration | Status |
|-------------|----------|--------|
| testHandleOrderCreated_DuplicateMessage_SkippedAndAcknowledged | 2.27s | ✅ |
| testHandleOrderCreated_UnexpectedException_Acknowledged | 64ms | ✅ |
| testHandleOrderCreated_NullCorrelationId_ProcessedAndAcknowledged | 36ms | ✅ |
| testHandleOrderCreated_ProcessingException_NotAcknowledged | 17ms | ✅ |
| testHandleOrderStatusChanged_NewMessage_ProcessedAndAcknowledged | 11ms | ✅ |
| testHandleOrderCreated_NewMessage_ProcessedAndAcknowledged | 11ms | ✅ |
| testHandleOrderStatusChanged_DuplicateMessage_SkippedAndAcknowledged | 9ms | ✅ |

### OrderEventProcessingServiceTest

**Class Total Duration:** 1.42s

| Test Method | Duration | Status |
|-------------|----------|--------|
| testProcessOrderStatusChanged_Success | 627ms | ✅ |
| testProcessOrderCreated_Success | 615ms | ✅ |
| testCounters_TrackProcessedEvents | 148ms | ✅ |
| testResetCounters | 25ms | ✅ |

### OrderProcessorE2ETest

**Class Total Duration:** 4.00s

| Test Method | Duration | Status |
|-------------|----------|--------|
| testMixedScenario_UniqueAndDuplicateMessages | 1.90s | ✅ |
| testDeduplication_DuplicateMessages_ProcessedOnlyOnce | 1.62s | ✅ |
| testMultipleUniqueMessages_AllProcessed | 233ms | ✅ |
| testHappyPath_SingleMessage_ProcessedSuccessfully | 122ms | ✅ |
| testMessageWithoutCorrelationId_StillProcessed | 118ms | ✅ |


---

