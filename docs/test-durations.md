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

## Module: catbox-server

### Summary Statistics

- **Test Classes:** 23
- **Test Methods:** 108
- **Passed:** 108
- **Failed:** 0
- **Total Test Duration:** 15.23s
- **Module Execution Time:** 2m 54s
- **Average Test Duration:** 141ms

### Test Details

### AdminControllerTest

**Class Total Duration:** 781ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| adminPage_WithPendingOnlyFilter_ShouldReturnOnlyPending | 514ms | ✅ |
| adminPage_WithPagination_ShouldReturnPagedResults | 119ms | ✅ |
| adminPage_WithSorting_ShouldReturnSortedResults | 51ms | ✅ |
| adminPage_WithFilters_ShouldReturnFilteredResults | 41ms | ✅ |
| adminPage_ShouldReturnAdminView | 31ms | ✅ |
| adminPage_WithDefaultParameters_ShouldUseDefaults | 25ms | ✅ |

### CatboxApplicationTests

**Class Total Duration:** 3ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| contextLoads | 3ms | ✅ |

### CorrelationIdTest

**Class Total Duration:** 154ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| publishEvent_withCorrelationId_sendsCorrelationIdInKafkaHeader | 69ms | ✅ |
| createEvent_withCorrelationId_storesInDatabase | 43ms | ✅ |
| publishEvent_withoutCorrelationId_sendsWithoutHeader | 42ms | ✅ |

### DynamicKafkaTemplateFactoryEvictionTest

**Class Total Duration:** 1.04s

| Test Method | Duration | Status |
|-------------|----------|--------|
| testConcurrentAccessDoesNotCauseExceptions | 1.01s | ✅ |
| testBeanNamingConvention | 14ms | ✅ |
| testTemplateCreationAndCaching | 4ms | ✅ |
| testEvictionHandlesEmptyCache | 4ms | ✅ |
| testTemplateReCreationAfterManualRemoval | 4ms | ✅ |
| testMultipleEvictionCallsDontCauseIssues | 3ms | ✅ |

### DynamicKafkaTemplateFactoryProxyTest

**Class Total Duration:** 99ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| testMultipleCallsDoNotDuplicateBeans | 37ms | ✅ |
| testTemplateCanBeRetrievedDirectlyFromContext | 28ms | ✅ |
| testDynamicallyCreatedTemplateHasSpringLifecycle | 9ms | ✅ |
| testKafkaTemplateIsSingleton | 9ms | ✅ |
| testKafkaTemplateIsSpringManagedBean | 8ms | ✅ |
| testProducerFactoryIsSpringManagedBean | 8ms | ✅ |

### DynamicKafkaTemplateFactorySslBundlePositiveTest

**Class Total Duration:** 53ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| testClusterWithSslBundleHasSslConfiguration | 53ms | ✅ |

### DynamicKafkaTemplateFactorySslBundleTest

**Class Total Duration:** 25ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| testClusterWithoutSslBundleDoesNotHaveSslConfig | 15ms | ✅ |
| testMissingClusterThrowsException | 10ms | ✅ |

### E2EPollerMultiClusterTest

**Class Total Duration:** 6.46s

| Test Method | Duration | Status |
|-------------|----------|--------|
| testPollerRoutesEventsToCorrectClusters | 6.46s | ✅ |

### E2EPollerTest

**Class Total Duration:** 2.87s

| Test Method | Duration | Status |
|-------------|----------|--------|
| testPollerClaimsAndPublishesEvent | 2.87s | ✅ |

### OutboxArchivalServiceTest

**Class Total Duration:** 559ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| archiveOldEvents_movesOldSentEventsToArchive | 115ms | ✅ |
| archiveOldEvents_preservesKafkaMetadata | 82ms | ✅ |
| manualArchive_returnsZeroForInvalidRetention | 77ms | ✅ |
| archiveOldEvents_preservesAllEventData | 67ms | ✅ |
| manualArchive_archivesWithCustomRetention | 66ms | ✅ |
| manualArchive_returnsZeroWhenNoEventsToArchive | 66ms | ✅ |
| archiveOldEvents_doesNothingWhenNoOldEvents | 48ms | ✅ |
| manualArchive_returnsZeroForNegativeRetention | 38ms | ✅ |

### OutboxControllerTest

**Class Total Duration:** 381ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| markOutboxUnsent_ShouldMarkEventAsUnsent | 133ms | ✅ |
| getPendingOutboxEvents_ShouldReturnOnlyPending | 124ms | ✅ |
| searchOutbox_WithFilters_ShouldReturnFilteredResults | 92ms | ✅ |
| getAllOutboxEvents_ShouldReturnAllEvents | 32ms | ✅ |

### OutboxEventClaimTest

**Class Total Duration:** 120ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| testClaimPendingEvents_claimsExpiredInProgressEvents | 35ms | ✅ |
| testClaimPendingEvents_claimsEventsSuccessfully | 22ms | ✅ |
| testClaimPendingEvents_respectsBatchSize | 18ms | ✅ |
| testClaimPendingEvents_skipsAlreadySentEvents | 17ms | ✅ |
| testClaimPendingEvents_skipsEventsInProgress | 15ms | ✅ |
| testClaimPendingEvents_ordersEventsByCreatedAt | 13ms | ✅ |

### OutboxEventClaimerConcurrencyTest

**Class Total Duration:** 775ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| testConcurrentClaimersDoNotProcessSameEvents | 775ms | ✅ |

### OutboxEventPublisherMultiClusterTest

**Class Total Duration:** 200ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| allMustSucceed_successWhenAllClustersSucceed | 44ms | ✅ |
| atLeastOne_successWhenOnlyOneClusterSucceeds | 38ms | ✅ |
| optionalClusters_failsWhenRequiredClusterFails | 32ms | ✅ |
| atLeastOne_failsWhenAllClustersFail | 32ms | ✅ |
| optionalClusters_successWhenRequiredSucceedsOptionalFails | 32ms | ✅ |
| allMustSucceed_failsWhenOneClusterFails | 22ms | ✅ |

### OutboxEventPublisherTest

**Class Total Duration:** 760ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| publishEvent_resetsFailureCountOnSuccess | 348ms | ✅ |
| publishEvent_movesToDeadLetterAfterMaxPermanentRetries | 187ms | ✅ |
| publishEvent_handlesPermanentFailure_kafkaException | 62ms | ✅ |
| publishEvent_capturesKafkaMetadata | 59ms | ✅ |
| publishEvent_successfullySendsAndMarksSent | 56ms | ✅ |
| publishEvent_handlesPermanentFailure_noRoute | 48ms | ✅ |

### OutboxFailureHandlerTest

**Class Total Duration:** 264ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| recordPermanentFailure_movesToDeadLetterAfterMaxRetries | 89ms | ✅ |
| recordPermanentFailure_handlesMultipleEvents | 67ms | ✅ |
| recordPermanentFailure_incrementsFailureCount | 36ms | ✅ |
| recordPermanentFailure_throwsExceptionWhenEventNotFound | 27ms | ✅ |
| resetFailureCount_clearsFailureData | 26ms | ✅ |
| resetFailureCount_doesNothingWhenCountIsZero | 19ms | ✅ |

### OutboxMetricsServiceTest

**Class Total Duration:** 450ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| recordDeadLetter_incrementsCounter | 99ms | ✅ |
| updateArchivalMetrics_countsDeadLetterCorrectly | 97ms | ✅ |
| updatePendingEventsMetrics_calculatesOldestAge | 52ms | ✅ |
| updateArchivalMetrics_countsCorrectly | 35ms | ✅ |
| updatePendingEventsMetrics_whenNoEvents_setsToZero | 34ms | ✅ |
| updatePendingEventsMetrics_countsCorrectly | 29ms | ✅ |
| recordProcessingDuration_recordsTimer | 21ms | ✅ |
| updateArchivalMetrics_whenNoEvents_setsToZero | 21ms | ✅ |
| recordArchival_incrementsCounter | 19ms | ✅ |
| recordPublishFailure_incrementsCounter | 17ms | ✅ |
| recordPublishSuccess_incrementsCounter | 14ms | ✅ |
| metricsAreRegistered | 12ms | ✅ |

### OutboxProcessingConfigTest

**Class Total Duration:** 9ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| config_loadsDefaultPermanentFailureExceptions | 3ms | ✅ |
| config_hasExistingProcessingSettings | 3ms | ✅ |
| config_loadsMaxPermanentRetries | 3ms | ✅ |

### OutboxRoutingConfigTest

**Class Total Duration:** 4ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| getRoutingRule_handlesOptionalClusters | 2ms | ✅ |
| getRoutingRule_throwsExceptionForInvalidFormat | 1ms | ✅ |
| getRoutingRule_handlesIndexedOptionalProperties | 1ms | ✅ |
| getRoutingRule_handlesStrategyWithHyphens | 0ms | ✅ |
| getRoutingRule_handlesSingleOptionalClusterAsString | 0ms | ✅ |
| getRoutingRule_handlesStrategyWithUnderscores | 0ms | ✅ |
| getRoutingRule_defaultsToAllMustSucceedWhenStrategyNotSpecified | 0ms | ✅ |
| getRoutingRule_handlesMultipleClustersWithStrategy | 0ms | ✅ |
| getRoutingRule_handlesIndexedPropertiesFromDynamicRegistry | 0ms | ✅ |
| getRoutingRule_throwsExceptionForInvalidClustersFormat | 0ms | ✅ |
| getRoutingRule_returnsNullForUnknownEventType | 0ms | ✅ |
| getRoutingRule_handlesSingleClusterAsString | 0ms | ✅ |
| getRoutingRule_handlesBackwardCompatibleStringFormat | 0ms | ✅ |

### OutboxServiceTest

**Class Total Duration:** 206ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| findPaged_filtersWithAggregateIdAndPending | 39ms | ✅ |
| findPaged_filtersAndSorts | 29ms | ✅ |
| markUnsent_clearsSentAtAndLease | 27ms | ✅ |
| findPaged_filtersWithBlankValues | 26ms | ✅ |
| getAllEvents_returnsAllEvents | 26ms | ✅ |
| markUnsent_throwsExceptionWhenNotFound | 23ms | ✅ |
| getPendingEvents_returnsAllUnsent | 18ms | ✅ |
| findPaged_withAllNullParameters | 18ms | ✅ |

### SecurityConfigTest

**Class Total Duration:** 5ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| shouldHaveDisabledSecurityByDefault | 3ms | ✅ |
| shouldLoadSecurityFilterChain | 2ms | ✅ |

### SecurityConfigurationTest

**Class Total Duration:** 8ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| testSecureClusterConfiguration | 4ms | ✅ |
| testSaslConfigurationProperties | 2ms | ✅ |
| testSslBundleIsConfigured | 2ms | ✅ |

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
- **Total Test Duration:** 1.76s
- **Module Execution Time:** 32.28s
- **Average Test Duration:** 73ms

### Test Details

### OrderControllerTest

**Class Total Duration:** 385ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| getAllOrders_ShouldReturnAllOrders | 186ms | ✅ |
| updateOrderStatus_WithNullStatus_ShouldReturnBadRequest | 61ms | ✅ |
| createOrder_ShouldReturnCreatedOrder | 55ms | ✅ |
| getOrderById_ShouldReturnOrder | 37ms | ✅ |
| updateOrderStatus_WithBlankStatus_ShouldReturnBadRequest | 23ms | ✅ |
| updateOrderStatus_ShouldUpdateAndReturnOrder | 23ms | ✅ |

### OrderNotFoundExceptionTest

**Class Total Duration:** 161ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| shouldCreateExceptionWithCorrectMessage | 154ms | ✅ |
| shouldBeRuntimeException | 7ms | ✅ |

### OrderServiceFailureTest

**Class Total Duration:** 266ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| testOrderCreationFailsWhenOutboxWriteFails | 266ms | ✅ |

### OrderServiceTest

**Class Total Duration:** 883ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| testGetAllOrders | 760ms | ✅ |
| testCreateOrder_CreatesOrderAndOutboxEvent | 48ms | ✅ |
| testUpdateOrderStatus_CreatesOutboxEvent | 30ms | ✅ |
| testGetOrderById_Success | 24ms | ✅ |
| testGetOrderById_ThrowsExceptionWhenNotFound | 21ms | ✅ |

### OrderTest

**Class Total Duration:** 5ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| onCreate_shouldNotOverrideExistingStatus | 2ms | ✅ |
| shouldCreateOrderWithConstructor | 1ms | ✅ |
| shouldSupportSettersAndGetters | 1ms | ✅ |
| onCreate_shouldSetDefaultStatus | 1ms | ✅ |

### TracingConfigurationTest

**Class Total Duration:** 38ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| tracerShouldCreateSpans | 16ms | ✅ |
| tracerBeanShouldBeAvailable | 15ms | ✅ |
| tracerShouldPropagateContext | 7ms | ✅ |

### UpdateStatusRequestTest

**Class Total Duration:** 25ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| shouldSupportEquality | 22ms | ✅ |
| shouldCreateRequestWithStatus | 2ms | ✅ |
| shouldHandleNullStatus | 1ms | ✅ |


---

## Module: order-processor

### Summary Statistics

- **Test Classes:** 3
- **Test Methods:** 16
- **Passed:** 16
- **Failed:** 0
- **Total Test Duration:** 7.86s
- **Module Execution Time:** 24.77s
- **Average Test Duration:** 491ms

### Test Details

### OrderEventListenerTest

**Class Total Duration:** 2.50s

| Test Method | Duration | Status |
|-------------|----------|--------|
| testHandleOrderCreated_DuplicateMessage_SkippedAndAcknowledged | 2.36s | ✅ |
| testHandleOrderCreated_UnexpectedException_Acknowledged | 65ms | ✅ |
| testHandleOrderCreated_NullCorrelationId_ProcessedAndAcknowledged | 34ms | ✅ |
| testHandleOrderCreated_ProcessingException_NotAcknowledged | 16ms | ✅ |
| testHandleOrderStatusChanged_NewMessage_ProcessedAndAcknowledged | 10ms | ✅ |
| testHandleOrderCreated_NewMessage_ProcessedAndAcknowledged | 10ms | ✅ |
| testHandleOrderStatusChanged_DuplicateMessage_SkippedAndAcknowledged | 8ms | ✅ |

### OrderEventProcessingServiceTest

**Class Total Duration:** 1.46s

| Test Method | Duration | Status |
|-------------|----------|--------|
| testProcessOrderStatusChanged_Success | 666ms | ✅ |
| testProcessOrderCreated_Success | 632ms | ✅ |
| testCounters_TrackProcessedEvents | 119ms | ✅ |
| testResetCounters | 42ms | ✅ |

### OrderProcessorE2ETest

**Class Total Duration:** 3.90s

| Test Method | Duration | Status |
|-------------|----------|--------|
| testMixedScenario_UniqueAndDuplicateMessages | 1.70s | ✅ |
| testDeduplication_DuplicateMessages_ProcessedOnlyOnce | 1.63s | ✅ |
| testMultipleUniqueMessages_AllProcessed | 337ms | ✅ |
| testHappyPath_SingleMessage_ProcessedSuccessfully | 123ms | ✅ |
| testMessageWithoutCorrelationId_StillProcessed | 115ms | ✅ |


---

## Module: catbox-archunit

### Summary Statistics

- **Test Classes:** 6
- **Test Methods:** 32
- **Passed:** 32
- **Failed:** 0
- **Total Test Duration:** 210ms
- **Module Execution Time:** 4.32s
- **Average Test Duration:** 6ms

### Test Details

### EntityRepositoryPatternTest

**Class Total Duration:** 108ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| tableAnnotationShouldBeUsedForEntities | 71ms | ✅ |
| entitiesShouldNotHavePublicFields | 24ms | ✅ |
| repositoriesShouldBeInterfaces | 7ms | ✅ |
| entitiesIdFieldsShouldBeAnnotatedWithId | 4ms | ✅ |
| entitiesShouldResideInEntityPackage | 2ms | ✅ |

### LayeringArchitectureTest

**Class Total Duration:** 25ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| repositoriesShouldNotAccessServicesOrControllers | 20ms | ✅ |
| servicesShouldNotAccessControllers | 4ms | ✅ |
| controllersShouldNotAccessRepositoriesDirectly | 1ms | ✅ |

### NamingConventionTest

**Class Total Duration:** 12ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| servicesShouldBeSuffixed | 4ms | ✅ |
| configurationsShouldBeSuffixed | 2ms | ✅ |
| dtosShouldResideInDtoPackage | 2ms | ✅ |
| repositoriesShouldBeSuffixed | 1ms | ✅ |
| entitiesShouldResideInEntityPackage | 1ms | ✅ |
| controllersShouldBeSuffixed | 1ms | ✅ |
| exceptionsShouldBeSuffixed | 1ms | ✅ |

### PackageDependencyTest

**Class Total Duration:** 27ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| commonModuleShouldNotDependOnOtherModules | 10ms | ✅ |
| clientModuleShouldNotDependOnServerOrOrderService | 8ms | ✅ |
| entitiesShouldNotDependOnServicesOrControllers | 3ms | ✅ |
| repositoriesShouldOnlyDependOnEntitiesAndSpringData | 2ms | ✅ |
| serverModuleShouldNotDependOnOrderService | 2ms | ✅ |
| controllersShouldNotDependOnOtherControllers | 1ms | ✅ |
| orderServiceShouldNotDependOnCatboxServer | 1ms | ✅ |

### SpringAnnotationTest

**Class Total Duration:** 16ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| repositoriesShouldExtendSpringDataRepository | 4ms | ✅ |
| controllersShouldBeAnnotatedWithRestController | 4ms | ✅ |
| configurationClassesShouldBeAnnotatedWithConfiguration | 3ms | ✅ |
| serviceMethodsShouldNotBePublicUnlessNecessary | 2ms | ✅ |
| entitiesShouldBeAnnotatedWithEntity | 2ms | ✅ |
| servicesShouldBeAnnotatedWithService | 1ms | ✅ |

### TransactionBoundaryTest

**Class Total Duration:** 22ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| repositoriesShouldNotDefineTransactions | 10ms | ✅ |
| serviceClassesShouldBeAnnotatedWithTransactional | 8ms | ✅ |
| serviceMethodsModifyingDataShouldBeTransactional | 3ms | ✅ |
| controllersShouldNotBeTransactional | 1ms | ✅ |


---

