# Test Duration Report

Generated: 2025-11-04T02:13:57.362114040Z

This report aggregates test durations across all modules in the build.

---

## Module: catbox-server

### Summary Statistics

- **Test Classes:** 23
- **Test Methods:** 108
- **Passed:** 108
- **Failed:** 0
- **Total Test Duration:** 15.49s
- **Module Execution Time:** 3m 0s
- **Average Test Duration:** 143ms

### Test Details

### AdminControllerTest

**Class Total Duration:** 703ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| adminPage_WithPendingOnlyFilter_ShouldReturnOnlyPending | 479ms | ✅ |
| adminPage_WithPagination_ShouldReturnPagedResults | 94ms | ✅ |
| adminPage_WithSorting_ShouldReturnSortedResults | 45ms | ✅ |
| adminPage_WithFilters_ShouldReturnFilteredResults | 37ms | ✅ |
| adminPage_ShouldReturnAdminView | 26ms | ✅ |
| adminPage_WithDefaultParameters_ShouldUseDefaults | 22ms | ✅ |

### CatboxApplicationTests

**Class Total Duration:** 5ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| contextLoads | 5ms | ✅ |

### CorrelationIdTest

**Class Total Duration:** 130ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| publishEvent_withCorrelationId_sendsCorrelationIdInKafkaHeader | 52ms | ✅ |
| publishEvent_withoutCorrelationId_sendsWithoutHeader | 51ms | ✅ |
| createEvent_withCorrelationId_storesInDatabase | 27ms | ✅ |

### DynamicKafkaTemplateFactoryEvictionTest

**Class Total Duration:** 1.04s

| Test Method | Duration | Status |
|-------------|----------|--------|
| testConcurrentAccessDoesNotCauseExceptions | 1.01s | ✅ |
| testBeanNamingConvention | 12ms | ✅ |
| testTemplateCreationAndCaching | 4ms | ✅ |
| testEvictionHandlesEmptyCache | 4ms | ✅ |
| testTemplateReCreationAfterManualRemoval | 3ms | ✅ |
| testMultipleEvictionCallsDontCauseIssues | 3ms | ✅ |

### DynamicKafkaTemplateFactoryProxyTest

**Class Total Duration:** 86ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| testMultipleCallsDoNotDuplicateBeans | 36ms | ✅ |
| testTemplateCanBeRetrievedDirectlyFromContext | 22ms | ✅ |
| testDynamicallyCreatedTemplateHasSpringLifecycle | 7ms | ✅ |
| testKafkaTemplateIsSpringManagedBean | 7ms | ✅ |
| testKafkaTemplateIsSingleton | 7ms | ✅ |
| testProducerFactoryIsSpringManagedBean | 7ms | ✅ |

### DynamicKafkaTemplateFactorySslBundlePositiveTest

**Class Total Duration:** 59ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| testClusterWithSslBundleHasSslConfiguration | 59ms | ✅ |

### DynamicKafkaTemplateFactorySslBundleTest

**Class Total Duration:** 23ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| testClusterWithoutSslBundleDoesNotHaveSslConfig | 13ms | ✅ |
| testMissingClusterThrowsException | 10ms | ✅ |

### E2EPollerMultiClusterTest

**Class Total Duration:** 6.99s

| Test Method | Duration | Status |
|-------------|----------|--------|
| testPollerRoutesEventsToCorrectClusters | 6.99s | ✅ |

### E2EPollerTest

**Class Total Duration:** 2.62s

| Test Method | Duration | Status |
|-------------|----------|--------|
| testPollerClaimsAndPublishesEvent | 2.62s | ✅ |

### OutboxArchivalServiceTest

**Class Total Duration:** 527ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| archiveOldEvents_movesOldSentEventsToArchive | 113ms | ✅ |
| archiveOldEvents_preservesKafkaMetadata | 93ms | ✅ |
| manualArchive_returnsZeroForInvalidRetention | 77ms | ✅ |
| manualArchive_archivesWithCustomRetention | 59ms | ✅ |
| manualArchive_returnsZeroWhenNoEventsToArchive | 55ms | ✅ |
| archiveOldEvents_preservesAllEventData | 51ms | ✅ |
| archiveOldEvents_doesNothingWhenNoOldEvents | 45ms | ✅ |
| manualArchive_returnsZeroForNegativeRetention | 34ms | ✅ |

### OutboxControllerTest

**Class Total Duration:** 390ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| markOutboxUnsent_ShouldMarkEventAsUnsent | 147ms | ✅ |
| getPendingOutboxEvents_ShouldReturnOnlyPending | 135ms | ✅ |
| searchOutbox_WithFilters_ShouldReturnFilteredResults | 86ms | ✅ |
| getAllOutboxEvents_ShouldReturnAllEvents | 22ms | ✅ |

### OutboxEventClaimTest

**Class Total Duration:** 112ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| testClaimPendingEvents_claimsExpiredInProgressEvents | 37ms | ✅ |
| testClaimPendingEvents_claimsEventsSuccessfully | 19ms | ✅ |
| testClaimPendingEvents_skipsEventsInProgress | 15ms | ✅ |
| testClaimPendingEvents_skipsAlreadySentEvents | 15ms | ✅ |
| testClaimPendingEvents_respectsBatchSize | 15ms | ✅ |
| testClaimPendingEvents_ordersEventsByCreatedAt | 11ms | ✅ |

### OutboxEventClaimerConcurrencyTest

**Class Total Duration:** 809ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| testConcurrentClaimersDoNotProcessSameEvents | 809ms | ✅ |

### OutboxEventPublisherMultiClusterTest

**Class Total Duration:** 200ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| allMustSucceed_successWhenAllClustersSucceed | 47ms | ✅ |
| optionalClusters_failsWhenRequiredClusterFails | 36ms | ✅ |
| atLeastOne_successWhenOnlyOneClusterSucceeds | 35ms | ✅ |
| atLeastOne_failsWhenAllClustersFail | 33ms | ✅ |
| optionalClusters_successWhenRequiredSucceedsOptionalFails | 27ms | ✅ |
| allMustSucceed_failsWhenOneClusterFails | 22ms | ✅ |

### OutboxEventPublisherTest

**Class Total Duration:** 724ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| publishEvent_resetsFailureCountOnSuccess | 344ms | ✅ |
| publishEvent_movesToDeadLetterAfterMaxPermanentRetries | 177ms | ✅ |
| publishEvent_handlesPermanentFailure_noRoute | 56ms | ✅ |
| publishEvent_handlesPermanentFailure_kafkaException | 52ms | ✅ |
| publishEvent_capturesKafkaMetadata | 49ms | ✅ |
| publishEvent_successfullySendsAndMarksSent | 46ms | ✅ |

### OutboxFailureHandlerTest

**Class Total Duration:** 350ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| recordPermanentFailure_movesToDeadLetterAfterMaxRetries | 112ms | ✅ |
| recordPermanentFailure_handlesMultipleEvents | 102ms | ✅ |
| recordPermanentFailure_incrementsFailureCount | 49ms | ✅ |
| resetFailureCount_doesNothingWhenCountIsZero | 33ms | ✅ |
| resetFailureCount_clearsFailureData | 30ms | ✅ |
| recordPermanentFailure_throwsExceptionWhenEventNotFound | 24ms | ✅ |

### OutboxMetricsServiceTest

**Class Total Duration:** 467ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| recordDeadLetter_incrementsCounter | 105ms | ✅ |
| updateArchivalMetrics_countsDeadLetterCorrectly | 89ms | ✅ |
| updatePendingEventsMetrics_calculatesOldestAge | 59ms | ✅ |
| updatePendingEventsMetrics_whenNoEvents_setsToZero | 38ms | ✅ |
| updatePendingEventsMetrics_countsCorrectly | 34ms | ✅ |
| updateArchivalMetrics_countsCorrectly | 31ms | ✅ |
| metricsAreRegistered | 21ms | ✅ |
| updateArchivalMetrics_whenNoEvents_setsToZero | 20ms | ✅ |
| recordProcessingDuration_recordsTimer | 19ms | ✅ |
| recordPublishFailure_incrementsCounter | 18ms | ✅ |
| recordPublishSuccess_incrementsCounter | 17ms | ✅ |
| recordArchival_incrementsCounter | 16ms | ✅ |

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
| getRoutingRule_handlesOptionalClusters | 3ms | ✅ |
| getRoutingRule_throwsExceptionForInvalidFormat | 1ms | ✅ |
| getRoutingRule_handlesIndexedPropertiesFromDynamicRegistry | 1ms | ✅ |
| getRoutingRule_handlesIndexedOptionalProperties | 1ms | ✅ |
| getRoutingRule_throwsExceptionForInvalidClustersFormat | 1ms | ✅ |
| getRoutingRule_handlesStrategyWithHyphens | 0ms | ✅ |
| getRoutingRule_handlesSingleOptionalClusterAsString | 0ms | ✅ |
| getRoutingRule_handlesStrategyWithUnderscores | 0ms | ✅ |
| getRoutingRule_defaultsToAllMustSucceedWhenStrategyNotSpecified | 0ms | ✅ |
| getRoutingRule_handlesMultipleClustersWithStrategy | 0ms | ✅ |
| getRoutingRule_returnsNullForUnknownEventType | 0ms | ✅ |
| getRoutingRule_handlesSingleClusterAsString | 0ms | ✅ |
| getRoutingRule_handlesBackwardCompatibleStringFormat | 0ms | ✅ |

### OutboxServiceTest

**Class Total Duration:** 227ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| findPaged_filtersWithAggregateIdAndPending | 41ms | ✅ |
| markUnsent_clearsSentAtAndLease | 37ms | ✅ |
| findPaged_filtersWithBlankValues | 35ms | ✅ |
| findPaged_filtersAndSorts | 25ms | ✅ |
| findPaged_withAllNullParameters | 25ms | ✅ |
| getAllEvents_returnsAllEvents | 24ms | ✅ |
| getPendingEvents_returnsAllUnsent | 22ms | ✅ |
| markUnsent_throwsExceptionWhenNotFound | 18ms | ✅ |

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

**Class Total Duration:** 9ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| tracerBeanShouldBeAvailable | 4ms | ✅ |
| tracerShouldPropagateContext | 3ms | ✅ |
| tracerShouldCreateSpans | 2ms | ✅ |


---

## Module: order-service

### Summary Statistics

- **Test Classes:** 7
- **Test Methods:** 24
- **Passed:** 24
- **Failed:** 0
- **Total Test Duration:** 1.60s
- **Module Execution Time:** 31.18s
- **Average Test Duration:** 66ms

### Test Details

### OrderControllerTest

**Class Total Duration:** 408ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| getAllOrders_ShouldReturnAllOrders | 207ms | ✅ |
| updateOrderStatus_WithNullStatus_ShouldReturnBadRequest | 69ms | ✅ |
| createOrder_ShouldReturnCreatedOrder | 55ms | ✅ |
| getOrderById_ShouldReturnOrder | 37ms | ✅ |
| updateOrderStatus_ShouldUpdateAndReturnOrder | 24ms | ✅ |
| updateOrderStatus_WithBlankStatus_ShouldReturnBadRequest | 16ms | ✅ |

### OrderNotFoundExceptionTest

**Class Total Duration:** 120ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| shouldCreateExceptionWithCorrectMessage | 116ms | ✅ |
| shouldBeRuntimeException | 4ms | ✅ |

### OrderServiceFailureTest

**Class Total Duration:** 240ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| testOrderCreationFailsWhenOutboxWriteFails | 240ms | ✅ |

### OrderServiceTest

**Class Total Duration:** 785ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| testGetAllOrders | 658ms | ✅ |
| testCreateOrder_CreatesOrderAndOutboxEvent | 51ms | ✅ |
| testUpdateOrderStatus_CreatesOutboxEvent | 33ms | ✅ |
| testGetOrderById_ThrowsExceptionWhenNotFound | 24ms | ✅ |
| testGetOrderById_Success | 19ms | ✅ |

### OrderTest

**Class Total Duration:** 4ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| onCreate_shouldNotOverrideExistingStatus | 1ms | ✅ |
| shouldCreateOrderWithConstructor | 1ms | ✅ |
| shouldSupportSettersAndGetters | 1ms | ✅ |
| onCreate_shouldSetDefaultStatus | 1ms | ✅ |

### TracingConfigurationTest

**Class Total Duration:** 29ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| tracerBeanShouldBeAvailable | 13ms | ✅ |
| tracerShouldCreateSpans | 8ms | ✅ |
| tracerShouldPropagateContext | 8ms | ✅ |

### UpdateStatusRequestTest

**Class Total Duration:** 18ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| shouldSupportEquality | 15ms | ✅ |
| shouldCreateRequestWithStatus | 2ms | ✅ |
| shouldHandleNullStatus | 1ms | ✅ |


---

## Module: order-processor

### Summary Statistics

- **Test Classes:** 3
- **Test Methods:** 16
- **Passed:** 11
- **Failed:** 5
- **Total Test Duration:** 4.03s
- **Module Execution Time:** 47.27s
- **Average Test Duration:** 251ms

### Test Details

### OrderEventListenerTest

**Class Total Duration:** 2.48s

| Test Method | Duration | Status |
|-------------|----------|--------|
| testHandleOrderCreated_DuplicateMessage_SkippedAndAcknowledged | 2.34s | ✅ |
| testHandleOrderCreated_UnexpectedException_Acknowledged | 59ms | ✅ |
| testHandleOrderCreated_NullCorrelationId_ProcessedAndAcknowledged | 33ms | ✅ |
| testHandleOrderCreated_ProcessingException_NotAcknowledged | 16ms | ✅ |
| testHandleOrderCreated_NewMessage_ProcessedAndAcknowledged | 11ms | ✅ |
| testHandleOrderStatusChanged_NewMessage_ProcessedAndAcknowledged | 9ms | ✅ |
| testHandleOrderStatusChanged_DuplicateMessage_SkippedAndAcknowledged | 7ms | ✅ |

### OrderEventProcessingServiceTest

**Class Total Duration:** 1.54s

| Test Method | Duration | Status |
|-------------|----------|--------|
| testProcessOrderCreated_Success | 684ms | ✅ |
| testProcessOrderStatusChanged_Success | 634ms | ✅ |
| testCounters_TrackProcessedEvents | 173ms | ✅ |
| testResetCounters | 51ms | ✅ |

### OrderProcessorE2ETest

**Class Total Duration:** 11ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| testMixedScenario_UniqueAndDuplicateMessages | 7ms | ❌ |
| testDeduplication_DuplicateMessages_ProcessedOnlyOnce | 1ms | ❌ |
| testMultipleUniqueMessages_AllProcessed | 1ms | ❌ |
| testMessageWithoutCorrelationId_StillProcessed | 1ms | ❌ |
| testHappyPath_SingleMessage_ProcessedSuccessfully | 1ms | ❌ |


---

## Module: catbox-archunit

### Summary Statistics

- **Test Classes:** 6
- **Test Methods:** 32
- **Passed:** 32
- **Failed:** 0
- **Total Test Duration:** 197ms
- **Module Execution Time:** 4.39s
- **Average Test Duration:** 6ms

### Test Details

### EntityRepositoryPatternTest

**Class Total Duration:** 98ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| tableAnnotationShouldBeUsedForEntities | 69ms | ✅ |
| entitiesShouldNotHavePublicFields | 17ms | ✅ |
| repositoriesShouldBeInterfaces | 6ms | ✅ |
| entitiesIdFieldsShouldBeAnnotatedWithId | 4ms | ✅ |
| entitiesShouldResideInEntityPackage | 2ms | ✅ |

### LayeringArchitectureTest

**Class Total Duration:** 33ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| repositoriesShouldNotAccessServicesOrControllers | 24ms | ✅ |
| servicesShouldNotAccessControllers | 8ms | ✅ |
| controllersShouldNotAccessRepositoriesDirectly | 1ms | ✅ |

### NamingConventionTest

**Class Total Duration:** 10ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| servicesShouldBeSuffixed | 3ms | ✅ |
| dtosShouldResideInDtoPackage | 2ms | ✅ |
| repositoriesShouldBeSuffixed | 1ms | ✅ |
| configurationsShouldBeSuffixed | 1ms | ✅ |
| entitiesShouldResideInEntityPackage | 1ms | ✅ |
| controllersShouldBeSuffixed | 1ms | ✅ |
| exceptionsShouldBeSuffixed | 1ms | ✅ |

### PackageDependencyTest

**Class Total Duration:** 24ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| commonModuleShouldNotDependOnOtherModules | 9ms | ✅ |
| clientModuleShouldNotDependOnServerOrOrderService | 7ms | ✅ |
| repositoriesShouldOnlyDependOnEntitiesAndSpringData | 2ms | ✅ |
| serverModuleShouldNotDependOnOrderService | 2ms | ✅ |
| entitiesShouldNotDependOnServicesOrControllers | 2ms | ✅ |
| controllersShouldNotDependOnOtherControllers | 1ms | ✅ |
| orderServiceShouldNotDependOnCatboxServer | 1ms | ✅ |

### SpringAnnotationTest

**Class Total Duration:** 10ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| controllersShouldBeAnnotatedWithRestController | 4ms | ✅ |
| repositoriesShouldExtendSpringDataRepository | 2ms | ✅ |
| serviceMethodsShouldNotBePublicUnlessNecessary | 1ms | ✅ |
| servicesShouldBeAnnotatedWithService | 1ms | ✅ |
| configurationClassesShouldBeAnnotatedWithConfiguration | 1ms | ✅ |
| entitiesShouldBeAnnotatedWithEntity | 1ms | ✅ |

### TransactionBoundaryTest

**Class Total Duration:** 22ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| repositoriesShouldNotDefineTransactions | 10ms | ✅ |
| serviceClassesShouldBeAnnotatedWithTransactional | 8ms | ✅ |
| serviceMethodsModifyingDataShouldBeTransactional | 3ms | ✅ |
| controllersShouldNotBeTransactional | 1ms | ✅ |


---

