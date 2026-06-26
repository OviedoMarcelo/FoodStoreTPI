import type { IProduct } from "./IProduct";

export interface IProductInCart {
    product: IProduct;
    quantity: number;
}
