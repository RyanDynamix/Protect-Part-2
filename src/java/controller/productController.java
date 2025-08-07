package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import model.*;
import dal.*;
import jakarta.servlet.http.HttpSession;
import java.util.logging.Level;
import java.util.logging.Logger;
import util.Validate;

public class productController extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        ProductDAO p = new ProductDAO();
        List<Products> list = p.listAll();

        for (Products sp : list) {
            sp.setPriceString(Validate.doubleToMoney(sp.getPrice()));
            sp.setDisString(Validate.doubleToMoney(sp.getDiscount()));
        }

        request.setAttribute("lists", list);
        request.getRequestDispatcher("products.jsp").forward(request, response);
    }
    
//    public Product checkDuplicate(Product newProduct) {
//    // Tìm tất cả sản phẩm có cùng tên
//    List<Product> sameNameProducts = getProductsByName(newProduct.getName());
//
//    for (Product p : sameNameProducts) {
//        boolean sameColor = p.getColor().equalsIgnoreCase(newProduct.getColor());
//        boolean sameMemory = p.getMemory().equalsIgnoreCase(newProduct.getMemory());
//        boolean sameChip = p.getChip().equalsIgnoreCase(newProduct.getChip());
//        boolean sameScreen = p.getScreen().equalsIgnoreCase(newProduct.getScreen());
//        boolean sameCamera = p.getCamera().equalsIgnoreCase(newProduct.getCamera());
//
//        if (sameColor && sameMemory && sameChip && sameScreen && sameCamera) {
//            return p; // Trùng toàn bộ
//        }
//    }
//
//    return null; // Không trùng hoàn toàn
//}


    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
    }

    @Override
    public String getServletInfo() {
        return "Short description";
    }

}
