package com.mdtalalwasim.ecommerce.service;

import com.mdtalalwasim.ecommerce.entity.ProductOrder;
import com.mdtalalwasim.ecommerce.entity.ProductOrderRequest;

public interface ProductOrderService {
	
	public ProductOrder saveProductOrder(Long id, ProductOrderRequest productOrderRequest);
	
	public java.util.List<ProductOrder> getAllOrders();
	
	public ProductOrder updateOrderStatus(Long id, String status);

	public java.util.List<ProductOrder> getOrdersByUser(Long userId);
}
