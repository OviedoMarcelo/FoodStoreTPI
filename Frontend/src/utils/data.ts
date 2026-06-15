import type { ICategory } from "../types/ICategory";
import type { IOrder } from "../types/IOrder";
import type { IProduct } from "../types/IProduct";
import type { IUser } from "../types/IUser";


//Funciones para obtener datos de categorías desde JSON local
export const getCategories = async (): Promise<ICategory[]> => {
    const response = await fetch('/data/categorias.json');
    const data = await response.json();
    return data;
}


//Funciones para obtener datos de ordenes desde JSON local
export const getOrders = async (): Promise<IOrder[]> => {
    const response = await fetch('/data/pedidos.json');
    const data = await response.json();
    return data;
}

//Funciones para obtener datos de productos desde JSON local
export const getProducts = async (): Promise<IProduct[]> => {
    const response = await fetch('/data/productos.json');
    const data = await response.json();
    return data;
}

//Funciones para obtener datos de usuarios desde JSON local
export const getUsers = async (): Promise<IUser[]> => {
    const response = await fetch('/data/usuarios.json');
    const data = await response.json();
    return data;
}