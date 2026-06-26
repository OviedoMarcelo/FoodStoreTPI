import type { IProductInCart } from "../types/IProductInCart";


/*Obtener el carrito desde el local storage*/

export const getCart = (): IProductInCart[] => {
    const cart = localStorage.getItem('cart');
    return cart ? JSON.parse(cart) : [];
}

/*Guardar el carrito en el local storage*/

export const saveCart = (cart: IProductInCart[]): void => {
    localStorage.setItem('cart', JSON.stringify(cart));
}

/*Eliminar el carrito del local storage*/

export const clearCart = (): void => {
    localStorage.removeItem('cart');
}

/*Agregar un producto al carrito*/
export const addToCart = (product: IProductInCart): void => {
    const cart = getCart();
    const existingProductIndex = cart.findIndex(item => item.product.id === product.product.id);
    if (existingProductIndex !== -1) {
        cart[existingProductIndex].quantity += product.quantity;
    } else {
        cart.push(product);
    }
    saveCart(cart);
}

/*Eliminar un producto del carrito*/
export const removeFromCart = (productId: string): void => {
    const cart = getCart();
    const updatedCart = cart.filter(item => item.product.id !== productId);
    saveCart(updatedCart);
}

/*Actualizar la cantidad de un producto en el carrito*/
export const updateCartQuantity = (productId: string, quantity: number): void => {
    const cart = getCart();
    const productIndex = cart.findIndex(item => item.product.id === productId);
    if (productIndex !== -1) {
        cart[productIndex].quantity = quantity;
        saveCart(cart);
    }
}

/*Calcular el total del carrito*/
export const calculateCartTotal = (): number => {
    const cart = getCart();
    return cart.reduce((total, item) => total + item.product.price * item.quantity, 0);
}

/*Contar la cantidad total de productos en el carrito*/
export const countCartItems = (): number => {
    const cart = getCart();
    return cart.reduce((total, item) => total + item.quantity, 0);
}

