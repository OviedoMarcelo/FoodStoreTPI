export interface IProduct {
    id: string;
    name: string;
    description: string;
    price: number;
    imageUrl: string;
    categoryId: string;
    stock: number;
    available: boolean;
    deleted: boolean;
}