# Test Duration Report

Generated: 2025-11-04T01:05:34.240012847Z

This report aggregates test durations across all modules in the build.

---

## Module: catbox-server

### Summary Statistics

- **Test Classes:** 23
- **Test Methods:** 108
- **Passed:** 108
- **Failed:** 0
- **Total Test Duration:** 15.32s
- **Module Execution Time:** 3m 7s
- **Average Test Duration:** 141ms

### Test Details

### AdminControllerTest

**Class Total Duration:** 753ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| adminPage_WithPendingOnlyFilter_ShouldReturnOnlyPending | 516ms | ✅ |
| adminPage_WithPagination_ShouldReturnPagedResults | 110ms | ✅ |
| adminPage_WithDefaultParameters_ShouldUseDefaults | 37ms | ✅ |
| adminPage_WithFilters_ShouldReturnFilteredResults | 36ms | ✅ |
| adminPage_ShouldReturnAdminView | 27ms | ✅ |
| adminPage_WithSorting_ShouldReturnSortedResults | 27ms | ✅ |

### CatboxApplicationTests

**Class Total Duration:** 5ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| contextLoads | 5ms | ✅ |

### CorrelationIdTest

**Class Total Duration:** 150ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| publishEvent_withCorrelationId_sendsCorrelationIdInKafkaHeader | 78ms | ✅ |
| createEvent_withCorrelationId_storesInDatabase | 38ms | ✅ |
| publishEvent_withoutCorrelationId_sendsWithoutHeader | 34ms | ✅ |

### DynamicKafkaTemplateFactoryEvictionTest

**Class Total Duration:** 1.04s

| Test Method | Duration | Status |
|-------------|----------|--------|
| testConcurrentAccessDoesNotCauseExceptions | 1.01s | ✅ |
| testBeanNamingConvention | 13ms | ✅ |
| testTemplateCreationAndCaching | 4ms | ✅ |
| testEvictionHandlesEmptyCache | 4ms | ✅ |
| testTemplateReCreationAfterManualRemoval | 3ms | ✅ |
| testMultipleEvictionCallsDontCauseIssues | 3ms | ✅ |

### DynamicKafkaTemplateFactoryProxyTest

**Class Total Duration:** 112ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| testMultipleCallsDoNotDuplicateBeans | 50ms | ✅ |
| testTemplateCanBeRetrievedDirectlyFromContext | 32ms | ✅ |
| testDynamicallyCreatedTemplateHasSpringLifecycle | 8ms | ✅ |
| testKafkaTemplateIsSpringManagedBean | 8ms | ✅ |
| testKafkaTemplateIsSingleton | 7ms | ✅ |
| testProducerFactoryIsSpringManagedBean | 7ms | ✅ |

### DynamicKafkaTemplateFactorySslBundlePositiveTest

**Class Total Duration:** 63ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| testClusterWithSslBundleHasSslConfiguration | 63ms | ✅ |

### DynamicKafkaTemplateFactorySslBundleTest

**Class Total Duration:** 30ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| testClusterWithoutSslBundleDoesNotHaveSslConfig | 18ms | ✅ |
| testMissingClusterThrowsException | 12ms | ✅ |

### E2EPollerMultiClusterTest

**Class Total Duration:** 6.60s

| Test Method | Duration | Status |
|-------------|----------|--------|
| testPollerRoutesEventsToCorrectClusters | 6.60s | ✅ |

### E2EPollerTest

**Class Total Duration:** 2.74s

| Test Method | Duration | Status |
|-------------|----------|--------|
| testPollerClaimsAndPublishesEvent | 2.74s | ✅ |

### OutboxArchivalServiceTest

**Class Total Duration:** 498ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| archiveOldEvents_movesOldSentEventsToArchive | 102ms | ✅ |
| archiveOldEvents_preservesKafkaMetadata | 88ms | ✅ |
| manualArchive_returnsZeroForInvalidRetention | 65ms | ✅ |
| archiveOldEvents_doesNothingWhenNoOldEvents | 56ms | ✅ |
| archiveOldEvents_preservesAllEventData | 52ms | ✅ |
| manualArchive_returnsZeroWhenNoEventsToArchive | 52ms | ✅ |
| manualArchive_archivesWithCustomRetention | 43ms | ✅ |
| manualArchive_returnsZeroForNegativeRetention | 40ms | ✅ |

### OutboxControllerTest

**Class Total Duration:** 415ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| markOutboxUnsent_ShouldMarkEventAsUnsent | 175ms | ✅ |
| getPendingOutboxEvents_ShouldReturnOnlyPending | 122ms | ✅ |
| searchOutbox_WithFilters_ShouldReturnFilteredResults | 81ms | ✅ |
| getAllOutboxEvents_ShouldReturnAllEvents | 37ms | ✅ |

### OutboxEventClaimTest

**Class Total Duration:** 114ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| testClaimPendingEvents_claimsExpiredInProgressEvents | 29ms | ✅ |
| testClaimPendingEvents_claimsEventsSuccessfully | 25ms | ✅ |
| testClaimPendingEvents_skipsAlreadySentEvents | 17ms | ✅ |
| testClaimPendingEvents_respectsBatchSize | 15ms | ✅ |
| testClaimPendingEvents_skipsEventsInProgress | 14ms | ✅ |
| testClaimPendingEvents_ordersEventsByCreatedAt | 14ms | ✅ |

### OutboxEventClaimerConcurrencyTest

**Class Total Duration:** 821ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| testConcurrentClaimersDoNotProcessSameEvents | 821ms | ✅ |

### OutboxEventPublisherMultiClusterTest

**Class Total Duration:** 245ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| allMustSucceed_successWhenAllClustersSucceed | 64ms | ✅ |
| atLeastOne_failsWhenAllClustersFail | 56ms | ✅ |
| atLeastOne_successWhenOnlyOneClusterSucceeds | 38ms | ✅ |
| optionalClusters_successWhenRequiredSucceedsOptionalFails | 36ms | ✅ |
| allMustSucceed_failsWhenOneClusterFails | 26ms | ✅ |
| optionalClusters_failsWhenRequiredClusterFails | 25ms | ✅ |

### OutboxEventPublisherTest

**Class Total Duration:** 760ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| publishEvent_resetsFailureCountOnSuccess | 354ms | ✅ |
| publishEvent_movesToDeadLetterAfterMaxPermanentRetries | 164ms | ✅ |
| publishEvent_successfullySendsAndMarksSent | 72ms | ✅ |
| publishEvent_handlesPermanentFailure_kafkaException | 64ms | ✅ |
| publishEvent_capturesKafkaMetadata | 59ms | ✅ |
| publishEvent_handlesPermanentFailure_noRoute | 47ms | ✅ |

### OutboxFailureHandlerTest

**Class Total Duration:** 282ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| recordPermanentFailure_movesToDeadLetterAfterMaxRetries | 85ms | ✅ |
| recordPermanentFailure_handlesMultipleEvents | 61ms | ✅ |
| resetFailureCount_clearsFailureData | 43ms | ✅ |
| recordPermanentFailure_incrementsFailureCount | 42ms | ✅ |
| resetFailureCount_doesNothingWhenCountIsZero | 28ms | ✅ |
| recordPermanentFailure_throwsExceptionWhenEventNotFound | 23ms | ✅ |

### OutboxMetricsServiceTest

**Class Total Duration:** 422ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| recordDeadLetter_incrementsCounter | 86ms | ✅ |
| updateArchivalMetrics_countsDeadLetterCorrectly | 78ms | ✅ |
| updatePendingEventsMetrics_calculatesOldestAge | 71ms | ✅ |
| updateArchivalMetrics_countsCorrectly | 33ms | ✅ |
| updatePendingEventsMetrics_whenNoEvents_setsToZero | 33ms | ✅ |
| updatePendingEventsMetrics_countsCorrectly | 26ms | ✅ |
| updateArchivalMetrics_whenNoEvents_setsToZero | 19ms | ✅ |
| recordProcessingDuration_recordsTimer | 18ms | ✅ |
| recordPublishFailure_incrementsCounter | 16ms | ✅ |
| recordArchival_incrementsCounter | 16ms | ✅ |
| recordPublishSuccess_incrementsCounter | 14ms | ✅ |
| metricsAreRegistered | 12ms | ✅ |

### OutboxProcessingConfigTest

**Class Total Duration:** 10ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| config_loadsMaxPermanentRetries | 4ms | ✅ |
| config_loadsDefaultPermanentFailureExceptions | 3ms | ✅ |
| config_hasExistingProcessingSettings | 3ms | ✅ |

### OutboxRoutingConfigTest

**Class Total Duration:** 7ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| getRoutingRule_handlesOptionalClusters | 4ms | ✅ |
| getRoutingRule_handlesIndexedPropertiesFromDynamicRegistry | 1ms | ✅ |
| getRoutingRule_handlesIndexedOptionalProperties | 1ms | ✅ |
| getRoutingRule_throwsExceptionForInvalidClustersFormat | 1ms | ✅ |
| getRoutingRule_handlesStrategyWithHyphens | 0ms | ✅ |
| getRoutingRule_handlesSingleOptionalClusterAsString | 0ms | ✅ |
| getRoutingRule_handlesStrategyWithUnderscores | 0ms | ✅ |
| getRoutingRule_defaultsToAllMustSucceedWhenStrategyNotSpecified | 0ms | ✅ |
| getRoutingRule_throwsExceptionForInvalidFormat | 0ms | ✅ |
| getRoutingRule_handlesMultipleClustersWithStrategy | 0ms | ✅ |
| getRoutingRule_returnsNullForUnknownEventType | 0ms | ✅ |
| getRoutingRule_handlesSingleClusterAsString | 0ms | ✅ |
| getRoutingRule_handlesBackwardCompatibleStringFormat | 0ms | ✅ |

### OutboxServiceTest

**Class Total Duration:** 226ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| findPaged_filtersWithAggregateIdAndPending | 41ms | ✅ |
| findPaged_filtersWithBlankValues | 39ms | ✅ |
| getAllEvents_returnsAllEvents | 33ms | ✅ |
| markUnsent_clearsSentAtAndLease | 27ms | ✅ |
| markUnsent_throwsExceptionWhenNotFound | 25ms | ✅ |
| findPaged_filtersAndSorts | 23ms | ✅ |
| getPendingEvents_returnsAllUnsent | 19ms | ✅ |
| findPaged_withAllNullParameters | 19ms | ✅ |

### SecurityConfigTest

**Class Total Duration:** 6ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| shouldLoadSecurityFilterChain | 3ms | ✅ |
| shouldHaveDisabledSecurityByDefault | 3ms | ✅ |

### SecurityConfigurationTest

**Class Total Duration:** 17ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| testSaslConfigurationProperties | 14ms | ✅ |
| testSecureClusterConfiguration | 2ms | ✅ |
| testSslBundleIsConfigured | 1ms | ✅ |

### TracingConfigurationTest

**Class Total Duration:** 8ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| tracerShouldPropagateContext | 3ms | ✅ |
| tracerBeanShouldBeAvailable | 3ms | ✅ |
| tracerShouldCreateSpans | 2ms | ✅ |


---

## Module: order-service

### Summary Statistics

- **Test Classes:** 7
- **Test Methods:** 24
- **Passed:** 24
- **Failed:** 0
- **Total Test Duration:** 1.61s
- **Module Execution Time:** 34.50s
- **Average Test Duration:** 67ms

### Test Details

### OrderControllerTest

**Class Total Duration:** 424ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| getAllOrders_ShouldReturnAllOrders | 222ms | ✅ |
| updateOrderStatus_WithNullStatus_ShouldReturnBadRequest | 66ms | ✅ |
| createOrder_ShouldReturnCreatedOrder | 57ms | ✅ |
| getOrderById_ShouldReturnOrder | 39ms | ✅ |
| updateOrderStatus_ShouldUpdateAndReturnOrder | 24ms | ✅ |
| updateOrderStatus_WithBlankStatus_ShouldReturnBadRequest | 16ms | ✅ |

### OrderNotFoundExceptionTest

**Class Total Duration:** 126ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| shouldCreateExceptionWithCorrectMessage | 121ms | ✅ |
| shouldBeRuntimeException | 5ms | ✅ |

### OrderServiceFailureTest

**Class Total Duration:** 227ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| testOrderCreationFailsWhenOutboxWriteFails | 227ms | ✅ |

### OrderServiceTest

**Class Total Duration:** 792ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| testGetAllOrders | 667ms | ✅ |
| testUpdateOrderStatus_CreatesOutboxEvent | 41ms | ✅ |
| testCreateOrder_CreatesOrderAndOutboxEvent | 39ms | ✅ |
| testGetOrderById_ThrowsExceptionWhenNotFound | 24ms | ✅ |
| testGetOrderById_Success | 21ms | ✅ |

### OrderTest

**Class Total Duration:** 4ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| onCreate_shouldNotOverrideExistingStatus | 1ms | ✅ |
| shouldCreateOrderWithConstructor | 1ms | ✅ |
| shouldSupportSettersAndGetters | 1ms | ✅ |
| onCreate_shouldSetDefaultStatus | 1ms | ✅ |

### TracingConfigurationTest

**Class Total Duration:** 22ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| tracerBeanShouldBeAvailable | 9ms | ✅ |
| tracerShouldPropagateContext | 7ms | ✅ |
| tracerShouldCreateSpans | 6ms | ✅ |

### UpdateStatusRequestTest

**Class Total Duration:** 17ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| shouldSupportEquality | 15ms | ✅ |
| shouldCreateRequestWithStatus | 1ms | ✅ |
| shouldHandleNullStatus | 1ms | ✅ |


---

## Module: order-processor

### Summary Statistics

- **Test Classes:** 3
- **Test Methods:** 16
- **Passed:** 11
- **Failed:** 5
- **Total Test Duration:** 3.96s
- **Module Execution Time:** 51.28s
- **Average Test Duration:** 247ms

### Test Details

### OrderEventListenerTest

**Class Total Duration:** 2.41s

| Test Method | Duration | Status |
|-------------|----------|--------|
| testHandleOrderCreated_DuplicateMessage_SkippedAndAcknowledged | 2.26s | ✅ |
| testHandleOrderCreated_UnexpectedException_Acknowledged | 75ms | ✅ |
| testHandleOrderCreated_NullCorrelationId_ProcessedAndAcknowledged | 33ms | ✅ |
| testHandleOrderCreated_ProcessingException_NotAcknowledged | 15ms | ✅ |
| testHandleOrderStatusChanged_NewMessage_ProcessedAndAcknowledged | 11ms | ✅ |
| testHandleOrderCreated_NewMessage_ProcessedAndAcknowledged | 11ms | ✅ |
| testHandleOrderStatusChanged_DuplicateMessage_SkippedAndAcknowledged | 8ms | ✅ |

### OrderEventProcessingServiceTest

**Class Total Duration:** 1.53s

| Test Method | Duration | Status |
|-------------|----------|--------|
| testProcessOrderStatusChanged_Success | 681ms | ✅ |
| testProcessOrderCreated_Success | 679ms | ✅ |
| testCounters_TrackProcessedEvents | 147ms | ✅ |
| testResetCounters | 25ms | ✅ |

### OrderProcessorE2ETest

**Class Total Duration:** 17ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| testMixedScenario_UniqueAndDuplicateMessages | 11ms | ❌ |
| testMultipleUniqueMessages_AllProcessed | 3ms | ❌ |
| testDeduplication_DuplicateMessages_ProcessedOnlyOnce | 1ms | ❌ |
| testMessageWithoutCorrelationId_StillProcessed | 1ms | ❌ |
| testHappyPath_SingleMessage_ProcessedSuccessfully | 1ms | ❌ |


---

## Module: order-processor

### Summary Statistics

- **Test Classes:** 3
- **Test Methods:** 16
- **Passed:** 11
- **Failed:** 5
- **Total Test Duration:** 4.15s
- **Module Execution Time:** 20.56s
- **Average Test Duration:** 259ms

### Test Details

### OrderEventListenerTest

**Class Total Duration:** 2.61s

| Test Method | Duration | Status |
|-------------|----------|--------|
| testHandleOrderCreated_DuplicateMessage_SkippedAndAcknowledged | 2.44s | ✅ |
| testHandleOrderCreated_UnexpectedException_Acknowledged | 80ms | ✅ |
| testHandleOrderCreated_NullCorrelationId_ProcessedAndAcknowledged | 36ms | ✅ |
| testHandleOrderStatusChanged_NewMessage_ProcessedAndAcknowledged | 19ms | ✅ |
| testHandleOrderCreated_ProcessingException_NotAcknowledged | 18ms | ✅ |
| testHandleOrderCreated_NewMessage_ProcessedAndAcknowledged | 11ms | ✅ |
| testHandleOrderStatusChanged_DuplicateMessage_SkippedAndAcknowledged | 8ms | ✅ |

### OrderEventProcessingServiceTest

**Class Total Duration:** 1.53s

| Test Method | Duration | Status |
|-------------|----------|--------|
| testProcessOrderCreated_Success | 786ms | ✅ |
| testProcessOrderStatusChanged_Success | 624ms | ✅ |
| testCounters_TrackProcessedEvents | 105ms | ✅ |
| testResetCounters | 17ms | ✅ |

### OrderProcessorE2ETest

**Class Total Duration:** 10ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| testMixedScenario_UniqueAndDuplicateMessages | 6ms | ❌ |
| testDeduplication_DuplicateMessages_ProcessedOnlyOnce | 1ms | ❌ |
| testMultipleUniqueMessages_AllProcessed | 1ms | ❌ |
| testMessageWithoutCorrelationId_StillProcessed | 1ms | ❌ |
| testHappyPath_SingleMessage_ProcessedSuccessfully | 1ms | ❌ |


---

## Module: order-processor

### Summary Statistics

- **Test Classes:** 3
- **Test Methods:** 16
- **Passed:** 11
- **Failed:** 5
- **Total Test Duration:** 3.85s
- **Module Execution Time:** 20.57s
- **Average Test Duration:** 240ms

### Test Details

### OrderEventListenerTest

**Class Total Duration:** 2.29s

| Test Method | Duration | Status |
|-------------|----------|--------|
| testHandleOrderCreated_DuplicateMessage_SkippedAndAcknowledged | 2.14s | ✅ |
| testHandleOrderCreated_UnexpectedException_Acknowledged | 60ms | ✅ |
| testHandleOrderCreated_NullCorrelationId_ProcessedAndAcknowledged | 36ms | ✅ |
| testHandleOrderCreated_ProcessingException_NotAcknowledged | 18ms | ✅ |
| testHandleOrderStatusChanged_NewMessage_ProcessedAndAcknowledged | 14ms | ✅ |
| testHandleOrderCreated_NewMessage_ProcessedAndAcknowledged | 12ms | ✅ |
| testHandleOrderStatusChanged_DuplicateMessage_SkippedAndAcknowledged | 9ms | ✅ |

### OrderEventProcessingServiceTest

**Class Total Duration:** 1.55s

| Test Method | Duration | Status |
|-------------|----------|--------|
| testProcessOrderCreated_Success | 752ms | ✅ |
| testProcessOrderStatusChanged_Success | 636ms | ✅ |
| testCounters_TrackProcessedEvents | 127ms | ✅ |
| testResetCounters | 32ms | ✅ |

### OrderProcessorE2ETest

**Class Total Duration:** 10ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| testMixedScenario_UniqueAndDuplicateMessages | 6ms | ❌ |
| testDeduplication_DuplicateMessages_ProcessedOnlyOnce | 1ms | ❌ |
| testMultipleUniqueMessages_AllProcessed | 1ms | ❌ |
| testMessageWithoutCorrelationId_StillProcessed | 1ms | ❌ |
| testHappyPath_SingleMessage_ProcessedSuccessfully | 1ms | ❌ |


---

## Module: order-processor

### Summary Statistics

- **Test Classes:** 3
- **Test Methods:** 16
- **Passed:** 16
- **Failed:** 0
- **Total Test Duration:** 8.32s
- **Module Execution Time:** 25.75s
- **Average Test Duration:** 519ms

### Test Details

### OrderEventListenerTest

**Class Total Duration:** 2.49s

| Test Method | Duration | Status |
|-------------|----------|--------|
| testHandleOrderCreated_DuplicateMessage_SkippedAndAcknowledged | 2.34s | ✅ |
| testHandleOrderCreated_UnexpectedException_Acknowledged | 64ms | ✅ |
| testHandleOrderCreated_NullCorrelationId_ProcessedAndAcknowledged | 38ms | ✅ |
| testHandleOrderCreated_ProcessingException_NotAcknowledged | 19ms | ✅ |
| testHandleOrderCreated_NewMessage_ProcessedAndAcknowledged | 13ms | ✅ |
| testHandleOrderStatusChanged_NewMessage_ProcessedAndAcknowledged | 11ms | ✅ |
| testHandleOrderStatusChanged_DuplicateMessage_SkippedAndAcknowledged | 8ms | ✅ |

### OrderEventProcessingServiceTest

**Class Total Duration:** 1.46s

| Test Method | Duration | Status |
|-------------|----------|--------|
| testProcessOrderCreated_Success | 793ms | ✅ |
| testProcessOrderStatusChanged_Success | 535ms | ✅ |
| testCounters_TrackProcessedEvents | 110ms | ✅ |
| testResetCounters | 23ms | ✅ |

### OrderProcessorE2ETest

**Class Total Duration:** 4.36s

| Test Method | Duration | Status |
|-------------|----------|--------|
| testMixedScenario_UniqueAndDuplicateMessages | 2.15s | ✅ |
| testDeduplication_DuplicateMessages_ProcessedOnlyOnce | 1.64s | ✅ |
| testMultipleUniqueMessages_AllProcessed | 336ms | ✅ |
| testHappyPath_SingleMessage_ProcessedSuccessfully | 124ms | ✅ |
| testMessageWithoutCorrelationId_StillProcessed | 116ms | ✅ |


---

## Module: catbox-server

### Summary Statistics

- **Test Classes:** 23
- **Test Methods:** 108
- **Passed:** 108
- **Failed:** 0
- **Total Test Duration:** 15.51s
- **Module Execution Time:** 2m 31s
- **Average Test Duration:** 143ms

### Test Details

### AdminControllerTest

**Class Total Duration:** 731ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| adminPage_WithPendingOnlyFilter_ShouldReturnOnlyPending | 486ms | ✅ |
| adminPage_WithPagination_ShouldReturnPagedResults | 103ms | ✅ |
| adminPage_WithSorting_ShouldReturnSortedResults | 49ms | ✅ |
| adminPage_WithFilters_ShouldReturnFilteredResults | 39ms | ✅ |
| adminPage_ShouldReturnAdminView | 28ms | ✅ |
| adminPage_WithDefaultParameters_ShouldUseDefaults | 26ms | ✅ |

### CatboxApplicationTests

**Class Total Duration:** 5ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| contextLoads | 5ms | ✅ |

### CorrelationIdTest

**Class Total Duration:** 163ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| publishEvent_withCorrelationId_sendsCorrelationIdInKafkaHeader | 77ms | ✅ |
| publishEvent_withoutCorrelationId_sendsWithoutHeader | 46ms | ✅ |
| createEvent_withCorrelationId_storesInDatabase | 40ms | ✅ |

### DynamicKafkaTemplateFactoryEvictionTest

**Class Total Duration:** 1.04s

| Test Method | Duration | Status |
|-------------|----------|--------|
| testConcurrentAccessDoesNotCauseExceptions | 1.01s | ✅ |
| testBeanNamingConvention | 14ms | ✅ |
| testTemplateCreationAndCaching | 5ms | ✅ |
| testTemplateReCreationAfterManualRemoval | 5ms | ✅ |
| testEvictionHandlesEmptyCache | 4ms | ✅ |
| testMultipleEvictionCallsDontCauseIssues | 4ms | ✅ |

### DynamicKafkaTemplateFactoryProxyTest

**Class Total Duration:** 105ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| testMultipleCallsDoNotDuplicateBeans | 48ms | ✅ |
| testTemplateCanBeRetrievedDirectlyFromContext | 26ms | ✅ |
| testDynamicallyCreatedTemplateHasSpringLifecycle | 8ms | ✅ |
| testKafkaTemplateIsSpringManagedBean | 8ms | ✅ |
| testKafkaTemplateIsSingleton | 8ms | ✅ |
| testProducerFactoryIsSpringManagedBean | 7ms | ✅ |

### DynamicKafkaTemplateFactorySslBundlePositiveTest

**Class Total Duration:** 74ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| testClusterWithSslBundleHasSslConfiguration | 74ms | ✅ |

### DynamicKafkaTemplateFactorySslBundleTest

**Class Total Duration:** 27ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| testClusterWithoutSslBundleDoesNotHaveSslConfig | 16ms | ✅ |
| testMissingClusterThrowsException | 11ms | ✅ |

### E2EPollerMultiClusterTest

**Class Total Duration:** 6.62s

| Test Method | Duration | Status |
|-------------|----------|--------|
| testPollerRoutesEventsToCorrectClusters | 6.62s | ✅ |

### E2EPollerTest

**Class Total Duration:** 2.64s

| Test Method | Duration | Status |
|-------------|----------|--------|
| testPollerClaimsAndPublishesEvent | 2.64s | ✅ |

### OutboxArchivalServiceTest

**Class Total Duration:** 576ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| archiveOldEvents_preservesKafkaMetadata | 118ms | ✅ |
| archiveOldEvents_movesOldSentEventsToArchive | 106ms | ✅ |
| archiveOldEvents_preservesAllEventData | 82ms | ✅ |
| manualArchive_returnsZeroForInvalidRetention | 75ms | ✅ |
| manualArchive_returnsZeroWhenNoEventsToArchive | 64ms | ✅ |
| manualArchive_archivesWithCustomRetention | 50ms | ✅ |
| manualArchive_returnsZeroForNegativeRetention | 42ms | ✅ |
| archiveOldEvents_doesNothingWhenNoOldEvents | 39ms | ✅ |

### OutboxControllerTest

**Class Total Duration:** 428ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| markOutboxUnsent_ShouldMarkEventAsUnsent | 185ms | ✅ |
| getPendingOutboxEvents_ShouldReturnOnlyPending | 124ms | ✅ |
| searchOutbox_WithFilters_ShouldReturnFilteredResults | 83ms | ✅ |
| getAllOutboxEvents_ShouldReturnAllEvents | 36ms | ✅ |

### OutboxEventClaimTest

**Class Total Duration:** 105ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| testClaimPendingEvents_claimsExpiredInProgressEvents | 31ms | ✅ |
| testClaimPendingEvents_claimsEventsSuccessfully | 16ms | ✅ |
| testClaimPendingEvents_respectsBatchSize | 16ms | ✅ |
| testClaimPendingEvents_ordersEventsByCreatedAt | 15ms | ✅ |
| testClaimPendingEvents_skipsEventsInProgress | 14ms | ✅ |
| testClaimPendingEvents_skipsAlreadySentEvents | 13ms | ✅ |

### OutboxEventClaimerConcurrencyTest

**Class Total Duration:** 811ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| testConcurrentClaimersDoNotProcessSameEvents | 811ms | ✅ |

### OutboxEventPublisherMultiClusterTest

**Class Total Duration:** 215ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| allMustSucceed_successWhenAllClustersSucceed | 59ms | ✅ |
| atLeastOne_failsWhenAllClustersFail | 46ms | ✅ |
| atLeastOne_successWhenOnlyOneClusterSucceeds | 33ms | ✅ |
| optionalClusters_successWhenRequiredSucceedsOptionalFails | 31ms | ✅ |
| optionalClusters_failsWhenRequiredClusterFails | 23ms | ✅ |
| allMustSucceed_failsWhenOneClusterFails | 23ms | ✅ |

### OutboxEventPublisherTest

**Class Total Duration:** 880ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| publishEvent_resetsFailureCountOnSuccess | 437ms | ✅ |
| publishEvent_movesToDeadLetterAfterMaxPermanentRetries | 186ms | ✅ |
| publishEvent_handlesPermanentFailure_kafkaException | 68ms | ✅ |
| publishEvent_handlesPermanentFailure_noRoute | 66ms | ✅ |
| publishEvent_successfullySendsAndMarksSent | 62ms | ✅ |
| publishEvent_capturesKafkaMetadata | 61ms | ✅ |

### OutboxFailureHandlerTest

**Class Total Duration:** 289ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| recordPermanentFailure_movesToDeadLetterAfterMaxRetries | 99ms | ✅ |
| recordPermanentFailure_handlesMultipleEvents | 67ms | ✅ |
| recordPermanentFailure_incrementsFailureCount | 47ms | ✅ |
| recordPermanentFailure_throwsExceptionWhenEventNotFound | 31ms | ✅ |
| resetFailureCount_clearsFailureData | 30ms | ✅ |
| resetFailureCount_doesNothingWhenCountIsZero | 15ms | ✅ |

### OutboxMetricsServiceTest

**Class Total Duration:** 504ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| updateArchivalMetrics_countsDeadLetterCorrectly | 131ms | ✅ |
| recordDeadLetter_incrementsCounter | 84ms | ✅ |
| updatePendingEventsMetrics_calculatesOldestAge | 53ms | ✅ |
| updateArchivalMetrics_countsCorrectly | 44ms | ✅ |
| updatePendingEventsMetrics_whenNoEvents_setsToZero | 41ms | ✅ |
| updatePendingEventsMetrics_countsCorrectly | 33ms | ✅ |
| updateArchivalMetrics_whenNoEvents_setsToZero | 28ms | ✅ |
| recordProcessingDuration_recordsTimer | 25ms | ✅ |
| recordPublishFailure_incrementsCounter | 24ms | ✅ |
| metricsAreRegistered | 14ms | ✅ |
| recordPublishSuccess_incrementsCounter | 14ms | ✅ |
| recordArchival_incrementsCounter | 13ms | ✅ |

### OutboxProcessingConfigTest

**Class Total Duration:** 10ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| config_loadsDefaultPermanentFailureExceptions | 4ms | ✅ |
| config_hasExistingProcessingSettings | 3ms | ✅ |
| config_loadsMaxPermanentRetries | 3ms | ✅ |

### OutboxRoutingConfigTest

**Class Total Duration:** 5ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| getRoutingRule_handlesOptionalClusters | 2ms | ✅ |
| getRoutingRule_throwsExceptionForInvalidFormat | 1ms | ✅ |
| getRoutingRule_handlesIndexedOptionalProperties | 1ms | ✅ |
| getRoutingRule_throwsExceptionForInvalidClustersFormat | 1ms | ✅ |
| getRoutingRule_handlesStrategyWithHyphens | 0ms | ✅ |
| getRoutingRule_handlesSingleOptionalClusterAsString | 0ms | ✅ |
| getRoutingRule_handlesStrategyWithUnderscores | 0ms | ✅ |
| getRoutingRule_defaultsToAllMustSucceedWhenStrategyNotSpecified | 0ms | ✅ |
| getRoutingRule_handlesMultipleClustersWithStrategy | 0ms | ✅ |
| getRoutingRule_handlesIndexedPropertiesFromDynamicRegistry | 0ms | ✅ |
| getRoutingRule_returnsNullForUnknownEventType | 0ms | ✅ |
| getRoutingRule_handlesSingleClusterAsString | 0ms | ✅ |
| getRoutingRule_handlesBackwardCompatibleStringFormat | 0ms | ✅ |

### OutboxServiceTest

**Class Total Duration:** 266ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| findPaged_filtersWithAggregateIdAndPending | 51ms | ✅ |
| findPaged_filtersWithBlankValues | 48ms | ✅ |
| findPaged_filtersAndSorts | 37ms | ✅ |
| markUnsent_clearsSentAtAndLease | 30ms | ✅ |
| markUnsent_throwsExceptionWhenNotFound | 28ms | ✅ |
| getAllEvents_returnsAllEvents | 26ms | ✅ |
| getPendingEvents_returnsAllUnsent | 23ms | ✅ |
| findPaged_withAllNullParameters | 23ms | ✅ |

### SecurityConfigTest

**Class Total Duration:** 5ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| shouldHaveDisabledSecurityByDefault | 3ms | ✅ |
| shouldLoadSecurityFilterChain | 2ms | ✅ |

### SecurityConfigurationTest

**Class Total Duration:** 5ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| testSecureClusterConfiguration | 2ms | ✅ |
| testSaslConfigurationProperties | 2ms | ✅ |
| testSslBundleIsConfigured | 1ms | ✅ |

### TracingConfigurationTest

**Class Total Duration:** 8ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| tracerShouldPropagateContext | 3ms | ✅ |
| tracerBeanShouldBeAvailable | 3ms | ✅ |
| tracerShouldCreateSpans | 2ms | ✅ |


---

## Module: order-service

### Summary Statistics

- **Test Classes:** 7
- **Test Methods:** 24
- **Passed:** 24
- **Failed:** 0
- **Total Test Duration:** 1.85s
- **Module Execution Time:** 31.62s
- **Average Test Duration:** 76ms

### Test Details

### OrderControllerTest

**Class Total Duration:** 461ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| getAllOrders_ShouldReturnAllOrders | 244ms | ✅ |
| updateOrderStatus_WithNullStatus_ShouldReturnBadRequest | 80ms | ✅ |
| createOrder_ShouldReturnCreatedOrder | 43ms | ✅ |
| getOrderById_ShouldReturnOrder | 42ms | ✅ |
| updateOrderStatus_ShouldUpdateAndReturnOrder | 34ms | ✅ |
| updateOrderStatus_WithBlankStatus_ShouldReturnBadRequest | 18ms | ✅ |

### OrderNotFoundExceptionTest

**Class Total Duration:** 167ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| shouldCreateExceptionWithCorrectMessage | 161ms | ✅ |
| shouldBeRuntimeException | 6ms | ✅ |

### OrderServiceFailureTest

**Class Total Duration:** 273ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| testOrderCreationFailsWhenOutboxWriteFails | 273ms | ✅ |

### OrderServiceTest

**Class Total Duration:** 894ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| testGetAllOrders | 750ms | ✅ |
| testCreateOrder_CreatesOrderAndOutboxEvent | 52ms | ✅ |
| testGetOrderById_Success | 35ms | ✅ |
| testUpdateOrderStatus_CreatesOutboxEvent | 32ms | ✅ |
| testGetOrderById_ThrowsExceptionWhenNotFound | 25ms | ✅ |

### OrderTest

**Class Total Duration:** 6ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| shouldCreateOrderWithConstructor | 3ms | ✅ |
| onCreate_shouldNotOverrideExistingStatus | 1ms | ✅ |
| shouldSupportSettersAndGetters | 1ms | ✅ |
| onCreate_shouldSetDefaultStatus | 1ms | ✅ |

### TracingConfigurationTest

**Class Total Duration:** 26ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| tracerBeanShouldBeAvailable | 12ms | ✅ |
| tracerShouldCreateSpans | 7ms | ✅ |
| tracerShouldPropagateContext | 7ms | ✅ |

### UpdateStatusRequestTest

**Class Total Duration:** 19ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| shouldSupportEquality | 16ms | ✅ |
| shouldCreateRequestWithStatus | 2ms | ✅ |
| shouldHandleNullStatus | 1ms | ✅ |


---

## Module: order-processor

### Summary Statistics

- **Test Classes:** 3
- **Test Methods:** 16
- **Passed:** 16
- **Failed:** 0
- **Total Test Duration:** 7.83s
- **Module Execution Time:** 25.17s
- **Average Test Duration:** 489ms

### Test Details

### OrderEventListenerTest

**Class Total Duration:** 2.27s

| Test Method | Duration | Status |
|-------------|----------|--------|
| testHandleOrderCreated_DuplicateMessage_SkippedAndAcknowledged | 2.12s | ✅ |
| testHandleOrderCreated_UnexpectedException_Acknowledged | 65ms | ✅ |
| testHandleOrderCreated_NullCorrelationId_ProcessedAndAcknowledged | 36ms | ✅ |
| testHandleOrderCreated_ProcessingException_NotAcknowledged | 19ms | ✅ |
| testHandleOrderCreated_NewMessage_ProcessedAndAcknowledged | 12ms | ✅ |
| testHandleOrderStatusChanged_NewMessage_ProcessedAndAcknowledged | 11ms | ✅ |
| testHandleOrderStatusChanged_DuplicateMessage_SkippedAndAcknowledged | 9ms | ✅ |

### OrderEventProcessingServiceTest

**Class Total Duration:** 1.61s

| Test Method | Duration | Status |
|-------------|----------|--------|
| testProcessOrderCreated_Success | 841ms | ✅ |
| testProcessOrderStatusChanged_Success | 637ms | ✅ |
| testCounters_TrackProcessedEvents | 91ms | ✅ |
| testResetCounters | 45ms | ✅ |

### OrderProcessorE2ETest

**Class Total Duration:** 3.95s

| Test Method | Duration | Status |
|-------------|----------|--------|
| testMixedScenario_UniqueAndDuplicateMessages | 1.75s | ✅ |
| testDeduplication_DuplicateMessages_ProcessedOnlyOnce | 1.63s | ✅ |
| testMultipleUniqueMessages_AllProcessed | 331ms | ✅ |
| testHappyPath_SingleMessage_ProcessedSuccessfully | 122ms | ✅ |
| testMessageWithoutCorrelationId_StillProcessed | 117ms | ✅ |


---

## Module: catbox-archunit

### Summary Statistics

- **Test Classes:** 6
- **Test Methods:** 32
- **Passed:** 32
- **Failed:** 0
- **Total Test Duration:** 222ms
- **Module Execution Time:** 4.05s
- **Average Test Duration:** 6ms

### Test Details

### EntityRepositoryPatternTest

**Class Total Duration:** 111ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| tableAnnotationShouldBeUsedForEntities | 74ms | ✅ |
| entitiesShouldNotHavePublicFields | 23ms | ✅ |
| repositoriesShouldBeInterfaces | 8ms | ✅ |
| entitiesIdFieldsShouldBeAnnotatedWithId | 4ms | ✅ |
| entitiesShouldResideInEntityPackage | 2ms | ✅ |

### LayeringArchitectureTest

**Class Total Duration:** 33ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| repositoriesShouldNotAccessServicesOrControllers | 29ms | ✅ |
| servicesShouldNotAccessControllers | 3ms | ✅ |
| controllersShouldNotAccessRepositoriesDirectly | 1ms | ✅ |

### NamingConventionTest

**Class Total Duration:** 11ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| servicesShouldBeSuffixed | 3ms | ✅ |
| configurationsShouldBeSuffixed | 2ms | ✅ |
| dtosShouldResideInDtoPackage | 2ms | ✅ |
| repositoriesShouldBeSuffixed | 1ms | ✅ |
| entitiesShouldResideInEntityPackage | 1ms | ✅ |
| controllersShouldBeSuffixed | 1ms | ✅ |
| exceptionsShouldBeSuffixed | 1ms | ✅ |

### PackageDependencyTest

**Class Total Duration:** 29ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| commonModuleShouldNotDependOnOtherModules | 10ms | ✅ |
| clientModuleShouldNotDependOnServerOrOrderService | 9ms | ✅ |
| serverModuleShouldNotDependOnOrderService | 3ms | ✅ |
| entitiesShouldNotDependOnServicesOrControllers | 3ms | ✅ |
| repositoriesShouldOnlyDependOnEntitiesAndSpringData | 2ms | ✅ |
| controllersShouldNotDependOnOtherControllers | 1ms | ✅ |
| orderServiceShouldNotDependOnCatboxServer | 1ms | ✅ |

### SpringAnnotationTest

**Class Total Duration:** 13ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| controllersShouldBeAnnotatedWithRestController | 4ms | ✅ |
| repositoriesShouldExtendSpringDataRepository | 3ms | ✅ |
| servicesShouldBeAnnotatedWithService | 2ms | ✅ |
| configurationClassesShouldBeAnnotatedWithConfiguration | 2ms | ✅ |
| serviceMethodsShouldNotBePublicUnlessNecessary | 1ms | ✅ |
| entitiesShouldBeAnnotatedWithEntity | 1ms | ✅ |

### TransactionBoundaryTest

**Class Total Duration:** 25ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| repositoriesShouldNotDefineTransactions | 11ms | ✅ |
| serviceClassesShouldBeAnnotatedWithTransactional | 8ms | ✅ |
| serviceMethodsModifyingDataShouldBeTransactional | 4ms | ✅ |
| controllersShouldNotBeTransactional | 2ms | ✅ |


---

