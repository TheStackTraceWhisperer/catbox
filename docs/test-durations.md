# Test Duration Report

Generated: 2025-11-03T15:14:16.243600473Z

This report aggregates test durations across all modules in the build.

---

## Module: catbox-server

### Summary Statistics

- **Test Classes:** 20
- **Test Methods:** 77
- **Passed:** 77
- **Failed:** 0
- **Total Test Duration:** 13.85s
- **Module Execution Time:** 2m 42s
- **Average Test Duration:** 179ms

### Test Details

### CatboxApplicationTests

**Class Total Duration:** 31ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| contextLoads | 31ms | ✅ |

### CorrelationIdTest

**Class Total Duration:** 139ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| publishEvent_withCorrelationId_sendsCorrelationIdInKafkaHeader | 72ms | ✅ |
| publishEvent_withoutCorrelationId_sendsWithoutHeader | 38ms | ✅ |
| createEvent_withCorrelationId_storesInDatabase | 29ms | ✅ |

### DynamicKafkaTemplateFactoryEvictionTest

**Class Total Duration:** 1.04s

| Test Method | Duration | Status |
|-------------|----------|--------|
| testConcurrentAccessDoesNotCauseExceptions | 1.01s | ✅ |
| testBeanNamingConvention | 11ms | ✅ |
| testTemplateCreationAndCaching | 6ms | ✅ |
| testEvictionHandlesEmptyCache | 5ms | ✅ |
| testTemplateReCreationAfterManualRemoval | 4ms | ✅ |
| testMultipleEvictionCallsDontCauseIssues | 3ms | ✅ |

### DynamicKafkaTemplateFactoryProxyTest

**Class Total Duration:** 83ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| testMultipleCallsDoNotDuplicateBeans | 39ms | ✅ |
| testTemplateCanBeRetrievedDirectlyFromContext | 14ms | ✅ |
| testDynamicallyCreatedTemplateHasSpringLifecycle | 8ms | ✅ |
| testKafkaTemplateIsSingleton | 8ms | ✅ |
| testKafkaTemplateIsSpringManagedBean | 7ms | ✅ |
| testProducerFactoryIsSpringManagedBean | 7ms | ✅ |

### DynamicKafkaTemplateFactorySslBundlePositiveTest

**Class Total Duration:** 50ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| testClusterWithSslBundleHasSslConfiguration | 50ms | ✅ |

### DynamicKafkaTemplateFactorySslBundleTest

**Class Total Duration:** 21ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| testClusterWithoutSslBundleDoesNotHaveSslConfig | 11ms | ✅ |
| testMissingClusterThrowsException | 10ms | ✅ |

### E2EPollerMultiClusterTest

**Class Total Duration:** 6.58s

| Test Method | Duration | Status |
|-------------|----------|--------|
| testPollerRoutesEventsToCorrectClusters | 6.58s | ✅ |

### E2EPollerTest

**Class Total Duration:** 2.63s

| Test Method | Duration | Status |
|-------------|----------|--------|
| testPollerClaimsAndPublishesEvent | 2.63s | ✅ |

### OutboxArchivalServiceTest

**Class Total Duration:** 609ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| manualArchive_returnsZeroForInvalidRetention | 276ms | ✅ |
| archiveOldEvents_movesOldSentEventsToArchive | 153ms | ✅ |
| archiveOldEvents_preservesAllEventData | 83ms | ✅ |
| manualArchive_archivesWithCustomRetention | 58ms | ✅ |
| archiveOldEvents_doesNothingWhenNoOldEvents | 39ms | ✅ |

### OutboxEventClaimTest

**Class Total Duration:** 174ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| testClaimPendingEvents_claimsExpiredInProgressEvents | 74ms | ✅ |
| testClaimPendingEvents_claimsEventsSuccessfully | 28ms | ✅ |
| testClaimPendingEvents_skipsAlreadySentEvents | 22ms | ✅ |
| testClaimPendingEvents_skipsEventsInProgress | 20ms | ✅ |
| testClaimPendingEvents_respectsBatchSize | 16ms | ✅ |
| testClaimPendingEvents_ordersEventsByCreatedAt | 14ms | ✅ |

### OutboxEventClaimerConcurrencyTest

**Class Total Duration:** 830ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| testConcurrentClaimersDoNotProcessSameEvents | 830ms | ✅ |

### OutboxEventPublisherMultiClusterTest

**Class Total Duration:** 257ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| atLeastOne_successWhenOnlyOneClusterSucceeds | 58ms | ✅ |
| atLeastOne_failsWhenAllClustersFail | 51ms | ✅ |
| optionalClusters_successWhenRequiredSucceedsOptionalFails | 42ms | ✅ |
| allMustSucceed_successWhenAllClustersSucceed | 41ms | ✅ |
| optionalClusters_failsWhenRequiredClusterFails | 38ms | ✅ |
| allMustSucceed_failsWhenOneClusterFails | 27ms | ✅ |

### OutboxEventPublisherTest

**Class Total Duration:** 748ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| publishEvent_resetsFailureCountOnSuccess | 411ms | ✅ |
| publishEvent_movesToDeadLetterAfterMaxPermanentRetries | 162ms | ✅ |
| publishEvent_handlesPermanentFailure_kafkaException | 64ms | ✅ |
| publishEvent_handlesPermanentFailure_noRoute | 60ms | ✅ |
| publishEvent_successfullySendsAndMarksSent | 51ms | ✅ |

### OutboxFailureHandlerTest

**Class Total Duration:** 258ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| recordPermanentFailure_movesToDeadLetterAfterMaxRetries | 97ms | ✅ |
| recordPermanentFailure_handlesMultipleEvents | 73ms | ✅ |
| recordPermanentFailure_incrementsFailureCount | 47ms | ✅ |
| resetFailureCount_clearsFailureData | 26ms | ✅ |
| resetFailureCount_doesNothingWhenCountIsZero | 15ms | ✅ |

### OutboxMetricsServiceTest

**Class Total Duration:** 265ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| recordProcessingDuration_recordsTimer | 100ms | ✅ |
| updatePendingEventsMetrics_calculatesOldestAge | 57ms | ✅ |
| updatePendingEventsMetrics_whenNoEvents_setsToZero | 40ms | ✅ |
| updatePendingEventsMetrics_countsCorrectly | 29ms | ✅ |
| recordPublishFailure_incrementsCounter | 14ms | ✅ |
| metricsAreRegistered | 13ms | ✅ |
| recordPublishSuccess_incrementsCounter | 12ms | ✅ |

### OutboxProcessingConfigTest

**Class Total Duration:** 14ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| config_loadsMaxPermanentRetries | 6ms | ✅ |
| config_loadsDefaultPermanentFailureExceptions | 5ms | ✅ |
| config_hasExistingProcessingSettings | 3ms | ✅ |

### OutboxRoutingConfigTest

**Class Total Duration:** 8ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| getRoutingRule_handlesOptionalClusters | 3ms | ✅ |
| getRoutingRule_handlesIndexedPropertiesFromDynamicRegistry | 1ms | ✅ |
| getRoutingRule_handlesStrategyWithUnderscores | 1ms | ✅ |
| getRoutingRule_handlesSingleClusterAsString | 1ms | ✅ |
| getRoutingRule_defaultsToAllMustSucceedWhenStrategyNotSpecified | 1ms | ✅ |
| getRoutingRule_throwsExceptionForInvalidFormat | 1ms | ✅ |
| getRoutingRule_handlesMultipleClustersWithStrategy | 0ms | ✅ |
| getRoutingRule_handlesStrategyWithHyphens | 0ms | ✅ |
| getRoutingRule_returnsNullForUnknownEventType | 0ms | ✅ |
| getRoutingRule_handlesBackwardCompatibleStringFormat | 0ms | ✅ |

### OutboxServiceTest

**Class Total Duration:** 98ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| findPaged_filtersAndSorts | 40ms | ✅ |
| getPendingEvents_returnsAllUnsent | 32ms | ✅ |
| markUnsent_clearsSentAtAndLease | 26ms | ✅ |

### SecurityConfigTest

**Class Total Duration:** 8ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| shouldHaveDisabledSecurityByDefault | 5ms | ✅ |
| shouldLoadSecurityFilterChain | 3ms | ✅ |

### SecurityConfigurationTest

**Class Total Duration:** 8ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| testSecureClusterConfiguration | 4ms | ✅ |
| testSaslConfigurationProperties | 2ms | ✅ |
| testSslBundleIsConfigured | 2ms | ✅ |


---

## Module: order-service

### Summary Statistics

- **Test Classes:** 2
- **Test Methods:** 4
- **Passed:** 4
- **Failed:** 0
- **Total Test Duration:** 1.06s
- **Module Execution Time:** 21.53s
- **Average Test Duration:** 264ms

### Test Details

### OrderServiceFailureTest

**Class Total Duration:** 224ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| testOrderCreationFailsWhenOutboxWriteFails | 224ms | ✅ |

### OrderServiceTest

**Class Total Duration:** 833ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| testGetAllOrders | 751ms | ✅ |
| testCreateOrder_CreatesOrderAndOutboxEvent | 41ms | ✅ |
| testUpdateOrderStatus_CreatesOutboxEvent | 41ms | ✅ |


---

## Module: catbox-archunit

### Summary Statistics

- **Test Classes:** 6
- **Test Methods:** 32
- **Passed:** 32
- **Failed:** 0
- **Total Test Duration:** 185ms
- **Module Execution Time:** 3.91s
- **Average Test Duration:** 5ms

### Test Details

### EntityRepositoryPatternTest

**Class Total Duration:** 83ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| tableAnnotationShouldBeUsedForEntities | 53ms | ✅ |
| entitiesShouldNotHavePublicFields | 18ms | ✅ |
| repositoriesShouldBeInterfaces | 6ms | ✅ |
| entitiesIdFieldsShouldBeAnnotatedWithId | 4ms | ✅ |
| entitiesShouldResideInEntityPackage | 2ms | ✅ |

### LayeringArchitectureTest

**Class Total Duration:** 28ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| repositoriesShouldNotAccessServicesOrControllers | 22ms | ✅ |
| servicesShouldNotAccessControllers | 5ms | ✅ |
| controllersShouldNotAccessRepositoriesDirectly | 1ms | ✅ |

### NamingConventionTest

**Class Total Duration:** 20ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| servicesShouldBeSuffixed | 6ms | ✅ |
| configurationsShouldBeSuffixed | 3ms | ✅ |
| entitiesShouldResideInEntityPackage | 3ms | ✅ |
| dtosShouldResideInDtoPackage | 3ms | ✅ |
| exceptionsShouldBeSuffixed | 3ms | ✅ |
| repositoriesShouldBeSuffixed | 1ms | ✅ |
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

**Class Total Duration:** 21ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| repositoriesShouldNotDefineTransactions | 9ms | ✅ |
| serviceClassesShouldBeAnnotatedWithTransactional | 7ms | ✅ |
| serviceMethodsModifyingDataShouldBeTransactional | 3ms | ✅ |
| controllersShouldNotBeTransactional | 2ms | ✅ |


---

## Module: catbox-server

### Summary Statistics

- **Test Classes:** 20
- **Test Methods:** 77
- **Passed:** 77
- **Failed:** 0
- **Total Test Duration:** 13.86s
- **Module Execution Time:** 2m 6s
- **Average Test Duration:** 179ms

### Test Details

### CatboxApplicationTests

**Class Total Duration:** 3ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| contextLoads | 3ms | ✅ |

### CorrelationIdTest

**Class Total Duration:** 133ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| publishEvent_withCorrelationId_sendsCorrelationIdInKafkaHeader | 60ms | ✅ |
| publishEvent_withoutCorrelationId_sendsWithoutHeader | 49ms | ✅ |
| createEvent_withCorrelationId_storesInDatabase | 24ms | ✅ |

### DynamicKafkaTemplateFactoryEvictionTest

**Class Total Duration:** 1.03s

| Test Method | Duration | Status |
|-------------|----------|--------|
| testConcurrentAccessDoesNotCauseExceptions | 1.01s | ✅ |
| testBeanNamingConvention | 9ms | ✅ |
| testTemplateReCreationAfterManualRemoval | 4ms | ✅ |
| testTemplateCreationAndCaching | 4ms | ✅ |
| testEvictionHandlesEmptyCache | 4ms | ✅ |
| testMultipleEvictionCallsDontCauseIssues | 3ms | ✅ |

### DynamicKafkaTemplateFactoryProxyTest

**Class Total Duration:** 79ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| testMultipleCallsDoNotDuplicateBeans | 37ms | ✅ |
| testTemplateCanBeRetrievedDirectlyFromContext | 13ms | ✅ |
| testProducerFactoryIsSpringManagedBean | 8ms | ✅ |
| testDynamicallyCreatedTemplateHasSpringLifecycle | 7ms | ✅ |
| testKafkaTemplateIsSpringManagedBean | 7ms | ✅ |
| testKafkaTemplateIsSingleton | 7ms | ✅ |

### DynamicKafkaTemplateFactorySslBundlePositiveTest

**Class Total Duration:** 50ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| testClusterWithSslBundleHasSslConfiguration | 50ms | ✅ |

### DynamicKafkaTemplateFactorySslBundleTest

**Class Total Duration:** 22ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| testMissingClusterThrowsException | 11ms | ✅ |
| testClusterWithoutSslBundleDoesNotHaveSslConfig | 11ms | ✅ |

### E2EPollerMultiClusterTest

**Class Total Duration:** 7.00s

| Test Method | Duration | Status |
|-------------|----------|--------|
| testPollerRoutesEventsToCorrectClusters | 7.00s | ✅ |

### E2EPollerTest

**Class Total Duration:** 2.53s

| Test Method | Duration | Status |
|-------------|----------|--------|
| testPollerClaimsAndPublishesEvent | 2.53s | ✅ |

### OutboxArchivalServiceTest

**Class Total Duration:** 575ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| manualArchive_returnsZeroForInvalidRetention | 214ms | ✅ |
| archiveOldEvents_movesOldSentEventsToArchive | 166ms | ✅ |
| archiveOldEvents_preservesAllEventData | 85ms | ✅ |
| manualArchive_archivesWithCustomRetention | 69ms | ✅ |
| archiveOldEvents_doesNothingWhenNoOldEvents | 41ms | ✅ |

### OutboxEventClaimTest

**Class Total Duration:** 111ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| testClaimPendingEvents_claimsExpiredInProgressEvents | 37ms | ✅ |
| testClaimPendingEvents_ordersEventsByCreatedAt | 17ms | ✅ |
| testClaimPendingEvents_skipsEventsInProgress | 16ms | ✅ |
| testClaimPendingEvents_skipsAlreadySentEvents | 15ms | ✅ |
| testClaimPendingEvents_respectsBatchSize | 13ms | ✅ |
| testClaimPendingEvents_claimsEventsSuccessfully | 13ms | ✅ |

### OutboxEventClaimerConcurrencyTest

**Class Total Duration:** 760ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| testConcurrentClaimersDoNotProcessSameEvents | 760ms | ✅ |

### OutboxEventPublisherMultiClusterTest

**Class Total Duration:** 226ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| allMustSucceed_successWhenAllClustersSucceed | 40ms | ✅ |
| atLeastOne_failsWhenAllClustersFail | 40ms | ✅ |
| allMustSucceed_failsWhenOneClusterFails | 38ms | ✅ |
| optionalClusters_successWhenRequiredSucceedsOptionalFails | 37ms | ✅ |
| optionalClusters_failsWhenRequiredClusterFails | 37ms | ✅ |
| atLeastOne_successWhenOnlyOneClusterSucceeds | 34ms | ✅ |

### OutboxEventPublisherTest

**Class Total Duration:** 692ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| publishEvent_resetsFailureCountOnSuccess | 365ms | ✅ |
| publishEvent_movesToDeadLetterAfterMaxPermanentRetries | 156ms | ✅ |
| publishEvent_handlesPermanentFailure_kafkaException | 69ms | ✅ |
| publishEvent_handlesPermanentFailure_noRoute | 53ms | ✅ |
| publishEvent_successfullySendsAndMarksSent | 49ms | ✅ |

### OutboxFailureHandlerTest

**Class Total Duration:** 259ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| recordPermanentFailure_movesToDeadLetterAfterMaxRetries | 113ms | ✅ |
| recordPermanentFailure_handlesMultipleEvents | 67ms | ✅ |
| recordPermanentFailure_incrementsFailureCount | 42ms | ✅ |
| resetFailureCount_clearsFailureData | 21ms | ✅ |
| resetFailureCount_doesNothingWhenCountIsZero | 16ms | ✅ |

### OutboxMetricsServiceTest

**Class Total Duration:** 240ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| recordProcessingDuration_recordsTimer | 85ms | ✅ |
| updatePendingEventsMetrics_calculatesOldestAge | 48ms | ✅ |
| updatePendingEventsMetrics_countsCorrectly | 37ms | ✅ |
| updatePendingEventsMetrics_whenNoEvents_setsToZero | 34ms | ✅ |
| recordPublishSuccess_incrementsCounter | 13ms | ✅ |
| recordPublishFailure_incrementsCounter | 12ms | ✅ |
| metricsAreRegistered | 11ms | ✅ |

### OutboxProcessingConfigTest

**Class Total Duration:** 9ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| config_loadsMaxPermanentRetries | 4ms | ✅ |
| config_loadsDefaultPermanentFailureExceptions | 3ms | ✅ |
| config_hasExistingProcessingSettings | 2ms | ✅ |

### OutboxRoutingConfigTest

**Class Total Duration:** 7ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| getRoutingRule_handlesOptionalClusters | 2ms | ✅ |
| getRoutingRule_handlesMultipleClustersWithStrategy | 1ms | ✅ |
| getRoutingRule_handlesIndexedPropertiesFromDynamicRegistry | 1ms | ✅ |
| getRoutingRule_handlesStrategyWithUnderscores | 1ms | ✅ |
| getRoutingRule_defaultsToAllMustSucceedWhenStrategyNotSpecified | 1ms | ✅ |
| getRoutingRule_throwsExceptionForInvalidFormat | 1ms | ✅ |
| getRoutingRule_handlesStrategyWithHyphens | 0ms | ✅ |
| getRoutingRule_returnsNullForUnknownEventType | 0ms | ✅ |
| getRoutingRule_handlesSingleClusterAsString | 0ms | ✅ |
| getRoutingRule_handlesBackwardCompatibleStringFormat | 0ms | ✅ |

### OutboxServiceTest

**Class Total Duration:** 109ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| findPaged_filtersAndSorts | 49ms | ✅ |
| markUnsent_clearsSentAtAndLease | 35ms | ✅ |
| getPendingEvents_returnsAllUnsent | 25ms | ✅ |

### SecurityConfigTest

**Class Total Duration:** 11ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| shouldHaveDisabledSecurityByDefault | 8ms | ✅ |
| shouldLoadSecurityFilterChain | 3ms | ✅ |

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
- **Total Test Duration:** 1.95s
- **Module Execution Time:** 11.05s
- **Average Test Duration:** 1.95s

### Test Details

### KafkaIntegrationTest

**Class Total Duration:** 1.95s

| Test Method | Duration | Status |
|-------------|----------|--------|
| testDynamicFactorySendAndReceive | 1.95s | ✅ |


---

## Module: order-service

### Summary Statistics

- **Test Classes:** 2
- **Test Methods:** 4
- **Passed:** 4
- **Failed:** 0
- **Total Test Duration:** 1.05s
- **Module Execution Time:** 21.21s
- **Average Test Duration:** 262ms

### Test Details

### OrderServiceFailureTest

**Class Total Duration:** 252ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| testOrderCreationFailsWhenOutboxWriteFails | 252ms | ✅ |

### OrderServiceTest

**Class Total Duration:** 797ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| testGetAllOrders | 713ms | ✅ |
| testCreateOrder_CreatesOrderAndOutboxEvent | 52ms | ✅ |
| testUpdateOrderStatus_CreatesOutboxEvent | 32ms | ✅ |


---

## Module: catbox-archunit

### Summary Statistics

- **Test Classes:** 6
- **Test Methods:** 32
- **Passed:** 32
- **Failed:** 0
- **Total Test Duration:** 198ms
- **Module Execution Time:** 3.81s
- **Average Test Duration:** 6ms

### Test Details

### EntityRepositoryPatternTest

**Class Total Duration:** 107ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| tableAnnotationShouldBeUsedForEntities | 71ms | ✅ |
| entitiesShouldNotHavePublicFields | 23ms | ✅ |
| repositoriesShouldBeInterfaces | 7ms | ✅ |
| entitiesIdFieldsShouldBeAnnotatedWithId | 4ms | ✅ |
| entitiesShouldResideInEntityPackage | 2ms | ✅ |

### LayeringArchitectureTest

**Class Total Duration:** 22ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| repositoriesShouldNotAccessServicesOrControllers | 18ms | ✅ |
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

**Class Total Duration:** 21ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| commonModuleShouldNotDependOnOtherModules | 9ms | ✅ |
| clientModuleShouldNotDependOnServerOrOrderService | 4ms | ✅ |
| entitiesShouldNotDependOnServicesOrControllers | 3ms | ✅ |
| serverModuleShouldNotDependOnOrderService | 2ms | ✅ |
| controllersShouldNotDependOnOtherControllers | 1ms | ✅ |
| repositoriesShouldOnlyDependOnEntitiesAndSpringData | 1ms | ✅ |
| orderServiceShouldNotDependOnCatboxServer | 1ms | ✅ |

### SpringAnnotationTest

**Class Total Duration:** 10ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| repositoriesShouldExtendSpringDataRepository | 3ms | ✅ |
| controllersShouldBeAnnotatedWithRestController | 3ms | ✅ |
| serviceMethodsShouldNotBePublicUnlessNecessary | 1ms | ✅ |
| servicesShouldBeAnnotatedWithService | 1ms | ✅ |
| configurationClassesShouldBeAnnotatedWithConfiguration | 1ms | ✅ |
| entitiesShouldBeAnnotatedWithEntity | 1ms | ✅ |

### TransactionBoundaryTest

**Class Total Duration:** 27ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| serviceClassesShouldBeAnnotatedWithTransactional | 15ms | ✅ |
| repositoriesShouldNotDefineTransactions | 9ms | ✅ |
| serviceMethodsModifyingDataShouldBeTransactional | 2ms | ✅ |
| controllersShouldNotBeTransactional | 1ms | ✅ |


---

## Module: catbox-server

### Summary Statistics

- **Test Classes:** 22
- **Test Methods:** 105
- **Passed:** 105
- **Failed:** 0
- **Total Test Duration:** 15.44s
- **Module Execution Time:** 2m 52s
- **Average Test Duration:** 147ms

### Test Details

### AdminControllerTest

**Class Total Duration:** 695ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| adminPage_WithPendingOnlyFilter_ShouldReturnOnlyPending | 500ms | ✅ |
| adminPage_WithPagination_ShouldReturnPagedResults | 93ms | ✅ |
| adminPage_WithFilters_ShouldReturnFilteredResults | 31ms | ✅ |
| adminPage_WithSorting_ShouldReturnSortedResults | 31ms | ✅ |
| adminPage_ShouldReturnAdminView | 22ms | ✅ |
| adminPage_WithDefaultParameters_ShouldUseDefaults | 18ms | ✅ |

### CatboxApplicationTests

**Class Total Duration:** 5ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| contextLoads | 5ms | ✅ |

### CorrelationIdTest

**Class Total Duration:** 136ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| publishEvent_withCorrelationId_sendsCorrelationIdInKafkaHeader | 68ms | ✅ |
| publishEvent_withoutCorrelationId_sendsWithoutHeader | 45ms | ✅ |
| createEvent_withCorrelationId_storesInDatabase | 23ms | ✅ |

### DynamicKafkaTemplateFactoryEvictionTest

**Class Total Duration:** 1.03s

| Test Method | Duration | Status |
|-------------|----------|--------|
| testConcurrentAccessDoesNotCauseExceptions | 1.01s | ✅ |
| testBeanNamingConvention | 7ms | ✅ |
| testTemplateCreationAndCaching | 4ms | ✅ |
| testEvictionHandlesEmptyCache | 4ms | ✅ |
| testTemplateReCreationAfterManualRemoval | 4ms | ✅ |
| testMultipleEvictionCallsDontCauseIssues | 3ms | ✅ |

### DynamicKafkaTemplateFactoryProxyTest

**Class Total Duration:** 91ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| testMultipleCallsDoNotDuplicateBeans | 43ms | ✅ |
| testTemplateCanBeRetrievedDirectlyFromContext | 14ms | ✅ |
| testDynamicallyCreatedTemplateHasSpringLifecycle | 9ms | ✅ |
| testKafkaTemplateIsSingleton | 9ms | ✅ |
| testKafkaTemplateIsSpringManagedBean | 8ms | ✅ |
| testProducerFactoryIsSpringManagedBean | 8ms | ✅ |

### DynamicKafkaTemplateFactorySslBundlePositiveTest

**Class Total Duration:** 47ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| testClusterWithSslBundleHasSslConfiguration | 47ms | ✅ |

### DynamicKafkaTemplateFactorySslBundleTest

**Class Total Duration:** 26ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| testMissingClusterThrowsException | 13ms | ✅ |
| testClusterWithoutSslBundleDoesNotHaveSslConfig | 13ms | ✅ |

### E2EPollerMultiClusterTest

**Class Total Duration:** 7.01s

| Test Method | Duration | Status |
|-------------|----------|--------|
| testPollerRoutesEventsToCorrectClusters | 7.01s | ✅ |

### E2EPollerTest

**Class Total Duration:** 2.58s

| Test Method | Duration | Status |
|-------------|----------|--------|
| testPollerClaimsAndPublishesEvent | 2.58s | ✅ |

### OutboxArchivalServiceTest

**Class Total Duration:** 549ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| archiveOldEvents_movesOldSentEventsToArchive | 106ms | ✅ |
| archiveOldEvents_preservesKafkaMetadata | 104ms | ✅ |
| manualArchive_returnsZeroForInvalidRetention | 75ms | ✅ |
| manualArchive_returnsZeroWhenNoEventsToArchive | 64ms | ✅ |
| archiveOldEvents_preservesAllEventData | 56ms | ✅ |
| manualArchive_archivesWithCustomRetention | 56ms | ✅ |
| manualArchive_returnsZeroForNegativeRetention | 47ms | ✅ |
| archiveOldEvents_doesNothingWhenNoOldEvents | 41ms | ✅ |

### OutboxControllerTest

**Class Total Duration:** 442ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| markOutboxUnsent_ShouldMarkEventAsUnsent | 177ms | ✅ |
| getPendingOutboxEvents_ShouldReturnOnlyPending | 159ms | ✅ |
| searchOutbox_WithFilters_ShouldReturnFilteredResults | 83ms | ✅ |
| getAllOutboxEvents_ShouldReturnAllEvents | 23ms | ✅ |

### OutboxEventClaimTest

**Class Total Duration:** 157ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| testClaimPendingEvents_skipsAlreadySentEvents | 51ms | ✅ |
| testClaimPendingEvents_claimsExpiredInProgressEvents | 49ms | ✅ |
| testClaimPendingEvents_ordersEventsByCreatedAt | 19ms | ✅ |
| testClaimPendingEvents_skipsEventsInProgress | 14ms | ✅ |
| testClaimPendingEvents_claimsEventsSuccessfully | 13ms | ✅ |
| testClaimPendingEvents_respectsBatchSize | 11ms | ✅ |

### OutboxEventClaimerConcurrencyTest

**Class Total Duration:** 729ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| testConcurrentClaimersDoNotProcessSameEvents | 729ms | ✅ |

### OutboxEventPublisherMultiClusterTest

**Class Total Duration:** 275ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| optionalClusters_failsWhenRequiredClusterFails | 68ms | ✅ |
| allMustSucceed_successWhenAllClustersSucceed | 64ms | ✅ |
| atLeastOne_failsWhenAllClustersFail | 52ms | ✅ |
| optionalClusters_successWhenRequiredSucceedsOptionalFails | 36ms | ✅ |
| allMustSucceed_failsWhenOneClusterFails | 28ms | ✅ |
| atLeastOne_successWhenOnlyOneClusterSucceeds | 27ms | ✅ |

### OutboxEventPublisherTest

**Class Total Duration:** 713ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| publishEvent_resetsFailureCountOnSuccess | 347ms | ✅ |
| publishEvent_movesToDeadLetterAfterMaxPermanentRetries | 161ms | ✅ |
| publishEvent_handlesPermanentFailure_kafkaException | 55ms | ✅ |
| publishEvent_successfullySendsAndMarksSent | 53ms | ✅ |
| publishEvent_handlesPermanentFailure_noRoute | 51ms | ✅ |
| publishEvent_capturesKafkaMetadata | 46ms | ✅ |

### OutboxFailureHandlerTest

**Class Total Duration:** 259ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| recordPermanentFailure_movesToDeadLetterAfterMaxRetries | 88ms | ✅ |
| recordPermanentFailure_handlesMultipleEvents | 72ms | ✅ |
| recordPermanentFailure_incrementsFailureCount | 31ms | ✅ |
| recordPermanentFailure_throwsExceptionWhenEventNotFound | 29ms | ✅ |
| resetFailureCount_clearsFailureData | 26ms | ✅ |
| resetFailureCount_doesNothingWhenCountIsZero | 13ms | ✅ |

### OutboxMetricsServiceTest

**Class Total Duration:** 422ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| updateArchivalMetrics_countsDeadLetterCorrectly | 90ms | ✅ |
| recordDeadLetter_incrementsCounter | 89ms | ✅ |
| updatePendingEventsMetrics_calculatesOldestAge | 57ms | ✅ |
| updateArchivalMetrics_countsCorrectly | 36ms | ✅ |
| updatePendingEventsMetrics_whenNoEvents_setsToZero | 33ms | ✅ |
| updatePendingEventsMetrics_countsCorrectly | 24ms | ✅ |
| updateArchivalMetrics_whenNoEvents_setsToZero | 21ms | ✅ |
| recordProcessingDuration_recordsTimer | 16ms | ✅ |
| recordPublishFailure_incrementsCounter | 16ms | ✅ |
| recordArchival_incrementsCounter | 16ms | ✅ |
| metricsAreRegistered | 12ms | ✅ |
| recordPublishSuccess_incrementsCounter | 12ms | ✅ |

### OutboxProcessingConfigTest

**Class Total Duration:** 12ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| config_loadsMaxPermanentRetries | 5ms | ✅ |
| config_loadsDefaultPermanentFailureExceptions | 4ms | ✅ |
| config_hasExistingProcessingSettings | 3ms | ✅ |

### OutboxRoutingConfigTest

**Class Total Duration:** 13ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| getRoutingRule_handlesOptionalClusters | 3ms | ✅ |
| getRoutingRule_handlesMultipleClustersWithStrategy | 2ms | ✅ |
| getRoutingRule_throwsExceptionForInvalidClustersFormat | 2ms | ✅ |
| getRoutingRule_handlesSingleOptionalClusterAsString | 1ms | ✅ |
| getRoutingRule_handlesStrategyWithUnderscores | 1ms | ✅ |
| getRoutingRule_defaultsToAllMustSucceedWhenStrategyNotSpecified | 1ms | ✅ |
| getRoutingRule_throwsExceptionForInvalidFormat | 1ms | ✅ |
| getRoutingRule_handlesIndexedPropertiesFromDynamicRegistry | 1ms | ✅ |
| getRoutingRule_handlesIndexedOptionalProperties | 1ms | ✅ |
| getRoutingRule_handlesStrategyWithHyphens | 0ms | ✅ |
| getRoutingRule_returnsNullForUnknownEventType | 0ms | ✅ |
| getRoutingRule_handlesSingleClusterAsString | 0ms | ✅ |
| getRoutingRule_handlesBackwardCompatibleStringFormat | 0ms | ✅ |

### OutboxServiceTest

**Class Total Duration:** 233ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| findPaged_filtersWithAggregateIdAndPending | 51ms | ✅ |
| markUnsent_clearsSentAtAndLease | 34ms | ✅ |
| markUnsent_throwsExceptionWhenNotFound | 29ms | ✅ |
| findPaged_filtersWithBlankValues | 28ms | ✅ |
| findPaged_withAllNullParameters | 28ms | ✅ |
| findPaged_filtersAndSorts | 27ms | ✅ |
| getPendingEvents_returnsAllUnsent | 19ms | ✅ |
| getAllEvents_returnsAllEvents | 17ms | ✅ |

### SecurityConfigTest

**Class Total Duration:** 5ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| shouldHaveDisabledSecurityByDefault | 3ms | ✅ |
| shouldLoadSecurityFilterChain | 2ms | ✅ |

### SecurityConfigurationTest

**Class Total Duration:** 6ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| testSecureClusterConfiguration | 2ms | ✅ |
| testSaslConfigurationProperties | 2ms | ✅ |
| testSslBundleIsConfigured | 2ms | ✅ |


---

## Module: order-service

### Summary Statistics

- **Test Classes:** 6
- **Test Methods:** 21
- **Passed:** 21
- **Failed:** 0
- **Total Test Duration:** 1.66s
- **Module Execution Time:** 26.47s
- **Average Test Duration:** 79ms

### Test Details

### OrderControllerTest

**Class Total Duration:** 480ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| getAllOrders_ShouldReturnAllOrders | 243ms | ✅ |
| updateOrderStatus_WithNullStatus_ShouldReturnBadRequest | 80ms | ✅ |
| createOrder_ShouldReturnCreatedOrder | 64ms | ✅ |
| getOrderById_ShouldReturnOrder | 41ms | ✅ |
| updateOrderStatus_ShouldUpdateAndReturnOrder | 27ms | ✅ |
| updateOrderStatus_WithBlankStatus_ShouldReturnBadRequest | 25ms | ✅ |

### OrderNotFoundExceptionTest

**Class Total Duration:** 171ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| shouldCreateExceptionWithCorrectMessage | 165ms | ✅ |
| shouldBeRuntimeException | 6ms | ✅ |

### OrderServiceFailureTest

**Class Total Duration:** 215ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| testOrderCreationFailsWhenOutboxWriteFails | 215ms | ✅ |

### OrderServiceTest

**Class Total Duration:** 767ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| testGetAllOrders | 658ms | ✅ |
| testCreateOrder_CreatesOrderAndOutboxEvent | 47ms | ✅ |
| testUpdateOrderStatus_CreatesOutboxEvent | 27ms | ✅ |
| testGetOrderById_ThrowsExceptionWhenNotFound | 18ms | ✅ |
| testGetOrderById_Success | 17ms | ✅ |

### OrderTest

**Class Total Duration:** 8ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| onCreate_shouldNotOverrideExistingStatus | 3ms | ✅ |
| shouldCreateOrderWithConstructor | 2ms | ✅ |
| onCreate_shouldSetDefaultStatus | 2ms | ✅ |
| shouldSupportSettersAndGetters | 1ms | ✅ |

### UpdateStatusRequestTest

**Class Total Duration:** 19ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| shouldSupportEquality | 17ms | ✅ |
| shouldCreateRequestWithStatus | 1ms | ✅ |
| shouldHandleNullStatus | 1ms | ✅ |


---

## Module: catbox-archunit

### Summary Statistics

- **Test Classes:** 6
- **Test Methods:** 32
- **Passed:** 32
- **Failed:** 0
- **Total Test Duration:** 201ms
- **Module Execution Time:** 3.67s
- **Average Test Duration:** 6ms

### Test Details

### EntityRepositoryPatternTest

**Class Total Duration:** 114ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| tableAnnotationShouldBeUsedForEntities | 78ms | ✅ |
| entitiesShouldNotHavePublicFields | 23ms | ✅ |
| repositoriesShouldBeInterfaces | 7ms | ✅ |
| entitiesIdFieldsShouldBeAnnotatedWithId | 4ms | ✅ |
| entitiesShouldResideInEntityPackage | 2ms | ✅ |

### LayeringArchitectureTest

**Class Total Duration:** 23ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| repositoriesShouldNotAccessServicesOrControllers | 18ms | ✅ |
| servicesShouldNotAccessControllers | 4ms | ✅ |
| controllersShouldNotAccessRepositoriesDirectly | 1ms | ✅ |

### NamingConventionTest

**Class Total Duration:** 9ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| servicesShouldBeSuffixed | 3ms | ✅ |
| repositoriesShouldBeSuffixed | 1ms | ✅ |
| configurationsShouldBeSuffixed | 1ms | ✅ |
| entitiesShouldResideInEntityPackage | 1ms | ✅ |
| dtosShouldResideInDtoPackage | 1ms | ✅ |
| controllersShouldBeSuffixed | 1ms | ✅ |
| exceptionsShouldBeSuffixed | 1ms | ✅ |

### PackageDependencyTest

**Class Total Duration:** 24ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| commonModuleShouldNotDependOnOtherModules | 10ms | ✅ |
| clientModuleShouldNotDependOnServerOrOrderService | 6ms | ✅ |
| entitiesShouldNotDependOnServicesOrControllers | 3ms | ✅ |
| serverModuleShouldNotDependOnOrderService | 2ms | ✅ |
| controllersShouldNotDependOnOtherControllers | 1ms | ✅ |
| repositoriesShouldOnlyDependOnEntitiesAndSpringData | 1ms | ✅ |
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

**Class Total Duration:** 21ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| repositoriesShouldNotDefineTransactions | 10ms | ✅ |
| serviceClassesShouldBeAnnotatedWithTransactional | 7ms | ✅ |
| serviceMethodsModifyingDataShouldBeTransactional | 3ms | ✅ |
| controllersShouldNotBeTransactional | 1ms | ✅ |


---

## Module: catbox-server

### Summary Statistics

- **Test Classes:** 22
- **Test Methods:** 105
- **Passed:** 105
- **Failed:** 0
- **Total Test Duration:** 15.18s
- **Module Execution Time:** 2m 54s
- **Average Test Duration:** 144ms

### Test Details

### AdminControllerTest

**Class Total Duration:** 630ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| adminPage_WithPendingOnlyFilter_ShouldReturnOnlyPending | 437ms | ✅ |
| adminPage_WithPagination_ShouldReturnPagedResults | 90ms | ✅ |
| adminPage_WithSorting_ShouldReturnSortedResults | 32ms | ✅ |
| adminPage_WithFilters_ShouldReturnFilteredResults | 30ms | ✅ |
| adminPage_ShouldReturnAdminView | 22ms | ✅ |
| adminPage_WithDefaultParameters_ShouldUseDefaults | 19ms | ✅ |

### CatboxApplicationTests

**Class Total Duration:** 4ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| contextLoads | 4ms | ✅ |

### CorrelationIdTest

**Class Total Duration:** 147ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| publishEvent_withCorrelationId_sendsCorrelationIdInKafkaHeader | 62ms | ✅ |
| publishEvent_withoutCorrelationId_sendsWithoutHeader | 59ms | ✅ |
| createEvent_withCorrelationId_storesInDatabase | 26ms | ✅ |

### DynamicKafkaTemplateFactoryEvictionTest

**Class Total Duration:** 1.04s

| Test Method | Duration | Status |
|-------------|----------|--------|
| testConcurrentAccessDoesNotCauseExceptions | 1.01s | ✅ |
| testBeanNamingConvention | 10ms | ✅ |
| testTemplateCreationAndCaching | 4ms | ✅ |
| testEvictionHandlesEmptyCache | 4ms | ✅ |
| testTemplateReCreationAfterManualRemoval | 4ms | ✅ |
| testMultipleEvictionCallsDontCauseIssues | 3ms | ✅ |

### DynamicKafkaTemplateFactoryProxyTest

**Class Total Duration:** 77ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| testMultipleCallsDoNotDuplicateBeans | 40ms | ✅ |
| testTemplateCanBeRetrievedDirectlyFromContext | 14ms | ✅ |
| testDynamicallyCreatedTemplateHasSpringLifecycle | 7ms | ✅ |
| testKafkaTemplateIsSingleton | 7ms | ✅ |
| testKafkaTemplateIsSpringManagedBean | 5ms | ✅ |
| testProducerFactoryIsSpringManagedBean | 4ms | ✅ |

### DynamicKafkaTemplateFactorySslBundlePositiveTest

**Class Total Duration:** 53ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| testClusterWithSslBundleHasSslConfiguration | 53ms | ✅ |

### DynamicKafkaTemplateFactorySslBundleTest

**Class Total Duration:** 22ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| testMissingClusterThrowsException | 11ms | ✅ |
| testClusterWithoutSslBundleDoesNotHaveSslConfig | 11ms | ✅ |

### E2EPollerMultiClusterTest

**Class Total Duration:** 6.58s

| Test Method | Duration | Status |
|-------------|----------|--------|
| testPollerRoutesEventsToCorrectClusters | 6.58s | ✅ |

### E2EPollerTest

**Class Total Duration:** 2.66s

| Test Method | Duration | Status |
|-------------|----------|--------|
| testPollerClaimsAndPublishesEvent | 2.66s | ✅ |

### OutboxArchivalServiceTest

**Class Total Duration:** 577ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| archiveOldEvents_movesOldSentEventsToArchive | 115ms | ✅ |
| archiveOldEvents_preservesKafkaMetadata | 114ms | ✅ |
| archiveOldEvents_preservesAllEventData | 73ms | ✅ |
| manualArchive_returnsZeroWhenNoEventsToArchive | 67ms | ✅ |
| manualArchive_returnsZeroForInvalidRetention | 63ms | ✅ |
| manualArchive_archivesWithCustomRetention | 57ms | ✅ |
| manualArchive_returnsZeroForNegativeRetention | 45ms | ✅ |
| archiveOldEvents_doesNothingWhenNoOldEvents | 43ms | ✅ |

### OutboxControllerTest

**Class Total Duration:** 413ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| markOutboxUnsent_ShouldMarkEventAsUnsent | 167ms | ✅ |
| getPendingOutboxEvents_ShouldReturnOnlyPending | 142ms | ✅ |
| searchOutbox_WithFilters_ShouldReturnFilteredResults | 79ms | ✅ |
| getAllOutboxEvents_ShouldReturnAllEvents | 25ms | ✅ |

### OutboxEventClaimTest

**Class Total Duration:** 192ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| testClaimPendingEvents_claimsEventsSuccessfully | 60ms | ✅ |
| testClaimPendingEvents_claimsExpiredInProgressEvents | 59ms | ✅ |
| testClaimPendingEvents_ordersEventsByCreatedAt | 21ms | ✅ |
| testClaimPendingEvents_skipsEventsInProgress | 19ms | ✅ |
| testClaimPendingEvents_skipsAlreadySentEvents | 19ms | ✅ |
| testClaimPendingEvents_respectsBatchSize | 14ms | ✅ |

### OutboxEventClaimerConcurrencyTest

**Class Total Duration:** 807ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| testConcurrentClaimersDoNotProcessSameEvents | 807ms | ✅ |

### OutboxEventPublisherMultiClusterTest

**Class Total Duration:** 210ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| allMustSucceed_successWhenAllClustersSucceed | 55ms | ✅ |
| atLeastOne_failsWhenAllClustersFail | 41ms | ✅ |
| optionalClusters_failsWhenRequiredClusterFails | 37ms | ✅ |
| atLeastOne_successWhenOnlyOneClusterSucceeds | 30ms | ✅ |
| optionalClusters_successWhenRequiredSucceedsOptionalFails | 28ms | ✅ |
| allMustSucceed_failsWhenOneClusterFails | 19ms | ✅ |

### OutboxEventPublisherTest

**Class Total Duration:** 750ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| publishEvent_resetsFailureCountOnSuccess | 368ms | ✅ |
| publishEvent_movesToDeadLetterAfterMaxPermanentRetries | 167ms | ✅ |
| publishEvent_handlesPermanentFailure_kafkaException | 62ms | ✅ |
| publishEvent_capturesKafkaMetadata | 54ms | ✅ |
| publishEvent_handlesPermanentFailure_noRoute | 51ms | ✅ |
| publishEvent_successfullySendsAndMarksSent | 48ms | ✅ |

### OutboxFailureHandlerTest

**Class Total Duration:** 257ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| recordPermanentFailure_movesToDeadLetterAfterMaxRetries | 88ms | ✅ |
| recordPermanentFailure_handlesMultipleEvents | 64ms | ✅ |
| recordPermanentFailure_incrementsFailureCount | 41ms | ✅ |
| resetFailureCount_clearsFailureData | 27ms | ✅ |
| recordPermanentFailure_throwsExceptionWhenEventNotFound | 21ms | ✅ |
| resetFailureCount_doesNothingWhenCountIsZero | 16ms | ✅ |

### OutboxMetricsServiceTest

**Class Total Duration:** 475ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| updateArchivalMetrics_countsDeadLetterCorrectly | 88ms | ✅ |
| recordDeadLetter_incrementsCounter | 86ms | ✅ |
| updatePendingEventsMetrics_calculatesOldestAge | 53ms | ✅ |
| updateArchivalMetrics_countsCorrectly | 49ms | ✅ |
| updateArchivalMetrics_whenNoEvents_setsToZero | 46ms | ✅ |
| updatePendingEventsMetrics_whenNoEvents_setsToZero | 36ms | ✅ |
| updatePendingEventsMetrics_countsCorrectly | 31ms | ✅ |
| recordProcessingDuration_recordsTimer | 22ms | ✅ |
| recordPublishFailure_incrementsCounter | 19ms | ✅ |
| recordArchival_incrementsCounter | 16ms | ✅ |
| metricsAreRegistered | 16ms | ✅ |
| recordPublishSuccess_incrementsCounter | 13ms | ✅ |

### OutboxProcessingConfigTest

**Class Total Duration:** 12ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| config_loadsMaxPermanentRetries | 5ms | ✅ |
| config_loadsDefaultPermanentFailureExceptions | 4ms | ✅ |
| config_hasExistingProcessingSettings | 3ms | ✅ |

### OutboxRoutingConfigTest

**Class Total Duration:** 6ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| getRoutingRule_handlesOptionalClusters | 3ms | ✅ |
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

**Class Total Duration:** 256ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| findPaged_filtersWithAggregateIdAndPending | 52ms | ✅ |
| findPaged_filtersWithBlankValues | 45ms | ✅ |
| markUnsent_clearsSentAtAndLease | 39ms | ✅ |
| getAllEvents_returnsAllEvents | 33ms | ✅ |
| findPaged_filtersAndSorts | 27ms | ✅ |
| getPendingEvents_returnsAllUnsent | 22ms | ✅ |
| markUnsent_throwsExceptionWhenNotFound | 20ms | ✅ |
| findPaged_withAllNullParameters | 18ms | ✅ |

### SecurityConfigTest

**Class Total Duration:** 6ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| shouldLoadSecurityFilterChain | 3ms | ✅ |
| shouldHaveDisabledSecurityByDefault | 3ms | ✅ |

### SecurityConfigurationTest

**Class Total Duration:** 6ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| testSecureClusterConfiguration | 2ms | ✅ |
| testSaslConfigurationProperties | 2ms | ✅ |
| testSslBundleIsConfigured | 2ms | ✅ |


---

## Module: order-service

### Summary Statistics

- **Test Classes:** 6
- **Test Methods:** 21
- **Passed:** 21
- **Failed:** 0
- **Total Test Duration:** 1.66s
- **Module Execution Time:** 26.63s
- **Average Test Duration:** 79ms

### Test Details

### OrderControllerTest

**Class Total Duration:** 471ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| getAllOrders_ShouldReturnAllOrders | 269ms | ✅ |
| updateOrderStatus_WithNullStatus_ShouldReturnBadRequest | 75ms | ✅ |
| createOrder_ShouldReturnCreatedOrder | 45ms | ✅ |
| getOrderById_ShouldReturnOrder | 39ms | ✅ |
| updateOrderStatus_ShouldUpdateAndReturnOrder | 23ms | ✅ |
| updateOrderStatus_WithBlankStatus_ShouldReturnBadRequest | 20ms | ✅ |

### OrderNotFoundExceptionTest

**Class Total Duration:** 121ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| shouldCreateExceptionWithCorrectMessage | 117ms | ✅ |
| shouldBeRuntimeException | 4ms | ✅ |

### OrderServiceFailureTest

**Class Total Duration:** 229ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| testOrderCreationFailsWhenOutboxWriteFails | 229ms | ✅ |

### OrderServiceTest

**Class Total Duration:** 814ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| testGetAllOrders | 695ms | ✅ |
| testCreateOrder_CreatesOrderAndOutboxEvent | 42ms | ✅ |
| testUpdateOrderStatus_CreatesOutboxEvent | 36ms | ✅ |
| testGetOrderById_ThrowsExceptionWhenNotFound | 22ms | ✅ |
| testGetOrderById_Success | 19ms | ✅ |

### OrderTest

**Class Total Duration:** 6ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| shouldCreateOrderWithConstructor | 2ms | ✅ |
| onCreate_shouldNotOverrideExistingStatus | 2ms | ✅ |
| shouldSupportSettersAndGetters | 1ms | ✅ |
| onCreate_shouldSetDefaultStatus | 1ms | ✅ |

### UpdateStatusRequestTest

**Class Total Duration:** 19ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| shouldSupportEquality | 16ms | ✅ |
| shouldCreateRequestWithStatus | 2ms | ✅ |
| shouldHandleNullStatus | 1ms | ✅ |


---

## Module: catbox-archunit

### Summary Statistics

- **Test Classes:** 6
- **Test Methods:** 32
- **Passed:** 32
- **Failed:** 0
- **Total Test Duration:** 209ms
- **Module Execution Time:** 4.18s
- **Average Test Duration:** 6ms

### Test Details

### EntityRepositoryPatternTest

**Class Total Duration:** 104ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| tableAnnotationShouldBeUsedForEntities | 66ms | ✅ |
| entitiesShouldNotHavePublicFields | 23ms | ✅ |
| repositoriesShouldBeInterfaces | 9ms | ✅ |
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

**Class Total Duration:** 9ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| servicesShouldBeSuffixed | 3ms | ✅ |
| repositoriesShouldBeSuffixed | 1ms | ✅ |
| configurationsShouldBeSuffixed | 1ms | ✅ |
| entitiesShouldResideInEntityPackage | 1ms | ✅ |
| dtosShouldResideInDtoPackage | 1ms | ✅ |
| controllersShouldBeSuffixed | 1ms | ✅ |
| exceptionsShouldBeSuffixed | 1ms | ✅ |

### PackageDependencyTest

**Class Total Duration:** 34ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| clientModuleShouldNotDependOnServerOrOrderService | 15ms | ✅ |
| commonModuleShouldNotDependOnOtherModules | 9ms | ✅ |
| serverModuleShouldNotDependOnOrderService | 3ms | ✅ |
| entitiesShouldNotDependOnServicesOrControllers | 3ms | ✅ |
| repositoriesShouldOnlyDependOnEntitiesAndSpringData | 2ms | ✅ |
| controllersShouldNotDependOnOtherControllers | 1ms | ✅ |
| orderServiceShouldNotDependOnCatboxServer | 1ms | ✅ |

### SpringAnnotationTest

**Class Total Duration:** 14ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| repositoriesShouldExtendSpringDataRepository | 5ms | ✅ |
| controllersShouldBeAnnotatedWithRestController | 4ms | ✅ |
| configurationClassesShouldBeAnnotatedWithConfiguration | 2ms | ✅ |
| serviceMethodsShouldNotBePublicUnlessNecessary | 1ms | ✅ |
| servicesShouldBeAnnotatedWithService | 1ms | ✅ |
| entitiesShouldBeAnnotatedWithEntity | 1ms | ✅ |

### TransactionBoundaryTest

**Class Total Duration:** 23ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| repositoriesShouldNotDefineTransactions | 10ms | ✅ |
| serviceClassesShouldBeAnnotatedWithTransactional | 8ms | ✅ |
| serviceMethodsModifyingDataShouldBeTransactional | 3ms | ✅ |
| controllersShouldNotBeTransactional | 2ms | ✅ |


---

## Module: catbox-server

### Summary Statistics

- **Test Classes:** 22
- **Test Methods:** 105
- **Passed:** 105
- **Failed:** 0
- **Total Test Duration:** 15.38s
- **Module Execution Time:** 2m 26s
- **Average Test Duration:** 146ms

### Test Details

### AdminControllerTest

**Class Total Duration:** 702ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| adminPage_WithPendingOnlyFilter_ShouldReturnOnlyPending | 493ms | ✅ |
| adminPage_WithPagination_ShouldReturnPagedResults | 91ms | ✅ |
| adminPage_WithFilters_ShouldReturnFilteredResults | 37ms | ✅ |
| adminPage_WithSorting_ShouldReturnSortedResults | 32ms | ✅ |
| adminPage_ShouldReturnAdminView | 27ms | ✅ |
| adminPage_WithDefaultParameters_ShouldUseDefaults | 22ms | ✅ |

### CatboxApplicationTests

**Class Total Duration:** 4ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| contextLoads | 4ms | ✅ |

### CorrelationIdTest

**Class Total Duration:** 164ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| publishEvent_withCorrelationId_sendsCorrelationIdInKafkaHeader | 76ms | ✅ |
| publishEvent_withoutCorrelationId_sendsWithoutHeader | 53ms | ✅ |
| createEvent_withCorrelationId_storesInDatabase | 35ms | ✅ |

### DynamicKafkaTemplateFactoryEvictionTest

**Class Total Duration:** 1.04s

| Test Method | Duration | Status |
|-------------|----------|--------|
| testConcurrentAccessDoesNotCauseExceptions | 1.01s | ✅ |
| testBeanNamingConvention | 13ms | ✅ |
| testTemplateCreationAndCaching | 4ms | ✅ |
| testTemplateReCreationAfterManualRemoval | 4ms | ✅ |
| testEvictionHandlesEmptyCache | 3ms | ✅ |
| testMultipleEvictionCallsDontCauseIssues | 3ms | ✅ |

### DynamicKafkaTemplateFactoryProxyTest

**Class Total Duration:** 92ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| testMultipleCallsDoNotDuplicateBeans | 39ms | ✅ |
| testTemplateCanBeRetrievedDirectlyFromContext | 22ms | ✅ |
| testDynamicallyCreatedTemplateHasSpringLifecycle | 8ms | ✅ |
| testKafkaTemplateIsSpringManagedBean | 8ms | ✅ |
| testKafkaTemplateIsSingleton | 8ms | ✅ |
| testProducerFactoryIsSpringManagedBean | 7ms | ✅ |

### DynamicKafkaTemplateFactorySslBundlePositiveTest

**Class Total Duration:** 61ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| testClusterWithSslBundleHasSslConfiguration | 61ms | ✅ |

### DynamicKafkaTemplateFactorySslBundleTest

**Class Total Duration:** 23ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| testClusterWithoutSslBundleDoesNotHaveSslConfig | 14ms | ✅ |
| testMissingClusterThrowsException | 9ms | ✅ |

### E2EPollerMultiClusterTest

**Class Total Duration:** 6.67s

| Test Method | Duration | Status |
|-------------|----------|--------|
| testPollerRoutesEventsToCorrectClusters | 6.67s | ✅ |

### E2EPollerTest

**Class Total Duration:** 2.69s

| Test Method | Duration | Status |
|-------------|----------|--------|
| testPollerClaimsAndPublishesEvent | 2.69s | ✅ |

### OutboxArchivalServiceTest

**Class Total Duration:** 552ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| archiveOldEvents_preservesKafkaMetadata | 107ms | ✅ |
| archiveOldEvents_movesOldSentEventsToArchive | 100ms | ✅ |
| manualArchive_returnsZeroWhenNoEventsToArchive | 88ms | ✅ |
| manualArchive_returnsZeroForInvalidRetention | 71ms | ✅ |
| archiveOldEvents_preservesAllEventData | 50ms | ✅ |
| manualArchive_archivesWithCustomRetention | 50ms | ✅ |
| archiveOldEvents_doesNothingWhenNoOldEvents | 49ms | ✅ |
| manualArchive_returnsZeroForNegativeRetention | 37ms | ✅ |

### OutboxControllerTest

**Class Total Duration:** 435ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| markOutboxUnsent_ShouldMarkEventAsUnsent | 180ms | ✅ |
| getPendingOutboxEvents_ShouldReturnOnlyPending | 147ms | ✅ |
| searchOutbox_WithFilters_ShouldReturnFilteredResults | 76ms | ✅ |
| getAllOutboxEvents_ShouldReturnAllEvents | 32ms | ✅ |

### OutboxEventClaimTest

**Class Total Duration:** 128ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| testClaimPendingEvents_claimsExpiredInProgressEvents | 46ms | ✅ |
| testClaimPendingEvents_claimsEventsSuccessfully | 23ms | ✅ |
| testClaimPendingEvents_skipsAlreadySentEvents | 16ms | ✅ |
| testClaimPendingEvents_respectsBatchSize | 16ms | ✅ |
| testClaimPendingEvents_skipsEventsInProgress | 14ms | ✅ |
| testClaimPendingEvents_ordersEventsByCreatedAt | 13ms | ✅ |

### OutboxEventClaimerConcurrencyTest

**Class Total Duration:** 854ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| testConcurrentClaimersDoNotProcessSameEvents | 854ms | ✅ |

### OutboxEventPublisherMultiClusterTest

**Class Total Duration:** 253ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| allMustSucceed_successWhenAllClustersSucceed | 70ms | ✅ |
| optionalClusters_successWhenRequiredSucceedsOptionalFails | 44ms | ✅ |
| atLeastOne_successWhenOnlyOneClusterSucceeds | 41ms | ✅ |
| atLeastOne_failsWhenAllClustersFail | 39ms | ✅ |
| optionalClusters_failsWhenRequiredClusterFails | 30ms | ✅ |
| allMustSucceed_failsWhenOneClusterFails | 29ms | ✅ |

### OutboxEventPublisherTest

**Class Total Duration:** 786ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| publishEvent_resetsFailureCountOnSuccess | 355ms | ✅ |
| publishEvent_movesToDeadLetterAfterMaxPermanentRetries | 194ms | ✅ |
| publishEvent_handlesPermanentFailure_kafkaException | 69ms | ✅ |
| publishEvent_handlesPermanentFailure_noRoute | 62ms | ✅ |
| publishEvent_capturesKafkaMetadata | 54ms | ✅ |
| publishEvent_successfullySendsAndMarksSent | 52ms | ✅ |

### OutboxFailureHandlerTest

**Class Total Duration:** 254ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| recordPermanentFailure_movesToDeadLetterAfterMaxRetries | 78ms | ✅ |
| recordPermanentFailure_handlesMultipleEvents | 63ms | ✅ |
| recordPermanentFailure_incrementsFailureCount | 39ms | ✅ |
| resetFailureCount_clearsFailureData | 32ms | ✅ |
| recordPermanentFailure_throwsExceptionWhenEventNotFound | 26ms | ✅ |
| resetFailureCount_doesNothingWhenCountIsZero | 16ms | ✅ |

### OutboxMetricsServiceTest

**Class Total Duration:** 433ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| recordDeadLetter_incrementsCounter | 86ms | ✅ |
| updateArchivalMetrics_countsDeadLetterCorrectly | 76ms | ✅ |
| updatePendingEventsMetrics_calculatesOldestAge | 41ms | ✅ |
| updateArchivalMetrics_countsCorrectly | 34ms | ✅ |
| updatePendingEventsMetrics_countsCorrectly | 34ms | ✅ |
| updateArchivalMetrics_whenNoEvents_setsToZero | 32ms | ✅ |
| updatePendingEventsMetrics_whenNoEvents_setsToZero | 32ms | ✅ |
| metricsAreRegistered | 31ms | ✅ |
| recordPublishFailure_incrementsCounter | 20ms | ✅ |
| recordProcessingDuration_recordsTimer | 19ms | ✅ |
| recordArchival_incrementsCounter | 14ms | ✅ |
| recordPublishSuccess_incrementsCounter | 14ms | ✅ |

### OutboxProcessingConfigTest

**Class Total Duration:** 9ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| config_loadsDefaultPermanentFailureExceptions | 3ms | ✅ |
| config_hasExistingProcessingSettings | 3ms | ✅ |
| config_loadsMaxPermanentRetries | 3ms | ✅ |

### OutboxRoutingConfigTest

**Class Total Duration:** 6ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| getRoutingRule_handlesOptionalClusters | 3ms | ✅ |
| getRoutingRule_handlesIndexedPropertiesFromDynamicRegistry | 1ms | ✅ |
| getRoutingRule_throwsExceptionForInvalidClustersFormat | 1ms | ✅ |
| getRoutingRule_handlesBackwardCompatibleStringFormat | 1ms | ✅ |
| getRoutingRule_handlesStrategyWithHyphens | 0ms | ✅ |
| getRoutingRule_handlesSingleOptionalClusterAsString | 0ms | ✅ |
| getRoutingRule_handlesStrategyWithUnderscores | 0ms | ✅ |
| getRoutingRule_defaultsToAllMustSucceedWhenStrategyNotSpecified | 0ms | ✅ |
| getRoutingRule_throwsExceptionForInvalidFormat | 0ms | ✅ |
| getRoutingRule_handlesMultipleClustersWithStrategy | 0ms | ✅ |
| getRoutingRule_handlesIndexedOptionalProperties | 0ms | ✅ |
| getRoutingRule_returnsNullForUnknownEventType | 0ms | ✅ |
| getRoutingRule_handlesSingleClusterAsString | 0ms | ✅ |

### OutboxServiceTest

**Class Total Duration:** 217ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| findPaged_filtersWithBlankValues | 39ms | ✅ |
| findPaged_filtersWithAggregateIdAndPending | 38ms | ✅ |
| markUnsent_clearsSentAtAndLease | 30ms | ✅ |
| findPaged_withAllNullParameters | 26ms | ✅ |
| findPaged_filtersAndSorts | 25ms | ✅ |
| getAllEvents_returnsAllEvents | 21ms | ✅ |
| getPendingEvents_returnsAllUnsent | 20ms | ✅ |
| markUnsent_throwsExceptionWhenNotFound | 18ms | ✅ |

### SecurityConfigTest

**Class Total Duration:** 8ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| shouldHaveDisabledSecurityByDefault | 5ms | ✅ |
| shouldLoadSecurityFilterChain | 3ms | ✅ |

### SecurityConfigurationTest

**Class Total Duration:** 6ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| testSecureClusterConfiguration | 3ms | ✅ |
| testSaslConfigurationProperties | 2ms | ✅ |
| testSslBundleIsConfigured | 1ms | ✅ |


---

## Module: order-service

### Summary Statistics

- **Test Classes:** 6
- **Test Methods:** 21
- **Passed:** 20
- **Failed:** 1
- **Total Test Duration:** 1.72s
- **Module Execution Time:** 30.03s
- **Average Test Duration:** 81ms

### Test Details

### OrderControllerTest

**Class Total Duration:** 469ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| getAllOrders_ShouldReturnAllOrders | 238ms | ✅ |
| updateOrderStatus_WithNullStatus_ShouldReturnBadRequest | 79ms | ✅ |
| getOrderById_ShouldReturnOrder | 67ms | ✅ |
| createOrder_ShouldReturnCreatedOrder | 39ms | ✅ |
| updateOrderStatus_ShouldUpdateAndReturnOrder | 26ms | ✅ |
| updateOrderStatus_WithBlankStatus_ShouldReturnBadRequest | 20ms | ✅ |

### OrderNotFoundExceptionTest

**Class Total Duration:** 122ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| shouldCreateExceptionWithCorrectMessage | 118ms | ✅ |
| shouldBeRuntimeException | 4ms | ✅ |

### OrderServiceFailureTest

**Class Total Duration:** 228ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| testOrderCreationFailsWhenOutboxWriteFails | 228ms | ❌ |

### OrderServiceTest

**Class Total Duration:** 863ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| testGetAllOrders | 735ms | ✅ |
| testCreateOrder_CreatesOrderAndOutboxEvent | 51ms | ✅ |
| testUpdateOrderStatus_CreatesOutboxEvent | 33ms | ✅ |
| testGetOrderById_ThrowsExceptionWhenNotFound | 25ms | ✅ |
| testGetOrderById_Success | 19ms | ✅ |

### OrderTest

**Class Total Duration:** 8ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| onCreate_shouldNotOverrideExistingStatus | 3ms | ✅ |
| shouldSupportSettersAndGetters | 3ms | ✅ |
| shouldCreateOrderWithConstructor | 1ms | ✅ |
| onCreate_shouldSetDefaultStatus | 1ms | ✅ |

### UpdateStatusRequestTest

**Class Total Duration:** 26ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| shouldSupportEquality | 22ms | ✅ |
| shouldCreateRequestWithStatus | 3ms | ✅ |
| shouldHandleNullStatus | 1ms | ✅ |


---

## Module: catbox-server

### Summary Statistics

- **Test Classes:** 22
- **Test Methods:** 105
- **Passed:** 105
- **Failed:** 0
- **Total Test Duration:** 15.16s
- **Module Execution Time:** 2m 30s
- **Average Test Duration:** 144ms

### Test Details

### AdminControllerTest

**Class Total Duration:** 650ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| adminPage_WithPendingOnlyFilter_ShouldReturnOnlyPending | 449ms | ✅ |
| adminPage_WithPagination_ShouldReturnPagedResults | 84ms | ✅ |
| adminPage_WithFilters_ShouldReturnFilteredResults | 36ms | ✅ |
| adminPage_WithSorting_ShouldReturnSortedResults | 32ms | ✅ |
| adminPage_ShouldReturnAdminView | 25ms | ✅ |
| adminPage_WithDefaultParameters_ShouldUseDefaults | 24ms | ✅ |

### CatboxApplicationTests

**Class Total Duration:** 4ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| contextLoads | 4ms | ✅ |

### CorrelationIdTest

**Class Total Duration:** 141ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| publishEvent_withoutCorrelationId_sendsWithoutHeader | 61ms | ✅ |
| publishEvent_withCorrelationId_sendsCorrelationIdInKafkaHeader | 58ms | ✅ |
| createEvent_withCorrelationId_storesInDatabase | 22ms | ✅ |

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

**Class Total Duration:** 81ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| testMultipleCallsDoNotDuplicateBeans | 37ms | ✅ |
| testTemplateCanBeRetrievedDirectlyFromContext | 21ms | ✅ |
| testDynamicallyCreatedTemplateHasSpringLifecycle | 6ms | ✅ |
| testKafkaTemplateIsSpringManagedBean | 6ms | ✅ |
| testKafkaTemplateIsSingleton | 6ms | ✅ |
| testProducerFactoryIsSpringManagedBean | 5ms | ✅ |

### DynamicKafkaTemplateFactorySslBundlePositiveTest

**Class Total Duration:** 77ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| testClusterWithSslBundleHasSslConfiguration | 77ms | ✅ |

### DynamicKafkaTemplateFactorySslBundleTest

**Class Total Duration:** 27ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| testClusterWithoutSslBundleDoesNotHaveSslConfig | 16ms | ✅ |
| testMissingClusterThrowsException | 11ms | ✅ |

### E2EPollerMultiClusterTest

**Class Total Duration:** 6.64s

| Test Method | Duration | Status |
|-------------|----------|--------|
| testPollerRoutesEventsToCorrectClusters | 6.64s | ✅ |

### E2EPollerTest

**Class Total Duration:** 2.70s

| Test Method | Duration | Status |
|-------------|----------|--------|
| testPollerClaimsAndPublishesEvent | 2.70s | ✅ |

### OutboxArchivalServiceTest

**Class Total Duration:** 516ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| archiveOldEvents_movesOldSentEventsToArchive | 100ms | ✅ |
| archiveOldEvents_preservesKafkaMetadata | 82ms | ✅ |
| manualArchive_returnsZeroForInvalidRetention | 68ms | ✅ |
| manualArchive_archivesWithCustomRetention | 60ms | ✅ |
| manualArchive_returnsZeroWhenNoEventsToArchive | 60ms | ✅ |
| archiveOldEvents_doesNothingWhenNoOldEvents | 57ms | ✅ |
| archiveOldEvents_preservesAllEventData | 49ms | ✅ |
| manualArchive_returnsZeroForNegativeRetention | 40ms | ✅ |

### OutboxControllerTest

**Class Total Duration:** 392ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| markOutboxUnsent_ShouldMarkEventAsUnsent | 153ms | ✅ |
| getPendingOutboxEvents_ShouldReturnOnlyPending | 148ms | ✅ |
| searchOutbox_WithFilters_ShouldReturnFilteredResults | 66ms | ✅ |
| getAllOutboxEvents_ShouldReturnAllEvents | 25ms | ✅ |

### OutboxEventClaimTest

**Class Total Duration:** 112ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| testClaimPendingEvents_claimsExpiredInProgressEvents | 33ms | ✅ |
| testClaimPendingEvents_respectsBatchSize | 25ms | ✅ |
| testClaimPendingEvents_skipsEventsInProgress | 14ms | ✅ |
| testClaimPendingEvents_claimsEventsSuccessfully | 14ms | ✅ |
| testClaimPendingEvents_skipsAlreadySentEvents | 13ms | ✅ |
| testClaimPendingEvents_ordersEventsByCreatedAt | 13ms | ✅ |

### OutboxEventClaimerConcurrencyTest

**Class Total Duration:** 768ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| testConcurrentClaimersDoNotProcessSameEvents | 768ms | ✅ |

### OutboxEventPublisherMultiClusterTest

**Class Total Duration:** 264ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| allMustSucceed_successWhenAllClustersSucceed | 66ms | ✅ |
| atLeastOne_failsWhenAllClustersFail | 51ms | ✅ |
| optionalClusters_failsWhenRequiredClusterFails | 45ms | ✅ |
| atLeastOne_successWhenOnlyOneClusterSucceeds | 44ms | ✅ |
| optionalClusters_successWhenRequiredSucceedsOptionalFails | 31ms | ✅ |
| allMustSucceed_failsWhenOneClusterFails | 27ms | ✅ |

### OutboxEventPublisherTest

**Class Total Duration:** 744ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| publishEvent_resetsFailureCountOnSuccess | 337ms | ✅ |
| publishEvent_movesToDeadLetterAfterMaxPermanentRetries | 177ms | ✅ |
| publishEvent_capturesKafkaMetadata | 68ms | ✅ |
| publishEvent_handlesPermanentFailure_kafkaException | 63ms | ✅ |
| publishEvent_handlesPermanentFailure_noRoute | 53ms | ✅ |
| publishEvent_successfullySendsAndMarksSent | 46ms | ✅ |

### OutboxFailureHandlerTest

**Class Total Duration:** 312ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| recordPermanentFailure_movesToDeadLetterAfterMaxRetries | 97ms | ✅ |
| recordPermanentFailure_handlesMultipleEvents | 77ms | ✅ |
| recordPermanentFailure_incrementsFailureCount | 51ms | ✅ |
| recordPermanentFailure_throwsExceptionWhenEventNotFound | 31ms | ✅ |
| resetFailureCount_clearsFailureData | 28ms | ✅ |
| resetFailureCount_doesNothingWhenCountIsZero | 28ms | ✅ |

### OutboxMetricsServiceTest

**Class Total Duration:** 438ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| updateArchivalMetrics_countsDeadLetterCorrectly | 85ms | ✅ |
| recordDeadLetter_incrementsCounter | 83ms | ✅ |
| updatePendingEventsMetrics_calculatesOldestAge | 55ms | ✅ |
| updatePendingEventsMetrics_whenNoEvents_setsToZero | 37ms | ✅ |
| updateArchivalMetrics_whenNoEvents_setsToZero | 34ms | ✅ |
| updatePendingEventsMetrics_countsCorrectly | 34ms | ✅ |
| updateArchivalMetrics_countsCorrectly | 32ms | ✅ |
| metricsAreRegistered | 19ms | ✅ |
| recordProcessingDuration_recordsTimer | 18ms | ✅ |
| recordPublishFailure_incrementsCounter | 15ms | ✅ |
| recordArchival_incrementsCounter | 14ms | ✅ |
| recordPublishSuccess_incrementsCounter | 12ms | ✅ |

### OutboxProcessingConfigTest

**Class Total Duration:** 11ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| config_loadsDefaultPermanentFailureExceptions | 4ms | ✅ |
| config_loadsMaxPermanentRetries | 4ms | ✅ |
| config_hasExistingProcessingSettings | 3ms | ✅ |

### OutboxRoutingConfigTest

**Class Total Duration:** 4ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| getRoutingRule_handlesOptionalClusters | 2ms | ✅ |
| getRoutingRule_handlesIndexedPropertiesFromDynamicRegistry | 1ms | ✅ |
| getRoutingRule_throwsExceptionForInvalidClustersFormat | 1ms | ✅ |
| getRoutingRule_handlesStrategyWithHyphens | 0ms | ✅ |
| getRoutingRule_handlesSingleOptionalClusterAsString | 0ms | ✅ |
| getRoutingRule_handlesStrategyWithUnderscores | 0ms | ✅ |
| getRoutingRule_defaultsToAllMustSucceedWhenStrategyNotSpecified | 0ms | ✅ |
| getRoutingRule_throwsExceptionForInvalidFormat | 0ms | ✅ |
| getRoutingRule_handlesMultipleClustersWithStrategy | 0ms | ✅ |
| getRoutingRule_handlesIndexedOptionalProperties | 0ms | ✅ |
| getRoutingRule_returnsNullForUnknownEventType | 0ms | ✅ |
| getRoutingRule_handlesSingleClusterAsString | 0ms | ✅ |
| getRoutingRule_handlesBackwardCompatibleStringFormat | 0ms | ✅ |

### OutboxServiceTest

**Class Total Duration:** 237ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| findPaged_filtersWithBlankValues | 47ms | ✅ |
| findPaged_filtersWithAggregateIdAndPending | 39ms | ✅ |
| findPaged_withAllNullParameters | 34ms | ✅ |
| markUnsent_clearsSentAtAndLease | 31ms | ✅ |
| findPaged_filtersAndSorts | 26ms | ✅ |
| getPendingEvents_returnsAllUnsent | 21ms | ✅ |
| getAllEvents_returnsAllEvents | 20ms | ✅ |
| markUnsent_throwsExceptionWhenNotFound | 19ms | ✅ |

### SecurityConfigTest

**Class Total Duration:** 4ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| shouldLoadSecurityFilterChain | 2ms | ✅ |
| shouldHaveDisabledSecurityByDefault | 2ms | ✅ |

### SecurityConfigurationTest

**Class Total Duration:** 6ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| testSecureClusterConfiguration | 2ms | ✅ |
| testSaslConfigurationProperties | 2ms | ✅ |
| testSslBundleIsConfigured | 2ms | ✅ |


---

## Module: order-service

### Summary Statistics

- **Test Classes:** 6
- **Test Methods:** 21
- **Passed:** 21
- **Failed:** 0
- **Total Test Duration:** 1.73s
- **Module Execution Time:** 31.34s
- **Average Test Duration:** 82ms

### Test Details

### OrderControllerTest

**Class Total Duration:** 492ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| getAllOrders_ShouldReturnAllOrders | 248ms | ✅ |
| updateOrderStatus_WithNullStatus_ShouldReturnBadRequest | 89ms | ✅ |
| createOrder_ShouldReturnCreatedOrder | 63ms | ✅ |
| getOrderById_ShouldReturnOrder | 44ms | ✅ |
| updateOrderStatus_ShouldUpdateAndReturnOrder | 30ms | ✅ |
| updateOrderStatus_WithBlankStatus_ShouldReturnBadRequest | 18ms | ✅ |

### OrderNotFoundExceptionTest

**Class Total Duration:** 162ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| shouldCreateExceptionWithCorrectMessage | 157ms | ✅ |
| shouldBeRuntimeException | 5ms | ✅ |

### OrderServiceFailureTest

**Class Total Duration:** 248ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| testOrderCreationFailsWhenOutboxWriteFails | 248ms | ✅ |

### OrderServiceTest

**Class Total Duration:** 800ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| testGetAllOrders | 664ms | ✅ |
| testCreateOrder_CreatesOrderAndOutboxEvent | 50ms | ✅ |
| testUpdateOrderStatus_CreatesOutboxEvent | 34ms | ✅ |
| testGetOrderById_Success | 27ms | ✅ |
| testGetOrderById_ThrowsExceptionWhenNotFound | 25ms | ✅ |

### OrderTest

**Class Total Duration:** 8ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| onCreate_shouldNotOverrideExistingStatus | 3ms | ✅ |
| shouldCreateOrderWithConstructor | 2ms | ✅ |
| onCreate_shouldSetDefaultStatus | 2ms | ✅ |
| shouldSupportSettersAndGetters | 1ms | ✅ |

### UpdateStatusRequestTest

**Class Total Duration:** 22ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| shouldSupportEquality | 18ms | ✅ |
| shouldCreateRequestWithStatus | 2ms | ✅ |
| shouldHandleNullStatus | 2ms | ✅ |


---

## Module: catbox-archunit

### Summary Statistics

- **Test Classes:** 6
- **Test Methods:** 32
- **Passed:** 32
- **Failed:** 0
- **Total Test Duration:** 198ms
- **Module Execution Time:** 3.71s
- **Average Test Duration:** 6ms

### Test Details

### EntityRepositoryPatternTest

**Class Total Duration:** 103ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| tableAnnotationShouldBeUsedForEntities | 68ms | ✅ |
| entitiesShouldNotHavePublicFields | 22ms | ✅ |
| repositoriesShouldBeInterfaces | 7ms | ✅ |
| entitiesIdFieldsShouldBeAnnotatedWithId | 4ms | ✅ |
| entitiesShouldResideInEntityPackage | 2ms | ✅ |

### LayeringArchitectureTest

**Class Total Duration:** 24ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| repositoriesShouldNotAccessServicesOrControllers | 19ms | ✅ |
| servicesShouldNotAccessControllers | 4ms | ✅ |
| controllersShouldNotAccessRepositoriesDirectly | 1ms | ✅ |

### NamingConventionTest

**Class Total Duration:** 11ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| servicesShouldBeSuffixed | 3ms | ✅ |
| dtosShouldResideInDtoPackage | 2ms | ✅ |
| exceptionsShouldBeSuffixed | 2ms | ✅ |
| repositoriesShouldBeSuffixed | 1ms | ✅ |
| configurationsShouldBeSuffixed | 1ms | ✅ |
| entitiesShouldResideInEntityPackage | 1ms | ✅ |
| controllersShouldBeSuffixed | 1ms | ✅ |

### PackageDependencyTest

**Class Total Duration:** 26ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| commonModuleShouldNotDependOnOtherModules | 11ms | ✅ |
| clientModuleShouldNotDependOnServerOrOrderService | 6ms | ✅ |
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
| servicesShouldBeAnnotatedWithService | 2ms | ✅ |
| repositoriesShouldExtendSpringDataRepository | 2ms | ✅ |
| configurationClassesShouldBeAnnotatedWithConfiguration | 2ms | ✅ |
| serviceMethodsShouldNotBePublicUnlessNecessary | 1ms | ✅ |
| entitiesShouldBeAnnotatedWithEntity | 1ms | ✅ |

### TransactionBoundaryTest

**Class Total Duration:** 22ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| repositoriesShouldNotDefineTransactions | 9ms | ✅ |
| serviceClassesShouldBeAnnotatedWithTransactional | 8ms | ✅ |
| serviceMethodsModifyingDataShouldBeTransactional | 3ms | ✅ |
| controllersShouldNotBeTransactional | 2ms | ✅ |


---

## Module: catbox-server

### Summary Statistics

- **Test Classes:** 23
- **Test Methods:** 108
- **Passed:** 105
- **Failed:** 3
- **Total Test Duration:** 15.19s
- **Module Execution Time:** 2m 32s
- **Average Test Duration:** 140ms

### Test Details

### AdminControllerTest

**Class Total Duration:** 680ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| adminPage_WithPendingOnlyFilter_ShouldReturnOnlyPending | 458ms | ✅ |
| adminPage_WithPagination_ShouldReturnPagedResults | 89ms | ✅ |
| adminPage_WithSorting_ShouldReturnSortedResults | 47ms | ✅ |
| adminPage_WithFilters_ShouldReturnFilteredResults | 34ms | ✅ |
| adminPage_WithDefaultParameters_ShouldUseDefaults | 26ms | ✅ |
| adminPage_ShouldReturnAdminView | 26ms | ✅ |

### CatboxApplicationTests

**Class Total Duration:** 4ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| contextLoads | 4ms | ✅ |

### CorrelationIdTest

**Class Total Duration:** 136ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| publishEvent_withCorrelationId_sendsCorrelationIdInKafkaHeader | 70ms | ✅ |
| publishEvent_withoutCorrelationId_sendsWithoutHeader | 37ms | ✅ |
| createEvent_withCorrelationId_storesInDatabase | 29ms | ✅ |

### DynamicKafkaTemplateFactoryEvictionTest

**Class Total Duration:** 1.04s

| Test Method | Duration | Status |
|-------------|----------|--------|
| testConcurrentAccessDoesNotCauseExceptions | 1.01s | ✅ |
| testBeanNamingConvention | 12ms | ✅ |
| testMultipleEvictionCallsDontCauseIssues | 5ms | ✅ |
| testTemplateCreationAndCaching | 4ms | ✅ |
| testEvictionHandlesEmptyCache | 4ms | ✅ |
| testTemplateReCreationAfterManualRemoval | 4ms | ✅ |

### DynamicKafkaTemplateFactoryProxyTest

**Class Total Duration:** 113ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| testMultipleCallsDoNotDuplicateBeans | 56ms | ✅ |
| testTemplateCanBeRetrievedDirectlyFromContext | 26ms | ✅ |
| testDynamicallyCreatedTemplateHasSpringLifecycle | 9ms | ✅ |
| testKafkaTemplateIsSpringManagedBean | 8ms | ✅ |
| testKafkaTemplateIsSingleton | 8ms | ✅ |
| testProducerFactoryIsSpringManagedBean | 6ms | ✅ |

### DynamicKafkaTemplateFactorySslBundlePositiveTest

**Class Total Duration:** 79ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| testClusterWithSslBundleHasSslConfiguration | 79ms | ✅ |

### DynamicKafkaTemplateFactorySslBundleTest

**Class Total Duration:** 32ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| testClusterWithoutSslBundleDoesNotHaveSslConfig | 20ms | ✅ |
| testMissingClusterThrowsException | 12ms | ✅ |

### E2EPollerMultiClusterTest

**Class Total Duration:** 6.61s

| Test Method | Duration | Status |
|-------------|----------|--------|
| testPollerRoutesEventsToCorrectClusters | 6.61s | ✅ |

### E2EPollerTest

**Class Total Duration:** 2.68s

| Test Method | Duration | Status |
|-------------|----------|--------|
| testPollerClaimsAndPublishesEvent | 2.68s | ✅ |

### OutboxArchivalServiceTest

**Class Total Duration:** 566ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| archiveOldEvents_movesOldSentEventsToArchive | 117ms | ✅ |
| archiveOldEvents_preservesKafkaMetadata | 107ms | ✅ |
| archiveOldEvents_preservesAllEventData | 67ms | ✅ |
| manualArchive_returnsZeroWhenNoEventsToArchive | 67ms | ✅ |
| manualArchive_archivesWithCustomRetention | 63ms | ✅ |
| manualArchive_returnsZeroForInvalidRetention | 55ms | ✅ |
| archiveOldEvents_doesNothingWhenNoOldEvents | 53ms | ✅ |
| manualArchive_returnsZeroForNegativeRetention | 37ms | ✅ |

### OutboxControllerTest

**Class Total Duration:** 412ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| markOutboxUnsent_ShouldMarkEventAsUnsent | 167ms | ✅ |
| getPendingOutboxEvents_ShouldReturnOnlyPending | 147ms | ✅ |
| searchOutbox_WithFilters_ShouldReturnFilteredResults | 70ms | ✅ |
| getAllOutboxEvents_ShouldReturnAllEvents | 28ms | ✅ |

### OutboxEventClaimTest

**Class Total Duration:** 106ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| testClaimPendingEvents_claimsExpiredInProgressEvents | 30ms | ✅ |
| testClaimPendingEvents_claimsEventsSuccessfully | 19ms | ✅ |
| testClaimPendingEvents_respectsBatchSize | 16ms | ✅ |
| testClaimPendingEvents_skipsAlreadySentEvents | 15ms | ✅ |
| testClaimPendingEvents_skipsEventsInProgress | 14ms | ✅ |
| testClaimPendingEvents_ordersEventsByCreatedAt | 12ms | ✅ |

### OutboxEventClaimerConcurrencyTest

**Class Total Duration:** 778ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| testConcurrentClaimersDoNotProcessSameEvents | 778ms | ✅ |

### OutboxEventPublisherMultiClusterTest

**Class Total Duration:** 215ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| allMustSucceed_successWhenAllClustersSucceed | 55ms | ✅ |
| atLeastOne_successWhenOnlyOneClusterSucceeds | 41ms | ✅ |
| atLeastOne_failsWhenAllClustersFail | 35ms | ✅ |
| optionalClusters_failsWhenRequiredClusterFails | 32ms | ✅ |
| optionalClusters_successWhenRequiredSucceedsOptionalFails | 29ms | ✅ |
| allMustSucceed_failsWhenOneClusterFails | 23ms | ✅ |

### OutboxEventPublisherTest

**Class Total Duration:** 748ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| publishEvent_resetsFailureCountOnSuccess | 341ms | ✅ |
| publishEvent_movesToDeadLetterAfterMaxPermanentRetries | 176ms | ✅ |
| publishEvent_successfullySendsAndMarksSent | 67ms | ✅ |
| publishEvent_handlesPermanentFailure_noRoute | 63ms | ✅ |
| publishEvent_handlesPermanentFailure_kafkaException | 52ms | ✅ |
| publishEvent_capturesKafkaMetadata | 49ms | ✅ |

### OutboxFailureHandlerTest

**Class Total Duration:** 259ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| recordPermanentFailure_movesToDeadLetterAfterMaxRetries | 80ms | ✅ |
| recordPermanentFailure_handlesMultipleEvents | 57ms | ✅ |
| recordPermanentFailure_incrementsFailureCount | 52ms | ✅ |
| resetFailureCount_clearsFailureData | 27ms | ✅ |
| recordPermanentFailure_throwsExceptionWhenEventNotFound | 24ms | ✅ |
| resetFailureCount_doesNothingWhenCountIsZero | 19ms | ✅ |

### OutboxMetricsServiceTest

**Class Total Duration:** 471ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| updateArchivalMetrics_countsDeadLetterCorrectly | 93ms | ✅ |
| recordDeadLetter_incrementsCounter | 85ms | ✅ |
| updatePendingEventsMetrics_calculatesOldestAge | 65ms | ✅ |
| updateArchivalMetrics_countsCorrectly | 43ms | ✅ |
| updatePendingEventsMetrics_whenNoEvents_setsToZero | 35ms | ✅ |
| updatePendingEventsMetrics_countsCorrectly | 33ms | ✅ |
| updateArchivalMetrics_whenNoEvents_setsToZero | 29ms | ✅ |
| metricsAreRegistered | 21ms | ✅ |
| recordProcessingDuration_recordsTimer | 20ms | ✅ |
| recordPublishFailure_incrementsCounter | 18ms | ✅ |
| recordArchival_incrementsCounter | 15ms | ✅ |
| recordPublishSuccess_incrementsCounter | 14ms | ✅ |

### OutboxProcessingConfigTest

**Class Total Duration:** 11ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| config_loadsDefaultPermanentFailureExceptions | 4ms | ✅ |
| config_loadsMaxPermanentRetries | 4ms | ✅ |
| config_hasExistingProcessingSettings | 3ms | ✅ |

### OutboxRoutingConfigTest

**Class Total Duration:** 7ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| getRoutingRule_handlesOptionalClusters | 4ms | ✅ |
| getRoutingRule_throwsExceptionForInvalidFormat | 1ms | ✅ |
| getRoutingRule_handlesIndexedPropertiesFromDynamicRegistry | 1ms | ✅ |
| getRoutingRule_handlesIndexedOptionalProperties | 1ms | ✅ |
| getRoutingRule_handlesStrategyWithHyphens | 0ms | ✅ |
| getRoutingRule_handlesSingleOptionalClusterAsString | 0ms | ✅ |
| getRoutingRule_handlesStrategyWithUnderscores | 0ms | ✅ |
| getRoutingRule_defaultsToAllMustSucceedWhenStrategyNotSpecified | 0ms | ✅ |
| getRoutingRule_handlesMultipleClustersWithStrategy | 0ms | ✅ |
| getRoutingRule_throwsExceptionForInvalidClustersFormat | 0ms | ✅ |
| getRoutingRule_returnsNullForUnknownEventType | 0ms | ✅ |
| getRoutingRule_handlesSingleClusterAsString | 0ms | ✅ |
| getRoutingRule_handlesBackwardCompatibleStringFormat | 0ms | ✅ |

### OutboxServiceTest

**Class Total Duration:** 225ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| findPaged_filtersWithAggregateIdAndPending | 53ms | ✅ |
| findPaged_filtersAndSorts | 30ms | ✅ |
| findPaged_filtersWithBlankValues | 30ms | ✅ |
| findPaged_withAllNullParameters | 26ms | ✅ |
| markUnsent_throwsExceptionWhenNotFound | 25ms | ✅ |
| markUnsent_clearsSentAtAndLease | 22ms | ✅ |
| getPendingEvents_returnsAllUnsent | 21ms | ✅ |
| getAllEvents_returnsAllEvents | 18ms | ✅ |

### SecurityConfigTest

**Class Total Duration:** 5ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| shouldHaveDisabledSecurityByDefault | 3ms | ✅ |
| shouldLoadSecurityFilterChain | 2ms | ✅ |

### SecurityConfigurationTest

**Class Total Duration:** 7ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| testSecureClusterConfiguration | 3ms | ✅ |
| testSaslConfigurationProperties | 2ms | ✅ |
| testSslBundleIsConfigured | 2ms | ✅ |

### TracingConfigurationTest

**Class Total Duration:** 6ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| tracerBeanShouldBeAvailable | 5ms | ❌ |
| tracerShouldCreateSpans | 1ms | ❌ |
| tracerShouldPropagateContext | 0ms | ❌ |


---

## Module: catbox-server

### Summary Statistics

- **Test Classes:** 23
- **Test Methods:** 108
- **Passed:** 108
- **Failed:** 0
- **Total Test Duration:** 15.76s
- **Module Execution Time:** 2m 30s
- **Average Test Duration:** 145ms

### Test Details

### AdminControllerTest

**Class Total Duration:** 849ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| adminPage_WithPendingOnlyFilter_ShouldReturnOnlyPending | 603ms | ✅ |
| adminPage_WithPagination_ShouldReturnPagedResults | 102ms | ✅ |
| adminPage_WithSorting_ShouldReturnSortedResults | 43ms | ✅ |
| adminPage_WithFilters_ShouldReturnFilteredResults | 39ms | ✅ |
| adminPage_ShouldReturnAdminView | 39ms | ✅ |
| adminPage_WithDefaultParameters_ShouldUseDefaults | 23ms | ✅ |

### CatboxApplicationTests

**Class Total Duration:** 5ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| contextLoads | 5ms | ✅ |

### CorrelationIdTest

**Class Total Duration:** 147ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| publishEvent_withCorrelationId_sendsCorrelationIdInKafkaHeader | 66ms | ✅ |
| publishEvent_withoutCorrelationId_sendsWithoutHeader | 52ms | ✅ |
| createEvent_withCorrelationId_storesInDatabase | 29ms | ✅ |

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

**Class Total Duration:** 114ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| testMultipleCallsDoNotDuplicateBeans | 57ms | ✅ |
| testTemplateCanBeRetrievedDirectlyFromContext | 25ms | ✅ |
| testDynamicallyCreatedTemplateHasSpringLifecycle | 8ms | ✅ |
| testKafkaTemplateIsSpringManagedBean | 8ms | ✅ |
| testKafkaTemplateIsSingleton | 8ms | ✅ |
| testProducerFactoryIsSpringManagedBean | 8ms | ✅ |

### DynamicKafkaTemplateFactorySslBundlePositiveTest

**Class Total Duration:** 68ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| testClusterWithSslBundleHasSslConfiguration | 68ms | ✅ |

### DynamicKafkaTemplateFactorySslBundleTest

**Class Total Duration:** 35ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| testClusterWithoutSslBundleDoesNotHaveSslConfig | 21ms | ✅ |
| testMissingClusterThrowsException | 14ms | ✅ |

### E2EPollerMultiClusterTest

**Class Total Duration:** 6.75s

| Test Method | Duration | Status |
|-------------|----------|--------|
| testPollerRoutesEventsToCorrectClusters | 6.75s | ✅ |

### E2EPollerTest

**Class Total Duration:** 2.75s

| Test Method | Duration | Status |
|-------------|----------|--------|
| testPollerClaimsAndPublishesEvent | 2.75s | ✅ |

### OutboxArchivalServiceTest

**Class Total Duration:** 556ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| archiveOldEvents_movesOldSentEventsToArchive | 114ms | ✅ |
| archiveOldEvents_preservesKafkaMetadata | 94ms | ✅ |
| manualArchive_returnsZeroForInvalidRetention | 70ms | ✅ |
| manualArchive_archivesWithCustomRetention | 65ms | ✅ |
| manualArchive_returnsZeroWhenNoEventsToArchive | 64ms | ✅ |
| archiveOldEvents_preservesAllEventData | 62ms | ✅ |
| manualArchive_returnsZeroForNegativeRetention | 47ms | ✅ |
| archiveOldEvents_doesNothingWhenNoOldEvents | 40ms | ✅ |

### OutboxControllerTest

**Class Total Duration:** 454ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| markOutboxUnsent_ShouldMarkEventAsUnsent | 202ms | ✅ |
| getPendingOutboxEvents_ShouldReturnOnlyPending | 138ms | ✅ |
| searchOutbox_WithFilters_ShouldReturnFilteredResults | 91ms | ✅ |
| getAllOutboxEvents_ShouldReturnAllEvents | 23ms | ✅ |

### OutboxEventClaimTest

**Class Total Duration:** 141ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| testClaimPendingEvents_claimsExpiredInProgressEvents | 39ms | ✅ |
| testClaimPendingEvents_ordersEventsByCreatedAt | 39ms | ✅ |
| testClaimPendingEvents_skipsAlreadySentEvents | 18ms | ✅ |
| testClaimPendingEvents_skipsEventsInProgress | 16ms | ✅ |
| testClaimPendingEvents_claimsEventsSuccessfully | 16ms | ✅ |
| testClaimPendingEvents_respectsBatchSize | 13ms | ✅ |

### OutboxEventClaimerConcurrencyTest

**Class Total Duration:** 805ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| testConcurrentClaimersDoNotProcessSameEvents | 805ms | ✅ |

### OutboxEventPublisherMultiClusterTest

**Class Total Duration:** 229ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| allMustSucceed_successWhenAllClustersSucceed | 67ms | ✅ |
| atLeastOne_failsWhenAllClustersFail | 48ms | ✅ |
| atLeastOne_successWhenOnlyOneClusterSucceeds | 34ms | ✅ |
| optionalClusters_successWhenRequiredSucceedsOptionalFails | 28ms | ✅ |
| optionalClusters_failsWhenRequiredClusterFails | 27ms | ✅ |
| allMustSucceed_failsWhenOneClusterFails | 25ms | ✅ |

### OutboxEventPublisherTest

**Class Total Duration:** 757ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| publishEvent_resetsFailureCountOnSuccess | 342ms | ✅ |
| publishEvent_movesToDeadLetterAfterMaxPermanentRetries | 167ms | ✅ |
| publishEvent_handlesPermanentFailure_noRoute | 68ms | ✅ |
| publishEvent_capturesKafkaMetadata | 62ms | ✅ |
| publishEvent_handlesPermanentFailure_kafkaException | 59ms | ✅ |
| publishEvent_successfullySendsAndMarksSent | 59ms | ✅ |

### OutboxFailureHandlerTest

**Class Total Duration:** 322ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| recordPermanentFailure_movesToDeadLetterAfterMaxRetries | 108ms | ✅ |
| recordPermanentFailure_handlesMultipleEvents | 82ms | ✅ |
| recordPermanentFailure_incrementsFailureCount | 50ms | ✅ |
| recordPermanentFailure_throwsExceptionWhenEventNotFound | 33ms | ✅ |
| resetFailureCount_clearsFailureData | 30ms | ✅ |
| resetFailureCount_doesNothingWhenCountIsZero | 19ms | ✅ |

### OutboxMetricsServiceTest

**Class Total Duration:** 465ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| recordDeadLetter_incrementsCounter | 96ms | ✅ |
| updateArchivalMetrics_countsDeadLetterCorrectly | 93ms | ✅ |
| updateArchivalMetrics_countsCorrectly | 48ms | ✅ |
| updatePendingEventsMetrics_calculatesOldestAge | 44ms | ✅ |
| updatePendingEventsMetrics_countsCorrectly | 43ms | ✅ |
| updatePendingEventsMetrics_whenNoEvents_setsToZero | 34ms | ✅ |
| updateArchivalMetrics_whenNoEvents_setsToZero | 26ms | ✅ |
| recordProcessingDuration_recordsTimer | 18ms | ✅ |
| recordPublishFailure_incrementsCounter | 17ms | ✅ |
| recordArchival_incrementsCounter | 16ms | ✅ |
| recordPublishSuccess_incrementsCounter | 16ms | ✅ |
| metricsAreRegistered | 14ms | ✅ |

### OutboxProcessingConfigTest

**Class Total Duration:** 9ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| config_loadsDefaultPermanentFailureExceptions | 3ms | ✅ |
| config_hasExistingProcessingSettings | 3ms | ✅ |
| config_loadsMaxPermanentRetries | 3ms | ✅ |

### OutboxRoutingConfigTest

**Class Total Duration:** 6ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| getRoutingRule_handlesOptionalClusters | 3ms | ✅ |
| getRoutingRule_throwsExceptionForInvalidFormat | 1ms | ✅ |
| getRoutingRule_handlesIndexedOptionalProperties | 1ms | ✅ |
| getRoutingRule_returnsNullForUnknownEventType | 1ms | ✅ |
| getRoutingRule_handlesStrategyWithHyphens | 0ms | ✅ |
| getRoutingRule_handlesSingleOptionalClusterAsString | 0ms | ✅ |
| getRoutingRule_handlesStrategyWithUnderscores | 0ms | ✅ |
| getRoutingRule_defaultsToAllMustSucceedWhenStrategyNotSpecified | 0ms | ✅ |
| getRoutingRule_handlesMultipleClustersWithStrategy | 0ms | ✅ |
| getRoutingRule_handlesIndexedPropertiesFromDynamicRegistry | 0ms | ✅ |
| getRoutingRule_throwsExceptionForInvalidClustersFormat | 0ms | ✅ |
| getRoutingRule_handlesSingleClusterAsString | 0ms | ✅ |
| getRoutingRule_handlesBackwardCompatibleStringFormat | 0ms | ✅ |

### OutboxServiceTest

**Class Total Duration:** 239ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| findPaged_filtersWithAggregateIdAndPending | 40ms | ✅ |
| findPaged_filtersAndSorts | 34ms | ✅ |
| findPaged_filtersWithBlankValues | 33ms | ✅ |
| markUnsent_clearsSentAtAndLease | 30ms | ✅ |
| getAllEvents_returnsAllEvents | 29ms | ✅ |
| markUnsent_throwsExceptionWhenNotFound | 28ms | ✅ |
| findPaged_withAllNullParameters | 24ms | ✅ |
| getPendingEvents_returnsAllUnsent | 21ms | ✅ |

### SecurityConfigTest

**Class Total Duration:** 8ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| shouldHaveDisabledSecurityByDefault | 5ms | ✅ |
| shouldLoadSecurityFilterChain | 3ms | ✅ |

### SecurityConfigurationTest

**Class Total Duration:** 9ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| testSecureClusterConfiguration | 5ms | ✅ |
| testSaslConfigurationProperties | 2ms | ✅ |
| testSslBundleIsConfigured | 2ms | ✅ |

### TracingConfigurationTest

**Class Total Duration:** 6ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| tracerShouldPropagateContext | 2ms | ✅ |
| tracerBeanShouldBeAvailable | 2ms | ✅ |
| tracerShouldCreateSpans | 2ms | ✅ |


---

## Module: order-service

### Summary Statistics

- **Test Classes:** 7
- **Test Methods:** 24
- **Passed:** 24
- **Failed:** 0
- **Total Test Duration:** 1.72s
- **Module Execution Time:** 32.99s
- **Average Test Duration:** 71ms

### Test Details

### OrderControllerTest

**Class Total Duration:** 435ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| getAllOrders_ShouldReturnAllOrders | 221ms | ✅ |
| updateOrderStatus_WithNullStatus_ShouldReturnBadRequest | 90ms | ✅ |
| getOrderById_ShouldReturnOrder | 40ms | ✅ |
| updateOrderStatus_ShouldUpdateAndReturnOrder | 35ms | ✅ |
| createOrder_ShouldReturnCreatedOrder | 33ms | ✅ |
| updateOrderStatus_WithBlankStatus_ShouldReturnBadRequest | 16ms | ✅ |

### OrderNotFoundExceptionTest

**Class Total Duration:** 161ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| shouldCreateExceptionWithCorrectMessage | 155ms | ✅ |
| shouldBeRuntimeException | 6ms | ✅ |

### OrderServiceFailureTest

**Class Total Duration:** 231ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| testOrderCreationFailsWhenOutboxWriteFails | 231ms | ✅ |

### OrderServiceTest

**Class Total Duration:** 844ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| testGetAllOrders | 700ms | ✅ |
| testCreateOrder_CreatesOrderAndOutboxEvent | 61ms | ✅ |
| testUpdateOrderStatus_CreatesOutboxEvent | 34ms | ✅ |
| testGetOrderById_Success | 26ms | ✅ |
| testGetOrderById_ThrowsExceptionWhenNotFound | 23ms | ✅ |

### OrderTest

**Class Total Duration:** 7ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| onCreate_shouldSetDefaultStatus | 4ms | ✅ |
| onCreate_shouldNotOverrideExistingStatus | 1ms | ✅ |
| shouldCreateOrderWithConstructor | 1ms | ✅ |
| shouldSupportSettersAndGetters | 1ms | ✅ |

### TracingConfigurationTest

**Class Total Duration:** 28ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| tracerBeanShouldBeAvailable | 15ms | ✅ |
| tracerShouldCreateSpans | 7ms | ✅ |
| tracerShouldPropagateContext | 6ms | ✅ |

### UpdateStatusRequestTest

**Class Total Duration:** 17ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| shouldSupportEquality | 14ms | ✅ |
| shouldCreateRequestWithStatus | 2ms | ✅ |
| shouldHandleNullStatus | 1ms | ✅ |


---

## Module: catbox-archunit

### Summary Statistics

- **Test Classes:** 6
- **Test Methods:** 32
- **Passed:** 32
- **Failed:** 0
- **Total Test Duration:** 184ms
- **Module Execution Time:** 3.87s
- **Average Test Duration:** 5ms

### Test Details

### EntityRepositoryPatternTest

**Class Total Duration:** 86ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| tableAnnotationShouldBeUsedForEntities | 53ms | ✅ |
| entitiesShouldNotHavePublicFields | 19ms | ✅ |
| repositoriesShouldBeInterfaces | 7ms | ✅ |
| entitiesIdFieldsShouldBeAnnotatedWithId | 5ms | ✅ |
| entitiesShouldResideInEntityPackage | 2ms | ✅ |

### LayeringArchitectureTest

**Class Total Duration:** 27ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| repositoriesShouldNotAccessServicesOrControllers | 20ms | ✅ |
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

**Class Total Duration:** 26ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| commonModuleShouldNotDependOnOtherModules | 11ms | ✅ |
| clientModuleShouldNotDependOnServerOrOrderService | 6ms | ✅ |
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
| repositoriesShouldExtendSpringDataRepository | 2ms | ✅ |
| configurationClassesShouldBeAnnotatedWithConfiguration | 2ms | ✅ |
| serviceMethodsShouldNotBePublicUnlessNecessary | 1ms | ✅ |
| servicesShouldBeAnnotatedWithService | 1ms | ✅ |
| entitiesShouldBeAnnotatedWithEntity | 1ms | ✅ |

### TransactionBoundaryTest

**Class Total Duration:** 22ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| repositoriesShouldNotDefineTransactions | 9ms | ✅ |
| serviceClassesShouldBeAnnotatedWithTransactional | 8ms | ✅ |
| serviceMethodsModifyingDataShouldBeTransactional | 3ms | ✅ |
| controllersShouldNotBeTransactional | 2ms | ✅ |


---

