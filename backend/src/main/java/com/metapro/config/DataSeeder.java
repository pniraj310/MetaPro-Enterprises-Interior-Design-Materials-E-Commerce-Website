package com.metapro.config;

import com.metapro.model.*;
import com.metapro.repository.*;
import com.metapro.service.AuthService;
import com.metapro.service.BusinessSettingsService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataSeeder implements CommandLineRunner {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final AuthService authService;
    private final BusinessSettingsService settingsService;

    public DataSeeder(ProductRepository productRepository,
                      CategoryRepository categoryRepository,
                      AuthService authService,
                      BusinessSettingsService settingsService) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.authService = authService;
        this.settingsService = settingsService;
    }

    @Override
    public void run(String... args) {
        authService.ensureDefaultAdmin();
        settingsService.getSettings();

        seedCategories();
        seedProducts();
    }

    private void seedCategories() {
        if (categoryRepository.count() == 0) {
            List<Category> categories = List.of(
                    new Category("cat-fasteners-screws", "Fasteners & Screws", "fasteners-screws",
                            "Black phosphated twinfast drywall screws, self-drilling fasteners and framing screws.",
                            "/assets/images/material_fasteners_anchors_1790919426419.jpg", true),
                    new Category("cat-anchor-bolts", "Anchor Bolts & Hardware", "anchor-bolts-hardware",
                            "Torque-controlled sleeve expansion anchor bolts, GI ceiling channels, and concealed mounting clips.",
                            "/assets/images/material_fasteners_anchors_1790919426419.jpg", true),
                    new Category("cat-pvc-wall-panels", "PVC Wall Panels", "pvc-wall-panels",
                            "Moisture-resistant stone-core PVC wall cladding sheets with UV marble and satin finishes.",
                            "/assets/images/material_pvc_marble_ceiling_1790919414453.jpg", true),
                    new Category("cat-fluted-panels", "Fluted Panels", "fluted-panels",
                            "Linear charcoal and warm wood-grain slatted fluted panels that bring architectural depth.",
                            "/assets/images/material_fluted_wpc_panels_1790919395083.jpg", true),
                    new Category("cat-wpc-panels", "WPC Panels", "wpc-panels",
                            "Co-extruded wood-plastic composite louvers suited for interior elevations, soffits, and facades.",
                            "/assets/images/material_fluted_wpc_panels_1790919395083.jpg", true),
                    new Category("cat-ceiling-panels", "Interior Ceiling Panels", "interior-ceiling-panels",
                            "Micro-perforated acoustic ceiling tiles and T-grid suspension systems for commercial & home interiors.",
                            "/assets/images/material_pvc_marble_ceiling_1790919414453.jpg", true),
                    new Category("cat-decorative-panels", "Decorative Wall Panels", "decorative-wall-panels",
                            "Sculptural 3D plant-fiber and textured relief panels ready for custom matte or satin finishes.",
                            "/assets/images/material_pvc_marble_ceiling_1790919414453.jpg", true),
                    new Category("cat-installation-tools", "Installation Tools & Accessories", "installation-tools-accessories",
                            "Panel adhesives, joining profiles, snap-line chalk reels, and site alignment accessories.",
                            "/assets/images/adhesives_sealants_tools_1790924264227.jpg", true)
            );
            categoryRepository.saveAll(categories);
        }
    }

    private void seedProducts() {
        if (productRepository.count() == 0) {
            // Helper method to build products
            List<Product> products = List.of(
                    buildProduct("mp-prod-001", "WPC Fluted Panel – Natural Oak", "wpc-fluted-panel-natural-oak",
                            "Linear slatted architectural fluted wall panels crafted from high-density moisture-resistant polymer in natural oak wood finish.",
                            "Fluted Panels", 145.0, "sq ft", "Available", "MP-FLT-NOAK", 120,
                            "/assets/images/material_fluted_wpc_panels_1790919395083.jpg",
                            List.of("/assets/images/material_fluted_wpc_panels_1790919395083.jpg", "/assets/images/hero_showroom_interiors_1790919377238.jpg"),
                            List.of(new Specification("Material", "High-Density Co-Extruded WPC Polymer"), new Specification("Finish", "Natural Oak Grain")),
                            List.of("Feature Walls", "TV Units", "Partitions"), true),

                    buildProduct("mp-prod-002", "WPC Fluted Panel – Walnut", "wpc-fluted-panel-walnut",
                            "Rich warm walnut fluted louver panels engineered for acoustic softening and linear wall geometry.",
                            "Fluted Panels", 155.0, "sq ft", "Available", "MP-FLT-WAL", 100,
                            "/assets/images/material_fluted_wpc_panels_1790919395083.jpg",
                            List.of("/assets/images/material_fluted_wpc_panels_1790919395083.jpg"),
                            List.of(new Specification("Material", "WPC Polymer Core"), new Specification("Finish", "Dark Walnut")),
                            List.of("Living Room", "Executive Office", "Corridors"), false),

                    buildProduct("mp-prod-003", "WPC Fluted Panel – Charcoal", "wpc-fluted-panel-charcoal",
                            "Architectural matte charcoal slatted fluted panels for modern, high-contrast interior backdrops.",
                            "Fluted Panels", 150.0, "sq ft", "Available", "MP-FLT-CH", 90,
                            "/assets/images/material_fluted_wpc_panels_1790919395083.jpg",
                            List.of("/assets/images/material_fluted_wpc_panels_1790919395083.jpg"),
                            List.of(new Specification("Material", "Charcoal Polymer"), new Specification("Finish", "Matte Charcoal")),
                            List.of("TV Backdrops", "Reception Lobbies", "Bedrooms"), false),

                    buildProduct("mp-prod-004", "WPC Fluted Panel – Teak", "wpc-fluted-panel-teak",
                            "Classic golden teak wood finish fluted panels with tongue-and-groove interlocking channels.",
                            "Fluted Panels", 160.0, "sq ft", "Available", "MP-FLT-TEAK", 80,
                            "/assets/images/material_fluted_wpc_panels_1790919395083.jpg",
                            List.of("/assets/images/material_fluted_wpc_panels_1790919395083.jpg"),
                            List.of(new Specification("Material", "WPC Wood Composite"), new Specification("Finish", "Golden Teak")),
                            List.of("Balconies", "Dining Areas", "Feature Walls"), false),

                    buildProduct("mp-prod-005", "PVC Marble Wall Panel – White Carrara", "pvc-marble-wall-panel-white-carrara",
                            "Full-height stone-core PVC wall cladding sheet with UV-cured White Carrara marble pattern.",
                            "PVC Wall Panels", 85.0, "sq ft", "Available", "MP-PVC-CAR", 140,
                            "/assets/images/material_pvc_marble_ceiling_1790919414453.jpg",
                            List.of("/assets/images/material_pvc_marble_ceiling_1790919414453.jpg"),
                            List.of(new Specification("Composition", "Virgin PVC + Calcium Carbonate Core"), new Specification("Finish", "UV High Gloss Carrara")),
                            List.of("Living Accent Walls", "Lift Lobbies", "Vanity Zones"), false),

                    buildProduct("mp-prod-006", "PVC Marble Wall Panel – Grey Stone", "pvc-marble-wall-panel-grey-stone",
                            "Contemporary grey stone texture PVC cladding panel with high scratch and water resistance.",
                            "PVC Wall Panels", 90.0, "sq ft", "Available", "MP-PVC-GRY", 110,
                            "/assets/images/material_pvc_marble_ceiling_1790919414453.jpg",
                            List.of("/assets/images/material_pvc_marble_ceiling_1790919414453.jpg"),
                            List.of(new Specification("Composition", "SPC PVC Core"), new Specification("Finish", "Satin Grey Stone")),
                            List.of("Offices", "Corridors", "Bathrooms"), false),

                    buildProduct("mp-prod-007", "PVC Wooden Finish Wall Panel – Walnut", "pvc-wooden-finish-wall-panel-walnut",
                            "Lightweight moisture-proof PVC wall sheet featuring realistic warm walnut timber grain.",
                            "PVC Wall Panels", 95.0, "sq ft", "Available", "MP-PVC-WAL", 95,
                            "/assets/images/material_pvc_marble_ceiling_1790919414453.jpg",
                            List.of("/assets/images/material_pvc_marble_ceiling_1790919414453.jpg"),
                            List.of(new Specification("Finish", "Matte Walnut Timber")),
                            List.of("Bedrooms", "Dining Rooms", "Retail Stores"), false),

                    buildProduct("mp-prod-008", "PVC High Gloss Wall Panel – Beige", "pvc-high-gloss-wall-panel-beige",
                            "UV-cured warm beige gloss marble sheet providing seamless light-reflective wall surfaces.",
                            "PVC Wall Panels", 80.0, "sq ft", "Available", "MP-PVC-BGE", 130,
                            "/assets/images/material_pvc_marble_ceiling_1790919414453.jpg",
                            List.of("/assets/images/material_pvc_marble_ceiling_1790919414453.jpg"),
                            List.of(new Specification("Finish", "UV Mirror Gloss Beige")),
                            List.of("Foyers", "Showrooms", "Residential Walls"), false),

                    buildProduct("mp-prod-009", "WPC Wall Panel – Teak Wood Finish", "wpc-wall-panel-teak-wood-finish",
                            "Heavy-duty exterior & interior wood-plastic composite deep-channel wall panel in teak finish.",
                            "WPC Panels", 135.0, "sq ft", "Available", "MP-WPC-TEAK", 100,
                            "/assets/images/material_fluted_wpc_panels_1790919395083.jpg",
                            List.of("/assets/images/material_fluted_wpc_panels_1790919395083.jpg"),
                            List.of(new Specification("Composition", "60% Wood Fiber + 35% HDPE")),
                            List.of("Facade Cladding", "Soffits", "Pergolas"), false),

                    buildProduct("mp-prod-010", "WPC Wall Panel – Dark Walnut", "wpc-wall-panel-dark-walnut",
                            "Weatherproof co-extruded WPC composite cladding panel in dark walnut shade.",
                            "WPC Panels", 140.0, "sq ft", "Available", "MP-WPC-DWAL", 85,
                            "/assets/images/material_fluted_wpc_panels_1790919395083.jpg",
                            List.of("/assets/images/material_fluted_wpc_panels_1790919395083.jpg"),
                            List.of(new Specification("Composition", "HDPE + Hardwood Flour")),
                            List.of("Terrace Walls", "Retail Fronts", "Feature Walls"), false),

                    buildProduct("mp-prod-011", "WPC Decorative Panel – Stone Grey", "wpc-decorative-panel-stone-grey",
                            "Modern stone grey WPC composite architectural panel designed for dry-wall installation.",
                            "WPC Panels", 130.0, "sq ft", "Available", "MP-WPC-SGRY", 90,
                            "/assets/images/material_fluted_wpc_panels_1790919395083.jpg",
                            List.of("/assets/images/material_fluted_wpc_panels_1790919395083.jpg"),
                            List.of(new Specification("Finish", "Brushed Stone Grey")),
                            List.of("Office Partitions", "Building Lobbies"), false),

                    buildProduct("mp-prod-012", "PVC Ceiling Panel – White Matte", "pvc-ceiling-panel-white-matte",
                            "Clean, lightweight moisture-proof hollow PVC false ceiling planks with interlocking edges.",
                            "Interior Ceiling Panels", 55.0, "sq ft", "Available", "MP-CLG-WMAT", 200,
                            "/assets/images/material_pvc_marble_ceiling_1790919414453.jpg",
                            List.of("/assets/images/material_pvc_marble_ceiling_1790919414453.jpg"),
                            List.of(new Specification("Finish", "Monolithic Matte White")),
                            List.of("Bathrooms", "Kitchens", "Commercial Corridors"), false),

                    buildProduct("mp-prod-013", "PVC Ceiling Panel – Wood Grain", "pvc-ceiling-panel-wood-grain",
                            "Warm wood-grain architectural PVC false ceiling panel engineered for residential interiors.",
                            "Interior Ceiling Panels", 70.0, "sq ft", "Available", "MP-CLG-WGRN", 150,
                            "/assets/images/material_pvc_marble_ceiling_1790919414453.jpg",
                            List.of("/assets/images/material_pvc_marble_ceiling_1790919414453.jpg"),
                            List.of(new Specification("Finish", "Natural Timber Texture")),
                            List.of("Living Room Ceilings", "Balconies"), false),

                    buildProduct("mp-prod-014", "PVC Ceiling Panel – Marble Finish", "pvc-ceiling-panel-marble-finish",
                            "Glossy Italian marble vein PVC ceiling panel for elegant overhead lighting designs.",
                            "Interior Ceiling Panels", 75.0, "sq ft", "Available", "MP-CLG-MARB", 140,
                            "/assets/images/material_pvc_marble_ceiling_1790919414453.jpg",
                            List.of("/assets/images/material_pvc_marble_ceiling_1790919414453.jpg"),
                            List.of(new Specification("Finish", "Gloss Marble Coat")),
                            List.of("Dining Ceilings", "Lobbies"), false),

                    buildProduct("mp-prod-015", "Decorative 3D Wall Panel – Concrete Texture", "decorative-3d-wall-panel-concrete-texture",
                            "Molded geometric 3D relief decorative wall tiles creating sharp shadow lines and raw concrete visual.",
                            "Decorative Wall Panels", 110.0, "sq ft", "Available", "MP-DEC-3DCON", 80,
                            "/assets/images/material_pvc_marble_ceiling_1790919414453.jpg",
                            List.of("/assets/images/material_pvc_marble_ceiling_1790919414453.jpg"),
                            List.of(new Specification("Material", "High-Density Plant Fiber Composite")),
                            List.of("Cafe Interiors", "Living Room Feature Walls"), false),

                    buildProduct("mp-prod-016", "Decorative Wall Panel – Travertine Finish", "decorative-wall-panel-travertine-finish",
                            "Warm limestone travertine finish sculptural wall panel for understated luxury.",
                            "Decorative Wall Panels", 125.0, "sq ft", "Available", "MP-DEC-TRAV", 70,
                            "/assets/images/material_pvc_marble_ceiling_1790919414453.jpg",
                            List.of("/assets/images/material_pvc_marble_ceiling_1790919414453.jpg"),
                            List.of(new Specification("Finish", "Natural Travertine Pores")),
                            List.of("Master Bedrooms", "Studios"), false),

                    buildProduct("mp-prod-017", "Decorative Wall Panel – Sandstone Texture", "decorative-wall-panel-sandstone-texture",
                            "Tactile sandstone texture architectural wall panel paintable in any interior emulsion.",
                            "Decorative Wall Panels", 115.0, "sq ft", "Available", "MP-DEC-SAND", 75,
                            "/assets/images/material_pvc_marble_ceiling_1790919414453.jpg",
                            List.of("/assets/images/material_pvc_marble_ceiling_1790919414453.jpg"),
                            List.of(new Specification("Finish", "Fine Grain Sandstone")),
                            List.of("Retail Showrooms", "Lounges"), false),

                    buildProduct("mp-prod-018", "Self Drilling Screw – 25mm", "self-drilling-screw-25mm",
                            "Zinc-plated hardened steel self-drilling screws with tek point for light metal studs and framing.",
                            "Fasteners & Screws", 180.0, "box", "Available", "MP-FS-SD25", 300,
                            "/assets/images/material_fasteners_anchors_1790919426419.jpg",
                            List.of("/assets/images/material_fasteners_anchors_1790919426419.jpg"),
                            List.of(new Specification("Material", "Hardened Carbon Steel"), new Specification("Quantity", "500 pcs/box")),
                            List.of("GI Stud Framing", "Panel Installation"), false),

                    buildProduct("mp-prod-019", "Drywall Screw – 32mm", "drywall-screw-32mm",
                            "Anti-corrosive black phosphated twinfast bugle head drywall screws for gypsum partition boards.",
                            "Fasteners & Screws", 220.0, "box", "Available", "MP-FS-DW32", 350,
                            "/assets/images/material_fasteners_anchors_1790919426419.jpg",
                            List.of("/assets/images/material_fasteners_anchors_1790919426419.jpg"),
                            List.of(new Specification("Coating", "Black Phosphate"), new Specification("Quantity", "1000 pcs/box")),
                            List.of("Gypsum Drywall", "False Ceilings"), false),

                    buildProduct("mp-prod-020", "Wood Screw – 40mm", "wood-screw-40mm",
                            "Yellow zinc passivated countersunk wood screws with sharp deep threads for plywood and timber battens.",
                            "Fasteners & Screws", 250.0, "box", "Available", "MP-FS-WD40", 250,
                            "/assets/images/material_fasteners_anchors_1790919426419.jpg",
                            List.of("/assets/images/material_fasteners_anchors_1790919426419.jpg"),
                            List.of(new Specification("Coating", "Yellow Zinc"), new Specification("Quantity", "500 pcs/box")),
                            List.of("Carpentry", "Wood Furring"), false),

                    buildProduct("mp-prod-021", "Expansion Anchor Bolt – M8", "expansion-anchor-bolt-m8",
                            "Carbon steel sleeve expansion anchor bolts for secure fixing into solid concrete and brickwork.",
                            "Anchor Bolts & Hardware", 160.0, "pack", "Available", "MP-ANC-M8", 220,
                            "/assets/images/material_fasteners_anchors_1790919426419.jpg",
                            List.of("/assets/images/material_fasteners_anchors_1790919426419.jpg"),
                            List.of(new Specification("Mechanism", "Sleeve Expansion Pin Bolt"), new Specification("Quantity", "50 pcs/pack")),
                            List.of("Concrete Ceilings", "Heavy Channel Anchoring"), false),

                    buildProduct("mp-prod-022", "Heavy Duty Anchor Bolt – M10", "heavy-duty-anchor-bolt-m10",
                            "High-tensile yellow zinc M10 expansion anchor bolts for structural ceiling and heavy framework mounting.",
                            "Anchor Bolts & Hardware", 240.0, "pack", "Available", "MP-ANC-M10", 180,
                            "/assets/images/material_fasteners_anchors_1790919426419.jpg",
                            List.of("/assets/images/material_fasteners_anchors_1790919426419.jpg"),
                            List.of(new Specification("Mechanism", "Heavy Duty Torque Expansion"), new Specification("Quantity", "50 pcs/pack")),
                            List.of("Structural Framing", "HVAC Supports"), false),

                    buildProduct("mp-prod-023", "Wall Plug Set – Assorted", "wall-plug-set-assorted",
                            "Virgin nylon expansion wall plugs with anti-rotation ribs for hollow and solid masonry walls.",
                            "Anchor Bolts & Hardware", 120.0, "pack", "Available", "MP-ANC-PLUG", 300,
                            "/assets/images/material_fasteners_anchors_1790919426419.jpg",
                            List.of("/assets/images/material_fasteners_anchors_1790919426419.jpg"),
                            List.of(new Specification("Material", "Engineered Nylon"), new Specification("Quantity", "100 pcs/pack")),
                            List.of("Lightweight Fixtures", "Fittings"), false),

                    buildProduct("mp-prod-024", "Panel Installation Adhesive", "panel-installation-adhesive",
                            "Instant-grab hybrid polymer construction adhesive formulated for direct bonding of PVC marble and WPC panels.",
                            "Installation Tools & Accessories", 180.0, "tube", "Available", "MP-TL-ADH", 200,
                            "/assets/images/adhesives_sealants_tools_1790924264227.jpg",
                            List.of("/assets/images/adhesives_sealants_tools_1790924264227.jpg"),
                            List.of(new Specification("Volume", "300 ml Cartridge"), new Specification("Bond Type", "Zero-Sag Instant Grab")),
                            List.of("Wall Panel Bonding", "Trim Fixing"), false),

                    buildProduct("mp-prod-025", "PVC Panel Joining Profile", "pvc-panel-joining-profile",
                            "H-profile PVC joint connector strip for seamless visual alignment between two panel sheets.",
                            "Installation Tools & Accessories", 45.0, "piece", "Available", "MP-TL-JPROF", 250,
                            "/assets/images/adhesives_sealants_tools_1790924264227.jpg",
                            List.of("/assets/images/adhesives_sealants_tools_1790924264227.jpg"),
                            List.of(new Specification("Length", "8 ft (2440 mm)")),
                            List.of("Panel Sheet Transitions"), false),

                    buildProduct("mp-prod-026", "Panel Corner Finishing Profile", "panel-corner-finishing-profile",
                            "L-shaped outer corner guard and internal corner trim for clean panel edge finishing.",
                            "Installation Tools & Accessories", 55.0, "piece", "Available", "MP-TL-CORN", 250,
                            "/assets/images/adhesives_sealants_tools_1790924264227.jpg",
                            List.of("/assets/images/adhesives_sealants_tools_1790924264227.jpg"),
                            List.of(new Specification("Length", "8 ft (2440 mm)")),
                            List.of("External Corner Edging", "Window Reveals"), false)
            );
            productRepository.saveAll(products);
        }
    }

    private Product buildProduct(String id, String name, String slug, String desc,
                                 String cat, Double price, String unit, String avail, String sku, Integer stock,
                                 String img, List<String> imgs, List<Specification> specs, List<String> apps, boolean featured) {
        Product p = new Product();
        p.setId(id);
        p.setName(name);
        p.setSlug(slug);
        p.setDescription(desc);
        p.setCategory(cat);
        p.setPrice(price);
        p.setSalePrice(price);
        p.setUnit(unit);
        p.setAvailability(avail);
        p.setSku(sku);
        p.setStock(stock);
        p.setImage(img);
        p.setImages(imgs);
        p.setSpecifications(specs);
        p.setApplications(apps);
        p.setFeatured(featured);
        p.setActive(true);
        return p;
    }
}
