package com.pcparts.ec.config;

import com.pcparts.ec.model.Category;
import com.pcparts.ec.model.Product;
import com.pcparts.ec.model.Role;
import com.pcparts.ec.model.User;
import com.pcparts.ec.repository.CategoryRepository;
import com.pcparts.ec.repository.ProductRepository;
import com.pcparts.ec.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @org.springframework.beans.factory.annotation.Value("${ADMIN_PASSWORD:local-dev-only}")
    private String adminPassword;

    @Override
    public void run(String... args) {
        seedAdminUser();
        seedCatalog();
    }

    private void seedAdminUser() {
        if (userRepository.existsByEmail("admin@pcparts.local")) {
            return;
        }
        User admin = User.builder()
                .email("admin@pcparts.local")
                .password(passwordEncoder.encode(adminPassword))
                .name("管理者")
                .role(Role.ADMIN)
                .build();
        userRepository.save(admin);
    }

    private void seedCatalog() {
        if (categoryRepository.count() > 0) {
            return;
        }

        Category cpu = categoryRepository.save(Category.builder().name("CPU").slug("cpu").build());
        Category gpu = categoryRepository.save(Category.builder().name("グラフィックボード").slug("gpu").build());
        Category motherboard = categoryRepository.save(Category.builder().name("マザーボード").slug("motherboard").build());
        Category memory = categoryRepository.save(Category.builder().name("メモリ").slug("memory").build());
        Category storage = categoryRepository.save(Category.builder().name("ストレージ").slug("storage").build());
        Category psu = categoryRepository.save(Category.builder().name("電源ユニット").slug("psu").build());
        Category caseCategory = categoryRepository.save(Category.builder().name("PCケース").slug("case").build());
        Category cooler = categoryRepository.save(Category.builder().name("CPUクーラー").slug("cooler").build());

        List<Product> products = List.of(
                Product.builder().name("Core i7-14700K").manufacturer("Intel").category(cpu)
                        .description("20コア28スレッドの高性能デスクトップCPU")
                        .specs("コア数: 20 / スレッド数: 28 / ベースクロック: 3.4GHz / TDP: 125W")
                        .price(new BigDecimal("58800")).stockQuantity(25).imageUrl("").build(),
                Product.builder().name("Ryzen 7 7800X3D").manufacturer("AMD").category(cpu)
                        .description("3D V-Cache搭載のゲーミング向けCPU")
                        .specs("コア数: 8 / スレッド数: 16 / ベースクロック: 4.2GHz / TDP: 120W")
                        .price(new BigDecimal("62800")).stockQuantity(18).imageUrl("").build(),
                Product.builder().name("GeForce RTX 4070 SUPER").manufacturer("NVIDIA").category(gpu)
                        .description("フルHD/WQHDゲーミングに最適なグラフィックボード")
                        .specs("VRAM: 12GB GDDR6X / 消費電力: 220W")
                        .price(new BigDecimal("99800")).stockQuantity(12).imageUrl("").build(),
                Product.builder().name("Radeon RX 7800 XT").manufacturer("AMD").category(gpu)
                        .description("コストパフォーマンスに優れたグラフィックボード")
                        .specs("VRAM: 16GB GDDR6 / 消費電力: 263W")
                        .price(new BigDecimal("89800")).stockQuantity(10).imageUrl("").build(),
                Product.builder().name("ROG STRIX Z790-E").manufacturer("ASUS").category(motherboard)
                        .description("Intel第14世代CPU対応のハイエンドマザーボード")
                        .specs("ソケット: LGA1700 / チップセット: Z790 / メモリ: DDR5")
                        .price(new BigDecimal("68000")).stockQuantity(8).imageUrl("").build(),
                Product.builder().name("MAG B650 TOMAHAWK").manufacturer("MSI").category(motherboard)
                        .description("AMD Ryzen対応のミドルレンジマザーボード")
                        .specs("ソケット: AM5 / チップセット: B650 / メモリ: DDR5")
                        .price(new BigDecimal("32800")).stockQuantity(15).imageUrl("").build(),
                Product.builder().name("Fury Beast DDR5-5600 32GB").manufacturer("Kingston").category(memory)
                        .description("16GB x2 デュアルチャンネルキット")
                        .specs("容量: 32GB (16GBx2) / 規格: DDR5-5600 / CL: 40")
                        .price(new BigDecimal("14800")).stockQuantity(40).imageUrl("").build(),
                Product.builder().name("Trident Z5 RGB DDR5-6000 32GB").manufacturer("G.Skill").category(memory)
                        .description("高発光ヒートシンク搭載のハイエンドメモリ")
                        .specs("容量: 32GB (16GBx2) / 規格: DDR5-6000 / CL: 30")
                        .price(new BigDecimal("19800")).stockQuantity(22).imageUrl("").build(),
                Product.builder().name("980 PRO 2TB").manufacturer("Samsung").category(storage)
                        .description("PCIe 4.0対応の高速NVMe SSD")
                        .specs("容量: 2TB / インターフェース: PCIe 4.0 x4 / 読込: 7000MB/s")
                        .price(new BigDecimal("22800")).stockQuantity(30).imageUrl("").build(),
                Product.builder().name("SN850X 1TB").manufacturer("Western Digital").category(storage)
                        .description("ゲーミング向け高耐久NVMe SSD")
                        .specs("容量: 1TB / インターフェース: PCIe 4.0 x4 / 読込: 7300MB/s")
                        .price(new BigDecimal("13800")).stockQuantity(35).imageUrl("").build(),
                Product.builder().name("RM850x").manufacturer("Corsair").category(psu)
                        .description("80PLUS Gold認証のフルモジュラー電源")
                        .specs("出力: 850W / 認証: 80PLUS Gold / フルモジュラー")
                        .price(new BigDecimal("21800")).stockQuantity(20).imageUrl("").build(),
                Product.builder().name("SFX-L 750 Platinum").manufacturer("Corsair").category(psu)
                        .description("小型PC向け高効率SFX-L電源")
                        .specs("出力: 750W / 認証: 80PLUS Platinum / フォームファクタ: SFX-L")
                        .price(new BigDecimal("28800")).stockQuantity(10).imageUrl("").build(),
                Product.builder().name("Meshify 2 Compact").manufacturer("Fractal Design").category(caseCategory)
                        .description("エアフロー重視のミドルタワーケース")
                        .specs("対応マザーボード: ATX/mATX/ITX / 前面: メッシュパネル")
                        .price(new BigDecimal("16800")).stockQuantity(14).imageUrl("").build(),
                Product.builder().name("O11 Dynamic EVO").manufacturer("Lian Li").category(caseCategory)
                        .description("水冷構築に人気のデュアルチャンバーケース")
                        .specs("対応マザーボード: ATX/mATX/ITX / 拡張性: 高い")
                        .price(new BigDecimal("24800")).stockQuantity(9).imageUrl("").build(),
                Product.builder().name("NH-D15").manufacturer("Noctua").category(cooler)
                        .description("定番の空冷デュアルタワークーラー")
                        .specs("タイプ: 空冷 / ファン: 140mm x2 / 対応TDP: 220W+")
                        .price(new BigDecimal("13800")).stockQuantity(17).imageUrl("").build(),
                Product.builder().name("Kraken X63").manufacturer("NZXT").category(cooler)
                        .description("280mmラジエーター搭載の簡易水冷クーラー")
                        .specs("タイプ: 簡易水冷 / ラジエーター: 280mm")
                        .price(new BigDecimal("18800")).stockQuantity(11).imageUrl("").build()
        );

        productRepository.saveAll(products);
    }
}
