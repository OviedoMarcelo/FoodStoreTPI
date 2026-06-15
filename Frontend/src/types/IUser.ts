import type { Role } from "./Role";

export interface IUser {
    name: string;
    email: string;
    password: string;
    role: Role;
}