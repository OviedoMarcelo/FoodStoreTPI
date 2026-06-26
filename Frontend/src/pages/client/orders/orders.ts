import { checkAuth } from "../../../utils/auth";
import { getOrders } from "../../../utils/data";
import { getLocalOrders } from "../../../utils/storage";
import { countCartItems } from "../../../utils/cart";
import { navigateTo } from "../../../utils/navigate";
import { getSession, removeSession } from "../../../utils/storage";
import type { IOrder } from "../../../types/IOrder";

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

function getStatusBadge(status: IOrder['status']): string {
    const map: Record<IOrder['status'], { label: string; class: string }> = {
        pending:    { label: 'Pendiente',    class: 'badge-pending' },
        processing: { label: 'En preparación', class: 'badge-processing' },
        completed:  { label: 'Entregado',    class: 'badge-completed' },
        cancelled:  { label: 'Cancelado',    class: 'badge-cancelled' }
    };
    const s = map[status];
    return `<span class="badge ${s.class}">${s.label}</span>`;
}

function getPaymentLabel(method: string): string {
    const map: Record<string, string> = {
        cash: 'Efectivo',
        card: 'Tarjeta',
        transfer: 'Transferencia'
    };
    return map[method] || method;
}

function openOrderModal(order: IOrder): void {
    const body = document.getElementById('modal-order-body');
    if (!body) return;

    body.innerHTML = `
        <div class="order-modal-status">
            <p><strong>Estado:</strong> ${getStatusBadge(order.status)}</p>
            <p><strong>Fecha:</strong> ${new Date(order.createdAt).toLocaleDateString('es-AR', { day: '2-digit', month: '2-digit', year: 'numeric', hour: '2-digit', minute: '2-digit' })}</p>
            <p><strong>Teléfono:</strong> ${order.phone || '-'}</p>
            <p><strong>Dirección:</strong> ${order.address || '-'}</p>
            <p><strong>Forma de pago:</strong> ${getPaymentLabel(order.paymentMethod)}</p>
            ${order.notes ? `<p><strong>Notas:</strong> ${order.notes}</p>` : ''}
        </div>
        <hr>
        <h3>Productos</h3>
        <ul class="order-modal-items">
            ${order.items.map(item => `
                <li>
                    <span>${item.name} x${item.quantity}</span>
                    <span>$${(item.price * item.quantity).toLocaleString()}</span>
                </li>
            `).join('')}
        </ul>
        <hr>
        <div class="order-modal-total">
            <strong>Total:</strong> <strong>$${order.totalPrice.toLocaleString()}</strong>
        </div>
    `;

    document.getElementById('modal-order')!.style.display = 'flex';
}

function renderOrders(orders: IOrder[]): void {
    const container = document.getElementById('orders-container');
    if (!container) return;

    if (orders.length === 0) {
        container.innerHTML = `
            <div class="cart-empty">
                <p>No tenés pedidos todavía 📋</p>
                <button id="btn-ir-tienda" class="btn-submit">Ver productos</button>
            </div>`;
        document.getElementById('btn-ir-tienda')?.addEventListener('click', () => {
            navigateTo('/src/pages/store/home/home.html');
        });
        return;
    }

    container.innerHTML = orders.map(order => `
        <div class="order-card" data-order-id="${order.id}">
            <div class="order-card-header">
                <span class="order-number">Pedido #${order.id}</span>
                ${getStatusBadge(order.status)}
            </div>
            <div class="order-card-body">
                <p class="order-date">${new Date(order.createdAt).toLocaleDateString('es-AR', { day: '2-digit', month: '2-digit', year: 'numeric', hour: '2-digit', minute: '2-digit' })}</p>
                <p class="order-summary">${order.items.slice(0, 3).map(i => i.name).join(', ')}${order.items.length > 3 ? ` y ${order.items.length - 3} más` : ''}</p>
                <p class="order-total"><strong>Total: $${order.totalPrice.toLocaleString()}</strong></p>
            </div>
        </div>
    `).join('');

    container.querySelectorAll('.order-card').forEach(card => {
        card.addEventListener('click', () => {
            const orderId = card.getAttribute('data-order-id');
            const order = orders.find(o => o.id === orderId);
            if (order) openOrderModal(order);
        });
    });
}

document.getElementById('btn-close-order-modal')?.addEventListener('click', () => {
    document.getElementById('modal-order')!.style.display = 'none';
});

async function init(): Promise<void> {
    const session = getSession();
    if (!session) return;

    // Combinar pedidos del JSON y del localStorage, filtrar por usuario
    const jsonOrders = await getOrders();
    const localOrders = getLocalOrders();
    const allOrders = [...jsonOrders, ...localOrders]
        .filter(o => o.userId === session.id)
        .sort((a, b) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime());

    sessionInNav();
    updateCartCount();
    renderOrders(allOrders);
}

init();
