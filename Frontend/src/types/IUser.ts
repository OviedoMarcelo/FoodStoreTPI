import type { Role } from "./Role";

export interface IUser {
    id: string;
    name: string;
    email: string;
    password: string;
    role: Role;
}