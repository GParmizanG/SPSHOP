package com.mdtalalwasim.ecommerce.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.mdtalalwasim.ecommerce.entity.Category;
import com.mdtalalwasim.ecommerce.entity.Product;
import com.mdtalalwasim.ecommerce.entity.User;
import com.mdtalalwasim.ecommerce.service.CartService;
import com.mdtalalwasim.ecommerce.service.CategoryService;
import com.mdtalalwasim.ecommerce.service.ProductService;
import com.mdtalalwasim.ecommerce.service.UserService;
import com.mdtalalwasim.ecommerce.entity.ProductOrder;
import com.mdtalalwasim.ecommerce.service.ProductOrderService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin")
public class AdminViewController {
	
	@Autowired
	CategoryService categoryService;
	
	@Autowired
	ProductService productService;

	@Autowired
	UserService userService;
	
	@Autowired
	CartService cartService;
	
	/**
	 * Binds the active authenticated administrator's details, shopping cart size, 
	 * and active category trees to the model on every incoming request.
	 */
	@ModelAttribute 
	public void getUserDetails(Principal principal, Model model) {
		if(principal != null) {
			String currenLoggedInUserEmail = principal.getName();
			User currentUserDetails = userService.getUserByEmail(currenLoggedInUserEmail);
			model.addAttribute("currentLoggedInUserDetails",currentUserDetails);
			
			Long countCartForUser = cartService.getCounterCart(currentUserDetails.getId());
			System.out.println("Admin Cart Count :"+countCartForUser);
			model.addAttribute("countCartForUser", countCartForUser);
			
		}
		List<Category> allActiveCategory = categoryService.findAllActiveCategory();
		model.addAttribute("allActiveCategory",allActiveCategory);
		
	}
	
	/**
	 * Renders the main administrator dashboard and analytics interface.
	 */
	@GetMapping("/")
	public String adminIndex() {
		
		return "admin/admin-dashboard";
	}
	
	/**
	 * Shows the category creation form.
	 */
	@GetMapping("/add-category")
	public String addCategory(Model model) {
		
		return "admin/category/category-add-form";
	}
	
	/**
	 * Persists a new product category, checks duplicates, uploads visual banners,
	 * and updates the active categories.
	 */
	@PostMapping("/save-category")
	public String saveCategory(@ModelAttribute Category category, @RequestParam("file") MultipartFile file, HttpSession session) throws IOException {
		
		String imageName = file !=null ? file.getOriginalFilename() : "default.jpg";
		category.setCategoryImage(imageName);
		
		if (!file.isEmpty()) {
			category.setImageBytes(file.getBytes());
		}
		
		if(categoryService.existCategory(category.getCategoryName())) {
			session.setAttribute("errorMsg", "Category Name already Exists");
		}else {
			Category saveCategory = categoryService.saveCategory(category);
			
			if(ObjectUtils.isEmpty(saveCategory)) {
				session.setAttribute("errorMsg", "Not Saved! Internal Server Error!");
			}else {
				try {
					File saveFile = new ClassPathResource("static/img").getFile();
					Path path = Paths.get(saveFile.getAbsolutePath()+File.separator+"category"+File.separator+file.getOriginalFilename());
					Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				} catch (Exception e) {
					System.out.println("File System Save Failed: " + e.getMessage());
				}
				session.setAttribute("successMsg", "Category Save Successfully.");
			}
		}
		return "redirect:/admin/category";
	}

	/**
	 * Displays all categories registered on the platform with local formatting.
	 */
	@GetMapping("/category")
	public String category(Model model) {
		System.out.println("category:WWWWWWWWW");
		List<Category> allCategories = categoryService.getAllCategories();
		System.out.println("category: "+allCategories.toString());
		for (Category category : allCategories) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyy HH:mm:ss");
			String format = formatter.format(category.getCreatedAt());
			model.addAttribute("formattedDateTimeCreatedAt",format);
			
		}
		
		model.addAttribute("allCategoryList",allCategories);
		
		return "/admin/category/category-home";
	}
	
	/**
	 * Displays the category editing interface loaded with existing details.
	 */
	@GetMapping("/edit-category/{id}")
	public String editCategoryForm(@PathVariable("id") long id, Model model) {
		Optional<Category> categoryObj = categoryService.findById(id);
		if(categoryObj.isPresent()) {
			Category category = categoryObj.get();
			model.addAttribute("category", category);
		}else {
			System.out.println("ELSEEEEE");
		}
		return "/admin/category/category-edit-form";
	}
	
	/**
	 * Saves edits to existing categories, including name adjustments, toggle operations, and image re-uploads.
	 */
	@PostMapping("/update-category")
	public String udateCategory(@ModelAttribute Category category, @RequestParam("file") MultipartFile file, HttpSession session) throws IOException {
		System.out.println("Category for UPDATE :"+category.toString());
		
		Optional<Category> categoryById = categoryService.findById(category.getId());
		System.out.println("Category obj"+categoryById.toString());
		
		if(categoryById.isPresent()) {
			System.out.println("Present:");
			Category oldCategory = categoryById.get();
			System.out.println("Category old Obj "+oldCategory.toString());
			oldCategory.setCategoryName(category.getCategoryName());
			oldCategory.setIsActive(category.getIsActive());
			
			String imageName =  file.isEmpty() ?  oldCategory.getCategoryImage() : file.getOriginalFilename();
			oldCategory.setCategoryImage(imageName);	
			
			if (!file.isEmpty()) {
				oldCategory.setImageBytes(file.getBytes());
			}
			
			Category updatedCategory = categoryService.saveCategory(oldCategory);
			
			if(!ObjectUtils.isEmpty(updatedCategory)) {
				if(!file.isEmpty()) {
					try {
						File saveFile = new ClassPathResource("static/img").getFile();
						Path path = Paths.get(saveFile.getAbsolutePath()+File.separator+"category"+File.separator+file.getOriginalFilename());
						Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
					} catch (Exception e) {
						System.out.println("File backup failed: " + e.getMessage());
					}
				}
				session.setAttribute("successMsg", "Edited successfully");
			}else {
				session.setAttribute("errorMsg", "Something wrong on server!");
			}
			
		}else {
			System.out.println("Not Present:");
		}
		
		return "redirect:/admin/category";
	}
	
	/**
	 * Deletes a registered product category from the system inventory.
	 */
	@GetMapping("/delete-category/{id}")
	public String deleteCategory(@PathVariable("id") long id, HttpSession session) {
		Boolean deleteCategory = categoryService.deleteCategory(id);
		if(deleteCategory) {
			session.setAttribute("successMsg", "Category Deleted Successfully");
		}else {
			session.setAttribute("errorMsg", "Server Error");
		}
		
		return "redirect:/admin/category";
	}
	
	/**
	 * Renders the new product registration interface containing current categories catalog.
	 */
	@GetMapping("/add-product")
	public String addProduct(Model model) {
		List<Category> allCategories = categoryService.getAllCategories();
		model.addAttribute("allCategoryList",allCategories);
		return "/admin/product/add-product";
	}
	
	/**
	 * Saves a newly declared product, handles visual asset updates, and initializes standard discounts.
	 */
	@PostMapping("/save-product")
	public String saveProduct(@ModelAttribute Product product, @RequestParam("file") MultipartFile file, HttpSession session) throws IOException {
		String imageName = file !=null ? file.getOriginalFilename() : "default.png"; 
		
		product.setProductImage(imageName);
		if (file != null && !file.isEmpty()) {
			product.setImageBytes(file.getBytes());
		}
		product.setDiscount(0);
		product.setDiscountPrice(product.getProductPrice());
		
		Product saveProduct = productService.saveProduct(product);
		 
		if(!ObjectUtils.isEmpty(saveProduct)) {
			try {
				File savefile = new ClassPathResource("static/img").getFile();
				Path path = Paths.get(savefile.getAbsolutePath()+File.separator+"product_image"+File.separator+imageName);
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
			} catch (Exception e) {
				System.out.println("File system backup failed: " + e.getMessage());
			}
			session.setAttribute("successMsg", "Awesome! The product was successfully added to the catalog.");
		}else {
			session.setAttribute("errorMsg", "Something Wrong on server while save Product");
		}
		
		return "redirect:/admin/product-list";
	}
	
	/**
	 * Lists all products currently registered in the database catalog.
	 */
	@GetMapping("/product-list")
	public String productList(Model model) {
		model.addAttribute("productList", productService.getAllProducts());
		return "/admin/product/product-list";
	}
	
	/**
	 * Deletes a target product from the store catalog.
	 */
	@GetMapping("/delete-product/{id}")
	public String deleteProduct(@PathVariable("id") long id, HttpSession session) {
		Boolean deleteProduct = productService.deleteProduct(id);
		
		if(deleteProduct) {
			session.setAttribute("successMsg", "Product Deleted Successfully.");
		}else {
			session.setAttribute("errorMsg", "Something Wrong on server while deleting Product");
		}
		return "redirect:/admin/product-list";
		
	}
	
	/**
	 * Displays the product editing form loaded with existing product details and category trees.
	 */
	@GetMapping("/edit-product/{id}")
	public String editProduct(@PathVariable long id,Model model) {
		Product product = productService.getProductById(id);
		model.addAttribute("product",product);
		model.addAttribute("allCategoryList",categoryService.getAllCategories());
		return "/admin/product/edit-product";
	}
	
	/**
	 * Commits edits to a selected product, including discount adjustments and product description changes.
	 */
	@PostMapping("/update-product")
	public String updateProduct(@ModelAttribute Product product, @RequestParam("file") MultipartFile file,
			HttpSession session, Model model) {

		if (product.getDiscount() < 0 || product.getDiscount() > 100) {
			session.setAttribute("errorMsg", "INVALID DISCOUNT!");
		} else {
			Product updateProduct = productService.updateProductById(product, file);
			if (!ObjectUtils.isEmpty(updateProduct)) {
				session.setAttribute("successMsg", "Edited successfully");
			} else {
				session.setAttribute("errorMsg", "Something Wrong on server while deleting Product");
			}
		}

		return "redirect:/admin/product-list";
	}
	
	/**
	 * Retrieves and lists all registered customers (holding ROLE_USER) for audit.
	 */
	@GetMapping("/get-all-users")
	public String getAllUser(Model model) {
		
		List<User> allUsers = userService.getAllUsersByRole("ROLE_USER");
		for (User user : allUsers) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
			String format = formatter.format(user.getCreatedAt());
			model.addAttribute("formattedDateTimeCreatedAt",format);
			
		}
		model.addAttribute("allUsers",allUsers);
		
		return "/admin/users/user-home";
		
	}
	
	/**
	 * Toggles customer profile account state between active and locked/disabled.
	 */
	@GetMapping("/edit-user-status")
	public String editUser(@RequestParam("status") Boolean status, @RequestParam("id") Long id, Model model, HttpSession session) {
		Boolean updateUserStatus = userService.updateUserStatus(status,id);
		if(updateUserStatus == true) {
			session.setAttribute("successMsg", "Edited successfully");
		}
		else {
			session.setAttribute("errorMsg", "Something Wrong on server while Updating User status");
		}
		return "redirect:/admin/get-all-users";
		
	}
	
	@Autowired
	ProductOrderService productOrderService;

	/**
	 * Retrieves and lists all platform administrators (holding ROLE_ADMIN) for audit.
	 */
	@GetMapping("/get-all-admin")
	public String getAllAdmin(Model model) {
		List<User> allAdmins = userService.getAllUsersByRole("ROLE_ADMIN");
		model.addAttribute("allAdmins", allAdmins);
		return "/admin/users/admin-home";
	}

	/**
	 * Retrieves and renders all system-wide customer billing orders in the admin ledger.
	 */
	@GetMapping("/orders")
	public String getAllOrders(Model model) {
		List<ProductOrder> allOrders = productOrderService.getAllOrders();
		model.addAttribute("allOrders", allOrders);
		return "/admin/orders/orders-home";
	}

	/**
	 * Updates the shipping / transaction fulfillment timeline status for a customer order.
	 */
	@PostMapping("/update-order-status")
	public String updateOrderStatus(@RequestParam("id") Long id, @RequestParam("status") String status, HttpSession session) {
		ProductOrder updateOrder = productOrderService.updateOrderStatus(id, status);
		if (updateOrder != null) {
			session.setAttribute("successMsg", "Edited successfully");
		} else {
			session.setAttribute("errorMsg", "Something Wrong on server while Updating Order status");
		}
		return "redirect:/admin/orders";
	}
}
