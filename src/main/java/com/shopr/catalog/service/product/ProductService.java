package com.shopr.catalog.service.product;

import com.shopr.catalog.dtos.ImageDto;
import com.shopr.catalog.dtos.ProductDto;
import com.shopr.catalog.exceptions.AlreadyExistException;
import com.shopr.catalog.exceptions.ResourceNotFoundException;
import com.shopr.catalog.model.Brand;
import com.shopr.catalog.model.Category;
import com.shopr.catalog.model.Product;
import com.shopr.catalog.model.ProductImage;
import com.shopr.catalog.repo.BrandRepository;
import com.shopr.catalog.repo.CategoryRepository;
import com.shopr.catalog.repo.ImageRepository;
import com.shopr.catalog.repo.ProductRepository;
import com.shopr.catalog.request.AddProductRequest;
import com.shopr.catalog.response.ProductPageResponse;
import com.shopr.catalog.service.image.IImageService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.modelmapper.ModelMapper;

import java.sql.SQLException;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService implements IProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ImageRepository imageRepository;
    private final BrandRepository brandRepository;
    private final IImageService imageService;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public ProductDto addProduct(AddProductRequest request )  {
        //check if category is found in the db
        //if yes ,set it as the new product category
        //if not, create a new category and
        // set it as the new product category
        //create category
        Brand brand = brandRepository.findByName(request.getBrand())
                .orElseGet(()->{
                    Brand newBrand = new Brand(request.getBrand());
                    return brandRepository.save(newBrand);
                });
        if(product_exist(request.getName(), brand)){
            throw  new AlreadyExistException(request.getBrand() + " " + request.getName() + " already exists,you may update the product instead!");
        }

        Category category = Optional.ofNullable(categoryRepository.findByName(request.getCategory()))
                .orElseGet(()->{
                    Category newCategory = new Category(request.getCategory());
                    return categoryRepository.save(newCategory);
                });
       // request.setCategory(category);
        //save the images
        Product product = null;
        try {
            product = productRepository.save(createProduct(request, category, brand));
           List<ProductImage> images =  imageService.saveImages(request.getFiles(),product,request.getThumbnailIndex());
           product.setImages(images);

        } catch (ResourceNotFoundException e) {
            throw new ResourceNotFoundException("Error while saving the product to database");
        }
        return convertToDto(product);

    }

    @Override
    public Product getProductById(Long id) {
        return productRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("Product not found"));
    }
    //get all the products
    @Override
    public ProductPageResponse getAllProducts(int pageNo, int recordCount) {
        Pageable pageable = PageRequest.of(pageNo,recordCount);
        Page<Product> productsPage =  productRepository.findAll(pageable);
        List<Product> products = productsPage.get().toList();
        List<ProductDto> dtos = products.stream()
                   .map(this::attachThumbnailImage)
                   .map(this::convertToDto)
                   .toList();
        return new ProductPageResponse(dtos, productsPage.isLast());

    }
 //attaches thumbnail to each product
   public Product  attachThumbnailImage(Product product) {
            ProductImage thumbnail = product.getImages().stream()
                    .filter(ProductImage::isThumbnail)
                    .findFirst()
                    .orElse(null);
            product.getImages().clear();
            product.getImages().add(thumbnail);
            return product;
    }


    private Boolean product_exist(String name,Brand brand){
        return productRepository.existsByNameAndBrand(name, brand);
    }

    private Product createProduct(AddProductRequest request, Category category, Brand brand){
        return Product.builder()
                .name(request.getName())
                .price(request.getPrice())
                .description(request.getDescription())
                .brand(brand)
                .inventory(request.getInventory())
                .price(request.getPrice())
                .category(category)
                .isBestSelling(request.isBestSelling())
                .build();

    }
    @Override
    public List<ProductDto> getConvertedProducts(List<Product> products){
        return products.stream()
                .map(this::convertToDto)
                .toList();

    }
    @Override
    public ProductDto convertToDto(Product product){
        //convert the images to multipart
        ProductDto productDto = new ProductDto();
        productDto.setId(product.getId());
        productDto.setName(product.getName());
        productDto.setBrand(product.getBrand().getName());
        productDto.setCategory(product.getCategory().getName());
        productDto.setDescription(product.getDescription());
        productDto.setPrice(product.getPrice());
        productDto.setInventory(product.getInventory());
        productDto.setIsBestSelling(product.getIsBestSelling());
        List<ImageDto> imageDtos = product.getImages().stream()
                .map(this::convertImageToImagDto)
                .toList();
        productDto.setImages(imageDtos);
        return productDto;

    }

    @Override
    public ProductPageResponse searchProducts(String search,int pageNo, int recordCount) {
        Pageable pageable = PageRequest.of(pageNo,recordCount);
        Page<Product> productPage = productRepository.findByNameContainingIgnoreCaseOrBrandNameContainingIgnoreCase(search,search,pageable);
        List<Product> products = productPage.getContent().stream().toList();
            List<ProductDto> dtos  = products.stream()
                    .map(this :: attachThumbnailImage)
                    .map(this::convertToDto)
                    .toList();
            return new ProductPageResponse(dtos, productPage.isLast());
    }
    @Override
    public ProductPageResponse getProductsByCategory(Long categoryId, int pageNo, int recordCount) {
        Pageable pageable = PageRequest.of(pageNo,recordCount);
        Page<Product> productPage = productRepository.findByCategoryId(categoryId, pageable);
        List<Product> products = productPage.getContent().stream().toList();
        List<ProductDto> dtos  = products.stream()
                .map(this :: attachThumbnailImage)
                .map(this::convertToDto)
                .toList();
        return new ProductPageResponse(dtos, productPage.isLast());

    }

    @Override
    public List<ProductDto> getBestSellingProducts() {
        List<Product> products = productRepository.findByIsBestSellingTrue();
        return products.stream()
                .map(this :: attachThumbnailImage)
                .map(this::convertToDto)
                .toList();

    }

    @Override
    public void deleteProduct(Long productId) {
//        Product product = productRepository.findById(productId)
//                .orElseThrow(()-> new ResourceNotFoundException("Product not found"));
        try {
            productRepository.deleteById(productId);
        } catch (Exception e) {
            throw new ResourceNotFoundException("Error while deleting the product");
        }

    }

    @Transactional
    @Override
    public void updateProduct(Long productId, AddProductRequest request) {
        //get the product from the db
        Product product = productRepository.findById(productId)
                .orElseThrow(()-> new ResourceNotFoundException("Product not found"));

        Brand brand = brandRepository.findByName(request.getBrand())
                .orElseGet(()->{
                    Brand newBrand = new Brand(request.getBrand());
                    return brandRepository.save(newBrand);
                });


        Category category = Optional.ofNullable(categoryRepository.findByName(request.getCategory()))
                .orElseGet(()->{
                    Category newCategory = new Category(request.getCategory());
                    return categoryRepository.save(newCategory);
                });

        try {
            System.out.println(request.isBestSelling());
            product.setBrand(brand);
            product.setCategory(category);
            product.setName(request.getName());
            product.setDescription(request.getDescription());
            product.setInventory(request.getInventory());
            product.setPrice(request.getPrice());
            product.setIsBestSelling(request.isBestSelling());
            // Clear the existing images
            if (request.getFiles()!=null) {
                List<ProductImage> existingImages = product.getImages();
                existingImages.clear();
                List<ProductImage> newImages=  imageService.saveImages(request.getFiles(),product,request.getThumbnailIndex());
                existingImages.addAll(newImages);
            }
            System.out.println("after update"+product.getImages().size());
            product = productRepository.save(product);
            log.info("product updated successfully");


        } catch (ResourceNotFoundException e) {
            throw new ResourceNotFoundException("Error while saving the product to database");
        }

        productRepository.save(product);
    }



    private ImageDto convertImageToImagDto(ProductImage productImage) {
        ImageDto imageDto = new ImageDto();
        if(productImage!=null){
            try {
                byte[] imageBytes = productImage.getImage().getBytes(1, (int) productImage.getImage().length());
                String base64Image = Base64.getEncoder().encodeToString(imageBytes);
                imageDto.setImage("data:" + productImage.getFileType() + ";base64," + base64Image);
                imageDto.setThumbnail(productImage.isThumbnail());
                imageDto.setFileName(productImage.getFileName());
                imageDto.setId(productImage.getId());

            } catch (SQLException e) {
                e.printStackTrace();
                // Handle SQL exception as needed
            }

        }
        return imageDto;
    }

}
