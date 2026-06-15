export interface IOrder {
    id: string;
    userId: string;
    status: "pending" | "processing" | "completed" | "cancelled";
    items: {
        productId: string;
        name: string;
        price: number;
        quantity: number
    }[];
    totalPrice: number;
    createdAt: Date;
}