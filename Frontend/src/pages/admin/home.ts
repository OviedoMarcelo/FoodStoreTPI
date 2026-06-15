import type { IUser } from "../../types/IUser";
import { checkAuth } from "../../utils/auth";
import { navigateTo } from "../../utils/navigate";
import { removeSession, getSession } from "../../utils/storage";

checkAuth('admin');
const user: IUser = getSession() as IUser;

const welcomeMessage = document.getElementById('welcome-message');
if (welcomeMessage) {
    welcomeMessage.textContent = `Bienvenido, ${user.name}!`;
}

const logoutButton = document.getElementById('logout-button');
logoutButton?.addEventListener('click', () => {
    removeSession();
    navigateTo("../auth/login/login.html");
});