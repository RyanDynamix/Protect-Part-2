package controller;

import dal.ProductDAO;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.sql.Date;
import java.util.List;
import model.ProductDetail;
import model.Products;
import model.Categories;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.Part;
import java.io.File;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

@MultipartConfig
public class editProduct extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(editProduct.class.getName());
    private static final String UPLOAD_DIRECTORY = "D:\\HexTech_DuyHung\\HexTech (2)\\HexTech\\web\\Admin\\img_svg";

    /**
     * Handles the HTTP <code>GET</code> method.
     * Load product data for editing
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        LOGGER.log(Level.INFO, "GET request received to editProduct servlet");
        
        try {
            String action = request.getParameter("action");
            
            if ("deleteGalleryImage".equals(action)) {
                handleDeleteGalleryImage(request, response);
                return;
            }
            
            // Standard edit page loading
            String productIdStr = request.getParameter("id");
            if (productIdStr == null || productIdStr.isEmpty()) {
                LOGGER.log(Level.WARNING, "Product ID is missing");
                request.getSession().setAttribute("errorMessage", "Product ID is required");
                response.sendRedirect("products");
                return;
            }
            
            int productId = Integer.parseInt(productIdStr);
            LOGGER.log(Level.INFO, "Loading product data for ID: {0}", productId);
            
            ProductDAO dao = new ProductDAO();
            
            // Get product basic info
            Products product = dao.findProductByID(productId);
            if (product == null) {
                LOGGER.log(Level.WARNING, "Product not found with ID: {0}", productId);
                request.getSession().setAttribute("errorMessage", "Product not found");
                response.sendRedirect("products");
                return;
            }
            
            // Get product details (all color-storage combinations)
            List<ProductDetail> productDetails = dao.findProductDetailsByID(productId);
            LOGGER.log(Level.INFO, "Found {0} ProductDetails for product ID {1}", 
                      new Object[]{productDetails.size(), productId});
            
            // Get product galleries (additional images)
            List<String> galleries = dao.findAllGalleryOfProduct(productId);
            LOGGER.log(Level.INFO, "Found {0} Gallery images for product ID {1}", 
                      new Object[]{galleries.size(), productId});
            
            LOGGER.log(Level.INFO, "Product data loaded: {0} details, {1} gallery images", 
                      new Object[]{productDetails.size(), galleries.size()});
            
            // Set attributes for JSP
            request.setAttribute("product", product);
            request.setAttribute("productDetails", productDetails);
            request.setAttribute("galleries", galleries);
            
            // Forward to edit page
            request.getRequestDispatcher("/Admin/edit-products.jsp").forward(request, response);
            
        } catch (NumberFormatException e) {
            LOGGER.log(Level.SEVERE, "Invalid product ID format: {0}", e.getMessage());
            request.getSession().setAttribute("errorMessage", "Invalid product ID");
            response.sendRedirect("products");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error in doGet: {0}", e.getMessage());
            e.printStackTrace();
            request.getSession().setAttribute("errorMessage", "Error loading product: " + e.getMessage());
            response.sendRedirect("products");
        }
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     * Process product update
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        LOGGER.log(Level.INFO, "POST request received to editProduct servlet");
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        
        try {
            String action = request.getParameter("action");
            
            if ("deleteGalleryImage".equals(action)) {
                handleDeleteGalleryImage(request, response);
            } else if ("updateProduct".equals(action)) {
                handleUpdateProduct(request, response);
            } else {
                // Original logic for updating product with details
                handleUpdateProductWithDetails(request, response);
            }
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error in doPost: {0}", e.getMessage());
            e.printStackTrace();
            request.getSession().setAttribute("errorMessage", "Error updating product: " + e.getMessage());
            response.sendRedirect("products");
        }
    }
    
    /**
     * Handle updating basic product information only
     */
    private void handleUpdateProduct(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            // Create upload directory if it doesn't exist
            File uploadDir = new File(UPLOAD_DIRECTORY);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
                LOGGER.log(Level.INFO, "Created upload directory: {0}", UPLOAD_DIRECTORY);
            }
            
            // Get product ID
            String productIdStr = request.getParameter("productId");
            if (productIdStr == null || productIdStr.isEmpty()) {
                request.getSession().setAttribute("errorMessage", "Product ID is required");
                response.sendRedirect("products");
                return;
            }
            
            int productId = Integer.parseInt(productIdStr);
            LOGGER.log(Level.INFO, "Updating basic product info for ID: {0}", productId);
            
            // Get form data
            String name = request.getParameter("name");
            String description = request.getParameter("description");
            String created_at = request.getParameter("created_at");
            String updated_at = request.getParameter("updated_at");
            String priceStr = request.getParameter("price");
            String quantityStr = request.getParameter("quantity");
            String discountStr = request.getParameter("discount");
            
            // Validate required fields
            if (name == null || name.trim().isEmpty() || 
                priceStr == null || priceStr.trim().isEmpty() ||
                quantityStr == null || quantityStr.trim().isEmpty()) {
                request.getSession().setAttribute("errorMessage", "Name, price, and quantity are required");
                response.sendRedirect("editProduct?id=" + productId);
                return;
            }
            
            // Parse numeric values - remove thousand separators
            String cleanPriceStr = priceStr.replace(".", "").replace(",", "");
            String cleanDiscountStr = (discountStr != null && !discountStr.trim().isEmpty()) ? 
                                     discountStr.replace(".", "").replace(",", "") : "0";
            
            double price = Double.parseDouble(cleanPriceStr);
            int quantity = Integer.parseInt(quantityStr);
            double discount = Double.parseDouble(cleanDiscountStr);
            
            // Create Products object
            ProductDAO dao = new ProductDAO();
            Products product = dao.findProductByID(productId);
            
            if (product == null) {
                LOGGER.log(Level.WARNING, "Product not found with ID: {0}", productId);
                request.getSession().setAttribute("errorMessage", "Product not found");
                response.sendRedirect("products");
                return;
            }
            
            // Update product properties
            product.setName(name.trim());
            product.setDescription(description != null ? description.trim() : "");
            product.setPrice(price);
            product.setQuantity(quantity);
            product.setDiscount(discount);
            
            // Parse dates
            if (created_at != null && !created_at.trim().isEmpty()) {
                product.setCreated_at(Date.valueOf(created_at));
            }
            if (updated_at != null && !updated_at.trim().isEmpty()) {
                product.setUpdated_at(Date.valueOf(updated_at));
            } else {
                // Set current date as updated_at
                product.setUpdated_at(new Date(System.currentTimeMillis()));
            }
            
            // Handle thumbnail image upload
            Part thumbnailPart = request.getPart("thumbnail");
            if (thumbnailPart != null && thumbnailPart.getSize() > 0) {
                String fileName = getSubmittedFileName(thumbnailPart);
                if (fileName != null && !fileName.isEmpty()) {
                    // Save file to disk
                    File thumbnailFile = new File(UPLOAD_DIRECTORY, fileName);
                    Files.copy(thumbnailPart.getInputStream(), thumbnailFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    LOGGER.log(Level.INFO, "Saved thumbnail file: {0}", thumbnailFile.getAbsolutePath());
                    
                    // Create database path
                    String thumbnailPath = "./img_svg/" + fileName;
                    product.setThumbnail(thumbnailPath);
                    LOGGER.log(Level.INFO, "Updated thumbnail path: {0}", thumbnailPath);
                }
            }
            
            // Update product in database
            boolean success = dao.updateNewProduct(product);
            
            // Handle gallery image uploads
            List<Part> galleryParts = request.getParts().stream()
                    .filter(part -> "galleries".equals(part.getName()) && part.getSize() > 0)
                    .toList();
            
            if (!galleryParts.isEmpty()) {
                LOGGER.log(Level.INFO, "Processing {0} gallery images", galleryParts.size());
                List<String> imagePaths = new ArrayList<>();
                
                for (Part galleryPart : galleryParts) {
                    String fileName = getSubmittedFileName(galleryPart);
                    if (fileName != null && !fileName.isEmpty()) {
                        // Save file to disk
                        File galleryFile = new File(UPLOAD_DIRECTORY, fileName);
                        Files.copy(galleryPart.getInputStream(), galleryFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                        LOGGER.log(Level.INFO, "Saved gallery image: {0}", galleryFile.getAbsolutePath());
                        
                        // Create database path
                        String imagePath = "./img_svg/" + fileName;
                        imagePaths.add(imagePath);
                    }
                }
                
                // Add new gallery images to database
                if (!imagePaths.isEmpty()) {
                    dao.addListImageForProduct(productId, imagePaths);
                    LOGGER.log(Level.INFO, "Added {0} new gallery images", imagePaths.size());
                }
            }
            
            if (success) {
                LOGGER.log(Level.INFO, "Successfully updated product ID: {0}", productId);
                request.getSession().setAttribute("successMessage", "Product updated successfully");
            } else {
                LOGGER.log(Level.WARNING, "Failed to update product ID: {0}", productId);
                request.getSession().setAttribute("errorMessage", "Failed to update product");
            }
            
            // Redirect back to edit page
            response.sendRedirect("editProduct?id=" + productId);
            
        } catch (NumberFormatException e) {
            LOGGER.log(Level.WARNING, "Invalid number format: {0}", e.getMessage());
            request.getSession().setAttribute("errorMessage", "Invalid number format in form data");
            response.sendRedirect("products");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error updating product: {0}", e.getMessage());
            request.getSession().setAttribute("errorMessage", "Error updating product: " + e.getMessage());
            response.sendRedirect("products");
        }
    }
    
    /**
     * Handle deleting a gallery image
     */
    private void handleDeleteGalleryImage(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        LOGGER.log(Level.INFO, "Handling delete gallery image request");
        
        // Set response type to JSON
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        StringBuilder jsonResponse = new StringBuilder();
        
        try {
            String productIdStr = request.getParameter("productId");
            String imagePath = request.getParameter("imagePath");
            
            if (productIdStr == null || productIdStr.isEmpty() || imagePath == null || imagePath.isEmpty()) {
                jsonResponse.append("{\"success\": false, \"message\": \"Missing required parameters\"}");
                out.print(jsonResponse.toString());
                return;
            }
            
            int productId = Integer.parseInt(productIdStr);
            LOGGER.log(Level.INFO, "Deleting gallery image: {0} for product ID: {1}", 
                       new Object[]{imagePath, productId});
            
            ProductDAO dao = new ProductDAO();
            
            // Delete from database
            boolean success = dao.deleteGalleryImage(productId, imagePath);
            
            if (success) {
                // Try to delete the physical file (optional)
                try {
                    // Extract file name from path
                    String fileName = imagePath.substring(imagePath.lastIndexOf('/') + 1);
                    File fileToDelete = new File(UPLOAD_DIRECTORY, fileName);
                    
                    if (fileToDelete.exists()) {
                        if (fileToDelete.delete()) {
                            LOGGER.log(Level.INFO, "Successfully deleted physical file: {0}", fileToDelete.getAbsolutePath());
                        } else {
                            LOGGER.log(Level.WARNING, "Could not delete physical file: {0}", fileToDelete.getAbsolutePath());
                        }
                    } else {
                        LOGGER.log(Level.WARNING, "Physical file does not exist: {0}", fileToDelete.getAbsolutePath());
                    }
                } catch (Exception e) {
                    LOGGER.log(Level.WARNING, "Error deleting physical file: {0}", e.getMessage());
                    // Continue execution even if physical file deletion fails
                }
                
                jsonResponse.append("{\"success\": true, \"message\": \"Gallery image deleted successfully\"}");
            } else {
                jsonResponse.append("{\"success\": false, \"message\": \"Failed to delete gallery image from database\"}");
            }
            
        } catch (NumberFormatException e) {
            jsonResponse.append("{\"success\": false, \"message\": \"Invalid product ID format\"}");
            LOGGER.log(Level.WARNING, "Invalid product ID format: {0}", e.getMessage());
        } catch (Exception e) {
            jsonResponse.append("{\"success\": false, \"message\": \"Error: ").append(escapeJsonString(e.getMessage())).append("\"}");
            LOGGER.log(Level.SEVERE, "Error deleting gallery image: {0}", e.getMessage());
            e.printStackTrace();
        }
        
        out.print(jsonResponse.toString());
    }
    
    /**
     * Helper method to escape JSON strings
     */
    private String escapeJsonString(String input) {
        if (input == null) {
            return "";
        }
        
        StringBuilder escaped = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            char ch = input.charAt(i);
            switch (ch) {
                case '"':
                    escaped.append("\\\"");
                    break;
                case '\\':
                    escaped.append("\\\\");
                    break;
                case '\b':
                    escaped.append("\\b");
                    break;
                case '\f':
                    escaped.append("\\f");
                    break;
                case '\n':
                    escaped.append("\\n");
                    break;
                case '\r':
                    escaped.append("\\r");
                    break;
                case '\t':
                    escaped.append("\\t");
                    break;
                default:
                    escaped.append(ch);
            }
        }
        return escaped.toString();
    }
    
    // Helper method to extract file name from Part
    private String getSubmittedFileName(Part part) {
        String contentDisp = part.getHeader("content-disposition");
        String[] tokens = contentDisp.split(";");
        for (String token : tokens) {
            if (token.trim().startsWith("filename")) {
                return token.substring(token.indexOf("=") + 2, token.length() - 1);
            }
        }
        return null;
    }
    
    /**
     * Handle updating product with all details (original logic)
     */
    private void handleUpdateProductWithDetails(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        LOGGER.log(Level.INFO, "Using original update logic");
        request.getSession().setAttribute("errorMessage", "This feature is not yet implemented");
        response.sendRedirect("products");
    }

    @Override
    public String getServletInfo() {
        return "Edit Product Controller";
    }
}
