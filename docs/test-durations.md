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

