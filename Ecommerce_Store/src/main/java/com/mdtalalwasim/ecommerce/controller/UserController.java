package com.mdtalalwasim.ecommerce.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.mdtalalwasim.ecommerce.entity.Cart;
import com.mdtalalwasim.ecommerce.entity.Category;
import com.mdtalalwasim.ecommerce.entity.ProductOrder;
import com.mdtalalwasim.ecommerce.entity.ProductOrderRequest;
import com.mdtalalwasim.ecommerce.entity.User;
import com.mdtalalwasim.ecommerce.service.CartService;
import com.mdtalalwasim.ecommerce.service.CategoryService;
import com.mdtalalwasim.ecommerce.service.ProductOrderService;
import com.mdtalalwasim.ecommerce.service.UserService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/user")
public class UserController {

	@Autowired
	CategoryService categoryService;

	@Autowired
	UserService userService;

	@Autowired
	CartService cartService;

	@Autowired
	ProductOrderService productOrderService;

	@Value("${stripe.api.publicKey}")
	private String stripePublicKey;

	// Track which user is logged in
	@ModelAttribute
	public void getUserDetails(Principal principal, Model model) {
		if (principal != null) {
			String currenLoggedInUserEmail = principal.getName();
			User currentUserDetails = userService.getUserByEmail(currenLoggedInUserEmail);
			model.addAttribute("currentLoggedInUserDetails", currentUserDetails);

			Long countCartForUser = cartService.getCounterCart(currentUserDetails.getId());
			model.addAttribute("countCartForUser", countCartForUser);
		}

		List<Category> allActiveCategory = categoryService.findAllActiveCategory();
		model.addAttribute("allActiveCategory", allActiveCategory);
	}

	@GetMapping("/")
	public String home(Principal principal, Model model) {
		User user = getLoggedUserDetails(principal);
		List<ProductOrder> orders = productOrderService.getOrdersByUser(user.getId());
		model.addAttribute("orders", orders);
		return "user/user-home";
	}

	// ADD TO CART
	@GetMapping("/add-to-cart")
	String addToCart(@RequestParam Long productId, Principal principal, HttpSession session) {
		User user = getLoggedUserDetails(principal);
		Cart saveCart = cartService.saveCart(productId, user.getId());
		if (ObjectUtils.isEmpty(saveCart)) {
			session.setAttribute("errorMsg", "Failed to add product to cart");
		} else {
			session.setAttribute("successMsg", "Product added to cart successfully");
		}
		return "redirect:/product/" + productId;
	}

	@GetMapping("/cart")
	String loadCartPage(Principal principal, Model model, HttpSession session) {
		User user = getLoggedUserDetails(principal);
		List<Cart> carts = cartService.getCartsByUser(user.getId());
		
		if(carts.isEmpty()) {
			session.setAttribute("errorMsg", "Your cart is empty");
			return "redirect:/products";
		}
		
		model.addAttribute("carts", carts);
		Double totalOrderPrice = carts.get(carts.size() - 1).getTotalOrderPrice();
		model.addAttribute("totalOrderPrice", totalOrderPrice);
		return "/user/cart";
	}

	@GetMapping("/cart-quantity-update")
	public String updateCartQuantity(@RequestParam("symbol") String symbol, @RequestParam("cartId") Long cartId) {
		cartService.updateCartQuantity(symbol, cartId);
		return "redirect:/user/cart";
	}

	@GetMapping("/orders")
	public String orderPage(Principal principal, Model model) {
		User user = getLoggedUserDetails(principal);
		List<Cart> carts = cartService.getCartsByUser(user.getId());
		
		if(carts.isEmpty()) {
			return "redirect:/products";
		}
		
		model.addAttribute("carts", carts);
		Double orderPrice = carts.get(carts.size() - 1).getTotalOrderPrice();
		Double totalOrderPrice = orderPrice + 250 + 100;
		model.addAttribute("orderPrice", orderPrice);
		model.addAttribute("totalOrderPrice", totalOrderPrice);
		model.addAttribute("stripePublicKey", stripePublicKey);
		return "/user/order";
	}

	@PostMapping("/save-order")
	public String saveOrder(@ModelAttribute ProductOrderRequest orderRequest, Principal principal, HttpSession session) {
		try {
			User user = getLoggedUserDetails(principal);
			ProductOrder savedOrder = productOrderService.saveProductOrder(user.getId(), orderRequest);
			if (!ObjectUtils.isEmpty(savedOrder)) {
				session.setAttribute("successMsg", "Order placed successfully!");
			} else {
				session.setAttribute("errorMsg", "Failed to place order. Please try again.");
			}
		} catch (Exception e) {
			e.printStackTrace();
			session.setAttribute("errorMsg", "An error occurred: " + e.getMessage());
		}
		return "redirect:/user/";
	}

	private User getLoggedUserDetails(Principal principal) {
		String email = principal.getName();
		return userService.getUserByEmail(email);
	}
}

