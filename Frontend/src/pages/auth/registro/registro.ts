// Importamos la interfaz que definimos para el usuario
import type { IUser } from "../../../types/IUser";
import { navigateTo } from "../../../utils/navigate";
import { getUsers, saveUsers } from "../../../utils/storage";

//Seleccionamos el formulario por su id
const form = document.querySelector<HTMLFormElement>('#form-registro');

//Escuchamos el evento submit
form?.addEventListener('submit', (event: SubmitEvent) => {
    //Prevenimos la recarga de la página
    event.preventDefault();
    //Capturamos los datos del formulario
    const formEl = event.currentTarget as HTMLFormElement;
    const data = new FormData(formEl);
    //Armamos el objeto usuario con la interfaz IUser
    const nuevoUsuario: IUser = {
        name: data.get('name') as string,
        email: data.get('email') as string,
        password: data.get('password') as string,
        role: 'client'
    };
    //Recuperamos el array existente o array vacío si no hay nada
    const users: IUser[] = getUsers();

    //Agregamos el nuevo usuario al array
    users.push(nuevoUsuario);

    //Guardamos el array actualizado en localStorage
    saveUsers(users);
    navigateTo("../login/login.html");
})
