# Test Duration Report

Generated: 2025-11-03T15:14:16.243600473Z

This report aggregates test durations across all modules in the build.

---

## Module: routebox-server

### Summary Statistics

- **Test Classes:** 20
- **Test Methods:** 77
- **Passed:** 77
- **Failed:** 0
- **Total Test Duration:** 13.85s
- **Module Execution Time:** 2m 42s
- **Average Test Duration:** 179ms

### Test Details

### RouteBoxApplicationTests

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

## Module: routebox-archunit

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
| orderServiceShouldNotDependOnRouteBoxServer | 1ms | ✅ |

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

## Module: routebox-server

### Summary Statistics

- **Test Classes:** 20
- **Test Methods:** 77
- **Passed:** 77
- **Failed:** 0
- **Total Test Duration:** 13.86s
- **Module Execution Time:** 2m 6s
- **Average Test Duration:** 179ms

### Test Details

### RouteBoxApplicationTests

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

## Module: routebox-server

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

## Module: routebox-archunit

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
| orderServiceShouldNotDependOnRouteBoxServer | 1ms | ✅ |

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

## Module: routebox-server

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

### RouteBoxApplicationTests

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

## Module: routebox-archunit

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
| orderServiceShouldNotDependOnRouteBoxServer | 1ms | ✅ |

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

## Module: routebox-server

### Summary Statistics

- **Test Classes:** 22
- **Test Methods:** 105
- **Passed:** 105
- **Failed:** 0
- **Total Test Duration:** 15.51s
- **Module Execution Time:** 2m 54s
- **Average Test Duration:** 147ms

### Test Details

### AdminControllerTest

**Class Total Duration:** 778ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| adminPage_WithPendingOnlyFilter_ShouldReturnOnlyPending | 532ms | ✅ |
| adminPage_WithPagination_ShouldReturnPagedResults | 95ms | ✅ |
| adminPage_WithSorting_ShouldReturnSortedResults | 50ms | ✅ |
| adminPage_WithFilters_ShouldReturnFilteredResults | 45ms | ✅ |
| adminPage_ShouldReturnAdminView | 33ms | ✅ |
| adminPage_WithDefaultParameters_ShouldUseDefaults | 23ms | ✅ |

### RouteBoxApplicationTests

**Class Total Duration:** 4ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| contextLoads | 4ms | ✅ |

### CorrelationIdTest

**Class Total Duration:** 146ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| publishEvent_withCorrelationId_sendsCorrelationIdInKafkaHeader | 76ms | ✅ |
| publishEvent_withoutCorrelationId_sendsWithoutHeader | 37ms | ✅ |
| createEvent_withCorrelationId_storesInDatabase | 33ms | ✅ |

### DynamicKafkaTemplateFactoryEvictionTest

**Class Total Duration:** 1.05s

| Test Method | Duration | Status |
|-------------|----------|--------|
| testConcurrentAccessDoesNotCauseExceptions | 1.01s | ✅ |
| testBeanNamingConvention | 12ms | ✅ |
| testEvictionHandlesEmptyCache | 8ms | ✅ |
| testTemplateCreationAndCaching | 7ms | ✅ |
| testTemplateReCreationAfterManualRemoval | 4ms | ✅ |
| testMultipleEvictionCallsDontCauseIssues | 3ms | ✅ |

### DynamicKafkaTemplateFactoryProxyTest

**Class Total Duration:** 85ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| testMultipleCallsDoNotDuplicateBeans | 39ms | ✅ |
| testTemplateCanBeRetrievedDirectlyFromContext | 15ms | ✅ |
| testKafkaTemplateIsSpringManagedBean | 9ms | ✅ |
| testKafkaTemplateIsSingleton | 9ms | ✅ |
| testDynamicallyCreatedTemplateHasSpringLifecycle | 7ms | ✅ |
| testProducerFactoryIsSpringManagedBean | 6ms | ✅ |

### DynamicKafkaTemplateFactorySslBundlePositiveTest

**Class Total Duration:** 71ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| testClusterWithSslBundleHasSslConfiguration | 71ms | ✅ |

### DynamicKafkaTemplateFactorySslBundleTest

**Class Total Duration:** 28ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| testMissingClusterThrowsException | 15ms | ✅ |
| testClusterWithoutSslBundleDoesNotHaveSslConfig | 13ms | ✅ |

### E2EPollerMultiClusterTest

**Class Total Duration:** 6.57s

| Test Method | Duration | Status |
|-------------|----------|--------|
| testPollerRoutesEventsToCorrectClusters | 6.57s | ✅ |

### E2EPollerTest

**Class Total Duration:** 2.74s

| Test Method | Duration | Status |
|-------------|----------|--------|
| testPollerClaimsAndPublishesEvent | 2.74s | ✅ |

### OutboxArchivalServiceTest

**Class Total Duration:** 573ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| archiveOldEvents_movesOldSentEventsToArchive | 121ms | ✅ |
| archiveOldEvents_preservesKafkaMetadata | 107ms | ✅ |
| manualArchive_returnsZeroForInvalidRetention | 78ms | ✅ |
| manualArchive_archivesWithCustomRetention | 73ms | ✅ |
| manualArchive_returnsZeroWhenNoEventsToArchive | 54ms | ✅ |
| archiveOldEvents_preservesAllEventData | 53ms | ✅ |
| archiveOldEvents_doesNothingWhenNoOldEvents | 45ms | ✅ |
| manualArchive_returnsZeroForNegativeRetention | 42ms | ✅ |

### OutboxControllerTest

**Class Total Duration:** 407ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| markOutboxUnsent_ShouldMarkEventAsUnsent | 180ms | ✅ |
| getPendingOutboxEvents_ShouldReturnOnlyPending | 116ms | ✅ |
| searchOutbox_WithFilters_ShouldReturnFilteredResults | 75ms | ✅ |
| getAllOutboxEvents_ShouldReturnAllEvents | 36ms | ✅ |

### OutboxEventClaimTest

**Class Total Duration:** 158ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| testClaimPendingEvents_claimsExpiredInProgressEvents | 48ms | ✅ |
| testClaimPendingEvents_skipsAlreadySentEvents | 28ms | ✅ |
| testClaimPendingEvents_claimsEventsSuccessfully | 26ms | ✅ |
| testClaimPendingEvents_skipsEventsInProgress | 24ms | ✅ |
| testClaimPendingEvents_respectsBatchSize | 16ms | ✅ |
| testClaimPendingEvents_ordersEventsByCreatedAt | 16ms | ✅ |

### OutboxEventClaimerConcurrencyTest

**Class Total Duration:** 782ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| testConcurrentClaimersDoNotProcessSameEvents | 782ms | ✅ |

### OutboxEventPublisherMultiClusterTest

**Class Total Duration:** 292ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| allMustSucceed_successWhenAllClustersSucceed | 107ms | ✅ |
| atLeastOne_failsWhenAllClustersFail | 55ms | ✅ |
| optionalClusters_successWhenRequiredSucceedsOptionalFails | 37ms | ✅ |
| atLeastOne_successWhenOnlyOneClusterSucceeds | 36ms | ✅ |
| optionalClusters_failsWhenRequiredClusterFails | 31ms | ✅ |
| allMustSucceed_failsWhenOneClusterFails | 26ms | ✅ |

### OutboxEventPublisherTest

**Class Total Duration:** 777ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| publishEvent_resetsFailureCountOnSuccess | 385ms | ✅ |
| publishEvent_movesToDeadLetterAfterMaxPermanentRetries | 161ms | ✅ |
| publishEvent_handlesPermanentFailure_kafkaException | 63ms | ✅ |
| publishEvent_successfullySendsAndMarksSent | 62ms | ✅ |
| publishEvent_capturesKafkaMetadata | 53ms | ✅ |
| publishEvent_handlesPermanentFailure_noRoute | 53ms | ✅ |

### OutboxFailureHandlerTest

**Class Total Duration:** 265ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| recordPermanentFailure_movesToDeadLetterAfterMaxRetries | 83ms | ✅ |
| recordPermanentFailure_handlesMultipleEvents | 62ms | ✅ |
| recordPermanentFailure_incrementsFailureCount | 42ms | ✅ |
| recordPermanentFailure_throwsExceptionWhenEventNotFound | 31ms | ✅ |
| resetFailureCount_clearsFailureData | 30ms | ✅ |
| resetFailureCount_doesNothingWhenCountIsZero | 17ms | ✅ |

### OutboxMetricsServiceTest

**Class Total Duration:** 492ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| recordDeadLetter_incrementsCounter | 116ms | ✅ |
| updateArchivalMetrics_countsDeadLetterCorrectly | 94ms | ✅ |
| updatePendingEventsMetrics_calculatesOldestAge | 48ms | ✅ |
| updatePendingEventsMetrics_whenNoEvents_setsToZero | 39ms | ✅ |
| updateArchivalMetrics_whenNoEvents_setsToZero | 32ms | ✅ |
| updatePendingEventsMetrics_countsCorrectly | 30ms | ✅ |
| updateArchivalMetrics_countsCorrectly | 29ms | ✅ |
| recordProcessingDuration_recordsTimer | 28ms | ✅ |
| recordPublishSuccess_incrementsCounter | 23ms | ✅ |
| recordPublishFailure_incrementsCounter | 19ms | ✅ |
| metricsAreRegistered | 18ms | ✅ |
| recordArchival_incrementsCounter | 16ms | ✅ |

### OutboxProcessingConfigTest

**Class Total Duration:** 12ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| config_loadsMaxPermanentRetries | 5ms | ✅ |
| config_loadsDefaultPermanentFailureExceptions | 4ms | ✅ |
| config_hasExistingProcessingSettings | 3ms | ✅ |

### OutboxRoutingConfigTest

**Class Total Duration:** 3ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| getRoutingRule_handlesOptionalClusters | 2ms | ✅ |
| getRoutingRule_handlesIndexedOptionalProperties | 1ms | ✅ |
| getRoutingRule_handlesStrategyWithHyphens | 0ms | ✅ |
| getRoutingRule_handlesSingleOptionalClusterAsString | 0ms | ✅ |
| getRoutingRule_handlesStrategyWithUnderscores | 0ms | ✅ |
| getRoutingRule_defaultsToAllMustSucceedWhenStrategyNotSpecified | 0ms | ✅ |
| getRoutingRule_throwsExceptionForInvalidFormat | 0ms | ✅ |
| getRoutingRule_handlesMultipleClustersWithStrategy | 0ms | ✅ |
| getRoutingRule_handlesIndexedPropertiesFromDynamicRegistry | 0ms | ✅ |
| getRoutingRule_throwsExceptionForInvalidClustersFormat | 0ms | ✅ |
| getRoutingRule_returnsNullForUnknownEventType | 0ms | ✅ |
| getRoutingRule_handlesSingleClusterAsString | 0ms | ✅ |
| getRoutingRule_handlesBackwardCompatibleStringFormat | 0ms | ✅ |

### OutboxServiceTest

**Class Total Duration:** 270ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| findPaged_filtersWithAggregateIdAndPending | 59ms | ✅ |
| findPaged_filtersAndSorts | 40ms | ✅ |
| getAllEvents_returnsAllEvents | 36ms | ✅ |
| markUnsent_clearsSentAtAndLease | 33ms | ✅ |
| markUnsent_throwsExceptionWhenNotFound | 33ms | ✅ |
| findPaged_filtersWithBlankValues | 29ms | ✅ |
| getPendingEvents_returnsAllUnsent | 20ms | ✅ |
| findPaged_withAllNullParameters | 20ms | ✅ |

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
- **Total Test Duration:** 1.67s
- **Module Execution Time:** 26.55s
- **Average Test Duration:** 79ms

### Test Details

### OrderControllerTest

**Class Total Duration:** 489ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| getAllOrders_ShouldReturnAllOrders | 268ms | ✅ |
| updateOrderStatus_WithNullStatus_ShouldReturnBadRequest | 86ms | ✅ |
| createOrder_ShouldReturnCreatedOrder | 55ms | ✅ |
| getOrderById_ShouldReturnOrder | 39ms | ✅ |
| updateOrderStatus_ShouldUpdateAndReturnOrder | 24ms | ✅ |
| updateOrderStatus_WithBlankStatus_ShouldReturnBadRequest | 17ms | ✅ |

### OrderNotFoundExceptionTest

**Class Total Duration:** 163ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| shouldCreateExceptionWithCorrectMessage | 157ms | ✅ |
| shouldBeRuntimeException | 6ms | ✅ |

### OrderServiceFailureTest

**Class Total Duration:** 230ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| testOrderCreationFailsWhenOutboxWriteFails | 230ms | ✅ |

### OrderServiceTest

**Class Total Duration:** 755ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| testGetAllOrders | 643ms | ✅ |
| testCreateOrder_CreatesOrderAndOutboxEvent | 50ms | ✅ |
| testUpdateOrderStatus_CreatesOutboxEvent | 28ms | ✅ |
| testGetOrderById_ThrowsExceptionWhenNotFound | 18ms | ✅ |
| testGetOrderById_Success | 16ms | ✅ |

### OrderTest

**Class Total Duration:** 8ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| onCreate_shouldNotOverrideExistingStatus | 3ms | ✅ |
| shouldSupportSettersAndGetters | 2ms | ✅ |
| onCreate_shouldSetDefaultStatus | 2ms | ✅ |
| shouldCreateOrderWithConstructor | 1ms | ✅ |

### UpdateStatusRequestTest

**Class Total Duration:** 20ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| shouldSupportEquality | 18ms | ✅ |
| shouldCreateRequestWithStatus | 1ms | ✅ |
| shouldHandleNullStatus | 1ms | ✅ |


---

## Module: order-processor

### Summary Statistics

- **Test Classes:** 2
- **Test Methods:** 11
- **Passed:** 11
- **Failed:** 0
- **Total Test Duration:** 4.00s
- **Module Execution Time:** 4.60s
- **Average Test Duration:** 363ms

### Test Details

### OrderEventListenerTest

**Class Total Duration:** 2.56s

| Test Method | Duration | Status |
|-------------|----------|--------|
| testHandleOrderCreated_DuplicateMessage_SkippedAndAcknowledged | 2.42s | ✅ |
| testHandleOrderCreated_UnexpectedException_Acknowledged | 59ms | ✅ |
| testHandleOrderCreated_NullCorrelationId_ProcessedAndAcknowledged | 33ms | ✅ |
| testHandleOrderCreated_ProcessingException_NotAcknowledged | 16ms | ✅ |
| testHandleOrderCreated_NewMessage_ProcessedAndAcknowledged | 11ms | ✅ |
| testHandleOrderStatusChanged_NewMessage_ProcessedAndAcknowledged | 10ms | ✅ |
| testHandleOrderStatusChanged_DuplicateMessage_SkippedAndAcknowledged | 10ms | ✅ |

### OrderEventProcessingServiceTest

**Class Total Duration:** 1.44s

| Test Method | Duration | Status |
|-------------|----------|--------|
| testProcessOrderCreated_Success | 725ms | ✅ |
| testProcessOrderStatusChanged_Success | 533ms | ✅ |
| testCounters_TrackProcessedEvents | 133ms | ✅ |
| testResetCounters | 45ms | ✅ |


---

## Module: order-processor

### Summary Statistics

- **Test Classes:** 3
- **Test Methods:** 16
- **Passed:** 11
- **Failed:** 5
- **Total Test Duration:** 3.51s
- **Module Execution Time:** 44.77s
- **Average Test Duration:** 219ms

### Test Details

### OrderEventListenerTest

**Class Total Duration:** 2.26s

| Test Method | Duration | Status |
|-------------|----------|--------|
| testHandleOrderCreated_DuplicateMessage_SkippedAndAcknowledged | 2.11s | ✅ |
| testHandleOrderCreated_UnexpectedException_Acknowledged | 67ms | ✅ |
| testHandleOrderCreated_NullCorrelationId_ProcessedAndAcknowledged | 38ms | ✅ |
| testHandleOrderCreated_ProcessingException_NotAcknowledged | 17ms | ✅ |
| testHandleOrderCreated_NewMessage_ProcessedAndAcknowledged | 13ms | ✅ |
| testHandleOrderStatusChanged_DuplicateMessage_SkippedAndAcknowledged | 11ms | ✅ |
| testHandleOrderStatusChanged_NewMessage_ProcessedAndAcknowledged | 9ms | ✅ |

### OrderEventProcessingServiceTest

**Class Total Duration:** 1.23s

| Test Method | Duration | Status |
|-------------|----------|--------|
| testProcessOrderCreated_Success | 636ms | ✅ |
| testProcessOrderStatusChanged_Success | 496ms | ✅ |
| testCounters_TrackProcessedEvents | 79ms | ✅ |
| testResetCounters | 16ms | ✅ |

### OrderProcessorE2ETest

**Class Total Duration:** 14ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| testMixedScenario_UniqueAndDuplicateMessages | 6ms | ❌ |
| testHappyPath_SingleMessage_ProcessedSuccessfully | 3ms | ❌ |
| testDeduplication_DuplicateMessages_ProcessedOnlyOnce | 2ms | ❌ |
| testMultipleUniqueMessages_AllProcessed | 2ms | ❌ |
| testMessageWithoutCorrelationId_StillProcessed | 1ms | ❌ |


---

## Module: order-processor

### Summary Statistics

- **Test Classes:** 3
- **Test Methods:** 16
- **Passed:** 11
- **Failed:** 5
- **Total Test Duration:** 3.81s
- **Module Execution Time:** 18.64s
- **Average Test Duration:** 238ms

### Test Details

### OrderEventListenerTest

**Class Total Duration:** 2.41s

| Test Method | Duration | Status |
|-------------|----------|--------|
| testHandleOrderCreated_DuplicateMessage_SkippedAndAcknowledged | 2.25s | ✅ |
| testHandleOrderCreated_UnexpectedException_Acknowledged | 74ms | ✅ |
| testHandleOrderCreated_NullCorrelationId_ProcessedAndAcknowledged | 35ms | ✅ |
| testHandleOrderCreated_ProcessingException_NotAcknowledged | 16ms | ✅ |
| testHandleOrderCreated_NewMessage_ProcessedAndAcknowledged | 12ms | ✅ |
| testHandleOrderStatusChanged_NewMessage_ProcessedAndAcknowledged | 10ms | ✅ |
| testHandleOrderStatusChanged_DuplicateMessage_SkippedAndAcknowledged | 8ms | ✅ |

### OrderEventProcessingServiceTest

**Class Total Duration:** 1.39s

| Test Method | Duration | Status |
|-------------|----------|--------|
| testProcessOrderCreated_Success | 628ms | ✅ |
| testProcessOrderStatusChanged_Success | 592ms | ✅ |
| testCounters_TrackProcessedEvents | 132ms | ✅ |
| testResetCounters | 39ms | ✅ |

### OrderProcessorE2ETest

**Class Total Duration:** 16ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| testMixedScenario_UniqueAndDuplicateMessages | 6ms | ❌ |
| testMessageWithoutCorrelationId_StillProcessed | 3ms | ❌ |
| testHappyPath_SingleMessage_ProcessedSuccessfully | 3ms | ❌ |
| testDeduplication_DuplicateMessages_ProcessedOnlyOnce | 2ms | ❌ |
| testMultipleUniqueMessages_AllProcessed | 2ms | ❌ |


---

## Module: order-processor

### Summary Statistics

- **Test Classes:** 3
- **Test Methods:** 16
- **Passed:** 11
- **Failed:** 5
- **Total Test Duration:** 3.95s
- **Module Execution Time:** 18.41s
- **Average Test Duration:** 246ms

### Test Details

### OrderEventListenerTest

**Class Total Duration:** 2.58s

| Test Method | Duration | Status |
|-------------|----------|--------|
| testHandleOrderCreated_DuplicateMessage_SkippedAndAcknowledged | 2.43s | ✅ |
| testHandleOrderCreated_UnexpectedException_Acknowledged | 73ms | ✅ |
| testHandleOrderCreated_NullCorrelationId_ProcessedAndAcknowledged | 32ms | ✅ |
| testHandleOrderCreated_ProcessingException_NotAcknowledged | 15ms | ✅ |
| testHandleOrderCreated_NewMessage_ProcessedAndAcknowledged | 12ms | ✅ |
| testHandleOrderStatusChanged_NewMessage_ProcessedAndAcknowledged | 11ms | ✅ |
| testHandleOrderStatusChanged_DuplicateMessage_SkippedAndAcknowledged | 8ms | ✅ |

### OrderEventProcessingServiceTest

**Class Total Duration:** 1.35s

| Test Method | Duration | Status |
|-------------|----------|--------|
| testProcessOrderCreated_Success | 742ms | ✅ |
| testProcessOrderStatusChanged_Success | 509ms | ✅ |
| testCounters_TrackProcessedEvents | 66ms | ✅ |
| testResetCounters | 32ms | ✅ |

### OrderProcessorE2ETest

**Class Total Duration:** 17ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| testMixedScenario_UniqueAndDuplicateMessages | 7ms | ❌ |
| testMultipleUniqueMessages_AllProcessed | 3ms | ❌ |
| testHappyPath_SingleMessage_ProcessedSuccessfully | 3ms | ❌ |
| testDeduplication_DuplicateMessages_ProcessedOnlyOnce | 2ms | ❌ |
| testMessageWithoutCorrelationId_StillProcessed | 2ms | ❌ |


---

## Module: order-processor

### Summary Statistics

- **Test Classes:** 3
- **Test Methods:** 16
- **Passed:** 11
- **Failed:** 5
- **Total Test Duration:** 3.89s
- **Module Execution Time:** 20.03s
- **Average Test Duration:** 242ms

### Test Details

### OrderEventListenerTest

**Class Total Duration:** 2.46s

| Test Method | Duration | Status |
|-------------|----------|--------|
| testHandleOrderCreated_DuplicateMessage_SkippedAndAcknowledged | 2.30s | ✅ |
| testHandleOrderCreated_UnexpectedException_Acknowledged | 81ms | ✅ |
| testHandleOrderCreated_NullCorrelationId_ProcessedAndAcknowledged | 34ms | ✅ |
| testHandleOrderCreated_ProcessingException_NotAcknowledged | 16ms | ✅ |
| testHandleOrderCreated_NewMessage_ProcessedAndAcknowledged | 11ms | ✅ |
| testHandleOrderStatusChanged_NewMessage_ProcessedAndAcknowledged | 10ms | ✅ |
| testHandleOrderStatusChanged_DuplicateMessage_SkippedAndAcknowledged | 8ms | ✅ |

### OrderEventProcessingServiceTest

**Class Total Duration:** 1.42s

| Test Method | Duration | Status |
|-------------|----------|--------|
| testProcessOrderCreated_Success | 740ms | ✅ |
| testProcessOrderStatusChanged_Success | 509ms | ✅ |
| testCounters_TrackProcessedEvents | 150ms | ✅ |
| testResetCounters | 18ms | ✅ |

### OrderProcessorE2ETest

**Class Total Duration:** 9ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| testMixedScenario_UniqueAndDuplicateMessages | 5ms | ❌ |
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
- **Total Test Duration:** 3.79s
- **Module Execution Time:** 19.87s
- **Average Test Duration:** 236ms

### Test Details

### OrderEventListenerTest

**Class Total Duration:** 2.47s

| Test Method | Duration | Status |
|-------------|----------|--------|
| testHandleOrderCreated_DuplicateMessage_SkippedAndAcknowledged | 2.32s | ✅ |
| testHandleOrderCreated_UnexpectedException_Acknowledged | 62ms | ✅ |
| testHandleOrderCreated_NullCorrelationId_ProcessedAndAcknowledged | 36ms | ✅ |
| testHandleOrderCreated_ProcessingException_NotAcknowledged | 19ms | ✅ |
| testHandleOrderStatusChanged_NewMessage_ProcessedAndAcknowledged | 17ms | ✅ |
| testHandleOrderCreated_NewMessage_ProcessedAndAcknowledged | 12ms | ✅ |
| testHandleOrderStatusChanged_DuplicateMessage_SkippedAndAcknowledged | 11ms | ✅ |

### OrderEventProcessingServiceTest

**Class Total Duration:** 1.31s

| Test Method | Duration | Status |
|-------------|----------|--------|
| testProcessOrderCreated_Success | 660ms | ✅ |
| testProcessOrderStatusChanged_Success | 521ms | ✅ |
| testCounters_TrackProcessedEvents | 97ms | ✅ |
| testResetCounters | 30ms | ✅ |

### OrderProcessorE2ETest

**Class Total Duration:** 9ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| testMixedScenario_UniqueAndDuplicateMessages | 5ms | ❌ |
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
- **Total Test Duration:** 7.95s
- **Module Execution Time:** 24.83s
- **Average Test Duration:** 496ms

### Test Details

### OrderEventListenerTest

**Class Total Duration:** 2.19s

| Test Method | Duration | Status |
|-------------|----------|--------|
| testHandleOrderCreated_DuplicateMessage_SkippedAndAcknowledged | 2.05s | ✅ |
| testHandleOrderCreated_UnexpectedException_Acknowledged | 68ms | ✅ |
| testHandleOrderCreated_NullCorrelationId_ProcessedAndAcknowledged | 35ms | ✅ |
| testHandleOrderCreated_ProcessingException_NotAcknowledged | 15ms | ✅ |
| testHandleOrderCreated_NewMessage_ProcessedAndAcknowledged | 13ms | ✅ |
| testHandleOrderStatusChanged_NewMessage_ProcessedAndAcknowledged | 10ms | ✅ |
| testHandleOrderStatusChanged_DuplicateMessage_SkippedAndAcknowledged | 8ms | ✅ |

### OrderEventProcessingServiceTest

**Class Total Duration:** 1.37s

| Test Method | Duration | Status |
|-------------|----------|--------|
| testProcessOrderCreated_Success | 617ms | ✅ |
| testProcessOrderStatusChanged_Success | 573ms | ✅ |
| testCounters_TrackProcessedEvents | 133ms | ✅ |
| testResetCounters | 48ms | ✅ |

### OrderProcessorE2ETest

**Class Total Duration:** 4.38s

| Test Method | Duration | Status |
|-------------|----------|--------|
| testDeduplication_DuplicateMessages_ProcessedOnlyOnce | 2.12s | ✅ |
| testMixedScenario_UniqueAndDuplicateMessages | 1.69s | ✅ |
| testMultipleUniqueMessages_AllProcessed | 328ms | ✅ |
| testHappyPath_SingleMessage_ProcessedSuccessfully | 124ms | ✅ |
| testMessageWithoutCorrelationId_StillProcessed | 118ms | ✅ |


---

## Module: routebox-server

### Summary Statistics

- **Test Classes:** 22
- **Test Methods:** 105
- **Passed:** 105
- **Failed:** 0
- **Total Test Duration:** 14.74s
- **Module Execution Time:** 2m 17s
- **Average Test Duration:** 140ms

### Test Details

### AdminControllerTest

**Class Total Duration:** 630ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| adminPage_WithPendingOnlyFilter_ShouldReturnOnlyPending | 447ms | ✅ |
| adminPage_WithPagination_ShouldReturnPagedResults | 84ms | ✅ |
| adminPage_WithFilters_ShouldReturnFilteredResults | 29ms | ✅ |
| adminPage_WithSorting_ShouldReturnSortedResults | 29ms | ✅ |
| adminPage_ShouldReturnAdminView | 23ms | ✅ |
| adminPage_WithDefaultParameters_ShouldUseDefaults | 18ms | ✅ |

### RouteBoxApplicationTests

**Class Total Duration:** 5ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| contextLoads | 5ms | ✅ |

### CorrelationIdTest

**Class Total Duration:** 135ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| publishEvent_withCorrelationId_sendsCorrelationIdInKafkaHeader | 58ms | ✅ |
| publishEvent_withoutCorrelationId_sendsWithoutHeader | 54ms | ✅ |
| createEvent_withCorrelationId_storesInDatabase | 23ms | ✅ |

### DynamicKafkaTemplateFactoryEvictionTest

**Class Total Duration:** 1.03s

| Test Method | Duration | Status |
|-------------|----------|--------|
| testConcurrentAccessDoesNotCauseExceptions | 1.01s | ✅ |
| testBeanNamingConvention | 9ms | ✅ |
| testTemplateCreationAndCaching | 4ms | ✅ |
| testTemplateReCreationAfterManualRemoval | 4ms | ✅ |
| testEvictionHandlesEmptyCache | 3ms | ✅ |
| testMultipleEvictionCallsDontCauseIssues | 3ms | ✅ |

### DynamicKafkaTemplateFactoryProxyTest

**Class Total Duration:** 89ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| testMultipleCallsDoNotDuplicateBeans | 47ms | ✅ |
| testTemplateCanBeRetrievedDirectlyFromContext | 14ms | ✅ |
| testDynamicallyCreatedTemplateHasSpringLifecycle | 7ms | ✅ |
| testKafkaTemplateIsSpringManagedBean | 7ms | ✅ |
| testKafkaTemplateIsSingleton | 7ms | ✅ |
| testProducerFactoryIsSpringManagedBean | 7ms | ✅ |

### DynamicKafkaTemplateFactorySslBundlePositiveTest

**Class Total Duration:** 58ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| testClusterWithSslBundleHasSslConfiguration | 58ms | ✅ |

### DynamicKafkaTemplateFactorySslBundleTest

**Class Total Duration:** 21ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| testMissingClusterThrowsException | 11ms | ✅ |
| testClusterWithoutSslBundleDoesNotHaveSslConfig | 10ms | ✅ |

### E2EPollerMultiClusterTest

**Class Total Duration:** 6.57s

| Test Method | Duration | Status |
|-------------|----------|--------|
| testPollerRoutesEventsToCorrectClusters | 6.57s | ✅ |

### E2EPollerTest

**Class Total Duration:** 2.64s

| Test Method | Duration | Status |
|-------------|----------|--------|
| testPollerClaimsAndPublishesEvent | 2.64s | ✅ |

### OutboxArchivalServiceTest

**Class Total Duration:** 476ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| archiveOldEvents_preservesKafkaMetadata | 123ms | ✅ |
| archiveOldEvents_movesOldSentEventsToArchive | 90ms | ✅ |
| manualArchive_returnsZeroWhenNoEventsToArchive | 59ms | ✅ |
| manualArchive_returnsZeroForInvalidRetention | 57ms | ✅ |
| archiveOldEvents_preservesAllEventData | 45ms | ✅ |
| manualArchive_archivesWithCustomRetention | 45ms | ✅ |
| manualArchive_returnsZeroForNegativeRetention | 30ms | ✅ |
| archiveOldEvents_doesNothingWhenNoOldEvents | 27ms | ✅ |

### OutboxControllerTest

**Class Total Duration:** 422ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| markOutboxUnsent_ShouldMarkEventAsUnsent | 190ms | ✅ |
| getPendingOutboxEvents_ShouldReturnOnlyPending | 131ms | ✅ |
| searchOutbox_WithFilters_ShouldReturnFilteredResults | 80ms | ✅ |
| getAllOutboxEvents_ShouldReturnAllEvents | 21ms | ✅ |

### OutboxEventClaimTest

**Class Total Duration:** 96ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| testClaimPendingEvents_claimsExpiredInProgressEvents | 31ms | ✅ |
| testClaimPendingEvents_respectsBatchSize | 15ms | ✅ |
| testClaimPendingEvents_skipsEventsInProgress | 13ms | ✅ |
| testClaimPendingEvents_claimsEventsSuccessfully | 13ms | ✅ |
| testClaimPendingEvents_skipsAlreadySentEvents | 13ms | ✅ |
| testClaimPendingEvents_ordersEventsByCreatedAt | 11ms | ✅ |

### OutboxEventClaimerConcurrencyTest

**Class Total Duration:** 723ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| testConcurrentClaimersDoNotProcessSameEvents | 723ms | ✅ |

### OutboxEventPublisherMultiClusterTest

**Class Total Duration:** 219ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| allMustSucceed_successWhenAllClustersSucceed | 58ms | ✅ |
| optionalClusters_failsWhenRequiredClusterFails | 36ms | ✅ |
| atLeastOne_failsWhenAllClustersFail | 35ms | ✅ |
| optionalClusters_successWhenRequiredSucceedsOptionalFails | 31ms | ✅ |
| atLeastOne_successWhenOnlyOneClusterSucceeds | 30ms | ✅ |
| allMustSucceed_failsWhenOneClusterFails | 29ms | ✅ |

### OutboxEventPublisherTest

**Class Total Duration:** 687ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| publishEvent_resetsFailureCountOnSuccess | 358ms | ✅ |
| publishEvent_movesToDeadLetterAfterMaxPermanentRetries | 137ms | ✅ |
| publishEvent_handlesPermanentFailure_kafkaException | 52ms | ✅ |
| publishEvent_capturesKafkaMetadata | 48ms | ✅ |
| publishEvent_successfullySendsAndMarksSent | 47ms | ✅ |
| publishEvent_handlesPermanentFailure_noRoute | 45ms | ✅ |

### OutboxFailureHandlerTest

**Class Total Duration:** 248ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| recordPermanentFailure_movesToDeadLetterAfterMaxRetries | 83ms | ✅ |
| recordPermanentFailure_handlesMultipleEvents | 58ms | ✅ |
| recordPermanentFailure_incrementsFailureCount | 37ms | ✅ |
| recordPermanentFailure_throwsExceptionWhenEventNotFound | 28ms | ✅ |
| resetFailureCount_clearsFailureData | 28ms | ✅ |
| resetFailureCount_doesNothingWhenCountIsZero | 14ms | ✅ |

### OutboxMetricsServiceTest

**Class Total Duration:** 414ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| recordDeadLetter_incrementsCounter | 85ms | ✅ |
| updateArchivalMetrics_countsDeadLetterCorrectly | 84ms | ✅ |
| updatePendingEventsMetrics_calculatesOldestAge | 56ms | ✅ |
| updatePendingEventsMetrics_whenNoEvents_setsToZero | 30ms | ✅ |
| updatePendingEventsMetrics_countsCorrectly | 28ms | ✅ |
| updateArchivalMetrics_countsCorrectly | 27ms | ✅ |
| updateArchivalMetrics_whenNoEvents_setsToZero | 23ms | ✅ |
| recordPublishSuccess_incrementsCounter | 20ms | ✅ |
| recordPublishFailure_incrementsCounter | 17ms | ✅ |
| recordProcessingDuration_recordsTimer | 16ms | ✅ |
| recordArchival_incrementsCounter | 14ms | ✅ |
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
| getRoutingRule_handlesOptionalClusters | 2ms | ✅ |
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

**Class Total Duration:** 248ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| findPaged_filtersWithAggregateIdAndPending | 63ms | ✅ |
| findPaged_filtersAndSorts | 37ms | ✅ |
| markUnsent_clearsSentAtAndLease | 30ms | ✅ |
| markUnsent_throwsExceptionWhenNotFound | 29ms | ✅ |
| findPaged_filtersWithBlankValues | 26ms | ✅ |
| getAllEvents_returnsAllEvents | 22ms | ✅ |
| getPendingEvents_returnsAllUnsent | 21ms | ✅ |
| findPaged_withAllNullParameters | 20ms | ✅ |

### SecurityConfigTest

**Class Total Duration:** 4ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| shouldLoadSecurityFilterChain | 2ms | ✅ |
| shouldHaveDisabledSecurityByDefault | 2ms | ✅ |

### SecurityConfigurationTest

**Class Total Duration:** 7ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| testSecureClusterConfiguration | 3ms | ✅ |
| testSaslConfigurationProperties | 2ms | ✅ |
| testSslBundleIsConfigured | 2ms | ✅ |


---

## Module: order-service

### Summary Statistics

- **Test Classes:** 6
- **Test Methods:** 21
- **Passed:** 21
- **Failed:** 0
- **Total Test Duration:** 1.67s
- **Module Execution Time:** 25.66s
- **Average Test Duration:** 79ms

### Test Details

### OrderControllerTest

**Class Total Duration:** 471ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| getAllOrders_ShouldReturnAllOrders | 237ms | ✅ |
| updateOrderStatus_WithNullStatus_ShouldReturnBadRequest | 89ms | ✅ |
| createOrder_ShouldReturnCreatedOrder | 60ms | ✅ |
| getOrderById_ShouldReturnOrder | 40ms | ✅ |
| updateOrderStatus_ShouldUpdateAndReturnOrder | 24ms | ✅ |
| updateOrderStatus_WithBlankStatus_ShouldReturnBadRequest | 21ms | ✅ |

### OrderNotFoundExceptionTest

**Class Total Duration:** 183ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| shouldCreateExceptionWithCorrectMessage | 178ms | ✅ |
| shouldBeRuntimeException | 5ms | ✅ |

### OrderServiceFailureTest

**Class Total Duration:** 228ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| testOrderCreationFailsWhenOutboxWriteFails | 228ms | ✅ |

### OrderServiceTest

**Class Total Duration:** 760ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| testGetAllOrders | 645ms | ✅ |
| testCreateOrder_CreatesOrderAndOutboxEvent | 49ms | ✅ |
| testUpdateOrderStatus_CreatesOutboxEvent | 31ms | ✅ |
| testGetOrderById_ThrowsExceptionWhenNotFound | 18ms | ✅ |
| testGetOrderById_Success | 17ms | ✅ |

### OrderTest

**Class Total Duration:** 10ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| shouldCreateOrderWithConstructor | 4ms | ✅ |
| onCreate_shouldNotOverrideExistingStatus | 3ms | ✅ |
| onCreate_shouldSetDefaultStatus | 2ms | ✅ |
| shouldSupportSettersAndGetters | 1ms | ✅ |

### UpdateStatusRequestTest

**Class Total Duration:** 22ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| shouldSupportEquality | 19ms | ✅ |
| shouldCreateRequestWithStatus | 2ms | ✅ |
| shouldHandleNullStatus | 1ms | ✅ |


---

## Module: order-processor

### Summary Statistics

- **Test Classes:** 3
- **Test Methods:** 16
- **Passed:** 15
- **Failed:** 1
- **Total Test Duration:** 17.81s
- **Module Execution Time:** 34.93s
- **Average Test Duration:** 1.11s

### Test Details

### OrderEventListenerTest

**Class Total Duration:** 2.16s

| Test Method | Duration | Status |
|-------------|----------|--------|
| testHandleOrderCreated_DuplicateMessage_SkippedAndAcknowledged | 2.02s | ✅ |
| testHandleOrderCreated_UnexpectedException_Acknowledged | 58ms | ✅ |
| testHandleOrderCreated_NullCorrelationId_ProcessedAndAcknowledged | 34ms | ✅ |
| testHandleOrderCreated_ProcessingException_NotAcknowledged | 16ms | ✅ |
| testHandleOrderCreated_NewMessage_ProcessedAndAcknowledged | 13ms | ✅ |
| testHandleOrderStatusChanged_NewMessage_ProcessedAndAcknowledged | 10ms | ✅ |
| testHandleOrderStatusChanged_DuplicateMessage_SkippedAndAcknowledged | 9ms | ✅ |

### OrderEventProcessingServiceTest

**Class Total Duration:** 1.24s

| Test Method | Duration | Status |
|-------------|----------|--------|
| testProcessOrderCreated_Success | 639ms | ✅ |
| testProcessOrderStatusChanged_Success | 426ms | ✅ |
| testCounters_TrackProcessedEvents | 120ms | ✅ |
| testResetCounters | 52ms | ✅ |

### OrderProcessorE2ETest

**Class Total Duration:** 14.42s

| Test Method | Duration | Status |
|-------------|----------|--------|
| testHappyPath_SingleMessage_ProcessedSuccessfully | 10.06s | ❌ |
| testDeduplication_DuplicateMessages_ProcessedOnlyOnce | 2.14s | ✅ |
| testMixedScenario_UniqueAndDuplicateMessages | 1.86s | ✅ |
| testMultipleUniqueMessages_AllProcessed | 231ms | ✅ |
| testMessageWithoutCorrelationId_StillProcessed | 120ms | ✅ |


---

## Module: order-processor

### Summary Statistics

- **Test Classes:** 3
- **Test Methods:** 16
- **Passed:** 15
- **Failed:** 1
- **Total Test Duration:** 17.98s
- **Module Execution Time:** 34.77s
- **Average Test Duration:** 1.12s

### Test Details

### OrderEventListenerTest

**Class Total Duration:** 2.43s

| Test Method | Duration | Status |
|-------------|----------|--------|
| testHandleOrderCreated_DuplicateMessage_SkippedAndAcknowledged | 2.28s | ✅ |
| testHandleOrderCreated_UnexpectedException_Acknowledged | 72ms | ✅ |
| testHandleOrderCreated_NullCorrelationId_ProcessedAndAcknowledged | 37ms | ✅ |
| testHandleOrderCreated_ProcessingException_NotAcknowledged | 17ms | ✅ |
| testHandleOrderCreated_NewMessage_ProcessedAndAcknowledged | 12ms | ✅ |
| testHandleOrderStatusChanged_NewMessage_ProcessedAndAcknowledged | 10ms | ✅ |
| testHandleOrderStatusChanged_DuplicateMessage_SkippedAndAcknowledged | 8ms | ✅ |

### OrderEventProcessingServiceTest

**Class Total Duration:** 1.31s

| Test Method | Duration | Status |
|-------------|----------|--------|
| testProcessOrderCreated_Success | 733ms | ✅ |
| testProcessOrderStatusChanged_Success | 452ms | ✅ |
| testCounters_TrackProcessedEvents | 104ms | ✅ |
| testResetCounters | 19ms | ✅ |

### OrderProcessorE2ETest

**Class Total Duration:** 14.24s

| Test Method | Duration | Status |
|-------------|----------|--------|
| testMixedScenario_UniqueAndDuplicateMessages | 11.54s | ❌ |
| testDeduplication_DuplicateMessages_ProcessedOnlyOnce | 2.12s | ✅ |
| testMultipleUniqueMessages_AllProcessed | 335ms | ✅ |
| testMessageWithoutCorrelationId_StillProcessed | 124ms | ✅ |
| testHappyPath_SingleMessage_ProcessedSuccessfully | 122ms | ✅ |


---

## Module: order-processor

### Summary Statistics

- **Test Classes:** 3
- **Test Methods:** 16
- **Passed:** 16
- **Failed:** 0
- **Total Test Duration:** 8.68s
- **Module Execution Time:** 25.81s
- **Average Test Duration:** 542ms

### Test Details

### OrderEventListenerTest

**Class Total Duration:** 2.48s

| Test Method | Duration | Status |
|-------------|----------|--------|
| testHandleOrderCreated_DuplicateMessage_SkippedAndAcknowledged | 2.33s | ✅ |
| testHandleOrderCreated_UnexpectedException_Acknowledged | 77ms | ✅ |
| testHandleOrderCreated_NullCorrelationId_ProcessedAndAcknowledged | 35ms | ✅ |
| testHandleOrderCreated_ProcessingException_NotAcknowledged | 16ms | ✅ |
| testHandleOrderCreated_NewMessage_ProcessedAndAcknowledged | 12ms | ✅ |
| testHandleOrderStatusChanged_NewMessage_ProcessedAndAcknowledged | 10ms | ✅ |
| testHandleOrderStatusChanged_DuplicateMessage_SkippedAndAcknowledged | 7ms | ✅ |

### OrderEventProcessingServiceTest

**Class Total Duration:** 1.43s

| Test Method | Duration | Status |
|-------------|----------|--------|
| testProcessOrderCreated_Success | 677ms | ✅ |
| testProcessOrderStatusChanged_Success | 579ms | ✅ |
| testCounters_TrackProcessedEvents | 125ms | ✅ |
| testResetCounters | 48ms | ✅ |

### OrderProcessorE2ETest

**Class Total Duration:** 4.76s

| Test Method | Duration | Status |
|-------------|----------|--------|
| testDeduplication_DuplicateMessages_ProcessedOnlyOnce | 2.13s | ✅ |
| testMixedScenario_UniqueAndDuplicateMessages | 2.06s | ✅ |
| testMultipleUniqueMessages_AllProcessed | 331ms | ✅ |
| testHappyPath_SingleMessage_ProcessedSuccessfully | 126ms | ✅ |
| testMessageWithoutCorrelationId_StillProcessed | 119ms | ✅ |


---

## Module: order-processor

### Summary Statistics

- **Test Classes:** 3
- **Test Methods:** 16
- **Passed:** 16
- **Failed:** 0
- **Total Test Duration:** 7.98s
- **Module Execution Time:** 25.03s
- **Average Test Duration:** 498ms

### Test Details

### OrderEventListenerTest

**Class Total Duration:** 2.51s

| Test Method | Duration | Status |
|-------------|----------|--------|
| testHandleOrderCreated_DuplicateMessage_SkippedAndAcknowledged | 2.36s | ✅ |
| testHandleOrderCreated_UnexpectedException_Acknowledged | 53ms | ✅ |
| testHandleOrderCreated_NullCorrelationId_ProcessedAndAcknowledged | 39ms | ✅ |
| testHandleOrderStatusChanged_DuplicateMessage_SkippedAndAcknowledged | 17ms | ✅ |
| testHandleOrderCreated_ProcessingException_NotAcknowledged | 17ms | ✅ |
| testHandleOrderCreated_NewMessage_ProcessedAndAcknowledged | 13ms | ✅ |
| testHandleOrderStatusChanged_NewMessage_ProcessedAndAcknowledged | 11ms | ✅ |

### OrderEventProcessingServiceTest

**Class Total Duration:** 1.37s

| Test Method | Duration | Status |
|-------------|----------|--------|
| testProcessOrderCreated_Success | 684ms | ✅ |
| testProcessOrderStatusChanged_Success | 584ms | ✅ |
| testCounters_TrackProcessedEvents | 72ms | ✅ |
| testResetCounters | 30ms | ✅ |

### OrderProcessorE2ETest

**Class Total Duration:** 4.10s

| Test Method | Duration | Status |
|-------------|----------|--------|
| testMixedScenario_UniqueAndDuplicateMessages | 1.90s | ✅ |
| testDeduplication_DuplicateMessages_ProcessedOnlyOnce | 1.63s | ✅ |
| testMultipleUniqueMessages_AllProcessed | 329ms | ✅ |
| testHappyPath_SingleMessage_ProcessedSuccessfully | 121ms | ✅ |
| testMessageWithoutCorrelationId_StillProcessed | 118ms | ✅ |


---

## Module: routebox-archunit

### Summary Statistics

- **Test Classes:** 6
- **Test Methods:** 32
- **Passed:** 32
- **Failed:** 0
- **Total Test Duration:** 227ms
- **Module Execution Time:** 4.46s
- **Average Test Duration:** 7ms

### Test Details

### EntityRepositoryPatternTest

**Class Total Duration:** 119ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| tableAnnotationShouldBeUsedForEntities | 81ms | ✅ |
| entitiesShouldNotHavePublicFields | 24ms | ✅ |
| repositoriesShouldBeInterfaces | 8ms | ✅ |
| entitiesIdFieldsShouldBeAnnotatedWithId | 4ms | ✅ |
| entitiesShouldResideInEntityPackage | 2ms | ✅ |

### LayeringArchitectureTest

**Class Total Duration:** 29ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| repositoriesShouldNotAccessServicesOrControllers | 23ms | ✅ |
| servicesShouldNotAccessControllers | 4ms | ✅ |
| controllersShouldNotAccessRepositoriesDirectly | 2ms | ✅ |

### NamingConventionTest

**Class Total Duration:** 14ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| servicesShouldBeSuffixed | 3ms | ✅ |
| exceptionsShouldBeSuffixed | 2ms | ✅ |
| configurationsShouldBeSuffixed | 2ms | ✅ |
| repositoriesShouldBeSuffixed | 2ms | ✅ |
| dtosShouldResideInDtoPackage | 2ms | ✅ |
| entitiesShouldResideInEntityPackage | 2ms | ✅ |
| controllersShouldBeSuffixed | 1ms | ✅ |

### PackageDependencyTest

**Class Total Duration:** 26ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| commonModuleShouldNotDependOnOtherModules | 9ms | ✅ |
| clientModuleShouldNotDependOnServerOrOrderService | 7ms | ✅ |
| serverModuleShouldNotDependOnOrderService | 3ms | ✅ |
| entitiesShouldNotDependOnServicesOrControllers | 3ms | ✅ |
| repositoriesShouldOnlyDependOnEntitiesAndSpringData | 2ms | ✅ |
| orderServiceShouldNotDependOnRouteBoxServer | 1ms | ✅ |
| controllersShouldNotDependOnOtherControllers | 1ms | ✅ |

### SpringAnnotationTest

**Class Total Duration:** 14ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| controllersShouldBeAnnotatedWithRestController | 5ms | ✅ |
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
| serviceClassesShouldBeAnnotatedWithTransactional | 9ms | ✅ |
| serviceMethodsModifyingDataShouldBeTransactional | 3ms | ✅ |
| controllersShouldNotBeTransactional | 2ms | ✅ |


---

