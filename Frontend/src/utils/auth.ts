import type { Role } from "../types/Role";
import { getSession } from "./storage";
import { navigateTo } from "./navigate";

//Se modifica la función checkAuth para que acepte un array de roles permitidos, y si el usuario no tiene ninguno de esos roles, se lo redirige a la página correspondiente según su rol.
export const checkAuth = (requiredRole: Role | Role[]): void => {
    //traigo la sesion
    const user = getSession();
    if (!user) { navigateTo('/src/pages/auth/login/login.html'); return; }
    //Si el usuario no tiene el rol requerido, lo redirijo a la página correspondiente según su rol.
    const roles = Array.isArray(requiredRole) ? requiredRole : [requiredRole];
    if (!roles.includes(user.role)) {
        navigateTo(user.role === 'admin'
            ? '/src/pages/admin/adminHome/home.html'
            : '/src/pages/store/home/home.html');
    }
}
