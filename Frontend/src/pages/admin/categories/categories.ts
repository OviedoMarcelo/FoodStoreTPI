import { checkAuth } from "../../../utils/auth";
import { getCategories } from "../../../utils/data";
import { getSession, removeSession } from "../../../utils/storage";
import { navigateTo } from "../../../utils/navigate";
import type { ICategory } from "../../../types/ICategory";

checkAuth('admin');

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

function renderTable(): void {
    const tbody = document.getElementById('categories-table-body');
    if (!tbody) return;
    tbody.innerHTML = categories.map(cat => `
        <tr>
            <td>${cat.id}</td>
            <td><img src="${cat.image || '/images/hamburguesa-clasica.png'}" alt="${cat.name}" class="admin-thumbnail"></td>
            <td>${cat.name}</td>
            <td>${cat.description}</td>
            <td class="admin-actions">
                <button class="btn-edit" data-id="${cat.id}">Editar</button>
                <button class="btn-delete" data-id="${cat.id}">Eliminar</button>
            </td>
        </tr>
    `).join('');

    document.querySelectorAll('.btn-edit').forEach(btn => {
        btn.addEventListener('click', () => {
            const id = btn.getAttribute('data-id');
            const cat = categories.find(c => c.id === id);
            if (!cat) return;
            editingId = id;
            (document.getElementById('input-cat-name') as HTMLInputElement).value = cat.name;
            (document.getElementById('input-cat-description') as HTMLTextAreaElement).value = cat.description;
            (document.getElementById('input-cat-image') as HTMLInputElement).value = cat.image || '';
            document.getElementById('modal-category-title')!.textContent = 'Editar Categoría';
            document.getElementById('modal-category')!.style.display = 'flex';
        });
    });

    document.querySelectorAll('.btn-delete').forEach(btn => {
        btn.addEventListener('click', () => {
            deletingId = btn.getAttribute('data-id');
            document.getElementById('modal-delete')!.style.display = 'flex';
        });
    });
}

// Abrir modal nueva categoría
document.getElementById('btn-new-category')?.addEventListener('click', () => {
    editingId = null;
    (document.getElementById('input-cat-name') as HTMLInputElement).value = '';
    (document.getElementById('input-cat-description') as HTMLTextAreaElement).value = '';
    (document.getElementById('input-cat-image') as HTMLInputElement).value = '';
    document.getElementById('modal-category-title')!.textContent = 'Nueva Categoría';
    document.getElementById('modal-category')!.style.display = 'flex';
});

document.getElementById('btn-close-category-modal')?.addEventListener('click', () => {
    document.getElementById('modal-category')!.style.display = 'none';
});

// Guardar categoría (crear o editar en memoria)
document.getElementById('btn-save-category')?.addEventListener('click', () => {
    const name = (document.getElementById('input-cat-name') as HTMLInputElement).value.trim();
    const description = (document.getElementById('input-cat-description') as HTMLTextAreaElement).value.trim();
    const image = (document.getElementById('input-cat-image') as HTMLInputElement).value.trim();

    if (!name || !description) {
        alert('Nombre y descripción son obligatorios.');
        return;
    }

    if (editingId) {
        const idx = categories.findIndex(c => c.id === editingId);
        if (idx !== -1) categories[idx] = { ...categories[idx], name, description, image };
    } else {
        const newCat: ICategory = {
            id: Date.now().toString(),
            name,
            description,
            image
        };
        categories.push(newCat);
    }

    document.getElementById('modal-category')!.style.display = 'none';
    renderTable();
});

// Confirmar eliminación
document.getElementById('btn-confirm-delete')?.addEventListener('click', () => {
    if (deletingId) {
        categories = categories.filter(c => c.id !== deletingId);
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
    categories = await getCategories();
    sessionInNav();
    renderTable();
}

init();
