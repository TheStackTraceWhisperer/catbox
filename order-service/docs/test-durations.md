# Test Duration Report

Generated: 2025-11-04T00:41:04.616588941Z

This report aggregates test durations across all modules in the build.

---

## Module: order-service

### Summary Statistics

- **Test Classes:** 6
- **Test Methods:** 21
- **Passed:** 21
- **Failed:** 0
- **Total Test Duration:** 1.69s
- **Module Execution Time:** 28.03s
- **Average Test Duration:** 80ms

### Test Details

### OrderControllerTest

**Class Total Duration:** 460ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| getAllOrders_ShouldReturnAllOrders | 252ms | ✅ |
| updateOrderStatus_WithNullStatus_ShouldReturnBadRequest | 95ms | ✅ |
| getOrderById_ShouldReturnOrder | 42ms | ✅ |
| createOrder_ShouldReturnCreatedOrder | 34ms | ✅ |
| updateOrderStatus_ShouldUpdateAndReturnOrder | 20ms | ✅ |
| updateOrderStatus_WithBlankStatus_ShouldReturnBadRequest | 17ms | ✅ |

### OrderNotFoundExceptionTest

**Class Total Duration:** 208ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| shouldCreateExceptionWithCorrectMessage | 201ms | ✅ |
| shouldBeRuntimeException | 7ms | ✅ |

### OrderServiceFailureTest

**Class Total Duration:** 217ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| testOrderCreationFailsWhenOutboxWriteFails | 217ms | ✅ |

### OrderServiceTest

**Class Total Duration:** 767ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| testGetAllOrders | 631ms | ✅ |
| testCreateOrder_CreatesOrderAndOutboxEvent | 50ms | ✅ |
| testUpdateOrderStatus_CreatesOutboxEvent | 34ms | ✅ |
| testGetOrderById_Success | 27ms | ✅ |
| testGetOrderById_ThrowsExceptionWhenNotFound | 25ms | ✅ |

### OrderTest

**Class Total Duration:** 5ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| onCreate_shouldNotOverrideExistingStatus | 2ms | ✅ |
| shouldCreateOrderWithConstructor | 1ms | ✅ |
| shouldSupportSettersAndGetters | 1ms | ✅ |
| onCreate_shouldSetDefaultStatus | 1ms | ✅ |

### UpdateStatusRequestTest

**Class Total Duration:** 33ms

| Test Method | Duration | Status |
|-------------|----------|--------|
| shouldSupportEquality | 25ms | ✅ |
| shouldCreateRequestWithStatus | 4ms | ✅ |
| shouldHandleNullStatus | 4ms | ✅ |


---

