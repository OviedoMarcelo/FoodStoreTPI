import { checkAuth } from "../../../utils/auth";
import { getCategories, getProducts, getOrders } from "../../../utils/data";
import { getSession, removeSession } from "../../../utils/storage";
import { navigateTo } from "../../../utils/navigate";

checkAuth('admin');

function sessionInNav(): void {
    const session = getSession();
    const navUsername = document.getElementById('nav-username');
    if (navUsername && session) navUsername.textContent = session.name;

    document.getElementById('btn-logout')?.addEventListener('click', () => {
        removeSession();
        navigateTo("/src/pages/auth/login/login.html");
    });
}

async function init(): Promise<void> {
    const [categories, products, orders] = await Promise.all([
        getCategories(),
        getProducts(),
        getOrders()
    ]);

    document.getElementById('stat-categories')!.textContent = String(categories.length);
    document.getElementById('stat-products')!.textContent = String(products.length);
    document.getElementById('stat-orders')!.textContent = String(orders.length);
    document.getElementById('stat-available')!.textContent = String(products.filter(p => p.available && !p.deleted).length);

    document.getElementById('summary-products-active')!.textContent = String(products.filter(p => p.available).length);
    document.getElementById('summary-products-inactive')!.textContent = String(products.filter(p => !p.available).length);

    document.getElementById('summary-pending')!.textContent = String(orders.filter(o => o.status === 'pending').length);
    document.getElementById('summary-processing')!.textContent = String(orders.filter(o => o.status === 'processing').length);
    document.getElementById('summary-completed')!.textContent = String(orders.filter(o => o.status === 'completed').length);
    document.getElementById('summary-cancelled')!.textContent = String(orders.filter(o => o.status === 'cancelled').length);

    sessionInNav();
}

init();
