import type { IUser } from "../../../types/IUser";
import { navigateTo } from "../../../utils/navigate";
import { getUsers } from "../../../utils/data";
import { saveUsers, saveSession } from "../../../utils/storage";

const form = document.querySelector<HTMLFormElement>('#form-registro');

form?.addEventListener('submit', async (event: SubmitEvent) => {
    event.preventDefault();
    const formEl = event.currentTarget as HTMLFormElement;
    const data = new FormData(formEl);

    const name = data.get('name') as string;
    const email = data.get('email') as string;
    const password = data.get('password') as string;

    // Validar formato de email
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(email)) {
        alert('El email no tiene un formato válido.');
        return;
    }

    // Validar longitud de contraseña
    if (password.length < 6) {
        alert('La contraseña debe tener al menos 6 caracteres.');
        return;
    }

    // Verificar email duplicado
    const existingUsers = await getUsers();
    const emailTaken = existingUsers.find(u => u.email === email);
    if (emailTaken) {
        alert('Ya existe una cuenta con ese email.');
        return;
    }

    const nuevoUsuario: IUser = {
        id: Date.now().toString(),
        name,
        email,
        password,
        role: 'client'
    };

    // Guardar en localStorage y hacer auto-login
    const localUsers = JSON.parse(localStorage.getItem('users') || '[]');
    localUsers.push(nuevoUsuario);
    saveUsers(localUsers);
    saveSession(nuevoUsuario);
    navigateTo("/src/pages/store/home/home.html");
});