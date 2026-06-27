import { checkAuth } from "../../../utils/auth";
import { getProducts } from "../../../utils/data";
import { addToCart, countCartItems } from "../../../utils/cart";
import { navigateTo } from "../../../utils/navigate";
import { removeSession, getSession } from "../../../utils/storage";
import { showToast } from "../../../utils/toast";
import type { IProduct } from "../../../types/IProduct";

checkAuth('client');

// Leer el id de la URL
const params = new URLSearchParams(window.location.search);
const productId = params.get('id');

// Actualizar navbar
function sessionInNav(): void {
    const session = getSession();
    const navUsername = document.getElementById('nav-username');
    if (navUsername && session) navUsername.textContent = `Hola, ${session.name}!`;

    // Si es admin: agrego Panel Admin y oculto carrito y mis pedidos
    if (session?.role === 'admin') {
        const navLinks = document.querySelector('.nav-links');
        if (navLinks) {
            navLinks.querySelectorAll('a').forEach(a => {
                if (a.href.includes('cart') || a.href.includes('orders')) {
                    (a.parentElement as HTMLElement).style.display = 'none';
                }
            });
            const li = document.createElement('li');
            li.innerHTML = `<a href="/src/pages/admin/adminHome/home.html" class="nav-admin-link">Panel Admin</a>`;
            navLinks.appendChild(li);
        }
    }

    document.getElementById('btn-logout')?.addEventListener('click', () => {
        removeSession();
        navigateTo("/src/pages/auth/login/login.html");
    });
}

function updateCartCount(): void {
    const cartCount = document.getElementById('cart-count');
    if (cartCount) cartCount.textContent = String(countCartItems());
}

function renderProduct(product: IProduct): void {
    const container = document.getElementById('product-detail-container');
    if (!container) return;

    container.innerHTML = `
        
        <div class="product-detail">
            <img src="${product.imageUrl}" alt="${product.name}" class="product-detail-img">
            <div class="product-detail-info">
                <h1>${product.name}</h1>
                <p class="product-detail-description">${product.description}</p>
                <p class="product-detail-price">$${product.price.toLocaleString()}</p>
                <p class="product-detail-stock">Stock disponible: ${product.stock}</p>
                <span class="badge ${product.available ? 'badge-available' : 'badge-unavailable'}">
                    ${product.available ? 'Disponible' : 'No disponible'}
                </span>
                <div class="quantity-selector">
                    <button id="btn-minus">-</button>
                    <span id="quantity">1</span>
                    <button id="btn-plus">+</button>
                </div>
                <button id="btn-add-cart" ${!product.available || product.stock === 0 ? 'disabled' : ''}>
                    Agregar al carrito
                </button>
                <button id="btn-volver" class="btn-volver">← Volver</button>
            </div>
        </div>
        
    `;

    let quantity = 1;

    document.getElementById('btn-minus')?.addEventListener('click', () => {
        if (quantity > 1) {
            quantity--;
            document.getElementById('quantity')!.textContent = String(quantity);
        }
    });

    document.getElementById('btn-plus')?.addEventListener('click', () => {
        if (quantity < product.stock) {
            quantity++;
            document.getElementById('quantity')!.textContent = String(quantity);
        } else {
            showToast('No hay más stock disponible', 2000);
        }
    });

    document.getElementById('btn-add-cart')?.addEventListener('click', () => {
        addToCart({ product, quantity });
        updateCartCount();
        showToast('Producto agregado al carrito', 2000);
    });

    document.getElementById('btn-volver')?.addEventListener('click', () => {
        navigateTo("/src/pages/store/home/home.html");
    });
}

async function init(): Promise<void> {
    if (!productId) {
        navigateTo("/src/pages/store/home/home.html");
        return;
    }

    const products = await getProducts();
    const product = products.find(p => p.id === productId);

    if (!product) {
        navigateTo("/src/pages/store/home/home.html");
        return;
    }

    sessionInNav();
    updateCartCount();
    renderProduct(product);
}

init();