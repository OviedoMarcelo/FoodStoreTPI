import { checkAuth } from "../../../utils/auth";
import { getOrders, getUsers } from "../../../utils/data";
import { getSession, removeSession } from "../../../utils/storage";
import { navigateTo } from "../../../utils/navigate";
import type { IOrder } from "../../../types/IOrder";
import type { IUser } from "../../../types/IUser";

checkAuth('admin');

let allOrders: IOrder[] = [];
let users: IUser[] = [];
let currentFilter: string = '';

function sessionInNav(): void {
    const session = getSession();
    const navUsername = document.getElementById('nav-username');
    if (navUsername && session) navUsername.textContent = session.name;
    document.getElementById('btn-logout')?.addEventListener('click', () => {
        removeSession();
        navigateTo("/src/pages/auth/login/login.html");
    });
}

function getStatusBadge(status: IOrder['status']): string {
    const map: Record<IOrder['status'], { label: string; class: string }> = {
        pending:    { label: 'Pendiente',       class: 'badge-pending' },
        processing: { label: 'En preparación',  class: 'badge-processing' },
        completed:  { label: 'Entregado',       class: 'badge-completed' },
        cancelled:  { label: 'Cancelado',       class: 'badge-cancelled' }
    };
    const s = map[status];
    return `<span class="badge ${s.class}">${s.label}</span>`;
}

function getClientName(userId: string): string {
    return users.find(u => u.id === userId)?.name || `Usuario #${userId}`;
}

function getPaymentLabel(method: string): string {
    const map: Record<string, string> = { cash: 'Efectivo', card: 'Tarjeta', transfer: 'Transferencia' };
    return map[method] || method;
}

function openOrderModal(order: IOrder): void {
    const body = document.getElementById('modal-order-body');
    if (!body) return;

    body.innerHTML = `
        <div class="order-modal-status">
            <p><strong>Pedido #${order.id}</strong></p>
            <p><strong>Cliente:</strong> ${getClientName(order.userId)}</p>
            <p><strong>Estado:</strong> ${getStatusBadge(order.status)}</p>
            <p><strong>Fecha:</strong> ${new Date(order.createdAt).toLocaleDateString('es-AR', { day: '2-digit', month: '2-digit', year: 'numeric', hour: '2-digit', minute: '2-digit' })}</p>
            ${order.phone ? `<p><strong>Teléfono:</strong> ${order.phone}</p>` : ''}
            ${order.address ? `<p><strong>Dirección:</strong> ${order.address}</p>` : ''}
            ${order.paymentMethod ? `<p><strong>Forma de pago:</strong> ${getPaymentLabel(order.paymentMethod)}</p>` : ''}
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
        <div style="margin-top:1rem;">
            <label><strong>Cambiar estado:</strong></label>
            <select id="select-order-status" style="margin-top:0.4rem; width:100%; padding:0.5rem; border:1.5px solid var(--color-borde); border-radius:6px;">
                <option value="pending"    ${order.status === 'pending'    ? 'selected' : ''}>Pendiente</option>
                <option value="processing" ${order.status === 'processing' ? 'selected' : ''}>En preparación</option>
                <option value="completed"  ${order.status === 'completed'  ? 'selected' : ''}>Entregado</option>
                <option value="cancelled"  ${order.status === 'cancelled'  ? 'selected' : ''}>Cancelado</option>
            </select>
            <button id="btn-save-status" class="btn-submit" style="margin-top:0.75rem; width:100%;">Guardar cambio</button>
        </div>
    `;

    document.getElementById('modal-order')!.style.display = 'flex';

    document.getElementById('btn-save-status')?.addEventListener('click', () => {
        const newStatus = (document.getElementById('select-order-status') as HTMLSelectElement).value as IOrder['status'];
        const idx = allOrders.findIndex(o => o.id === order.id);
        if (idx !== -1) allOrders[idx].status = newStatus;
        document.getElementById('modal-order')!.style.display = 'none';
        renderOrders();
    });
}

function renderOrders(): void {
    const container = document.getElementById('orders-container');
    if (!container) return;

    const filtered = currentFilter
        ? allOrders.filter(o => o.status === currentFilter)
        : allOrders;

    if (filtered.length === 0) {
        container.innerHTML = `<div class="cart-empty"><p>No hay pedidos para mostrar.</p></div>`;
        return;
    }

    container.innerHTML = filtered.map(order => `
        <div class="order-card" data-order-id="${order.id}">
            <div class="order-card-header">
                <span class="order-number">Pedido #${order.id}</span>
                ${getStatusBadge(order.status)}
            </div>
            <div class="order-card-body">
                <p class="order-date">${new Date(order.createdAt).toLocaleDateString('es-AR', { day: '2-digit', month: '2-digit', year: 'numeric', hour: '2-digit', minute: '2-digit' })}</p>
                <p><strong>Cliente:</strong> ${getClientName(order.userId)}</p>
                <p class="order-summary">${order.items.length} producto${order.items.length !== 1 ? 's' : ''}</p>
                <p class="order-total"><strong>Total: $${order.totalPrice.toLocaleString()}</strong></p>
            </div>
        </div>
    `).join('');

    container.querySelectorAll('.order-card').forEach(card => {
        card.addEventListener('click', () => {
            const orderId = card.getAttribute('data-order-id');
            const order = allOrders.find(o => o.id === orderId);
            if (order) openOrderModal(order);
        });
    });
}

document.getElementById('btn-close-order-modal')?.addEventListener('click', () => {
    document.getElementById('modal-order')!.style.display = 'none';
});

document.getElementById('filter-status')?.addEventListener('change', (e) => {
    currentFilter = (e.target as HTMLSelectElement).value;
    renderOrders();
});

async function init(): Promise<void> {
    [allOrders, users] = await Promise.all([getOrders(), getUsers()]);
    allOrders.sort((a, b) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime());
    sessionInNav();
    renderOrders();
}

init();
