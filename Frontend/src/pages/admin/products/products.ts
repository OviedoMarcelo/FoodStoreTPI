import { checkAuth } from "../../../utils/auth";
import { getProducts, getCategories } from "../../../utils/data";
import { getSession, removeSession } from "../../../utils/storage";
import { navigateTo } from "../../../utils/navigate";
import type { IProduct } from "../../../types/IProduct";
import type { ICategory } from "../../../types/ICategory";

checkAuth('admin');

let products: IProduct[] = [];
let categories: ICategory[] = [];
let editingId: string | null = null;
let deletingId: string | null = null;

function sessionInNav(): void {
    const session = getSession();
    const navUsername = document.getElementById('nav-username');
    if (navUsername && session) navUsername.textContent = session.name;
    document.getElementById('btn-logout')?.addEventListener('click', () => {
        removeSession();
        navigateTo("/src/pages/auth/login/login.html");
    });
}

function getCategoryName(categoryId: string): string {
    return categories.find(c => c.id === categoryId)?.name || '-';
}

function renderTable(): void {
    const tbody = document.getElementById('products-table-body');
    if (!tbody) return;
    tbody.innerHTML = products.map(p => `
        <tr>
            <td>${p.id}</td>
            <td><img src="${p.imageUrl}" alt="${p.name}" class="admin-thumbnail"></td>
            <td>${p.name}</td>
            <td class="td-description">${p.description}</td>
            <td>$${p.price.toLocaleString()}</td>
            <td>${getCategoryName(p.categoryId)}</td>
            <td>${p.stock}</td>
            <td><span class="badge ${p.available ? 'badge-available' : 'badge-unavailable'}">${p.available ? 'Disponible' : 'No disponible'}</span></td>
            <td class="admin-actions">
                <button class="btn-edit" data-id="${p.id}">Editar</button>
                <button class="btn-delete" data-id="${p.id}">Eliminar</button>
            </td>
        </tr>
    `).join('');

    document.querySelectorAll('.btn-edit').forEach(btn => {
        btn.addEventListener('click', () => {
            const id = btn.getAttribute('data-id');
            const product = products.find(p => p.id === id);
            if (!product) return;
            editingId = id;
            (document.getElementById('input-prod-name') as HTMLInputElement).value = product.name;
            (document.getElementById('input-prod-description') as HTMLTextAreaElement).value = product.description;
            (document.getElementById('input-prod-price') as HTMLInputElement).value = String(product.price);
            (document.getElementById('input-prod-stock') as HTMLInputElement).value = String(product.stock);
            (document.getElementById('input-prod-category') as HTMLSelectElement).value = product.categoryId;
            (document.getElementById('input-prod-image') as HTMLInputElement).value = product.imageUrl;
            (document.getElementById('input-prod-available') as HTMLInputElement).checked = product.available;
            document.getElementById('modal-product-title')!.textContent = 'Editar Producto';
            document.getElementById('modal-product')!.style.display = 'flex';
        });
    });

    document.querySelectorAll('.btn-delete').forEach(btn => {
        btn.addEventListener('click', () => {
            deletingId = btn.getAttribute('data-id');
            document.getElementById('modal-delete')!.style.display = 'flex';
        });
    });
}

function fillCategorySelect(): void {
    const select = document.getElementById('input-prod-category') as HTMLSelectElement;
    select.innerHTML = categories.map(c => `<option value="${c.id}">${c.name}</option>`).join('');
}

document.getElementById('btn-new-product')?.addEventListener('click', () => {
    editingId = null;
    (document.getElementById('input-prod-name') as HTMLInputElement).value = '';
    (document.getElementById('input-prod-description') as HTMLTextAreaElement).value = '';
    (document.getElementById('input-prod-price') as HTMLInputElement).value = '';
    (document.getElementById('input-prod-stock') as HTMLInputElement).value = '';
    (document.getElementById('input-prod-image') as HTMLInputElement).value = '';
    (document.getElementById('input-prod-available') as HTMLInputElement).checked = true;
    document.getElementById('modal-product-title')!.textContent = 'Nuevo Producto';
    document.getElementById('modal-product')!.style.display = 'flex';
});

document.getElementById('btn-close-product-modal')?.addEventListener('click', () => {
    document.getElementById('modal-product')!.style.display = 'none';
});

document.getElementById('btn-save-product')?.addEventListener('click', () => {
    const name = (document.getElementById('input-prod-name') as HTMLInputElement).value.trim();
    const description = (document.getElementById('input-prod-description') as HTMLTextAreaElement).value.trim();
    const price = parseFloat((document.getElementById('input-prod-price') as HTMLInputElement).value);
    const stock = parseInt((document.getElementById('input-prod-stock') as HTMLInputElement).value);
    const categoryId = (document.getElementById('input-prod-category') as HTMLSelectElement).value;
    const imageUrl = (document.getElementById('input-prod-image') as HTMLInputElement).value.trim();
    const available = (document.getElementById('input-prod-available') as HTMLInputElement).checked;

    if (!name || !description || isNaN(price) || price <= 0 || isNaN(stock) || stock < 0 || !categoryId) {
        alert('Completá todos los campos correctamente.');
        return;
    }

    if (editingId) {
        const idx = products.findIndex(p => p.id === editingId);
        if (idx !== -1) products[idx] = { ...products[idx], name, description, price, stock, categoryId, imageUrl, available };
    } else {
        products.push({
            id: Date.now().toString(),
            name, description, price, stock, categoryId, imageUrl, available, deleted: false
        });
    }

    document.getElementById('modal-product')!.style.display = 'none';
    renderTable();
});

document.getElementById('btn-confirm-delete')?.addEventListener('click', () => {
    if (deletingId) {
        products = products.filter(p => p.id !== deletingId);
        deletingId = null;
        document.getElementById('modal-delete')!.style.display = 'none';
        renderTable();
    }
});

document.getElementById('btn-close-delete-modal')?.addEventListener('click', () => {
    document.getElementById('modal-delete')!.style.display = 'none';
});

document.getElementById('btn-cancel-delete')?.addEventListener('click', () => {
    document.getElementById('modal-delete')!.style.display = 'none';
});

async function init(): Promise<void> {
    [products, categories] = await Promise.all([getProducts(), getCategories()]);
    sessionInNav();
    fillCategorySelect();
    renderTable();
}

init();
