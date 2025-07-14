
package com.hrms.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.hrms.model.Headphones;
import com.hrms.model.Laptops;
import com.hrms.model.Mobiles;
import com.hrms.model.User;
import com.hrms.repo.UserRepository;
import com.hrms.service.ExcelRService;
import com.hrms.util.JwtUtil;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
public class ExcelRController {

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private JwtUtil jwtUtil;
	
	@Autowired
	private ExcelRService excelRService;
	
	@Autowired
	private PasswordEncoder passwordEncoder;

	// Google OAuth login/register endpoint
@PostMapping("/auth/google")
public ResponseEntity<Map<String, String>> googleAuth(@RequestBody Map<String, String> payload) {
	try {
		String credential = payload.get("credential");
		// Decode JWT (Google ID token) to extract email and name
		String[] parts = credential.split("\\.");
		if (parts.length != 3) throw new IllegalArgumentException("Invalid JWT");
		String bodyJson = new String(java.util.Base64.getUrlDecoder().decode(parts[1]));
		com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
		Map<String, Object> body = mapper.readValue(bodyJson, Map.class);
		String email = (String) body.get("email");
		String name = (String) body.get("name");

		if (email == null || email.isEmpty()) throw new IllegalArgumentException("No email in Google token");

		Optional<User> userOpt = userRepository.findByUsername(email);
		User user;
		if (userOpt.isPresent()) {
			user = userOpt.get();
		} else {
			user = new User();
			user.setUsername(email);
			user.setPassword(passwordEncoder.encode(java.util.UUID.randomUUID().toString())); // random password
			user.setRole("ROLE_EMPLOYEE"); // default role
			user.setName(name);
			userRepository.save(user);
		}
		// Always update name if changed
		if (name != null && (user.getName() == null || !user.getName().equals(name))) {
			user.setName(name);
			userRepository.save(user);
		}
		String token = jwtUtil.generateToken(email);
		Map<String, String> response = new HashMap<>();
		response.put("login", "success");
		response.put("token", token);
		response.put("role", user.getRole());
		response.put("username", user.getUsername());
		response.put("name", user.getName());
		return ResponseEntity.ok(response);
	} catch (Exception e) {
		Map<String, String> response = new HashMap<>();
		response.put("login", "fail");
		response.put("error", e.getMessage());
		return ResponseEntity.status(401).body(response);
	}
}
	// FILE: ExcelRController.java (Only login() method shown here for brevity)
	@PostMapping("/login")
	public ResponseEntity<Map<String, String>> login(@RequestBody Map<String, String> loginData) {
		String username = loginData.get("username");
		String password = loginData.get("password");

		Optional<User> user = userRepository.findByUsername(username);

		if (user.isPresent() && passwordEncoder.matches(password, user.get().getPassword())) {
			Map<String, String> response = new HashMap<>();
			String token = jwtUtil.generateToken(username);
			response.put("login", "success");
			response.put("token", token);
			response.put("role", user.get().getRole());
			return ResponseEntity.ok(response);
		} else {
			Map<String, String> response1 = new HashMap<>();
			response1.put("login", "fail");
			return ResponseEntity.status(401).body(response1);
		}
	}

//	
//	@PostMapping("/login")
//	public ResponseEntity<Map<String, String>> login(@RequestBody Map<String, String> loginData){
//		//read username & password from loginData
//		String username = loginData.get("username");
//		String password = loginData.get("password");
//		//compare react username with database
//		Optional<User> user = userRepository.findByUsername(username);
//		if(user.isPresent() && user.get().getPassword().equals(password)) {
//			Map<String, String> response = new HashMap<>();
//			String token = jwtUtil.generateToken(username);
//			response.put("login", "success");
//			response.put("token", token);
//			response.put("role", user.get().getRole());
//			return ResponseEntity.ok(response);
//		}else {
//			Map<String, String> response1 = new HashMap<>();
//			response1.put("login", "fail");
//			return ResponseEntity.status(401).body(response1);
//		}
//	}
//	
	
	
	@GetMapping("/user/laptops")
	public List<Laptops> getLatops() {
		return excelRService.getLaptops();
	}
	
	@GetMapping("/user/mobiles")
	public List<Mobiles> getMobiles() {
		return excelRService.getMobiles();
	}
	
	@GetMapping("/user/headphones")
	public List<Headphones> getHeadphones() {
		return excelRService.getHeadphones();
	}
	
	@GetMapping("/user/laptops/{pid}")
	public Optional<Laptops> getSingleLaptop(@PathVariable Long pid){
		return excelRService.getLaptopById(pid);
	}
	
	@GetMapping("/user/mobiles/{pid}")
	public Optional<Mobiles> getSingleMobile(@PathVariable Long pid){
		return excelRService.getMobilesById(pid);
	}
	
	@GetMapping("/user/headphones/{pid}")
	public Optional<Headphones> getSingleHeadphone(@PathVariable Long pid){
		return excelRService.getHeadphonesById(pid);
	}
	
	
	@PostMapping("/admin/upload/laptops")
	public ResponseEntity<?> uploadLaptops(@RequestParam String pname,
								@RequestParam int pqty,
								@RequestParam int pcost,
								@RequestParam MultipartFile file) {
		 if (pname == null || pname.isEmpty() || pcost <= 0 || file == null || file.isEmpty() || pqty<=0) {
			 return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid input parameters");
		 }else {
			 Laptops savedLaptop = null;
			try {
				savedLaptop = excelRService.saveLaptop(pname, pcost, pqty, file);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return ResponseEntity.ok(savedLaptop);
		 }
	}
	
	@PostMapping("/admin/upload/mobiles")
	public String uploadMobiles() {
		return "admin will upload mobiles soon....";
	}
	
	@PostMapping("/admin/upload/headphones")
	public String uploadHeadphones() {
		return "admin will upload headphones soon....";
	}
	
	@PostMapping("/admin/register")
	public User register(@RequestBody User user) {
		return excelRService.saveUser(user);
	}
	
	
	/*
	 * razorpay
	 */
	@PostMapping("/create-order")
	public ResponseEntity<?> createOrder(@RequestBody Map<String, Object> data) {
		try {
			int amount = (int) data.get("amount");
			String currency = (String) data.get("currency");
			String receipt = (String) data.get("receipt");

			String order = excelRService.createOrder(amount, currency, receipt);
			return ResponseEntity.ok(order);
		} catch (Exception e) {
			return ResponseEntity.badRequest().body("Failed to create order");
		}
	}
	@PostMapping("/verify-payment")
	public ResponseEntity<?> verifyPayment(@RequestBody Map<String, String> data) {
		String orderId = data.get("razorpay_order_id");
		String paymentId = data.get("razorpay_payment_id");
		String signature = data.get("razorpay_signature");

		boolean isValid = excelRService.verifyPayment(orderId, paymentId, signature);

		if (isValid) {
			return ResponseEntity.ok("Payment Verified");
		} else {
			return ResponseEntity.badRequest().body("Payment Verification Failed");
		}
	}
	
	
	
	
}
