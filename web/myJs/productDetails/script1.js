//nút xem thêm
function toggleContent() {
    var moreContent = document.querySelector('.more-content');
    var showMoreBtn = document.querySelector('.show-more-btn');
    if (moreContent && showMoreBtn) {
        if (moreContent.style.display === 'none') {
            moreContent.style.display = 'inline';
            showMoreBtn.textContent = 'Thu gọn';
        } else {
            moreContent.style.display = 'none';
            showMoreBtn.textContent = 'Xem thêm';
        }
    }
}

// Slider functionality with safety checks
const listImage = document.querySelector('.slider-content-left-top');
const imgs = document.getElementsByClassName("imgs");
const btnLeft = document.querySelector('.btn-left'); 
const btnRight = document.querySelector('.btn-right');
const length = imgs ? imgs.length : 0;
let current = 0;

const handleChangeSlide = () => {
    if (!imgs || length === 0 || !listImage) return; // Safety check
    
    if (current == length - 1) {
        current = 0;
        let width = imgs[0].offsetWidth;
        listImage.style.transform = `translateX(0px)`;
        const activeEl = document.querySelector('.active');
        if (activeEl) activeEl.classList.remove('active');
        const indexEl = document.querySelector('.index-item-'+ current);
        if (indexEl) indexEl.classList.add('active');
    } else {
        current++;
        let width = imgs[0].offsetWidth;
        listImage.style.transform = `translateX(${width * -1 * current}px)`;
        const activeEl = document.querySelector('.active');
        if (activeEl) activeEl.classList.remove('active');
        const indexEl = document.querySelector('.index-item-'+ current);
        if (indexEl) indexEl.classList.add('active');
    }
};

let handleEventChangeSlide = (imgs && length > 0) ? setInterval(handleChangeSlide, 4000) : null;

// Right button event listener
if (btnRight) {
    btnRight.addEventListener('click', () => {
        clearInterval(handleEventChangeSlide);
        handleChangeSlide();
        handleEventChangeSlide = setInterval(handleChangeSlide, 4000);
    });
}

// Left button event listener
if (btnLeft) {
    btnLeft.addEventListener('click', () => {
        clearInterval(handleEventChangeSlide);
        if (current == 0) {
            current = length - 1;
            let width = imgs[0].offsetWidth;
            listImage.style.transform = `translateX(${width * -1 * current}px)`;
            const activeEl = document.querySelector('.active');
            if (activeEl) activeEl.classList.remove('active');
            const indexEl = document.querySelector('.index-item-'+ current);
            if (indexEl) indexEl.classList.add('active');
        } else {
            current--;
            let width = imgs[0].offsetWidth;
            listImage.style.transform = `translateX(${width * -1 * current}px)`;
            const activeEl = document.querySelector('.active');
            if (activeEl) activeEl.classList.remove('active');
            const indexEl = document.querySelector('.index-item-'+ current);
            if (indexEl) indexEl.classList.add('active');
        }
        handleEventChangeSlide = setInterval(handleChangeSlide, 4000);
    });
}

// Color selection functionality
var colorsList = document.querySelectorAll(".d-lg-flex>img");
for(var i = 0; i < colorsList.length; i++){
    colorsList[i].onmouseover = function(){
        var mainImg = document.querySelector(".mainImg");
        if (mainImg) {
            mainImg.src = this.src;
        }
    };
}

// Search functionality
const searchBtn = document.querySelector('.search-btn');
if (searchBtn) {
    searchBtn.addEventListener('click', function () {
        this.parentElement.classList.toggle('open');
        this.previousElementSibling.focus();
    });
}