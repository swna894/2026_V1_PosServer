package com.swna.server.product.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.swna.server.common.exception.BusinessException;
import com.swna.server.common.exception.ErrorCode;
import com.swna.server.common.exception.ExceptionUtils;
import com.swna.server.product.dto.ProductCreateRequest;
import com.swna.server.product.dto.ProductLabelDto;
import com.swna.server.product.dto.ProductResponse;
import com.swna.server.product.dto.ProductUpdateRequest;
import com.swna.server.product.entity.Product;
import com.swna.server.product.entity.ProductStock;
import com.swna.server.product.repository.ProductRepository;
import com.swna.server.product.repository.ProductStockRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductStockRepository productStockRepository;

    // ==========================================
    // C (Create)
    // ==========================================
    @Transactional
    public ProductResponse createProduct(ProductCreateRequest request) {
        Product product = Product.create(
                request.code(),
                request.description(),
                request.price(),
                request.cost(),
                request.barcode(),
                request.category()
        );

        if (request.priceOld() != null) {
            product.changePrice(request.price());
        }
        if (request.costOld() != null) {
            product.changeCost(request.cost());
        }

        Product savedProduct = productRepository.save(product);

        int maxStock = request.maxStock() > 0 ? request.maxStock() : 100;
        ProductStock stock = ProductStock.create(
                savedProduct,
                request.quantity(),
                request.minStock(),
                maxStock,
                request.minOrderQuantity()
        );
        ProductStock savedStock = productStockRepository.save(stock);

        return ProductResponse.from(savedProduct, savedStock);
    }

    // ==========================================
    // R (Read)
    // ==========================================
    public ProductResponse getProductById(Long id) {
        return productRepository.findProductResponseById(id)
                .orElseThrow(() -> ExceptionUtils.productNotFound(String.valueOf(id)));
    }

    public ProductResponse getProductByBarcode(String barcode) {
        validateBarcode(barcode);
        return productRepository.findProductResponseByBarcode(barcode)
                .orElseThrow(() -> ExceptionUtils.productNotFound(barcode));
    }

    public List<ProductResponse> getAllProducts() {
        return productRepository.findAllProductResponses();
    }

    public List<ProductLabelDto> getProductLabelsWithCompany() {
        List<ProductLabelDto> labels = productRepository.findRandomProductLabelsWithSupplier();
        if (labels == null || labels.isEmpty()) {
            throw ExceptionUtils.productNotFound("No products found in database", "findRandomProductLabelsWithSupplier");
        }
        return labels;
    }

    public List<ProductLabelDto> getProductLabelsWithCompany(int limit) {
        PageRequest pageable = PageRequest.of(0, limit);
        List<ProductLabelDto> labels = productRepository.findRandomProductLabelsWithSupplierLimit(pageable);
        if (labels == null || labels.isEmpty()) {
            throw ExceptionUtils.productNotFound("No products found in database", "findRandomProductLabelsWithSupplier");
        }
        return labels;
    }

    public List<ProductLabelDto> getProductLabelsFromActiveSuppliers() {
        List<ProductLabelDto> labels = productRepository.findRandomProductLabelsFromActiveSuppliers();
        if (labels == null || labels.isEmpty()) {
            throw ExceptionUtils.productNotFound("No products found from active suppliers", "findRandomProductLabelsFromActiveSuppliers");
        }
        return labels;
    }

    public List<ProductLabelDto> getProductLabelsByCompany(String company) {
        if (company == null || company.isBlank()) {
            return List.of();
        }
        return productRepository.findProductLabelsByCompany(company);
    }

    public List<ProductLabelDto> getProductLabelsByAbbrs(List<String> abbrs) {
        if (abbrs == null || abbrs.isEmpty()) {
            return List.of();
        }
        return productRepository.findProductLabelsByAbbrs(abbrs);
    }

    public ProductLabelDto getProductLabelByBarcode(String barcode) {
        validateBarcode(barcode);
        return productRepository.findProductLabelByBarcode(barcode)
                .orElseThrow(() -> ExceptionUtils.productNotFound(barcode));
    }

    public List<ProductResponse> getProductsByAbbr(String abbr) {
        if (abbr == null || abbr.isBlank()) {
            return List.of();
        }
        return productRepository.findProductResponsesByAbbr(abbr);
    }

    // ==========================================
    // U (Update): 단건, 다중, 주문일자 단독 수정
    // ==========================================
    @Transactional
    public ProductResponse updateProduct(Long id, ProductUpdateRequest request) {
        Product product = productRepository.findById(id)
                .filter(Product::isAvailable)
                .orElseThrow(() -> ExceptionUtils.productNotFound(String.valueOf(id)));

        return applyUpdate(product, request);
    }

    @Transactional
    public List<ProductResponse> updateProductsBulk(List<ProductUpdateRequest> requests) {
        if (requests == null || requests.isEmpty()) {
            return List.of();
        }

        List<Long> productIds = requests.stream()
                .map(ProductUpdateRequest::id)
                .toList();

        Map<Long, Product> productMap = productRepository.findAllByIdInAndDeletedFalse(productIds)
                .stream()
                .collect(Collectors.toMap(Product::getId, Function.identity()));

        Map<Long, ProductStock> stockMap = productStockRepository.findAllByProductIdIn(productIds)
                .stream()
                .collect(Collectors.toMap(ps -> ps.getProduct().getId(), Function.identity()));

        List<ProductResponse> responses = new ArrayList<>();

        for (ProductUpdateRequest req : requests) {
            Product product = productMap.get(req.id());
            if (product == null) {
                continue;
            }
            ProductResponse response = applyUpdateWithStockMap(product, req, stockMap.get(product.getId()));
            responses.add(response);
        }

        return responses;
    }

    /**
     * 🔥 주문일자(lastOrderedAt) 단독 변경
     */
    @Transactional
    public ProductResponse updateLastOrderedAt(Long id, LocalDateTime customOrderedAt) {
        Product product = productRepository.findById(id)
                .filter(Product::isAvailable)
                .orElseThrow(() -> ExceptionUtils.productNotFound(String.valueOf(id)));

        ProductStock stock = productStockRepository.findByProduct(product)
                .orElseGet(() -> productStockRepository.save(
                        ProductStock.create(product, 0, 0, 100, 12)
                ));

        if (customOrderedAt != null) {
            stock.updateLastOrderedAt(customOrderedAt);
        } else {
            stock.touchLastOrderedAt();
        }

        return ProductResponse.from(product, stock);
    }

    // ==========================================
    // D (Delete)
    // ==========================================
    @Transactional
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .filter(Product::isAvailable)
                .orElseThrow(() -> ExceptionUtils.productNotFound(String.valueOf(id)));

        product.delete();
    }

    @Transactional
    public int deleteProductsBulk(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return 0;
        }
        return productRepository.deleteAllByIds(ids);
    }

    // ==========================================
    // Internal Helper Methods
    // ==========================================
    private ProductResponse applyUpdate(Product product, ProductUpdateRequest request) {
        updateProductFields(product, request);

        ProductStock stock = productStockRepository.findByProduct(product)
                .orElseGet(() -> productStockRepository.save(
                        ProductStock.create(product, 0, 0, 100, 12)
                ));

        updateStockFields(product.getId(), stock, request);
        return ProductResponse.from(product, stock);
    }

    private ProductResponse applyUpdateWithStockMap(Product product, ProductUpdateRequest request, ProductStock stock) {
        updateProductFields(product, request);

        if (stock == null) {
            stock = productStockRepository.save(ProductStock.create(product, 0, 0, 100, 12));
        }

        updateStockFields(product.getId(), stock, request);
        return ProductResponse.from(product, stock);
    }

    private void updateProductFields(Product product, ProductUpdateRequest request) {
        if (request.description() != null) {
            product.changeName(request.description());
        }
        if (request.price() != null) {
            product.changePrice(request.price());
        }
        if (request.cost() != null) {
            product.changeCost(request.cost());
        }
        if (request.category() != null) {
            product.changeCategory(request.category());
        }
    }

    private void updateStockFields(Long productId, ProductStock stock, ProductUpdateRequest request) {
        if (request.quantity() != null || request.minStock() != null || request.maxStock() != null || request.minOrderQuantity() != null) {
            
            int newMinStock = request.minStock() != null ? request.minStock() : stock.getMinStock();
            int newMaxStock = request.maxStock() != null ? request.maxStock() : stock.getMaxStock();
            int newMinOrderQty = request.minOrderQuantity() != null ? request.minOrderQuantity() : stock.getMinOrderQuantity();

            // 엔티티 내부 메서드를 통해 값 변경 (Dirty Checking 적용)
            stock.updateSettings(newMinStock, newMaxStock, newMinOrderQty);

            if (request.quantity() != null) {
                int diff = request.quantity() - stock.getQuantity();
                if (diff > 0) {
                    stock.increase(diff);
                } else if (diff < 0) {
                    stock.decrease(Math.abs(diff));
                }
            }
        }
    }

    private void validateBarcode(String barcode) {
        if (barcode == null || barcode.isEmpty()) {
            throw BusinessException.builder(ErrorCode.INVALID_INPUT)
                    .message("Barcode cannot be empty")
                    .detail("field", "barcode")
                    .build();
        }
    }
}