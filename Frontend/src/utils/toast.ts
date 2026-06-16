//Función para mostrar mensaje de notificación ante cada acción del usuario.

export function showToast(message: string, duration: number = 3000) {
    const toast = document.createElement('div');
    toast.className = 'toast';
    toast.textContent = message;
    document.body.appendChild(toast);

    setTimeout(() => {
        document.body.removeChild(toast);
    }, duration);
}