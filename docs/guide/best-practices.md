# 最佳实践

本文档总结了DDD项目开发中的最佳实践。

## 领域建模

### 1. 识别聚合

聚合是数据修改的单元，应该：

- **保持聚合小**：聚合应该尽可能小，只包含真正需要一起修改的实体
- **通过ID引用其他聚合**：不要直接引用其他聚合的实体
- **一个聚合一个仓储**：只通过聚合根访问聚合

**示例**：
```java
// 好的设计：Order聚合只包含Order和OrderItem
public class Order extends BaseEntity {
    private List<OrderItem> items; // 聚合内的实体
    private Long customerId;        // 通过ID引用Customer聚合
}

// 不好的设计
public class Order extends BaseEntity {
    private Customer customer; // 直接引用其他聚合
}
```

### 2. 使用值对象

值对象应该：

- **不可变**：创建后不能修改
- **通过值相等性比较**：两个值对象如果值相同，则相等
- **包含验证逻辑**：值对象可以包含自己的验证规则

**示例**：
```java
public class Email {
    private final String value;
    
    public Email(String value) {
        if (value == null || !isValidEmail(value)) {
            throw new IllegalArgumentException("无效的邮箱地址");
        }
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Email email = (Email) o;
        return Objects.equals(value, email.value);
    }
}
```

### 3. 封装业务规则

业务规则应该封装在领域对象中：

```java
// 好的设计：业务规则在实体中
public class Order {
    public void addItem(OrderItem item) {
        if (this.status != OrderStatus.DRAFT) {
            throw new IllegalStateException("只能向草稿订单添加商品");
        }
        this.items.add(item);
    }
}

// 不好的设计：业务规则在应用层
public class OrderApplicationService {
    public void addItem(Long orderId, OrderItem item) {
        Order order = repository.findById(orderId);
        if (order.getStatus() != OrderStatus.DRAFT) {
            throw new IllegalStateException("只能向草稿订单添加商品");
        }
        order.getItems().add(item); // 直接操作集合
    }
}
```

## 应用服务

### 1. 保持应用服务薄

应用服务应该只负责协调，不包含业务逻辑：

```java
// 好的设计
@Transactional
public OrderDTO createOrder(CreateOrderCommand command) {
    // 1. 验证
    Customer customer = customerRepository.findById(command.getCustomerId())
        .orElseThrow(() -> new IllegalArgumentException("客户不存在"));
    
    // 2. 创建领域对象
    Order order = Order.create(customer.getId());
    
    // 3. 添加商品
    for (OrderItemCommand item : command.getItems()) {
        Product product = productRepository.findById(item.getProductId())
            .orElseThrow(() -> new IllegalArgumentException("商品不存在"));
        order.addItem(OrderItem.create(product.getId(), item.getQuantity()));
    }
    
    // 4. 保存
    Order savedOrder = orderRepository.save(order);
    
    // 5. 返回DTO
    return orderMapper.toDTO(savedOrder);
}
```

### 2. 使用领域服务处理复杂逻辑

当业务逻辑涉及多个聚合时，使用领域服务：

```java
// 领域服务
public class OrderDomainService {
    public boolean canPlaceOrder(Customer customer, Order order) {
        // 检查客户信用
        if (!customer.hasGoodCredit()) {
            return false;
        }
        
        // 检查库存
        for (OrderItem item : order.getItems()) {
            if (!inventoryService.hasStock(item.getProductId(), item.getQuantity())) {
                return false;
            }
        }
        
        return true;
    }
}
```

### 3. 管理事务边界

事务边界应该在应用服务方法级别：

```java
@Transactional
public OrderDTO placeOrder(PlaceOrderCommand command) {
    // 整个方法在一个事务中
    Order order = orderRepository.findById(command.getOrderId())
        .orElseThrow(...);
    
    if (!orderDomainService.canPlaceOrder(customer, order)) {
        throw new IllegalArgumentException("无法下单");
    }
    
    order.place();
    orderRepository.save(order);
    
    // 发布领域事件（如果需要）
    eventPublisher.publish(new OrderPlacedEvent(order.getId()));
    
    return orderMapper.toDTO(order);
}
```

## 仓储模式

### 1. 仓储接口在领域层

仓储接口定义在领域层，使用领域对象：

```java
// 领域层
public interface OrderRepository {
    Order save(Order order);
    Optional<Order> findById(Long id);
    List<Order> findByCustomerId(Long customerId);
}
```

### 2. 仓储实现在基础设施层

仓储实现在基础设施层，处理持久化细节：

```java
// 基础设施层
@Repository
public class OrderRepositoryImpl implements OrderRepository {
    private final OrderJpaRepository jpaRepository;
    private final OrderEntityMapper mapper;
    
    @Override
    public Order save(Order order) {
        OrderEntity entity = mapper.toEntity(order);
        OrderEntity savedEntity = jpaRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }
}
```

### 3. 使用规范模式（Specification Pattern）

复杂查询可以使用规范模式：

```java
public interface OrderSpecification {
    boolean isSatisfiedBy(Order order);
}

public class HighValueOrderSpecification implements OrderSpecification {
    private final BigDecimal threshold;
    
    @Override
    public boolean isSatisfiedBy(Order order) {
        return order.getTotalAmount().compareTo(threshold) > 0;
    }
}
```

## 异常处理

### 1. 使用领域异常

定义领域特定的异常：

```java
public class OrderNotFoundException extends DomainException {
    public OrderNotFoundException(Long orderId) {
        super("订单不存在: " + orderId);
    }
}

public class InsufficientStockException extends DomainException {
    public InsufficientStockException(Long productId, int requested, int available) {
        super(String.format("商品库存不足: 商品ID=%d, 请求数量=%d, 可用数量=%d", 
            productId, requested, available));
    }
}
```

### 2. 统一异常处理

在接口层统一处理异常：

```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleOrderNotFound(
            OrderNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(ErrorResponse.of(e.getMessage()));
    }
    
    @ExceptionHandler(InsufficientStockException.class)
    public ResponseEntity<ErrorResponse> handleInsufficientStock(
            InsufficientStockException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ErrorResponse.of(e.getMessage()));
    }
}
```

## 性能优化

### 1. 使用延迟加载

对于关联实体，使用延迟加载：

```java
@Entity
public class OrderEntity {
    @OneToMany(fetch = FetchType.LAZY)
    private List<OrderItemEntity> items;
}
```

### 2. 使用DTO投影

查询时只选择需要的字段：

```java
public interface OrderSummary {
    Long getId();
    BigDecimal getTotalAmount();
    OrderStatus getStatus();
}

@Query("SELECT o.id as id, o.totalAmount as totalAmount, o.status as status " +
       "FROM OrderEntity o WHERE o.customerId = :customerId")
List<OrderSummary> findOrderSummariesByCustomerId(Long customerId);
```

### 3. 批量操作

对于批量操作，使用批量更新：

```java
@Modifying
@Query("UPDATE OrderEntity o SET o.status = :status WHERE o.id IN :ids")
void updateStatusByIds(@Param("ids") List<Long> ids, @Param("status") OrderStatus status);
```

## 测试

### 1. 领域层测试

领域层测试应该快速、独立：

```java
class OrderTest {
    @Test
    void shouldAddItemToDraftOrder() {
        // Given
        Order order = Order.create(customerId);
        
        // When
        order.addItem(OrderItem.create(productId, quantity));
        
        // Then
        assertThat(order.getItems()).hasSize(1);
    }
    
    @Test
    void shouldNotAddItemToPlacedOrder() {
        // Given
        Order order = Order.create(customerId);
        order.place();
        
        // When & Then
        assertThatThrownBy(() -> order.addItem(OrderItem.create(productId, quantity)))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("只能向草稿订单添加商品");
    }
}
```

### 2. 应用服务测试

使用Mock测试应用服务：

```java
@ExtendWith(MockitoExtension.class)
class OrderApplicationServiceTest {
    
    @Mock
    private OrderRepository orderRepository;
    
    @Mock
    private OrderDomainService orderDomainService;
    
    @InjectMocks
    private OrderApplicationService orderApplicationService;
    
    @Test
    void shouldCreateOrder() {
        // Given
        CreateOrderCommand command = new CreateOrderCommand(...);
        when(orderDomainService.canCreateOrder(any())).thenReturn(true);
        
        // When
        OrderDTO result = orderApplicationService.createOrder(command);
        
        // Then
        assertThat(result).isNotNull();
        verify(orderRepository).save(any(Order.class));
    }
}
```

### 3. 集成测试

关键流程应该有集成测试：

```java
@SpringBootTest
@AutoConfigureMockMvc
class OrderControllerIntegrationTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Test
    void shouldCreateOrder() throws Exception {
        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"customerId\":1,\"items\":[...]}"))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").exists());
    }
}
```

## 代码质量

### 1. 使用静态分析工具

- SonarQube：代码质量分析
- Checkstyle：代码风格检查
- SpotBugs：Bug检测

### 2. 代码审查

- 每个PR都应该有代码审查
- 关注架构一致性
- 检查是否遵循DDD原则

### 3. 持续重构

- 定期重构代码
- 保持代码简洁
- 移除重复代码

