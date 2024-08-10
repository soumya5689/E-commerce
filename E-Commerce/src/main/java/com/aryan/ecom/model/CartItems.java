	package com.aryan.ecom.model;

	import lombok.AllArgsConstructor;
	import lombok.Builder;
	import lombok.NoArgsConstructor;
	import org.hibernate.annotations.OnDelete;
	import org.hibernate.annotations.OnDeleteAction;

	import com.aryan.ecom.dto.CartItemsDto;

	import jakarta.persistence.Entity;
	import jakarta.persistence.FetchType;
	import jakarta.persistence.GeneratedValue;
	import jakarta.persistence.GenerationType;
	import jakarta.persistence.Id;
	import jakarta.persistence.JoinColumn;
	import jakarta.persistence.ManyToOne;
	import jakarta.persistence.OneToOne;
	import lombok.Data;

	@Entity
	@Data
	@AllArgsConstructor
	@Builder
	@NoArgsConstructor
	public class CartItems {
		@Id
		@GeneratedValue(strategy = GenerationType.IDENTITY)
		private Long id;

		private Long price;

		private Long quantity;

		@ManyToOne(fetch = FetchType.LAZY, optional = false)
		@JoinColumn(name = "product_id", nullable = false)
		@OnDelete(action = OnDeleteAction.CASCADE)
		private Product product;

		@ManyToOne(fetch = FetchType.LAZY, optional = false)
		@JoinColumn(name = "user_id", nullable = false)
		@OnDelete(action = OnDeleteAction.CASCADE)
		private User user;

		@OneToOne(fetch = FetchType.LAZY)
		@JoinColumn(name = "order_id")
		private Order order;

		public CartItemsDto getCartDto() {
			return CartItemsDto.builder()
					.id(id)
					.price(price)
					.productId(product.getId())
					.quantity(quantity)
					.userId(user.getId())
					.productName(product.getName())
					.returnedImage(product.getImg())
					.build();
		}
	}
