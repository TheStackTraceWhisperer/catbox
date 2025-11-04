# Test Duration Report

Generated: 2025-11-04T00:39:33.813053781Z

This report aggregates test durations across all modules in the build.

---

## Module: routebox-server

### Summary Statistics

- **Test Classes:** 22
- **Test Methods:** 105
- **Passed:** 105
- **Failed:** 0
- **Total Test Duration:** 15.37s
- **Module Execution Time:** 2m 55s
- **Average Test Duration:** 146ms

### Test Details

### AdminControllerTest

**Class Total Duration:** 682ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| adminPage_WithPendingOnlyFilter_ShouldReturnOnlyPending | 469ms | ✅ |
| adminPage_WithPagination_ShouldReturnPagedResults | 104ms | ✅ |
| adminPage_WithFilters_ShouldReturnFilteredResults | 41ms | ✅ |
| adminPage_ShouldReturnAdminView | 24ms | ✅ |
| adminPage_WithSorting_ShouldReturnSortedResults | 22ms | ✅ |
| adminPage_WithDefaultParameters_ShouldUseDefaults | 22ms | ✅ |

### CorrelationIdTest

**Class Total Duration:** 139ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| publishEvent_withoutCorrelationId_sendsWithoutHeader | 61ms | ✅ |
| publishEvent_withCorrelationId_sendsCorrelationIdInKafkaHeader | 52ms | ✅ |
| createEvent_withCorrelationId_storesInDatabase | 26ms | ✅ |

### DynamicKafkaTemplateFactoryEvictionTest

**Class Total Duration:** 1.04s

| Test Method | Duration | Status |
|-------------|----------|--------|
| testConcurrentAccessDoesNotCauseExceptions | 1.01s | ✅ |
| testBeanNamingConvention | 12ms | ✅ |
| testTemplateCreationAndCaching | 7ms | ✅ |
| testEvictionHandlesEmptyCache | 6ms | ✅ |
| testMultipleEvictionCallsDontCauseIssues | 4ms | ✅ |
| testTemplateReCreationAfterManualRemoval | 4ms | ✅ |

### DynamicKafkaTemplateFactoryProxyTest

**Class Total Duration:** 84ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| testMultipleCallsDoNotDuplicateBeans | 39ms | ✅ |
| testTemplateCanBeRetrievedDirectlyFromContext | 14ms | ✅ |
| testKafkaTemplateIsSingleton | 9ms | ✅ |
| testDynamicallyCreatedTemplateHasSpringLifecycle | 8ms | ✅ |
| testProducerFactoryIsSpringManagedBean | 7ms | ✅ |
| testKafkaTemplateIsSpringManagedBean | 7ms | ✅ |

### DynamicKafkaTemplateFactorySslBundlePositiveTest

**Class Total Duration:** 65ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| testClusterWithSslBundleHasSslConfiguration | 65ms | ✅ |

### DynamicKafkaTemplateFactorySslBundleTest

**Class Total Duration:** 31ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| testMissingClusterThrowsException | 16ms | ✅ |
| testClusterWithoutSslBundleDoesNotHaveSslConfig | 15ms | ✅ |

### E2EPollerMultiClusterTest

**Class Total Duration:** 6.69s

| Test Method | Duration | Status |
|-------------|----------|--------|
| testPollerRoutesEventsToCorrectClusters | 6.69s | ✅ |

### E2EPollerTest

**Class Total Duration:** 2.60s

| Test Method | Duration | Status |
|-------------|----------|--------|
| testPollerClaimsAndPublishesEvent | 2.60s | ✅ |

### OutboxArchivalServiceTest

**Class Total Duration:** 485ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| archiveOldEvents_movesOldSentEventsToArchive | 112ms | ✅ |
| archiveOldEvents_preservesKafkaMetadata | 102ms | ✅ |
| manualArchive_returnsZeroWhenNoEventsToArchive | 53ms | ✅ |
| archiveOldEvents_preservesAllEventData | 52ms | ✅ |
| manualArchive_returnsZeroForInvalidRetention | 50ms | ✅ |
| archiveOldEvents_doesNothingWhenNoOldEvents | 42ms | ✅ |
| manualArchive_archivesWithCustomRetention | 39ms | ✅ |
| manualArchive_returnsZeroForNegativeRetention | 35ms | ✅ |

### OutboxControllerTest

**Class Total Duration:** 467ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| getPendingOutboxEvents_ShouldReturnOnlyPending | 186ms | ✅ |
| markOutboxUnsent_ShouldMarkEventAsUnsent | 184ms | ✅ |
| searchOutbox_WithFilters_ShouldReturnFilteredResults | 70ms | ✅ |
| getAllOutboxEvents_ShouldReturnAllEvents | 27ms | ✅ |

### OutboxEventClaimTest

**Class Total Duration:** 173ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| testClaimPendingEvents_claimsExpiredInProgressEvents | 68ms | ✅ |
| testClaimPendingEvents_skipsAlreadySentEvents | 31ms | ✅ |
| testClaimPendingEvents_claimsEventsSuccessfully | 30ms | ✅ |
| testClaimPendingEvents_skipsEventsInProgress | 16ms | ✅ |
| testClaimPendingEvents_ordersEventsByCreatedAt | 14ms | ✅ |
| testClaimPendingEvents_respectsBatchSize | 14ms | ✅ |

### OutboxEventClaimerConcurrencyTest

**Class Total Duration:** 844ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| testConcurrentClaimersDoNotProcessSameEvents | 844ms | ✅ |

### OutboxEventPublisherMultiClusterTest

**Class Total Duration:** 227ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| allMustSucceed_successWhenAllClustersSucceed | 54ms | ✅ |
| atLeastOne_failsWhenAllClustersFail | 48ms | ✅ |
| optionalClusters_successWhenRequiredSucceedsOptionalFails | 46ms | ✅ |
| optionalClusters_failsWhenRequiredClusterFails | 30ms | ✅ |
| atLeastOne_successWhenOnlyOneClusterSucceeds | 27ms | ✅ |
| allMustSucceed_failsWhenOneClusterFails | 22ms | ✅ |

### OutboxEventPublisherTest

**Class Total Duration:** 725ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| publishEvent_resetsFailureCountOnSuccess | 365ms | ✅ |
| publishEvent_movesToDeadLetterAfterMaxPermanentRetries | 134ms | ✅ |
| publishEvent_successfullySendsAndMarksSent | 64ms | ✅ |
| publishEvent_capturesKafkaMetadata | 56ms | ✅ |
| publishEvent_handlesPermanentFailure_kafkaException | 56ms | ✅ |
| publishEvent_handlesPermanentFailure_noRoute | 50ms | ✅ |

### OutboxFailureHandlerTest

**Class Total Duration:** 267ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| recordPermanentFailure_movesToDeadLetterAfterMaxRetries | 93ms | ✅ |
| recordPermanentFailure_handlesMultipleEvents | 69ms | ✅ |
| recordPermanentFailure_incrementsFailureCount | 37ms | ✅ |
| resetFailureCount_clearsFailureData | 27ms | ✅ |
| recordPermanentFailure_throwsExceptionWhenEventNotFound | 27ms | ✅ |
| resetFailureCount_doesNothingWhenCountIsZero | 14ms | ✅ |

### OutboxMetricsServiceTest

**Class Total Duration:** 491ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| updateArchivalMetrics_countsDeadLetterCorrectly | 123ms | ✅ |
| recordDeadLetter_incrementsCounter | 103ms | ✅ |
| updatePendingEventsMetrics_calculatesOldestAge | 57ms | ✅ |
| updatePendingEventsMetrics_whenNoEvents_setsToZero | 38ms | ✅ |
| updateArchivalMetrics_whenNoEvents_setsToZero | 34ms | ✅ |
| updatePendingEventsMetrics_countsCorrectly | 32ms | ✅ |
| updateArchivalMetrics_countsCorrectly | 28ms | ✅ |
| recordProcessingDuration_recordsTimer | 19ms | ✅ |
| recordPublishFailure_incrementsCounter | 18ms | ✅ |
| recordArchival_incrementsCounter | 15ms | ✅ |
| metricsAreRegistered | 12ms | ✅ |
| recordPublishSuccess_incrementsCounter | 12ms | ✅ |

### OutboxProcessingConfigTest

**Class Total Duration:** 10ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| config_loadsMaxPermanentRetries | 4ms | ✅ |
| config_hasExistingProcessingSettings | 3ms | ✅ |
| config_loadsDefaultPermanentFailureExceptions | 3ms | ✅ |

### OutboxRoutingConfigTest

**Class Total Duration:** 3ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| getRoutingRule_handlesOptionalClusters | 2ms | ✅ |
| getRoutingRule_handlesIndexedOptionalProperties | 1ms | ✅ |
| getRoutingRule_returnsNullForUnknownEventType | 0ms | ✅ |
| getRoutingRule_throwsExceptionForInvalidFormat | 0ms | ✅ |
| getRoutingRule_handlesStrategyWithUnderscores | 0ms | ✅ |
| getRoutingRule_handlesSingleClusterAsString | 0ms | ✅ |
| getRoutingRule_throwsExceptionForInvalidClustersFormat | 0ms | ✅ |
| getRoutingRule_handlesSingleOptionalClusterAsString | 0ms | ✅ |
| getRoutingRule_handlesMultipleClustersWithStrategy | 0ms | ✅ |
| getRoutingRule_defaultsToAllMustSucceedWhenStrategyNotSpecified | 0ms | ✅ |
| getRoutingRule_handlesStrategyWithHyphens | 0ms | ✅ |
| getRoutingRule_handlesBackwardCompatibleStringFormat | 0ms | ✅ |
| getRoutingRule_handlesIndexedPropertiesFromDynamicRegistry | 0ms | ✅ |

### OutboxServiceTest

**Class Total Duration:** 291ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| findPaged_filtersWithAggregateIdAndPending | 57ms | ✅ |
| markUnsent_throwsExceptionWhenNotFound | 52ms | ✅ |
| markUnsent_clearsSentAtAndLease | 38ms | ✅ |
| findPaged_filtersWithBlankValues | 37ms | ✅ |
| findPaged_filtersAndSorts | 30ms | ✅ |
| getPendingEvents_returnsAllUnsent | 27ms | ✅ |
| findPaged_withAllNullParameters | 26ms | ✅ |
| getAllEvents_returnsAllEvents | 24ms | ✅ |

### RouteBoxApplicationTests

**Class Total Duration:** 4ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| contextLoads | 4ms | ✅ |

### SecurityConfigTest

**Class Total Duration:** 12ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| shouldHaveDisabledSecurityByDefault | 6ms | ✅ |
| shouldLoadSecurityFilterChain | 6ms | ✅ |

### SecurityConfigurationTest

**Class Total Duration:** 35ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| testSecureClusterConfiguration | 26ms | ✅ |
| testSaslConfigurationProperties | 7ms | ✅ |
| testSslBundleIsConfigured | 2ms | ✅ |


---

