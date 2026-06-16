import { checkAuth } from "../../../utils/auth";
import { countCartItems, getCart, calculateCartTotal, removeFromCart, updateCartQuantity, clearCart } from "../../../utils/cart";
import { navigateTo } from "../../../utils/navigate";
import { getSession, removeSession } from "../../../utils/storage";
import { showToast } from "../../../utils/toast";

checkAuth('client');

//Modifico el cart count del nav si se agrega un elemento al carrito
function updateCartCount(): void {
    const cartCount = document.getElementById('cart-count');
    const totalItems = countCartItems();
    if (cartCount) {
        cartCount.textContent = String(totalItems);
    }
}

//Defino función para actualizar el nombre de sesión en el nav y agregar funcionalidad al botón de logout
function sessionInNav(): void {
    const session = getSession();
    const navUsername = document.getElementById('nav-username');
    if (navUsername && session) {
        navUsername.textContent = `Hola, ${session.name}!`;
    }

    const logoutButton = document.getElementById('btn-logout');
    logoutButton?.addEventListener('click', () => {
        removeSession();
        navigateTo("../auth/login/login.html");
    });
}

/******************************
RENDER DEL CARRITO DE COMPRAS*
*****************************/


function renderCartItems(): void {
    const cartItemsContainer = document.getElementById('cart-items');
    if (!cartItemsContainer) return;
    const productsIncart = getCart();
    if (productsIncart.length === 0) {
        cartItemsContainer.innerHTML = '<p>Tu carrito está vacío.</p>';
        document.getElementById('cart-total')!.textContent = '0';
        document.getElementById('cart-quantity')!.textContent = '0';
        return;
    }
    cartItemsContainer.innerHTML = productsIncart.map(item =>
        `<div class="cart-item">
        <img src="${item.product.imageUrl}" alt="${item.product.name}">
        <div class="cart-item-details">
            <h3>${item.product.name}</h3>
            <p>${item.product.description}</p>
            <p>Precio: $${item.product.price.toLocaleString()}</p>
            <p>Cantidad: ${item.quantity}</p>
        </div>
        <div class="cart-item-actions">
            <button class="btn-remove" data-product-id="${item.product.id}">Eliminar</button>
            <button class="btn-update" data-product-id="${item.product.id}">Actualizar Cantidad</button>
            <input type="number" class="quantity-input" data-product-id="${item.product.id}" value="${item.quantity}" min="1">
        </div>
        <div class="cart-item-total">
            <p>Total: $${(item.product.price * item.quantity).toLocaleString()}</p>
        </div>
    </div>`).join('');

    const cartTotal = calculateCartTotal();
    const cartTotalContainer = document.getElementById('cart-total');
    if (cartTotalContainer) {
        cartTotalContainer.innerHTML = `${cartTotal.toLocaleString()}`;
    }
    const cartQuantityContainer = document.getElementById('cart-quantity');
    if (cartQuantityContainer) {
        cartQuantityContainer.innerHTML = `${countCartItems()}`;
    }

    // Agregar event listeners a los botones de eliminar y actualizar cantidad
    const removeButtons = document.querySelectorAll('.btn-remove');
    removeButtons.forEach(button => {
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

    const updateButtons = document.querySelectorAll('.btn-update');
    updateButtons.forEach(button => {
        button.addEventListener('click', () => {
            const productId = button.getAttribute('data-product-id');
            if (productId) {
                const quantityInput = document.querySelector(`.quantity-input[data-product-id="${productId}"]`) as HTMLInputElement;
                if (quantityInput) {
                    const newQuantity = parseInt(quantityInput.value);
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


updateCartCount();
sessionInNav();
renderCartItems();