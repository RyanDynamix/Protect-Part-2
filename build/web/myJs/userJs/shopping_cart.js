// //calculate money amount
document.querySelectorAll(".product").forEach(product => {
    const soLuongInput = product.querySelector(".soLuong");
    const thanhTienSpan = product.querySelector(".thanhTien");
    const giaText = product.querySelector(".gia").textContent;
    const gia = parseFloat(giaText.replace(/\./g, '').replace('đ', ''));

    const updateThanhTien = () => {
        const soLuong = parseInt(soLuongInput.value);
        const thanhTien = gia * soLuong;
        const formattedThanhTien = new Intl.NumberFormat('vi-VN', {style: 'currency', currency: 'VND'}).format(thanhTien);
        thanhTienSpan.textContent = formattedThanhTien;
        
        // Update total after individual item change, but preserve discount
        setTimeout(() => {
            updateTongTien();
        }, 50); // Small delay to ensure all items are updated
    };

    // Initialize thanhTien when page loads
    updateThanhTien();

    // Update thanhTien on input change
    soLuongInput.addEventListener("input", updateThanhTien);
});

//calculate total money amount
function updateTongTien() {
    let tongTienHang = 0;

    document.querySelectorAll(".product").forEach(product => {
        const thanhTienText = product.querySelector(".thanhTien").textContent;
        const thanhTien = parseFloat(thanhTienText.replace(/\./g, '').replace('đ', ''));
        tongTienHang += thanhTien;
    });

    // Update the subtotal display
    document.getElementById("tongTienHang").textContent = new Intl.NumberFormat('vi-VN', {style: 'currency', currency: 'VND'}).format(tongTienHang);
    
    // Get shipping fee
    const phiVanChuyenElement = document.getElementById("phiVanChuyen");
    let shippingFee = 0;
    if (phiVanChuyenElement && phiVanChuyenElement.textContent !== '0đ') {
        const shippingText = phiVanChuyenElement.textContent.replace(/\./g, '').replace('đ', '');
        shippingFee = parseFloat(shippingText) || 0;
    }
    
    // Check if discount is applied and stored globally
    if (window.discountInfo && window.discountInfo.isApplied) {
        // Use the stored discount amount (don't recalculate it)
        let discountAmount = window.discountInfo.discountAmount;
        
        // Calculate final total: subtotal - discount + shipping
        const finalTotal = tongTienHang - discountAmount + shippingFee;
        
        console.log('UpdateTongTien with discount:', {
            subtotal: tongTienHang,
            discount: discountAmount,
            shipping: shippingFee,
            final: finalTotal,
            discountInfo: window.discountInfo
        });
        
        // Update the final total display
        const tongCongElement = document.getElementById("tongCong");
        if (tongCongElement) {
            tongCongElement.textContent = new Intl.NumberFormat('vi-VN', {style: 'currency', currency: 'VND'}).format(finalTotal);
            tongCongElement.style.color = 'green';
            tongCongElement.style.fontWeight = 'bold';
        }
        
        // Update stored info with new amounts
        window.discountInfo.originalAmount = tongTienHang;
        window.discountInfo.finalAmount = finalTotal;
    } else {
        // No discount applied: subtotal + shipping
        const finalTotal = tongTienHang + shippingFee;
        
        const giamGiaElement = document.getElementById("giamGia");
        if (giamGiaElement) {
            giamGiaElement.textContent = '0đ';
        }
        
        const tongCongElement = document.getElementById("tongCong");
        if (tongCongElement) {
            tongCongElement.textContent = new Intl.NumberFormat('vi-VN', {style: 'currency', currency: 'VND'}).format(finalTotal);
            tongCongElement.style.color = '';
            tongCongElement.style.fontWeight = '';
        }
    }
}

// Initialize tongTien when page loads, but check for existing discount first
document.addEventListener('DOMContentLoaded', function() {
    // Check if there's already discount applied (from server-side)
    const giamGiaElement = document.getElementById("giamGia");
    const hasExistingDiscount = giamGiaElement && 
        giamGiaElement.textContent !== '0đ' && 
        giamGiaElement.textContent.includes('-');
    
    if (!hasExistingDiscount) {
        // Only update if no discount is already applied
updateTongTien();
    } else {
        console.log('Existing discount found, skipping initial updateTongTien');
    }
});

//btn go to top
let mybutton = document.getElementById("myBtn");

// When the user scrolls down 20px from the top of the document, show the button
window.onscroll = function () {
    scrollFunction()
};

function scrollFunction() {
    if (document.body.scrollTop > 20 || document.documentElement.scrollTop > 20) {
        mybutton.style.display = "block";
    } else {
        mybutton.style.display = "none";
    }
}

// When the user clicks on the button, scroll to the top of the document
function topFunction() {
    document.body.scrollTop = 0;
    document.documentElement.scrollTop = 0;
}