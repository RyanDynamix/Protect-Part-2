document.addEventListener('DOMContentLoaded', function() {
    const discountForm = document.getElementById('discountForm');
    const discountInput = document.getElementById('discountCode');
    const discountFeedback = document.getElementById('discountFeedback');

    if (discountForm) {
        discountForm.addEventListener('submit', function(e) {
            e.preventDefault();
            
            const discountCode = discountInput.value.trim();
            
            if (!discountCode) {
                showFeedback('Vui lòng nhập mã giảm giá', 'danger');
                return;
            }

            // Use fetch to submit the discount code
            fetch('shopping?action=applyDiscount', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                    'X-Requested-With': 'XMLHttpRequest'
                },
                body: `discountCode=${encodeURIComponent(discountCode)}`
            })
            .then(response => response.text())
            .then(data => {
                try {
                    // Try to parse as JSON first
                    const jsonData = JSON.parse(data);
                    if (jsonData.success) {
                        // Update the UI with discount information
                        updatePriceDisplay(jsonData);
                        showDiscountSuccess(jsonData);
                        
                        // Clear the input field after successful application
                        discountInput.value = '';
                    }
                } catch (e) {
                    // If not JSON, treat as HTML error message
                    discountFeedback.innerHTML = data;
                }
            })
            .catch(error => {
                console.error('Error:', error);
                showFeedback('Có lỗi xảy ra. Vui lòng thử lại.', 'danger');
            });
        });
    }

    function updatePriceDisplay(discountData) {
        console.log('Applying discount:', discountData.discountCode);
        
        // Update the price summary section
        const tongTienHang = document.getElementById('tongTienHang');
        const giamGia = document.getElementById('giamGia');
        const tongCong = document.getElementById('tongCong');
        
        // Calculate total from selected products
        let selectedTotal = 0;
        let checkboxes = document.querySelectorAll('.checkProduct:checked');
        checkboxes.forEach(cb => {
            let price = Number(cb.getAttribute('data-price'));
            let quantity = Number(cb.getAttribute('data-quantity'));
            selectedTotal += price * quantity;
        });
        
        // Parse discount amount from the response (remove currency formatting)
        const discountAmount = parseFloat(discountData.discountAmount.replace(/\./g, '').replace('đ', ''));
        
        // Shipping fee and shipping discount
        let shippingFee = selectedTotal > 0 ? 50000 : 0;
        let shippingDiscount = selectedTotal > 0 ? 50000 : 0; // Free shipping discount
        
        // Calculate final amount: selected total - discount + shipping fee - shipping discount
        const finalAmount = selectedTotal - discountAmount + shippingFee - shippingDiscount;
        
        console.log('Final calculation: ' + selectedTotal + ' - ' + discountAmount + ' + ' + shippingFee + ' - ' + shippingDiscount + ' = ' + finalAmount);
        
        if (tongTienHang) {
            const formattedOriginal = new Intl.NumberFormat('vi-VN', {style: 'currency', currency: 'VND'}).format(selectedTotal);
            tongTienHang.textContent = formattedOriginal;
        }
        
        if (giamGia) {
            const formattedDiscount = '-' + new Intl.NumberFormat('vi-VN', {style: 'currency', currency: 'VND'}).format(discountAmount);
            giamGia.textContent = formattedDiscount;
            giamGia.style.color = 'red';
        }
        
        if (tongCong) {
            const formattedFinal = new Intl.NumberFormat('vi-VN', {style: 'currency', currency: 'VND'}).format(finalAmount);
            tongCong.textContent = formattedFinal;
            tongCong.style.color = 'green';
            tongCong.style.fontWeight = 'bold';
        }
        
        // Update shipping fee display
        const phiVanChuyenElement = document.getElementById("phiVanChuyen");
        const giamPhiShipElement = document.getElementById("giamPhiShip");
        
        if (phiVanChuyenElement) {
            const formattedShipping = new Intl.NumberFormat('vi-VN', {style: 'currency', currency: 'VND'}).format(shippingFee);
            phiVanChuyenElement.textContent = formattedShipping;
        }
        
        if (giamPhiShipElement) {
            const formattedShippingDiscount = shippingDiscount > 0 ? '-' + new Intl.NumberFormat('vi-VN', {style: 'currency', currency: 'VND'}).format(shippingDiscount) : '0đ';
            giamPhiShipElement.textContent = formattedShippingDiscount;
            giamPhiShipElement.style.color = 'red';
        }
        
        // Store discount info globally so other functions can access it
        window.discountInfo = {
            originalAmount: selectedTotal,
            discountAmount: discountAmount,
            shippingFee: shippingFee,
            shippingDiscount: shippingDiscount,
            finalAmount: finalAmount,
            isApplied: true
        };
    }

    function showDiscountSuccess(discountData) {
        const successHtml = `
            <div class="alert alert-success mt-2" role="alert">
                <strong>✓ Áp dụng mã giảm giá thành công!</strong><br>
                <strong>Mã giảm giá:</strong> ${discountData.discountCode}<br>
                <strong>Tên chương trình:</strong> ${discountData.discountName}<br>
                <strong>Loại giảm giá:</strong> ${discountData.discountType}<br>
                <strong>Giá trị:</strong> ${discountData.discountValue}<br>
                <strong>Điều kiện:</strong> Áp dụng cho đơn hàng từ ${discountData.minOrderValue}<br>
                <strong>Hiệu lực:</strong> Từ ${discountData.startDate} đến ${discountData.endDate}
            </div>
        `;
        discountFeedback.innerHTML = successHtml;
    }

    function showFeedback(message, type) {
        discountFeedback.innerHTML = `
            <div class="alert alert-${type} mt-2" role="alert">
                ${message}
            </div>
        `;
    }
    
    // Function to clear discount
    function clearDiscount() {
        // Clear the discount code input
        discountInput.value = '';
        
        // Clear the feedback message
        discountFeedback.innerHTML = '';
        
        // Reset discount info
        window.discountInfo = {
            isApplied: false,
            discountAmount: 0
        };
        
        // Recalculate totals without discount by calling the main updateTotal function
        if (typeof updateTotal === 'function') {
            updateTotal(); // This will recalculate with selected products
        } else {
            // Fallback: manually reset the display
            resetPriceDisplay();
        }
    }

    function resetPriceDisplay() {
        // Calculate total from selected products
        let selectedTotal = 0;
        let checkboxes = document.querySelectorAll('.checkProduct:checked');
        checkboxes.forEach(cb => {
            let price = Number(cb.getAttribute('data-price'));
            let quantity = Number(cb.getAttribute('data-quantity'));
            selectedTotal += price * quantity;
        });
        
        // Shipping fee and shipping discount
        let shippingFee = selectedTotal > 0 ? 50000 : 0;
        let shippingDiscount = selectedTotal > 0 ? 50000 : 0;
        
        const tongTienHang = document.getElementById('tongTienHang');
        const giamGia = document.getElementById('giamGia');
        const tongCong = document.getElementById('tongCong');
        const phiVanChuyen = document.getElementById('phiVanChuyen');
        const giamPhiShip = document.getElementById('giamPhiShip');
        
        if (tongTienHang) {
            tongTienHang.textContent = selectedTotal.toLocaleString('vi-VN') + "đ";
        }
        
        if (giamGia) {
            giamGia.textContent = "0đ";
            giamGia.style.color = '';
        }
        
        if (phiVanChuyen) {
            phiVanChuyen.textContent = shippingFee.toLocaleString('vi-VN') + "đ";
        }
        
        if (giamPhiShip) {
            giamPhiShip.textContent = shippingDiscount > 0 ? "-" + shippingDiscount.toLocaleString('vi-VN') + "đ" : "0đ";
            giamPhiShip.style.color = 'red';
        }
        
        if (tongCong) {
            // Final total = selected total + shipping fee - shipping discount
            const finalTotal = selectedTotal + shippingFee - shippingDiscount;
            tongCong.textContent = finalTotal.toLocaleString('vi-VN') + "đ";
            tongCong.style.color = '';
            tongCong.style.fontWeight = '';
        }
    }
    
    // Expose clearDiscount function globally
    window.clearDiscount = clearDiscount;
    
    // Debug function to check calculations
    window.debugDiscount = function() {
        console.log('=== DEBUG DISCOUNT CALCULATION ===');
        
        const tongTienHang = document.getElementById('tongTienHang');
        const giamGia = document.getElementById('giamGia');
        const phiVanChuyen = document.getElementById('phiVanChuyen');
        const tongCong = document.getElementById('tongCong');
        
        console.log('Current DOM values:');
        console.log('- tongTienHang:', tongTienHang ? tongTienHang.textContent : 'null');
        console.log('- giamGia:', giamGia ? giamGia.textContent : 'null');
        console.log('- phiVanChuyen:', phiVanChuyen ? phiVanChuyen.textContent : 'null');
        console.log('- tongCong:', tongCong ? tongCong.textContent : 'null');
        
        if (window.discountInfo) {
            console.log('Stored discount info:', window.discountInfo);
            
            // Manual calculation
            const expected = window.discountInfo.originalAmount - window.discountInfo.discountAmount + window.discountInfo.shippingFee - window.discountInfo.shippingDiscount;
            const expectedFormatted = new Intl.NumberFormat('vi-VN', {style: 'currency', currency: 'VND'}).format(expected);
            
            console.log('Expected tongCong:', expectedFormatted);
            console.log('Current tongCong:', tongCong ? tongCong.textContent : 'null');
            console.log('Match:', tongCong ? (tongCong.textContent === expectedFormatted) : false);
        } else {
            console.log('No discount info stored');
        }
        
        console.log('=== END DEBUG ===');
    };
}); 