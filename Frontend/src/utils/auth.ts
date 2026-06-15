import type { Role } from "../types/Role";
import { getSession } from "./storage";
import { navigateTo } from "./navigate";

export const checkAuth = (requiredRole: Role): void => {
    // 1. Traer la sesión
    const user = getSession();

    // 2. Si no hay sesión → login
    if (!user) {
        console.log("No hay sesión, redirigiendo a login...");
        navigateTo("/src/pages/auth/login/login.html");;
        return;
    }

    // 3. Si el rol no coincide → su home
    if (user.role !== requiredRole) {
        console.log(`Rol no autorizado, redirigiendo a ${user.role} home...`);
        navigateTo(`/src/pages/${user.role}/home.html`);
        return;
    }

    // 4. Si todo ok → no hace nada, deja pasar
}
