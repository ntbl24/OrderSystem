package io.order.backend.cart;

import io.order.backend.cart.domain.Cart;
import io.order.backend.cart.infrastructure.persistence.*;
import io.order.backend.product.domain.ProductId;
import io.order.backend.user.domain.UserId;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JpaCartAdapterTest {
    @Mock CartJpaRepository repository;
    @InjectMocks JpaCartAdapter adapter;

    @Test
    void save_whenNew_shouldPersistAssignedIdAndReturnVersion() {
        Cart cart = Cart.create(UserId.generate());
        cart.addItem(ProductId.generate(), "Product", 2, BigDecimal.TEN);
        when(repository.findById(cart.getId().value())).thenReturn(Optional.empty());
        when(repository.saveAndFlush(any())).thenAnswer(invocation -> {
            CartEntity entity = invocation.getArgument(0);
            assertThat(entity.createdAt).isNotNull();
            assertThat(entity.updatedAt).isNotNull();
            entity.version = 0L;
            return entity;
        });
        Cart saved = adapter.save(cart);
        assertThat(saved.getId()).isEqualTo(cart.getId());
        assertThat(saved.getVersion()).isZero();
        assertThat(saved.getTotal()).isEqualByComparingTo("20");
    }

    @Test
    void save_whenStaleOrDeleted_shouldRejectBeforeWriting() {
        Cart cart = Cart.create(UserId.generate());
        CartEntity entity = new CartEntity();
        CartPersistenceMapper.copy(cart, entity);
        entity.version = 1L;
        Cart stale = CartPersistenceMapper.domain(entity);
        entity.version = 2L;
        when(repository.findById(entity.id)).thenReturn(Optional.of(entity));
        assertThatThrownBy(() -> adapter.save(stale)).isInstanceOf(ObjectOptimisticLockingFailureException.class);
        when(repository.findById(entity.id)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> adapter.save(stale)).isInstanceOf(ObjectOptimisticLockingFailureException.class);
        verify(repository, never()).saveAndFlush(any());
    }
}
