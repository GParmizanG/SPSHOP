package com.mdtalalwasim.ecommerce.service.impl;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mdtalalwasim.ecommerce.entity.Cart;
import com.mdtalalwasim.ecommerce.entity.OrderAddress;
import com.mdtalalwasim.ecommerce.entity.ProductOrder;
import com.mdtalalwasim.ecommerce.entity.ProductOrderRequest;
import com.mdtalalwasim.ecommerce.repository.CartRepository;
import com.mdtalalwasim.ecommerce.repository.ProductOrderRepository;
import com.mdtalalwasim.ecommerce.service.ProductOrderService;
import com.mdtalalwasim.ecommerce.utils.SecurityUtils;

@Service
public class ProductOrderServiceImpl implements ProductOrderService {

	@Autowired
	private ProductOrderRepository productOrderRepository;

	@Autowired
	private CartRepository cartRepository;

	@Override
	public ProductOrder saveProductOrder(Long userId, ProductOrderRequest req) {

		List<Cart> listOfCarts = cartRepository.findByUserId(userId);
		ProductOrder finalOrder = null;

		for (Cart cart : listOfCarts) {

			// Build shipping address
			OrderAddress address = new OrderAddress();
			address.setFirstName(req.getFirstName());
			address.setLastName(req.getLastName());
			address.setEmail(req.getEmail());
			address.setMobile(req.getMobile());
			address.setAddress(req.getAddress());
			address.setCity(req.getCity());
			address.setState(req.getState());
			address.setPinCode(req.getPinCode());

			// Build order
			ProductOrder order = new ProductOrder();
			order.setOrderId(UUID.randomUUID().toString());
			order.setOrderDate(new Date());
			order.setProduct(cart.getProduct());
			order.setPrice(cart.getProduct().getDiscountPrice());
			order.setQuantity(cart.getQuantity());
			order.setUser(cart.getUser());
			
			// Handle Payment Status and Transaction ID
			if ("Online Payment".equalsIgnoreCase(req.getPaymentType())) {
				order.setStatus("Success");
				// Encode the transaction ID as requested (2jwt style)
				order.setTransactionId(SecurityUtils.encodePaymentId(req.getTransactionId()));
			} else {
				order.setStatus("In Progress");
			}
			
			order.setPaymentType(req.getPaymentType());
			order.setOrderAddress(address);

			finalOrder = productOrderRepository.save(order);
		}

		// Clear cart after placing order
		cartRepository.deleteAll(listOfCarts);

		return finalOrder;
	}

	@Override
	public List<ProductOrder> getAllOrders() {
		return productOrderRepository.findAll();
	}

	@Override
	public ProductOrder updateOrderStatus(Long id, String status) {
		ProductOrder order = productOrderRepository.findById(id).orElse(null);
		if (order != null) {
			order.setStatus(status);
			return productOrderRepository.save(order);
		}
		return null;
	}

	@Override
	public List<ProductOrder> getOrdersByUser(Long userId) {
		return productOrderRepository.findByUserId(userId);
	}
}
