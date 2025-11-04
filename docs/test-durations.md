# Test Duration Report

Generated: 2025-11-04T15:13:47.968385803Z

This report aggregates test durations across all modules in the build.

---

## Module: routebox-server

### Summary Statistics

- **Test Classes:** 23
- **Test Methods:** 108
- **Passed:** 108
- **Failed:** 0
- **Total Test Duration:** 16.51s
- **Module Execution Time:** 3m 15s
- **Average Test Duration:** 152ms

### Test Details

### AdminControllerTest

**Class Total Duration:** 667ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| adminPage_WithPendingOnlyFilter_ShouldReturnOnlyPending | 437ms | ✅ |
| adminPage_WithPagination_ShouldReturnPagedResults | 102ms | ✅ |
| adminPage_WithFilters_ShouldReturnFilteredResults | 43ms | ✅ |
| adminPage_WithSorting_ShouldReturnSortedResults | 32ms | ✅ |
| adminPage_ShouldReturnAdminView | 30ms | ✅ |
| adminPage_WithDefaultParameters_ShouldUseDefaults | 23ms | ✅ |

### CatboxApplicationTests

**Class Total Duration:** 5ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| contextLoads | 5ms | ✅ |

### CorrelationIdTest

**Class Total Duration:** 198ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| publishEvent_withCorrelationId_sendsCorrelationIdInKafkaHeader | 88ms | ✅ |
| publishEvent_withoutCorrelationId_sendsWithoutHeader | 70ms | ✅ |
| createEvent_withCorrelationId_storesInDatabase | 40ms | ✅ |

### DynamicKafkaTemplateFactoryEvictionTest

**Class Total Duration:** 1.04s

| Test Method | Duration | Status |
|-------------|----------|--------|
| testConcurrentAccessDoesNotCauseExceptions | 1.01s | ✅ |
| testBeanNamingConvention | 13ms | ✅ |
| testTemplateCreationAndCaching | 5ms | ✅ |
| testEvictionHandlesEmptyCache | 4ms | ✅ |
| testMultipleEvictionCallsDontCauseIssues | 3ms | ✅ |
| testTemplateReCreationAfterManualRemoval | 3ms | ✅ |

### DynamicKafkaTemplateFactoryProxyTest

**Class Total Duration:** 154ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| testMultipleCallsDoNotDuplicateBeans | 56ms | ✅ |
| testTemplateCanBeRetrievedDirectlyFromContext | 33ms | ✅ |
| testKafkaTemplateIsSingleton | 20ms | ✅ |
| testDynamicallyCreatedTemplateHasSpringLifecycle | 19ms | ✅ |
| testKafkaTemplateIsSpringManagedBean | 16ms | ✅ |
| testProducerFactoryIsSpringManagedBean | 10ms | ✅ |

### DynamicKafkaTemplateFactorySslBundlePositiveTest

**Class Total Duration:** 80ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| testClusterWithSslBundleHasSslConfiguration | 80ms | ✅ |

### DynamicKafkaTemplateFactorySslBundleTest

**Class Total Duration:** 20ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| testClusterWithoutSslBundleDoesNotHaveSslConfig | 11ms | ✅ |
| testMissingClusterThrowsException | 9ms | ✅ |

### E2EPollerMultiClusterTest

**Class Total Duration:** 7.17s

| Test Method | Duration | Status |
|-------------|----------|--------|
| testPollerRoutesEventsToCorrectClusters | 7.17s | ✅ |

### E2EPollerTest

**Class Total Duration:** 3.15s

| Test Method | Duration | Status |
|-------------|----------|--------|
| testPollerClaimsAndPublishesEvent | 3.15s | ✅ |

### OutboxArchivalServiceTest

**Class Total Duration:** 610ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| archiveOldEvents_movesOldSentEventsToArchive | 129ms | ✅ |
| archiveOldEvents_preservesKafkaMetadata | 104ms | ✅ |
| archiveOldEvents_preservesAllEventData | 82ms | ✅ |
| manualArchive_returnsZeroWhenNoEventsToArchive | 67ms | ✅ |
| manualArchive_archivesWithCustomRetention | 67ms | ✅ |
| manualArchive_returnsZeroForNegativeRetention | 57ms | ✅ |
| archiveOldEvents_doesNothingWhenNoOldEvents | 57ms | ✅ |
| manualArchive_returnsZeroForInvalidRetention | 47ms | ✅ |

### OutboxControllerTest

**Class Total Duration:** 406ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| markOutboxUnsent_ShouldMarkEventAsUnsent | 175ms | ✅ |
| getPendingOutboxEvents_ShouldReturnOnlyPending | 129ms | ✅ |
| searchOutbox_WithFilters_ShouldReturnFilteredResults | 80ms | ✅ |
| getAllOutboxEvents_ShouldReturnAllEvents | 22ms | ✅ |

### OutboxEventClaimTest

**Class Total Duration:** 124ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| testClaimPendingEvents_claimsExpiredInProgressEvents | 32ms | ✅ |
| testClaimPendingEvents_respectsBatchSize | 25ms | ✅ |
| testClaimPendingEvents_claimsEventsSuccessfully | 21ms | ✅ |
| testClaimPendingEvents_skipsEventsInProgress | 17ms | ✅ |
| testClaimPendingEvents_skipsAlreadySentEvents | 17ms | ✅ |
| testClaimPendingEvents_ordersEventsByCreatedAt | 12ms | ✅ |

### OutboxEventClaimerConcurrencyTest

**Class Total Duration:** 821ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| testConcurrentClaimersDoNotProcessSameEvents | 821ms | ✅ |

### OutboxEventPublisherMultiClusterTest

**Class Total Duration:** 224ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| allMustSucceed_successWhenAllClustersSucceed | 73ms | ✅ |
| atLeastOne_failsWhenAllClustersFail | 39ms | ✅ |
| optionalClusters_failsWhenRequiredClusterFails | 33ms | ✅ |
| atLeastOne_successWhenOnlyOneClusterSucceeds | 32ms | ✅ |
| allMustSucceed_failsWhenOneClusterFails | 25ms | ✅ |
| optionalClusters_successWhenRequiredSucceedsOptionalFails | 22ms | ✅ |

### OutboxEventPublisherTest

**Class Total Duration:** 758ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| publishEvent_resetsFailureCountOnSuccess | 379ms | ✅ |
| publishEvent_movesToDeadLetterAfterMaxPermanentRetries | 155ms | ✅ |
| publishEvent_handlesPermanentFailure_kafkaException | 66ms | ✅ |
| publishEvent_successfullySendsAndMarksSent | 54ms | ✅ |
| publishEvent_handlesPermanentFailure_noRoute | 53ms | ✅ |
| publishEvent_capturesKafkaMetadata | 51ms | ✅ |

### OutboxFailureHandlerTest

**Class Total Duration:** 327ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| recordPermanentFailure_movesToDeadLetterAfterMaxRetries | 98ms | ✅ |
| recordPermanentFailure_handlesMultipleEvents | 80ms | ✅ |
| recordPermanentFailure_incrementsFailureCount | 56ms | ✅ |
| recordPermanentFailure_throwsExceptionWhenEventNotFound | 45ms | ✅ |
| resetFailureCount_clearsFailureData | 28ms | ✅ |
| resetFailureCount_doesNothingWhenCountIsZero | 20ms | ✅ |

### OutboxMetricsServiceTest

**Class Total Duration:** 475ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| recordDeadLetter_incrementsCounter | 101ms | ✅ |
| updateArchivalMetrics_countsDeadLetterCorrectly | 93ms | ✅ |
| updatePendingEventsMetrics_calculatesOldestAge | 54ms | ✅ |
| updateArchivalMetrics_countsCorrectly | 47ms | ✅ |
| updatePendingEventsMetrics_whenNoEvents_setsToZero | 38ms | ✅ |
| updatePendingEventsMetrics_countsCorrectly | 32ms | ✅ |
| updateArchivalMetrics_whenNoEvents_setsToZero | 24ms | ✅ |
| recordPublishFailure_incrementsCounter | 21ms | ✅ |
| recordProcessingDuration_recordsTimer | 19ms | ✅ |
| metricsAreRegistered | 16ms | ✅ |
| recordPublishSuccess_incrementsCounter | 15ms | ✅ |
| recordArchival_incrementsCounter | 15ms | ✅ |

### OutboxProcessingConfigTest

**Class Total Duration:** 12ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| config_loadsMaxPermanentRetries | 5ms | ✅ |
| config_loadsDefaultPermanentFailureExceptions | 4ms | ✅ |
| config_hasExistingProcessingSettings | 3ms | ✅ |

### OutboxRoutingConfigTest

**Class Total Duration:** 7ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| getRoutingRule_handlesOptionalClusters | 3ms | ✅ |
| getRoutingRule_throwsExceptionForInvalidFormat | 1ms | ✅ |
| getRoutingRule_handlesMultipleClustersWithStrategy | 1ms | ✅ |
| getRoutingRule_handlesIndexedOptionalProperties | 1ms | ✅ |
| getRoutingRule_handlesIndexedPropertiesFromDynamicRegistry | 1ms | ✅ |
| getRoutingRule_returnsNullForUnknownEventType | 0ms | ✅ |
| getRoutingRule_handlesStrategyWithUnderscores | 0ms | ✅ |
| getRoutingRule_handlesSingleClusterAsString | 0ms | ✅ |
| getRoutingRule_throwsExceptionForInvalidClustersFormat | 0ms | ✅ |
| getRoutingRule_handlesSingleOptionalClusterAsString | 0ms | ✅ |
| getRoutingRule_defaultsToAllMustSucceedWhenStrategyNotSpecified | 0ms | ✅ |
| getRoutingRule_handlesStrategyWithHyphens | 0ms | ✅ |
| getRoutingRule_handlesBackwardCompatibleStringFormat | 0ms | ✅ |

### OutboxServiceTest

**Class Total Duration:** 241ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| findPaged_filtersWithAggregateIdAndPending | 44ms | ✅ |
| findPaged_filtersWithBlankValues | 35ms | ✅ |
| findPaged_filtersAndSorts | 34ms | ✅ |
| markUnsent_clearsSentAtAndLease | 29ms | ✅ |
| markUnsent_throwsExceptionWhenNotFound | 29ms | ✅ |
| getPendingEvents_returnsAllUnsent | 26ms | ✅ |
| getAllEvents_returnsAllEvents | 25ms | ✅ |
| findPaged_withAllNullParameters | 19ms | ✅ |

### SecurityConfigTest

**Class Total Duration:** 6ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| shouldHaveDisabledSecurityByDefault | 3ms | ✅ |
| shouldLoadSecurityFilterChain | 3ms | ✅ |

### SecurityConfigurationTest

**Class Total Duration:** 8ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| testSslBundleIsConfigured | 3ms | ✅ |
| testSecureClusterConfiguration | 3ms | ✅ |
| testSaslConfigurationProperties | 2ms | ✅ |

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
- **Total Test Duration:** 2.00s
- **Module Execution Time:** 37.10s
- **Average Test Duration:** 83ms

### Test Details

### OrderControllerTest

**Class Total Duration:** 393ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| getAllOrders_ShouldReturnAllOrders | 192ms | ✅ |
| updateOrderStatus_WithNullStatus_ShouldReturnBadRequest | 66ms | ✅ |
| createOrder_ShouldReturnCreatedOrder | 47ms | ✅ |
| getOrderById_ShouldReturnOrder | 39ms | ✅ |
| updateOrderStatus_WithBlankStatus_ShouldReturnBadRequest | 26ms | ✅ |
| updateOrderStatus_ShouldUpdateAndReturnOrder | 23ms | ✅ |

### OrderNotFoundExceptionTest

**Class Total Duration:** 163ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| shouldCreateExceptionWithCorrectMessage | 157ms | ✅ |
| shouldBeRuntimeException | 6ms | ✅ |

### OrderServiceFailureTest

**Class Total Duration:** 223ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| testOrderCreationFailsWhenOutboxWriteFails | 223ms | ✅ |

### OrderServiceTest

**Class Total Duration:** 1.15s

| Test Method | Duration | Status |
|-------------|----------|--------|
| testGetAllOrders | 946ms | ✅ |
| testUpdateOrderStatus_CreatesOutboxEvent | 83ms | ✅ |
| testCreateOrder_CreatesOrderAndOutboxEvent | 61ms | ✅ |
| testGetOrderById_ThrowsExceptionWhenNotFound | 33ms | ✅ |
| testGetOrderById_Success | 28ms | ✅ |

### OrderTest

**Class Total Duration:** 8ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| onCreate_shouldNotOverrideExistingStatus | 4ms | ✅ |
| shouldCreateOrderWithConstructor | 2ms | ✅ |
| shouldSupportSettersAndGetters | 1ms | ✅ |
| onCreate_shouldSetDefaultStatus | 1ms | ✅ |

### TracingConfigurationTest

**Class Total Duration:** 39ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| tracerBeanShouldBeAvailable | 18ms | ✅ |
| tracerShouldCreateSpans | 12ms | ✅ |
| tracerShouldPropagateContext | 9ms | ✅ |

### UpdateStatusRequestTest

**Class Total Duration:** 18ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| shouldSupportEquality | 16ms | ✅ |
| shouldCreateRequestWithStatus | 1ms | ✅ |
| shouldHandleNullStatus | 1ms | ✅ |


---

