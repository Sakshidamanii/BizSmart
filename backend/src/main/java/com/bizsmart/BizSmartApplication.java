package com.bizsmart;

import com.bizsmart.models.*;
import com.bizsmart.repositories.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

@SpringBootApplication
public class BizSmartApplication {

    public static void main(String[] args) {
        SpringApplication.run(BizSmartApplication.class, args);
    }

    @Bean
    public CommandLineRunner initData(
            RoleRepository roleRepository,
            UserRepository userRepository,
            CategoryRepository categoryRepository,
            SupplierRepository supplierRepository,
            ProductRepository productRepository,
            CustomerRepository customerRepository,
            ExpenseRepository expenseRepository,
            PasswordEncoder passwordEncoder) {
        return args -> {
            // Seed Roles
            Role ownerRole = roleRepository.findByName(ERole.ROLE_BUSINESS_OWNER)
                    .orElseGet(() -> roleRepository.save(new Role(ERole.ROLE_BUSINESS_OWNER)));
            Role employeeRole = roleRepository.findByName(ERole.ROLE_EMPLOYEE)
                    .orElseGet(() -> roleRepository.save(new Role(ERole.ROLE_EMPLOYEE)));
            Role supplierRole = roleRepository.findByName(ERole.ROLE_SUPPLIER)
                    .orElseGet(() -> roleRepository.save(new Role(ERole.ROLE_SUPPLIER)));
            Role adminRole = roleRepository.findByName(ERole.ROLE_PLATFORM_ADMIN)
                    .orElseGet(() -> roleRepository.save(new Role(ERole.ROLE_PLATFORM_ADMIN)));

            // Seed Users
            if (!userRepository.existsByUsername("owner")) {
                User owner = new User("owner", "owner@bizsmart.in", passwordEncoder.encode("password123"), "Rajesh Sharma (Owner)");
                owner.setRoles(Set.of(ownerRole));
                userRepository.save(owner);
            }

            if (!userRepository.existsByUsername("employee")) {
                User employee = new User("employee", "cashier@bizsmart.in", passwordEncoder.encode("password123"), "Amit Verma (Cashier)");
                employee.setRoles(Set.of(employeeRole));
                userRepository.save(employee);
            }

            if (!userRepository.existsByUsername("supplier")) {
                User supp = new User("supplier", "supplier@itc.in", passwordEncoder.encode("password123"), "Sunil Kumar (ITC Distributor)");
                supp.setRoles(Set.of(supplierRole));
                userRepository.save(supp);
            }

            // Seed Suppliers
            Supplier itc = supplierRepository.findById(1L).orElseGet(() ->
                    supplierRepository.save(new Supplier("ITC Consumer Goods Distribution", "Sunil Kumar", "+91-98200-11223", "itc@distributor.in", "Okhla Phase III, Delhi", new BigDecimal("45000.00"))));
            Supplier tata = supplierRepository.findById(2L).orElseGet(() ->
                    supplierRepository.save(new Supplier("Tata Consumer Products Hub", "Ramesh Patel", "+91-98211-44556", "tata@supply.in", "Sector 18, Gurugram", new BigDecimal("18500.00"))));
            Supplier amul = supplierRepository.findById(3L).orElseGet(() ->
                    supplierRepository.save(new Supplier("Amul Dairy Federation Depot", "Dinesh Rawat", "+91-98100-99887", "amul@depot.in", "Patparganj Industrial Area, Delhi", new BigDecimal("12000.00"))));
            Supplier adani = supplierRepository.findById(4L).orElseGet(() ->
                    supplierRepository.save(new Supplier("Adani Wilmar Edible Oils", "Vikas Gupta", "+91-98999-33221", "sales@adani.in", "Transport Nagar, Delhi", new BigDecimal("28000.00"))));

            // Seed Categories
            Category staples = categoryRepository.findByName("Staples & Grains")
                    .orElseGet(() -> categoryRepository.save(new Category("Staples & Grains", "Atta, rice, pulses, and grains")));
            Category oils = categoryRepository.findByName("Edible Oils & Ghee")
                    .orElseGet(() -> categoryRepository.save(new Category("Edible Oils & Ghee", "Cooking oils and ghee")));
            Category dairy = categoryRepository.findByName("Dairy & Breakfast")
                    .orElseGet(() -> categoryRepository.save(new Category("Dairy & Breakfast", "Milk, butter, and breakfast")));
            Category fmcg = categoryRepository.findByName("FMCG & Packaged Foods")
                    .orElseGet(() -> categoryRepository.save(new Category("FMCG & Packaged Foods", "Noodles, biscuits, and snacks")));

            // Seed Products matching Section 5
            if (productRepository.count() == 0) {
                Product p1 = new Product();
                p1.setSku("GROC-ATT-001");
                p1.setName("Aashirvaad Atta 5kg");
                p1.setCategory(staples);
                p1.setSupplier(itc);
                p1.setPurchasePrice(new BigDecimal("240.00"));
                p1.setSellingPrice(new BigDecimal("280.00"));
                p1.setQuantity(8); // ⚠️ LOW STOCK (< 10)
                p1.setMinStock(10);
                p1.setReorderQuantity(50);
                p1.setExpiryDate(LocalDate.now().plusMonths(6));
                productRepository.save(p1);

                Product p2 = new Product();
                p2.setSku("GROC-OIL-002");
                p2.setName("Fortune Sunlite Sunflower Oil 1L");
                p2.setCategory(oils);
                p2.setSupplier(adani);
                p2.setPurchasePrice(new BigDecimal("135.00"));
                p2.setSellingPrice(new BigDecimal("160.00"));
                p2.setQuantity(14); // ⚠️ LOW STOCK (< 15)
                p2.setMinStock(15);
                p2.setReorderQuantity(60);
                p2.setExpiryDate(LocalDate.now().plusMonths(9));
                productRepository.save(p2);

                Product p3 = new Product();
                p3.setSku("GROC-SLT-003");
                p3.setName("Tata Salt Vacuum Evaporated 1kg");
                p3.setCategory(staples);
                p3.setSupplier(tata);
                p3.setPurchasePrice(new BigDecimal("22.00"));
                p3.setSellingPrice(new BigDecimal("28.00"));
                p3.setQuantity(45);
                p3.setMinStock(20);
                p3.setReorderQuantity(80);
                p3.setExpiryDate(LocalDate.now().plusMonths(18));
                productRepository.save(p3);

                Product p4 = new Product();
                p4.setSku("DAIR-BUT-004");
                p4.setName("Amul Pasteurised Butter 500g");
                p4.setCategory(dairy);
                p4.setSupplier(amul);
                p4.setPurchasePrice(new BigDecimal("245.00"));
                p4.setSellingPrice(new BigDecimal("275.00"));
                p4.setQuantity(6); // ⚠️ LOW STOCK (< 12)
                p4.setMinStock(12);
                p4.setReorderQuantity(40);
                p4.setExpiryDate(LocalDate.now().plusDays(45));
                productRepository.save(p4);
            }

            // Seed Customers with Udhaar/Khata
            if (customerRepository.count() == 0) {
                customerRepository.save(new Customer("Rakesh Gupta (Shop Regular)", "rakesh.gupta@gmail.com", "+91-98111-22334", "Sector 12", "Dwarka, Delhi", new BigDecimal("1850.00")));
                customerRepository.save(new Customer("Priya Sundaram", "priya.s@yahoo.com", "+91-98777-66554", "Mayur Vihar", "Delhi", BigDecimal.ZERO));
                customerRepository.save(new Customer("Anand Mehra (Catering)", "anand.mehra@caterers.in", "+91-98990-11223", "Lajpat Nagar", "Delhi", new BigDecimal("4200.00")));
            }

            // Seed Expenses
            if (expenseRepository.count() == 0) {
                expenseRepository.save(new Expense("Shop Floor Monthly Rent", ExpenseCategory.RENT, new BigDecimal("85000.00"), LocalDate.now(), "Main market commercial space"));
                expenseRepository.save(new Expense("Commercial Electricity Bill", ExpenseCategory.ELECTRICITY, new BigDecimal("24500.00"), LocalDate.now(), "Cooling and lighting"));
                expenseRepository.save(new Expense("Staff Salaries", ExpenseCategory.SALARY, new BigDecimal("65000.00"), LocalDate.now(), "Cashier and helper"));
                expenseRepository.save(new Expense("Wholesale Stock Transport", ExpenseCategory.LOGISTICS, new BigDecimal("18000.00"), LocalDate.now(), "Goods delivery vehicle"));
                expenseRepository.save(new Expense("Packaging & Thermal Rolls", ExpenseCategory.PACKAGING, new BigDecimal("7500.00"), LocalDate.now(), "Paper bags"));
                expenseRepository.save(new Expense("Maintenance & Sanitation", ExpenseCategory.MAINTENANCE, new BigDecimal("10000.00"), LocalDate.now(), "Pest control & repairs"));
            }
        };
    }
}
