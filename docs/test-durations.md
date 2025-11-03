# Test Duration Report

Generated: 2025-11-03T05:41:26.274632281Z

This report aggregates test durations across all modules in the build.

---

## Module: catbox-common

### Summary Statistics

- **Test Classes:** 0
- **Test Methods:** 0
- **Passed:** 0
- **Failed:** 0
- **Total Test Duration:** 0ms
- **Module Execution Time:** 69ms

### Test Details


---

## Module: catbox-server

### Summary Statistics

- **Test Classes:** 20
- **Test Methods:** 77
- **Passed:** 77
- **Failed:** 0
- **Total Test Duration:** 13.76s
- **Module Execution Time:** 2m 4s
- **Average Test Duration:** 178ms

### Test Details

### CatboxApplicationTests

**Class Total Duration:** 5ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| contextLoads | 5ms | ✅ |

### CorrelationIdTest

**Class Total Duration:** 139ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| publishEvent_withCorrelationId_sendsCorrelationIdInKafkaHeader | 76ms | ✅ |
| publishEvent_withoutCorrelationId_sendsWithoutHeader | 40ms | ✅ |
| createEvent_withCorrelationId_storesInDatabase | 23ms | ✅ |

### DynamicKafkaTemplateFactoryEvictionTest

**Class Total Duration:** 1.04s

| Test Method | Duration | Status |
|-------------|----------|--------|
| testConcurrentAccessDoesNotCauseExceptions | 1.01s | ✅ |
| testBeanNamingConvention | 8ms | ✅ |
| testTemplateReCreationAfterManualRemoval | 5ms | ✅ |
| testTemplateCreationAndCaching | 5ms | ✅ |
| testMultipleEvictionCallsDontCauseIssues | 4ms | ✅ |
| testEvictionHandlesEmptyCache | 4ms | ✅ |

### DynamicKafkaTemplateFactoryProxyTest

**Class Total Duration:** 79ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| testMultipleCallsDoNotDuplicateBeans | 41ms | ✅ |
| testTemplateCanBeRetrievedDirectlyFromContext | 12ms | ✅ |
| testDynamicallyCreatedTemplateHasSpringLifecycle | 7ms | ✅ |
| testKafkaTemplateIsSingleton | 7ms | ✅ |
| testKafkaTemplateIsSpringManagedBean | 6ms | ✅ |
| testProducerFactoryIsSpringManagedBean | 6ms | ✅ |

### DynamicKafkaTemplateFactorySslBundlePositiveTest

**Class Total Duration:** 58ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| testClusterWithSslBundleHasSslConfiguration | 58ms | ✅ |

### DynamicKafkaTemplateFactorySslBundleTest

**Class Total Duration:** 19ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| testMissingClusterThrowsException | 10ms | ✅ |
| testClusterWithoutSslBundleDoesNotHaveSslConfig | 9ms | ✅ |

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

**Class Total Duration:** 650ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| manualArchive_returnsZeroForInvalidRetention | 318ms | ✅ |
| archiveOldEvents_movesOldSentEventsToArchive | 161ms | ✅ |
| archiveOldEvents_preservesAllEventData | 77ms | ✅ |
| manualArchive_archivesWithCustomRetention | 52ms | ✅ |
| archiveOldEvents_doesNothingWhenNoOldEvents | 42ms | ✅ |

### OutboxEventClaimTest

**Class Total Duration:** 121ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| testClaimPendingEvents_claimsExpiredInProgressEvents | 33ms | ✅ |
| testClaimPendingEvents_skipsEventsInProgress | 26ms | ✅ |
| testClaimPendingEvents_respectsBatchSize | 19ms | ✅ |
| testClaimPendingEvents_claimsEventsSuccessfully | 16ms | ✅ |
| testClaimPendingEvents_ordersEventsByCreatedAt | 14ms | ✅ |
| testClaimPendingEvents_skipsAlreadySentEvents | 13ms | ✅ |

### OutboxEventClaimerConcurrencyTest

**Class Total Duration:** 793ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| testConcurrentClaimersDoNotProcessSameEvents | 793ms | ✅ |

### OutboxEventPublisherMultiClusterTest

**Class Total Duration:** 258ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| allMustSucceed_successWhenAllClustersSucceed | 73ms | ✅ |
| atLeastOne_successWhenOnlyOneClusterSucceeds | 46ms | ✅ |
| atLeastOne_failsWhenAllClustersFail | 45ms | ✅ |
| optionalClusters_failsWhenRequiredClusterFails | 36ms | ✅ |
| allMustSucceed_failsWhenOneClusterFails | 29ms | ✅ |
| optionalClusters_successWhenRequiredSucceedsOptionalFails | 29ms | ✅ |

### OutboxEventPublisherTest

**Class Total Duration:** 709ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| publishEvent_resetsFailureCountOnSuccess | 380ms | ✅ |
| publishEvent_movesToDeadLetterAfterMaxPermanentRetries | 175ms | ✅ |
| publishEvent_handlesPermanentFailure_noRoute | 57ms | ✅ |
| publishEvent_successfullySendsAndMarksSent | 50ms | ✅ |
| publishEvent_handlesPermanentFailure_kafkaException | 47ms | ✅ |

### OutboxFailureHandlerTest

**Class Total Duration:** 233ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| recordPermanentFailure_movesToDeadLetterAfterMaxRetries | 89ms | ✅ |
| recordPermanentFailure_handlesMultipleEvents | 71ms | ✅ |
| recordPermanentFailure_incrementsFailureCount | 35ms | ✅ |
| resetFailureCount_clearsFailureData | 24ms | ✅ |
| resetFailureCount_doesNothingWhenCountIsZero | 14ms | ✅ |

### OutboxMetricsServiceTest

**Class Total Duration:** 240ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| recordProcessingDuration_recordsTimer | 85ms | ✅ |
| updatePendingEventsMetrics_whenNoEvents_setsToZero | 47ms | ✅ |
| updatePendingEventsMetrics_calculatesOldestAge | 44ms | ✅ |
| updatePendingEventsMetrics_countsCorrectly | 27ms | ✅ |
| recordPublishFailure_incrementsCounter | 13ms | ✅ |
| metricsAreRegistered | 12ms | ✅ |
| recordPublishSuccess_incrementsCounter | 12ms | ✅ |

### OutboxProcessingConfigTest

**Class Total Duration:** 11ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| config_loadsDefaultPermanentFailureExceptions | 5ms | ✅ |
| config_hasExistingProcessingSettings | 3ms | ✅ |
| config_loadsMaxPermanentRetries | 3ms | ✅ |

### OutboxRoutingConfigTest

**Class Total Duration:** 9ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| getRoutingRule_handlesOptionalClusters | 6ms | ✅ |
| getRoutingRule_handlesIndexedPropertiesFromDynamicRegistry | 1ms | ✅ |
| getRoutingRule_defaultsToAllMustSucceedWhenStrategyNotSpecified | 1ms | ✅ |
| getRoutingRule_throwsExceptionForInvalidFormat | 1ms | ✅ |
| getRoutingRule_handlesMultipleClustersWithStrategy | 0ms | ✅ |
| getRoutingRule_handlesStrategyWithHyphens | 0ms | ✅ |
| getRoutingRule_returnsNullForUnknownEventType | 0ms | ✅ |
| getRoutingRule_handlesStrategyWithUnderscores | 0ms | ✅ |
| getRoutingRule_handlesSingleClusterAsString | 0ms | ✅ |
| getRoutingRule_handlesBackwardCompatibleStringFormat | 0ms | ✅ |

### OutboxServiceTest

**Class Total Duration:** 104ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| markUnsent_clearsSentAtAndLease | 37ms | ✅ |
| findPaged_filtersAndSorts | 36ms | ✅ |
| getPendingEvents_returnsAllUnsent | 31ms | ✅ |

### SecurityConfigTest

**Class Total Duration:** 7ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| shouldHaveDisabledSecurityByDefault | 4ms | ✅ |
| shouldLoadSecurityFilterChain | 3ms | ✅ |

### SecurityConfigurationTest

**Class Total Duration:** 4ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| testSecureClusterConfiguration | 2ms | ✅ |
| testSaslConfigurationProperties | 1ms | ✅ |
| testSslBundleIsConfigured | 1ms | ✅ |


---

## Module: order-service

### Summary Statistics

- **Test Classes:** 2
- **Test Methods:** 4
- **Passed:** 4
- **Failed:** 0
- **Total Test Duration:** 1.08s
- **Module Execution Time:** 20.17s
- **Average Test Duration:** 270ms

### Test Details

### OrderServiceFailureTest

**Class Total Duration:** 236ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| testOrderCreationFailsWhenOutboxWriteFails | 236ms | ✅ |

### OrderServiceTest

**Class Total Duration:** 847ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| testGetAllOrders | 766ms | ✅ |
| testCreateOrder_CreatesOrderAndOutboxEvent | 46ms | ✅ |
| testUpdateOrderStatus_CreatesOutboxEvent | 35ms | ✅ |


---

## Module: catbox-archunit

### Summary Statistics

- **Test Classes:** 6
- **Test Methods:** 32
- **Passed:** 32
- **Failed:** 0
- **Total Test Duration:** 205ms
- **Module Execution Time:** 3.51s
- **Average Test Duration:** 6ms

### Test Details

### EntityRepositoryPatternTest

**Class Total Duration:** 101ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| tableAnnotationShouldBeUsedForEntities | 67ms | ✅ |
| entitiesShouldNotHavePublicFields | 21ms | ✅ |
| repositoriesShouldBeInterfaces | 7ms | ✅ |
| entitiesIdFieldsShouldBeAnnotatedWithId | 4ms | ✅ |
| entitiesShouldResideInEntityPackage | 2ms | ✅ |

### LayeringArchitectureTest

**Class Total Duration:** 29ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| repositoriesShouldNotAccessServicesOrControllers | 22ms | ✅ |
| servicesShouldNotAccessControllers | 5ms | ✅ |
| controllersShouldNotAccessRepositoriesDirectly | 2ms | ✅ |

### NamingConventionTest

**Class Total Duration:** 12ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| servicesShouldBeSuffixed | 4ms | ✅ |
| dtosShouldResideInDtoPackage | 2ms | ✅ |
| exceptionsShouldBeSuffixed | 2ms | ✅ |
| repositoriesShouldBeSuffixed | 1ms | ✅ |
| configurationsShouldBeSuffixed | 1ms | ✅ |
| entitiesShouldResideInEntityPackage | 1ms | ✅ |
| controllersShouldBeSuffixed | 1ms | ✅ |

### PackageDependencyTest

**Class Total Duration:** 22ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| commonModuleShouldNotDependOnOtherModules | 9ms | ✅ |
| clientModuleShouldNotDependOnServerOrOrderService | 4ms | ✅ |
| entitiesShouldNotDependOnServicesOrControllers | 3ms | ✅ |
| repositoriesShouldOnlyDependOnEntitiesAndSpringData | 2ms | ✅ |
| serverModuleShouldNotDependOnOrderService | 2ms | ✅ |
| controllersShouldNotDependOnOtherControllers | 1ms | ✅ |
| orderServiceShouldNotDependOnCatboxServer | 1ms | ✅ |

### SpringAnnotationTest

**Class Total Duration:** 11ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| controllersShouldBeAnnotatedWithRestController | 4ms | ✅ |
| repositoriesShouldExtendSpringDataRepository | 3ms | ✅ |
| serviceMethodsShouldNotBePublicUnlessNecessary | 1ms | ✅ |
| servicesShouldBeAnnotatedWithService | 1ms | ✅ |
| configurationClassesShouldBeAnnotatedWithConfiguration | 1ms | ✅ |
| entitiesShouldBeAnnotatedWithEntity | 1ms | ✅ |

### TransactionBoundaryTest

**Class Total Duration:** 30ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| repositoriesShouldNotDefineTransactions | 17ms | ✅ |
| serviceClassesShouldBeAnnotatedWithTransactional | 8ms | ✅ |
| serviceMethodsModifyingDataShouldBeTransactional | 3ms | ✅ |
| controllersShouldNotBeTransactional | 2ms | ✅ |


---

