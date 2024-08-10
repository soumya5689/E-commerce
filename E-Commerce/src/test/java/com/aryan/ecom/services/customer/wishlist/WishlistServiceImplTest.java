package com.aryan.ecom.services.customer.wishlist;

import com.aryan.ecom.dto.WishlistDto;
import com.aryan.ecom.model.Product;
import com.aryan.ecom.model.User;
import com.aryan.ecom.model.Wishlist;
import com.aryan.ecom.repository.ProductRepository;
import com.aryan.ecom.repository.UserRepository;
import com.aryan.ecom.repository.WishlistRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@Slf4j
class WishlistServiceImplTest {

    AutoCloseable autoCloseable;
    @Mock
    UserRepository userRepository;
    @Mock
    ProductRepository  productRepository;
    @Mock
    WishlistRepository wishlistRepository;

    private WishlistServiceImpl wishlistService;

    private Product createMockProduct(Long id) {
        return Product.builder()
                .id(id)
                .price(100L)
                .name("Demo Product")
                .description("This is a demo product.")
                .img(new byte[]{1, 2, 3})
                .build();
    }

    private User createMockUser(Long id) {
        return User.builder()
                .id(id)
                .name("Demo User")
                .build();
    }

    private Wishlist createMockWishlist(Long id, Product product, User user) {
        Wishlist wishlist = new Wishlist();
        wishlist.setId(id);
        wishlist.setProduct(product);
        wishlist.setUser(user);
        return wishlist;
    }
    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        wishlistService = new WishlistServiceImpl(userRepository,productRepository,wishlistRepository);
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    void addProductToWishlist() {
        WishlistDto wishlistDto = WishlistDto.builder()
                .userId(1L)
                .productId(1L)
                .build();

        Product mockProduct = createMockProduct(1L);
        User mockUser = createMockUser(1L);
        Wishlist mockWishlist = createMockWishlist(1L, mockProduct, mockUser);

        when(productRepository.findById(1L)).thenReturn(Optional.of(mockProduct));
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(wishlistRepository.save(any(Wishlist.class))).thenReturn(mockWishlist);

        WishlistDto result = wishlistService.addProductToWishlist(wishlistDto);

        assertNotNull(result);
        assertEquals(1L, result.getUserId());
        assertEquals(1L, result.getProductId());
    }

    @Test
    void getWishlistByUserId() {
        Long userId = 1L;
        Product mockProduct1 = createMockProduct(1L);
        Product mockProduct2 = createMockProduct(2L);
        User mockUser = createMockUser(userId);

        Wishlist mockWishlist1 = createMockWishlist(1L, mockProduct1, mockUser);
        Wishlist mockWishlist2 = createMockWishlist(2L, mockProduct2, mockUser);

        when(wishlistRepository.findAllByUserId(userId)).thenReturn(List.of(mockWishlist1, mockWishlist2));

        List<WishlistDto> wishlistDtos = wishlistService.getWishlistByUserId(userId);

        assertNotNull(wishlistDtos);
        assertEquals(2, wishlistDtos.size());
        assertEquals(1L, wishlistDtos.get(0).getProductId());
        assertEquals(2L, wishlistDtos.get(1).getProductId());
    }
}