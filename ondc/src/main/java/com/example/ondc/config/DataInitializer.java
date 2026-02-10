package com.example.ondc.config;

import com.example.ondc.entity.*;
import com.example.ondc.enums.OutletType;
import com.example.ondc.enums.SellerAppStatus;
import com.example.ondc.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final VendorRepository vendorRepository;
    private final OutletRepository outletRepository;
    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;
    private final SellerAppRepository sellerAppRepository;

    @Override
    public void run(String... args) {
        if (vendorRepository.count() > 0) {
            log.info("Data already seeded, skipping initialization.");
            return;
        }

        log.info("Seeding demo data...");

        // === Vendors ===
        Vendor vendor1 = vendorRepository.save(Vendor.builder()
                .name("Rajesh Kumar")
                .email("rajesh@freshmart.in")
                .phone("+91-9876543210")
                .businessName("FreshMart Groceries")
                .address("45, MG Road, Koramangala, Bangalore - 560034")
                .build());

        Vendor vendor2 = vendorRepository.save(Vendor.builder()
                .name("Priya Sharma")
                .email("priya@dailyneeds.in")
                .phone("+91-9876543211")
                .businessName("DailyNeeds Express")
                .address("12, Anna Nagar, Chennai - 600040")
                .build());

        // === Outlets ===
        Outlet outlet1 = outletRepository.save(Outlet.builder()
                .name("FreshMart - Koramangala")
                .type(OutletType.STORE)
                .address("45, MG Road, Koramangala")
                .latitude(12.9352)
                .longitude(77.6245)
                .pincode("560034")
                .maxCapacity(50)
                .vendor(vendor1)
                .build());

        Outlet outlet2 = outletRepository.save(Outlet.builder()
                .name("FreshMart Dark Store - HSR")
                .type(OutletType.DARK_STORE)
                .address("78, Sector 2, HSR Layout")
                .latitude(12.9116)
                .longitude(77.6389)
                .pincode("560102")
                .maxCapacity(100)
                .vendor(vendor1)
                .build());

        Outlet outlet3 = outletRepository.save(Outlet.builder()
                .name("DailyNeeds - Anna Nagar")
                .type(OutletType.STORE)
                .address("12, Anna Nagar Main Road")
                .latitude(13.0850)
                .longitude(80.2101)
                .pincode("600040")
                .maxCapacity(40)
                .vendor(vendor2)
                .build());

        // === Products (Vendor 1) ===
        Product p1 = productRepository.save(Product.builder()
                .name("Amul Toned Milk 500ml").sku("MILK-AMUL-500").category("Dairy")
                .price(27.0).unit("packet").vendor(vendor1).build());
        Product p2 = productRepository.save(Product.builder()
                .name("Aashirvaad Atta 5kg").sku("ATTA-AASH-5KG").category("Staples")
                .price(280.0).unit("bag").vendor(vendor1).build());
        Product p3 = productRepository.save(Product.builder()
                .name("Fortune Sunflower Oil 1L").sku("OIL-FORT-1L").category("Cooking Oil")
                .price(145.0).unit("bottle").vendor(vendor1).build());
        Product p4 = productRepository.save(Product.builder()
                .name("Maggi Noodles Pack of 12").sku("NOODLE-MAGGI-12").category("Instant Food")
                .price(120.0).unit("pack").vendor(vendor1).build());
        Product p5 = productRepository.save(Product.builder()
                .name("Onions 1kg").sku("VEG-ONION-1KG").category("Vegetables")
                .price(40.0).unit("kg").vendor(vendor1).build());

        // === Products (Vendor 2) ===
        Product p6 = productRepository.save(Product.builder()
                .name("Tata Tea Gold 500g").sku("TEA-TATA-500").category("Beverages")
                .price(230.0).unit("pack").vendor(vendor2).build());
        Product p7 = productRepository.save(Product.builder()
                .name("Surf Excel Liquid 1L").sku("DET-SURF-1L").category("Household")
                .price(199.0).unit("bottle").vendor(vendor2).build());

        // === Inventory ===
        // Outlet 1 (Koramangala store)
        inventoryRepository.save(Inventory.builder().product(p1).outlet(outlet1).totalStock(200).reorderLevel(30).build());
        inventoryRepository.save(Inventory.builder().product(p2).outlet(outlet1).totalStock(50).reorderLevel(10).build());
        inventoryRepository.save(Inventory.builder().product(p3).outlet(outlet1).totalStock(80).reorderLevel(15).build());
        inventoryRepository.save(Inventory.builder().product(p4).outlet(outlet1).totalStock(150).reorderLevel(20).build());
        inventoryRepository.save(Inventory.builder().product(p5).outlet(outlet1).totalStock(100).reorderLevel(25).build());

        // Outlet 2 (HSR dark store) — some items have low stock
        inventoryRepository.save(Inventory.builder().product(p1).outlet(outlet2).totalStock(500).reorderLevel(50).build());
        inventoryRepository.save(Inventory.builder().product(p2).outlet(outlet2).totalStock(8).reorderLevel(10).build());  // LOW STOCK
        inventoryRepository.save(Inventory.builder().product(p3).outlet(outlet2).totalStock(120).reorderLevel(20).build());
        inventoryRepository.save(Inventory.builder().product(p5).outlet(outlet2).totalStock(5).reorderLevel(15).build());   // LOW STOCK

        // Outlet 3 (Anna Nagar)
        inventoryRepository.save(Inventory.builder().product(p6).outlet(outlet3).totalStock(60).reorderLevel(10).build());
        inventoryRepository.save(Inventory.builder().product(p7).outlet(outlet3).totalStock(3).reorderLevel(5).build());   // LOW STOCK

        // === Seller Apps ===
        sellerAppRepository.save(SellerApp.builder()
                .name("ONDC Buyer App - Paytm")
                .apiEndpoint("https://api.paytm-ondc.in/v1")
                .apiKey("paytm-key-001")
                .status(SellerAppStatus.ACTIVE)
                .responseTimeMs(120L)
                .build());

        sellerAppRepository.save(SellerApp.builder()
                .name("ONDC Buyer App - Magicpin")
                .apiEndpoint("https://api.magicpin-ondc.in/v1")
                .apiKey("magicpin-key-001")
                .status(SellerAppStatus.ACTIVE)
                .responseTimeMs(95L)
                .build());

        sellerAppRepository.save(SellerApp.builder()
                .name("ONDC Buyer App - MyStore")
                .apiEndpoint("https://api.mystore-ondc.in/v1")
                .apiKey("mystore-key-001")
                .status(SellerAppStatus.DEGRADED)
                .responseTimeMs(350L)
                .build());

        log.info("Demo data seeded successfully!");
        log.info("  Vendors: 2");
        log.info("  Outlets: 3");
        log.info("  Products: 7");
        log.info("  Inventory records: 11 (3 low-stock)");
        log.info("  Seller Apps: 3");
    }
}
