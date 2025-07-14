package com.hrms.service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.hrms.model.Headphones;
import com.hrms.model.Laptops;
import com.hrms.model.Mobiles;
import com.hrms.model.User;
import com.hrms.repo.HeadphonesRepo;
import com.hrms.repo.LaptopsRepo;
import com.hrms.repo.MobilesRepo;
import com.hrms.repo.UserRepository;


import org.springframework.security.crypto.password.PasswordEncoder;

@Service
public class ExcelRService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LaptopsRepo laptopsRepo;

    @Autowired
    private MobilesRepo mobilesRepo;

    @Autowired
    private HeadphonesRepo headphonesRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Add your methods and logic here, for example:
    public User saveUser(User user) {
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        return userRepository.save(user);
    }

    // Laptops
    public List<Laptops> getLaptops() {
        return laptopsRepo.findAll();
    }
    public Optional<Laptops> getLaptopById(Long pid) {
        return laptopsRepo.findById(pid);
    }
    public Laptops saveLaptop(String pname, int pcost, int pqty, MultipartFile file) throws IOException {
        Laptops laptop = new Laptops();
        laptop.setPname(pname);
        laptop.setPcost(pcost);
        laptop.setPqty(pqty);
        // For now, just set the filename as pimage (implement S3 upload as needed)
        laptop.setPimage(file.getOriginalFilename());
        return laptopsRepo.save(laptop);
    }

    // Mobiles
    public List<Mobiles> getMobiles() {
        return mobilesRepo.findAll();
    }
    public Optional<Mobiles> getMobilesById(Long pid) {
        return mobilesRepo.findById(pid);
    }

    // Headphones
    public List<Headphones> getHeadphones() {
        return headphonesRepo.findAll();
    }
    public Optional<Headphones> getHeadphonesById(Long pid) {
        return headphonesRepo.findById(pid);
    }

    // Razorpay (dummy implementations)
    public String createOrder(int amount, String currency, String receipt) {
        // Implement Razorpay order creation logic here
        return "order_created_dummy_id";
    }
    public boolean verifyPayment(String orderId, String paymentId, String signature) {
        // Implement Razorpay payment verification logic here
        return true;
    }
}
    

