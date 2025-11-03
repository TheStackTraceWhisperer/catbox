# Test Duration Report

Generated: 2025-11-03T05:27:26.562332071Z

This report aggregates test durations across all modules in the build.

---

## Module: catbox-common

### Summary Statistics

- **Test Classes:** 0
- **Test Methods:** 0
- **Passed:** 0
- **Failed:** 0
- **Total Test Duration:** 0ms
- **Module Execution Time:** 73ms

### Test Details


---

## Module: catbox-server

### Summary Statistics

- **Test Classes:** 20
- **Test Methods:** 77
- **Passed:** 77
- **Failed:** 0
- **Total Test Duration:** 14.16s
- **Module Execution Time:** 2m 5s
- **Average Test Duration:** 183ms

### Test Details

### CatboxApplicationTests

**Class Total Duration:** 6ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| contextLoads | 6ms | ✅ |

### CorrelationIdTest

**Class Total Duration:** 181ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| publishEvent_withCorrelationId_sendsCorrelationIdInKafkaHeader | 86ms | ✅ |
| publishEvent_withoutCorrelationId_sendsWithoutHeader | 67ms | ✅ |
| createEvent_withCorrelationId_storesInDatabase | 28ms | ✅ |

### DynamicKafkaTemplateFactoryEvictionTest

**Class Total Duration:** 1.03s

| Test Method | Duration | Status |
|-------------|----------|--------|
| testConcurrentAccessDoesNotCauseExceptions | 1.01s | ✅ |
| testBeanNamingConvention | 9ms | ✅ |
| testTemplateReCreationAfterManualRemoval | 4ms | ✅ |
| testTemplateCreationAndCaching | 4ms | ✅ |
| testMultipleEvictionCallsDontCauseIssues | 4ms | ✅ |
| testEvictionHandlesEmptyCache | 3ms | ✅ |

### DynamicKafkaTemplateFactoryProxyTest

**Class Total Duration:** 85ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| testMultipleCallsDoNotDuplicateBeans | 40ms | ✅ |
| testTemplateCanBeRetrievedDirectlyFromContext | 14ms | ✅ |
| testDynamicallyCreatedTemplateHasSpringLifecycle | 8ms | ✅ |
| testKafkaTemplateIsSpringManagedBean | 8ms | ✅ |
| testKafkaTemplateIsSingleton | 8ms | ✅ |
| testProducerFactoryIsSpringManagedBean | 7ms | ✅ |

### DynamicKafkaTemplateFactorySslBundlePositiveTest

**Class Total Duration:** 54ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| testClusterWithSslBundleHasSslConfiguration | 54ms | ✅ |

### DynamicKafkaTemplateFactorySslBundleTest

**Class Total Duration:** 24ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| testClusterWithoutSslBundleDoesNotHaveSslConfig | 13ms | ✅ |
| testMissingClusterThrowsException | 11ms | ✅ |

### E2EPollerMultiClusterTest

**Class Total Duration:** 6.64s

| Test Method | Duration | Status |
|-------------|----------|--------|
| testPollerRoutesEventsToCorrectClusters | 6.64s | ✅ |

### E2EPollerTest

**Class Total Duration:** 2.90s

| Test Method | Duration | Status |
|-------------|----------|--------|
| testPollerClaimsAndPublishesEvent | 2.90s | ✅ |

### OutboxArchivalServiceTest

**Class Total Duration:** 589ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| manualArchive_returnsZeroForInvalidRetention | 264ms | ✅ |
| archiveOldEvents_movesOldSentEventsToArchive | 159ms | ✅ |
| archiveOldEvents_preservesAllEventData | 69ms | ✅ |
| manualArchive_archivesWithCustomRetention | 53ms | ✅ |
| archiveOldEvents_doesNothingWhenNoOldEvents | 44ms | ✅ |

### OutboxEventClaimTest

**Class Total Duration:** 125ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| testClaimPendingEvents_claimsExpiredInProgressEvents | 32ms | ✅ |
| testClaimPendingEvents_ordersEventsByCreatedAt | 29ms | ✅ |
| testClaimPendingEvents_skipsEventsInProgress | 25ms | ✅ |
| testClaimPendingEvents_skipsAlreadySentEvents | 14ms | ✅ |
| testClaimPendingEvents_respectsBatchSize | 13ms | ✅ |
| testClaimPendingEvents_claimsEventsSuccessfully | 12ms | ✅ |

### OutboxEventClaimerConcurrencyTest

**Class Total Duration:** 797ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| testConcurrentClaimersDoNotProcessSameEvents | 797ms | ✅ |

### OutboxEventPublisherMultiClusterTest

**Class Total Duration:** 306ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| allMustSucceed_successWhenAllClustersSucceed | 88ms | ✅ |
| atLeastOne_failsWhenAllClustersFail | 59ms | ✅ |
| atLeastOne_successWhenOnlyOneClusterSucceeds | 49ms | ✅ |
| optionalClusters_failsWhenRequiredClusterFails | 44ms | ✅ |
| optionalClusters_successWhenRequiredSucceedsOptionalFails | 38ms | ✅ |
| allMustSucceed_failsWhenOneClusterFails | 28ms | ✅ |

### OutboxEventPublisherTest

**Class Total Duration:** 715ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| publishEvent_resetsFailureCountOnSuccess | 388ms | ✅ |
| publishEvent_movesToDeadLetterAfterMaxPermanentRetries | 157ms | ✅ |
| publishEvent_successfullySendsAndMarksSent | 66ms | ✅ |
| publishEvent_handlesPermanentFailure_kafkaException | 62ms | ✅ |
| publishEvent_handlesPermanentFailure_noRoute | 42ms | ✅ |

### OutboxFailureHandlerTest

**Class Total Duration:** 287ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| recordPermanentFailure_movesToDeadLetterAfterMaxRetries | 109ms | ✅ |
| recordPermanentFailure_handlesMultipleEvents | 92ms | ✅ |
| recordPermanentFailure_incrementsFailureCount | 44ms | ✅ |
| resetFailureCount_clearsFailureData | 26ms | ✅ |
| resetFailureCount_doesNothingWhenCountIsZero | 16ms | ✅ |

### OutboxMetricsServiceTest

**Class Total Duration:** 263ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| recordProcessingDuration_recordsTimer | 89ms | ✅ |
| updatePendingEventsMetrics_calculatesOldestAge | 52ms | ✅ |
| updatePendingEventsMetrics_countsCorrectly | 50ms | ✅ |
| updatePendingEventsMetrics_whenNoEvents_setsToZero | 33ms | ✅ |
| recordPublishSuccess_incrementsCounter | 16ms | ✅ |
| recordPublishFailure_incrementsCounter | 12ms | ✅ |
| metricsAreRegistered | 11ms | ✅ |

### OutboxProcessingConfigTest

**Class Total Duration:** 16ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| config_loadsDefaultPermanentFailureExceptions | 7ms | ✅ |
| config_loadsMaxPermanentRetries | 6ms | ✅ |
| config_hasExistingProcessingSettings | 3ms | ✅ |

### OutboxRoutingConfigTest

**Class Total Duration:** 9ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| getRoutingRule_handlesOptionalClusters | 3ms | ✅ |
| getRoutingRule_handlesMultipleClustersWithStrategy | 1ms | ✅ |
| getRoutingRule_handlesIndexedPropertiesFromDynamicRegistry | 1ms | ✅ |
| getRoutingRule_returnsNullForUnknownEventType | 1ms | ✅ |
| getRoutingRule_handlesSingleClusterAsString | 1ms | ✅ |
| getRoutingRule_defaultsToAllMustSucceedWhenStrategyNotSpecified | 1ms | ✅ |
| getRoutingRule_throwsExceptionForInvalidFormat | 1ms | ✅ |
| getRoutingRule_handlesStrategyWithHyphens | 0ms | ✅ |
| getRoutingRule_handlesStrategyWithUnderscores | 0ms | ✅ |
| getRoutingRule_handlesBackwardCompatibleStringFormat | 0ms | ✅ |

### OutboxServiceTest

**Class Total Duration:** 121ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| findPaged_filtersAndSorts | 59ms | ✅ |
| getPendingEvents_returnsAllUnsent | 31ms | ✅ |
| markUnsent_clearsSentAtAndLease | 31ms | ✅ |

### SecurityConfigTest

**Class Total Duration:** 6ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| shouldLoadSecurityFilterChain | 3ms | ✅ |
| shouldHaveDisabledSecurityByDefault | 3ms | ✅ |

### SecurityConfigurationTest

**Class Total Duration:** 5ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| testSaslConfigurationProperties | 2ms | ✅ |
| testSecureClusterConfiguration | 2ms | ✅ |
| testSslBundleIsConfigured | 1ms | ✅ |


---

## Module: order-service

### Summary Statistics

- **Test Classes:** 2
- **Test Methods:** 4
- **Passed:** 4
- **Failed:** 0
- **Total Test Duration:** 1.05s
- **Module Execution Time:** 21.32s
- **Average Test Duration:** 263ms

### Test Details

### OrderServiceFailureTest

**Class Total Duration:** 243ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| testOrderCreationFailsWhenOutboxWriteFails | 243ms | ✅ |

### OrderServiceTest

**Class Total Duration:** 811ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| testGetAllOrders | 738ms | ✅ |
| testCreateOrder_CreatesOrderAndOutboxEvent | 40ms | ✅ |
| testUpdateOrderStatus_CreatesOutboxEvent | 33ms | ✅ |


---

## Module: catbox-archunit

### Summary Statistics

- **Test Classes:** 6
- **Test Methods:** 32
- **Passed:** 32
- **Failed:** 0
- **Total Test Duration:** 183ms
- **Module Execution Time:** 3.17s
- **Average Test Duration:** 5ms

### Test Details

### EntityRepositoryPatternTest

**Class Total Duration:** 88ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| tableAnnotationShouldBeUsedForEntities | 57ms | ✅ |
| entitiesShouldNotHavePublicFields | 19ms | ✅ |
| repositoriesShouldBeInterfaces | 7ms | ✅ |
| entitiesIdFieldsShouldBeAnnotatedWithId | 3ms | ✅ |
| entitiesShouldResideInEntityPackage | 2ms | ✅ |

### LayeringArchitectureTest

**Class Total Duration:** 24ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| repositoriesShouldNotAccessServicesOrControllers | 19ms | ✅ |
| servicesShouldNotAccessControllers | 4ms | ✅ |
| controllersShouldNotAccessRepositoriesDirectly | 1ms | ✅ |

### NamingConventionTest

**Class Total Duration:** 13ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| servicesShouldBeSuffixed | 3ms | ✅ |
| configurationsShouldBeSuffixed | 2ms | ✅ |
| dtosShouldResideInDtoPackage | 2ms | ✅ |
| controllersShouldBeSuffixed | 2ms | ✅ |
| exceptionsShouldBeSuffixed | 2ms | ✅ |
| repositoriesShouldBeSuffixed | 1ms | ✅ |
| entitiesShouldResideInEntityPackage | 1ms | ✅ |

### PackageDependencyTest

**Class Total Duration:** 22ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| commonModuleShouldNotDependOnOtherModules | 10ms | ✅ |
| clientModuleShouldNotDependOnServerOrOrderService | 4ms | ✅ |
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

**Class Total Duration:** 26ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| repositoriesShouldNotDefineTransactions | 15ms | ✅ |
| serviceClassesShouldBeAnnotatedWithTransactional | 7ms | ✅ |
| serviceMethodsModifyingDataShouldBeTransactional | 3ms | ✅ |
| controllersShouldNotBeTransactional | 1ms | ✅ |


---

## Module: catbox-common

### Summary Statistics

- **Test Classes:** 0
- **Test Methods:** 0
- **Passed:** 0
- **Failed:** 0
- **Total Test Duration:** 0ms
- **Module Execution Time:** 70ms

### Test Details


---

## Module: catbox-server

### Summary Statistics

- **Test Classes:** 20
- **Test Methods:** 77
- **Passed:** 77
- **Failed:** 0
- **Total Test Duration:** 13.75s
- **Module Execution Time:** 2m 5s
- **Average Test Duration:** 178ms

### Test Details

### CatboxApplicationTests

**Class Total Duration:** 4ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| contextLoads | 4ms | ✅ |

### CorrelationIdTest

**Class Total Duration:** 146ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| publishEvent_withCorrelationId_sendsCorrelationIdInKafkaHeader | 79ms | ✅ |
| publishEvent_withoutCorrelationId_sendsWithoutHeader | 35ms | ✅ |
| createEvent_withCorrelationId_storesInDatabase | 32ms | ✅ |

### DynamicKafkaTemplateFactoryEvictionTest

**Class Total Duration:** 1.03s

| Test Method | Duration | Status |
|-------------|----------|--------|
| testConcurrentAccessDoesNotCauseExceptions | 1.01s | ✅ |
| testBeanNamingConvention | 8ms | ✅ |
| testTemplateReCreationAfterManualRemoval | 4ms | ✅ |
| testTemplateCreationAndCaching | 4ms | ✅ |
| testEvictionHandlesEmptyCache | 4ms | ✅ |
| testMultipleEvictionCallsDontCauseIssues | 3ms | ✅ |

### DynamicKafkaTemplateFactoryProxyTest

**Class Total Duration:** 80ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| testMultipleCallsDoNotDuplicateBeans | 38ms | ✅ |
| testTemplateCanBeRetrievedDirectlyFromContext | 15ms | ✅ |
| testDynamicallyCreatedTemplateHasSpringLifecycle | 7ms | ✅ |
| testKafkaTemplateIsSpringManagedBean | 7ms | ✅ |
| testKafkaTemplateIsSingleton | 7ms | ✅ |
| testProducerFactoryIsSpringManagedBean | 6ms | ✅ |

### DynamicKafkaTemplateFactorySslBundlePositiveTest

**Class Total Duration:** 55ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| testClusterWithSslBundleHasSslConfiguration | 55ms | ✅ |

### DynamicKafkaTemplateFactorySslBundleTest

**Class Total Duration:** 20ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| testMissingClusterThrowsException | 11ms | ✅ |
| testClusterWithoutSslBundleDoesNotHaveSslConfig | 9ms | ✅ |

### E2EPollerMultiClusterTest

**Class Total Duration:** 6.67s

| Test Method | Duration | Status |
|-------------|----------|--------|
| testPollerRoutesEventsToCorrectClusters | 6.67s | ✅ |

### E2EPollerTest

**Class Total Duration:** 2.65s

| Test Method | Duration | Status |
|-------------|----------|--------|
| testPollerClaimsAndPublishesEvent | 2.65s | ✅ |

### OutboxArchivalServiceTest

**Class Total Duration:** 550ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| manualArchive_returnsZeroForInvalidRetention | 218ms | ✅ |
| archiveOldEvents_movesOldSentEventsToArchive | 158ms | ✅ |
| archiveOldEvents_preservesAllEventData | 72ms | ✅ |
| manualArchive_archivesWithCustomRetention | 56ms | ✅ |
| archiveOldEvents_doesNothingWhenNoOldEvents | 46ms | ✅ |

### OutboxEventClaimTest

**Class Total Duration:** 159ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| testClaimPendingEvents_skipsEventsInProgress | 45ms | ✅ |
| testClaimPendingEvents_claimsExpiredInProgressEvents | 37ms | ✅ |
| testClaimPendingEvents_respectsBatchSize | 33ms | ✅ |
| testClaimPendingEvents_ordersEventsByCreatedAt | 16ms | ✅ |
| testClaimPendingEvents_skipsAlreadySentEvents | 15ms | ✅ |
| testClaimPendingEvents_claimsEventsSuccessfully | 13ms | ✅ |

### OutboxEventClaimerConcurrencyTest

**Class Total Duration:** 778ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| testConcurrentClaimersDoNotProcessSameEvents | 778ms | ✅ |

### OutboxEventPublisherMultiClusterTest

**Class Total Duration:** 200ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| allMustSucceed_successWhenAllClustersSucceed | 45ms | ✅ |
| atLeastOne_failsWhenAllClustersFail | 41ms | ✅ |
| atLeastOne_successWhenOnlyOneClusterSucceeds | 35ms | ✅ |
| optionalClusters_failsWhenRequiredClusterFails | 31ms | ✅ |
| optionalClusters_successWhenRequiredSucceedsOptionalFails | 26ms | ✅ |
| allMustSucceed_failsWhenOneClusterFails | 22ms | ✅ |

### OutboxEventPublisherTest

**Class Total Duration:** 690ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| publishEvent_resetsFailureCountOnSuccess | 374ms | ✅ |
| publishEvent_movesToDeadLetterAfterMaxPermanentRetries | 134ms | ✅ |
| publishEvent_successfullySendsAndMarksSent | 69ms | ✅ |
| publishEvent_handlesPermanentFailure_kafkaException | 61ms | ✅ |
| publishEvent_handlesPermanentFailure_noRoute | 52ms | ✅ |

### OutboxFailureHandlerTest

**Class Total Duration:** 295ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| recordPermanentFailure_movesToDeadLetterAfterMaxRetries | 113ms | ✅ |
| recordPermanentFailure_handlesMultipleEvents | 91ms | ✅ |
| recordPermanentFailure_incrementsFailureCount | 53ms | ✅ |
| resetFailureCount_clearsFailureData | 23ms | ✅ |
| resetFailureCount_doesNothingWhenCountIsZero | 15ms | ✅ |

### OutboxMetricsServiceTest

**Class Total Duration:** 253ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| recordProcessingDuration_recordsTimer | 90ms | ✅ |
| updatePendingEventsMetrics_calculatesOldestAge | 62ms | ✅ |
| updatePendingEventsMetrics_whenNoEvents_setsToZero | 35ms | ✅ |
| updatePendingEventsMetrics_countsCorrectly | 30ms | ✅ |
| metricsAreRegistered | 13ms | ✅ |
| recordPublishFailure_incrementsCounter | 12ms | ✅ |
| recordPublishSuccess_incrementsCounter | 11ms | ✅ |

### OutboxProcessingConfigTest

**Class Total Duration:** 12ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| config_loadsDefaultPermanentFailureExceptions | 4ms | ✅ |
| config_hasExistingProcessingSettings | 4ms | ✅ |
| config_loadsMaxPermanentRetries | 4ms | ✅ |

### OutboxRoutingConfigTest

**Class Total Duration:** 15ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| getRoutingRule_returnsNullForUnknownEventType | 5ms | ✅ |
| getRoutingRule_handlesMultipleClustersWithStrategy | 2ms | ✅ |
| getRoutingRule_handlesOptionalClusters | 2ms | ✅ |
| getRoutingRule_handlesIndexedPropertiesFromDynamicRegistry | 1ms | ✅ |
| getRoutingRule_handlesStrategyWithHyphens | 1ms | ✅ |
| getRoutingRule_handlesStrategyWithUnderscores | 1ms | ✅ |
| getRoutingRule_defaultsToAllMustSucceedWhenStrategyNotSpecified | 1ms | ✅ |
| getRoutingRule_handlesBackwardCompatibleStringFormat | 1ms | ✅ |
| getRoutingRule_throwsExceptionForInvalidFormat | 1ms | ✅ |
| getRoutingRule_handlesSingleClusterAsString | 0ms | ✅ |

### OutboxServiceTest

**Class Total Duration:** 124ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| markUnsent_clearsSentAtAndLease | 53ms | ✅ |
| findPaged_filtersAndSorts | 42ms | ✅ |
| getPendingEvents_returnsAllUnsent | 29ms | ✅ |

### SecurityConfigTest

**Class Total Duration:** 12ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| shouldHaveDisabledSecurityByDefault | 7ms | ✅ |
| shouldLoadSecurityFilterChain | 5ms | ✅ |

### SecurityConfigurationTest

**Class Total Duration:** 7ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| testSecureClusterConfiguration | 3ms | ✅ |
| testSaslConfigurationProperties | 2ms | ✅ |
| testSslBundleIsConfigured | 2ms | ✅ |


---

## Module: catbox-server

### Summary Statistics

- **Test Classes:** 1
- **Test Methods:** 1
- **Passed:** 1
- **Failed:** 0
- **Total Test Duration:** 1.81s
- **Module Execution Time:** 9.82s
- **Average Test Duration:** 1.81s

### Test Details

### KafkaIntegrationTest

**Class Total Duration:** 1.81s

| Test Method | Duration | Status |
|-------------|----------|--------|
| testDynamicFactorySendAndReceive | 1.81s | ✅ |


---

## Module: order-service

### Summary Statistics

- **Test Classes:** 2
- **Test Methods:** 4
- **Passed:** 4
- **Failed:** 0
- **Total Test Duration:** 1.00s
- **Module Execution Time:** 21.40s
- **Average Test Duration:** 250ms

### Test Details

### OrderServiceFailureTest

**Class Total Duration:** 206ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| testOrderCreationFailsWhenOutboxWriteFails | 206ms | ✅ |

### OrderServiceTest

**Class Total Duration:** 796ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| testGetAllOrders | 718ms | ✅ |
| testCreateOrder_CreatesOrderAndOutboxEvent | 45ms | ✅ |
| testUpdateOrderStatus_CreatesOutboxEvent | 33ms | ✅ |


---

## Module: catbox-archunit

### Summary Statistics

- **Test Classes:** 6
- **Test Methods:** 32
- **Passed:** 32
- **Failed:** 0
- **Total Test Duration:** 211ms
- **Module Execution Time:** 3.81s
- **Average Test Duration:** 6ms

### Test Details

### EntityRepositoryPatternTest

**Class Total Duration:** 114ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| tableAnnotationShouldBeUsedForEntities | 70ms | ✅ |
| entitiesShouldNotHavePublicFields | 31ms | ✅ |
| repositoriesShouldBeInterfaces | 7ms | ✅ |
| entitiesIdFieldsShouldBeAnnotatedWithId | 4ms | ✅ |
| entitiesShouldResideInEntityPackage | 2ms | ✅ |

### LayeringArchitectureTest

**Class Total Duration:** 25ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| repositoriesShouldNotAccessServicesOrControllers | 19ms | ✅ |
| servicesShouldNotAccessControllers | 4ms | ✅ |
| controllersShouldNotAccessRepositoriesDirectly | 2ms | ✅ |

### NamingConventionTest

**Class Total Duration:** 12ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| servicesShouldBeSuffixed | 3ms | ✅ |
| configurationsShouldBeSuffixed | 2ms | ✅ |
| dtosShouldResideInDtoPackage | 2ms | ✅ |
| exceptionsShouldBeSuffixed | 2ms | ✅ |
| repositoriesShouldBeSuffixed | 1ms | ✅ |
| entitiesShouldResideInEntityPackage | 1ms | ✅ |
| controllersShouldBeSuffixed | 1ms | ✅ |

### PackageDependencyTest

**Class Total Duration:** 28ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| commonModuleShouldNotDependOnOtherModules | 15ms | ✅ |
| clientModuleShouldNotDependOnServerOrOrderService | 4ms | ✅ |
| entitiesShouldNotDependOnServicesOrControllers | 3ms | ✅ |
| repositoriesShouldOnlyDependOnEntitiesAndSpringData | 2ms | ✅ |
| serverModuleShouldNotDependOnOrderService | 2ms | ✅ |
| controllersShouldNotDependOnOtherControllers | 1ms | ✅ |
| orderServiceShouldNotDependOnCatboxServer | 1ms | ✅ |

### SpringAnnotationTest

**Class Total Duration:** 12ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| controllersShouldBeAnnotatedWithRestController | 4ms | ✅ |
| repositoriesShouldExtendSpringDataRepository | 3ms | ✅ |
| configurationClassesShouldBeAnnotatedWithConfiguration | 2ms | ✅ |
| serviceMethodsShouldNotBePublicUnlessNecessary | 1ms | ✅ |
| servicesShouldBeAnnotatedWithService | 1ms | ✅ |
| entitiesShouldBeAnnotatedWithEntity | 1ms | ✅ |

### TransactionBoundaryTest

**Class Total Duration:** 20ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| repositoriesShouldNotDefineTransactions | 9ms | ✅ |
| serviceClassesShouldBeAnnotatedWithTransactional | 7ms | ✅ |
| serviceMethodsModifyingDataShouldBeTransactional | 3ms | ✅ |
| controllersShouldNotBeTransactional | 1ms | ✅ |


---

