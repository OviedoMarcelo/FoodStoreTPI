import { checkAuth } from "../../../utils/auth";
import { countCartItems, getCart, calculateCartTotal, removeFromCart, updateCartQuantity, clearCart } from "../../../utils/cart";
import { navigateTo } from "../../../utils/navigate";
import { getSession, removeSession, saveOrder } from "../../../utils/storage";
import { showToast } from "../../../utils/toast";
import type { IOrder } from "../../../types/IOrder";

// Costo de envío fijo (ver README para más info)
const ENVIO = 1000;

checkAuth('client');

function updateCartCount(): void {
    const cartCount = document.getElementById('cart-count');
    if (cartCount) cartCount.textContent = String(countCartItems());
}

function sessionInNav(): void {
    const session = getSession();
    const navUsername = document.getElementById('nav-username');
    if (navUsername && session) navUsername.textContent = `Hola, ${session.name}!`;

    document.getElementById('btn-logout')?.addEventListener('click', () => {
        removeSession();
        navigateTo("/src/pages/auth/login/login.html");
    });
}

function renderCartItems(): void {
    const cartItemsContainer = document.getElementById('cart-items');
    if (!cartItemsContainer) return;

    const productsInCart = getCart();

    if (productsInCart.length === 0) {
        cartItemsContainer.innerHTML = `
            <div class="cart-empty">
                <p>Tu carrito está vacío 🛒</p>
                <button id="btn-ir-tienda" class="btn-submit">Ver productos</button>
            </div>`;
        document.getElementById('cart-total')!.textContent = '0';
        document.getElementById('cart-quantity')!.textContent = '0';
        document.getElementById('btn-ir-tienda')?.addEventListener('click', () => {
            navigateTo('/src/pages/store/home/home.html');
        });
        return;
    }

    cartItemsContainer.innerHTML = productsInCart.map(item =>
        `<div class="cart-item">
            <img src="${item.product.imageUrl}" alt="${item.product.name}">
            <div class="cart-item-details">
                <h3>${item.product.name}</h3>
                <p>${item.product.description}</p>
                <p>Precio unitario: $${item.product.price.toLocaleString()}</p>
            </div>
            <div class="cart-item-actions">
                <input type="number" class="quantity-input" data-product-id="${item.product.id}" value="${item.quantity}" min="1" max="${item.product.stock}">
                <button class="btn-update" data-product-id="${item.product.id}">Actualizar</button>
                <button class="btn-remove" data-product-id="${item.product.id}">Eliminar</button>
            </div>
            <div class="cart-item-total">
                <p>$${(item.product.price * item.quantity).toLocaleString()}</p>
            </div>
        </div>`).join('');

    const subtotal = calculateCartTotal();
    document.getElementById('cart-subtotal')!.textContent = subtotal.toLocaleString();
    document.getElementById('cart-envio')!.textContent = ENVIO.toLocaleString();
    document.getElementById('cart-total')!.textContent = (subtotal + ENVIO).toLocaleString();
    document.getElementById('cart-quantity')!.textContent = String(countCartItems());

    document.querySelectorAll('.btn-remove').forEach(button => {
        button.addEventListener('click', () => {
            const productId = button.getAttribute('data-product-id');
            if (productId) {
                removeFromCart(productId);
                updateCartCount();
                renderCartItems();
                showToast('Producto eliminado del carrito', 2000);
            }
        });
    });

    document.querySelectorAll('.btn-update').forEach(button => {
        button.addEventListener('click', () => {
            const productId = button.getAttribute('data-product-id');
            if (productId) {
                const input = document.querySelector(`.quantity-input[data-product-id="${productId}"]`) as HTMLInputElement;
                if (input) {
                    const newQuantity = parseInt(input.value);
                    if (!isNaN(newQuantity) && newQuantity > 0) {
                        updateCartQuantity(productId, newQuantity);
                        renderCartItems();
                        updateCartCount();
                        showToast('Cantidad actualizada', 2000);
                    }
                }
            }
        });
    });
}

// Abrir modal
document.getElementById('checkout-btn')?.addEventListener('click', () => {
    if (getCart().length === 0) {
        showToast('Tu carrito está vacío', 2000);
        return;
    }
    const total = calculateCartTotal() + ENVIO;
    document.getElementById('modal-total-price')!.textContent = `$${total.toLocaleString()}`;
    document.getElementById('modal-checkout')!.style.display = 'flex';
});

// Cerrar modal
document.getElementById('btn-close-modal')?.addEventListener('click', () => {
    document.getElementById('modal-checkout')!.style.display = 'none';
});

// Confirmar pedido
document.getElementById('btn-confirm-order')?.addEventListener('click', () => {
    const phone = (document.getElementById('input-phone') as HTMLInputElement).value.trim();
    const address = (document.getElementById('input-address') as HTMLTextAreaElement).value.trim();
    const paymentMethod = (document.getElementById('input-payment') as HTMLSelectElement).value;
    const notes = (document.getElementById('input-notes') as HTMLTextAreaElement).value.trim();

    if (!phone || !address || !paymentMethod) {
        showToast('Completá todos los campos obligatorios', 2000);
        return;
    }

    const session = getSession();
    const cart = getCart();

    const newOrder: IOrder = {
        id: Date.now().toString(),
        userId: session!.id,
        status: 'pending',
        items: cart.map(item => ({
            productId: item.product.id,
            name: item.product.name,
            price: item.product.price,
            quantity: item.quantity
        })),
        totalPrice: calculateCartTotal() + ENVIO,
        createdAt: new Date().toISOString(),
        phone,
        address,
        paymentMethod: paymentMethod as 'cash' | 'card' | 'transfer',
        notes
    };

    saveOrder(newOrder);
    clearCart();
    updateCartCount();
    document.getElementById('modal-checkout')!.style.display = 'none';
    renderCartItems();
    showToast('¡Pedido confirmado! Gracias por tu compra 🎉', 3000);
    // Espero 3 segundos para que el toast sea visible antes de redirigir
    setTimeout(() => {
        navigateTo('/src/pages/client/orders/orders.html');
    }, 2000);
});

// Vaciar carrito
document.getElementById('btn-vaciar')?.addEventListener('click', () => {
    clearCart();
    updateCartCount();
    renderCartItems();
    showToast('Carrito vaciado', 2000);
});

updateCartCount();
sessionInNav();
renderCartItems();
