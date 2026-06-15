import type { IUser } from "../types/IUser";

//Guarda el array completo de usuarios
export const saveUsers = (users: IUser[]): void => {
    localStorage.setItem('users', JSON.stringify(users));
}


//Guarda la sesión del usuario actualmente logueado
export const saveSession = (user: IUser): void => {
    localStorage.setItem('userData', JSON.stringify(user));
}

//Obtiene el usuario actualmente logueado
export const getSession = (): IUser | null => {
    const userData = localStorage.getItem('userData');
    return userData ? JSON.parse(userData) : null;
}

//Elimina la sesión del usuario actualmente logueado
export const removeSession = (): void => {
    localStorage.removeItem('userData');
}