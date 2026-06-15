import type { IUser } from "../../../types/IUser";
import { getUsers } from "../../../utils/data";
import { navigateTo } from "../../../utils/navigate";
import { saveSession } from "../../../utils/storage";

//Seleccionamos el formulario de inicio de sesión utilizando su ID
const form = document.querySelector<HTMLFormElement>('#form-login');

//Agregamos un event listener para el evento submit del formulario
form?.addEventListener('submit', async (event: SubmitEvent) => {

    //Prevenimos la recarga de la página al enviar el formulario
    event.preventDefault();

    //Obtenemos los datos del formulario
    const formLogin = event.currentTarget as HTMLFormElement;
    const data = new FormData(formLogin);

    //Extraemos el email y la contraseña ingresados por el usuario
    const email = data.get('email') as string;
    const password = data.get('password') as string;

    //Traemos el array de usuarios desde json data
    const users: IUser[] = await getUsers();

    //Buscamos un usuario que coincida con el email y la contraseña ingresados
    const user = users.find(u => u.email === email && u.password === password);

    //Si encontramos un usuario válido, lo guardo en el local y redirigimos a la página de inicio
    if (user) {
        saveSession(user);
        if (user.role === 'admin') {
            navigateTo("../../admin/home.html");
        } else {
            navigateTo("../../client/home.html");
        }
    } else {
        //Si no encontramos un usuario válido, mostramos un mensaje de error
        alert('Credenciales inválidas. Por favor, inténtalo de nuevo.');
    }

});