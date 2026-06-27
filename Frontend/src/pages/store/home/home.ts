import { getCategories, getProducts } from "../../../utils/data";
import type { IProduct } from "../../../types/IProduct";
import type { ICategory } from "../../../types/ICategory";
import { checkAuth } from "../../../utils/auth";
import { countCartItems } from "../../../utils/cart";
import { navigateTo } from "../../../utils/navigate";
import { removeSession, getSession } from "../../../utils/storage";


let currentCategory: string = '';
let searchText: string = '';
let allProducts: IProduct[] = [];
let allCategories: ICategory[] = [];
checkAuth(['client', 'admin']);


/*******************************************************
***************** Render de valores en NAVBAR***********
**********************************************************/

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

//Modifico el cart count del nav si se agrega un elemento al carrito
function updateCartCount(): void {
    const cartCount = document.getElementById('cart-count');
    const totalItems = countCartItems();
    if (cartCount) {
        cartCount.textContent = String(totalItems);
    }
}


/*******************************************************
***************** Render y filtrado de productos*********
**********************************************************/

// Función para Renderizar productos

function renderProducts(filteredProducts?: IProduct[]) {

    //Obtengo los productos que pueden o no estar filtrados por categoría.
    const products = filteredProducts || allProducts;
    const productList = document.getElementById('contenedor-productos');

    if (productList && products.length > 0) {
        document.getElementById('no-resultados')!.style.display = 'none';
        productList.innerHTML = products.map(product =>
            `<div class="product-card" data-id="${product.id}">
    <img src="${product.imageUrl}" alt="${product.name}">
    <div class="product-card-body">
    <h3>${product.name}</h3>
    <p>${product.description}</p>
    <div class="product-card-footer">
    <p class="product-price">Precio: $${product.price.toLocaleString()}</p>
        <span class="badge ${product.available ? 'badge-available' : 'badge-unavailable'}">
        ${product.available ? 'Disponible' : 'No disponible'}
        </span>
    </div>
    </div>
</div>`).join('');
    }
    else {
        const errorMessage = document.getElementById('no-resultados');
        // Mostrar mensaje de error si no se encuentran productos
        if (errorMessage) {
            errorMessage.style.display = 'block';
        }
        // Limpiar la lista de productos si no se encuentran resultados
        if (productList) productList.innerHTML = '';
    }

    //Agrego evento a los botones de agregar al carrito
    /*     productList?.querySelectorAll('.btn-agregar').forEach(button => {
            button.addEventListener('click', () => {
                const productId = button.getAttribute('data-id');
                if (productId) {
                    const product = products.find(p => p.id === productId);
                    if (product) {
                        addToCart({ product, quantity: 1 });
                        showToast('Producto agregado al carrito', 2000);
                    }
                }
                updateCartCount();
            });
        });
     */
    //Agrego evento para llevar al detalle del producto al hacer click en la tarjeta
    productList?.querySelectorAll('.product-card').forEach(card => {
        card.addEventListener('click', () => {
            const productId = card.getAttribute('data-id');
            navigateTo(`/src/pages/store/productDetail/productDetail.html?id=${productId}`);
        });
    });

}

//Función para filtrar productos por categoría y por texto de búsqueda, y luego renderizarlos

function filterAndRender(): void {
    const products = allProducts;
    let filteredProducts = products;
    if (currentCategory) {
        filteredProducts = filteredProducts.filter(p => p.categoryId === currentCategory);
    }
    if (searchText) {
        const lowerSearchText = searchText.toLowerCase();
        filteredProducts = filteredProducts.filter(p =>
            p.name.toLowerCase().includes(lowerSearchText) ||
            p.description.toLowerCase().includes(lowerSearchText)
        );
    }
    renderProducts(filteredProducts);
}

/*********************************************************************
***************** Render y filtrado de barra de categorías y buscador*
***********************************************************************/


function renderCategoryBar(): void {
    const categoryBar = document.getElementById('categories-list');
    const categories = allCategories;
    if (categoryBar) {
        categoryBar.innerHTML =
            `<li><a href="#" class="category-element active" data-category="">Todas</a></li>` +
            categories.map(category =>
                `<li><a href="#" class="category-element" data-category="${category.id}">${category.name}</a></li>`
            ).join('');
    }
    //Agrego evento a los links de categorías para filtrar productos al hacer click
    categoryBar?.querySelectorAll('.category-element').forEach(link => {
        link.addEventListener('click', (event) => {
            event.preventDefault();
            const categoryId = link.getAttribute('data-category');
            //Limpio los active de los links y pongo active al seleccionado
            categoryBar.querySelectorAll('.category-element').forEach(a => a.classList.remove('active'));
            link.classList.add('active');
            if (categoryId || categoryId === '') {
                currentCategory = categoryId;
                filterAndRender();
            }
        });
    });

    //Agrego evento al buscador para filtrar productos al escribir
    const searchInput = document.getElementById('search-input') as HTMLInputElement;
    searchInput.addEventListener('input', () => {
        searchText = searchInput.value;
        filterAndRender();
    });
}

async function init() {
    allProducts = (await getProducts()).filter(p => p.available && !p.deleted);
    allCategories = await getCategories();

    sessionInNav();
    updateCartCount();
    renderCategoryBar();
    renderProducts(allProducts);
}


//Llamo a la función para renderizar los productos al cargar la página y actualizar el contador del carrito.
init();
